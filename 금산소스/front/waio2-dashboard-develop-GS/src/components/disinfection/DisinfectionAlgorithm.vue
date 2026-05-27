<template>
  <div class="main">
    <div class="top-center">
      <!-- <div class="water-v-flow-one-img one">
        <div class="buble-v delay1"></div>
        <div class="buble-v delay2"></div>
        <div class="buble-v delay3"></div>
      </div>
      <div class="water-v-flow-one-img two">
        <div class="buble-v delay1"></div>
        <div class="buble-v delay2"></div>
        <div class="buble-v delay3"></div>
      </div>
      <div class="water-v-flow-one-img three">
        <div class="buble-v delay1"></div>
        <div class="buble-v delay2"></div>
        <div class="buble-v delay3"></div>
      </div> -->
      <div class="top-center__contents">
        <TopNavigator/>
      </div>
    </div>
    <div class="top">
      <div class="title">소독<p> 세부 현황</p></div>
      <div class="right">
        <div class="right-contents">
          <div class="right-contents__text-first">AI 운전 모드</div>
          <div class="right-contents__btn-first">
            <div v-if="$store.state.disinfection.disinfectionStep == 1 " class="control_box_operation">
              <div v-if="$store.state.disinfection.latest.pre_ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(2)">AI</div>
              <div v-if="$store.state.disinfection.latest.pre_ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(1)">AI추천</div>
              <div v-if="$store.state.disinfection.latest.pre_ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(0)">AI분석</div>
            </div>
            <div v-if="$store.state.disinfection.disinfectionStep == 2 " class="control_box_operation">
              <div v-if="$store.state.disinfection.latest.peri_ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(2)">AI</div>
              <div v-if="$store.state.disinfection.latest.peri_ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(1)">AI추천</div>
              <div v-if="$store.state.disinfection.latest.peri_ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(0)">AI분석</div>
            </div>
            <div v-if="$store.state.disinfection.disinfectionStep == 3 " class="control_box_operation">
              <div v-if="$store.state.disinfection.latest.post_ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(2)">AI</div>
              <div v-if="$store.state.disinfection.latest.post_ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(1)">AI추천</div>
              <div v-if="$store.state.disinfection.latest.post_ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(0)">AI분석</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="contents">
      <div class="contents__left">
        <DisinfectionLeftContentsPre v-if="this.$store.state.disinfection.disinfectionStep === 1"/>
        <DisinfectionLeftContentsMid v-if="this.$store.state.disinfection.disinfectionStep === 2"/>
        <DisinfectionLeftContentsAfter v-else-if="this.$store.state.disinfection.disinfectionStep === 3"/>
      </div>
      <div class="contents__right">
        <DisinfectionRightContentsPre v-if="this.$store.state.disinfection.disinfectionStep === 1"/>
        <DisinfectionRightContentsMid v-if="this.$store.state.disinfection.disinfectionStep === 2"/>
        <DisinfectionRightContentsAfter v-if="this.$store.state.disinfection.disinfectionStep === 3"/>
      </div>
    </div>
  </div>  
</template>
<script>
import DisinfectionLeftContentsPre from '@/components/disinfection/DisinfectionLeftContentsPre' 
import DisinfectionRightContentsPre from '@/components/disinfection/DisinfectionRightContentsPre'
import DisinfectionLeftContentsMid from '@/components/disinfection/DisinfectionLeftContentsMid'
import DisinfectionRightContentsMid from '@/components/disinfection/DisinfectionRightContentsMid'
import DisinfectionLeftContentsAfter from '@/components/disinfection/DisinfectionLeftContentsAfter' 
import DisinfectionRightContentsAfter from '@/components/disinfection/DisinfectionRightContentsAfter'
import { SET_OVERLAY } from '@/store'
import { GET_DISINFECTION_LATEST, PUT_DISINFECTION_HISTORY_CHOLRATE, PUT_DISINFECTION_AI_G_CONSUMPTION } from '@/store/modules/disinfection'
// import { PUT_SEDIMENTATION_HISTORY_CL } from '@/store/modules/sedimentation'
// import { PUT_CLEAR_HISTORY_CL} from '@/store/modules/clear'
import TopNavigator from '@/components/core/TopNavigator'
import { OPEN_AI_MODE_DIALOG } from '@/store/modules/dialog'
export default {
  name:'DisinfectionAlgorithm',
  data: () => ({
    intervalTime: 1000 * 60
  }),
  components: {
    DisinfectionLeftContentsPre,
    DisinfectionLeftContentsAfter,
    DisinfectionLeftContentsMid,
    DisinfectionRightContentsMid,
    DisinfectionRightContentsPre,
    DisinfectionRightContentsAfter,
    TopNavigator
  },
  methods: {
    onClickAICheckbox: function(expectedValue) {
      if( this.$store.state.login.user.tkn !== null ) {
        // this.$store.state.selectedBuildingIndex = index
        // this.$store.state.dialog.aiMode.disinfectionIndex = disinfectionIndex
        this.$store.dispatch('dialog/' + OPEN_AI_MODE_DIALOG, expectedValue)
      }
    },
    getInitialDisinfectionData: function(){
      Promise.all([
        this.$store.dispatch(GET_DISINFECTION_LATEST),
        // this.$store.dispatch(PUT_DISINFECTION_HISTORY_EVAPORATION),
        // this.$store.dispatch(PUT_SEDIMENTATION_HISTORY_CL),
        this.$store.dispatch(PUT_DISINFECTION_HISTORY_CHOLRATE),
        // this.$store.dispatch(PUT_CLEAR_HISTORY_CL),
        this.$store.dispatch(PUT_DISINFECTION_AI_G_CONSUMPTION)
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    
      this.timer = setInterval(() => {
        this.$store.dispatch(GET_DISINFECTION_LATEST),
        // this.$store.dispatch(PUT_DISINFECTION_HISTORY_EVAPORATION),
        // this.$store.dispatch(PUT_SEDIMENTATION_HISTORY_CL)
        this.$store.dispatch(PUT_DISINFECTION_HISTORY_CHOLRATE),
        // this.$store.dispatch(PUT_CLEAR_HISTORY_CL),
        this.$store.dispatch(PUT_DISINFECTION_AI_G_CONSUMPTION)
      }, this.intervalTime)
    }
  },
  mounted: function () {
    switch(this.$store.state.disinfection.disinfectionStep){
      case 1:
        this.$store.state.selectedBuildingIndex = 7.1
        break
      case 2:
        this.$store.state.selectedBuildingIndex = 7.2
        break
      case 3:
        this.$store.state.selectedBuildingIndex = 7.3
        break
    }
    this.$store.commit(SET_OVERLAY, true)
    this.getInitialDisinfectionData()
  },
  watch:{
    '$store.state.disinfection.disinfectionStep' : function (newVal, oldVal) {
        if (newVal !== oldVal) {
          switch(newVal){
            case 1:
              this.$store.state.selectedBuildingIndex = 7.1
              break
            case 2:
              this.$store.state.selectedBuildingIndex = 7.2
              break
            case 3:
              this.$store.state.selectedBuildingIndex = 7.3
              break
          }
          clearInterval(this.timer)
          this.$store.commit(SET_OVERLAY, true)
          this.getInitialDisinfectionData()
      }
    },
  },
  destroyed: function () {
    clearInterval(this.timer)
  }
}
</script>
<style lang="scss" scoped>
.one-value{
  top:75px;
  left: 483px;
}
.two-value{
  top:75px;
  left: 816px;
}
.three-value{
  top:75px;
  left: 1098px;
}
.four-value{
  top:188px;
  left: 503px;
}
.value-box{
  position: absolute;
  display: flex;
  align-items: center;
  &__title{
    text-shadow: 0 0 9px #5cafff;
    font-size: 10px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.1;
    letter-spacing: normal;
    text-align: center;
    color: #96c4ff;
  }
  &__img-one{
    width: 35px;
    height: 38px;
    background-image: url('../../assets/disinfection/one_circle.png');
  }
  &__img-two{
    width: 35px;
    height: 38px;
    background-image: url('../../assets/disinfection/two_circle.png');
  }
  &__value{
    text-shadow: 0 0 9px #5cafff;
    font-size: 10px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.1;
    letter-spacing: normal;
    text-align: center;
    color: #fff;
  }
}
// .one{
//   top: 76px;
//   left: 378px;
// }
// .two{
//   top: 76px;
//   left: 1014px;
// }
// .three{
//   top: 76px;
//   left: 1351px;
// }
// .water-v-flow-one-img{
//   position: absolute;
//   width: 6px;
//   height: 50px;
//   .buble-v {
//     position: absolute;
//     width: 6px;
//     height: 41px;
//     background-image: url('../../assets/disinfection/v_water_flow.png');
//     background-position:50% -28px;
//     animation-name: arrow-two;
//     animation-duration: 4s;
//     animation-timing-function: linear;
//     animation-iteration-count: infinite;
//     opacity: 0;
//   }
//   .delay1 {
//     animation-delay: 0s;
//   }
//   .delay2 {
//     animation-delay: 2s;
//   }
//   .delay3 {
//     animation-delay: 4s;
//   }
// }
// @keyframes arrow-two{ 
//   0% {opacity:0; transform: translateY(0px);}
//   20% {opacity:0; }
//   90% {opacity:1; }
//   100% {opacity:0; transform: translateY(50px);}
// }
.bottom{
  display: flex;
  width: 100%;
  height: 392px;
}
.contents{
  display: flex;
  width: 100%;
  height: 794px;
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
.control_box_operation {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 140px;
  height: 28px;
  padding: 0px 1px;
  border-radius: 14px;
  background-color: rgba(139, 194, 240, 0.25);
  &__btn {
    height: 22px;
    color: #19274e;
    font-size: 11px;
    margin: 0px 1px;
    padding: 4px 10px 4px 10px;
    border-radius: 11px;
    cursor: pointer;
    &--on {
      box-shadow: 0 0 6px 0 #e8faff;
      background-color: #b4dffa;
    }
    &--off {
      background-color: #417290;
    }
  }
}
</style>