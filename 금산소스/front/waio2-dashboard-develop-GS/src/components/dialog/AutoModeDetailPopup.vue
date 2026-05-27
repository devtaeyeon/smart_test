<template>
  <div v-if="this.$store.state.dialog.autoModeDetailPopup.visible" class="main">
    <div class="popup-main">
      <div class="popup-contents">
        <div class="top">
          <div class="top__img"></div>
          <div class="top__title">교반강도(G)자동모드 상세</div>
          <div class="top__exit-btn" @click="closePopup()"></div>
        </div>         
        <div class="top-title">보정 계수</div>
        <div class="value-contents">
          <div class="value-contents__wrap">
            <span>· #1</span>
            <div class="value-contents__num">
              <input type="text" :value="this.$store.state.mixing.latestModify.d_g_step1_crt" v-on:input="updateInput($event, 'd_g_step1_crt')">
            </div>
            <span>배</span>
          </div>
          <div class="value-contents__wrap">
            <span>· #2</span>
            <div class="value-contents__num">
              <input type="text" :value="this.$store.state.mixing.latestModify.d_g_step2_crt" v-on:input="updateInput($event, 'd_g_step2_crt')">
            </div>
            <span>배</span>
          </div>
        </div>
        <div class="btn-wrap">
          <button class="btn-wrap__save" @click="updateCrt()">저장</button>
          <button class="btn-wrap__cancel" @click="closePopup()">취소</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script> 
import { CLOSE_AUTO_MODE_DETAIL_POPUP } from '@/store/modules/dialog'
import { PUT_MIXING_G_CRT_CONTROL_AI } from '@/store/modules/mixing'
//TODO: import indMixing
export default {
  data: () => ({
  }),
  computed: {
  },
  methods: {
    updateInput: function (event, key) {
      this.$store.state.mixing.latestModify[key] = event.target.value
    },
    updateCrt: function() {      
      let crt_min = 0 
      let crt_max = 10
        if (this.$store.state.mixing.latestModify.d_g_step1_crt === ''
          || this.$store.state.mixing.latestModify.d_g_step2_crt === '') {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
        } else if (parseFloat(this.$store.state.mixing.latestModify.d_g_step1_crt) < crt_min || parseFloat(this.$store.state.mixing.latestModify.d_g_step1_crt) > crt_max
          || parseFloat(this.$store.state.mixing.latestModify.d_g_step2_crt) < crt_min || parseFloat(this.$store.state.mixing.latestModify.d_g_step2_crt) > crt_max) {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '보정계수 설정 범위', text2: crt_min + ' ~ ' + crt_max })
        } else {
          let obj = {}
          obj.d_g_step1_crt= parseFloat(this.$store.state.mixing.latestModify.d_g_step1_crt).toFixed(2)
          obj.d_g_step2_crt= parseFloat(this.$store.state.mixing.latestModify.d_g_step2_crt).toFixed(2)
          this.$store.dispatch(PUT_MIXING_G_CRT_CONTROL_AI, obj)
          this.$store.state.mixing.latest.d_g_step1_crt = parseFloat(this.$store.state.mixing.latestModify.d_g_step1_crt).toFixed(2)
          this.$store.state.mixing.latest.d_g_step2_crt = parseFloat(this.$store.state.mixing.latestModify.d_g_step2_crt).toFixed(2)
          this.$store.commit('dialog/' + CLOSE_AUTO_MODE_DETAIL_POPUP)
        }
    },
    closePopup: function () {
      this.$store.commit('dialog/' + CLOSE_AUTO_MODE_DETAIL_POPUP)
      this.$store.state.mixing.isModifyMode = false
      this.$store.state.mixing.latestModify = Object.assign({}, this.$store.state.mixing.latest)
    },
  },
  created: function () {
  },
}

</script>
<style lang="scss" scoped>
.main{
  position: absolute;
  top: -85px;
  left: 0;
  z-index: 200;
  width: 100%;
  height: 1190px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(30,37,61,0.8);
  .popup-main{
    display: flex;
    width: 450px;
    height: 410px;
    justify-content: center;
    align-items: center;
    // background-image: url('../../assets/sedimentation/popup_main.png');
    .popup-contents{
      position: relative;
      width: 100%;
      height: 100%;
      background-image: url('../../assets/gs_images/pump-bg.png');
      background-size: 100% 100%;
      padding: 30px 25px;
      .top-title{
        height: 53px;
        margin-top: 25px;
        background-image: url('../../assets/sedimentation/bottom_title_img.png');
        text-shadow: 0 0 9px #5cafff;
        font-size: 18px;
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        background-position: 50%;
        &__num {
          font-family: "LAB디지털" !important;
          font-size: 32px;
          color: #b4dffb;
          margin-left: 20px;
          vertical-align: middle;
        }
      }
      .value-contents {
        width: 250px;
        margin: 30px auto;
        &__wrap {
          display: flex;
          justify-content: center;
          align-items: center;
          margin-bottom: 20px;
          > span {
            color: #fff;
          }
        }
        &__num {
          display: flex;
          align-items: center;
          justify-content: center;
          border: solid 1px rgba(157, 191, 255, 0.3);
          width: 85px;
          margin: 0 20px;
          text-align: center;
          > span {
            color: #fff;
          }
          > input {
            width: 100px;
            // height: 30px;
            color: #c3eaff;
            text-align: center;
            padding-right: 5px;
            outline: none;
            }
          }
      }
      .btn-wrap {
        display: flex;
        justify-content: center;
        > button {
          width: 85px;
          height: 38px;
          margin: 0 3px;
        }
        &__save {
          background-color: #457fbc;
          color: #fff;
        }
        &__cancel {
          border: 1px solid #457fbc;
          color: #b4dffb;
        }
      }
      .top{
        display: flex;
        width: 100%;
        height: 30px;
        &__img{
          width: 19px;
          height: 30px;
          background-image: url('../../assets/sedimentation/top_title_img.png');
        }
        &__title{
          margin-left: 10px;
          font-size: 24px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 1.5;
          letter-spacing: normal;
          text-align: left;
          color: #b4dffb;
        }
        &__exit-btn{
          margin-left: auto;
          width: 24px;
          height: 30px;
          background-image: url('../../assets/sedimentation/exit_btn.png');
          background-position-y: center;
          cursor: pointer;
          z-index: 9;
        }
      }  
    }
  }
}
</style>