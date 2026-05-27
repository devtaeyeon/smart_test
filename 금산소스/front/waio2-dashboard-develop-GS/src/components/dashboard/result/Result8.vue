<template>
  <div class="right-contents">
    <div class="ai-contents">
      <div class="ai-contents__only-brain-img" :class="{ 'blink': isAiOn }"></div>
      <div class="ai-contents__brain-img">계통별 펌프 상태</div>
      <div class="ai-contents__line"></div>
      <div class="ai-contents__title">송산계통</div>
      <div class="ai-contents__value">
        <div class="value-sign-start"></div>
        <div class="value-sign"></div>
        <div class="value-sign"></div>
      </div>
      <div class="no-background">
        <div class="value-text">1호기</div>
        <div class="value-text">2호기</div>
        <div class="value-text">3호기</div>
      </div>
      <div class="top_px"></div>
      <div class="ai-contents__title">평택계통</div>
      <div class="ai-contents__value">
        <div class="value-sign-start"></div>
        <div class="value-sign"></div>
        <div class="value-sign"></div>
      </div>
      <div class="no-background">
        <div class="value-text">1호기</div>
        <div class="value-text">2호기</div>
        <div class="value-text">3호기</div>
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
      margin: 16px 0 10px 12px;
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
    .no-background{
      display: flex;
      justify-content: center;
      align-content: center;
      width: 100%;
      height: 20px;
      // background-image: linear-gradient(91deg, rgba(67, 109, 132, 0) 0%, #2d67a2 54%, rgba(68, 97, 137, 0) 100%);
      margin-left: 2px;
      margin-top: 3px;
      .value-text{
        width: 40px;
        text-shadow: 0 0 9px #5cafff;
        font-size: 12px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.06;
        letter-spacing: normal;
        text-align: center;
        color: #c3eaff;
      }
    }
    &__value{
      display: flex;
      justify-content: center;
      align-content: center;
      width: 100%;
      height: 40px;
      background-image: linear-gradient(91deg, rgba(67, 109, 132, 0) 0%, #2d67a2 54%, rgba(68, 97, 137, 0) 100%);
      margin-left: 2px;
      .value-sign-start{
        width: 14px;
        height: 14px;
        margin-top: 15px;
        -o-object-fit: contain;
        object-fit: contain;
        border-radius: 14px;
        box-shadow: 0 0 6px 0 #5cafff;
        background-color: #ea6464;
      }
      .value-sign{
        width: 14px;
        height: 14px;
        // margin: auto;
        margin-top: 15px;
        margin-left: 25px;
        -o-object-fit: contain;
        object-fit: contain;
        border-radius: 14px;
        box-shadow: 0 0 6px 0 #5cafff;
        background-color: #ea6464;
      }
      .value-text{
        width: 38px;
        text-shadow: 0 0 9px #5cafff;
        font-size: 12px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.06;
        letter-spacing: normal;
        text-align: center;
        color: #c3eaff;
      }
      .value-hz{
        width: 40px;
        text-shadow: 0 0 9px #5cafff;
        font-size: 10px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 2;
        text-align: center;
        letter-spacing: normal;
        color: #c3eaff;
      }
    }
  }
}
</style>