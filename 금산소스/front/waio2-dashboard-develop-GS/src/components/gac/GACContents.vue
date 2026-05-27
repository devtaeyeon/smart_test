<template>
  <div class="main">
    <div class="water-h-flow-img one">
      <div class="buble delay1"></div>
      <div class="buble delay2"></div>
      <div class="buble delay3"></div>
    </div>
    <div class="water-h-flow-img three">
      <div class="buble-two delay-one1"></div>
      <div class="buble-two delay-one2"></div>
      <div class="buble-two delay-one3"></div>
    </div>
    <div class="water-h-flow-img three">
      <div class="buble-two delay-two1"></div>
      <div class="buble-two delay-two2"></div>
      <div class="buble-two delay-two3"></div>
    </div>
    <div class="water-h-flow-img three">
      <div class="buble-two delay-three1"></div>
      <div class="buble-two delay-three2"></div>
      <div class="buble-two delay-three3"></div>
    </div>
    <div class="water-h-flow-img nine">
      <div class="buble delay1"></div>
      <div class="buble delay2"></div>
      <div class="buble delay3"></div>
    </div>
    <div class="contents-container">
      <div class="contents-box-other">
        <div class="box top">
          <div class="box-top-title">1계열 혼화지 유입 유량</div>
          <div class="box-bottom">
            <div class="box-bottom__value" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.gac.latest.d1_in_fr)"></div>
            <div class="box-bottom__unit">m<sup>3</sup>/h</div>
          </div>
          <div class="box-top-title">2계열 혼화지 유입 유량</div>
          <div class="box-bottom">
            <div class="box-bottom__value" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.gac.latest.d_ser_in_fr)"></div>
            <div class="box-bottom__unit">m<sup>3</sup>/h</div>
          </div>
        </div>
        <div class="box bottom">
          <div class="box-top-title">GAC 여과지 유입유량</div>
          <div class="box-bottom">
            <div class="box-bottom__value" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.gac.latest.i_in_fr)"></div>
            <div class="box-bottom__unit">m<sup>3</sup>/h</div>
          </div>
        </div>        
      </div>
      <div v-for="index in [5, 7, 9, 11, 13, 15, 17]" :key="index" class="contents-box">        
        <div class="box">
          <div class="water-fill" :class="getClassObject(index)" :style="{ paddingTop: getPaddingTop(index) }">
            <div class="water-fill__background"></div>
          </div>
          <div class="box-contents-title img-position-top" :class="[ isFiltering(index) ? 'on' : 'off']">{{ index }}지 {{ $store.getters['gac/getLocationState' + index] }}</div>
          <div class="box-value-contents">
            <div class="box-value-contents__value">{{ getLocationLevel(index) }}</div>
            <div class="box-value-contents__unit">m</div>
          </div>
          <div class="box-value-contents">
            <div class="box-value-contents__value">{{ getLocationGACFilterTime(index) }} / {{ getLocationAIGACFilterTime(index) }}</div>
            <div class="box-value-contents__unit">시간</div>
          </div>
          <div class="box-value-contents">
            <div class="box-value-contents__value">{{ getAIBWStartTime(index) }}</div>
            <div class="box-value-contents__unit">시간 후</div>
          </div>
        </div>
        <div class="box bottom-contents">
          <div class="water-fill" :class="getClassObject(index + 1)" :style="{ paddingTop: getPaddingTop(index + 1) }">
            <div class="water-fill__background"></div>
          </div>
          <div class="box-value-contents">
            <div class="box-value-contents__value">{{ getLocationLevel(index + 1) }}</div>
            <div class="box-value-contents__unit">m</div>
          </div>
          <div class="box-value-contents">
            <div class="box-value-contents__value">{{ getLocationGACFilterTime(index + 1) }} / {{ getLocationAIGACFilterTime(index + 1) }}</div>
            <div class="box-value-contents__unit">시간</div>
          </div>
          <div class="box-value-contents">
            <div class="box-value-contents__value">{{ getAIBWStartTime(index + 1) }}</div>
            <div class="box-value-contents__unit">시간 후</div>
          </div>
          <div class="box-contents-title img-position-bottom" :class="[ isFiltering(index + 1) ? 'on' : 'off']">{{ index + 1 }}지 {{ $store.getters['gac/getLocationState' + (index + 1)] }}</div>
        </div>
      </div>
      <div class="contents-box-other">        
        <div class="box other-bottom-padding">
          <div class="bottom-information">수위</div>
          <div class="bottom-information">여과지속(현재/예측)</div>
          <div class="bottom-information">역세시작시간</div>
        </div>
        <div class="box other-top">
          <div class="box-top-title">GAC 여과지 유출 유량</div>
          <div class="box-bottom">
            <div class="box-bottom__value" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.gac.latest.i_out_fr)"></div>
            <div class="box-bottom__unit">m<sup>3</sup>/h</div>
          </div>
        </div>
      </div>
    </div>
  </div>  
</template>
<script>
import numeral from 'numeral'
export default {
  name:"GACContents",
  components:{
  },
  methods: {
    isFiltering: function (location) {
      if ((this.$store.state.gac.latest.i_loc_stt && this.$store.state.gac.latest.i_loc_stt['location' + location])) {
        return this.$store.state.gac.latest.i_loc_stt['location' + location] === 1
      }
      return false
    },
    getLocationLevel: function (location) {
      if ((this.$store.state.gac.latest.i_loc_le && (this.$store.state.gac.latest.i_loc_le['location' + location] || this.$store.state.gac.latest.i_loc_le['location' + location] ===0))) {
        return numeral(this.$store.state.gac.latest.i_loc_le['location' + location]).format('0.00')
      }
      return '--'
    },
    getLocationGACFilterTime: function (location) {
      if ((this.$store.state.gac.latest.i_loc_ti && (this.$store.state.gac.latest.i_loc_ti['location' + location] || this.$store.state.gac.latest.i_loc_ti['location' + location] === 0))) {
        return numeral(this.$store.state.gac.latest.i_loc_ti['location' + location] / 60).format('0')
      }
      return '--'
    },
    getLocationAIGACFilterTime: function (location) {
      if ((this.$store.state.gac.latest.i_loc_ti && (this.$store.state.gac.latest.i_loc_ti['location' + location] || this.$store.state.gac.latest.i_loc_ti['location' + location] === 0))) {
        return numeral(this.$store.state.gac.latest.i_loc_ti['location' + location] / 60).format('0')
      }
      return '--'
    },
    getAIBWStartTime: function (location) {
      if ((this.$store.state.gac.latest.ai_i_loc_bw_ti && (this.$store.state.gac.latest.ai_i_loc_bw_ti['location' + location] || this.$store.state.gac.latest.ai_i_loc_bw_ti['location' + location] === 0))) {
        let v = this.$store.state.gac.latest.ai_i_loc_bw_ti['location' + location]
        v = v < 0 ? 0 : v
        return numeral(v / 60).format('0')
      }
      return '--'
    },
    getClassObject: function (index) {
      return 'ji' + (index + 2)
    },
    getPaddingTop: function (location) {
      if (this.$store.state.gac.latest.i_loc_le && (this.$store.state.gac.latest.i_loc_le['location' + location])) {
        return (130 - (this.$store.state.gac.latest.i_loc_le['location' + location] * 51)) + 'px'
      } else {
        return '0px'
      }
    },
  }
}
</script>
<style lang="scss" scoped>
.ji7{
  width: 195px;
  height: 132px;
  top: 212px;
  left: 252px;
}
.ji8{
  width: 195px;
  height: 132px;
  top: 415px;
  left: 252px;
}
.ji9{
  width: 195px;
  height: 132px;
  top: 212px;
  left: 456px;
}
.ji10{
  width: 195px;
  height: 132px;
  top: 415px;
  left: 456px;
}
.ji11{
  width: 195px;
  height: 132px;
  top: 212px;
  left: 660px;
}
.ji12{
  width: 195px;
  height: 132px;
  top: 415px;
  left: 660px;
}
.ji13{
  width: 195px;
  height: 132px;
  top: 212px;
  left: 865px;
}
.ji14{
  width: 195px;
  height: 132px;
  top: 415px;
  left: 865px;
}
.ji15{
  width: 195px;
  height: 132px;
  top: 212px;
  left: 1070px;
}
.ji16{
  width: 195px;
  height: 132px;
  top: 415px;
  left: 1070px;
}
.ji17{
  width: 195px;
  height: 132px;
  top: 212px;
  left: 1276px;
}
.ji18{
  width: 195px;
  height: 132px;
  top: 415px;
  left: 1276px;
}
.ji19{
  width: 195px;
  height: 132px;
  top: 212px;
  left: 1479px;
}
.ji20{
  width: 195px;
  height: 132px;
  top: 415px;
  left: 1479px;
}
.water-fill{
  position: absolute;
  z-index: 2;
  &__background{
    width: 100%;
    height: 100%;
    background-image: linear-gradient(to bottom, rgba(44, 117, 255, 0.3), rgba(127, 224, 255, 0));
  }
}
.one{
  top: 378px;
  left: 50px;
}
.two{
  top: 450px;
  left: 50px;
}
.three{
  left: 262px;
}
.four{
  left: 482px;
}
.five{
  left: 694px;
}
.six{
  left: 906px;
}
.seven{
  left: 1120px;
}
.eight{
  left: 1340px;
}
.nine{
  right:95px
}
.water-h-flow-img{
  position: absolute;
  width: 136px;
  height: 8px;
  .buble-two{
    position: absolute;
    width: 136px;
    height: 8px;
    background-image: url('../../assets/splashdown/water_h_flow.png');
    background-position: -35px 50%;
    animation-name: arrow-two;
    animation-duration: 18s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .buble {
    position: absolute;
    width: 136px;
    height: 8px;
    background-image: url('../../assets/splashdown/water_h_flow.png');
    background-position: -35px 50%;
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
  .delay-one1 {
    animation-delay: 0s;
  }
  .delay-one2 {
    animation-delay: 0.5s;
  }
  .delay-one3 {
    animation-delay: 1s;
  }
  .delay-two1 {
    animation-delay: 5s;
  }
  .delay-two2 {
    animation-delay: 5.5s;
  }
  .delay-two3 {
    animation-delay: 6s;
  }
  .delay-three1 {
    animation-delay: 11s;
  }
  .delay-three2 {
    animation-delay: 11.5s;
  }
  .delay-three3 {
    animation-delay: 12s;
  }
}
@keyframes arrow-one{ 
  0% {opacity:0; transform: translateX(0px);}
  50% {opacity:1; }
  90% {opacity:1; }
  100% {opacity:0; transform: translateX(150px);}
}
@keyframes arrow-two{ 
  0% {opacity:0; transform: translateX(0px);}
  5% {opacity:1; }
  10% {opacity:1;}
  15% {opacity:0; }
  20% {opacity:0; }
  25% {opacity:1;}
  30% {opacity:1;}
  35% {opacity:0;}
  40% {opacity:0;}
  45% {opacity:1;}
  50% {opacity:1;}
  55% {opacity:0;}
  60% {opacity:0;}
  65% {opacity:1;}
  70% {opacity:1;}
  75% {opacity:0;}
  80% {opacity:0;}
  85% {opacity:1;}
  90% {opacity:1;}
  95% {opacity:1;}
  100% {opacity:0; transform: translateX(1300px);}
}
.main{
  display: flex;
  width: 100%;
  justify-content: center;
  align-items: center;
}
.contents-container{
  display: flex;
  width: 1826px;
  height: 369px;
  background-image: url('../../assets/gacpercolation/contents_container.png');
  background-position-y: 7px;
  .bottom{
    justify-content: flex-end;
    padding-bottom: 70px;
  }
  .top{
    padding-top: 5px;
  }
  .other-top{
    padding-top: 25px;
  }
  .img-position-top{
    top:-16px;
    margin-bottom: 15px;
  }
  .img-position-bottom{
    bottom: -16px;
    margin-top: 15px;
  }
  .bottom-contents{
    justify-content: flex-end;
  }
  .other-bottom-padding{
    padding-top: 52px;
  }
  .box{
    display: flex;
    flex-flow: column;
    align-items: center;
    width: 100%;
    height: 50%;
    .bottom-information{
      width: 93%;
      background-image: url('../../assets/percolation/bottom_arrow.png');
      background-position-y: center;
      background-position-x: 20px;
      text-shadow: 0 0 9px #5cafff;
      font-size: 16px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.56;
      letter-spacing: normal;
      text-align: center;
      color: #c3eaff;
      padding-left: 45px;
    }
    .on{
      background-image: url('../../assets/percolation/box_contents_title_on.png');
    }
    .off{
      background-image: url('../../assets/percolation/box_contents_title_off.png');
    }
    .box-contents-title{
      position: relative;
      width: 100%;
      height: 35px;
      text-shadow: 0 0 9px #5cafff;
      font-size: 16px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2.13;
      letter-spacing: normal;
      text-align: center;
      color: #c3eaff;
      padding-left: 8px;
      z-index: 10;
    }
    .box-value-contents{
      display: flex;
      width: 100%;
      z-index: 5;
      &__value{
        width:115px;
        text-shadow: 0 0 9px #5cafff;
        font-size: 16px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.56;
        letter-spacing: normal;
        text-align: right;
        color: #fff;
      }
      &__unit{
        margin-left: 15.5px;
        font-size: 14px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.79;
        letter-spacing: normal;
        text-align: left;
        color: #a4ceed;
      }
    }
    .box-top-title{
      text-shadow: 0 0 9px #5cafff;
      font-size: 16px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.56;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
    }
    .box-bottom{
      display: flex;
      width: 131px;
      height: 43px;
      margin-top: 10px;
      object-fit: contain;
      border: solid 1px rgba(157, 191, 255, 0.3);
      &__value{
        width: 82px;
        text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
        font-family: "LAB디지털" !important;
        font-size: 24px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.6;
        letter-spacing: normal;
        text-align: center;
        color: #ccf1ff;
      }
      &__unit{
        margin: 0 5px;
        font-size: 16px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 2.7;
        letter-spacing: normal;
        text-align: left;
        color: #417db9;
      }
    }
  }
  .contents-box-other{
    width: 11%;
    height: 100%;
  }
  .contents-box{
    width: 11%;
    height: 100%;
    margin: 0 auto;
  }
}
.paddingfix{
  padding-left: 10px;
}
</style>