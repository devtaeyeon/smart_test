from ai_d import *

import os
import sys
import traceback
import logging
from logging import handlers
import time
from multiprocessing import Process, Manager
from datetime import datetime, timedelta

# 사용자 정의 클래스 
PROC_PATH = os.path.dirname(os.path.abspath(__file__)) # (.py) 
BASE_PATH = os.path.dirname(PROC_PATH)
ROOT_PATH = os.path.dirname(BASE_PATH)

sys.path.append(PROC_PATH + '/common')
sys.path.append(BASE_PATH + '/common')
sys.path.append(ROOT_PATH + '/common')

PROC_NAME = os.path.basename(PROC_PATH)
LOGS_PATH = BASE_PATH + '/logs/'
PROC_PATH = '/home/au/ca/d2_ind/rt_table'

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


DATE_FORMAT = '%Y-%m-%d %H:%M:%S'

def main(from_datetime, to_datetime):
    """공정 일괄 처리 메인 함수

    Args:
        from_datetime (datetime): 일괄처리 대상 시작일시
        to_datetime (datetime): 일괄처리 대상 종료일시
    """
    
    job_datetime = from_datetime
    delta = timedelta(minutes=10)
    df_result = pd.DataFrame()
    while job_datetime <= to_datetime:
        try:           
            df_tmp =  perform(db, job_datetime)
            df_result = pd.concat([df_result, df_tmp])
            job_datetime += delta
            
        except Exception as e:
            runLogger.error('Error: {}'.format(traceback.format_exc()))
            pass
    
    return df_result
        
try:
    
    # 날짜 데이터 선언
    from_datetime = datetime.strptime('2022-12-27 00:00:00', DATE_FORMAT)
    to_datetime = datetime.strptime('2022-12-27 23:59:00', DATE_FORMAT)
    # DB Class 호출 
    db = DBUtil(Config)
    
    df_result = main(from_datetime, to_datetime)
    df_result.to_csv(PROC_PATH+"/result/AI_D_RT.csv", index=False)
    print('### 결과 적재 완료 ###')
except Exception as e:
    runLogger.error('Error: {}'.format(str(e)))





