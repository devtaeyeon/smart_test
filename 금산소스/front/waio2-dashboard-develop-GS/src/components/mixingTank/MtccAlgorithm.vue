<template>
  <div class="main">
    <!-- Top 공정 네이게이터 -->
    <div class="top-center">
      <div class="top-center__contents">
        <TopNavigator/>
      </div>
    </div>
    <!-- Top 제목, 운전모드 -->
    <div class="top">
      <div class="title">혼화응집 <p>세부 현황</p></div>
      <div class="right">
        <div class="right-contents">
          <div class="right-contents__text-first">AI 운전 모드</div>
          <div class="right-contents__btn-first">
            <div class="control_box_operation">
              <div v-if="$store.state.mixing.latest.ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(3, 2)">AI</div>
              <div v-if="$store.state.mixing.latest.ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(3, 1)">AI추천</div>
              <div v-if="$store.state.mixing.latest.ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(3, 0)">AI분석</div>
            </div>
          </div>
        </div>
        <!-- g값 상/하한 -->
        <div class="g-value">
          <div class="g-value__title">
            <div class="g-value__text"></div>
            <div class="g-value__text">상한<span>(S<sup>-1</sup>)</span></div>
            <div class="g-value__text">하한<span>(S<sup>-1</sup>)</span></div>
          </div>
          <div class="g-value__num">
            <div class="g-value__text">#1</div>
            <div class="g-value__input-box">
              <input v-if="this.$store.state.mixing.isGLimitModifyMode" type="text" :value="this.$store.state.mixing.latestModify.d_g_step1_max" v-on:input="updateInput($event, 'd_g_step1_max')">
              <span v-else class="g-value__text-num">{{ this.$store.state.mixing.latestModify.d_g_step1_max | numFormat('0.0')}}</span>
            </div>
            <div class="g-value__input-box">
              <input v-if="this.$store.state.mixing.isGLimitModifyMode" type="text" :value="this.$store.state.mixing.latestModify.d_g_step1_min" v-on:input="updateInput($event, 'd_g_step1_min')">
              <span v-else class="g-value__text-num">{{ this.$store.state.mixing.latestModify.d_g_step1_min | numFormat('0.0')}}</span>
            </div>
          </div>
          <div class="g-value__num">
            <div class="g-value__text">#2</div>
            <div class="g-value__input-box" :class="{'': this.$store.state.mixing.isGLimitModifyMode }">
              <input v-if="this.$store.state.mixing.isGLimitModifyMode" type="text" :value="this.$store.state.mixing.latestModify.d_g_step2_max" v-on:input="updateInput($event, 'd_g_step2_max')">
              <span v-else class="g-value__text-num">{{ this.$store.state.mixing.latestModify.d_g_step2_max| numFormat('0.0')}}</span>
            </div>
            <div class="g-value__input-box">
              <input v-if="this.$store.state.mixing.isGLimitModifyMode" type="text" :value="this.$store.state.mixing.latestModify.d_g_step2_min" v-on:input="updateInput($event, 'd_g_step2_min')">
              <span v-else class="g-value__text-num">{{ this.$store.state.mixing.latestModify.d_g_step2_min | numFormat('0.0')}}</span>
            </div>
          </div>
          <div v-if="$store.state.login.user.tkn !== null" class="right-contents__icon">
            <div class="custom-icon" @click="updateControl">
              <div :class="[ this.$store.state.mixing.isGLimitModifyMode ? 'custom-icon__checkbox' : 'custom-icon__pencil' ]"></div>
            </div>
            <div v-if="this.$store.state.mixing.isGLimitModifyMode" class="custom-cancel-icon" @click="cancelControl">
              <div class='custom-cancel-icon__cancel'></div>
            </div>
          </div>
        </div>           
      </div>
    </div>
    <div class="contents">
      <!-- 혼화응집 공정 이미지 -->
      <div class="contents__left">
        <MtccLeftContents/>
      </div>
      <!-- 혼화응집 주요인자, AI응집기 설정 속도 예측, 차트 -->
      <div class="contents__right">
        <MtccRightContents/>
      </div>
    </div>
  </div>  
</template>
<script>
import MtccLeftContents from '@/components/mixingTank/MtccLeftContents' 
import MtccRightContents from '@/components/mixingTank/MtccRightContents' 
import { SET_OVERLAY } from '@/store'
import { GET_MIXING_LATEST, PUT_MIXING_HISTORY_FC_SP, PUT_MIXING_G_LIMIT_CONTROL_AI } from '@/store/modules/mixing'
import { PUT_RAW_HISTORY_TE } from '@/store/modules/raw'
import { OPEN_AI_MODE_DIALOG } from '@/store/modules/dialog'
import TopNavigator from '@/components/core/TopNavigator'
export default {
  name:'MtccAlgorithm',
  components: {
    MtccLeftContents,
    MtccRightContents,
    TopNavigator
  },
  methods: {
    /**
     * AI운전모드 변경시 
     * AI운전모드 확인 Dialog 오픈
     * 
     * @param index 공정 index
     * @param expectedValue 변경하고자 하는 운전모드
     */
    onClickAICheckbox: function(index, expectedValue) {
      if( this.$store.state.login.user.tkn !== null ) {
        this.$store.state.selectedBuildingIndex = index
        this.$store.dispatch('dialog/' + OPEN_AI_MODE_DIALOG, expectedValue)
      }
    },
    updateInput: function (event, key) {
      this.$store.state.mixing.latestModify[key] = event.target.value
    },
    updateControl: function() {      
      let g_min = 0 
      let g_max = 100
      if (this.$store.state.mixing.isGLimitModifyMode) {
        if (this.$store.state.mixing.latestModify.d_g_step1_max === ''
          || this.$store.state.mixing.latestModify.d_g_step1_min === ''
          || this.$store.state.mixing.latestModify.d_g_step2_max === ''
          || this.$store.state.mixing.latestModify.d_g_step2_min === '') {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
        } else if (parseFloat(this.$store.state.mixing.latestModify.d_g_step1_max) < g_min || parseFloat(this.$store.state.mixing.latestModify.d_g_step1_max) > g_max
          || parseFloat(this.$store.state.mixing.latestModify.d_g_step2_max) < g_min || parseFloat(this.$store.state.mixing.latestModify.d_g_step2_max) > g_max) {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: 'G값 설정 범위', text2: g_min + ' ~ ' + g_max })
        } else if (parseFloat(this.$store.state.mixing.latestModify.d_g_step1_min) < g_min || parseFloat(this.$store.state.mixing.latestModify.d_g_step1_min) > g_max
          || parseFloat(this.$store.state.mixing.latestModify.d_g_step2_min) < g_min || parseFloat(this.$store.state.mixing.latestModify.d_g_step2_min) > g_max) {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: 'G값 설정 범위', text2: g_min + ' ~ ' + g_max })
        } else {
          let obj = {}
          obj.d_g_step1_max= parseFloat(this.$store.state.mixing.latestModify.d_g_step1_max).toFixed(1)
          obj.d_g_step2_max= parseFloat(this.$store.state.mixing.latestModify.d_g_step2_max).toFixed(1)
          obj.d_g_step1_min= parseFloat(this.$store.state.mixing.latestModify.d_g_step1_min).toFixed(1)
          obj.d_g_step2_min= parseFloat(this.$store.state.mixing.latestModify.d_g_step2_min).toFixed(1)
          this.$store.dispatch(PUT_MIXING_G_LIMIT_CONTROL_AI, obj)
          this.$store.state.mixing.isGLimitModifyMode = !this.$store.state.mixing.isGLimitModifyMode
          this.$store.state.mixing.latest.d_g_step1_max = parseFloat(this.$store.state.mixing.latestModify.d_g_step1_max).toFixed(1)
          this.$store.state.mixing.latest.d_g_step1_min = parseFloat(this.$store.state.mixing.latestModify.d_g_step1_min).toFixed(1)
          this.$store.state.mixing.latest.d_g_step2_max = parseFloat(this.$store.state.mixing.latestModify.d_g_step2_max).toFixed(1)
          this.$store.state.mixing.latest.d_g_step2_min = parseFloat(this.$store.state.mixing.latestModify.d_g_step2_min).toFixed(1)
        }
      } else {
        this.$store.state.mixing.isGLimitModifyMode = !this.$store.state.mixing.isGLimitModifyMode
      }
    },
    
    cancelControl: function() {
      this.$store.state.mixing.latestModify = Object.assign({}, this.$store.state.mixing.latest)
      this.$store.state.mixing.isGLimitModifyMode = !this.$store.state.mixing.isGLimitModifyMode
    },
    
  },
  /**
   * 마운트시 실행되는 함수
   * 혼화응집에 필요한 API를 주기적으로 요청함
   */
  mounted: function() {
    this.$store.state.selectedBuildingIndex = 3
    this.$store.commit(SET_OVERLAY, true)
    Promise.all([
      this.$store.dispatch(GET_MIXING_LATEST),
      this.$store.dispatch(PUT_RAW_HISTORY_TE, 1),
      this.$store.dispatch(PUT_MIXING_HISTORY_FC_SP)
    ]).finally(() => {
      this.$store.commit(SET_OVERLAY, false)
    })
    
    this.timer = setInterval(() => {
      this.$store.dispatch(GET_MIXING_LATEST),
      this.$store.dispatch(PUT_RAW_HISTORY_TE, 1),
      this.$store.dispatch(PUT_MIXING_HISTORY_FC_SP)
    }, 60 * 1000)
  },
  /**
   * 마운트 해제시 
   * API 요청 타이머 해제
   */
  destroyed: function () {
    clearInterval(this.timer)
  }
}
</script>
<style lang="scss" scoped>
.bottom{
  display: flex;
  width: 100%;
  height: 392px;
}
.contents{
  display: flex;
  width: 100%;
  height: 822px;
  padding: 0 40px;
}
.top-center{
  display: flex;
  justify-content: center;
  position: absolute;
  top:-76px;
  left: 159px;
  width: 1585px;
  height: 249px;
  background-image: url('../../assets/splashdown/top_center_background.png');
  .timer{
    width: 72px;
    height: 72px;
    margin-top: 110px;
    margin-right: 14px;
  }
  &__contents{
    margin-top: 100px;
  }
}
.top{
  display: flex;
  width: 100%;
  height: 173px;
  .g-value {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
    width: 235px;
    &__title {
      width: 65px;
    }
    &__text {
      height: 25px;
      margin-bottom: 3px;
      text-align: center;
      color: #fff;
      > span {
        color: #6c9ed1;
        font-size: 12px;
        margin-left: 5px;
      }
    }
    &__text-num {
      text-align: center;
      color: #c3eaff;
    }
    &__num {
      width: 55px;
      display: flex;
      flex-direction: column;
      align-items: center;
    }
    &__input-box {
      width: 52px;
      height: 25px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 3px;
      > input {
        width: 52px;
        height: 24px;
        color: #c3eaff;
        text-align: right;
        padding-right: 5px;
        outline: none;
        border: solid 1px rgba(157, 191, 255, 0.3);
        // display: none;
      }
    }
  }       
  .title-down{
    position: absolute;
    width: 177px;
    height: 53px;
    background-image: url('../../assets/percolation/title_down.png');
    left: 35px;
    top: 167px;
    &__text{
      text-shadow: 0 0 9px #5cafff;
      font-size: 18px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 3;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
    }
    &__digital{
      text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
      font-family: "LAB디지털" !important;
      font-size: 24px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.17;
      letter-spacing: normal;
      text-align: center;
      color: #ccf1ff;
      margin: 0 5px;
    }
  }
  .title{
    width: 260px;
    background-position: 68px 25px;
    background-image: url('../../assets/as_images/main_title.png');
    letter-spacing: 0 !important;
    text-align:left;
    line-height: 1.5;
    font-weight: 600;
    height: 100%;
    text-shadow: 0 0 9px #5cafff;
    font-family: "KHNPHUotfR";
    font-size: 30px !important;
    font-stretch: normal;
    font-style: normal;
    color: #fff;
    margin-left: 55px;
    margin-top: 35px;
  }
  p {
    font-size: 24px;
    font-weight: 300;
  }
  .right-contents {
    &__icon {
      top: 86px;
      right: 30px;
    }
  }    
  // .right{
  //   width: 190px;
  //   margin-left: auto;
  //   margin-right: 29px;
  //   .right-contents{
  //     display: flex;
  //     width: 100%;
  //     margin-top: 20px;
  //     &__text-first{
  //       text-shadow: 0 0 9px #5cafff;
  //       font-family: "KHNPHUotfR";
  //       font-size: 18px;
  //       font-weight: normal;
  //       font-stretch: normal;
  //       font-style: normal;
  //       letter-spacing: normal;
  //       text-align: left;
  //       color: #c3eaff;
  //     }
  //     &__btn-first{
  //       width: 60px;
  //       height: 28px;
  //       margin-left: auto;
  //       .checkbox{
  //         position:relative;
  //         cursor:pointer;
  //         appearance:none;
  //         width:60px;
  //         height:28px;
  //         border-radius: 14px;
  //         border: solid 1px #417290;
  //         background-color: rgba(139, 194, 240, 0.25);
  //         outline:none;
  //         transition:0.3s;
  //       }
  //       .checkbox::before{
  //         content:"OFF";
  //         position:absolute;
  //         height:22px;
  //         width:29px;
  //         border-radius:11px;
  //         background:#b4dffa;
  //         top:2px;
  //         left:2px;
  //         transition:0.3s ease-in-out;
  //         font-size: 11px;
  //         font-family: KHNPHUotfR;
  //         font-weight: normal;
  //         font-stretch: normal;
  //         font-style: normal;
  //         line-height: 2;
  //         letter-spacing: normal;
  //         text-align: center;
  //         color: #19274e;
  //         background-color: rgba(122, 155, 175, 0.25);
  //       }
  //       .checkbox:checked::before{
  //         content:"AI";
  //         transform:translateX(25px);
  //         background:#b4dffa;
  //       }
  //       .checkbox:checked{
  //         border-color:#b4dffa;
  //       } 
  //     }
  //     &__text-second{
  //       text-shadow: 0 0 9px #5cafff;
  //       font-family: "KHNPHUotfR";
  //       font-size: 18px;
  //       font-weight: normal;
  //       font-stretch: normal;
  //       font-style: normal;
  //       letter-spacing: normal;
  //       text-align: left;
  //       color: #80b6ff;
  //     }
  //     &__btn-second{
  //       width: 60px;
  //       height: 28px;
  //       margin-left: auto;
  //       .checkbox{
  //         position:relative;
  //         cursor:pointer;
  //         appearance:none;
  //         width:60px;
  //         height:28px;
  //         border-radius: 14px;
  //         border: solid 1px #417290;
  //         background-color: rgba(139, 194, 240, 0.25);
  //         outline:none;
  //         transition:0.3s;
  //       }
  //       .checkbox::before{
  //         content:"OFF";
  //         position:absolute;
  //         height:22px;
  //         width:29px;
  //         border-radius:11px;
  //         background-color: rgba(122, 155, 175, 0.25);
  //         top:2px;
  //         left:2px;
  //         transition:0.3s ease-in-out;
  //         font-size: 11px;
  //         font-family: KHNPHUotfR;
  //         font-weight: normal;
  //         font-stretch: normal;
  //         font-style: normal;
  //         line-height: 2;
  //         letter-spacing: normal;
  //         text-align: center;
  //         color: #19274e;
  //       }
  //       .checkbox:checked::before{
  //         content:"AI";
  //         transform:translateX(25px);
  //         background:#80b6ff;
          
  //       }
  //       .checkbox:checked{
  //         border-color:#80b6ff;
  //       } 
  //     }
  //   }
  // }
}
</style>