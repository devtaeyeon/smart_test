<template>
  <div class="popup-info">
    <div class="popup-top-edge"></div>
    <div class="popup-bottom-edge"></div>
    <div class="popup-contents">
      <div class="popup-contents__img"></div>
      <div class="popup-text">
        <div class="popup-text__title">착수</div>
        <div class="popup-text__underbar"></div>
        <div class="popup-data">
          <div class="popup-data__text">· 원수 유입 유량</div>
          <div class="popup-data__value">{{ this.$store.state.receiving.latest.b_in_fr_i | numFormat('0,0') }}<span>m<sup>3</sup>/h</span></div>
        </div>
        <div class="popup-data">
          <div class="popup-data__text">· 원수 유입 압력</div>
          <div class="popup-data__value">{{ this.$store.state.receiving.latest.b_in_pr | numFormat('0.00') }}<span>㎏/cm<sup>3</sup></span></div>
        </div>
        <!-- <div class="popup-data">
          <div class="popup-data__text">· 1계열 원수 유입 밸브 개도</div>
          <div class="popup-data__value">{{ this.$store.state.receiving.latest.b1_vv_po | numFormat('0.00') }}<span>%</span></div>
        </div>
        <div class="popup-data">
          <div class="popup-data__text">· 2계열 원수 유입 밸브 개도</div>
          <div class="popup-data__value">{{ this.$store.state.receiving.latest.b2_vv_po | numFormat('0.00') }}<span>%</span></div>
        </div> -->
        <div class="popup-data">
          <div class="popup-data__text">· 정수지 수위#1</div>
          <div class="popup-data__value">{{ this.$store.state.receiving.latest.h_location_le1 | numFormat('0.00') }}<span>m</span></div>
        </div>
        <div class="popup-data">
          <div class="popup-data__text">· 정수지 수위#2</div>
          <div class="popup-data__value">{{ this.$store.state.receiving.latest.h_location_le2 | numFormat('0.00') }}<span>m</span></div>
        </div>
        <div class="popup-data">
          <div class="popup-data__text">· 정수지 수위#3</div>
          <div class="popup-data__value">{{ this.$store.state.receiving.latest.h_location_le3 | numFormat('0.00') }}<span>m</span></div>
        </div>
        <!-- <div class="popup-data">
          <div class="popup-data__text">· 정수지 수위#4</div>
          <div class="popup-data__value">{{ this.$store.state.receiving.latest.h_location_le4 | numFormat('0.00') }}<span>m</span></div>
        </div> -->
        <div class="popup-data">
          <div class="popup-data__text">· 정수지 유출 유량</div>
          <div class="popup-data__value">{{ this.$store.state.receiving.latest.h_out_fr | numFormat('0,0') }}<span>m<sup>3</sup>/h</span></div>
        </div>        
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 최상위 엘리먼트
.popup-info {
  z-index: 100;
  position: fixed;
  bottom: 11px;
  right: 432px;
  width: 511px;
  box-shadow: 0 0 10px 0 #accfff;
  background-color: rgb(19, 47, 78);
  animation: move-up-building 1s ease-in-out 0s normal backwards;
  // 위쪽 엣지
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
  // 아래쪽 엣지
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
  // 실제 내용을 담고 있는 컨텐츠
  .popup-contents{
    display: flex;
    width: 100%;
    height: 100%;
    // 공정 이미지
    &__img{
      width: 230px;
      background-image: url('../../../assets/dashboard_img/popup/icon1.png');
      background-position: center;
      background-repeat: no-repeat;
    }
    // 공정 이름
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
      // 측정항목별 제목 & 값
      .popup-data{
        display: flex;
        width: 320px;
        margin: 2px 0;
        font-size: 16px;
        &__text{
          width: 60%;
        }
        &__value > span {
          margin-left: 5px;
          font-size: 12px;
        }
      }
    }
  }
}
// 아래서 위로 올라오는 애니메이션
@keyframes move-up-building {
  0% {
    transform: translateY(380px)
  }
  100% {
    transform: translateY(0px)
  }
}
</style>