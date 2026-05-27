from ai_g import *
import check_vip as vip

def main(args):
    '''
    공정 메인 함수
    '''
    # DB 연결
    db = DBUtil(Config)

    # 보정 주기 도달 및 미도달 시 활용할 인자 설정
    count_pre = 999
    count_post = 999
    pre_chol = 0
    post_chol = 0
    calib_time_pre = datetime.now() - timedelta(days=1)
    calib_time_post = datetime.now() - timedelta(days=1)
    
    
    while True:
        vip_check = vip.main("waio-portal-vip")
        if vip_check == 'isVip':       
            try:
                calib_time_pre, calib_time_post, count_pre, count_post, pre_chol, post_chol = perform(db, calib_time_pre, calib_time_post, count_pre, count_post, pre_chol, post_chol)
            except Exception as e:
                pass
        else:
            pass
        time.sleep(60)

if __name__=="__main__":
    try:
        main(sys.argv)
    except Exception as e:
        runLogger.error('Error: {}'.format(str(e)))
