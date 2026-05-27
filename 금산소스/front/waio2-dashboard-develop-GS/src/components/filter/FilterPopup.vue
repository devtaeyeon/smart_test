<template>
  <div v-if="this.$store.state.filter.popup.visible" class="main">
    <div class="popup-main">
      <div class="popup-contents">
        <!-- 여과 세부 현황 제목 -->
        <div class="top">
          <div class="top__img"></div>
          <div class="top__title">여과 세부 현황</div>
          <div class="top__exit-btn" @click="closePopup()"></div>
        </div>
        <!-- 여과 세부 현황 메인 정보 -->
        <div class="information">
          <div class="water-h-flow-img one-animation">
            <div class="buble delay1"></div>
            <div class="buble delay2"></div>
            <div class="buble delay3"></div>
          </div>
          <div class="water-h-flow-img two-animation">
            <div class="buble delay1"></div>
            <div class="buble delay2"></div>
            <div class="buble delay3"></div>
          </div>
          <div class="top-title">여과 {{ this.$store.state.filter.popup.numJi }}지</div>
          <div class="contents-value">
            <div class="contents-value__text">원수 유입 유량</div>
            <div class="contents-value__box">
              <div class="box-text width50" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.filter.popup.location.b_in_fr)"></div>
              <div class="box-unit">m<sup>3</sup>/h</div>
            </div>
            <!-- <div class="contents-value__text">2계열 혼화지 유입 유량</div>
            <div class="contents-value__box">
              <div class="box-text width50" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.filter.popup.location.d_ser_in_fr)"></div>
              <div class="box-unit">m<sup>3</sup>/h</div>
            </div> -->
          </div>
          <!-- 여과 공정 세부 이미지 -->
          <div class="contents-center">
            <div class="line two">
              <div class="line-value">
                <div class="line-value__num two-value-color">{{ this.$store.getters['filter/getAiLocationWaterLevelLater10Minutes'] }}</div>
                <div class="line-value__unit two-value-color">m</div>
              </div>
              <div class="line-box two-title">t + 1</div>
            </div>
            <div class="line three">
              <div class="line-value">
                <div class="line-value__num three-value-color">{{ this.$store.state.filter.popup.location.f_loc_le | numFormat('0.00') }}</div>
                <div class="line-value__unit three-value-color">m</div>
              </div>
              <div class="line-box three-title">현재 수위</div>
            </div>
            <div class="box-value">
              <div class="box-value__text">여과지 유출 유량</div>
              <div class="box-value__box">
                <div class="box-text width50" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.filter.popup.location.f_out_fr)"></div>
                <div class="box-unit">m<sup>3</sup>/h</div>
              </div>
            </div>
          </div>
          <!-- 우측 여과 공정 값 -->
          <div class="contents-right">
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 여과 시작 시점</div>
              <!-- TODO 분석 AI운영스케줄 데이터 생성 후 수정 -->
              <div class="contents-text-box__value">{{ this.$store.getters['filter/getPopupFilterStartTime'] }}</div>
              <div class="contents-text-box__unit"></div>
            </div>
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 여과 종료 시점</div>
              <div class="contents-text-box__value">{{ this.$store.getters['filter/getPopupFilterEndTime'] }}</div>
              <div class="contents-text-box__unit"></div>
            </div>
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 역세 시작 시점</div>
              <div class="contents-text-box__value">{{ this.$store.getters['filter/getPopupBWStartTime'] }}</div>
              <div class="contents-text-box__unit"></div>
            </div>
            <div class="contents-liner"></div>
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 현재 수위</div>
              <div class="contents-text-box__value">{{ this.$store.state.filter.popup.location.f_loc_le | numFormat('0.00') }}</div>
              <div class="contents-text-box__unit">m</div>
            </div>
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 10분 후 수위</div>
              <div class="contents-text-box__value">{{ this.$store.getters['filter/getAiLocationWaterLevelLater10Minutes'] }}</div>
              <div class="contents-text-box__unit">m</div>
            </div>
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 24시간 후 수위</div>
              <div class="contents-text-box__value">{{ this.$store.getters['filter/getAiLocationWaterLevelLater24Hours'] }}</div>
              <div class="contents-text-box__unit">m</div>
            </div>
            <div class="contents-liner"></div>
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 탁도</div>
              <div class="contents-text-box__value">{{ this.$store.state.filter.popup.location.f_loc_tb | numFormat('0.00') }}</div>
              <div class="contents-text-box__unit">NTU</div>
            </div>
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 여과 지속시간(현재/예측)</div>
              <div class="contents-text-box__value">{{ this.$store.state.filter.popup.location.f_loc_ti / 60 | numFormat('0') }} / {{ this.$store.state.filter.popup.location.ai_f_loc_ti / 60 | numFormat('0') }}</div>
              <div class="contents-text-box__unit">시간</div>
            </div>
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 역세 대기시간</div>
              <div class="contents-text-box__value">{{ this.$store.state.filter.popup.location.ai_f_location_bw_wait_ti / 60 | numFormat('0') }}</div>
              <div class="contents-text-box__unit">시간</div>
            </div>
            <div class="contents-text-box">
              <div class="contents-text-box__title">· 여과 속도</div>
              <div class="contents-text-box__value">{{ this.$store.state.filter.popup.location.f_sp | numFormat('0.00') }}</div>
              <div class="contents-text-box__unit">m<sup>3</sup>/h</div>
            </div>
          </div>
        </div>
        <!-- 하단 차트 컨테이너 -->
        <div class="chart-container">
          <div class="chart-container__title">{{ this.$store.state.filter.popup.numJi }}지 운영 예측</div>
          <div class="chart-container__chart">
            <highcharts :options="ChartMeter" style="height:100%;"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { CLOSE_POPUP } from '@/store/modules/filter'
import { GET_FILTER_LOCATION_BY_JI } from '@/store/modules/filter'
export default {
name: 'FilterPopup',
data: () => ({
  timer: null, // API 요청 타이머
  // 운영 예측 차트
  chartDataMeter: {
    chart: {
      type: 'spline',
      backgroundColor: false,
      zoomType: 'x'
    },
    title: {
      text: null,
      style: {
        color: 'white'
      }
    },
    legend: {
      align: 'right',
      verticalAlign: 'top',
      borderWidth: 0,
      y: 0,
      x: -20,
      itemStyle: {
        color: '#ffffff',
        fontWeight: 'bold'
      },
      labelFormatter: function () {
        return '<span style="color: ' + this.color + '; width: 100px;">' + this.name + '</span>'
      }
    },
    credits: {
      enabled: false
    },
    tooltip: {
      valueDecimals: 2,
      xDateFormat: '%Y-%m-%d %H:%M:%S',
      valueSuffix: 'm'
    },
    xAxis: {
      title: {
        text: ''
      },
      type: 'datetime',
      labels: {
        format: '{value:%m-%d %H:%M}',
        style:{
          fontFamily:'NanumSquare',
          fontSize: 13,
          color:"rgb(255,255,255,0.8)"
        }
      },
      tickInterval: 1000 * 60 * 60 * 3 
    },
    yAxis: {
      title: {
        align: 'high',
        text: 'm',
        useHTML: true,
        offset: 0,
        rotation: 0,
        x: -15,
        y: -15,
        style: {
          color:"rgb(255,255,255,0.8)"
        }
      },
      gridLineColor: false,
      labels: {
        style:{
          fontFamily:'NanumSquare',
          fontSize: 13,
          color:"rgb(255,255,255,0.8)"
        }
      }
    },
    series: null,
    exporting: {
      buttons: {
        contextButton: {
          menuItems: ['viewFullscreen', 'downloadJPEG', 'downloadCSV'],
          menuItemStyle: { padding: "0 10px", background: "none", color: "#303030"},
          symbolStroke: "rgb(110, 157, 225)",
          theme: {
            fill:"rgba(0,0,0,0)",
            states: {
              hover: {
                fill: 'rgba(0,0,0,0)',
              },
              select: {
                fill: 'rgba(0,0,0,0)',
              }
            }
          },
          titleKey: null
        }
      },
      filename: null,
      fallbackToExportServer: false,
    }
  }
}),
computed: {
  // 운영 예측 차트 옵션
  ChartMeter: function () {
    let chart = this.chartDataMeter
    let now = new Date()
      chart.xAxis.plotLines = [{
        color: 'white', 
        dashStyle: 'ShortDash', 
        value: now.getTime(),
        width: 3,
      }]
    if (this.$store.state.filter.popup.location.ai_f_loc_le !== null && Object.keys(this.$store.state.filter.popup.location.ai_f_loc_le).length > 0) {
      let dataSeries = []
      for (let key in this.$store.state.filter.popup.location.ai_f_loc_le) {
        let value = this.$store.state.filter.popup.location.ai_f_loc_le[key] > 2.1 ? 2.1 : this.$store.state.filter.popup.location.ai_f_loc_le[key]
        dataSeries.push([new Date(key).getTime(), value])
      }
      chart.series = [{
        name: '예측 수위',
        data: dataSeries,
        color: 'rgb(110, 157, 225)'
      }]
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_운영 예측 차트 ' + this.$store.state.filter.popup.numJi + '지'
      return chart
    }
    return chart
  }
},
methods: {
  /**
   * 팝업이 닫힐 때 
   * 타이머 제거
   */
  closePopup: function () {
    clearInterval(this.timer)
    this.$store.commit('filter/' + CLOSE_POPUP)
  },
  // fullscreenchange 이벤트
  // fullscreenchanged: function () {
  //   console.log('fullscreenchange')
  //   if(document.fullscreenElement) {
  //     this.chartDataMeter.title.text = '운영 예측'
  //   } else {
  //     this.chartDataMeter.title.text = null
  //   }
  // }
},
// mounted: function () {
//   window.addEventListener('fullscreenchange', this.fullscreenchanged)
// },
// beforeDestroy () { window.removeEventListener('fullscreenchange', this.fullscreenchanged) },
destroyed: function () {
  clearInterval(this.timer)
},
watch: {
  // 팝업 열림/닫힘 값 변화 감지
  '$store.state.filter.popup.visible': function (newVal) {
    if (newVal) {
      this.timer = setInterval( () => {
        Promise.all([
          this.$store.dispatch(GET_FILTER_LOCATION_BY_JI, { numJi: this.$store.state.filter.popup.numJi }),
        ])
      }, 1000 * 60)
    }
  }
}
}
</script>
<style lang="scss" scoped>
.main{
position: absolute;
top: -85px;
z-index: 200;
width: 1920px;
height: 1080px;
display: flex;
justify-content: center;
align-items: center;
background-color: rgba(30,37,61,0.8);
.popup-main{
  display: flex;
  width: 1175px;
  height: 893px;
  justify-content: center;
  align-items: center;
  background-image: url('../../assets/sedimentation/popup_main.png');
  .popup-contents{
    width: 1163px;
    height: 813px;
    background-image: url('../../assets/sedimentation/popup_contents.png');
    background-position-x: -5px;
    padding: 31px 24.4px 40px 33.7px;
    .chart-container{
      width: 100%;
      margin-top: 10px;
      height: 290px;
      &__title{
        width: 179px;
        height: 53px;
        background-image: url('../../assets/percolation/top_title.png');
        padding-left: 30px;
        text-shadow: 0 0 9px #5cafff;
        font-size: 18px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 2.8;
        letter-spacing: normal;
        text-align: left;
        color: #fff;
      }
      &__chart{
        width: 100%;
        height: 237px;
        color:#fff;
      }
    }
    .information{
      display: flex;
      width: 100%;
      height: 388px;
      margin-top: 18px;
      padding-top: 34px;
      .one-animation{
        top: 300px;
        left: 468px;
      }
      .two-animation{
        top: 563px;
        left: 900px;
      }
      .water-h-flow-img{
        position: absolute;
        width: 136px;
        height: 8px;
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
      }
      @keyframes arrow-one{ 
        0% {opacity:1; transform: translateX(0px);}
        20% {opacity:1;}
        50% {opacity:1; }
        90% {opacity:1; }
        100% {opacity:0; transform: translateX(150px);}
      }
      .contents-right{
        position: absolute;
        right: 403px;
        width: 393px;
        height: 354px;
        background-image: url('../../assets/percolation/contents_right_background.png');
        padding: 10px 14px 10px 15px;
        .contents-liner{
          width: 100%;
          height: 14px;
          margin: 10px 0;
          background-image: url('../../assets/percolation/contents_liner.png');
        }
        .contents-text-box{
          width: 100%;
          display: flex;
          &__title{
            width: 180px;
            text-shadow: 0 0 9px #5cafff;
            font-size: 16px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 1.5;
            letter-spacing: normal;
            text-align: left;
            color: #9fc4ff;
          }
          &__value{
            margin-left: auto;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 1.4;
            letter-spacing: normal;
            text-align: right;
            color: #fff;
          }
          &__unit{
            width: 43px;
            margin-left: 15px;
            font-size: 14px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2;
            letter-spacing: normal;
            text-align: left;
            color: #417db9;
          }
        }
      }
      .contents-center{
        display: flex;
        flex-flow: column;
        justify-content: flex-end;
        align-items: flex-end;
        position: relative;
        left: -177px;
        width: 606px;
        height: 100%;
        background-image: url('../../assets/percolation/contents_center_background.png');
        .one{
          top: 57px;
        }
        .one-value-color{
          color: #ffcec6;
        }
        .one-title{
          margin: 0 85px 0 31px;
          color: #e93f24;
        }
        .two{
          top: 98px;
        }
        .two-value-color{
          color: #b4d3ff;
        }
        .two-title{
          margin: 0 85px 0 104px;
          color: #3984ff;
        }
        .three{
          top: 138px;
        }
        .three-value-color{
          color: #ddfcff;
        }
        .three-title{
          margin: 0 85px 0 192px;
          color: #3ce1ff;
        }
        .line{
          display: flex;
          justify-content: flex-end;
          align-items: center;
          position: absolute;
          width: 413px;
          height: 29px;
          .line-box{
            width: 73px;
            height: 23px;
            background-image: url('../../assets/percolation/line_title.png');
            font-size: 12px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2;
            letter-spacing: normal;
            text-align: left;
            padding-left: 12px;
          }
          .line-value{
            display: flex;
            &__num{
              text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
              font-family: "LAB디지털" !important;
              font-size: 24px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              letter-spacing: normal;
              text-align: center;
            }
            &__unit{
              text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
              font-size: 18px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              line-height: 2.5;
              letter-spacing: normal;
              text-align: center;
              margin-left: 6px;
            }
          }
        }
        .box-value{
          width: 168px;
          height: 125px;
          display: flex;
          flex-flow: column;
          align-items: center;
          &__text{
            text-shadow: 0 0 9px #5cafff;
            font-size: 16px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 1.56;
            letter-spacing: normal;
            text-align: center;
            color: #fff;
            margin-bottom: 5px;
          }
          &__box{
            display: flex;
            justify-content: center;
            width: 131px;
            height: 43px;
            margin-bottom: 21px;
            border: solid 1px rgba(157, 191, 255, 0.3);
            .width50{
              width: 50%;
            }
            .box-text{
              text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
              font-family: "LAB디지털" !important;
              font-size: 24px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              line-height: 1.8;
              letter-spacing: normal;
              text-align: right;
              color: #ccf1ff;
            }
            .box-unit{
              width: 50%;
              font-size: 16px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              line-height: 2.7;
              letter-spacing: normal;
              text-align: center;
              color: #417db9;
            }
          }
        }
      }
      .contents-value{
        width: 179px;
        height: 100%;
        display: flex;
        flex-flow: column;
        align-items: center;
        justify-content: center;
        margin-left: 50px;
        &__text{
          text-shadow: 0 0 9px #5cafff;
          font-size: 16px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 1.56;
          letter-spacing: normal;
          text-align: center;
          color: #fff;
          margin-bottom: 5px;
        }
        &__box{
          display: flex;
          justify-content: center;
          width: 131px;
          height: 43px;
          margin-bottom: 21px;
          border: solid 1px rgba(157, 191, 255, 0.3);
          .width50{
            width: 50%;
          }
          .box-text{
            text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
            font-family: "LAB디지털" !important;
            font-size: 24px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 1.8;
            letter-spacing: normal;
            text-align: right;
            color: #ccf1ff;
          }
          .box-unit{
            width: 50%;
            font-size: 16px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2.7;
            letter-spacing: normal;
            text-align: center;
            color: #417db9;
          }
        }
      }
      .top-title{
        position: absolute;
        top:210px;
        width: 179px;
        height: 53px;
        padding-left: 30px;
        background-image: url('../../assets/percolation/top_title.png');
        text-shadow: 0 0 9px #5cafff;
        font-size: 18px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 2.8;
        letter-spacing: normal;
        text-align: left;
        color: #fff;
      }
    }
    .top{
      display: flex;
      width: 100%;
      height: 30px;
      &__img{
        width: 19px;
        height: 30px;
        background-image: url('../../assets/sedimentation/top_title_img.png');
      }
      &__title{
        margin-left: 10px;
        font-size: 24px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.5;
        letter-spacing: normal;
        text-align: left;
        color: #b4dffb;
      }
      &__exit-btn{
        margin-left: auto;
        width: 24px;
        height: 30px;
        background-image: url('../../assets/sedimentation/exit_btn.png');
        background-position-y: center;
        cursor: pointer;
      }
    }
  }
}
}
</style>