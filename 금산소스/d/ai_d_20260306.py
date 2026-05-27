# 파일명: ai_d.py
# 최종수정일: 2024.11.11

#공통 부분
import sys
import os
import re
import json
import time 
from collections import OrderedDict

#Path 설정
if '__file__' in globals() and os.path.isfile(os.path.abspath(__file__)):
    PROC_PATH = os.path.dirname(os.path.abspath(__file__))
else:
    PROC_PATH = os.path.dirname(os.path.abspath('_'))
PROC_NAME = os.path.basename(PROC_PATH)
BASE_PATH = os.path.dirname(PROC_PATH)
ROOT_PATH = os.path.dirname(BASE_PATH)
sys.path.append(BASE_PATH + '/common')
sys.path.append(ROOT_PATH + '/common')

#DB 성정
from db_util import DBUtil
from config import Config
#분석 처리 공통 함수
from analysis import *

#전처리 관련 라이브러리
from scipy.signal import savgol_filter
from functools import reduce
import datetime
from datetime import datetime,timedelta

#분석 로그
import traceback
import logging
from logging import handlers

#전송 로거 생성
from aos_util import *
build_logger(ROOT_PATH, BASE_PATH, PROC_NAME)

LOGS_PATH = BASE_PATH + '/logs/'

#log settings
runLogFormatter = logging.Formatter('%(asctime)s : %(message)s')

logfile = LOGS_PATH + PROC_NAME + '.log'
runLogHandler = handlers.TimedRotatingFileHandler(filename=logfile, when='midnight', interval=1,
                                                  encoding='utf-8')
runLogHandler.setFormatter(runLogFormatter)
runLogHandler.suffix = "%Y%m%d"

#logger set
runLogger = logging.getLogger()
runLogger.setLevel(logging.ERROR)
runLogger.addHandler(runLogHandler)

#RPM 산출식 라이브러리 호출
from rpm_formula import * 

#수집 데이터 결측시 결과 적재
def empty_save_ai_rt(tb_ai_d_rt) : 
    ''' 
    Desc 
        필요한 태그 데이터가 없거나 데이터 수집이 되지 않은 경우, 이전 분석 결과로 적재 (현재 시간 기준)
    Args 
        tb_ai_d_rt : str
            적재할 결과 테이블 명 
    ''' 
    sql = f'''
        SELECT UPD_TI, AI_OPR, IN_VAL, OUT_VAL
        FROM {tb_ai_d_rt}
        ORDER BY UPD_TI DESC LIMIT 1
       '''
    df_last = db.read(sql)
    
    df_mixing_ai_result = pd.DataFrame(columns = ['upd_ti', 'AI_OPR', 'IN_VAL', 'OUT_VAL'], index=[0])    
    df_mixing_ai_result['upd_ti'] = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    df_mixing_ai_result['AI_OPR'] = df_last['AI_OPR']
    df_mixing_ai_result['IN_VAL'] = df_last['IN_VAL']
    df_mixing_ai_result['OUT_VAL'] = df_last['OUT_VAL']
    db.save_ai_rt(tb_ai_d_rt, df_mixing_ai_result)
    print("Upload last data to database")
    return    

#데이터 전처리 함수(상/하한 값 대체)
def upper_lower_bounds(col,upper,lower,dataframe) :
    '''
    Desc
        정수장에서 확보한 HH/LL 알람 기준 활용하여 태그 데이터 전처리 
    Args
        col : str
            전처리 할 컬럼 명 
        upper : float 
            이상치 상한 
        lower : float 
            이상치 하한 
    Return 
        dataframe : df
            이상치 대체 후 결과 데이터프레임
    '''
    if len(dataframe[col].unique()) == 1 : 
        pass
    else : 
        idx = dataframe[(dataframe[col]>=upper)|(dataframe[col]<=lower)].index
        dataframe.loc[dataframe.index.isin(idx),col] = np.nan
        
        if dataframe.index[0] in idx:
            dataframe.loc[dataframe.index[0], col] = dataframe[col].mean()
        else : 
            try: 
                dataframe[col].fillna(method='ffill',axis=0,inplace=True).fillna(method='bfill',axis=0,inplace=True)
            except :
                dataframe[col].fillna(dataframe[col].mean(),axis=0,inplace=True)
    return dataframe

#데이터 전처리 함수 (이상치 제거,평활화)
def outlier_preprocessing (d_rt_df_outlier,col,window_size,n,outlier_z_score=outlier_z_score,replace_outliers_with_mean=replace_outliers_with_mean,outlier_for_timeseries=outlier_for_timeseries) :
    ''' 
    Desc 
        혼화응집 태그 데이터 이상치 처리 (Z-score / STL 기준)
    Args
        d_rt_df_outlier : df
            상/하한 전처리 이후 데이터 프레임 
            
        col : str
            전처리 대상 컬럼명 
        
        window_size : int
            savgol_filter 함수 인자 (WindowSize)
            
        n : int
            savgol_filter 함수 인자 (Polyorder)
            
        outlier_z_score : func
            Z-score 기준 이상치 처리 함수
            
        replace_outliers_with_mean : func 
            이상치 평균 값 대체 함수
            
        outlier_for_timeseries : func 
            STL 기준 이상치 처리 함수 
    ''' 
    if len(d_rt_df_outlier[col].unique()) == 1 : 
        outlier_replace_n = pd.DataFrame(d_rt_df_outlier[col],columns=[col]).set_index(d_rt_df_outlier.index)
        return outlier_replace_n.copy()
        
    elif col == cfi_tot_col : 
            if window_size == 3 : 
                smooth_series = savgol_filter(d_rt_df_outlier[col], window_size,2)
            elif window_size == 2 : 
                smooth_series = savgol_filter(d_rt_df_outlier[col], window_size,1)
            else : 
                smooth_series = savgol_filter(d_rt_df_outlier[col],window_size,3)
                
            outlier_replace_df = pd.DataFrame(smooth_series,columns=[col]).set_index(d_rt_df_outlier.index)               
        
            if len(d_rt_df_outlier[col].unique()) == 1 :
                return d_rt_df_outlier.copy()
            else :
                return outlier_replace_df.copy()  
                
    else : 
            z_thresh = 3 
            outlier_series, z_score_series = outlier_z_score(d_rt_df_outlier[col],thresh=z_thresh)
            outlier_idx = outlier_series[outlier_series==True].index
            
            Series_dropna =  d_rt_df_outlier[col].squeeze()
            
            outlier_replace_df  = replace_outliers_with_mean(data=Series_dropna,indices=outlier_idx,n=n)
            
            outlier_replace_df = pd.DataFrame(outlier_replace_df,columns=[col])
            outlier_replace_df.fillna(outlier_replace_df.mean(),inplace=True)
    
            Series_dropna_2 =  outlier_replace_df[col].squeeze()
            
            is_outlier, _ = outlier_for_timeseries(Series_dropna_2)
            outlier_replace_df_2 = replace_outliers_with_mean(Series_dropna_2, Series_dropna_2[is_outlier].index, n=n)
            outlier_replace_df_2 = pd.DataFrame(outlier_replace_df_2,columns=[col])
            outlier_replace_df_2.fillna(outlier_replace_df_2.mean(),inplace=True)
    
            if window_size == 3 : 
                smooth_series = savgol_filter(outlier_replace_df_2[col], window_size,2)
            elif window_size == 2 : 
                smooth_series = savgol_filter(outlier_replace_df_2[col], window_size,1)
            else : 
                smooth_series = savgol_filter(outlier_replace_df_2[col], window_size,3)
                
            outlier_replace_df_3 = pd.DataFrame(smooth_series,columns=[col]).set_index(d_rt_df_outlier.index)               
        
            if len(outlier_replace_df[col].unique()) == 1 :
                return outlier_replace_df.copy()
            elif len(outlier_replace_df_2[col].unique()) == 1 :
                return outlier_replace_df_2.copy()
            else :
                return outlier_replace_df_3.copy()


#컬럼별 이상치 처리
def preprocess_columns(d_rt_df_outlier, window_size=10081, n=120,outlier_preprocessing=outlier_preprocessing):
    '''
    Desc
        각 컬럼별 이상치 처리
    Args
        d_rt_df_outlier : df
            전처리 이전 데이터 프레임 
        
        window_size : int
            savgol_filter 함수 인자 (WindowSize)

        n : int
            평균 값 대체 함수 인자 (최근 n분전 값)
            
        outlier_preprocessing : func 
            전처리 함수 
    Return 
        preprocessed_dfs : df
            각 컬럼별로 전처리가 완료된 데이터 프레임
    
    '''
    preprocessed_dfs = {}
    for col in d_rt_df_outlier.columns:
        try: 
            preprocessed_dfs[col] = outlier_preprocessing(d_rt_df_outlier=d_rt_df_outlier, col=col, window_size=window_size, n=n)
        except: 
            preprocessed_dfs[col] = outlier_preprocessing(d_rt_df_outlier=d_rt_df_outlier, col=col, window_size=len(d_rt_df_outlier), n=len(d_rt_df_outlier))  
    return preprocessed_dfs

    
#하나의 데이터 프레임으로 JOIN
def join_preprocessed_dfs(preprocessed_dfs):
    '''
    Desc
        하나의 데이터 프레임으로 조인
    Args
        preprocessed_dfs : df
            각 컬럼별로 전처리가 완료된 데이터 프레임 
    Return 
        d_pre_df : df
            전처리 완료된 데이터 프레임 (전체 컬럼)
    '''
    
    d_pre_df = pd.concat(preprocessed_dfs.values(), axis=1)
    return d_pre_df

#이상치 처리를 위한 각 함수들을 호출하여 전체 데이터프레임을 전처리하는 함수
def preprocess_dataframe(d_rt_df_outlier, window_size=10081, n=120):
    '''
    Desc
        하나의 데이터 프레임 생성 함수 (컬럼별 전처리 및 컬럼별 데이터 프레임 조인)
    Args
        d_rt_df_outlier : df
            전처리 이전 데이터 프레임 
    Return 
        join_preprocessed_dfs(preprocessed_dfs) : df
            전처리 완료된 데이터 프레임 (전체 컬럼)
    '''
    preprocessed_dfs = preprocess_columns(d_rt_df_outlier, window_size, n)
    return join_preprocessed_dfs(preprocessed_dfs)

                
#전처리 데이터 시간 설정 함수
def set_current_dt(current_dt):
    '''
    Desc
        현재 시간을 설정한다. 
        데이터 로딩 처리에 걸리는 시간을 고려하여 *0분에 시작이 아닌 *1분 59초에 *0분에 해당하는 Ai 결과를 출력한다.
        따라서 분단위 절삭처리를 진행한다.
    Args
        current_dt : 지정 날짜
    Return 
        current_dt : str
            지정 날짜가 있는 경우 해당 시점 
            지정 날짜가 없는 경우 현재 시간 기준
            
        current_dt_ts : str
            현재 시간 기준 분 단위 절삭 시간
            
        prev_3h : str
            기준 날짜 시점 3시간 전 
    
    '''
    if current_dt != '': # 지정된 날짜가 있는 경우 해당 시점 사용

        if str(type(current_dt)).find('str') >= 0: 
            current_dt_ts = datetime.strptime(current_dt, '%Y-%m-%d %H:%M:%S')
        else : 
            current_dt_ts = current_dt
        
    else:
        current_dt_ts = datetime.now()
        print('1. current_dt_ts |', current_dt_ts)
        current_dt_ts = datetime(current_dt_ts.year, 
                                    current_dt_ts.month, 
                                    current_dt_ts.day, 
                                    current_dt_ts.hour, 
                                    10*(current_dt_ts.minute//10)) # 현재 시점 에서 분단위 절삭 timestamp
        current_dt = current_dt_ts.strftime('%Y-%m-%d %H:%M:%S') # timestamp 문자열 변경
        print('2. current_dt    |', current_dt)
    try : 
        prev_3h_ts = current_dt_ts - timedelta(minutes=180)
    except : 
        prev_3h_ts = current_dt_ts
    prev_3h = prev_3h_ts.strftime('%Y-%m-%d %H:%M:%S')
    
    return current_dt,current_dt_ts,prev_3h


#수온 데이터 전처리 함수
def prepare_data(df,current_dt,tei_col):
    '''
    Desc
        원수 수온 데이터 전처리
        수온에 따른 점도/밀도 표 내 값을 활용하기 위한 전처리  
    Args
        df : df 
            전처리 후 태그 데이터 프레임 
        current_dt : str
            기준 시간대 
        tei_col : str
            원수 수온 태그명 
    Return 
        df_current : df
            결과 데이터 프레임 
    '''
    
    #원수 수온 데이터만 추출
    df_te = df[[tei_col]]
    
    #상하한 값 설정
    df_te_under = df_te[df_te[tei_col]<=0]
    df_te_over = df_te[df_te[tei_col]>=30]
    
    #상하한 초과 값 대체
    try : 
        df_te = df_te.drop(df_te_under.index).resample("1T").asfreq()
        df_te = df_te.drop(df_te_over.index).resample("1T").asfreq()
        df_te = df_te.fillna(method='ffill')
        df_te = df_te.fillna(method='bfill')
    except : 
        df_te = df_te.fillna(df_te.mean())
    
    return df_te[tei_col]

def get_v(df, te):
    '''
    Desc
        원수 수온에 따른 점성계수 및 밀도 값을 반환하는 함수
    Args
        df : 점성 계수 및 밀도 값이 저장된 리스트
        te : 전처리된 원수 수온 ex) 13.34062
    Return 
        입력된 원수 수온에 맞는 점성 계수 및 밀도값 반환
    '''
    if te < 0:
        te = 0
    if te > len(df) - 1:
        te = len(df) - 1
    te_index = int(te)
    return df[te_index]

#init 테이블 값 호출 함수
def d_init(db,tb_ai_init):
    '''
    Desc 
        init 테이블 내 분석 필요 변수 할당 
        =============================================
        d_operation_mode : AI 운영모드 (0: 수동운전, 1: 반자동운전, 2: AI운전(자동운전))
        d_g_value_ctr_flag : 혼화응집 공정 모드 (0: G값 수동모드 /1: G값 자동모드) 
        d_g_value_step : 열별 G값
        d_g_step1_crt  : (혼화응집 자동모드) 열별 보정계수
        d_g_step_min   : 열별 G값 하한 
        d_g_step_max   : 열별 G값 상한 
        =============================================
        
    Args 
        tb_ai_init : str 
            ai init 테이블 명 

    Return 
        d_operation_mode : str
            AI 운영모드 (0: 수동운전, 1: 반자동운전, 2: AI운전(자동운전))
        d_g_value_ctr_flag : int
            혼화응집 공정 모드 (0: G값 수동모드 /1: G값 자동모드) 
        d_g_valu_step : list
            (혼화응집 수동모드) 열별 G값 
        d_g_step_crt : list
            (혼화응집 자동모드) 열별 보정계수
        d_g_step_min : list
            열별 G값 하한 
        d_g_step_max : list
            열별 G값 상한 
    '''
    #각 항목 값을 담을 리스트 생성
    d_g_value_step,d_g_step_crt,d_g_step_min,d_g_step_max = [],[],[],[]
    
    #TB_AI_D_INIT 테이블 내 초기값 호출을 위한 쿼리
    init_sql = f''' select *
                    from {tb_ai_init} ''' 

    #INIT 테이블 LOAD
    init_tbl = db.read(init_sql)

    #INIT 테이블 내 AI 운영 모드 호출
    d_operation_mode = int(init_tbl[init_tbl['ITM']=='d_operation_mode']['INIT_VAL'].item())     # operation_mode(0: 수동운전, 1: 반자동운전, 2: AI운전)

    #INIT 테이블 내 초기 값 호출
    d_g_value_ctr_flag = int(init_tbl[init_tbl['ITM']=='d_g_value_ctr_flag']['INIT_VAL'].item()) # ctr_flag(0 : G값 수동모드, 1: G값 자동 모드)        

    #열별 수동모드
    d_g_value_step1 = float(init_tbl[init_tbl['ITM']=='d_g_value_loc1']['INIT_VAL'].item()) # 열별 수동모드 G값 
    d_g_value_step2 = float(init_tbl[init_tbl['ITM']=='d_g_value_loc2']['INIT_VAL'].item()) # 열별 수동모드 G값 

    #열별 보정계수
    d_g_step1_crt = float(init_tbl[init_tbl['ITM']=='d_g_step1_crt']['INIT_VAL'].item()) # 지별 보정계수 적용 
    d_g_step2_crt = float(init_tbl[init_tbl['ITM']=='d_g_step2_crt']['INIT_VAL'].item()) # 지별 보정계수 적용 
    
    #열별 G값 상/하한
    d_g_step1_min = float(init_tbl[init_tbl['ITM']=='d_g_step1_min']['INIT_VAL'].item()) # 지별 G값 상/하한 적용 
    d_g_step1_max = float(init_tbl[init_tbl['ITM']=='d_g_step1_max']['INIT_VAL'].item()) # 지별 G값 상/하한 적용  
    
    d_g_step2_min = float(init_tbl[init_tbl['ITM']=='d_g_step2_min']['INIT_VAL'].item()) # 지별 G값 상/하한 적용 
    d_g_step2_max = float(init_tbl[init_tbl['ITM']=='d_g_step2_max']['INIT_VAL'].item()) # 지별 G값 상/하한 적용  
    
    #G값 결과 list 저장
    d_g_value_step.extend([round(d_g_value_step1,1),round(d_g_value_step2,1)])
            
    #보정계수 결과 list 저장
    d_g_step_crt.extend([d_g_step1_crt,d_g_step2_crt])
            
    #G값 하한 list 저장
    d_g_step_min.extend([d_g_step1_min,d_g_step2_min])

    #G값 상한 list 저장
    d_g_step_max.extend([d_g_step1_max,d_g_step2_max])
    
    return d_operation_mode,d_g_value_ctr_flag,d_g_value_step,d_g_step_crt,d_g_step_min,d_g_step_max
    
#(시각화) G값 상하한 처리
def d_g_min_max (g_value,min,max) :
    '''
    Desc
        (INIT) 조회한 열볇 G값 상/하한 기준을 초과하는 경우, 해당 값으로 대체 
    Args
        g_value : float
            열별 G값 
        min : float
            열별 G값 하한
        max : float
            열별 G값 상한
    Return 
        g_value : float 
            상/하한 값 대체 후 열별 G값 
    '''
    
    #상/하한 반대로 입력된 경우에는 적용하지 않음
    if max >= min : 
        if g_value < min :
            g_value = min    
        elif g_value > max :
            g_value = max 
    return g_value

#결과 데이터 프레임 저장 형태 변환
def rslt_val(val,val_col):
    '''
    Desc
        결과데이터 프레임 JSON 형태로 변환 (IN_VAL/OUT_VAL)
    Args 
        val : df
            결과 데이터프레임 (IN_VAL 또는 OUT_VAL) 
        val_col : list
            띄어쓰기 공백 제거가 필요한 컬럼명  
    Return
        val_json
            JSON 형태로 변환한 결과 데이터 프레임 
    '''
    #NULL 값 방지용 전처리
    val = val.fillna(0)

    #데이터 프레임 내 문자열 포맷 변경
    for col in val_col : 
        try : 
            val[col] = val[col].apply(lambda x : eval(x))
        except : 
            val[col] = 'nan'
            
    #JSON 변환
    val_json = val.to_json(orient='records')
    #불필요한 문자 제거
    val_json = str(val_json).replace("\\", "").replace(" ","")
    
    return val_json

#제어 태그 리스트 호출
def mk_ctr_tag_list (db,tb_tag_mng,ai_cd,proc_cd,itm_tmp): 
    '''
    Desc
        (DB) 제어 태그 정보 데이터 프레임 변환 
    Args
        db : class 
            테이블 조회 공통함수 활용 
             - common.db_util.read
        tb_tag_mng : str
            태그 관리 테이블 명
        ai_cd : str
            공정 코드
        proc_cd : str
            단계 구분 코드
        itm_tmp : str 
            분석 코드에서 활용할 제어 테이블 ITM
    Return 
        ctr_tag_lst : df 
            제어 태그 리스트 데이터 프레임 (TB_TAG_MNG)
    '''
    #TAG_MNG 제어 값 호출
    get_ctr_tag_sql = f''' select *
                    from {tb_tag_mng}
                    where ai_cd = "{ai_cd}"
                    and proc_cd = "{proc_cd}"
                    and tag_ty = 4''' 
    
    ctr_tag_lst = db.read(get_ctr_tag_sql)
    ctr_tag_lst = ctr_tag_lst[ctr_tag_lst['ITM'].str.contains('FCC')]
    ctr_tag_lst.sort_values(['ITM'],ascending=True)  
    ctr_tag_lst.reset_index(drop=True,inplace=True)
    ctr_tag_lst['ITM'] = itm_tmp
        
    return ctr_tag_lst

#ALM TBL 저장 함수
def send_alm(db,table,alm_id,alm_ti) : 
    '''
    Desc
        공통 함수 활용하여 알람테이블 알람 적재 
    Args
        db : class 
            알람 저장 공통함수 활용 
             - common.db_util.save_alm
        table : str
            알람 저장 테이블명 
        alm_id : str
            조회할 알람 ID (ALM_ID)
        alm_ti : str 
            알람 테이블 적재 시간 (ALM_TI)    
    '''
    db.save_alm(table=table,alm_id="'"+str(alm_id)+"'",alm_ti="'"+str(alm_ti)+"'")
    return

    
#(반자동 모드) 알람 팝업 주기 제어 관련 함수
def pre_alm_check(db,tb_ai_d_alm,alm_id):
    '''
    Desc
        최근 일정 시간 내 이상치 알람이 출력된 경우, 알람 표출하지 않음
        ALM TBL에서 이전 ALM_ID 호출하여 조회 
    Args
        tb_ai_d_alm : str
            알람 테이블 명
        alm_id : str
            조회할 알람 ID (ALM_ID)
    Return 
        pre_alm_flag : boolean 
            이전 시간 알람 여부       
    '''
    pre_alm_flag = 0 # 이전 시간 알람 적재 여부  
    hour = 12        # 알람 적재 주기  
    
    sql = f'''
        select * from {tb_ai_d_alm}
        where ALM_TI >= SYSDATE() - INTERVAL {hour} HOUR
        AND ALM_ID = {"'"+str(alm_id)+"'"};
       '''
    #AI RT 테이블 LOAD
    pre_ai_alm = db.read(sql)
    #시간 이내 알람 횟수 측정
    pre_alm_cnt = len(pre_ai_alm)

    #알람이 1건이라도 있으면 True
    if pre_alm_cnt > 0  :
        pre_alm_flag = 1 
    #알람 없는 경우 False
    else : 
        pre_alm_flag = 0
    
    return pre_alm_flag

#알람 자동 일괄 제어 조건 확인
def d_alm_thrshld_check (dict_sp,g_s,ctr_tag_lst,d_alm_thrshld_undr,d_alm_thrshld_over) : 
    '''
    Desc 
        AI 자동 모드 제어시 한 열이라도 임계치를 초과하는 열인 경우, 모든 열의 팝업을 반자동 제어 팝업으로 출력
        
        팝업 혼선을 방지하기 위한 조건 
            - 제어 완료 팝업과 임계치 조정 팝업이 같이 출력되는 경우 제거 
    Args 
        dict_sp : df
            (DB) 최근 결과 출력 행 
        g_s : df 
            (현재 값) g값 제어 태그 값 포함한 데이터 프레임
        d_alm_thrshld_undr : lambda 
            임계치 하한 조건
        d_alm_thrshld_over : lambda 
            임계치 상한 조건
    Return 
        alm_thrshld_yn : bool 
            회전수가 임계치를 초과하는 조건 열 존재 여부 
    '''
    
    undr_lst = [] # 임계치 하한 조건 여부 리스트
    over_lst = [] # 임계치 상한 조건 여부 리스트
    
    #지/열변호 추출
    for loc_id, loc_val in dict_sp.items():
        #지 번호 추출
        loc_no = re.sub(r'[^0-9]+', '', loc_id) 
        
        #지/열변호 기준 제어태그 매핑
        tag_item = 'd_fc_sp_set{}'.format(loc_no)
        control_tag = ctr_tag_lst[ctr_tag_lst['ITM'] == tag_item]['TAG_SN'].values[0]

        #해당 태그명 G값 설정값 매핑
        cmp_val = g_s[tag_item].values[-1]

        d_alm_thrshld_undr_yn = d_alm_thrshld_undr(dict_sp, loc_no, cmp_val) # 임계치 이하 여부
        d_alm_thrshld_over_yn = d_alm_thrshld_over(dict_sp, loc_no, cmp_val) # 임계치 이상 여부

        undr_lst.append(d_alm_thrshld_undr_yn) # 임계치 하한 조건 리스트
        over_lst.append(d_alm_thrshld_over_yn) # 임계치 상한 조건 리스트
            
    #조건 여부 확인 리스트 생성
    cond_lst = undr_lst+over_lst
    
    #한 열이라도 회전수가 임계치를 초과하는 경우 임계치 제어 팝업으로 전환
    if any(cond_lst) == True : 
        alm_thrshld_yn = 1 # 임계치 알람 팝업 
    else : 
        alm_thrshld_yn = 0 # 자동제어 알람 팝업
    return alm_thrshld_yn
    
#CTR TBL 저장 함수
def send_ctr(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val) : 
    '''
    Desc
        공통 함수 활용하여 제어테이블 결과 적재 
    Args
        db : class
            제어 결과 저장 공통함수 활용 
             - common.db_util.save_ctr
        df_mixing_ai_result : df
            혼화응집 실행 결과 데이터프레임  
        sp_no : int 
            열 번호 
        cmp_val : float 
            현재 제어 값 (응집기 회전 수)
        '''
    """    UPD_TI 팝업 분할 용도
    각 열별로 팝업 하나씩 생성"""
    upd_ti_p = str(pd.to_datetime(df_mixing_ai_result['upd_ti'][0])+timedelta(minutes=int(loc_no)-1))
    #제어 테이블 결과 저장 데이터 프레임 생성
    df_location_sp = pd.DataFrame(
        [(
            "'"+upd_ti_p+"'",
            "'"+upd_ti_p+"'",
            "'"+str(control_tag)+"'",
            round(float(dict_sp['location{}'.format(loc_no)]),2),
            round(float(cmp_val),2), 
            0,          
            0            
        )],
        columns=['upd_ti', 'rnti', 'tag_sn', 'tag_val', 'tag_cmp_val', 'kfk_flg','ctr_flg'])
    
    #결과 적재용 리스트 추출
    save_rslt = df_location_sp.values[0]
    #CTR 테이블 저장
    db.save_ctr(tb_ai_d_ctr,save_rslt[0],save_rslt[1],save_rslt[2],save_rslt[3],save_rslt[4],save_rslt[5],save_rslt[6])      
    return   

#(자동제어) CTR TBL 저장 함수
def send_ctr_all(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val) : 
    '''
    Desc
        공통 함수 활용하여 제어테이블 결과 적재 
        자동 모드 제어시 열 관계없이 모든 태그 제어
    Args
        db : class
            제어 결과 저장 공통함수 활용 
             - common.db_util.save_ctr
        df_mixing_ai_result : df
            혼화응집 실행 결과 데이터프레임  
        sp_no : int 
            열 번호 
        cmp_val : float 
            현재 제어 값 (응집기 회전 수)
        '''
    """    UPD_TI 팝업 분할 용도
    각 열별로 팝업 하나씩 생성"""
    upd_ti_p = str(pd.to_datetime(df_mixing_ai_result['upd_ti'][0]))
    #제어 테이블 결과 저장 데이터 프레임 생성
    df_location_sp = pd.DataFrame(
        [(
            "'"+upd_ti_p+"'",
            "'"+upd_ti_p+"'",
            "'"+str(control_tag)+"'",
            round(float(dict_sp['location{}'.format(loc_no)]),2),
            round(float(cmp_val),2), 
            0,          
            0            
        )],
        columns=['upd_ti', 'rnti', 'tag_sn', 'tag_val', 'tag_cmp_val', 'kfk_flg','ctr_flg'])
    
    #결과 적재용 리스트 추출
    save_rslt = df_location_sp.values[0]
    #CTR 테이블 저장
    db.save_ctr(tb_ai_d_ctr,save_rslt[0],save_rslt[1],save_rslt[2],save_rslt[3],save_rslt[4],save_rslt[5],save_rslt[6])      
    return   

# RPM 산출식 Flag
rpm_flag = 3
    
#테이블 매핑
tb_d_rt = 'TB_D_RT'
tb_ai_d_rt = 'TB_AI_D_RT'
tb_ai_init = 'TB_AI_D_INIT'

#제어 태이블 매핑
tb_tag_mng = 'TB_TAG_MNG'
ai_cd = 'D'
proc_cd = 'D'
tb_ai_d_ctr = 'TB_AI_D_CTR'
tb_ai_d_alm = 'TB_AI_D_ALM'

#컬럼 태그 매핑
tei_col = '771-358-TEI-6028'
tei_loc_col1 = '771-358-TEI-2111' 
tei_loc_col2 = '771-358-TEI-2121'

fri_col = '771-358-FRI-2011'
cfi_col_1 = '771-358-CFI-2462'
cfi_col_2 = '771-358-CFI-2472'
cfi_run_1 = '771-358-CFB-2462'
cfi_run_2 = '771-358-CFB-2472'
cfi_run_tot_col = 'cfi_run_tot'
cfi_tot_col = 'cfi_tot'
cfi_loc_col = '771-358-CFC-2101'
lei_loc_col1 = '771-358-LEI-2111'
lei_loc_col2 = '771-358-LEI-2121'
g_s_col1 = '771-358-FCC-2111'
g_s_col2 = '771-358-FCC-2121'

# 5개년 기준 입력변수 평균 값
dic_mean = {
    '771-358-TEI-6028': 14 ,   #원수 수온
    '771-358-TEI-2111': 14 ,   #1지 원수 수온
    '771-358-TEI-2121': 14 ,   #2지 원수 수온
    '881-355-FRI-2864': 1200 , #원수유입유량
    cfi_tot_col     : 10}    #응집제 총 주입률

#태그데이터 상/하한 값
tei_min = 0       # 원수 수온 하한
tei_max = 35      # 원수 수온 상한
fri_min = 0       # 원수 유입유량 하한 
fri_max = 2000    # 원수 유입유량 상한 
cfi_min = 0       # 약품 주입 하한
cfi_max = 100     # 약품 주입 상한 
cfi_run_min = 1   # 약품 투입기 상태 하한
cfi_run_max = 3   # 약품 투입기 상태 상한

#표준화 운영 G값 산출식 재원
d_cglnt_cnt = 2      # 응집지 수
d_cglnt_de = 1.2     # 응집제 비중
d_o_al_de = 10.5     # 산화알루미늄농도
d_sas_o_al_de = 17   # SAS 산화알루미늄농도

#응집기 재원
d_rq = 51.243       # 조 체적
d_rq_2 = 74         # 응집지 부피 
d_g = 9.8           # 중력가속도
d_Np = 0.3          # Power-number
d_D = 1.65          # 임펠러직경
d_Cv = 3.281        # 단위환산계수

#알람 ID
alm_id1 = 133005     # (반자동모드) 혼화응집 기존 G값 대비 일정범위 이상
alm_id2 = 133001     # (반자동모드)  혼화응집 AI 제어
alm_id3 = 133004     # (자동모드) G값 변경 
alm_id4 = 133002     # 혼화응집 AI 분석 이상

@log_perform
def perform(db, job_datetime=None):
    try : 
        tags = (
                '771-358-TEI-6028',	# 금산광역(정) 원수 온도
                '771-358-TEI-2111', # 금산광역(정) 금산광역(정) 응집기 1지 수온 (F_CV
                '771-358-TEI-2121', # 금산광역(정) 금산광역(정) 응집기 2지 수온 (F_CV)
                '771-358-FRI-2011',	# 공업용수 원수유입유량
                '771-358-CFI-2462',	# 금산광역(정) PAC 주입기#1 현재주입률 (F_CV)
                '771-358-CFI-2472',	# 금산광역(정) PAC 주입기#2 현재주입률 (F_CV)
                '771-358-CFB-2462', # 금산광역(정) PAC 주입기#1 RUN
                '771-358-CFB-2472', # 금산광역(정) PAC 주입기#2 RUN
                '771-358-CFC-2101',	# 금산광역(정) 응집지 약품주입율 (F_CV)
                '771-358-LEI-2111',	# 금산광역(정) 응집기 1지 수위 (F_CV)
                '771-358-LEI-2121',	# 금산광역(정) 응집기 2지 수위 (F_CV)
                '771-358-FCC-2111',	# 금산광역(정) 응집기 1지 G값 설정 (F_CV)
                '771-358-FCC-2121',	# 금산광역(정) 응집기 2지 G값 설정 (F_CV)
                '771-358-XXI-2112',	# 금산광역(정) 응집기 1지#1 rpm값 (F_CV)
                '771-358-XXI-2114',	# 금산광역(정) 응집기 1지#2 rpm값 (F_CV)
                '771-358-XXI-2122',	# 금산광역(정) 응집기 2지#1 rpm값 (F_CV)
                '771-358-XXI-2124',	# 금산광역(정) 응집기 2지#2 rpm값 (F_CV)
                '771-358-FCB-2112',	# 금산광역(정) 응집기 1지#1 RUN (F_CV)
                '771-358-FCB-2114',	# 금산광역(정) 응집기 1지#2 RUN (F_CV)
                '771-358-FCB-2122',	# 금산광역(정) 응집기 2지#1 RUN (F_CV)
                '771-358-FCB-2124'	# 금산광역(정) 응집기 2지#2 RUN (F_CV)
        )
        
        if job_datetime is None:
            d_rt_df = db.read_rt_subday_max(tb_d_rt, tags)
            start_dt = str(d_rt_df.index[-1])

        else : 
            start_dt = job_datetime 
            d_rt_df = db.read_rt_subday_etime(tb_d_rt, tags, start_dt)

        #태그 데이터 없는 경우 이전 결과 적재
        if (len(d_rt_df) <= 0) or (len(tags) != len(d_rt_df.columns)):
            print("Empty Data")
            empty_save_ai_rt(tb_ai_d_rt) 
            
        #중복 index 제거 (공통 함수)
        d_rt_df_dup = drop_duplicate_rows(d_rt_df)
        
        # 투입기별 응집제 주입률 태그 리스트
        cfi_lst = [cfi_col_1,cfi_col_2]

        #응집제 주입기 작동 여부 태그 리스트
        cfi_run_lst = [cfi_run_1,cfi_run_2]

        #결측치 제거
        result = check_drop_missing(d_rt_df_dup)
        d_rt_df_dup_ms = d_rt_df_dup.drop(index = result[result == True].index)

        #주입기 작동 상태에 따른 전처리
        d_rt_df_dup_ms[cfi_run_lst[0]] = d_rt_df_dup_ms[cfi_run_lst[0]].apply(lambda x : 1 if x==1 else 0)
        d_rt_df_dup_ms[cfi_run_lst[1]] = d_rt_df_dup_ms[cfi_run_lst[1]].apply(lambda x : 2 if x==1 else 0)

        #작동 상태 합계
        d_rt_df_dup_ms[cfi_run_tot_col] = d_rt_df_dup_ms[cfi_run_lst[0]] + d_rt_df_dup_ms[cfi_run_lst[1]]
        
        #주입기 교번 운용을 반영한 전처리
        d_rt_df_dup_ms[cfi_run_tot_col] = d_rt_df_dup_ms[cfi_run_tot_col].apply(lambda x : 3 if x>=3 else 2 if x==2 else 1 if x==1 else 0)    
        
        #정수장에서 확보한 태그 데이터의 정상 계측 범위 상/하한 값 적용
        d_rt_df_dup_ms = upper_lower_bounds(col=tei_col        ,upper=tei_max    ,lower=tei_min       ,dataframe=d_rt_df_dup_ms)  
        d_rt_df_dup_ms = upper_lower_bounds(col=tei_loc_col1   ,upper=tei_max    ,lower=tei_min       ,dataframe=d_rt_df_dup_ms)  
        d_rt_df_dup_ms = upper_lower_bounds(col=tei_loc_col2   ,upper=tei_max    ,lower=tei_min       ,dataframe=d_rt_df_dup_ms)  
        d_rt_df_dup_ms = upper_lower_bounds(col=fri_col        ,upper=fri_max    ,lower=fri_min       ,dataframe=d_rt_df_dup_ms)
        d_rt_df_dup_ms = upper_lower_bounds(col=cfi_run_tot_col,upper=cfi_run_max,lower=cfi_run_min ,dataframe=d_rt_df_dup_ms) 
        d_rt_df_dup_ms = upper_lower_bounds(col=cfi_col_1      ,upper=cfi_max    ,lower=cfi_min       ,dataframe=d_rt_df_dup_ms)   
        d_rt_df_dup_ms = upper_lower_bounds(col=cfi_col_2      ,upper=cfi_max    ,lower=cfi_min       ,dataframe=d_rt_df_dup_ms)           
        d_rt_df_dup_ms = upper_lower_bounds(col=cfi_loc_col    ,upper=cfi_max    ,lower=cfi_min       ,dataframe=d_rt_df_dup_ms)

        #주입량이 모두 0인 경우에 대한 전처리
        d_rt_df_dup_ms.fillna(0,inplace=True)
        
        #응집제 투입량 교번 운용 반영
        d_rt_df_dup_ms[cfi_tot_col] = d_rt_df_dup_ms.apply(lambda x : x[cfi_col_1] if x[cfi_run_tot_col]==1 else x[cfi_col_2],axis=1)
        
        #(IN_VAL) 약품 투입기별 주입률(투입량)
        d_cf_inr1 = round(float(d_rt_df_dup[[cfi_col_1]].iloc[-1][0]),1)
        d_cf_inr2 = round(float(d_rt_df_dup[[cfi_col_2]].iloc[-1][0]),1)
        
        #(IN_VAL) 약품 투입기별 투입 상태
        d_cf_run1 = d_rt_df_dup_ms[[cfi_run_1]].iloc[-1][0]  
        d_cf_run2 = d_rt_df_dup_ms[[cfi_run_2]].iloc[-1][0]

        #(FACTOR) 주요인자 - 원수 수온 (1지,2지)
        d_te_loc1_factor = round(float(d_rt_df_dup[[tei_loc_col1]].iloc[-1][0]),1)
        d_te_loc2_factor = round(float(d_rt_df_dup[[tei_loc_col2]].iloc[-1][0]),1)

        #투입기별 투입량 태그 제거
        d_rt_df_dup_ms.drop(columns=cfi_run_lst,axis=1,inplace=True)
        
        #투입기별 투입량 RUN 태그 제거
        d_rt_df_dup_ms.drop(columns=cfi_run_tot_col,axis=1,inplace=True)
        
        #RPM 현재 값 관련 태그 분리
        rpm_s = d_rt_df_dup_ms.filter(regex='771-358-XXI')
        
        #RPM ON 관련 태그 분리
        rpm_on_s = d_rt_df_dup_ms.filter(regex='771-358-FCB')

        #G값 설정 태그 분리
        g_s = d_rt_df_dup_ms[[g_s_col1,g_s_col2]]
        
        col_lst = []
        col_lst.extend(rpm_s.columns)
        col_lst.extend(rpm_on_s.columns)
        col_lst.extend(g_s.columns)
        
        #G값/RPM값 산출에 필요한 컬럼만 추출
        d_rt_df_outlier = d_rt_df_dup_ms[d_rt_df_dup_ms.columns.difference(col_lst)]
        
        #전처리된 데이터 프레임
        d_pre_df = preprocess_dataframe(d_rt_df_outlier)       
        
        # 혼화응집 지별 번호 리스트
        list_pond_nm = ['A','B']

        # 입력변수 기존 5개년 평균치로 대체
        for col in tags:
            if col in dic_mean : 
                if d_pre_df[col].isna().sum() > 0:
                    d_pre_df[col] = dic_mean[col]    
        
        #점성(viscosity)
        df_v = [0.000183, 0.000177, 0.000171, 0.000166, 0.000161, 0.000156, 0.000151, 0.000147, 0.000143, 0.000139, 
                0.000135, 0.000131, 0.000127, 0.000124, 0.000121, 0.000118, 0.000115, 0.000112, 0.000109, 0.000106, 
                0.000104, 0.000101, 0.000099, 0.000097, 0.000095, 0.000092, 0.000090, 0.000088, 0.000086, 0.000085, 
                0.000083, 0.000081, 0.000079, 0.000078, 0.000076, 0.000075, 0.000073, 0.000072, 0.000070, 0.000069]
        #밀도(density) 단위 : g/cm3
        df_d = [0.99987, 0.99993, 0.99997, 0.99999, 1, 0.99999, 0.99997, 0.99993, 0.99988, 0.99981, 
                0.99973, 0.99963, 0.99952, 0.9994, 0.99927, 0.99913, 0.99897, 0.9988, 0.99862, 0.99843, 
                0.99823, 0.99802, 0.9978, 0.99757, 0.99733, 0.99707, 0.99681, 0.99654, 0.99626, 0.99597, 
                0.99568, 0.99537, 0.99505, 0.99473, 0.9944, 0.99406, 0.99371, 0.99336, 0.99299, 0.99262]

        #시간대 추출
        current_dt,current_dt_ts,prev_3h = set_current_dt(current_dt=start_dt)
        
        #현재 시간대 기준 행 추출
        df_current = prepare_data(df=d_pre_df,tei_col=tei_col,current_dt=current_dt) 

        #전처리 후 입력변수 생성
        if len(d_pre_df) > 180 : 
            ai_d_te = round(prepare_data(df=d_pre_df.loc[prev_3h:current_dt],tei_col=tei_col,current_dt=current_dt).median(),0)  # 원수 수온
            ai_d_te_loc1 = round(prepare_data(df=d_pre_df.loc[prev_3h:current_dt],tei_col=tei_loc_col1,current_dt=current_dt).median(),0)  # 1지 응집지 수온           
            ai_d_te_loc2 = round(prepare_data(df=d_pre_df.loc[prev_3h:current_dt],tei_col=tei_loc_col2,current_dt=current_dt).median(),0)  # 2지 응집지 수온
            
            ai_d_in_fr = round(d_pre_df.loc[prev_3h:current_dt][fri_col].median(),0)                                             # 전처리 후 원수 유입유량
            ai_d_cglnt_inr = round(d_pre_df.loc[prev_3h:current_dt][cfi_tot_col].median(),0)                                     # 전처리 후 총 응집제 주입률
        
        else  :
            ai_d_te = round(prepare_data(df=d_pre_df,tei_col=tei_col,current_dt=current_dt).median(),0)  # 원수 수온
            ai_d_te_loc1 = round(prepare_data(df=d_pre_df,tei_col=ai_d_te_loc1,current_dt=current_dt).median(),0)  # 1지 응집지 수온  
            ai_d_te_loc2 = round(prepare_data(df=d_pre_df,tei_col=ai_d_te_loc2,current_dt=current_dt).median(),0)  # 2지 응집지 수온
            
            ai_d_in_fr = round(d_pre_df[fri_col].median(),0)                                             # 전처리 후 원수 유입유량
            ai_d_cglnt_inr = round(d_pre_df[cfi_tot_col].median(),0)                                     # 전처리 후 총 응집제 주입률

        
        #수온에 따른 파생변수 생성    
        d_de = get_v(df_d, ai_d_te)           # (원수수온) 밀도
        d_de_loc1 = get_v(df_d, ai_d_te_loc1) # (1지 수온) 밀도
        d_de_loc2 = get_v(df_d, ai_d_te_loc2) # (2지 수온) 밀도
        
        d_v = get_v(df_v, ai_d_te)           # (원수수온) 점도
        d_v_loc1 = get_v(df_v, ai_d_te_loc1) # (1지 수온) 점도       
        d_v_loc2 = get_v(df_v, ai_d_te_loc2) # (2지 수온) 점도        

        d_mu = cal_mu(ai_d_te)             # (원수수온) 점성계수
        d_mu_loc1 = cal_mu(ai_d_te_loc1)   # (1지 수온) 점성계수
        d_mu_loc2 = cal_mu(ai_d_te_loc2)   # (2지 수온) 점성계수
        
        d_vt = cal_vt(d_mu,d_de)           # (원수수온) 동점성계수
        d_vt_loc1 = cal_vt(d_mu_loc1,d_de) # (1지 수온) 동점성계수
        d_vt_loc2 = cal_vt(d_mu_loc2,d_de) # (2지 수온) 동점성계수


        # 원수 유입유량/응집제주입률
        if len(df_current) != 1 : 
            fri = d_pre_df[[fri_col]].iloc[-1:, :]
            sri = d_pre_df[[cfi_tot_col]].iloc[-1:, :]
        else : 
            fri = d_pre_df[[fri_col]]
            sri = d_pre_df[[cfi_tot_col]]

        #응집기 제원    
        param_dic = {'mu' : d_mu,    # 점성계수
                     'V'   : d_rq,   # 조 체적
                     'g'   : d_g,    # 중력가속도(m/s2)
                     'Np'  : d_Np,   # Power Number
                     'D'   : d_D,    # 임펠러 직경
                     'Cv'  : d_Cv}   # 단위환산계수
            
        #체류시간 제원
        param_dic_t = {
                    'V'   :  d_rq_2*2,               # 응집지 부피
                    'coagulation_cnt' : d_cglnt_cnt} # 응집지 수
        
        #응집제 주입률(SAS) 제원
        param_dic_c = {
            'LAS_D' : d_cglnt_de,      # LAS 비중 [응집제 비중]
            'LAS_AO' : d_o_al_de,      # LAS 산화알루미늄농도
            'SAS_AO' : d_sas_o_al_de}  # SAS 산화알루미늄농도
        
        #응집지 지별 매핑용 딕셔너리
        pond_dict = {'A':1,'B':2}

        #init 테이블 값 변수 할당
        d_operation_mode,d_g_value_ctr_flag,d_g_value_step,d_g_step_crt,d_g_step_min,d_g_step_max = d_init(db,tb_ai_init)

        #G값 수동모드
        if d_g_value_ctr_flag == 0 : 
            g1,g2 = d_g_value_step[0],d_g_value_step[1]
            
                 
        #G값 자동모드
        elif d_g_value_ctr_flag == 1 : 
            g1,g2,g3 = g_formula(fri,param_dic_t,sri,param_dic_c) 

            #자동모드 보정계수 적용
            g1 = g2*d_g_step_crt[0]
            g2 = g2*d_g_step_crt[1]
            
            #g값 상/하한 적용
            g1 = d_g_min_max(g1,d_g_step_min[0],d_g_step_max[0])
            g2 = d_g_min_max(g2,d_g_step_min[1],d_g_step_max[1])
            
        #RPM 산출
        rpm_values = {}
        for i, pond in enumerate(list_pond_nm): 
            if pond == 'A' : 
                rpm_1 = round(rpm_formula_3(g=g1,param_dic=param_dic,d_mu=d_mu_loc1),2)
                rpm_2 = round(rpm_formula_3(g=g1,param_dic=param_dic,d_mu=d_mu_loc1),2) 
            else : 
                rpm_1 = round(rpm_formula_3(g=g2,param_dic=param_dic,d_mu=d_mu_loc2),2)
                rpm_2 = round(rpm_formula_3(g=g2,param_dic=param_dic,d_mu=d_mu_loc2),2)      
                
            #결과 저장
            rpm_values[i] = [rpm_1,rpm_2]

                
        #각 응집지 지별 RPM 현재값 태그 매핑
        pond_tag_rpm  = {'A' : ['771-358-XXI-2112',
                               '771-358-XXI-2114'],
                        'B' :  ['771-358-XXI-2122',
                                '771-358-XXI-2124']}
                        

        #각 응집지 열별 RPM ON 태그 매핑
        pond_tag_rpm_on ={
                'A' : [
                '771-358-FCB-2112',	 # 금산광역(정) 응집기 1지#1 RUN (F_CV)
                '771-358-FCB-2114'], # 금산광역(정) 응집기 1지#2 RUN (F_CV)
        
                'B' : [
                '771-358-FCB-2122',	 # 금산광역(정) 응집기 2지#1 RUN (F_CV)
                '771-358-FCB-2124']} # 금산광역(정) 응집기 2지#2 RUN (F_CV)

        #응집기 RPM 현재 값
        d_fc_location_sp_json_str = dict()
        to_dict_location = dict()
        for pond in list_pond_nm:
            to_dict_step  = dict()
            for i in range(2):
                pond_tag_rpm1 = rpm_s[pond_tag_rpm[pond][i]].fillna(method='ffill')
                to_dict_step['step{}'.format(i+1)] = pond_tag_rpm1.values[-1]
            to_dict_location['location{}'.format(pond_dict[pond])] = to_dict_step

        d_fc_location_sp_json_str['d_fc_location_state_rpm'] = to_dict_location
        d_fc_location_sp_json_str = str(d_fc_location_sp_json_str).replace("'", "\"")

        #예측 응집기 RPM 설정값
        ai_d_fc_location_sp2_json_str = dict()
        to_dict_location = dict()
        for j, pond in enumerate(list_pond_nm): 
            to_dict_step  = dict()
            rpm_value = rpm_values[j]
            for i in range(2):
                to_dict_step['step{}'.format(i+1)] = rpm_value[i]
            to_dict_location['location{}'.format(pond_dict[pond])] = to_dict_step
        
        ai_d_fc_location_sp2_json_str['ai_d_fc_location_sp2'] = to_dict_location
        ai_d_fc_location_sp2_json_str = str(ai_d_fc_location_sp2_json_str).replace("'", "\"")

        #응집기 ON 상태
        d_fc_location_state_json_str = dict() 
        to_dict_location = dict()
        for pond in list_pond_nm:
            to_dict_step  = dict()
            for i in range(2):
                pond_tag_rpm_on1 = rpm_on_s[pond_tag_rpm_on[pond][(i)]].fillna(method='ffill')             
                to_dict_step['step{}'.format(i+1)] = pond_tag_rpm_on1.values[-1]
            to_dict_location['location{}'.format(pond_dict[pond])] = to_dict_step
            
        d_fc_location_state_json_str['d_fc_location_state'] = to_dict_location
        d_fc_location_state_json_str = str(d_fc_location_state_json_str).replace("'", "\"")

        #현재 G값 설정값
        d_fc_location_g_settings = {'location1':round(float(g_s[[g_s_col1]].iloc[-1][0]),0),'location2':round(float(g_s[[g_s_col2]].iloc[-1][0]),0)} 
        
        #산출 G값 (수동,자동)
        d_fc_location_g_json_str = {'location1':g1,'location2':g2}
            
        #G값 열별 보정 값
        d_fc_location_g_crt_json_str = {'g1':d_g_step_crt[0],'g2':d_g_step_crt[1]}
        
        #G값 열별 상하한
        d_fc_location_g_minmax_json_str = {'g1':{'max':d_g_step_max[0] ,'min':d_g_step_min[0]}
                                           ,'g2':{'max':d_g_step_max[1] ,'min':d_g_step_min[1]}}
        
        # 응집제 주입률
        a=OrderedDict() 
        a['cglnt_inr']=round(float(ai_d_cglnt_inr),1)
        a['d_loc_cglnt_inr']=round(float(ai_d_cglnt_inr),1)
        ai_d_cglnt_inr_rslt=json.dumps(a,ensure_ascii=False)   

        #원수 수온
        a=OrderedDict() 
        a['d_te']=round(float(ai_d_te),1)
        a['d_loc_te'] = dict()
        a['d_loc_te']['location1'] = round(float(ai_d_te_loc1),1)
        a['d_loc_te']['location2'] = round(float(ai_d_te_loc2),1)
        ai_d_te_rslt=json.dumps(a,ensure_ascii=False)           

        #수온 파생변수 (밀도)
        a=OrderedDict() 
        a['d_de']=round(float(d_de),1)
        a['d_loc_de'] = dict()
        a['d_loc_de']['location1'] = round(float(d_de_loc1),10)
        a['d_loc_de']['location2'] = round(float(d_de_loc2),10)
        ai_d_de_rslt=json.dumps(a,ensure_ascii=False)      
        
        #수온 파생변수 (점성계수)
        a=OrderedDict() 
        a['d_dv']=round(float(d_mu),1)
        a['d_loc_dv'] = dict()
        a['d_loc_dv']['location1'] = round(float(d_mu_loc1),10)
        a['d_loc_dv']['location2'] = round(float(d_mu_loc2),10)
        ai_d_dv_rslt=json.dumps(a,ensure_ascii=False)           

        #수온 파생변수 (동점성계수)
        a=OrderedDict() 
        a['d_vt']=round(float(d_vt),1)
        a['d_loc_vt'] = dict()
        a['d_loc_vt']['location1'] = round(float(d_vt_loc1),10)
        a['d_loc_vt']['location2'] = round(float(d_vt_loc2),10)
        ai_d_vt_rslt=json.dumps(a,ensure_ascii=False) 
        
        #응집지 수위 #1,#2
        a=OrderedDict() 
        a['location1'] = round(float(d_pre_df[lei_loc_col1][-1]),1)
        a['location2'] = round(float(d_pre_df[lei_loc_col2][-1]),1)
        ai_d_lei_rslt=json.dumps(a,ensure_ascii=False)         
        
        in_val = pd.DataFrame(
            [(
                rpm_flag,                          # 산출식 유형
                d_g_value_ctr_flag,                # G값 모드 (자동:0/수동:1)
                ai_d_in_fr,                        # 전처리된 원수 유입 유량
                ai_d_cglnt_inr_rslt,               # 전처리된 총 응집제 주입률
                d_cf_inr1,                         # 1호기 응집제 주입률
                d_cf_inr2,                         # 2호기 응집제 주입률
                d_cf_run1,                         # 응집제 주입기 #1 상태 (가동여부)
                d_cf_run2,                         # 응집제 주입기 #2 상태 (가동여부)
                d_cglnt_cnt,                       # 총 응집지 수 
                d_cglnt_de,                        # 응집제 비중
                d_o_al_de,                         # 산화 알루미늄 농도
                d_sas_o_al_de,                     # SAS 산화 알루미늄 농도
                ai_d_te_rslt,                      # 전처리된 원수 수온
                ai_d_de_rslt,                      # (전처리된 원수 수온 기준) 밀도
                ai_d_dv_rslt,                      # (전처리된 원수 수온 기준) 점성계수
                ai_d_vt_rslt,                      # (전처리된 원수 수온 기준) 동점성계수 
                ai_d_lei_rslt,                     # 응집지 수위
                d_rq,                              # 조 체적
                d_g,                               # 중력가속도(m/s2)
                d_Np,                              # Power Number
                d_D,                               # 임펠러 직경(m) 
                d_Cv,                              # 단위환산계수     
                d_fc_location_g_settings,          # G값 현재값
                d_fc_location_g_json_str,          # G값 산출값(자동/수동)
                d_fc_location_g_crt_json_str,      # G값 보정계수
                d_fc_location_g_minmax_json_str,   # G값 상/하한
                d_fc_location_sp_json_str,         # 응집지 지별 응집기 속도
                d_fc_location_state_json_str       # 응집지 지별 응집기 상태        
            )],
            columns = [
                'TYPE',                            # 산출식 유형
                'D_G_VALUE_CTR_FLAG',              # G값 모드 (자동:0/수동:1)
                'D_IN_FR',                         # 전처리된 원수 유입 유량
                'D_CGLNT_INR',                     # 전처리된 총 응집제 주입률
                'D_CF_INR1',                       # 1호기 응집제 주입률
                'D_CF_INR2',                       # 2호기 응집제 주입률
                'D_CF_RUN1',                       # 응집제 주입기 #1 상태 (가동여부)
                'D_CF_RUN2',                       # 응집제 주입기 #2 상태 (가동여부)
                'D_FC_SER_TOT_CNT',                # 총 응집지 수
                'D_CGLNT_DE',                      # 응집제 비중
                'D_O_AL_DE',                       # 산화알루미늄 농도
                'D_SAS_O_AL_DE',                   # SAS 산화 알루미늄 농도
                'D_TE',                            # 전처리된 원수 수온
                'D_DE',                            # (전처리된 원수 수온 기준) 밀도
                'D_DV',                            # (전처리된 원수 수온 기준) 점성계수
                'D_KI_DV',                         # (전처리된 원수 수온 기준) 동점성계수 
                'D_LEI',                           # 응집지 수위
                'D_V',                             # 조 체적
                'D_GR',                            # 중력가속도
                'D_PW',                            # Power Number
                'D_IM_D',                          # 임팰러 직경
                'D_CV',                            # 단위환산계수
                'D_G_S',                           # G값 현재 설정 값 
                'D_G',                             # G값 예측 값
                'D_G_CRT',                         # G값 보정계수
                'D_G_MINMAX',                      # G값 상/하한
                'D_LOC_FC_SP',                     # 응집지 지별 응집기 속도
                'D_LOC_FC_STT']                    # 응집지 지별 응집기 상태
        )
        
        out_val = pd.DataFrame(
            [(
                d_fc_location_g_json_str,          # G값 
                ai_d_fc_location_sp2_json_str,     # 응집지 지별 응집기 속도
                d_fc_location_state_json_str       # 응집지 지별 응집기 상태        
            )],
            columns = [
                'AI_D_LOC_FC_G',                  # 응집지 지별 G값 
                'AI_D_LOC_FC_SP',                 # 응집지 지별 응집기 예측 속도
                'AI_D_LOC_FC_STT'])               # 응집지 지별 응집기 상태
        
        #Dict 형 변환 컬럼
        in_val_col = ['D_CGLNT_INR','D_TE','D_DE','D_DV','D_KI_DV','D_LEI','D_LOC_FC_SP','D_LOC_FC_STT'] 
        out_val_col = ['AI_D_LOC_FC_SP','AI_D_LOC_FC_STT']
        
        #JSON 형 변환
        in_val_json = rslt_val(val=in_val,val_col=in_val_col)
        out_val_json = rslt_val(val=out_val,val_col=out_val_col)

        #AI_RT 결과 데이터 프레임 생성
        df_mixing_ai_result = pd.DataFrame(columns = ['upd_ti', 'AI_OPR', 'IN_VAL', 'OUT_VAL'], index=[start_dt])
        df_mixing_ai_result['upd_ti'] =  time.strftime('%Y-%m-%d %H:%M:%S')
        df_mixing_ai_result['AI_OPR'] =  d_operation_mode
        df_mixing_ai_result['IN_VAL'] =  in_val_json
        df_mixing_ai_result['OUT_VAL'] = out_val_json
        
        #AI_RT 결과 데이터 저장
        db.save_ai_rt(tb_ai_d_rt, df_mixing_ai_result)

        #FACTOR 주요인자 알람 항목 저장
        factor_val = pd.DataFrame(
            [(
              d_te_loc1_factor,           # 원수 수온 (1지) 
              d_te_loc2_factor,           # 원수 수온 (2지) 
              round(float(d_mu_loc1),10), # 점성계수 (1지) 
              round(float(d_mu_loc2),10), # 점성계수 (2지)
              d_D,                        # 임펠러 직경 
              d_Np,                       # power-number
              d_v                         # 조체적
            )],
            columns = [
                'b_te_loc1',    # 원수 수온(1지)
                'b_te_loc2',    # 원수 수온(2지)
                'd_dv_loc1',    # 점성계수 (1지)
                'd_dv_loc2',    # 점성계수 (2지)
                'd_im_d',       # 임펠러 직경
                'd_pw',         # power-number 
                'd_v'           # 조체적
                ])               

        #JSON 형 변환
        factor_val_json = rslt_val(val=factor_val,val_col=[])
        
        #FACTOR 데이터프레임 생성
        df_mixing_ai_factor = pd.DataFrame(columns = ['proc_cd', 'disinfection_index', 'rnti', 'factor'], index=[start_dt])
        df_mixing_ai_factor['proc_cd'] = proc_cd
        df_mixing_ai_factor['disinfection_index'] =  'NONE'
        df_mixing_ai_factor['rnti'] =  str(pd.to_datetime(df_mixing_ai_result['upd_ti'][0]))
        df_mixing_ai_factor['factor'] =  factor_val_json
        
        print('----------------혼화응집 모델 결과 적재 완료 ----------------')

        ## ITM 에 매핑
        itm_tmp = ['d_fc_sp_set1','d_fc_sp_set2']

        #제어 태그 목록 데이터프레임
        ctr_tag_lst = mk_ctr_tag_list (db,tb_tag_mng,ai_cd,proc_cd,itm_tmp)

        #G값 설정 매핑을 위해 컬럼명 변경
        g_s.columns = itm_tmp 
        g_s = g_s.sort_index(ascending=True)
        g_s = g_s.fillna(method='ffill').fillna(method='bfill')
        for col in g_s.columns : 
            g_s = upper_lower_bounds(col=col,  upper=100,  lower=0, dataframe=g_s) 

        # 알람 관련 변수
        alm_cnt_1 = [] # 133005 : 혼화응집 기존 G값 대비 일정범위 이상
        alm_cnt_2 = [] # 133001 : 혼화응집 AI 제어
        alm_cnt_3 = 0  # 133004 : 혼화응집 G값 변경 
        alm_cnt_factor = 0  # 주요인자 알람 팝업
        
        # 알람 팝업 주기
        pre_alm_yn_1 = pre_alm_check(db,tb_ai_d_alm,alm_id1) # 133005 : 혼화응집 기존 G값 대비 일정범위 이상
        pre_alm_yn_2 = pre_alm_check(db,tb_ai_d_alm,alm_id2) # 133001 : 혼화응집 AI 제어   
        pre_alm_yn_3 = pre_alm_check(db,tb_ai_d_alm,alm_id3) # 133004 : 혼화응집 G값 변경
        
        #반자동모드/자동모드 알람/제어
        if d_operation_mode != 0  : 
            dict_sp  =  json.loads(df_mixing_ai_result['OUT_VAL'][0])[0]['AI_D_LOC_FC_G']

            #지/열별 알람 임계치 조건
            d_alm_thrshld_undr = lambda dict_sp, loc_no, cmp_val: round(round(dict_sp['location{}'.format(loc_no)],0)/cmp_val, 2) <= 0.80
            d_alm_thrshld_over = lambda dict_sp, loc_no, cmp_val: round(round(dict_sp['location{}'.format(loc_no)],0)/cmp_val, 2) >= 1.20

            #모든 열 임계치 충족 여부
            alm_thrshld_yn = d_alm_thrshld_check (dict_sp,g_s,ctr_tag_lst,d_alm_thrshld_undr,d_alm_thrshld_over)
            
            #지/열변호 추출
            for loc_id, loc_val in dict_sp.items():
                #지 번호 추출
                loc_no = re.sub(r'[^0-9]+', '', loc_id) 
                
                #지/열변호 기준 제어태그 매핑
                tag_item = 'd_fc_sp_set{}'.format(loc_no)
                control_tag = ctr_tag_lst[ctr_tag_lst['ITM'] == tag_item]['TAG_SN'].values[0]

                #해당 태그명 G값 설정값 매핑
                cmp_val = g_s[tag_item].values[-1]
                
                #현재 값과 제어 값 동일한 경우 제외
                if round(float(cmp_val),0) == round(float(dict_sp['location{}'.format(loc_no)]),0) :             
                    continue
                
                #반자동모드 알람/제어
                if d_operation_mode == 1  :  
                    if d_g_value_ctr_flag == 0 :
                        #임계치 기준 초과 시 임계치 알람
                        if d_alm_thrshld_undr(dict_sp, loc_no, cmp_val) or d_alm_thrshld_over(dict_sp, loc_no, cmp_val) : 
                            if pre_alm_yn_1 == 0 : 
                                if loc_no not in alm_cnt_1 :
                                    send_alm(db=db,table=tb_ai_d_alm,alm_id=alm_id1,alm_ti=str(pd.to_datetime(df_mixing_ai_result['upd_ti'][0])+timedelta(minutes=int(loc_no)-1)))
                                    alm_cnt_1.append(loc_no)
                                send_ctr(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val)
                                if alm_cnt_factor == 0 : 
                                    db.save_ai_factor(df=df_mixing_ai_factor)
                                    alm_cnt_factor += 1 
                                
                        #임계치 기준을 초과하지 않는 경우 G값 변경 알람
                        else : 
                            if pre_alm_yn_2 == 0 : 
                                if loc_no not in alm_cnt_2 :
                                    alm_cnt_2.append(loc_no)
                                #제어 결과 적재
                                send_ctr(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val)
                                #주요인자 결과 적재
                                if alm_cnt_factor == 0 : 
                                    db.save_ai_factor(df=df_mixing_ai_factor)
                                    alm_cnt_factor += 1 
                                
                    #G값 자동모드 해당
                    elif d_g_value_ctr_flag ==1 : 
                        #임계치 기준 초과 시 임계치 알람
                        if  d_alm_thrshld_undr(dict_sp, loc_no, cmp_val) or d_alm_thrshld_over(dict_sp, loc_no, cmp_val) : 
                            if pre_alm_yn_1 == 0 :   
                                if loc_no not in alm_cnt_1 :
                                    send_alm(db=db,table=tb_ai_d_alm,alm_id=alm_id1,alm_ti=str(pd.to_datetime(df_mixing_ai_result['upd_ti'][0])+timedelta(minutes=int(loc_no)-1)))
                                    alm_cnt_1.append(loc_no)
                                send_ctr(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val)
                                if alm_cnt_factor == 0 : 
                                    db.save_ai_factor(df=df_mixing_ai_factor)
                                    alm_cnt_factor += 1 

                        #임계치 기준을 초과하지 않는 경우 G값 변경 알람
                        else : 
                            if pre_alm_yn_2 == 0 : 
                                if loc_no not in alm_cnt_2 :
                                    alm_cnt_2.append(loc_no)
                                #제어 결과 적재
                                send_ctr(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val)
                                #주요인자 결과 적재
                                if alm_cnt_factor == 0 : 
                                    db.save_ai_factor(df=df_mixing_ai_factor)
                                    alm_cnt_factor += 1 

                                
                #자동모드 알람/제어
                elif d_operation_mode == 2  :  
                    if d_g_value_ctr_flag == 0 :
                        #임계치 기준 초과 시 임계치 알람
                        if alm_thrshld_yn == 1 : 
                            if pre_alm_yn_1 == 0 : 
                                if loc_no not in alm_cnt_1 :
                                    send_alm(db=db,table=tb_ai_d_alm,alm_id=alm_id1,alm_ti=str(pd.to_datetime(df_mixing_ai_result['upd_ti'][0])+timedelta(minutes=int(loc_no)-1)))
                                    alm_cnt_1.append(loc_no)
                                send_ctr(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val)
                                if alm_cnt_factor == 0 : 
                                    db.save_ai_factor(df=df_mixing_ai_factor)
                                    alm_cnt_factor += 1 
                                
                        #임계치 기준을 초과하지 않는 경우 자동모드 제어
                        else : 
                            if pre_alm_yn_3 == 0 : 
                                if alm_cnt_3 == 0  : 
                                    send_alm(db=db,table=tb_ai_d_alm,alm_id=alm_id3,alm_ti=str(pd.to_datetime(df_mixing_ai_result['upd_ti'][0])))
                                    alm_cnt_3 += 1
                                send_ctr_all(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val)
                                if alm_cnt_factor == 0 : 
                                    db.save_ai_factor(df=df_mixing_ai_factor)
                                    alm_cnt_factor += 1 
                                
                    #G값 자동모드 해당
                    elif d_g_value_ctr_flag ==1 : 
                        if alm_thrshld_yn == 1 : 
                            if pre_alm_yn_1 == 0 :   
                                if loc_no not in alm_cnt_1 :
                                    send_alm(db=db,table=tb_ai_d_alm,alm_id=alm_id1,alm_ti=str(pd.to_datetime(df_mixing_ai_result['upd_ti'][0])+timedelta(minutes=int(loc_no)-1)))
                                    alm_cnt_1.append(loc_no)
                                send_ctr(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val)
                                if alm_cnt_factor == 0 : 
                                    db.save_ai_factor(df=df_mixing_ai_factor)
                                    alm_cnt_factor += 1 
                                    
                        #임계치 기준을 초과하지 않는 경우 자동모드 제어
                        else : 
                            if pre_alm_yn_3 == 0 :   
                                if alm_cnt_3 == 0 : 
                                    send_alm(db=db,table=tb_ai_d_alm,alm_id=alm_id3,alm_ti=str(pd.to_datetime(df_mixing_ai_result['upd_ti'][0])))
                                    alm_cnt_3 += 1 
                                send_ctr_all(db,tb_ai_d_ctr,df_mixing_ai_result,control_tag,dict_sp,loc_no,cmp_val)
                                if alm_cnt_factor == 0 : 
                                    db.save_ai_factor(df=df_mixing_ai_factor)
                                    alm_cnt_factor += 1 
                                                
            print('----------------혼화응집 모델 제어 값 적재 완료 ----------------')   

        else :
            print('----------------혼화응집 모델 제어 실행 완료 ----------------')  
            pass 

        return df_mixing_ai_result
        
    except:
        runLogger.error('Error: {}'.format(traceback.format_exc()))
        # (분석 이상 알람) 분석 코드 실행 중 발생한 에러는 분석 이상 알람으로 처리하여 ALM_TBL에 전송
        send_alm(db=db,table=tb_ai_d_alm,alm_id=alm_id4,alm_ti=time.strftime('%Y-%m-%d %H:%M:%S'))
        print('err occured')
    
