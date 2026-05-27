from ai_g import *

def main(args):
    '''
    공정 메인 함수
    '''
    # DB 연결
    db = DBUtil(Config)

    # 첫 loop 구분자로 count 사용
    # 첫 loop 증발량 저장을 위해 evaporation_frist 사용
    count = 1
    evaporation_first = -1
    # 보정 주기
    count_pre = 999
    count_post = 999
    # 보정 주기 사이에 사용할 주입률 값
    pre_chol = 0
    post_chol = 0
    calib_time_pre = datetime.now() - timedelta(days=1)
    calib_time_post = datetime.now() - timedelta(days=1)
    
    
    while True:
        try:
            calib_time_pre, calib_time_post, count_pre, count_post, pre_chol, post_chol = perform(db, calib_time_pre, calib_time_post, count_pre, count_post, pre_chol, post_chol)
        except Exception as e:
            pass
        time.sleep(60)

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
    