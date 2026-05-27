<template>
  <div class="right-contents">
    <div class="ai-contents">
      <div class="ai-contents__only-brain-img" :class="{ 'blink': isAiOn }"></div>
      <div class="ai-contents__brain-img">AI 운영 예측</div>
      <div class="ai-contents__line"></div>
      <div class="ai-contents__title">사용지수</div>
      <div class="ai-contents__value">
        <div class="value-num">{{ this.$store.state.gac.latest.ai_i_opr_cnt }}</div>
        <div class="value-unit">지</div>
      </div>
      <div class="top_px"></div>
      <div class="ai-contents__title">다음 역세 지</div>
      <div class="ai-contents__value">
        <div class="value-num">{{ this.$store.getters['gac/getMinAiILocationBwStartTi'].minIndex }}</div>
        <div class="value-unit">지</div>
      </div>
      <div class="ai-contents__title">역세 시작시간</div>
      <div class="ai-contents__value">
        <div class="value-num">{{ this.$store.getters['gac/getMinAiILocationBwStartTi'].minVal }}</div>
        <div class="value-unit">시간 후</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: [
    'isAiOn'
  ]
}
</script>
<style scoped lang="scss">
.blink {
  animation: blink 1s infinite alternate;
}
@keyframes blink {
  0% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}
.right-contents {
  width:165px;
  height: 100%;
  margin-top: 70px;
  .time-count {
    display: flex;
    height: 47px;
    margin: 14px 0 0 65px;
    margin-bottom: 14px;
    &__text{
      width: 58px;
      padding-left: 10px;
      text-shadow: 0 0 9px #5cafff;
      font-family: 'LAB디지털' !important;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2.1;
      letter-spacing: normal;
      text-align: center;
      color: #b4dffa
    }
    &__img{
      width: 43px;
      height: 43px;
      background-image: url('../../../assets/dashboard_img/timer_img.png');
    }
  }
  .ai-contents{
    position: relative;
    margin: 0 auto;
    width: 143px;
    height: 391px;
    background-image: url('../../../assets/dashboard_img/ai_agglomeration.png');
    background-repeat: no-repeat;
    background-position: center;
    // padding: 0 10px;
    &__only-brain-img{
      position: absolute;
      left: 45px;
      top: 23px;
      width: 58px;
      height: 45px;
      background: url('../../../assets/dashboard_img/brain.png');
      background-repeat: no-repeat;
      background-position: 50% 40%;
      background-size: 100%;
      z-index: 1;
    }
    &__brain-img{
      position: relative;
      height: 150px;
      text-shadow: 0 0 9px #5cafff;
      font-size: 16px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 16;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
    }
    &__brain-img::before{
      content: "";
      width: 87px;
      height: 116px;
      margin: 0 auto;
      background: url('../../../assets/dashboard_img/human.png');
      background-repeat: no-repeat;
      background-position: center 6px;
      background-size: 110% 110%;
      opacity: 0.5;
      top: 0;
      left: 0;
      bottom: 0;
      right: 0;
      position: absolute;
    }
    &__brain-img::after{
      content: "";
      width: 87px;
      height: 116px;
      margin: 0 auto;
      background: url('../../../assets/dashboard_img/human.png');
      background-repeat: no-repeat;
      background-position: center 6px;
      background-size: 110% 110%;
      opacity: 1;
      top: 0;
      left: 0;
      bottom: 0;
      right: 0;
      position: absolute;
    }
    &__line {
      margin: 0 auto;
      width: 121px;
      height: 10px;
      background-image: url('../../../assets/dashboard_img/ai_contents_line.png');
    }
    &__title{
      margin: 5px 0 10px 12px;
      text-shadow: 0 0 9px #5cafff;
      font-size: 16px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.06;
      letter-spacing: normal;
      text-align: left;
      color: #c3eaff;
    }
    &__value{
      display: flex;
      width: 100%;
      height: 45.5px;
      background-image: linear-gradient(91deg, rgba(67, 109, 132, 0) 0%, #2d67a2 54%, rgba(68, 97, 137, 0) 100%);
      .value-num{
        width: 90px;
        text-shadow: 0 0 9px #5cafff;
        font-family: "LAB디지털" !important;
        font-size: 30px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.5;
        letter-spacing: normal;
        text-align: right;
        color: #fff;
      }
      .value-unit {
        flex: 1;
        font-family: "KHNPHUotfR";
        font-size: 14px;
        text-shadow: 0 0 9px #5cafff;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 4;
        letter-spacing: normal;
        text-align: center;
        color: #fff;
        margin-left: 10px;
        margin-left: 5px;
      }
    }
  }
}
</style>