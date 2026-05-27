#-*- coding:utf-8 -*-
##########
# 약품 실행 파일
# author : Lee Hyeokhui
# since : 2024. 10. 07
# version : 0.1
##########
############################### package load ###############################
from ai_c import *
import tracemalloc
# vip 체크
import check_vip as vip

# 공정 메인 함수
def main(args):
    """공정 메인 함수

    Args:
        args (Dictionary): 파라미터
    """
        
    # DB Class 호출 
    db = DBUtil(Config)

    while True:
        vip_check = vip.main("waio-portal-vip")
        if vip_check == "isVip":
            try:
                perform(db)
            except Exception as e:
                runLogger.error('Error: {}'.format(traceback.format_exc()))
                pass

            print(tracemalloc.get_traced_memory())
            time.sleep(180)

        else:
            time.sleep(300)
            pass
        
if __name__=="__main__":
    tracemalloc.start()
    
    try:
        main(sys.argv)
    except Exception as e:
        runLogger.error('Error: {}'.format(str(e)))

    tracemalloc.stop()