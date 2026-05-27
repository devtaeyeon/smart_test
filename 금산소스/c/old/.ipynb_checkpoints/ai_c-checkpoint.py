##########
# 약품 모듈 파일
# author : Lee Hyeokhui
# since : 2024. 10. 07
# version : 0.1
##########

############################### 경로 설정 및 package load ###############################
# 공통 부분
import sys
import os

# 경로 설정
try:
    PROC_PATH = os.path.dirname(os.path.abspath(__file__))
except:
    PROC_PATH = os.path.dirname(os.path.abspath('_'))

PROC_NAME = os.path.basename(PROC_PATH)

BASE_PATH = os.path.dirname(PROC_PATH)
ROOT_PATH = os.path.dirname(BASE_PATH)
sys.path.append(BASE_PATH + '/common')
sys.path.append(ROOT_PATH + '/common')

MODEL_PATH = '/'.join([PROC_PATH, 'model/']) # 모델 경로
LOGS_PATH = BASE_PATH + '/logs/'             # 로그 경로

# 공통 util 관련 라이브러리
from db_util import DBUtil
from config import Config
from analysis import *
import json
import pickle
from multiprocessing import Process,Manager
import time
from datetime import datetime, timedelta
import warnings
warnings.filterwarnings("ignore")

# Log 관련 라이브러리
import traceback
import logging
from logging import handlers

# 전처리 관련 라이브러리 
from scipy.signal import savgol_filter
from functools import reduce
from scipy import stats

# model 관련 라이브러리
import tensorflow as tf
from tensorflow import keras
import keras.backend as K

# 전송 로거 생성
from aos_util import *
build_logger(ROOT_PATH, BASE_PATH, PROC_NAME)

############################### 로그 처리 ###############################
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

############################### 전처리 함수 ###############################
def upper_lower_bounds(col, upper, lower, dataframe) :
    """
    태그 데이터의 정상 계측 범위 상/하한 값 적용 함수

    Args:
        col (str): 정상 계측 범위를 적용할 컬럼명
        upper (float): 계측 범위 상한값
        lower (float): 계측 범위 하한값
        dataframe (DataFrame): 해당 함수를 적용할 DataFrame
        
    Returns:
        pd.DataFrame: 정상 계측 범위 적용 결과 DataFrame
    """
    if len(dataframe[col].unique()) == 1: 
        pass
        
    else: 
        idx = dataframe[(dataframe[col] >= upper) | (dataframe[col] <= lower)].index
        dataframe.loc[dataframe.index.isin(idx), col] = np.nan
        try: 
            dataframe[col].fillna(method = 'ffill', axis = 0, inplace = True)
            dataframe[col].fillna(method = 'bfill', axis = 0, inplace = True)
        except :
            dataframe[col].fillna(dataframe[col].mean(), axis=0, inplace=True)
    return dataframe

def mode(xs):
    """
    최빈값 산출 함수
    
    Args:
        xs: 최빈값을 산출하기 위한 list
        
    Returns:
        list 내 최빈값
    """
    try:
        return stats.mode(xs)[0][0]
    except IndexError:
        return np.nan

def series_to_supervised_simu(dataframe, n_in = 1, dropnan = True):
    """
    시계열 데이터를 모델 입력 데이터 세트로 변환하는 함수
    
    Args:
        dataframe (DataFrame): 모델 입력 데이터로 변환 할 DataFrame
        n_in (int): 예측하려는 시간 갯수
        dropnan (bool): NA 데이터 drop 여부
        
    Returns:
        pd.DataFrame: 모델 입력 DataFrame
    """
    cols, names = list(), list()
    
    for idx, dt in enumerate(dataframe.index):
        df_shift_input = dataframe.loc[[dt]]
        df_shift_input.columns += '(t-' + str(n_in - idx) + ')'
        df_shift_input.reset_index(drop = True, inplace = True)
        cols.append(df_shift_input)
        names += df_shift_input.columns.tolist()

    agg = pd.concat(cols, axis = 1)
    agg.columns = names

    if dropnan:
        agg.dropna(inplace = True)
    
    return agg

def get_init(db):
    """
    init table에서 값을 불러오는 함수 
    
    Args:
        db: DB Class
    Returns:
        ai_opr: 운영모드값
        co_max: 응집제 주입률 상한값
        co_min: 응집제 주입률 하한값
    """
    sql = """
    SELECT * FROM TB_AI_C_INIT
    """
    df_init = db.read(sql)

    ai_opr = df_init[df_init['ITM'] == 'c_operation_mode']['INIT_VAL'].values[0]
    co_max = df_init[df_init['ITM'] == 'c_cf_max']['INIT_VAL'].values[0]
    co_min = df_init[df_init['ITM'] == 'c_cf_min']['INIT_VAL'].values[0]
    co_user = df_init[df_init['ITM'] == 'c_user_correct']['INIT_VAL'].values[0]
    
    return ai_opr, co_max, co_min, co_user

def ctr_set(db, run_time, control_tag, ai_pred_y_set, last_set):
    """
    제어 테이블 적재 함수
    
    Args:
        db: DB Class
        run_time(datetime): 현재 시간
        control_tag(str): 제어할 태그명
        ai_pred_y_set(float): 응집제 주입률 예측값
        last_set(float): 응집제 주입률 기존값
    """
    tag_sn = f'"{control_tag}"'
    
    db.save_ctr('TB_AI_C_CTR', run_time, run_time, tag_sn, ai_pred_y_set, last_set, 0, 0)
    
############################### 모듈 실행 시 한 번만 불러올 list/scaler/model 선언 ###############################
# 불러올 태그 리스트
tags = (
    '771-358-CUI-6004', # 금산(정) 원수 전기전도도
    '771-358-FRI-2011', # 금산(정) 원수 유입 유량(순시)
    '771-358-PHI-6003', # 금산(정) 원수 pH
    '771-358-TBI-6001', # 금산(정) 원수 탁도
    '771-358-TEI-6028', # 금산광역(정) 원수 온도
    '771-358-ALI-6001', # 금산(정) 원수 알카리도
    '771-358-TBI-6002', # 금산(정) 막여과 유입수 탁도
    '771-358-TBI-6011', # 금산(정) 여과수 통합 탁도
    '771-358-CFB-2462', # 금산광역(정) PAC 주입기#1 RUN
    '771-358-CFB-2472', # 금산광역(정) PAC 주입기#2 RUN
    '771-358-CFC-2464', # 금산광역(정) PAC주입기#1 현재주입률설정
    '771-358-CFC-2474', # 금산광역(정) PAC주입기#2 현재주입률설정
)

# 태그 리스트 이름 dictionary
col_names = {
    '771-358-TBI-6001': '원수 탁도',
    '771-358-PHI-6003': '원수 pH',
    '771-358-TEI-6028': '원수 수온',
    '771-358-CUI-6004': '원수 전기전도도',
    '771-358-ALI-6001': '원수 알칼리도',
    '771-358-TBI-6002': '막여과 유입수 탁도',
    '771-358-TBI-6011': '여과수 통합 탁도',
    '771-358-CFC-2464': 'set1',
    '771-358-CFC-2474': 'set2',
    '771-358-CFB-2462': '투입기#1 F/C',
    '771-358-CFB-2472': '투입기#2 F/C'
}

# F/C 태그 리스트
fc_columns = [
    '투입기#1 F/C',
    '투입기#2 F/C'
]

# set 태그 리스트
set_columns = [
    'set1',  
    'set2',
]

# 태그별 정상 계측 범위 상/하한값
bounds_tbi = {'min': 0, 'max': 200}
bounds_phi = {'min': 6, 'max': 9}
bounds_tei = {'min': 0, 'max': 35}
bounds_cui = {'min': 0, 'max': 200} 
bounds_ali = {'min': 0, 'max': 250}
bounds_tbi_p = {'min': 0, 'max': 20}

# 데이터 스무딩 파라미터
savgol_win = 120
savgol_poly = 3

smooth_columns = [
    '원수 pH', 
    '원수 수온', 
    '원수 전기전도도', 
    '원수 알칼리도'
]

# 주입률 예측에 사용할 데이터 리스트
pred_c_columns_1 = [
    '원수 탁도',
    '원수 pH',
    '원수 수온',
    '원수 전기전도도',
    '원수 알칼리도',
    '막여과 유입수 탁도'
]

pred_c_columns_2 = [
    'apac'
]

# scaler 설정
minmax_dic_1 = {
    '원수 탁도_min': 0.059375,
    '원수 탁도_max': 90.05588541666665,
    '원수 pH_min': 6.232225676437579,
    '원수 pH_max': 8.552695526147826,
    '원수 수온_min': 3.849385044642724,
    '원수 수온_max': 27.747175641740114,
    '원수 전기전도도_min': 52.17356305803391,
    '원수 전기전도도_max': 294.8066662746245,
    '원수 알칼리도_min': 1.4176690493814796,
    '원수 알칼리도_max': 52.84402545089104,
    '막여과 유입수 탁도_min': 0.303125,
    '막여과 유입수 탁도_max': 62.88781097435898
}

minmax_dic_2 = {
    'x2_min': 2.00000,
    'x2_max': 15.00000
}

# dataset 형태 설정
input_hours = 144
n_features_1 = 10
n_features_2 = 1

# load model
co_model = keras.models.load_model(MODEL_PATH + 'GS_C_model_0403')

# json 형태로 저장하기 위한 key list(입력변수)
dict_key = [
    'C_TB', 
    'C_CU', 
    'C_AL', 
    'C_PH', 
    'C_TE',   
    'C_TB_E',
    'C_TB_F',
    'C_CF_1',
    'C_CF_2'
]

# 목표 침전수 탁도값 설정 - 임시 코드
warning_tbi_p_value = 100.0

############################### 예측 실행 ###############################
@log_perform
def perform(db, job_datetime=None):
    """
    응집제 주입률 예측 실행
    """
    print('---------------- 공정 실행 ----------------') 
    
    if job_datetime is None:
        c_rt_df = db.read_rt_subday_max('TB_C_RT', tags)
    else:
        c_rt_df = db.read_rt_subday_etime('TB_C_RT', tags, job_datetime)
    if len(c_rt_df) <= 0:
        return

    current_dt_date = c_rt_df.index[-1]
    ai_opr, co_max, co_min, co_user = get_init(db) # INIT 가져오기
    
    ### 데이터 전처리 진행 ###
    # 컬럼명 변경
    c_rt_df.rename(columns = col_names, inplace = True)

    # 약품 종류 컬럼 추가 - 임시 코드 (RT 테이블 수집 확인 후 제외)
    c_rt_df['apac'] = 0

    ################################# 데이터 전처리 #################################
    # 중복 index 제거
    df_filtered = drop_duplicate_rows(c_rt_df)

    # 결측치 제거
    result = check_drop_missing(df_filtered)
    df_filtered = df_filtered.drop(index = result[result == True].index)

    # 시간 index 설정
    time_df = pd.DataFrame(index = pd.date_range(start = current_dt_date - timedelta(days = 7), end = current_dt_date, freq = '1min'))
    df_filtered = df_filtered.merge(time_df, how = 'right', left_index = True, right_index = True)
    df_filtered = df_filtered.fillna(method = 'ffill')
    df_filtered = df_filtered.fillna(method = 'bfill')

    # 응집제 주입률 설정값 컬럼 생성
    df_filtered['응집제주입률설정'] = df_filtered.apply(lambda x: x['set2'] if x['투입기#1 F/C'] == 0 else x['set1'], axis = 1)

    # 응집제 주입률 설정값 수정 (5 미만 직전 또는 직후값 대체)
    df_filtered['응집제주입률설정'] = df_filtered['응집제주입률설정'].apply(lambda x: x if x >= 5 else None)
    df_filtered['응집제주입률설정'] = df_filtered['응집제주입률설정'].fillna(method = 'ffill')
    df_filtered['응집제주입률설정'] = df_filtered['응집제주입률설정'].fillna(method = 'bfill')

    # 태그 데이터 정상 계측 범위 상/하한 값 적용
    df_filtered = upper_lower_bounds(col = '원수 탁도', upper = bounds_tbi['max'], lower = bounds_tbi['min'], dataframe = df_filtered)
    df_filtered = upper_lower_bounds(col = '원수 pH', upper = bounds_phi['max'], lower = bounds_phi['min'], dataframe = df_filtered)
    df_filtered = upper_lower_bounds(col = '원수 수온', upper = bounds_tei['max'], lower = bounds_tei['min'], dataframe = df_filtered)
    df_filtered = upper_lower_bounds(col = '원수 전기전도도', upper = bounds_cui['max'], lower = bounds_cui['min'], dataframe = df_filtered)   
    df_filtered = upper_lower_bounds(col = '원수 알칼리도', upper = bounds_ali['max'], lower = bounds_ali['min'], dataframe = df_filtered)   
    df_filtered = upper_lower_bounds(col = '막여과 유입수 탁도', upper = bounds_tbi_p['max'], lower = bounds_tbi_p['min'], dataframe = df_filtered) 
    
    # F/C 태그 분리
    fc_df = df_filtered[fc_columns]
    df_filtered = df_filtered.drop(fc_columns, axis = 1)

    # F/C 태그를 사용하여 RUN 태그 생성 
    fc_df['run1'] = fc_df['투입기#1 F/C'].apply(lambda x: 1 if x == 1 else 0)
    fc_df['run2'] = fc_df['투입기#2 F/C'].apply(lambda x: 1 if x == 1 else 0)

    # set 태그 분리
    set_df = df_filtered[set_columns]
    set_df['set'] = df_filtered['응집제주입률설정']
    df_filtered = df_filtered.drop(set_columns, axis = 1)

    # F/C df와 set df 합치기
    set_df = pd.merge(fc_df, set_df, how = 'inner', left_index = True, right_index = True)
    
    set_df['inval_set1'] = set_df.apply(lambda x: x['set1'] if x['run1'] == 1 else 0, axis = 1)
    set_df['inval_set2'] = set_df.apply(lambda x: x['set2'] if x['run2'] == 1 else 0, axis = 1)

    # savgol_filter
    for col in smooth_columns:
        # 데이터 스무딩
        tmp_smooth = savgol_filter(df_filtered[col], savgol_win, savgol_poly)
        df_filtered[col] = tmp_smooth
        # 마이너스값 직전/직후값 대체
        if df_filtered[col].min() <= 0:
            df_filtered[col] = df_filtered[col].apply(lambda x: x if x > 0 else None)
            df_filtered[col] = df_filtered[col].fillna(method='ffill')
            df_filtered[col] = df_filtered[col].fillna(method='bfill')

    # 10분 resample
    modeling_df_10min = df_filtered[pred_c_columns_1].resample('10min', origin = 'end').mean()
    add_df_10min = df_filtered[pred_c_columns_2].resample('10min', origin='end').apply(mode)
    modeling_df_10min = pd.merge(modeling_df_10min, add_df_10min, how = 'left', left_index = True, right_index = True)

    # shift_set 
    df_shift = df_filtered[['응집제주입률설정']]
    df_shift['shift_set'] = df_shift['응집제주입률설정'].shift(1)
    modeling_df_10min = pd.merge(modeling_df_10min, df_shift[['shift_set']], how = 'left', left_index = True, right_index = True)
    
    # 가장 최근 144개(하루 동안) 데이터 사용
    use_modeling_df_10min = modeling_df_10min.tail(144)

    use_modeling_df_10min = use_modeling_df_10min.fillna(method = 'ffill')
    use_modeling_df_10min = use_modeling_df_10min.fillna(method = 'bfill')

    ### 모델 예측 
    # 데이터셋 변환
    simu_model_df = series_to_supervised_simu(use_modeling_df_10min, input_hours)

    # data 나누기
    simu_model_df_1 = simu_model_df.filter(regex = '^(?!.*shift_set.*\(t-)', axis = 1)
    simu_model_df_2 = simu_model_df[['shift_set(t-1)']]

    # scaling
    for col in simu_model_df_1.columns.tolist():
        string = col.split('(')[0]
        
        if string in ['apac']:
            continue
            
        simu_model_df_1[col] = (simu_model_df_1[col] - minmax_dic_1[f'{string}_min']) / (minmax_dic_1[f'{string}_max'] - minmax_dic_1[f'{string}_min'])

    scale_simu1 = np.array(simu_model_df_1)
    scale_simu1 = scale_simu1.reshape((simu_model_df_1.shape[0], input_hours, len(modeling_df_10min.columns) - 1))

    simu_model_df_2 = (simu_model_df_2 - minmax_dic_2['x2_min']) / (minmax_dic_2['x2_max'] - minmax_dic_2['x2_min'])
    
    scale_simu2 = np.array(simu_model_df_2)
    scale_simu2 = scale_simu2.reshape((scale_simu2.shape[0], 1, 1))

    # model predict
    ai_pred_y = co_model.predict([scale_simu1, scale_simu2])

    if ai_pred_y.item() is np.nan:
        ai_pred_y = np.array(float(df_filtered.loc[df_filtered.index[-1],'응집제주입률설정']))

    # 결과값 정수처리
    ai_pred_y_set = round(ai_pred_y.item())
    last_set = round(df_filtered.loc[df_filtered.index[-1],'응집제주입률설정'])

    # 30분 평균 탁도 15NTU이하 시 10ppm / 15~25NTU일 때 12~14ppm / 25NTU 이상 시 15ppm 주입
    tbi = float(df_filtered[['원수 탁도']].tail(30).mean())
    if (tbi > 15) & (tbi <= 20):
        ai_pred_y_set = 12
    elif (tbi > 20) & (tbi <= 25):
        ai_pred_y_set = 14
    elif (tbi > 25):
        ai_pred_y_set = 15

    # 사용자 보정값 적용
    ai_pred_y_set_final = ai_pred_y_set + co_user
    
    K.clear_session()

    # test
    # ai_opr = 2
    # ai_pred_y_set = 10
    # last_set = 15
    
    ############################### 결과 DB 적재 ###############################
    # 입/출력 변수 Dictionary 형태로 변환
    # 입력 변수
    in_val = dict()
    in_val = {
        dict_key[0] : float(df_filtered.loc[df_filtered.index[-1],'원수 탁도']),
        dict_key[1] : float(df_filtered.loc[df_filtered.index[-1],'원수 전기전도도']),
        dict_key[2] : float(df_filtered.loc[df_filtered.index[-1],'원수 알칼리도']),
        dict_key[3] : float(df_filtered.loc[df_filtered.index[-1],'원수 pH']),
        dict_key[4] : float(df_filtered.loc[df_filtered.index[-1],'원수 수온']),
        dict_key[5] : float(df_filtered.loc[df_filtered.index[-1],'막여과 유입수 탁도']),
        dict_key[6] : float(df_filtered.loc[df_filtered.index[-1],'여과수 통합 탁도']),
        dict_key[7] : float(set_df.loc[set_df.index[-1],'inval_set1']),
        dict_key[8] : float(set_df.loc[set_df.index[-1],'inval_set2']),
        'CO_MIN' : float(co_min),
        'CO_MAX' : float(co_max),
        'CO_USER' : float(co_user)
    }
    
    for key in dict_key:
        in_val[key] = float(0) if in_val[key] is np.nan else in_val[key] 
        in_val[key] = float(0) if in_val[key] < 0 else in_val[key]

    # 출력 변수
    out_val = dict()
    out_val = {
        'AI_C_CF' : float(ai_pred_y_set_final),
        'C_TB' : float(df_filtered.loc[df_filtered.index[-1],'원수 탁도']),
        'C_INJECTOR1' : float(set_df.loc[set_df.index[-1],'run1']),
        'C_INJECTOR2' : float(set_df.loc[set_df.index[-1],'run2']),
        'AI_C_CF_NR' : float(ai_pred_y.item()),
        'AI_C_CF_NORM_CO' : float(ai_pred_y_set) 
    }

    for key in ['C_TB', 'C_INJECTOR1', 'C_INJECTOR2']:
        out_val[key] = float(0) if out_val[key] is np.nan else out_val[key] 
        out_val[key] = float(0) if out_val[key] < 0 else out_val[key]

    # 주요 인자
    factor_val = dict()
    factor_val = {
        'b_tb' : float(df_filtered.loc[df_filtered.index[-1],'원수 탁도']),
        'b_ph' : float(df_filtered.loc[df_filtered.index[-1],'원수 pH']),
        'b_te' : float(df_filtered.loc[df_filtered.index[-1],'원수 수온']),
        'b_cu' : float(df_filtered.loc[df_filtered.index[-1],'원수 전기전도도'])
    }
    
    in_val = [in_val]
    out_val = [out_val]
    factor_val = [factor_val]

    # 입/출력 변수 Dictionary to json
    in_val_json = json.dumps(in_val)
    out_val_json = json.dumps(out_val)
    factor_val_json = json.dumps(factor_val)

    ############################### 알람/제어 코드 ###############################
    run_time = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

    df_factor = pd.DataFrame(columns = ['proc_cd', 'disinfection_index', 'rnti', 'factor'], index = [current_dt_date])
    df_factor['proc_cd'] = 'C'
    df_factor['disinfection_index'] = 'NONE'
    df_factor['rnti'] = run_time
    df_factor['factor'] = factor_val_json 
    
    run_time = f'"{run_time}"'

    # 제어 코드 (반자동 모드)
    if ai_opr == 1:
        # 이전값과 1ppm 이상 차이나면 제어 테이블 insert 진행
        if abs(ai_pred_y_set_final - last_set) >= 1:
            # 임계치 범위를 벗어나는 경우 4번 타입 알람 발생
            if (ai_pred_y_set_final > co_max) or (ai_pred_y_set_final < co_min):
                db.save_alm('TB_AI_C_ALM', 132006, run_time)
                print('---------------- 응집제 주입률 임계치 알람 발생 ----------------')
            # 응집제 주입률 제어
            if set_df['run1'].loc[current_dt_date] == 1: # 1호기 가동 시
                control_tag = '771-358-CFC-2464'
                ctr_set(db, run_time, control_tag, ai_pred_y_set_final, last_set)
                db.save_ai_factor(df_factor)
            elif set_df['run2'].loc[current_dt_date] == 1: # 2호기 가동 시
                control_tag = '771-358-CFC-2474'
                ctr_set(db, run_time, control_tag, ai_pred_y_set_final, last_set)
                db.save_ai_factor(df_factor)
            print('---------------- 제어테이블 적재 완료 ----------------')

    # 제어 코드 (자동 모드)
    if ai_opr == 2:
        # 이전값과 1ppm 이상 차이나면 제어 테이블 insert 진행
        if abs(ai_pred_y_set_final - last_set) >= 1:
            # 임계치 범위 내의 경우 응집제 주입률 변경 알람
            if (ai_pred_y_set_final <= co_max) and (ai_pred_y_set_final >= co_min):
                db.save_alm('TB_AI_C_ALM', 132004, run_time)
                print('---------------- 응집제 주입률 변경 알람 발생 ----------------')
            # 임계치 범위를 벗어나는 경우 4번 타입 알람 발생
            elif (ai_pred_y_set_final > co_max) or (ai_pred_y_set_final < co_min):
                db.save_alm('TB_AI_C_ALM', 132006, run_time)
                print('---------------- 응집제 주입률 임계치 알람 발생 ----------------')
            # 응집제 주입률 제어
            if set_df['run1'].loc[current_dt_date] == 1: # 1호기 가동 시
                control_tag = '771-358-CFC-2464'
                ctr_set(db, run_time, control_tag, ai_pred_y_set_final, last_set)
                db.save_ai_factor(df_factor)
            elif set_df['run2'].loc[current_dt_date] == 1: # 2호기 가동 시
                control_tag = '771-358-CFC-2474'
                ctr_set(db, run_time, control_tag, ai_pred_y_set_final, last_set)
                db.save_ai_factor(df_factor)
            print('---------------- 제어테이블 적재 완료 ----------------')

    # # 목표 침전수 탁도 초과 알람
    # if ai_opr != 0: # 반자동/자동모드
    #     if warning_tbi_p_value <= float(df_filtered.loc[df_filtered.index[-1],'막여과 유입수 탁도']):
    #         db.save_alm('TB_AI_C_ALM', 132005, run_time) 
    #         print('---------------- 목표 막여과 유입수 탁도 초과 알람 발생 ----------------')
            
    ############################### 예측 결과 저장 ###############################        
    # 결과 테이블에 저장
    # 약품 공정 결과 테이블 형태의 DataFrame 생성
    df_final = pd.DataFrame(columns = ['upd_ti', 'AI_OPR', 'IN_VAL', 'OUT_VAL'], index = [current_dt_date])
    df_final['upd_ti'] = datetime.now()
    df_final['AI_OPR'] = ai_opr
    df_final['IN_VAL'] = in_val_json
    df_final['OUT_VAL'] = out_val_json   
          
    # 결과 테이블에 저장
    db.save_ai_rt('TB_AI_C_RT', df_final)

    print('---------------- 공정 실행 완료 (1 cycle) ----------------')
