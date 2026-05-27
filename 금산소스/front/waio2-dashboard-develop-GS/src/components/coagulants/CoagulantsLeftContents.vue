<template>
  <div class="main">
    <!-- <div class="btn-tab">
        <div class="btn-tab__box-abled" >생활</div>
    </div> -->
    <div class="contents-img">
      <p class="unit-title unit-one">1호기</p>           
      <!-- 1호기 물 흐름 -->
      <div class="apac-line" :style="{ display: this.getDisplayStyleC1 }">
        <div class="water-flow-vertical one-line-v1">
          <div class="buble-v delay1"></div>
          <div class="buble-v delay2"></div>
        </div>
        <div class="water-big-right-horizontal">
          <div class="buble delay1"></div>
          <div class="buble delay2"></div>
          <div class="buble delay3"></div>
          <div class="buble delay4"></div>
        </div>
        <div class="water-flow-vertical one-line-v3">
          <div class="buble-v delay3"></div>
          <div class="buble-v delay4"></div>
        </div>
      </div>
      <p class="unit-title unit-two">2호기</p>        
      <!-- 2호기 물 흐름 -->
      <div class="polymax-line" :style="{ display: this.getDisplayStyleC2 }">
        <div class="water-flow-vertical one-line-v2">
          <div class="buble-v delay1"></div>
          <div class="buble-v delay2"></div>
        </div>
        <div class="water-big-left-horizontal">
          <div class="buble delay1"></div>
          <div class="buble delay2"></div>
          <div class="buble delay3"></div>
          <div class="buble delay4"></div>
        </div>
        <div class="water-flow-vertical one-line-v4">
          <div class="buble-v delay3"></div>
          <div class="buble-v delay4"></div>
        </div>
      </div>
      <!-- <div class="one-line-v3" :class="[ this.getDisplayStyleC1 === 'block' ? 'water-flow-vertical-short-second' : '']">
        <div class="buble-v delay1"></div>
        <div class="buble-v delay2"></div>
      </div>
      <div class="one-line-v4" :class="[ this.getDisplayStyleC2 === 'block' ? 'water-flow-vertical-short-first' : '']">
        <div class="buble-v delay1"></div>
        <div class="buble-v delay2"></div>
      </div> -->
      <!-- 응집제 종류 -->
      <div class="line-box-top">
        <div class="line-box__title">응집제 종류:</div>
        <v-select outlined 
        :menu-props="{
          offsetY: true,
          nudgeBottom: 0
        }"
        :items="this.$store.state.coagulants.coagulantType" v-model="$store.state.coagulants.latestModify.c_cf_coagulant" :disabled="this.$store.state.login.user.tkn == null">
        </v-select>
        <button @click="updateCfControl" class="btn-save" v-show="this.$store.state.login.user.tkn !== null">저장</button>
      </div>
      <!-- 1계열 응집제 주입률, 주입량-->
      <div class="line-box oneline">
        <div class="line-box__title margintop">1호기 응집제 주입률</div>
        <div class="box-contents">
          <div class="box-contents__value">{{ this.$store.state.coagulants.latest.c1_cf | numFormat('0.00') }}</div>
          <div class="box-contents__unit">ppm</div>
        </div>
        <div class="line-box__title margintop">1호기 응집제 주입량</div>
        <div class="box-contents">
          <div class="box-contents__value">{{ this.$store.state.coagulants.latest.c1_mm_fr | numFormat('0.00') }}</div>
          <div class="box-contents__unit">ℓ/h</div>
        </div>
      </div>
      <!-- PACS 2호기 응집제 종류, 주입률, 주입량-->
      <div class="line-box twoline">
        <div class="line-box__title margintop">2호기 응집제 주입률</div>
        <div class="box-contents">
          <div class="box-contents__value">{{ this.$store.state.coagulants.latest.c2_cf | numFormat('0.00') }}</div>
          <div class="box-contents__unit">ppm</div>
        </div>
        <div class="line-box__title margintop">2호기 응집제 주입량</div>
        <div class="box-contents">
          <div class="box-contents__value">{{ this.$store.state.coagulants.latest.c2_mm_fr | numFormat('0.00') }}</div>
          <div class="box-contents__unit">ℓ/h</div>
        </div>
      </div>
    </div>
  </div>  
</template>
<script>
import { PUT_COAGULANT_AI_CF_COAGULANT } from '@/store/modules/coagulants'
export default {
  methods:{
    updateCfControl: function(){
      this.$store.dispatch(PUT_COAGULANT_AI_CF_COAGULANT, this.$store.state.coagulants.latestModify.c_cf_coagulant)
    },
  },
  computed: {
    // 첫번째 약품통 애니메이션 여부
    getDisplayStyleC1() {
        if (this.$store.state.coagulants.latest.c_injector1 == 1) {
          return 'block'
        }
      return 'none'
    },
    // 두번째 약품통 애니메이션 여부
    getDisplayStyleC2() {
      if (this.$store.state.coagulants.latest.c_injector2 == 1) {
          return 'block'
        }
      return 'none'
    }
  }
}
</script>
<style lang="scss" scoped>
.v-input {
  max-width: 155px;
  height: 35px !important;
  color: #417db9 !important;
  border-radius: 0;
  border: none;
}
.main{
  display: flex;
  flex-flow: column;
  align-items: center;
  width: 592px;
  height: 100%;
  .contents-img{
    width: 533px;
    height: 100%;
    background-image: url('../../assets/gs_images/sub_04.png');
    background-position-x: center;
    background-position-y: 100px;
    position: relative;
    // 타이틀 추가
    .unit-title {
      color: #fff;
      display: inline-block;
      position: absolute;
    }
    .unit-one {
      top: 110px;
      left: 107px;
    }
    .unit-two {
      top: 110px;
      left: 392px;
    }    
    .oneline{
      top: 630px;
      left: 52px;
    }
    .twoline{
      top: 630px;
      left: 341px;
    }
    .line-box-top {
      position: absolute;
      display: flex;
      justify-content: space-evenly;
      align-items: center;
      width: 310px;
      left: 50%;
      transform: translateX(-50%);
      top: 35px;
      .margintop{
        margin-top: 10px;
      }
      &__title{
        text-shadow: 0 0 9px #5cafff;
        font-size: 16px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 2.56;
        letter-spacing: normal;
        text-align: left;
        color: #fff;
      }
      //03.29 저장버튼 추가
      .btn-save {
        border: none;
        color: #fff;
        width: 55px;
        height: 35px;    
        background-color: #496097;
        font-size: 14px;
      }
      .btn-save:hover {        
        background-color: #4aa1ff;
        color: #fff;
          transition: .3s
        }
        .modify-button {
          position: absolute;
          top: 10px;
          left: 233px;
          cursor: pointer;
          display: flex;
          justify-content: space-between;
          width: 53px;
        }
        .btn-bg {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 24px;
          height: 24px;
          background-color: #b4dffa;
          border-radius: 12px;
      }
      .custom-icon {
        width: 24px;
        height: 24px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        border-radius: 12px;
        background-color: #b4dffa;
        cursor: pointer;
      &__pencil {
        width: 15px;
        height: 15px;
        background-image: url('../../assets/disinfection/icon_pencil.png');
      }
      &__checkbox {
        width: 16px;
        height: 12px;
        background-image: url('../../assets/disinfection/icon_checkbox.png');
        background-position-x: 1px;
       }
      }
      .custom-cancel-icon {
        width: 24px;
        height: 24px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        border-radius: 12px;
        background-color: #649fff;
        cursor: pointer;
          &__cancel {
            width: 16px;
            height: 16px;
            background-image: url('../../assets/disinfection/icon_cancel.png');
          }
      }
    }
    .line-box{
      position: absolute;
      display: flex;
      flex-flow: column;
      align-items: center;
      width: 136px;
      .margintop{
        margin-top: 10px;
      }
      &__title{
        text-shadow: 0 0 9px #5cafff;
        font-size: 16px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 2.56;
        letter-spacing: normal;
        text-align: left;
        color: #fff;
      }
      .box-contents{
        display: flex;
        width: 131px;
        height: 43px;
        border: solid 1px rgba(157, 191, 255, 0.3);
        &__value{
          text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
          font-family: "LAB디지털" !important;
          font-size: 24px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 1.8;
          letter-spacing: normal;
          text-align: left;
          color: #ccf1ff;
          padding-left: 10px;
        }
        &__unit{
          font-size: 16px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 3;
          letter-spacing: normal;
          text-align: right;
          color: #417db9;
          padding-right: 10px;
          margin-left: auto;
        }
        &__text{
          width: 100%;
          text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
          font-family: "LAB디지털" !important;
          font-size: 24px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 1.8;
          letter-spacing: normal;
          text-align: center;
          color: #ccf1ff;
        }
      }
    }
    .apac-line{
      position: absolute;
      top: 0px;
      left: 41px;
    }
    .polymax-line{
      position: absolute;
      top: 0px;
      left: 337px;
    }
    .one-line1{
      position: absolute;
      top:517px;
      left: 142px;
      width: 40px;
      height: 75px;
      background-image: url('../../assets/drugInjection/off_valve.png');
    }
    .one-line2{
      position: absolute;
      top:517px;
      left: 209px;
      width: 40px;
      height: 75px;
      background-image: url('../../assets/drugInjection/off_valve.png');
    }
    .one-line3{
      position: absolute;
      top:517px;
      left: 278px;
      width: 40px;
      height: 75px;
      background-image: url('../../assets/drugInjection/off_valve.png');
    }
    .two-line1{
      position: absolute;
      top:517px;
      left: 437px;
      width: 40px;
      height: 75px;
      background-image: url('../../assets/drugInjection/off_valve.png');
    }
    .two-line2{
      position: absolute;
      top:517px;
      left: 505px;
      width: 40px;
      height: 75px;
      background-image: url('../../assets/drugInjection/off_valve.png');
    }
    .two-line3{
      position: absolute;
      top:517px;
      left: 573px;
      width: 40px;
      height: 75px;
      background-image: url('../../assets/drugInjection/on_valve.png');
    }
    .one-line-h1{
      top: 491px;
      left: 163px;
    }
    .one-line-h2{
      top: 491px;
      left: 106px;
    }
    .one-line-h3{
      top: 610px;
      left: 183px;
    }
    .one-line-h4{
      top: 610px;
      left: 82px;
    }
    .one-line-v1{
      top: 343px;
      left: 81px;
    }
    .one-line-v2{
      top: 343px;
      left: 69px;
    }
    .one-line-v3{
      top: 387px;
      left: 224px;
    }
    .one-line-v4{
      top: 387px;
      left: -73px;
    }
    .two-line-h{
      top: 491px;
      left: 480px;
    }
    .two-line-v1{
      top: 440px;
      left: 571px;
    }
    .two-line-v2{
      top: 490px;
      left: 460px;
    }
    // 좌측 가로 물흐름
    .water-right-horizontal{
      position: absolute;
      width: 100px;
      height: 8px;
      .buble {
        position: absolute;
        width: 100px;
        height: 8px;
        background-image: url('../../assets/disinfection/water_h_flow_one.png');
        background-position: -38px 50%;
        animation-name: arrow-one;
        animation-duration: 5s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 0s;
      }
      .delay2 {
        animation-delay: 1s;
      }
      .delay3 {
        animation-delay: 2s;
      }
    }
    @keyframes arrow-one{ 
      0% {opacity:0; transform: translateX(0px);}
      20% {opacity:0; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateX(80px);}
    }
    .water-big-right-horizontal{
      position: absolute;
      width: 100px;
      height: 8px;
      top: 393px;
      left: 78px;
      .buble {
        position: absolute;
        width: 100px;
        height: 8px;
        background-image: url('../../assets/disinfection/water_h_flow_one.png');
        background-position: -38px 50%;
        animation-name: big-arrow-one;
        animation-duration: 8s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 2s;
      }
      .delay2 {
        animation-delay: 4s;
      }
      .delay3 {
        animation-delay:6s;
      }
      .delay4 {
        animation-delay: 8s;
      }
    }
    @keyframes big-arrow-one{ 
      0% {opacity:0; transform: translateX(0px);}
      20% {opacity:1; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateX(125px);}
    }
    // 우측 가로 물흐름
    .water-left-horizontal{
      position: absolute;
      width: 100px;
      height: 8px;
      .buble {
        position: absolute;
        width: 100px;
        height: 8px;
        background-image: url('../../assets/disinfection/water_h_flow_two.png');
        background-position: 88px 50%;
        animation-name: arrow-three;
        animation-duration: 6s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 0s;
      }
      .delay2 {
        animation-delay: 1s;
      }
      .delay3 {
        animation-delay: 2s;
      }
    }
    @keyframes arrow-three{ 
      0% {opacity:0; transform: translateX(0px);}
      20% {opacity:0; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateX(-80px);}
    }
    .water-big-left-horizontal{
      position: absolute;
      width: 100px;
      height: 8px;
      top: 393px;
      left: -13px;
      .buble {
        position: absolute;
        width: 100px;
        height: 8px;
        background-image: url('../../assets/disinfection/water_h_flow_two.png');
        background-position: 88px 50%;
        animation-name: big-arrow-two;
        animation-duration: 8s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 2s;
      }
      .delay2 {
        animation-delay: 4s;
      }
      .delay3 {
        animation-delay: 6s;
      }
      .delay4 {
        animation-delay: 8s;
      }
    }
    @keyframes big-arrow-two{ 
      0% {opacity:0; transform: translateX(0px);}
      20% {opacity:1; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateX(-137px);}
    }
    // 세로 물흐름
    .water-flow-vertical{
      position: absolute;
      width: 8px;
      height: 60px;
      .buble-v {
        position: absolute;
        width: 8px;
        height: 60px;
        background-image: url('../../assets/disinfection/water_v_flow_one.png');
        background-position:50% -20px;
        animation-name: arrow-two;
        animation-duration: 4s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 0s;
      }
      .delay2 {
        animation-delay: 2s;
      }
      .delay3 {
       animation-delay: 8s;
      }
      .delay4 {
       animation-delay: 10s;
      }
    } 
    @keyframes arrow-two{ 
      0% {opacity:0; transform: translateY(0px);}
      10% {opacity:1; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateY(50px);}
    }
    // .water-flow-vertical-short-first{
    //   position: absolute;
    //   width: 8px;
    //   height: 50px;
    //   .buble-v {
    //     position: absolute;
    //     width: 8px;
    //     height: 50px;
    //     background-image: url('../../assets/disinfection/water_v_flow_one.png');
    //     background-position:50% -20px;
    //     animation-name: arrow-two-short;
    //     animation-duration: 4s;
    //     animation-timing-function: linear;
    //     animation-iteration-count: infinite;
    //     opacity: 0;
    //   }
    //   .delay1 {
    //     animation-delay: 6s;
    //   }
    //   .delay2 {
    //     animation-delay: 8s;
    //   }
    //   .delay3 {
    //     animation-delay: 10s;
    //   }
    // }
    // .water-flow-vertical-short-second{
    //   position: absolute;
    //   width: 8px;
    //   height: 50px;
    //   .buble-v {
    //     position: absolute;
    //     width: 8px;
    //     height: 50px;
    //     background-image: url('../../assets/disinfection/water_v_flow_one.png');
    //     background-position:50% -20px;
    //     animation-name: arrow-two-short;
    //     animation-duration: 4s;
    //     animation-timing-function: linear;
    //     animation-iteration-count: infinite;
    //     opacity: 0;
    //   }
    //   .delay1 {
    //     animation-delay: 6s;
    //   }
    //   .delay2 {
    //     animation-delay: 8s;
    //   }
    //   .delay3 {
    //     animation-delay: 16s;
    //   }
    // }
    // @keyframes arrow-two-short{ 
    //   0% {opacity:0; transform: translateY(0px);}
    //   10% {opacity:1; }
    //   90% {opacity:1; }
    //   100% {opacity:0; transform: translateY(50px);}
    // }
  }
}
.btn-tab{
    display: flex;
    justify-content:space-evenly;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2.3;
    letter-spacing: normal;
    text-align: center;
    color: #fff;
    width: 260px;
    .box-center-margin{
      // margin: 0 16px;
    }
    &__box-abled{
      width: 120px;
      height: 35px;
      box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
      border: solid 1px #72a3d6;
      background-color: #447fbc;
      color: #fff;
      line-height: 35px;
      cursor: pointer;
    }
    &__box-disabled{
      width: 120px;
      height: 35px;
      border: solid 1px #457fbc;
      background-color: #1a3462;
      color: #a7c2e7;
      line-height: 35px;
      cursor: pointer;
    }
  }
</style>