from ai_d import *
# from ai_d2_지열별제어 import *


# 공정 메인 함수
def main(args):
    """공정 메인 함수

    Args:
        args (Dictionary): 파라미터
    """
        
    # DB Class 호출 
    db = DBUtil(Config)

    while True:
        try:
            perform(db)
        except Exception as e:
            runLogger.error('Error: {}'.format(traceback.format_exc()))
            pass
            
        time.sleep(600)
        
if __name__=="__main__":
    try:
        main(sys.argv)
    except Exception as e:
        runLogger.error('Error: {}'.format(str(e)))

