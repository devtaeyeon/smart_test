<template>
  <div class="right-contents">
    <div class="ai-contents">
      <div class="ai-contents__only-brain-img" :class="{ 'blink': isAiOn }"></div>
      <div class="ai-contents__brain-img">AI 유입 밸브 개도</div>
      <div class="ai-contents__line"></div>
      <!-- <div class="ai-contents__title">생활</div> -->
      <div class="ai-contents__value">
        <div class="value-num">{{ 100 | numFormat('0.00') }}</div>
        <div class="value-unit">%</div>
      </div>
      <!-- <div class="ai-contents__title-text">AI 펌프 가동대수</div>
      <div class="ai-contents__line"></div>
      <div class="ai-contents__title">공업</div>
      <div class="ai-contents__value">
        <div class="value-num">{{ this.$store.state.receiving.latest.ai_b_pump_cnt_dashboard | numFormat('0') }}</div>
        <div class="value-unit">대</div>
      </div> -->
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
// 최상위 엘리먼트
.right-contents {
  width:165px;
  height: 100%;
  margin-top: 70px;
  // 인공지능 결과 컨텐츠
  .ai-contents{
    position: relative;
    margin: 0 auto;
    width: 143px;
    height: 391px;
    background-image: url('../../../assets/dashboard_img/ai_agglomeration.png');
    background-repeat: no-repeat;
    background-position: center;
    // 뇌 이미지
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
    // 사람 이미지
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
    &__title-text{
      position: relative;
      text-shadow: 0 0 9px #5cafff;
      font-size: 16px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2.5;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      margin-top: 10px;
    }
    // 사람 이미지
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
    // 사람 이미지
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
    // 구분선
    &__line {
      margin: 0 auto;
      width: 121px;
      height: 10px;
      background-image: url('../../../assets/dashboard_img/ai_contents_line.png');
    }
    // 제목(1계열, 2계열)
    &__title{
      margin: 12px 0 5px 12px;
      text-shadow: 0 0 9px #5cafff;
      font-size: 16px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1;
      letter-spacing: normal;
      text-align: left;
      color: #c3eaff;
    }
    // 값
    &__value{
      display: flex;
      width: 100%;
      height: 45.5px;
      background-image: linear-gradient(91deg, rgba(67, 109, 132, 0) 0%, #2d67a2 54%, rgba(68, 97, 137, 0) 100%);
      // 숫자
      .value-num{
        width: 100px;
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
      // 단위
      .value-unit {
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
      }
    }
  }
}
// 깜빡이는 애니메이션
.blink {
  animation: blink 1s infinite alternate;
}
// 깜빡이는 키프레임
@keyframes blink {
  0% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}
</style>