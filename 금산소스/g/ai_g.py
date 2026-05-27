# 파일명 : ai_g.py
# Description : 소독 주입률 예측 및 제어 운영코드
# Execute Program : ai_g_main.py 모듈 사용
# 수정일 : 2024-10-08
# 수정 내용 : 
#   1. 주석 추가
#   2. init 테이블에 추가 된 값을 사용하도록 코드 변경(g_d_residual_cl_holding, g_h_in_residual_cl_holding, g_post_chol_rate_holding_time)
import os
import sys
import json
import joblib
import time
import keras
import keras.backend as K
import tensorflow as tf
import pandas as pd
from pandas.api.types import is_numeric_dtype, is_integer_dtype, is_float_dtype, is_bool_dtype
from pandas.api.types import is_object_dtype, is_string_dtype, is_categorical_dtype, is_datetime64_dtype
import numpy as np
import scipy.stats as ss
import datetime
from datetime import timedelta

from multiprocessing import Process,Manager
import warnings

from scipy.signal import savgol_filter

import traceback
import logging
from logging import handlers

if '__file__' in globals() and os.path.isfile(os.path.abspath(__file__)):
    PROC_PATH = os.path.dirname(os.path.abspath(__file__))
else:
    PROC_PATH = os.path.dirname(os.path.abspath('_'))
    
BASE_PATH = os.path.dirname(PROC_PATH)
ROOT_PATH = os.path.dirname(BASE_PATH)
MODEL_PATH = PROC_PATH + '/model/' # 모델 경로
PROC_NAME = os.path.basename(PROC_PATH)
LOGS_PATH = BASE_PATH + '/logs/'
sys.path.append(BASE_PATH + '/common')
sys.path.append(ROOT_PATH + '/common')

from db_util import DBUtil
from config import Config
from analysis import *


MIN_MAX_SCALE_FILENAME_PRE = 'min_max_scale_pre.csv'
MIN_MAX_SCALE_FILENAME_POST = 'min_max_scale_post.csv'
MODEL_FILE_PRE = 'model_pre.h5'
MODEL_FILE_POST = 'model_post.h5'
DB_NAME = 'G'
G_PRE_CTR_TAG = '771-358-CIC-5113'
G_POST_CTR_TAG = '771-358-CIC-5213'
CTR_TB_PRE = 'TB_AI_PRE_G_CTR'
CTR_TB_POST = 'TB_AI_POST_G_CTR'
ALM_TB_PRE = 'WATER_GS.TB_AI_PRE_G_ALM'
ALM_TB_POST = 'WATER_GS.TB_AI_POST_G_ALM'
ALM_ERR_PRE = '137012'
ALM_ERR_POST = '137032'
ALM_CTR_PRE = '137014'
ALM_CTR_POST = '137034'
ALM_THRESHOLD_PRE = '137015'
ALM_THRESHOLD_POST = '137036'
ALM_H_OUT_RESIDUAL_CL_EXCEEDED = '137035'



# 전송 로거 생성
from aos_util import *
build_logger(ROOT_PATH, BASE_PATH, PROC_NAME)

# log settings
runLogFormatter = logging.Formatter('%(asctime)s : %(message)s')

logfile = LOGS_PATH + PROC_NAME + '.log'
runLogHandler = handlers.TimedRotatingFileHandler(filename=logfile, when='midnight', interval=1,
                                                  encoding='utf-8')
runLogHandler.setFormatter(runLogFormatter)
runLogHandler.suffix = "%Y%m%d"

# logger set
runLogger = logging.getLogger()
runLogger.setLevel(logging.ERROR)
runLogger.addHandler(runLogHandler)

# 모델 load 
model_pre = keras.models.load_model(MODEL_PATH + MODEL_FILE_PRE)
model_post = keras.models.load_model(MODEL_PATH + MODEL_FILE_POST)

def get_init(df_init):
    """
    해당 공정 Init 테이블 값을 불러오는 함수

    Args:
        df_init : init 테이블 값이 담긴 DataFrame

    Returns:
        Dict : init 테이블 값이 담긴 딕셔너리
    """
    dict_init = dict()

    for i in df_init.index:
        itm = df_init.loc[i]['ITM']
        dict_init[itm] = df_init.loc[i]['INIT_VAL']

    return dict_init

def get_pre_init(db):
    """
    전차염(pre)의 Init에서 관리하는 값들을 저장하는 dict를 반환

    Args:
        db : 연결된 db connection

    Returns:
        DataFrame : pre_init 테이블 값이 담긴 딕셔너리
    """
    sql = f'SELECT * FROM TB_AI_PRE_{DB_NAME}_INIT'

    df_init = db.read(sql)
    
    dict_init = get_init(df_init)
    
    return dict_init

def get_post_init(db):
    """
    후차염(post)의 Init에서 관리하는 값들을 저장하는 dict를 반환

    Args:
        db : 연결된 db connection

    Returns:
        DataFrame : post_init 테이블 값이 담긴 딕셔너리
    """
    sql = f'SELECT * FROM TB_AI_POST_{DB_NAME}_INIT'

    df_init = db.read(sql)
    
    dict_init = get_init(df_init)
    
    return dict_init

def get_ai_pre_rt(db):
    """
    전차염 AI 결과 테이블 최근 데이터 5개가 담긴 dataframe 반환

    Args:
        db : 연결된 db connection

    Returns:
        DataFrame : 결과테이블 중 최근 5개 값이 담긴 데이터프레임
    """
    sql = f'SELECT * FROM TB_AI_PRE_{DB_NAME}_RT \
    ORDER BY UPD_TI DESC \
    LIMIT 5'

    df_ai_rt = db.read(sql)

    return df_ai_rt

def get_ai_post_rt(db):
    """
    후차염 AI 결과 테이블 최근 데이터 5개가 담긴 dataframe 반환

    Args:
        db : 연결된 db connection

    Returns:
        DataFrame : 결과테이블 중 최근 5개 값이 담긴 데이터프레임
    """
    sql = f'SELECT * FROM TB_AI_POST_{DB_NAME}_RT \
    ORDER BY UPD_TI DESC \
    LIMIT 5'

    df_ai_rt = db.read(sql)

    return df_ai_rt

def save_ctr_tag(operation_mode, db, count, correct_degree, threshold, tb_ctr_name, tb_alm_name, UPD_TI, TAG_SN, TAG_VAL, TAG_CMP_VAL, KFK_FLG=0, CTR_FLG=0, ALM_CTR=0, ALM_THRES=0, min_chol_rate=0, max_chol_rate=2, df_factor=pd.DataFrame(), RNTI=datetime.now().strftime('%Y-%m-%d %H:%M') ):
    """
    운영 모드에 따라 맞는 방식으로 주입률 제어를 진행하는 함수.
    CTR 및 ALM 테이블에 쿼리를 insert 하는 식으로 제어 진행

    Args:
        operation_mode : 운영모드, 0: 분석모드, 1: 추천모드, 2: AI모드
        db : 연결된 db connection
        count : 보정주기 도달 여부. 도달 시 0
        correct_degree : 현재 주입률 대비 보정 정도
        threshold : 1회 변경 임계치 값. 
        tb_ctr_name : CTR 테이블의 이름
        tb_alm_name : ALM 테이블의 이름
        UPD_TI : CTR 및 ALM 테이블의 UPD_TI에 들어갈 시각.
        TAG_SN : 제어 태그
        TAG_VAL : 예측값
        TAG_CMP_VAL : 이전값
        KFK_FLG : CTR에 insert 시 사용되는 플래그 값
        CTR_FLG : CTR에 insert 시 사용되는 플래그 값
        ALM_CTR : 띄우고자 하는 제어 알람에 대한 number값
        ALM_THRES : 띄우고자 하는 임계치 알람에 대한 number값
        min_chol_rate : 임계치 알람 기준 최소값
        max_chol_rate : 임계치 알람 기준 최대값
        df_factor : ai factor값이 저장된 DataFrame
        RNTI : 함수 실행 시각
    
    Returns:
        None
    """
    UPD_TI = f'\"{RNTI}\"'
    RNTI = f'\"{RNTI}\"'
    TAG_SN = f'\"{TAG_SN}\"'
    
    if operation_mode == 0: 
        return

    if operation_mode == 1:
        if (count == 0) and (abs(correct_degree) >= threshold) and (round(TAG_VAL, 2) != round(TAG_CMP_VAL, 2)):
            # 변경하고자 하는 주입률 값이 최소, 최대 범위를 넘어설 경우 임계치 알람
            if ((min_chol_rate > TAG_VAL) or (max_chol_rate < TAG_VAL)):
                db.save_alm(
                    tb_alm_name,
                    ALM_THRES,
                    RNTI
                )
            # operation_mode가 1 or 2이면 ALM 먼저 삽입 이후 CTR 삽입
            db.save_ctr(
                tb_ctr_name,
                UPD_TI,
                RNTI,
                TAG_SN,
                TAG_VAL,
                TAG_CMP_VAL,
                KFK_FLG,
                CTR_FLG
            )
            db.save_ai_factor(df_factor)

    if operation_mode == 2:
        if (count == 0) and (abs(correct_degree) >= threshold) and (round(TAG_VAL, 2) != round(TAG_CMP_VAL, 2)):
            # 변경하고자 하는 주입률 값이 최소, 최대 범위를 도달하거나 넘어설 경우 임계치 알람
            if ((min_chol_rate > TAG_VAL) or (max_chol_rate < TAG_VAL)):
                db.save_alm(
                    tb_alm_name,
                    ALM_THRES,
                    RNTI
                )
            else:
                # CTR 저장했음을 ALM 테이블에 저장 
                db.save_alm(
                    tb_alm_name,
                    ALM_CTR,
                    RNTI
                )
            # operation_mode가 1 or 2이면 ALM 먼저 삽입 이후 CTR 삽입
            db.save_ctr(
                tb_ctr_name,
                UPD_TI,
                RNTI,
                TAG_SN,
                TAG_VAL,
                TAG_CMP_VAL,
                KFK_FLG,
                CTR_FLG
            )
            # 주요인자 db에 저장
            db.save_ai_factor(df_factor)



def perform_pre(db, calib_time_pre, count_pre, pre_chol, job_datetime):
    """
    전차염의 실시간 데이터 load 부터 전처리, 증발량 및 주입률 예측 진행하는 함수

    Args:
        db : 연결된 db connection
        calib_time_pre : 설정된 보정주기
        count_pre : 이전 보정주기 도달 이후 운영코드 실행 횟수
        pre_chol : 현재 주입률
        job_datetime : 특정 시점부터 데이터를 불러오기 위한 설정값. None일 경우 가장 최근 데이터로부터 불러옴

    Returns:
        calib_time_pre : 설정된 보정주기
        count_pre : 이전 보정주기 도달 이후 운영코드 실행 횟수
        pre_chol : 현재 주입률        
    """
    now_str = '2023-12-15 00:00:00'
    now = datetime.strptime(now_str, '%Y-%m-%d %H:%M:%S')
    residual_time = 35
    
    tags = (
    #  금산 필요 태그들(전차염)
        '771-358-TEI-6028',
        '771-358-TBI-6001',
        '771-358-PHI-6003',
        '771-358-CUI-6004',
        '771-358-ALI-6001',
        '771-358-MNI-3001',
        '771-358-CIB-5112',
        '771-358-CIB-5122',
        '771-358-CLI-6001',
        '771-358-CIC-5113',
        '771-358-FRI-2011',
        '771-358-CLI-6011',
    )

    if job_datetime is None:
        g_rt_df = db.read_rt_subday_max('TB_G_RT', tags)
        now_str = str(g_rt_df.iloc[-1].name)
        now = datetime.strptime(now_str, '%Y-%m-%d %H:%M:%S')
        rnti = datetime.now()
    else:
        g_rt_df = db.read_rt_subday_etime('TB_G_RT', tags, job_datetime)
        now_str = str(g_rt_df.iloc[-1].name)
        now = datetime.strptime(now_str, '%Y-%m-%d %H:%M:%S')
        rnti = datetime.now()
    if len(g_rt_df) <= 0:
        return
        
    for tag in tags:
        if tag not in g_rt_df.columns:
            g_rt_df[tag] = 0

    # index(UPD_TI)를 datetime으로 타입 변환
    g_rt_df.index = pd.to_datetime(g_rt_df.index)
    
    g_rt_df = g_rt_df.resample('1T').fillna(method='ffill').fillna(method='bfill')
    g_rt_df = g_rt_df.fillna(method='ffill')
    
    # column 명을 구분하기 쉽도록 변환
    column_name = {
        # 금산 태그 목록에 맞게 명칭 변경(전차염)
        '771-358-TEI-6028':'원수 수온',
        '771-358-TBI-6001':'원수 탁도',
        '771-358-PHI-6003':'원수 pH',
        '771-358-CUI-6004':'원수 전기전도도',
        '771-358-ALI-6001':'원수 알카리도',
        '771-358-MNI-3001':'취수 망간',
        '771-358-CIB-5112':'전차염 펌프1 RUN',
        '771-358-CIB-5122':'전차염 펌프2 RUN',
        '771-358-CLI-6001':'혼화지 잔류염소',
        '771-358-CIC-5113':'전차염 목표주입률',
        '771-358-FRI-2011':'원수 유입유량',
        '771-358-CLI-6011':'원수 잔류염소',
        }
    g_rt_df = g_rt_df.rename(columns=column_name)

    for col in g_rt_df.columns:
        if np.isnan(g_rt_df.loc[now, col]):
            g_rt_df.loc[now, col] = 0
    ana_df_droped = g_rt_df.copy()
    
    # Init 테이블 값 Load
    dict_pre_init = get_pre_init(db)
    
    # RT 테이블 값 Load
    df_ai_pre_rt = get_ai_pre_rt(db)
    #
    # 전처리 시작
    #
    
    # 데이터 상하한 범위 이탈 구간 제거
    ana_df_droped.loc[(ana_df_droped[ana_df_droped['혼화지 잔류염소'] < 0.05]).index, '혼화지 잔류염소'] = np.nan
    ana_df_droped.loc[(ana_df_droped[ana_df_droped['혼화지 잔류염소'] > 0.7]).index, '혼화지 잔류염소'] = np.nan

    ana_df_droped.loc[(ana_df_droped[ana_df_droped['원수 탁도'] <= 0.01]).index, '원수 탁도'] = np.nan
    
    ana_df_droped.loc[(ana_df_droped[ana_df_droped['원수 pH'] <= 0.01]).index, '원수 pH'] = np.nan

    ana_df_droped.loc[(ana_df_droped[ana_df_droped['원수 전기전도도'] <= 0.01]).index, '원수 전기전도도'] = np.nan

    ana_df_droped.loc[(ana_df_droped[ana_df_droped['원수 알카리도'] <= 0.01]).index, '원수 알카리도'] = np.nan

    ana_df_droped.loc[(ana_df_droped[ana_df_droped['취수 망간'] <= 0.001]).index, '취수 망간'] = np.nan

    
    ana_df_droped['G_PRE_CHOL_RATE'] = ana_df_droped['전차염 목표주입률']
    ana_df_droped['G_TBI'] = ana_df_droped['원수 탁도']
    ana_df_droped['G_PHI'] = ana_df_droped['원수 pH']
    ana_df_droped['G_CUI'] = ana_df_droped['원수 전기전도도']
    ana_df_droped['G_ALI'] = ana_df_droped['원수 알카리도']
    ana_df_droped['G_MNI'] = ana_df_droped['취수 망간']
    # 250410.전차염 잔류염소 미변동 대기시간 기능 추가.otsman.
    ana_df_droped['G_D_RESIDUAL_CL'] = ana_df_droped['혼화지 잔류염소']
    
    ana_df_droped.loc[ana_df_droped[ana_df_droped['G_PRE_CHOL_RATE'] == 0].index, 'G_PRE_CHOL_RATE'] = np.nan

    ana_df_droped = ana_df_droped.fillna(method='ffill').fillna(method='bfill').fillna(0)

    # 250410.전차염 잔류염소 미변동 대기시간 기능 추가.otsman.
    # 주입률 변경 시점의 시간, 주입률 변화량, 당시의 *혼화지 잔류염소* 값을 저장
    chol_change_diff = round(ana_df_droped['G_PRE_CHOL_RATE'].diff()[ana_df_droped['G_PRE_CHOL_RATE'].diff() != 0].tail(1).values[0], 2)
    chol_change_time = ana_df_droped[ana_df_droped['G_PRE_CHOL_RATE'].diff() != 0]['G_PRE_CHOL_RATE'].tail(1).index
    chol_change_cl = ana_df_droped.loc[chol_change_time[0], 'G_D_RESIDUAL_CL']

    # 펌프 RUN 여부 저장
    g_pump_1_run = 1 if ana_df_droped.iloc[-1]['전차염 펌프1 RUN'] == 1 else 0
    g_pump_2_run = 1 if ana_df_droped.iloc[-1]['전차염 펌프2 RUN'] == 1 else 0

    # 
    num_datas = len(ana_df_droped)
    savgol_window_size = min(30, num_datas//2)
    mov_avg_window_size = min(30, num_datas//2)
    
    # 모델 입력 데이터 스무딩
    if num_datas >= 1440:
        ana_df_droped["G_TEI_WATER"] = savgol_filter(ana_df_droped['원수 수온'], savgol_window_size, 3)
        ana_df_droped["G_D_RESIDUAL_CL"] = savgol_filter(ana_df_droped['혼화지 잔류염소'], savgol_window_size, 3)
        ana_df_droped["G_TBI"] = savgol_filter(ana_df_droped['원수 탁도'], savgol_window_size, 3)
        ana_df_droped["G_PHI"] = savgol_filter(ana_df_droped['원수 pH'], savgol_window_size, 3)
        ana_df_droped["G_CUI"] = savgol_filter(ana_df_droped['원수 전기전도도'], savgol_window_size, 3)
        ana_df_droped["G_ALI"] = savgol_filter(ana_df_droped['원수 알카리도'], savgol_window_size, 3)
        ana_df_droped["G_MNI"] = savgol_filter(ana_df_droped['취수 망간'], savgol_window_size, 3)

    else:
        ana_df_droped['G_TEI_WATER'] = ana_df_droped['원수 수온'].rolling(window=mov_avg_window_size, min_periods=1, center=False).mean()
        ana_df_droped["G_D_RESIDUAL_CL"] = ana_df_droped['혼화지 잔류염소'].rolling(window=mov_avg_window_size, min_periods=1, center=False).mean()
        
    # 전처리 데이터 중 체류시간 후 혼화지 잔류염소 예측 모델 입력에 사용될 변수만 추출
    ana_df_cli = ana_df_droped.loc[:, ['G_TBI', 'G_PHI', 'G_ALI', 'G_CUI', 'G_TEI_WATER', 'G_MNI', 'G_D_RESIDUAL_CL', 'G_PRE_CHOL_RATE']]
    
    ana_df_cli = ana_df_cli.interpolate(method='linear')

    # 파생변수 생성
    df_samp = []
    df_temp = ana_df_cli.copy()

    for col in df_temp.columns:
        df_samp.append( ana_df_cli[col] )

    df_sample = pd.concat(df_samp, axis=1)
    
    df_sample_origin = df_sample.copy()
    df_sample = df_sample[['G_TBI', 'G_PHI', 'G_ALI', 'G_CUI', 'G_TEI_WATER', 'G_MNI', 'G_D_RESIDUAL_CL', 'G_PRE_CHOL_RATE']]
    
    # GRU 모델 Input sequence 길이만큼 데이터 추출
    # 금산 전차염 현재 모델 sequence : 120
    if len(df_sample) > 120:
        data_x = df_sample.copy().iloc[-120:]
        data_x = data_x.fillna(method='bfill').fillna(0)
        data_x_origin = data_x.copy()      
    else:
        row_list = [df_sample.iloc[0]] * 120
        df_list = pd.DataFrame(row_list)
        df_sample = pd.concat([df_list, df_sample])
        data_x = df_sample.copy().iloc[-120:]
        data_x = data_x.fillna(method='bfill').fillna(0)
        data_x_origin = data_x.copy()   

    # Min-max scaling
    min_max_csv = pd.read_csv(MODEL_PATH + MIN_MAX_SCALE_FILENAME_PRE, index_col='title')

    for col in data_x.columns:
        if col in min_max_csv.index:
            min_col_val = min_max_csv.loc[col, 'MIN']
            max_col_val = min_max_csv.loc[col, 'MAX']
            data_x.loc[:, col] = (data_x[col] - min_col_val) / (max_col_val - min_col_val)

    # feature, target 변수 설정
    feature = data_x.columns.tolist()
    
    # predict
    X = data_x[feature].to_numpy().reshape(1, 120, -1)
    predict = model_pre.predict(X)
    ai_g_d_residual_cl = round(predict[0][0].astype('float64'), 2)

    # 데이터 문제 등으로 인해 OUT_VAL이 NaN일 경우에 대해 따로 처리
    # 증발량 예측값이 nan일 경우 -> ai 결과테이블의 가장 마지막 예측 증발량으로 설정
    if np.isnan(ai_g_d_residual_cl):
        ai_g_d_residual_cl = json.loads(df_ai_pre_rt.iloc[-1]['OUT_VAL'])['AI_D_RESIDUAL_CL']

    # 보정주기 계산용 rnti 갱신
    rnti = datetime.now()

    rnti_to_str = f'\"{rnti}\"'
    
    # 주입률 결정 피드백 수식 설정하기
    g_d_residual_cl = round(g_rt_df.loc[now, '혼화지 잔류염소'].astype('float64'), 2)
    g_pre_chol_rate = round(g_rt_df.loc[now, '전차염 목표주입률'].astype('float64'), 2)
    g_b_residual_cl = round(g_rt_df.loc[now, '원수 잔류염소'].astype('float64'), 2)

    # Init 데이터 할당
    g_pre_operation_mode = dict_pre_init['g_pre_operation_mode']
    g_pre_set_max = dict_pre_init['g_pre_set_max']
    g_pre_set_min = dict_pre_init['g_pre_set_min']
    g_pre_chg_limit_for_onetime = dict_pre_init['g_pre_chg_limit_for_onetime']
    g_pre_calib_cycle = dict_pre_init['g_pre_calib_cycle']
    g_d_obj_residual_cl = dict_pre_init['g_d_obj_residual_cl']
    g_d_residual_cl_holding = dict_pre_init['g_d_residual_cl_holding']
    # 250410.전차염 잔류염소 미변동 대기시간 기능 추가.otsman.
    g_pre_chol_rate_holding_time = dict_pre_init['g_pre_chol_rate_holding_time']

    ai_g_pre_consumption = round(g_pre_chol_rate + g_b_residual_cl - ai_g_d_residual_cl, 2)

    
    # 목표 혼화지 잔류염소와 예측 혼화지 잔류염소와의 편차만큼 주입률 변경
    # 편차가 1회변경 가능 주입률 범위를 초과 시 1회 변경 가능 주입률 범위 만큼만 변경
    if round(g_d_obj_residual_cl - g_d_residual_cl, 2) > g_d_residual_cl_holding:
        ai_g_pre_correct_degree = min(g_pre_chg_limit_for_onetime, round( (g_d_obj_residual_cl - g_d_residual_cl - 0.005) /3, 2) )
    elif round(g_d_obj_residual_cl - g_d_residual_cl, 2) < -g_d_residual_cl_holding:
        ai_g_pre_correct_degree = max(-g_pre_chg_limit_for_onetime, -round( (g_d_residual_cl - g_d_obj_residual_cl - 0.005) /3, 2) )
    else:
        ai_g_pre_correct_degree = 0

        
    # 보정로직 : 전차염의 경우 목표 혼화지 잔류염소와 혼화지 잔류염소의 편차가 '혼화지 잔류염소 홀딩' 이상일 경우에만 주입률을 변경한다.
    if g_d_obj_residual_cl - g_d_residual_cl_holding <= g_d_residual_cl <= g_d_obj_residual_cl + g_d_residual_cl_holding:
        ai_g_pre_correct_degree = 0
        
    # 주입률 변경치에 맞게 주입률 설정
    g_pre_chol = round(g_pre_chol_rate + ai_g_pre_correct_degree, 2)

    # 다음 주입률 예측값이 nan일 경우 -> 실시간 rt 테이블의 가장 최근 주입률로 설정
    if np.isnan(g_pre_chol):
        g_pre_chol = g_rt_df.loc[now, 'G_PRE_CHOL_RATE']
    
    # 주입률 상, 하한 설정
    if g_pre_chol > g_pre_set_max:
        g_pre_chol = g_pre_set_max 
    if g_pre_chol < g_pre_set_min:
        g_pre_chol = g_pre_set_min

    # 주입후 경과시간
    elapsed_time = ((rnti - calib_time_pre).seconds // 60)

    # 보정 주기가 지났을 경우 주입률 변경, 그렇지 않을 경우 기존 주입률 값으로 설정
    # 250410.전차염 잔류염소 미변동 대기시간 기능 추가.otsman.
    if calib_time_pre + timedelta(minutes=g_pre_calib_cycle) <= rnti:
        # 만약 주입률 변동 후 현재까지 n분이 지나지 않았다면 (n : 주입률 변동 후 잔류염소 미변동 시 대기시간 init값, default: 80분),
        # 주입률 변경 정도에 맞게 *혼화지 잔류염소*가 변화했다면, 주입률 예측 결과로 주입률 제어를 진행함.
        # *혼화지 잔류염소*가 변화하지 않았다면, 주입률 예측 결과로 제어를 진행하지 않음.
        # 80분 : 역세 주기를 2회 반복하는 시간
        if chol_change_time <= now - timedelta(minutes=g_pre_chol_rate_holding_time):
            pre_chol = g_pre_chol
            calib_time_pre = rnti
            count_pre = 0
            elapsed_time = 0
        else:
            # 주입률 변동 후 80분 이내 : *혼화지 잔류염소값*에 따라 변동 혹은 변동하지 않는다.
            if ((g_d_residual_cl - chol_change_cl) * 2 > chol_change_diff) and (chol_change_diff > 0):
                pre_chol = g_pre_chol
                calib_time_pre = rnti
                count_pre = 0
                elapsed_time = 0
            elif ((g_d_residual_cl - chol_change_cl) * 2 < chol_change_diff) and (chol_change_diff < 0):
                pre_chol = g_pre_chol
                calib_time_pre = rnti
                count_pre = 0
                elapsed_time = 0
            else:
                # 주입률 변동 후 80분 이내이며, *혼화지 잔류염소값* 변동이 주입률 변동치의 절반보다 덜하면 주입률을 변경하지 않는다.
                g_pre_chol = pre_chol
                calib_time_pre = rnti
                count_pre = 0
                elapsed_time = 0
    else:
        g_pre_chol = pre_chol
        count_pre += 1

    # 250410.예측 주입률이 0 -> 코드 시작 시 기존 주입률 변동이 있는 데 *혼화지 잔류염소* 변동이 없는 경우이며, 이 때는 현재값 유지
    if g_pre_chol == 0:
        g_pre_chol = g_pre_chol_rate

    IN_VAL_PRE = dict()
    IN_VAL_PRE['G_PRE_CHOL_RATE'] = g_pre_chol_rate
    IN_VAL_PRE['G_D_RESIDUAL_CL'] = g_d_residual_cl
    IN_VAL_PRE['G_D_OBJ_RESIDUAL_CL'] = g_d_obj_residual_cl
    IN_VAL_PRE['G_TEI_WATER'] = round(g_rt_df.loc[now, '원수 수온'].astype('float64'), 2)
    IN_VAL_PRE['G_TBI'] = round(g_rt_df.loc[now, '원수 탁도'].astype('float64'), 2)
    IN_VAL_PRE['G_PHI'] = round(g_rt_df.loc[now, '원수 pH'].astype('float64'), 2)
    IN_VAL_PRE['G_CUI'] = round(g_rt_df.loc[now, '원수 전기전도도'].astype('float64'), 2)
    IN_VAL_PRE['G_ALI'] = round(g_rt_df.loc[now, '원수 알카리도'].astype('float64'), 2)
    IN_VAL_PRE['G_MNI'] = round(g_rt_df.loc[now, '취수 망간'].astype('float64'), 2)
    IN_VAL_PRE['B_IN_FR'] = round(g_rt_df.loc[now, '원수 유입유량'].astype('float64'), 2)
    IN_VAL_PRE['G_B_RESIDUAL_CL'] = round(g_rt_df.loc[now, '원수 잔류염소'].astype('float64'), 2)
    
    OUT_VAL_PRE = dict()
    OUT_VAL_PRE = {
        'AI_G_D_RESIDUAL_CL':ai_g_d_residual_cl,
        'AI_G_CONSUMPTION':ai_g_pre_consumption,
        'AI_G_CHOL_RATE' : g_pre_chol,
        'G_PUMP_1_RUN': g_pump_1_run,
        'G_PUMP_2_RUN': g_pump_2_run,
        'G_ELAPSED_TIME':elapsed_time,
    }
    
    # 입/출력 변수 Dictionary to json
    IN_VAL_PRE_json = json.dumps(IN_VAL_PRE)
    OUT_VAL_PRE_json = json.dumps(OUT_VAL_PRE)

    # 데이터 저장 시점의 현재 시각으로 갱신
    rnti = datetime.now()
    
    # 소독 공정 결과 테이블 형태의 DataFrame 생성
    df_pre_final = pd.DataFrame(columns = ['upd_ti', 'AI_OPR', 'IN_VAL', 'OUT_VAL'], index=[rnti])
    df_pre_final['upd_ti'] = rnti
    df_pre_final['AI_OPR'] = g_pre_operation_mode
    df_pre_final['IN_VAL'] = IN_VAL_PRE_json
    df_pre_final['OUT_VAL'] = OUT_VAL_PRE_json
        

    # TB_AI_FACTOR에 넣은 주요 인자 구성
    FACTOR_PRE = dict()
    FACTOR_PRE['b_te'] = IN_VAL_PRE['G_TEI_WATER']
    FACTOR_PRE['b_tb'] = IN_VAL_PRE['G_TBI']
    FACTOR_PRE['b_ph'] = IN_VAL_PRE['G_PHI']
    FACTOR_PRE['b_cu'] = IN_VAL_PRE['G_CUI']
    FACTOR_PRE['b_al'] = IN_VAL_PRE['G_ALI']
    FACTOR_PRE['mnr'] = IN_VAL_PRE['G_MNI']
    FACTOR_PRE['g_d_residual_cl'] = IN_VAL_PRE['G_D_RESIDUAL_CL']
    FACTOR_PRE['g_pre_chol_rate'] = IN_VAL_PRE['G_PRE_CHOL_RATE']
    FACTOR_PRE['g_b_residual_cl'] = IN_VAL_PRE['G_B_RESIDUAL_CL']

    FACTOR_PRE_json = json.dumps([FACTOR_PRE])

    df_factor_pre = pd.DataFrame(['G', 'PRE', rnti, FACTOR_PRE_json], index=['proc_cd', 'disinfection_index', 'rnti', 'factor']).transpose()
    
    # CTR_TAG로부터 ITM 가져오기
    ctr_to_itm = {
        '771-358-CIC-5113':'CIC-5113',
    }
        
    # in_val_itm, out_val_itm 파일 load
    in_val_itm = get_in_val_itm()
    out_val_itm = get_out_val_itm()

    # in_val_itm에서 step이 'g'인 모든 row들 제거
    drop_idx = (in_val_itm[in_val_itm['step'] == 'g'].index)  
    in_val_itm = in_val_itm.drop(index=drop_idx)
    # 로깅할 데이터만 in_val_itm에 추가
    itm_pre = {
        'site':['gs', 'gs', 'gs', 'gs', 'gs', 'gs', 'gs', 'gs'],
        'step':['g', 'g', 'g', 'g', 'g', 'g', 'g', 'g'],
        'json_itm':['G_TEI_WATER', 'G_TBI', 'G_PHI', 'G_CUI', 'G_ALI', 'G_MNI', 'G_D_RESIDUAL_CL', 'G_PRE_CHOL_RATE'],
        'itm':['b_te', 'b_tb', 'b_ph', 'b_cu', 'b_al', 'mnr', 'CLI-6001', 'CIC-5113']
    }
    df_new_itm_pre = pd.DataFrame(data=itm_pre)
    in_val_itm = pd.concat([in_val_itm, df_new_itm_pre], ignore_index=True)
    set_in_val_itm(in_val_itm)

    
    
    # out_val_itm에서 step이 'g'이고 json_itm이 'AI_G_CHOL_RATE'인 row들 제거
    drop_idx = (out_val_itm[out_val_itm['step'] == 'g'].index).intersection(out_val_itm[out_val_itm['json_itm'] == 'AI_G_CHOL_RATE'].index)    
    out_val_itm = out_val_itm.drop(index=drop_idx)
    # 로깅할 데이터만 out_val_itm에 추가
    itm_pre = {
        'site':['gs'],
        'step':['g'],
        'json_itm':['AI_G_CHOL_RATE'],
        'itm':ctr_to_itm[G_PRE_CTR_TAG]
    }
    df_new_itm_pre = pd.DataFrame(data=itm_pre)
    out_val_itm = pd.concat([out_val_itm, df_new_itm_pre], ignore_index=True)
    set_out_val_itm(out_val_itm)
    
    # 전차염 저장 및 logging
    db.save_ai_rt('TB_AI_PRE_G_RT', df_pre_final)

    # 전차염 CTR 테이블에 저장
    save_ctr_tag(
        g_pre_operation_mode,
        db,
        count_pre,
        round(g_pre_chol - g_pre_chol_rate, 2),
        0.01, # threshold
        CTR_TB_PRE,
        ALM_TB_PRE,
        now,
        G_PRE_CTR_TAG,
        g_pre_chol,
        g_pre_chol_rate,
        0,
        0,
        ALM_CTR_PRE,
        ALM_THRESHOLD_PRE,
        # ana_df_droped.iloc[-60:]['G_PRE_CHOL_RATE'],
        min_chol_rate = g_pre_set_min,
        max_chol_rate = g_pre_set_max,
        df_factor = df_factor_pre, 
        RNTI=rnti
    )

    return calib_time_pre, count_pre, pre_chol

def perform_post(db, calib_time_post, count_post, post_chol, job_datetime):
    """
    후차염의 실시간 데이터 load 부터 전처리, 증발량 및 주입률 예측 진행하는 함수

    Args:
        db : 연결된 db connection
        calib_time_post : 설정된 보정주기
        count_post : 이전 보정주기 도달 이후 운영코드 실행 횟수
        post_chol : 현재 주입률
        job_datetime : 특정 시점부터 데이터를 불러오기 위한 설정값. None일 경우 가장 최근 데이터로부터 불러옴

    Returns:
        calib_time_post : 설정된 보정주기
        count_post : 이전 보정주기 도달 이후 운영코드 실행 횟수
        post_chol : 현재 주입률        
    """
    now_str = '2023-12-15 00:00:00'
    now = datetime.strptime(now_str, '%Y-%m-%d %H:%M:%S')
    residual_time = 10
    
    tags = (
        '771-358-TEI-6028',
        '771-358-TBI-6011',
        '771-358-CIB-5212',
        '771-358-CIB-5222',
        '771-358-CLI-4011',
        '771-358-CLI-4021',
        '771-358-CII-5213',
        '771-358-CIC-5213',
        '771-358-CII-5503',
        '771-358-CIC-5504',
        '771-358-CIC-5505',
        '771-358-PMB-6014',
        '771-358-PMB-6016',
        '771-358-CLI-6022',
        '771-358-CIC-5502',
        '771-358-PHI-4021',
        '771-358-TBI-4021',
        '771-358-FRI-4011',
        '771-358-CIB-5501',
    )

    if job_datetime is None:
        g_rt_df = db.read_rt_subday_max('TB_G_RT', tags)
        now_str = str(g_rt_df.iloc[-1].name)
        now = datetime.strptime(now_str, '%Y-%m-%d %H:%M:%S')
        rnti = datetime.now()
    else:
        g_rt_df = db.read_rt_subday_etime('TB_G_RT', tags, job_datetime)
        now_str = str(g_rt_df.iloc[-1].name)
        now = datetime.strptime(now_str, '%Y-%m-%d %H:%M:%S')
        rnti = datetime.now()
    if len(g_rt_df) <= 0:
        return
        
    for tag in tags:
        if tag not in g_rt_df.columns:
            g_rt_df[tag] = 0

    # index(UPD_TI)를 datetime으로 타입 변환
    g_rt_df.index = pd.to_datetime(g_rt_df.index)
    
    g_rt_df = g_rt_df.resample('1T').first()
    g_rt_df = g_rt_df.fillna(method='ffill')
    g_rt_df = g_rt_df.fillna(method='bfill')
    
    # column 명을 구분하기 쉽도록 변환
    column_name = {
        '771-358-TEI-6028':'원수 수온',
        '771-358-TBI-6011':'막여과 탁도',
        '771-358-CIB-5212':'후차염 펌프1 RUN',
        '771-358-CIB-5222':'후차염 펌프2 RUN',
        '771-358-CLI-4011':'정수지 유입 잔류염소',
        '771-358-CLI-4021':'정수지 유출 잔류염소',
        '771-358-CII-5213':'후차염 현재주입률',
        '771-358-CIC-5213':'후차염 목표주입률',
        '771-358-CII-5503':'후차염 주입률 계산값',
        '771-358-CIC-5504':'후차염 주입률 상한값',
        '771-358-CIC-5505':'후차염 주입률 하한값',
        '771-358-PMB-6014':'역세펌프A RUN',
        '771-358-PMB-6016':'역세펌프B RUN',
        '771-358-CLI-6022':'여과수 통합 잔류염소',
        '771-358-CIC-5502':'후차염 목표잔류염소',
        '771-358-PHI-4021':'정수 pH',
        '771-358-TBI-4021':'정수 탁도',
        '771-358-FRI-4011':'정수지 유입유량',
        '771-358-CIB-5501':'후차염 자동운전',
        }
    g_rt_df = g_rt_df.rename(columns=column_name)

    for col in g_rt_df.columns:
        if np.isnan(g_rt_df.loc[now, col]):
            g_rt_df.loc[now, col] = 0
    ana_df_droped = g_rt_df.copy()

    # Init 테이블 값 Load
    dict_post_init = get_post_init(db)
    
    # RT 테이블 값 Load
    df_ai_post_rt = get_ai_post_rt(db)
    
    #
    # 전처리 시작
    #
    
    # 역세펌프 RUN 구간 확인 및 역세펌프 RUN 시 정수지 유입 잔류염소 값 무시(NaN 처리)
    # 역세펌프는 보통 5분 정도 가동되며, 가동 시작 5분 이후 정수지 유입 잔류염소 값이 헌팅되며 20~30분에 걸쳐 원래대로 돌아오는 경향이 있다.
    # 잔류염소가 역세 영향을 받아 올라갔다 내려오는 구간의 데이터는 헌팅 데이터로 간주하여, 역세펌프 가동 시작 후 약 13~40분 이후 정수지 유입 잔류염소 값을 무시
    
    f_pump_idx = ana_df_droped[ana_df_droped['역세펌프A RUN'] == 1].index
    f_pump_idx = f_pump_idx.union(ana_df_droped[ana_df_droped['역세펌프B RUN'] == 1].index)

    for minute in range(13, 40):
        if minute == 13:
            g_h_residual_cl_hunting_idx = f_pump_idx + timedelta(minutes=minute)
        else:
            g_h_residual_cl_hunting_idx = g_h_residual_cl_hunting_idx.union(f_pump_idx + timedelta(minutes=minute))    
  
    g_h_residual_cl_hunting_idx = g_h_residual_cl_hunting_idx.intersection(ana_df_droped.index)
    
    ana_df_droped.loc[g_h_residual_cl_hunting_idx, '정수지 유입 잔류염소'] = np.nan
    
    # 데이터 상하한 처리 및 이상치(주입률 0값) 처리
    ana_df_droped.loc[(ana_df_droped[ana_df_droped['정수지 유입 잔류염소'] < 0.5]).index, '정수지 유입 잔류염소'] = np.nan
    ana_df_droped.loc[(ana_df_droped[ana_df_droped['정수지 유입 잔류염소'] > 1.8]).index, '정수지 유입 잔류염소'] = np.nan
    
    ana_df_droped['G_POST_CHOL_RATE'] = ana_df_droped['후차염 목표주입률']
    ana_df_droped['G_POST_CHOL_RATE_CAL'] = ana_df_droped['후차염 주입률 계산값']
    
    # 후차염 자동운전이 1인 경우 목표주입률 값으로, 0인 경우 계산값으로 현재 목표주입률을 설정함
    # 금산 후차염은 제어로직의 AUTO, MANUAL 여부에 따라 후차염 주입에 사용하는 주입률 태그가 다르므로 모드 변경에 관계없이 실제 적용된 주입률 값을 'G_POST_CHOL_RATE' 값으로 설정
    ana_df_droped.loc[ana_df_droped[ana_df_droped['후차염 자동운전'] == 1].index, 'G_POST_CHOL_RATE'] = ana_df_droped.loc[ana_df_droped[ana_df_droped['후차염 자동운전'] == 1].index, 'G_POST_CHOL_RATE_CAL']
    
    ana_df_droped.loc[ana_df_droped[ana_df_droped['G_POST_CHOL_RATE_CAL'] == 0].index, 'G_POST_CHOL_RATE_CAL'] = np.nan
    
    ana_df_droped = ana_df_droped.fillna(method='ffill').fillna(method='bfill').fillna(0)

    ana_df_droped.loc[ana_df_droped[ana_df_droped['G_POST_CHOL_RATE'] == 0].index, 'G_POST_CHOL_RATE'] = ana_df_droped.loc[ana_df_droped[ana_df_droped['G_POST_CHOL_RATE'] == 0].index, 'G_POST_CHOL_RATE_CAL']


    
    ana_df_droped['G_TEI_WATER'] = ana_df_droped['원수 수온']
    ana_df_droped['G_TBI'] = ana_df_droped['막여과 탁도']
    ana_df_droped['G_H_IN_RESIDUAL_CL'] = ana_df_droped['정수지 유입 잔류염소']
    ana_df_droped['G_H_IN_RESIDUAL_CL_DEL_HUNT'] = ana_df_droped['정수지 유입 잔류염소']

    # 주입률 변경 시점의 시간, 주입률 변화량, 당시의 정수지 유입 잔류염소 하한값을 저장
    chol_change_diff = round(ana_df_droped['G_POST_CHOL_RATE'].diff()[ana_df_droped['G_POST_CHOL_RATE'].diff() != 0].tail(1).values[0], 2)
    chol_change_time = ana_df_droped[ana_df_droped['G_POST_CHOL_RATE'].diff() != 0]['G_POST_CHOL_RATE'].tail(1).index
    chol_change_cl = ana_df_droped.loc[(chol_change_time - timedelta(minutes=40)).to_pydatetime()[0]:(chol_change_time).to_pydatetime()[0]]['G_H_IN_RESIDUAL_CL'].min()
    
    # 펌프 RUN 여부 저장
    g_pump_1_run = 1 if ana_df_droped.iloc[-1]['후차염 펌프1 RUN'] == 1 else 0
    g_pump_2_run = 1 if ana_df_droped.iloc[-1]['후차염 펌프2 RUN'] == 1 else 0

    # 
    num_datas = len(ana_df_droped)
    savgol_window_size = min(30, num_datas//2)
    mov_avg_window_size = min(30, num_datas//2)
    
    # 모델 입력 데이터 스무딩
    if num_datas >= 1440:
        ana_df_droped["G_TEI_WATER"] = savgol_filter(ana_df_droped['원수 수온'], savgol_window_size, 3)
        ana_df_droped["G_H_IN_RESIDUAL_CL"] = savgol_filter(ana_df_droped['정수지 유입 잔류염소'], savgol_window_size, 3)

    else:
        ana_df_droped['G_TEI_WATER'] = ana_df_droped['원수 수온'].rolling(window=mov_avg_window_size, min_periods=1, center=False).mean()
        ana_df_droped["G_H_IN_RESIDUAL_CL"] = ana_df_droped['정수지 유입 잔류염소'].rolling(window=mov_avg_window_size, min_periods=1, center=False).mean()
        
    # 전처리 데이터 중 증발량 예측 모델 입력에 사용될 변수만 추출
    ana_df_cli = ana_df_droped.loc[:, ['G_TEI_WATER', 'G_TBI', 'G_H_IN_RESIDUAL_CL', 'G_POST_CHOL_RATE']]
    
    ana_df_cli = ana_df_cli.interpolate(method='linear')

    # 파생변수 생성
    df_samp = []
    df_temp = ana_df_cli.copy()

    for col in df_temp.columns:
        df_samp.append( ana_df_cli[col] )

    df_sample = pd.concat(df_samp, axis=1)

    df_sample_origin = df_sample.copy()
    df_sample = df_sample[['G_TBI', 'G_TEI_WATER', 'G_H_IN_RESIDUAL_CL', 'G_POST_CHOL_RATE']]
    
    # GRU 모델 Input sequence 길이만큼 데이터 추출
    # 금산 후차염 모델 sequence : 30
    if len(df_sample) > 30:
        data_x = df_sample.copy().iloc[-30:]
        data_x = data_x.fillna(method='bfill').fillna(0)
        data_x_origin = data_x.copy()      
    else:
        row_list = [df_sample.iloc[0]] * 30
        df_list = pd.DataFrame(row_list)
        df_sample = pd.concat([df_list, df_sample])
        data_x = df_sample.copy().iloc[-30:]
        data_x = data_x.fillna(method='bfill').fillna(0)
        data_x_origin = data_x.copy()   

    # Min-max scaling
    min_max_csv = pd.read_csv(MODEL_PATH + MIN_MAX_SCALE_FILENAME_POST, index_col='title')

    for col in data_x.columns:
        if col in min_max_csv.index:
            min_col_val = min_max_csv.loc[col, 'MIN']
            max_col_val = min_max_csv.loc[col, 'MAX']
            data_x.loc[:, col] = (data_x[col] - min_col_val) / (max_col_val - min_col_val)

    # feature, target 변수 설정
    feature = data_x.columns.tolist()
    
    # predict
    X = data_x[feature].to_numpy().reshape(1, 30, -1)
    predict = model_post.predict(X)
    ai_g_h_in_residual_cl = round(predict[0][0].astype('float64'), 2)

    # OUT_VAL이 NaN일 경우에 대해 따로 처리
    # 증발량 예측값이 nan일 경우 -> ai 결과테이블의 가장 마지막 예측 증발량으로 설정
    if np.isnan(ai_g_h_in_residual_cl):
        ai_g_h_in_residual_cl = json.loads(df_ai_post_rt.iloc[-1]['OUT_VAL'])['AI_G_H_IN_RESIDUAL_CL']

    
    # 보정주기 계산용 rnti 갱신
    rnti = datetime.now()

    rnti_to_str = f'\"{rnti}\"'
    
    # 주입률 결정 피드백 수식 설정하기
    g_h_in_residual_cl = round(g_rt_df.loc[now, '정수지 유입 잔류염소'].astype('float64'), 2)
    g_h_out_residual_cl = round(g_rt_df.loc[now, '정수지 유출 잔류염소'].astype('float64'), 2)
    g_post_chol_rate = round(ana_df_droped.loc[now, 'G_POST_CHOL_RATE'].astype('float64'), 2)
    g_post_chol_rate_cal = round(g_rt_df.loc[now, '후차염 주입률 계산값'].astype('float64'), 2)
    g_post_chol_rate_upper = round(g_rt_df.loc[now, '후차염 주입률 상한값'].astype('float64'), 2)
    g_post_chol_rate_lower = round(g_rt_df.loc[now, '후차염 주입률 하한값'].astype('float64'), 2)
    g_f_out_residual_cl = round(g_rt_df.loc[now, '여과수 통합 잔류염소'].astype('float64'), 2)
    
    # 정수지 유입 잔류염소 최근 40분 데이터 및 하한값 설정
    before_40m = now - timedelta(minutes=40)
    g_h_in_residual_cl_prep = round((g_rt_df.loc[before_40m:now, '정수지 유입 잔류염소'].min()).astype('float64'), 2)
    g_h_in_residual_cl_40m = g_rt_df.loc[before_40m:now, '정수지 유입 잔류염소'].astype('float64')
    g_h_in_residual_cl_40m_max = g_rt_df.loc[before_40m:now, '정수지 유입 잔류염소'].max().astype('float64')
    
    
    # Init 데이터 할당
    g_post_operation_mode = dict_post_init['g_post_operation_mode']
    g_post_set_max = dict_post_init['g_post_set_max']
    g_post_set_min = dict_post_init['g_post_set_min']
    g_post_chg_limit_for_onetime = dict_post_init['g_post_chg_limit_for_onetime']
    g_post_calib_cycle = dict_post_init['g_post_calib_cycle']
    g_h_in_obj_residual_cl = dict_post_init['g_h_in_obj_residual_cl']
    g_h_in_residual_cl_holding = dict_post_init['g_h_in_residual_cl_holding']
    g_post_chol_rate_holding_time = dict_post_init['g_post_chol_rate_holding_time']

    # 후염소 염소소모량 계산
    # 수정 : 여과수 통합 잔류염소를 소모량 계산 시 반영
    # ai_g_post_consumption = g_post_chol_rate - ai_g_h_in_residual_cl
    ai_g_post_consumption = round(g_post_chol_rate - ai_g_h_in_residual_cl + g_f_out_residual_cl, 2)

    # 정수지 유입 잔류염소 하한값이 목표값보다 0.05 ppm 이상 차이날 경우 주입률 변경
    if round(g_h_in_obj_residual_cl - g_h_in_residual_cl_prep, 2) > g_h_in_residual_cl_holding:
        ai_g_post_correct_degree = min(g_post_chg_limit_for_onetime, round( (g_h_in_obj_residual_cl - g_h_in_residual_cl_prep - 0.005) / 3, 2) )
    elif round(g_h_in_obj_residual_cl - g_h_in_residual_cl_prep, 2) < -g_h_in_residual_cl_holding:
        ai_g_post_correct_degree = max(-g_post_chg_limit_for_onetime, -round( (g_h_in_residual_cl_prep - g_h_in_obj_residual_cl - 0.005) / 3, 2) ) 
    else:
        ai_g_post_correct_degree = 0 
        
    if round(g_h_in_obj_residual_cl - g_h_in_residual_cl_holding, 2) <= g_h_in_residual_cl_prep <= round(g_h_in_obj_residual_cl + g_h_in_residual_cl_holding, 2):
        ai_g_post_correct_degree = 0
                
    # 주입률 변경치에 맞게 주입률 설정
    g_post_chol = round(g_post_chol_rate + ai_g_post_correct_degree, 2)

    # 다음 주입률 예측값이 nan일 경우 -> 실시간 rt 테이블의 가장 최근 주입률로 설정
    if np.isnan(g_post_chol):
        g_post_chol = g_rt_df.loc[now, 'G_POST_CHOL_RATE']
    
    # 주입률 상, 하한 설정
    if g_post_chol > g_post_set_max:
        g_post_chol = g_post_set_max 
    if g_post_chol < g_post_set_min:
        g_post_chol = g_post_set_min

    # 주입후 경과시간
    elapsed_time = ((rnti - calib_time_post).seconds // 60)

    # 보정 주기가 지났을 경우 주입률 변경, 그렇지 않을 경우 기존 주입률 값으로 설정
    # 추가일자 : 24.09.04
    if calib_time_post + timedelta(minutes=g_post_calib_cycle) <= rnti:
        # 만약 주입률 변동 후 현재까지 n분이 지나지 않았다면 (n : 주입률 변동 후 잔류염소 미변동 시 대기시간 init값, default: 80분),
        # 주입률 변경 정도에 맞게 정수지 유입 잔류염소가 변화했다면, 주입률 예측 결과로 주입률 제어를 진행함.
        # 정수지 유입 잔류염소가 변화하지 않았다면, 주입률 예측 결과로 제어를 진행하지 않음.
        # 80분 : 역세 주기를 2회 반복하는 시간
        if chol_change_time <= now - timedelta(minutes=g_post_chol_rate_holding_time):
            post_chol = g_post_chol
            calib_time_post = rnti
            count_post = 0
            elapsed_time = 0
        else:
            # 주입률 변동 후 80분 이내 : 정수지 유입 잔류염소 값에 따라 변동 혹은 변동하지 않는다.
            if ((g_h_in_residual_cl_prep - chol_change_cl) * 2 > chol_change_diff) and (chol_change_diff > 0):
                post_chol = g_post_chol
                calib_time_post = rnti
                count_post = 0
                elapsed_time = 0      
            elif ((g_h_in_residual_cl_prep - chol_change_cl) * 2 < chol_change_diff) and (chol_change_diff < 0):
                post_chol = g_post_chol
                calib_time_post = rnti
                count_post = 0
                elapsed_time = 0
            else:
                # 주입률 변동 후 80분 이내이며, 유입 잔류염소 변동이 주입률 변동치의 절반보다 덜하면 주입률을 변경하지 않는다.
                g_post_chol = post_chol
                calib_time_post = rnti
                count_post = 0
                elapsed_time = 0
    else:
        g_post_chol = post_chol
        count_post += 1

    # 예측 주입률이 0 -> 코드 시작 시 기존 주입률 변동이 있는 데 잔류염소 변동이 없는 경우이며, 이 때는 현재값 유지
    if g_post_chol == 0:
         g_post_chol = g_post_chol_rate
    
    IN_VAL_POST = dict()
    # for feature in data_x.columns:
    #     IN_VAL_PRE[ feature ] = df_sample_origin.loc[now, feature].astype('float64')
    IN_VAL_POST['G_POST_CHOL_RATE'] = g_post_chol_rate
    IN_VAL_POST['G_POST_CHOL_RATE_CAL'] = g_post_chol_rate_cal
    IN_VAL_POST['G_H_IN_RESIDUAL_CL'] = g_h_in_residual_cl
    IN_VAL_POST['G_H_IN_RESIDUAL_CL_PREP'] = g_h_in_residual_cl_prep
    IN_VAL_POST['G_H_OUT_RESIDUAL_CL'] = g_h_out_residual_cl
    IN_VAL_POST['G_H_IN_OBJ_RESIDUAL_CL'] = g_h_in_obj_residual_cl
    IN_VAL_POST['G_TEI_WATER'] = round(g_rt_df.loc[now, '원수 수온'].astype('float64'), 2)
    IN_VAL_POST['G_TBI'] = round(g_rt_df.loc[now, '막여과 탁도'].astype('float64'), 2)
    IN_VAL_POST['G_POST_CHOL_RATE_UPPER'] = g_post_chol_rate_upper
    IN_VAL_POST['G_POST_CHOL_RATE_LOWER'] = g_post_chol_rate_lower
    IN_VAL_POST['H_TB'] = round(g_rt_df.loc[now, '정수 탁도'].astype('float64'), 2)
    IN_VAL_POST['H_PH'] = round(g_rt_df.loc[now, '정수 pH'].astype('float64'), 2)
    IN_VAL_POST['B_IN_FR'] = round(g_rt_df.loc[now, '정수지 유입유량'].astype('float64'), 2)
    IN_VAL_POST['G_F_OUT_RESIDUAL_CL'] = round(g_rt_df.loc[now, '여과수 통합 잔류염소'].astype('float64'), 2)
    
    OUT_VAL_POST = dict()
    OUT_VAL_POST = {
        'AI_G_H_IN_RESIDUAL_CL':ai_g_h_in_residual_cl,
        'AI_G_CONSUMPTION':ai_g_post_consumption,
        'AI_G_CHOL_RATE' : g_post_chol,
        'G_PUMP_1_RUN': g_pump_1_run,
        'G_PUMP_2_RUN': g_pump_2_run,
        'G_ELAPSED_TIME': elapsed_time,
    }
    
    # 입/출력 변수 Dictionary to json
    IN_VAL_POST_json = json.dumps(IN_VAL_POST)
    OUT_VAL_POST_json = json.dumps(OUT_VAL_POST)

    # 데이터 저장 시점의 현재 시각으로 갱신
    rnti = datetime.now()
    
    # 소독 공정 결과 테이블 형태의 DataFrame 생성
    df_post_final = pd.DataFrame(columns = ['upd_ti', 'AI_OPR', 'IN_VAL', 'OUT_VAL'], index=[rnti])
    df_post_final['upd_ti'] = rnti
    df_post_final['AI_OPR'] = g_post_operation_mode
    df_post_final['IN_VAL'] = IN_VAL_POST_json
    df_post_final['OUT_VAL'] = OUT_VAL_POST_json

    # TB_AI_FACTOR에 주요 인자 insert
    FACTOR_POST = dict()
    FACTOR_POST['b_te'] = IN_VAL_POST['G_TEI_WATER']
    FACTOR_POST['h_tb'] = IN_VAL_POST['H_TB']
    FACTOR_POST['g_h_in_residual_cl_prep'] = IN_VAL_POST['G_H_IN_RESIDUAL_CL_PREP']
    FACTOR_POST['g_post_chol_rate'] = IN_VAL_POST['G_POST_CHOL_RATE']
    FACTOR_POST['g_f_out_residual_cl'] = IN_VAL_POST['G_F_OUT_RESIDUAL_CL']

    FACTOR_POST_json = json.dumps([FACTOR_POST])

    df_factor_post = pd.DataFrame(['G', 'POST', rnti, FACTOR_POST_json], index=['proc_cd', 'disinfection_index', 'rnti', 'factor']).transpose()
        

    # CTR_TAG로부터 ITM 가져오기
    ctr_to_itm = {
        '771-358-CIC-5213':'CIC-5213',
    }
        
    # in_val_itm, out_val_itm 파일 load
    in_val_itm = get_in_val_itm()
    out_val_itm = get_out_val_itm()


    # in_val_itm에서 step이 'g2_liv'인 모든 row들 제거
    drop_idx = (in_val_itm[in_val_itm['step'] == 'g'].index)  
    in_val_itm = in_val_itm.drop(index=drop_idx)
    # 로깅할 데이터만 in_val_itm에 추가
    itm_post = {
        'site':['gs', 'gs', 'gs', 'gs'],
        'step':['g', 'g', 'g', 'g'],
        'json_itm':['G_TEI_WATER', 'G_TBI', 'G_H_IN_RESIDUAL_CL', 'G_POST_CHOL_RATE'],
        'itm':['b_te', 'f_tb', 'CLI-4011', 'CIC-5213']
    }
    df_new_itm_post = pd.DataFrame(data=itm_post)
    in_val_itm = pd.concat([in_val_itm, df_new_itm_post], ignore_index=True)
    set_in_val_itm(in_val_itm)
    
    # out_val_itm에서 step이 'g'이고 json_itm이 'AI_G_CHOL_RATE'인 row들 제거
    drop_idx = (out_val_itm[out_val_itm['step'] == 'g'].index).intersection(out_val_itm[out_val_itm['json_itm'] == 'AI_G_CHOL_RATE'].index)    
    out_val_itm = out_val_itm.drop(index=drop_idx)
    # 로깅할 데이터만 out_val_itm에 추가
    itm_post = {
        'site':['gs'],
        'step':['g'],
        'json_itm':['AI_G_CHOL_RATE'],
        'itm':ctr_to_itm[G_POST_CTR_TAG]
    }
    df_new_itm_post = pd.DataFrame(data=itm_post)
    out_val_itm = pd.concat([out_val_itm, df_new_itm_post], ignore_index=True)
    set_out_val_itm(out_val_itm)
    
    # 후차염 저장 및 logging
    db.save_ai_rt('TB_AI_POST_G_RT', df_post_final)

    
    
    # # 후차염 CTR 테이블에 저장
    save_ctr_tag(
        g_post_operation_mode,
        db,
        count_post,
        round(g_post_chol - g_post_chol_rate, 2),
        0.01, # threshold
        CTR_TB_POST,
        ALM_TB_POST,
        now,
        G_POST_CTR_TAG,
        g_post_chol,
        g_post_chol_rate,
        0,
        0,
        ALM_CTR_POST,
        ALM_THRESHOLD_POST,
        # ana_df_droped.iloc[-60:]['G_PRE_CHOL_RATE'],
        min_chol_rate = g_post_set_min,
        max_chol_rate = g_post_set_max,
        df_factor = df_factor_post,
        RNTI=rnti
    )

    rnti = f'\"{rnti}\"'
    # 정수지 유출 잔류염소가 기준치(0.7 ~ 1.1) 초과 시 알람 발생
    if (g_h_out_residual_cl < 0.7) or (g_h_out_residual_cl > 1.1):
        db.save_alm(
            ALM_TB_POST,
            ALM_H_OUT_RESIDUAL_CL_EXCEEDED,
            rnti
        )
    
    return calib_time_post, count_post, post_chol


    


@log_perform
def perform(db, calib_time_pre, calib_time_post, count_pre, count_post, pre_chol, post_chol, job_datetime=None):
    """
    전,중,후차염 각각 perform 함수를 실행하기 위한 공동함수.

    Args:
        db : 연결된 db connection
        calib_time_pre : 설정된 보정주기
        calib_time_post : 설정된 보정주기
        count_pre : 이전 보정주기 도달 이후 운영코드 실행 횟수
        count_post : 이전 보정주기 도달 이후 운영코드 실행 횟수
        pre_chol : 현재 주입률
        post_chol : 현재 주입률
        job_datetime : 특정 시점부터 데이터를 불러오기 위한 설정값. None일 경우 가장 최근 데이터로부터 불러옴

    Returns:
        calib_time_pre : 설정된 보정주기
        calib_time_post : 설정된 보정주기
        count_pre : 이전 보정주기 도달 이후 운영코드 실행 횟수
        count_post : 이전 보정주기 도달 이후 운영코드 실행 횟수
        pre_chol : 현재 주입률
        post_chol : 현재 주입률      
    """
    print('---------------- 공정 실행 ----------------')

    rnti = datetime.now()
    rnti_to_str = f'\"{rnti}\"'

    # 전차염 주입률 결정 코드 실행
    # 실행 중 오류 시 AI 분석 이상 알람 발생
    try:
        calib_time_pre, count_pre, pre_chol = perform_pre(
            db=db,
            calib_time_pre=calib_time_pre,
            count_pre=count_pre,
            pre_chol=pre_chol,
            job_datetime=job_datetime
        )
    except Exception as e:
        db.save_alm(        
            ALM_TB_PRE,
            ALM_ERR_PRE,
            rnti_to_str
        )
        runLogger.error('Error: {}'.format(traceback.format_exc()))
        raise

    print('------------- 전차염 주입률 예측 완료 --------------')
    # 후차염 주입률 결정 코드 실행
    # 실행 중 오류 시 AI 분석 이상 알람 발생
    try:
        calib_time_post, count_post, post_chol = perform_post(
            db=db,
            calib_time_post=calib_time_post,
            count_post=count_post,
            post_chol=post_chol,
            job_datetime=job_datetime,
        )
    except Exception as e:
        db.save_alm(        
            ALM_TB_POST,
            ALM_ERR_POST,
            rnti_to_str
        )
        runLogger.error('Error: {}'.format(traceback.format_exc()))
        raise

    print('------------- 후차염 주입률 예측 완료 --------------')

    print('------------- 공정 완료 -------------')

    return calib_time_pre, calib_time_post, count_pre, count_post, pre_chol, post_chol
