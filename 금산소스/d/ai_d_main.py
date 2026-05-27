from ai_d import *
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
        if vip_check == "isVip": # tmp 
            try:
                perform(db)
            except Exception as e:
                runLogger.error('Error: {}'.format(traceback.format_exc()))
                run_time = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
                run_time = f'"{run_time}"'
                save_alm('TB_AI_D_ALM', 133002, run_time)
                pass

            time.sleep(600)
            print(tracemalloc.get_traced_memory())
        else:
            time.sleep(600)

        
if __name__=="__main__":
    tracemalloc.start()
    
    try:
        main(sys.argv)
    except Exception as e:
        runLogger.error('Error: {}'.format(str(e)))

    tracemalloc.stop()

