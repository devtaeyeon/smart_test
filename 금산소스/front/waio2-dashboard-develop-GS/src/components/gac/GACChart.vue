<template>
  <div class="main">
    <div class="bottom-one">
      <div class="bottom-one__title">주요인자</div>
      <div class="box-container">
        <div class="arrow-animate-one"></div>
        <div class="box-contents">
          <div class="box-value">
            <div class="box-value__text">· 1계열 혼화지 유입 유량</div>
            <div class="box-value__value">{{ this.$store.state.gac.latest.d1_in_fr | numFormat('0,0') }}</div>
            <div class="box-value__unit">m<sup>3</sup>/h</div>
          </div>
          <div class="box-value">
            <div class="box-value__text">· 2계열 혼화지 유입 유량</div>
            <div class="box-value__value">{{ this.$store.state.gac.latest.d_ser_in_fr | numFormat('0,0') }}</div>
            <div class="box-value__unit">m<sup>3</sup>/h</div>
          </div>
          <div class="box-value">
            <div class="box-value__text">· 1계열 침전수 탁도</div>
            <div class="box-value__value">{{ this.$store.state.gac.latest.e1_tb_b | numFormat('0.00') }}</div>
            <div class="box-value__unit">NTU</div>
          </div>
          <div class="box-value">
            <div class="box-value__text">· 2계열 침전수 탁도</div>
            <div class="box-value__value">{{ this.$store.state.gac.latest.e2_tb_b | numFormat('0.00') }}</div>
            <div class="box-value__unit">NTU</div>
          </div>
          <div class="box-value">
            <div class="box-value__text">· 여과지 통합 탁도</div>
            <div class="box-value__value">{{ this.$store.state.gac.latest.i_tb | numFormat('0.00') }}</div>
            <div class="box-value__unit">NTU</div>
          </div>
          <div class="box-value">
            <div class="box-value__text">· GAC 여과 유출 유량</div>
            <div class="box-value__value">{{ this.$store.state.gac.latest.i_out_fr | numFormat('0,0') }}</div>
            <div class="box-value__unit">m<sup>3</sup>/h</div>
          </div>
        </div>
      </div>
    </div>
    <div class="bottom-two">
      <div class="bottom-two__title">알고리즘 예측</div>
      <div class="arrow-animate-two"></div>
      <div class="bottom-box">
        <div class="bottom-box__little-title">지수 결정 알고리즘</div>
        <div class="bottom-box__title">운영지수 <span>{{ this.$store.state.gac.latest.ai_i_opr_cnt }}</span> 지</div>
        <div class="bottom-box__under"></div>
      </div>
    </div>
    <div class="bottom-chart">
      <div class="bottom-chart__title">
        예측 결과
        <div class="bottom-chart__timerbox-outter">
          <div class="timerbox">
            {{ this.$store.state.gac.latest.upd_ti | moment('YYYY-MM-DD HH:mm:ss') }}
          </div>
        </div>
      </div>
      <div class="chart-box">
        <div class="chart-box-top">
          <div class="chart-box-top__title" @click="$store.state.dialog.aiFilterNGACSchedule.visible = true">AI 운영스케줄</div>
          <div class="legend-box">
            <div class="legend-box__legend legend-box__legend--load-mid">중간부하</div>
            <div class="legend-box__legend legend-box__legend--load-high">최대부하</div>
            <div class="legend-box__legend legend-box__legend--schedule">운영스케줄</div>
            <div class="legend-box__legend legend-box__legend--bw">역세시작시간</div>
          </div>
          <div class="select-box">
            <div class="select-box__selected" :class="[selectedLine === 1 ? 'on' : '']" @click="selectedLine = 1"># 1</div>
            <div class="select-box__selected" :class="[selectedLine === 2 ? 'on' : '']" @click="selectedLine = 2"># 2</div>
          </div>
        </div>
        <div class="chart-box__chart">
          <AIGACLocationChart :selectedLine="this.selectedLine" />
        </div>
      </div>
    </div>
    <FilterAndGACSchedulePopup/>
  </div>  
</template>
<script>
// import Highcharts from 'highcharts'
// import xrangeInit from 'highcharts/modules/xrange'
// import stockInit from 'highcharts/modules/stock'
import AIGACLocationChart from '@/components/gac/chart/AIGACLocationChart'
import FilterAndGACSchedulePopup from '@/components/dialog/FilterAndGACSchedulePopup'
// xrangeInit(Highcharts)
// stockInit(Highcharts)

// Highcharts.setOptions({
//   global: {
//     useUTC: false
//   },
//   lang: {
//     thousandsSep: ','
//   }
// })
export default {
  name:'GACChart',
  components: {
    FilterAndGACSchedulePopup,
    AIGACLocationChart
  },
  data: () => ({
    selectedLine: 1,
  })
}
</script>
<style lang="scss" scoped>
.main{
  display: flex;
  width: 100%;
  height: 392px;
  margin: 23px 0 0 60px;
}
.bottom-chart{
  position: relative;
  left: -120px;
  width: 100%;
  &__timerbox-outter {
    display: flex;
    position: relative;
    margin-left: auto;
    top: -3px;
  }
  .chart-box{
    width: 686px;
    height: 303px;
    background-image: url('../../assets/percolation/bottom_chart_box.png');
    
    &__chart{
      width: 100%;
      height: 235px;
    }
    .chart-box-top{
      display: flex;
      .select-box{
        display: flex;
        margin-top: 12px;
        margin-left: auto;
        &__selected{
          width: 30.9px;
          height: 26.4px;
          border: solid 1px #b4dffa;
          background-color: rgba(139, 194, 240, 0.25);
          font-size: 14px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 1.8;
          letter-spacing: normal;
          text-align: center;
          color: #fff;
          cursor: pointer;
        }
        .on {
          box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
          border: solid 1px #b4dffa;
          background-color: rgba(139, 194, 240, 0.4);
        }
      }
      .legend-box{
        display: flex;
        margin-top: 12px;
        margin-right: 15px;
        margin-left: auto;
        flex: 1;
        justify-content: flex-end;
        &__legend {
          height: 20px;
          font-size: 14px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          letter-spacing: normal;
          text-align: center;
          color: #fff;
          // cursor: pointer;
          &:before {
            content: "";
            display: inline-block;
            justify-content: center;
            align-items: center;
            width: 15px;
            height: 7px;
            margin-right: 5px;
          }
          &--load-mid:before {
            background-color:rgba(126, 110, 250)
          }
          &--load-high:before {
            margin-left: 10px;
            background-color:rgba(212, 110, 250)
          }
          &--schedule:before {
            margin-left: 10px;
            background-color:rgb(180, 223, 251)
          }
          &--bw:before {
            margin-left: 10px;
            background-color:red
          }
        }
        .on {
          box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
          border: solid 1px #b4dffa;
          background-color: rgba(139, 194, 240, 0.4);
        }
      }
      &__title{
        width: 196px;
        height: 53px;
        background-image: url('../../assets/percolation/chart_title.png');
        text-shadow: 0 0 9px #5cafff;
        font-size: 18px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 2.8;
        letter-spacing: normal;
        text-align: center;
        color: #fff;
        cursor: pointer;
      }
    }
  }
  &__title{
    display: flex;
    align-items: center;
    width: 815px;
    height: 53px;
    background-image: url('../../assets/percolation/bottom_title.png');
    text-shadow: 0 0 9px #5cafff;
    font-size: 18px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2.7;
    letter-spacing: normal;
    text-align: center;
    color: #fff;
    padding-right: 130px;
    padding-left: 40px;
  }
}
.bottom-two{
  position: relative;
  left: -87px;
  width: 100%;
  &__title{
    width: 297px;
    height: 53px;
    background-image: url('../../assets/percolation/bottom_title.png');
    text-shadow: 0 0 9px #5cafff;
    font-size: 18px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2.7;
    letter-spacing: normal;
    text-align: center;
    color: #fff;
    padding-right: 120px;
  }
  .bottom-box{
    width: 581px;
    height: 305px;
    background-image: url('../../assets/percolation/bottom_two_box.png');
    &__little-title{
      width: 244px;
      height: 53px;
      background-image: url('../../assets/percolation/bottom_box_little_box.png');
      text-shadow: 0 0 9px #5cafff;
      font-size: 18px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2.8;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
    }
    &__title{
      width: 490px;
      text-shadow: 0 0 9px #5cafff;
      font-size: 31px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.42;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      margin-top: 60px;
      span{
        text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
        font-family: "LAB디지털" !important;
        font-size: 40px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.18;
        letter-spacing: normal;
        text-align: center;
        color: #ccf1ff;
      }
    }
    &__under{
      height: 52px;
      background-image: url('../../assets/percolation/bottom_under_title.png');
      background-position-x: 50px;
      background-position-y: -10px;
    }
  }
  .chart-box{
    display: flex;
    width: 1381px;
    height: 310px;
    background-image: url('../../assets/percolation/chart_bottom.png');
  }
}
.arrow-animate-one{
  position: absolute;
  top: 688px;
  left: 550px;
  background-image: url('../../assets/percolation/move_arrow.png');
  width: 105px;
  height: 288px;
  animation-name: big-arrow-one;
  animation-duration: 5s;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
}
@keyframes big-arrow-one{ 
  0% {opacity:0; transform: translateX(-5px);}
  12% {opacity:0.5; transform: translateX(-2px);}
  24% {opacity:1; transform: translateX(0px);}
  36% {opacity:0.5; transform: translateX(2px);}
  48% {opacity:0; transform: translateX(5px);}
  60% {opacity:0;}
  72% {opacity:0;}
  84% {opacity:0;}
  100% {opacity:0;}
}
.arrow-animate-two{
  position: absolute;
  top: 75px;
  left: 436px;
  background-image: url('../../assets/percolation/move_arrow.png');
  width: 105px;
  height: 288px;
  animation-name: big-arrow-two;
  animation-duration: 5s;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
}
@keyframes big-arrow-two{ 
  0% {opacity:0;}
  12% {opacity:0;}
  24% {opacity:0;}
  36% {opacity:0;}
  48% {opacity:0;}
  60% {opacity:0.5; transform: translateX(-2px);}
  72% {opacity:1; transform: translateX(0px);}
  84% {opacity:0.5; transform: translateX(2px);}
  100% {opacity:0; transform: translateX(5px);}
}
.bottom-one{
  width: 666px;
  .box-container{
    display: flex;
    width: 666px;
    height: 311px;
    background-image: url('../../assets/percolation/box_container.png');
    .box-contents{
      width: 453px;
      height: 241px;
      margin-top: 23px;
      background-image: url('../../assets/percolation/box_contents.png');
      padding-top: 27px;
      .box-value{
        display: flex;
        &__text{
          width: 202px;
          margin-left: 20px;
          text-shadow: 0 0 9px #5cafff;
          font-size: 18px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          letter-spacing: normal;
          text-align: left;
          color: #9fc4ff;
        }
        &__value{
          width: 70px;
          text-shadow: 0 0 9px #5cafff;
          font-size: 20px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          letter-spacing: normal;
          text-align: right;
          color: #fff;
          margin-left: auto;
        }
        &__unit{
          width: 50px;
          margin-left: 10px;
          font-size: 14px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 2.3;
          letter-spacing: normal;
          text-align: left;
          color: #417db9;
        }
      }
    }
  }
  &__title{
    width: 297px;
    height: 53px;
    background-image: url('../../assets/percolation/bottom_title.png');
    text-shadow: 0 0 9px #5cafff;
    font-size: 18px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2.7;
    letter-spacing: normal;
    text-align: center;
    color: #fff;
    padding-right: 130px;
  }
}
</style>