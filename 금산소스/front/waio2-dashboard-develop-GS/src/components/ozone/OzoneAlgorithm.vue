<template>
  <div class="main">
    <div class="top-center">
      <div class="top-center__contents">
        <TopNavigator/>
      </div>
    </div>
    <div class="top">
      <div class="title">오존 세부 현황</div>
      <div class="right">
        <div class="right-contents">
          <div class="right-contents__text-first">AI 운전 모드</div>
          <div class="right-contents__btn-first">
            <div class="control_box_operation">
              <div v-if="$store.state.ozone.latest.ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(11, 2)">AI</div>
              <div v-if="$store.state.ozone.latest.ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(11, 1)">AI추천</div>
              <div v-if="$store.state.ozone.latest.ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(11, 0)">AI분석</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="contents">
      <div class="contents__left">
        <OzoneLeftContents/>
      </div>
      <div class="contents__right">
        <OzoneRightContents/>
      </div>
    </div>
  </div>  
</template>
<script>
import OzoneLeftContents from '@/components/ozone/OzoneLeftContents' 
import OzoneRightContents from '@/components/ozone/OzoneRightContents'
import { GET_OZONE_LATEST, PUT_OZONE_HISTORY_INJECTION, PUT_OZONE_HISTORY_DE } from '@/store/modules/ozone'
import { SET_OVERLAY } from '@/store'
import { OPEN_AI_MODE_DIALOG } from '@/store/modules/dialog'
import TopNavigator from '@/components/core/TopNavigator'
export default {
  name:'OzoneAlgorithm',
  components: {
    OzoneLeftContents,
    OzoneRightContents,
    TopNavigator
  },
  methods: {
    onClickAICheckbox: function(index, expectedValue) {
      if( this.$store.state.login.user.tkn !== null ) {
        this.$store.state.selectedBuildingIndex = index
        this.$store.dispatch('dialog/' + OPEN_AI_MODE_DIALOG, expectedValue)
      }
    },
  },
  mounted: function() {
    this.$store.state.selectedBuildingIndex = 11
    this.$store.commit(SET_OVERLAY, true)
    Promise.all([
      this.$store.dispatch(GET_OZONE_LATEST),
      this.$store.dispatch(PUT_OZONE_HISTORY_INJECTION),
      this.$store.dispatch(PUT_OZONE_HISTORY_DE)
    ]).finally(() => {
      this.$store.commit(SET_OVERLAY, false)
    })
    
    this.timer = setInterval(() => {
      this.$store.dispatch(GET_OZONE_LATEST),
      this.$store.dispatch(PUT_OZONE_HISTORY_INJECTION),
      this.$store.dispatch(PUT_OZONE_HISTORY_DE)
    }, 60 * 1000)
  },
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
  height: 794px;
  padding: 0 40px 0 86px;
}
.top-center{
  display: flex;
  justify-content: center;
  position: absolute;
  top:-76px;
  left: 159px;
  width: 1692px;
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
    width: 315px;
    height: 100%;
    background-image: url('../../assets/percolation/main_title.png');
    text-shadow: 0 0 9px #5cafff;
    font-family: "KHNPHUotfR";
    font-size: 30px !important;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2;
    letter-spacing: normal;
    text-align: left;
    color: #fff;
    margin-left: 55px;
    margin-top: 35px;
    background-position-x: right;
  }
}
</style>