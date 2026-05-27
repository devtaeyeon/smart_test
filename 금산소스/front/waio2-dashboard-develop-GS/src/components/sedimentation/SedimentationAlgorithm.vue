<template>
  <div class="main">
    <!-- Top 공정 네이게이터 -->
    <div class="top-center">
      <div class="top-center__contents">
        <TopNavigator/>
      </div>
    </div>
    <!-- Top 제목, 운전모드, EMS 제어 -->
    <div class="top">
      <div class="title">침전<p>전체 현황</p></div>
      <div class="right">
        <div class="right-contents">
          <div class="right-contents__text-first">AI 운전 모드</div>
          <div class="right-contents__btn-first">
            <div class="control_box_operation">
              <div v-if="$store.state.sedimentation.latest.ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(4, 2)">AI</div>
              <div v-if="$store.state.sedimentation.latest.ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(4, 1)">AI추천</div>
              <div v-if="$store.state.sedimentation.latest.ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(4, 0)">AI분석</div>
            </div>
          </div>
        </div>
        <!-- <div class="right-contents">
          <div class="right-contents__text-second">EMS 운전 모드</div>
          <div class="right-contents__btn-second">
            <input type="checkbox" class="checkbox">
          </div>
        </div> -->
      </div>
    </div>
    <div class="contents">
      <!-- 중앙 컨텐츠 -->
      <SedimentationContents/>
    </div>
    <div class="bottom">
      <!-- 하단 컨텐츠 -->
      <SedimentationHighchart/>
    </div>
  </div>  
</template>
<script>
import SedimentationContents from '@/components/sedimentation/SedimentationContents' 
import SedimentationHighchart from '@/components/sedimentation/SedimentationHighchart' 
// import { SET_OVERLAY } from '@/store'
// import { GET_SEDIMENTATION_LATEST, PUT_SEDIMENTATION_INTERFACE } from '@/store/modules/sedimentation'
// import { GET_COAGULANT_LATEST } from '@/store/modules/coagulants'
import { OPEN_AI_MODE_DIALOG } from '@/store/modules/dialog'
import TopNavigator from '@/components/core/TopNavigator'

export default {
  name:'SedimentationAlgorithm',
  data: () => ({
    timer: null // API 요청 타이머
  }),
  components: {
    SedimentationContents,
    SedimentationHighchart,
    TopNavigator
  },
  methods: {
    /**
     * AI 운전모드 클릭시
     * 운전모드 변경 Dialog 오픈
     * 
     * @param index 공정index
     * @expectedValue 변경할 운전모드
     */
    onClickAICheckbox: function(index, expectedValue) {
      if( this.$store.state.login.user.tkn !== null ) {
        this.$store.state.selectedBuildingIndex = index
        this.$store.dispatch('dialog/' + OPEN_AI_MODE_DIALOG, expectedValue)
      }
    }
  },
  /**
   * 마운트시 실행되는 함수
   * 침전 공정에 필요한 API를 주기적으로 요청
   */
  mounted: function() {
    // this.$store.state.selectedBuildingIndex = 4
    // this.$store.commit(SET_OVERLAY, true)
    // Promise.all([
    //   this.$store.dispatch(GET_SEDIMENTATION_LATEST),
    //   this.$store.dispatch(GET_COAGULANT_LATEST),
    //   this.$store.dispatch(PUT_SEDIMENTATION_INTERFACE, { numJi: 0 })
    // ]).finally(() => {
    //   this.$store.commit(SET_OVERLAY, false)
    // })
    
    // this.timer = setInterval(() => {
    //   this.$store.dispatch(GET_SEDIMENTATION_LATEST),
    //   this.$store.dispatch(GET_COAGULANT_LATEST),
    //   this.$store.dispatch(PUT_SEDIMENTATION_INTERFACE, { numJi: 0 })
    // }, 60 * 1000)
  },
  // 마운트 해제시 API 요청 타이머 해제
  destroyed: function () {
    clearInterval(this.timer)
  }
}
</script>
<style lang="scss" scoped>
.bottom{
  display: flex;
  width: 100%;
  height: 425px;
  // margin-top: 70px;
}
.contents{
  display: flex;
  width: 100%;
  height: 430px;
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
  .title{
    width: 230px;
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
}
</style>