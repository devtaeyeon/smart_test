<template>
  <div class="main-container" style="margin-top: 54px">
    <div class="main-container__header">
      <div class="header-container">
        <div class="header-container__title">
          모의 결과
        </div>
      </div>
    </div>
    <div class="main-container__body">
      <div class="box-container">
        <div class="box-container__header">
          약품 종류
        </div>
        <div class="box-container__contents">
          <div class="simul-result-input-box">
            <div class="simul-result-input-box__label">1계열</div>
            <div class="simul-result-input-box__value">{{ (this.$store.state.coagulants.simulation.ai_c1_cf_coagulant === null || this.$store.state.coagulants.simulation.ai_c1_cf_coagulant === '') ? '--' : this.$store.state.coagulants.simulation.ai_c1_cf_coagulant.toUpperCase() }}</div>
          </div>
          <div class="simul-result-input-box">
            <div class="simul-result-input-box__label">2계열</div>
            <div class="simul-result-input-box__value">{{ (this.$store.state.coagulants.simulation.ai_c2_cf_coagulant === null || this.$store.state.coagulants.simulation.ai_c2_cf_coagulant === '') ? '--' : this.$store.state.coagulants.simulation.ai_c2_cf_coagulant.toUpperCase() }}</div>
          </div>
        </div>
      </div>
      <div class="box-container__divider">
      </div>
      <div class="box-container">
        <div class="box-container__header">
          약품 주입률
        </div>
        <div class="box-container__contents">
          <div class="simul-result-input-box">
            <div class="simul-result-input-box__label">1계열</div>
            <div class="simul-result-input-box__value simul-result-input-box__value--number">
              {{ (this.$store.state.coagulants.simulation.ai_c1_cf === null || this.$store.state.coagulants.simulation.ai_c1_cf === '') ? '--' : toDecimalPoint(this.$store.state.coagulants.simulation.ai_c1_cf) }}
              <div class="simul-result-input-box__unit">ppm</div>
            </div>
          </div>
          <div class="simul-result-input-box">
            <div class="simul-result-input-box__label">2계열</div>
            <div class="simul-result-input-box__value simul-result-input-box__value--number">
              {{ (this.$store.state.coagulants.simulation.ai_c2_cf === null || this.$store.state.coagulants.simulation.ai_c2_cf === '') ? '--' : toDecimalPoint(this.$store.state.coagulants.simulation.ai_c2_cf) }}
              <div class="simul-result-input-box__unit">ppm</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import numeral from 'numeral'
export default {
  name: 'ResultComponent',
  data: () => ({
  }),
  methods: {
    toDecimalPoint (val) {
      return numeral(val).format('0.[00]')
    }
  }
}
</script>


<style lang="scss" scoped>
.main-container {
  display: flex;
  flex-direction: column;
  &__header {
    // display: flex;
    // flex: 1;
  }
  &__body {
    display: flex;
    // flex-direction: column;
    align-items: center;
    justify-content: center;
    // margin: 4px auto 0px auto;
    width: 495px;
    height: 320px;
    background-image: url('../../../assets/coagulants/simulation/body_background_result.png');
    background-size: 100% 100%;
    background-repeat: no-repeat;
    background-position: center;
  }
}
.header-container {
  display: flex;
  width: 501px;
  height: 51px;
  background-image: url('../../../assets/coagulants/simulation/header_container.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-position: center;
  &__title {
    display: flex;
    flex: 1;
    align-items: center;
    font-size: 18px;
    color: #fff;
    padding-left: 38px;
  }
  &__button-container {
    display: flex;
    margin-left: auto;
  }
  &__button-inner-container {
    display:flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 4px;
  }
}
.box-container {
  width: 207px;
  height: 272px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  &__header {
    width: 207px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-image: linear-gradient(89deg, rgba(2, 18, 47, 0) 0%, #18396c 26%, #18396c 78%, rgba(2, 20, 52, 0) 100%);
    font-size: 16px;
    color: #fff;
  }
  &__contents {
    display: flex;
    flex: 1;
    flex-direction: column;
    justify-content: space-between;
    padding-top: 32px;
  }
  &__divider {
    width: 7px;
    height: 298px;
    background-image: url('../../../assets/coagulants/simulation/simul_result_divider.png');
    background-size: 100% 100%;
    margin: 0px 18px;
  }
}
.simul-result-input-box {
  display: flex;
  flex-direction: column;
  &__label {
    display: flex;
    font-size: 16px;
    color: #c3eaff;
    align-items: center;
    &:before {
      content:"";
      display:inline-block;
      width: 47px;
      height: 34px;
      background-image: url('../../../assets/coagulants/simulation/simul_result_label_arrow.png');
      background-size: 47px 34px;
    }
  }
  &__value {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 207px;
    height: 42px;
    font-size: 19px;
    color: #c3eaff;
    border: solid 1px #9dbfff;
    &--number {
      font-size: 24px;
      font-family: "LAB디지털" !important;
    }
  }
  &__unit {
    position: absolute;
    right: 10px;
    width: 26px;
    // margin-left: auto;
    font-size: 12px;
    color: #417db9;
  }
}
</style>