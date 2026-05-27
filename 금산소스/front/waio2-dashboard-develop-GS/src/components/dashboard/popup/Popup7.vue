<template>
  <div class="popup-info">
    <div class="popup-top-edge"></div>
    <div class="popup-bottom-edge"></div>
    <div class="popup-contents">
      <div class="popup-contents__img"></div>
      <div class="popup-text">
        <div class="popup-text__title">소독</div>
        <div class="popup-text__underbar"></div>
        <div class="popup-data">
          <div class="popup-data__text">· 수온</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.b_te | numFormat('0.00') }}<span>℃</span></div>
        </div>
        <!-- <div class="popup-data">
          <div class="popup-data__text">· 1계열 혼화지 유입 유량</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.d1_in_fr | numFormat('0,0') }}<span>m<sup>3</sup>/h</span></div>
        </div>
        <div class="popup-data">
          <div class="popup-data__text">· 2계열 혼화지 유입 유량</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.d_ser_in_fr | numFormat('0,0') }}<span>m<sup>3</sup>/h</span></div>
        </div> -->
        <!-- <div class="popup-data">
          <div class="popup-data__text">· 1계열 혼화수 잔류염소</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.d1_cl | numFormat('0.00') }}<span>ppm</span></div>
        </div>
        <div class="popup-data">
          <div class="popup-data__text">· 2계열 혼화수 잔류염소</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.e_ser_cl | numFormat('0.00') }}<span>ppm</span></div>
        </div> -->
        <!-- <div class="popup-data">
          <div class="popup-data__text">· 정수지 pH</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.h_ph | numFormat('0.00') }}</div>
        </div> -->
        <div class="popup-data">
          <div class="popup-data__text">· 원수 유입 유량</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.b_in_fr | numFormat('0.00') }}</div>
          <span style="width: 36px; font-size: 12px;">m<sup>3</sup>/h</span>
        </div>
        <!-- <div class="popup-data">
          <div class="popup-data__text">· 정수지 탁도</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.h_tb | numFormat('0.00') }}<span>NTU</span></div>
        </div> -->
        <div class="popup-data">
          <div class="popup-data__text">· 혼화지 잔류염소</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.g_d_residual_cl  | numFormat('0.00') }}<span>ppm</span></div>
        </div>
        <!-- <div class="popup-data">
          <div class="popup-data__text">· 정수지 유출 잔류염소</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.h_out_cl | numFormat('0.00') }}<span>ppm</span></div>
        </div> -->
        <div class="popup-data">
          <div class="popup-data__text">· 현재 주입률</div>
          <div class="popup-data__value">{{ this.$store.state.disinfection.latest.g_pre_chol_rate | numFormat('0.00') }}</div>
          <span style="width: 36px; font-size: 12px;">ppm</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.popup-info {
  z-index: 100;
  position: fixed;
  bottom: 11px;
  left: 30px;
  width: 511px;
  box-shadow: 0 0 10px 0 #accfff;
  background-color: rgb(19, 47, 78);
  animation: move-up-building 1s ease-in-out 0s normal backwards;
  .popup-top-edge{
    position: absolute;
    top:1px;
    margin: 4px;
    width: 98%;
    height: 11px;
    background-image: url('../../../assets/dashboard_img/dashboard_popup_topbar.png');
    background-size: 100%;
    background-position: center;
  }
  .popup-bottom-edge{
    position: absolute;
    bottom:1px;
    margin: 4px;
    width: 98%;
    height: 11px;
    background-image: url('../../../assets/dashboard_img/dashboard_popup_underbar.png');
    background-size: 100%;
    background-position: center;
  }
  .popup-contents{
    display: flex;
    width: 100%;
    height: 100%;
    &__img{
      width: 230px;
      background-image: url('../../../assets/dashboard_img/popup/icon8.png');
      background-position: center;
      background-repeat: no-repeat;
    }
    .popup-text{
      padding: 45px 0;
      width: 320px;
      height: 100%;
      text-shadow: 0 0 9px #5cafff;
      font-family: KHNPHUotfR;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      letter-spacing: normal;
      text-align: left;
      color: #fff;
      &__title{
        width: 100%;
        font-size: 20px;
        line-height: 1.1;
      }
      &__underbar{
        margin: 6px 0;
        width: 232px;
        height: 10px;
        background-image: url('../../../assets/dashboard_img/popup_underbar.png');
      }
      .popup-data{
        display: flex;
        width: 320px;
        margin: 2px 0;
        font-size: 16px;
        &__text{
          width: 75%;
        }
        &__value > span {
          margin-left: 5px;
          font-size: 12px;
        }
      }
    }
  }
}
@keyframes move-up-building {
  0% {
    transform: translateY(480px)
  }
  100% {
    transform: translateY(0px)
  }
}
</style>