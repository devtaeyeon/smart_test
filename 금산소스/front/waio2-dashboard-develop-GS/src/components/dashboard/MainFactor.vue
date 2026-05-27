<template>
  <div class="factor-main">
    <div class="contents">
      <div class="content">
        <div class="content__text-box">원수탁도<br/>(NTU)</div>
        <div class="content__value-box">{{ this.$store.state.coagulants.latest.b_tb | numFormat('0.00') }}</div>
      </div>
      <div class="content">
        <div class="content__text-box">정수탁도<br/>(NTU)</div>
        <div class="content__value-box">{{ this.$store.state.disinfection.latest.h_tb | numFormat('0.00') }}</div>
      </div>            
    </div>
    <div class="contents">
      <div class="content">
        <div class="content__text-box">원수유입유량<br/>(m<sup>3</sup>/h)</div>
        <div class="content__value-box" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.coagulants.latest.b_in_fr)"></div>
      </div>
      <div class="content">
        <div class="content__text-box">정수유출유량<br/>(m<sup>3</sup>/h)</div>
        <!-- 20260306 이현수 조영우 과장 변경 요청 금산+진안 통합 유출유량으로 변경 -->
        <!--<div class="content__value-box" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.coagulants.latest.h_out_fr)"></div>-->
        <div class="content__value-box" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.coagulants.latest.h_out_fr_total)"></div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'MainFactor'
}
</script>

<style scoped lang="scss">
// 최상위 엘리먼트
.factor-main {
  position: absolute;
  left: 170px;
  top: 17px;
  // content를 담는 컨테이너
  .contents {
    display: flex;
    justify-content: left;
    height: 69px;
    margin-top: 5px;
    margin-left: 50px;
    // content
    .content {
      display: flex;
      height: 100%;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      letter-spacing: normal;
      // 측정 항목 이름
      &__text-box {
        width: 90px;
        height: 69px;
        background-image: url('../../assets/dashboard_icons/box-bottom.png');
        background-position-y: bottom;
        text-shadow: 0 0 9px #5cafff;
        font-family: KHNPHUotfR;
        font-size: 16px;
        line-height: 1.06;
        text-align: center;
        color: #fff;
        padding-top: 24px;
      }
      // 측정 항목 값
      &__value-box {
        width: 79px;
        height: 79px;
        background-image: url('../../assets/dashboard_icons/value_box.png');
        background-position: center;
        text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
        font-family: 'LAB디지털' !important;
        font-size: 18px;
        line-height: 4.3;
        text-align: center;
        color: #f2fbff;
      }
    }
  }
}
</style>