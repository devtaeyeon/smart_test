#-*- coding:utf-8 -*-

############################### package load ###############################
from ai_c import *

# 공정 메인 함수
def main(args):
    """공정 메인 함수

    Args:
        args (Dictionary): 파라미터
    """
        
    # DB Class 호출 
    db = DBUtil(Config)

    # 주입률 예측 시작
    while True:
        try:
            perform(db)
        except Exception as e:
            runLogger.error('Error: {}'.format(traceback.format_exc()))
            pass
            
        time.sleep(180)
        
if __name__=="__main__":
    try:
        main(sys.argv)
    except Exception as e:
        runLogger.error('Error: {}'.format(str(e)))
    
    # manager=Manager()
    # params=manager.dict()

    # p1=Process(target=main,args=(params,))

    # p1.start()
    # p1.join()
