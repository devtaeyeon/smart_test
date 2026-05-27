<template>
  <v-dialog
    v-model="$store.state.disinfection.dialog.pre.visible"
    width="700"
  >
    <div class="popup-main">
      <div class="user-modify-container">전처리</div>
      <div class="table-total">
        <div class="table-content">
          <div class="table-content__title"></div>
          <div class="table-content__value1" style="font-size: 20px;">1계열</div>
          <div class="table-content__value2" style="font-size: 20px;">2계열</div>
          <div class="table-content__sign"></div>
        </div>
        <div class="table-content">
          <div class="table-content__title">혼화수 잔류염소</div>
          <div class="table-content__value1">{{ this.tmpData[46] | numFormat('0.[00]')}}</div>
          <div class="table-content__value2">?</div>
          <div class="table-content__sign">ppm</div>
        </div>
        <div class="table-content">
          <div class="table-content__title">혼화수 pH</div>
          <div class="table-content__value1">{{ this.tmpData[47] | numFormat('0.[00]')}}</div>
          <div class="table-content__value2">?</div>
          <div class="table-content__sign"></div>
        </div>
        <div class="table-content">
          <div class="table-content__title">혼화지 유입유량</div>
          <div class="table-content__value1">{{ this.tmpData[48] | numFormat('0.[00]')}}</div>
          <div class="table-content__value2">{{ this.tmpData[49] | numFormat('0.[00]')}}</div>
          <div class="table-content__sign">m<sup>3</sup>/h</div>
        </div>
        <div class="table-content">
          <div class="table-content__title">목표 잔류염소 농도</div>
          <div class="table-content__value1">{{ this.tmpData[44] | numFormat('0.[00]')}}</div>
          <div class="table-content__value2">?</div>
          <div class="table-content__sign">ppm</div>
        </div>
        <div class="table-content">
          <div class="table-content__title">현재 주입률</div>
          <div class="table-content__value1">{{ this.tmpData[50] | numFormat('0.[00]')}}</div>
          <div class="table-content__value2">{{ this.tmpData[51] | numFormat('0.[00]')}}</div>
          <div class="table-content__sign">ppm</div>
        </div>
        <div class="table-content">
          <div class="table-content__title">체류시간</div>
          <div class="table-content__value1">?</div>
          <div class="table-content__value2">?</div>
          <div class="table-content__sign">시간</div>
        </div>
        <div class="table-content">
          <div class="table-content__title">기온</div>
          <div class="table-content__value1">?</div>
          <div class="table-content__value2">?</div>
          <div class="table-content__sign">°C</div>
        </div>
      </div>
      </div>
  </v-dialog>
</template>
<script>
export default {
  name: 'PretreatmentDialog',
  props: [
    'tmpData'
  ],
}
</script>
<style scoped lang="scss">
.popup-main{
  /* width: 824px; */
  // height: 388px;
  margin: auto;
  border-radius: 10px;
  background-color: #0f3272;
  padding: 11px 0px 14px 0;
}
.user-modify-container {
 font-size: 24px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2;
  letter-spacing: normal;
  text-align: left;
  color: #ffffff;
  height: 62px;
  padding: 7px 0 0 19.7px;
  background-image: linear-gradient(to right, rgba(0, 9, 81, 0.56), rgba(0, 7, 79, 0));
}
.modify-contents {
  font-size: 15px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: left;
  color: #ffffff;
  line-height: 2.4;
  padding:18px 12.3px
}
.table-total{
  padding-top: 10px;
  padding-bottom: 10px;
  // height: 280px;
  &__text {
    font-size: 26px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2;
    letter-spacing: normal;
    text-align: center;
    color: #00ebff;
    margin-bottom: 22.6px;
  }
}
.table-content {
  display: flex;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  line-height: 2;
  &__title{
    margin-left: 40px;
    font-size: 20px;
    text-align: left;
    color: #ffffff;
  }
  &__value1 {
    width: 120px;
    // height: 22px;
    margin-left: auto;
    // margin-left: 50px;
    line-height: 1.6;
    font-size: 22px;
    color: #ffffff;
    text-align:right;
  }
  &__value2 {
    width: 120px;
    // height: 22px;
    margin-right: 20px;
    line-height: 1.6;
    font-size: 22px;
    color: #ffffff;
    text-align:right;
  }
  &__sign {
    width: 90px;
    height: 18px;
    font-size: 18px;
    color: #95cffa;
  }
}
</style>