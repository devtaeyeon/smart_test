<template>
  <div v-if="this.isVisible" class="popup" :style="{ zIndex: myZIndex }">
    <div v-if="this.alm_ty == 3 || this.alm_ty == 0" class="popup-type3__outter" :style="{ marginLeft: myMargin, marginTop: myMargin }">
      <div class="popup-type3__box">
        <div class="popup-type3__title">
          <div class="popup-type3__icon"></div>
        </div>
        <div class="popup-type3__body">
          <div class="popup-type3__date">{{ this.alm_ntf_ti | moment('YYYY-MM-DD HH:mm:ss') }}</div>
          <div class="popup-type3__title">{{ this.message }}</div>
        </div>
        <div class="popup-type3__bottom">
          <div v-if="this.alm_ty == 0" class="btn btn--cancel" @click="closePopup()">확인</div>
          <div v-if="this.alm_ty == 3" class="btn btn--cancel" @click="closePopup()">닫기</div>
          <div v-if="this.alm_ty == 3" class="btn btn--change" @click="goToURL()">이동</div>
        </div>
      </div>
    </div>
    <div v-if="this.alm_ty === 2 || this.alm_ty == 4" class="popup__inner" :style="{ marginLeft: myMargin, marginTop: myMargin }">
      <div class="popup__title">
        <div class="popup__icon"></div>
        <span>{{ this.alm_ntf_ti | moment('YYYY-MM-DD HH:mm:ss') }}</span>
      </div>
      <div class="popup__body">
        <p class="popup__body--large"> {{ this.message }} </p>
        <div class="popup__wrap" :style=" { maxHeight: containerHeight + 'px', overflowY: containerHeight > 200 ? 'auto' : 'hidden' }">
          <ul style="padding-right: 10px;">
            <li v-for="item in this.ctr_list" :key="item.tag_sn">
                {{ item.tag_sn }} <span>{{ item.tag_dp }}</span>의 값을 현재 <span>{{ item.tag_cmp_val}}</span> 에서 <span>{{ item.tag_val }}</span>(으)로 변경하려고 합니다.
            </li>
          </ul>
          </div>
        </div>
        <div class="popup__bottom">
          <div class="btn btn--cancel" @click="closePopup()">취소</div>
          <div class="btn btn--change" @click="control()">제어</div>
        </div>
      </div>
    </div>
</template>
<script>
import { PUT_ALARM_CONTROL, PUT_ALARM_ALM_CTR_HIS, PUT_ALARM_CANCEL, PUT_ALARM_CONFIRM } from '@/store/modules/alarm/alarm'
import { DEL_NOTIFY } from '@/store/modules/alarm/alarm'
import router from '@/router'
export default {
  data() {
    return {
      containerHeight: 200 // 팝업 높이 설정
    // timeLeft: 60,
    }
  },
  props: [
    'index',
    'seq',
    'alm_ty',
    'alm_id',
    'alm_ntf_ti',
    'url',
    'message',
    'ctr_list',
    'isVisible'
  ],
  computed: {
    // 최대 30개까지만 반복
    myMargin: function () {
      return ((this.index % 30)* 10) + 'px'
    },
    myZIndex: function () {
      return 199 - this.index
    }
  },
  methods: {
    closePopup: function () {
      // this.$store.dispatch('dialog/' + CLOSE_ALARM_NOTIFY_DIALOG)
      if(this.alm_ty === 2 || this.alm_ty === 4) { // 제어알람, 임계치 알람 일시 History 내역 저장
        if (this.$store.state.login.user.tkn === null) {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '로그인이 필요한 기능입니다' })
        } else {
          this.$store.dispatch(PUT_ALARM_ALM_CTR_HIS, { ctrList : this.ctr_list, almSeq : this.seq, ctrYn : 'N' })
          this.$store.dispatch(PUT_ALARM_CANCEL, {alm_id: this.alm_id, alm_ntf_ti: this.alm_ntf_ti})
          this.$store.commit(DEL_NOTIFY, this.seq)
        }
      } else if(this.alm_ty === 3){ //alm,ctr데이터 ctr flag 변경
        let _url = (this.url.indexOf('http') > -1 || this.url.indexOf('https') > -1) ? this.url : 'http://'+this.url
        let _host = 'waio-portal-vip:38080'
        // 동일 호스트
        if (_url.includes(_host)) {
          this.$store.dispatch(PUT_ALARM_CANCEL, {alm_id: this.alm_id, alm_ntf_ti: this.alm_ntf_ti})
        }
        this.$store.commit(DEL_NOTIFY, this.seq)
      } else {
        this.$store.commit(DEL_NOTIFY, this.seq)
      }
    },
    goToURL: function () {
      let _url = (this.url.indexOf('http') > -1 || this.url.indexOf('https') > -1) ? this.url : 'http://'+this.url
      this.$store.commit(DEL_NOTIFY, this.seq)
      let _host = 'waio-portal-vip:38080'
      // 동일 호스트
      if (_url.includes(_host)) {
        router.push(_url.split(_host)[1])
        this.$store.dispatch(PUT_ALARM_CONFIRM, {alm_id: this.alm_id, alm_ntf_ti: this.alm_ntf_ti})
      // 다른 호스트
      } else {
        if (this.$store.state.login.user.tkn !== null) {
          window.open(_url +'?token=' + this.$store.state.login.user.tkn, "_self")
        } else {
          window.open(_url, "_self")
        }
      }
    },
    control: function () {
      if (this.$store.state.login.user.tkn === null) {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '로그인이 필요한 기능입니다' })
      } else {
        this.$store.dispatch(PUT_ALARM_CONTROL, {alm_id: this.alm_id, alm_ntf_ti: this.alm_ntf_ti})
        this.$store.dispatch(PUT_ALARM_ALM_CTR_HIS, { ctrList : this.ctr_list, almSeq : this.seq, ctrYn : 'Y' })
        this.$store.commit(DEL_NOTIFY, this.seq)
      }
    },
    updateContainerHeight() {
      const contentHeight = /* 여기에서 컨텐츠의 높이를 가져오는 로직 */
      this.containerHeight = Math.min(contentHeight, 200); // 최대 200px로 제한
    },
    mounted() {
    // 페이지가 로드되거나 컨텐츠가 업데이트될 때마다 호출
    this.updateContainerHeight();
    },
  },
  created: function () {
    console.log('created AlarmNotifyDialog ' + this.index)
  },
  mounted: function() {
    console.log('mounted AlarmNotifyDialog ' + this.index)
    // setTimeout(() => {
    //   this.isVisible = false
    // }, this.timeLeft * 1000)
    // TTS
    // if (this.index === 0) {
    //   this.$speak(this.message)
    // }
    this.$speak(this.message)
  },
  updated: function () {
    console.log('updated AlarmNotifyDialog ' + this.index)
    
  },
  destroyed: function() {
    console.log('destoryed AlarmNotifyDialog ' + this.index)
  },
  // watch: {
  //   timeLeft(newValue) {
  //     if (newValue <= 0) {
  //       this.isVisible = false
  //     }
  //   }
  // }
}
</script>
<style lang="scss" scoped>
.popup-type3{
  position: absolute;
  top: 433px;
  left: 728px;
  &__date {
    width: 100%;
    text-shadow: 0 0 9px #5cafff;
    font-family: KHNPHUotfR;
    font-size: 15px;
    color: #fff;
    margin: 0 auto;
    text-align: center;
  }
  &__outter{
    display: flex;
    align-items: center;
    justify-content: center;
    width: 400px;
    // height: 221px;
    background-image: url('../../assets/dialog/background-type3.png');
    background-size: 100% 100%;
    // padding: 25px;
  }
  &__box {
    display: flex;
    flex-direction: column;
    width: 370px;
    // height: 194px;
    background-image: url('../../assets/dialog/background-box-type3.png');
    background-size: 100% 100%;
    margin: 10px;
  }
  &__title {
    width: 100%;
    // display: flex;
    // align-items: center;
    padding: 10px 15px;
    font-size: 24px;
    color: #b4dffb;
    text-align: center;
  }
  &__icon {
    width: 75px;
    height: 75px;
    margin: 0 auto;
    background-image: url('../../assets/dialog/ai_header_icon_type3.png');
    background-size: 100% 100%;
  }
  &__body {
    width: 100%;
    // padding: 10px 15px;
    display: flex;
    flex-direction: column;
    margin: 0 auto;
    color: #fff;
    &--large {
      font-size: 20px;
    }
    font-family: KHNPHUotfR;
    font-size: 24px;
    color: #b4dffb;
  }
  &__bottom {
    display:flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto;
    padding: 10px 15px;
  }
}
.popup {
  position: absolute;
  top: 350px;
  left: 50%;
  transform: translateX(-50%);
  &__inner {
    width: 100%;
    max-width: 1100px;
    height: 100%;
    background-image: url('../../assets/dialog/big-background.png');
    padding: 25px;
    background-size: 100% 100%;
  }
  &__title {
    display: flex;
    align-items: center;
    padding: 15px 20px;
    font-size: 24px;
    color: #b4dffb;
  }
  &__icon {
    width: 17px;
    height: 28px;
    margin-right: 15px;
    background-image: url('../../assets/dialog/ai_header_icon.png');
    background-size: 100% 100%;
  }
  &__body {
    padding: 10px 15px;
    font-size: 16px;
    color: #fff;
    &--large {
      font-size: 20px;
      color: #b4dffb;
      margin-bottom: 0;
      padding-left: 40px;
    }
  }
  &__bottom {
    display:flex;
    align-items: center;
    justify-content: flex-end;
    padding: 10px 15px;
  }
  &__wrap {
    overflow-y: auto !important;
    ul > li {
      margin-bottom: 5px;
      > span {
        color: #b4dffa;
      }
    }
  }
}
.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 105px;
  height: 34px;
  font-size: 15px;
  color: white;
  margin: 0px 3px;
  cursor: pointer;
  &--cancel {
    border: solid 1px #b4dffa;
    background-color: rgba(185, 255, 250, 0.25);
  }
  &--change {
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.25);
  }
}
*::-webkit-scrollbar {
    width: 7px;
}
*::-webkit-scrollbar-track {
    background-color: #011527;
}
*::-webkit-scrollbar-thumb {
    background-color: #417db9;
    border-radius: 3.5px;
}
</style>