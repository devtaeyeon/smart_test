<template>
    <div  v-if="this.$store.state.sedimentation.popup.visible" class="main">
      <div class="popup-main">
        <div class="popup-contents">
          <!-- 침전 세부 현황 팝업 제목 -->
          <div class="top">
            <div class="top__img"></div>
            <div class="top__title">침전 세부 현황</div>
            <div class="top__exit-btn" @click="closePopup()"></div>
          </div>
          
          <!-- 대차 시작 시간 -->
          <div class="top-title">{{ this.$store.state.sedimentation.popup.numJi }}지</div>
          <div class="information">
            <!-- 이전 대차 시작 시간 -->
            <div class="flex-container">
              <div class="top-timer-text">이전 대차 시작 시간</div>
              <div class="top-timer">
                <div class="top-timer__num">{{ this.$store.state.sedimentation.popup.location.e_sc_latest !== "" ? (this.$store.state.sedimentation.popup.location.e_sc_latest !== null ? this.$store.state.sedimentation.popup.location.e_sc_latest.split(' ')[0].split('-')[1] : '00') : '00' }}</div>
                <div class="top-timer__text">월</div>
                <div class="top-timer__num">{{ this.$store.state.sedimentation.popup.location.e_sc_latest !== "" ? (this.$store.state.sedimentation.popup.location.e_sc_latest !== null ? this.$store.state.sedimentation.popup.location.e_sc_latest.split(' ')[0].split('-')[2] : '00') : '00' }}</div>
                <div class="top-timer__text">일</div>
                <div class="top-timer__num">{{ this.$store.state.sedimentation.popup.location.e_sc_latest !== "" ? (this.$store.state.sedimentation.popup.location.e_sc_latest !== null ? this.$store.state.sedimentation.popup.location.e_sc_latest.split(' ')[1].split(':')[0] : '00') : '00' }}</div>
                <div class="top-timer__text">시</div>
                <div class="top-timer__num">{{ this.$store.state.sedimentation.popup.location.e_sc_latest !== "" ? (this.$store.state.sedimentation.popup.location.e_sc_latest !== null ? this.$store.state.sedimentation.popup.location.e_sc_latest.split(' ')[1].split(':')[1] : '00') : '00' }}</div>
                <div class="top-timer__text">분</div>
              </div>
            </div>
            <!-- AI 다음 대차 시작 시간 -->
            <div class="flex-container">
              <div class="top-timer-text">AI 다음 대차 시작 시간</div>
              <div class="top-timer">
                <div class="top-timer__num">{{ this.$store.state.sedimentation.popup.location.ai_e_sc_next !== "" ? (this.$store.state.sedimentation.popup.location.ai_e_sc_next !== null ? this.$store.state.sedimentation.popup.location.ai_e_sc_next.split(' ')[0].split('-')[1] : '00') : '00' }}</div>
                <div class="top-timer__text">월</div>
                <div class="top-timer__num">{{ this.$store.state.sedimentation.popup.location.ai_e_sc_next !== "" ? (this.$store.state.sedimentation.popup.location.ai_e_sc_next !== null ? this.$store.state.sedimentation.popup.location.ai_e_sc_next.split(' ')[0].split('-')[2] : '00') : '00' }}</div>
                <div class="top-timer__text">일</div>
                <div class="top-timer__num">{{ this.$store.state.sedimentation.popup.location.ai_e_sc_next !== "" ? (this.$store.state.sedimentation.popup.location.ai_e_sc_next !== null ? this.$store.state.sedimentation.popup.location.ai_e_sc_next.split(' ')[1].split(':')[0] : '00') : '00' }}</div>
                <div class="top-timer__text">시</div>
                <div class="top-timer__num">{{ this.$store.state.sedimentation.popup.location.ai_e_sc_next !== "" ? (this.$store.state.sedimentation.popup.location.ai_e_sc_next !== null ? this.$store.state.sedimentation.popup.location.ai_e_sc_next.split(' ')[1].split(':')[1] : '00') : '00' }}</div>
                <div class="top-timer__text">분</div>
              </div>
            </div>
          </div>
          <!-- 침전지 세부 현황 컨테이너 -->
          <div class="contents">
            <div class="contents-value">
              <div class="contents-value__text">약품 종류</div>
              <div class="contents-value__box">
                <!-- <div class="box-text">{{ this.$store.state.sedimentation.popup.location.d_mm_coagulant }}</div> -->
                <div class="box-text">{{ this.$store.state.coagulants.latest.c_cf_coagulant }}</div>
              </div>
              <div class="contents-value__text">약품 사용량</div>
              <div class="contents-value__box">
                <div class="box-text width50">{{ this.$store.state.sedimentation.popup.location.d_mm_fr | numFormat('0.00') }}</div>
                <div class="box-unit">ℓ/h</div>
              </div>
              <div class="contents-value__text">슬러지 양</div>
              <div class="contents-value__box">
                <div class="box-text width50">{{ this.$store.state.sedimentation.popup.location.e_location_sludge | numFormat('0.00') }}</div>
                <div class="box-unit">ton/day</div>
              </div>
            </div>
            <div class="contents-value">
              <div class="contents-value__text">원수 유입 유량</div>
              <div class="contents-value__box">
                <div class="box-text width50" v-html="this.$getNumeralWithCommaAndFontFamily(this.$store.state.sedimentation.popup.location.b_in_fr)"></div>
                <div class="box-unit">m<sup>3</sup>/h</div>
              </div>
              <div class="contents-value__text">원수 탁도</div>
              <div class="contents-value__box">
                <div class="box-text width50">{{ this.$store.state.sedimentation.popup.location.b_tb | numFormat('0.00') }}</div>
                <div class="box-unit">NTU</div>
              </div>
            </div>
            <!-- 대차 위치 이미지 -->
            <div class="contents-img">
              <div class="contents-box">
                <!-- <div class="box-contents">
                  <div class="box-contents__text">계면계 수위</div>
                  <div class="box-contents__box">
                    <div class="box-text width50">{{ this.$store.state.sedimentation.popup.location.e_interface_le | numFormat('0.00') }}</div>
                    <div class="box-unit">m</div>
                  </div>
                </div> -->
                <!-- <div class="box-contents marginsideauto">
                  <div class="box-contents__text">중 탁도</div>
                  <div class="box-contents__box">
                    <div class="box-text width50">{{ this.$store.state.sedimentation.popup.location.e_tb_f | numFormat('0.00') }}</div>
                    <div class="box-unit">NTU</div>
                  </div>
                </div> -->
                <!-- <div class="box-contents">
                  <div class="box-contents__text">후 탁도</div>
                  <div class="box-contents__box">
                    <div class="box-text width50">{{ this.$store.state.sedimentation.popup.location.e_ser_tb_b | numFormat('0.00') }}</div>
                    <div class="box-unit">NTU</div>
                  </div>
                </div> -->
              </div>
              <div class="line six">
                <div class="line__num">#1</div>
                <div class="line__status" :class="[ this.$store.state.sedimentation.popup.location.e_drawing_vv_1 === 1 ? 'on' : 'off']"></div>
              </div>
              <div class="line five">
                <div class="line__num">#2</div>
                <div class="line__status" :class="[ this.$store.state.sedimentation.popup.location.e_drawing_vv_2 === 1 ? 'on' : 'off']"></div>
              </div>
              <div class="line  four">
                <div class="line__num">#3</div>
                <div class="line__status" :class="[ this.$store.state.sedimentation.popup.location.e_drawing_vv_3 === 1 ? 'on' : 'off']"></div>
              </div>
              <div class="line three">
                <div class="line__num">#4</div>
                <div class="line__status" :class="[ this.$store.state.sedimentation.popup.location.e_drawing_vv_4 === 1 ? 'on' : 'off']"></div>
              </div>
              <div class="line two">
                <div class="line__num">#5</div>
                <div class="line__status" :class="[ this.$store.state.sedimentation.popup.location.e_drawing_vv_3 === 1 ? 'on' : 'off']"></div>
              </div>
              <div class="line one">
                <div class="line__num">#6</div>
                <div class="line__status" :class="[ this.$store.state.sedimentation.popup.location.e_drawing_vv_4 === 1 ? 'on' : 'off']"></div>
              </div>
              <div class="contents-img-box">
                <div class="contents-img-box__top" :style="{ marginLeft: (70 + (this.$store.state.sedimentation.popup.location.e_sc_f * this.scDividedValue)) + 'px' }"></div>
                <div class="contents-img-box__bottom" :style="{ marginLeft: (this.$store.state.sedimentation.popup.location.e_sc_b * this.scDividedValue) + 'px' }"></div>
              </div>
              <div class="water-h-flow-img">
                <div class="buble delay1"></div>
                <div class="buble delay2"></div>
                <div class="buble delay3"></div>
              </div>
            </div>
          </div>
          <div class="chart-container">
            <div class="chart-box">
              <div class="chart-title">슬러지 발생량 예측</div>
              <div class="chart">
                <highcharts :options="ChartSludge" style="height:100%;"/>
              </div>
            </div>
            <!-- <div class="chart-box">
              <div class="chart-title">계면계 수위</div>
              <div class="chart">
                <highcharts :options="ChartMeter" style="height:100%;"/>
              </div>
            </div> -->
          </div>
        </div>
      </div>
    </div>
</template>
<script>
import { CLOSE_POPUP } from '@/store/modules/sedimentation'
import { PUT_SEDIMENTATION_INTERFACE, GET_SEDIMENTATION_LOCATION_BY_JI } from '@/store/modules/sedimentation'
export default {
  name: 'SedimentationPopup',
  data: () => ({
    timer: null, // API 요청 타이머
    scDividedValue: 6.7, // 대차 위치 계산 상수
    // 슬러지 발생량 예측 차트
    chartDataSludge: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        zoomType: 'x'
      },
      title: {
        useHTML: true,
        text: '슬러지 발생량 예측',
        style: {
          color: 'transparent'
        }
      },
      legend: {
        enabled: false,
      },
      credits: {
        enabled: false
      },
      tooltip: {
        valueDecimals: 2,
        xDateFormat: '%Y-%m-%d %H:%M:%S',
        valueSuffix: 'ton'
      },
      xAxis: {
        title: {
          text: ''
        },
        type: 'datetime',
        categories: [],
        labels: {
          format: '{value:%m-%d %H:%M}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        },
        tickInterval: 1000 * 60 * 60 * 12 
      },
      yAxis: {
        title: {
          align: 'high',
          text: 'ton',
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
    },
    // 계면계 수위 차트
    chartDataMeter: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        zoomType: 'x'
      },
      title: {
        useHTML: true,
        text: '계면계 수위',
        style: {
          color: 'transparent'
        }
      },
      legend: {
        enabled: false,
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
          format: '{value:%H:%M}',
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
          x: -10,
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
    // 슬러지 발생량 예측 차트 옵션
    ChartSludge: function () {
      let chart = this.chartDataSludge
      let now = new Date()
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_슬러지 발생량 예측 ' + this.$store.state.sedimentation.popup.numJi + '지'
      chart.xAxis.plotLines = []
      chart.xAxis.plotLines.push({
        color: 'white', 
        dashStyle: 'ShortDash',
        value: now.getTime(), 
        width: 3,
      })
      if (this.$store.state.sedimentation.popup.location.e_location_sludge_trend !== null && Object.keys(this.$store.state.sedimentation.popup.location.e_location_sludge_trend).length > 0) {
        let data = []
        for (let key in this.$store.state.sedimentation.popup.location.e_location_sludge_trend) {
          data.push([new Date(key).getTime(), this.$store.state.sedimentation.popup.location.e_location_sludge_trend[key]])
          console.log(this.$store.state.sedimentation.popup.location.e_location_sludge_trend[key], this.$store.state.sedimentation.latestModify.e_sc_set_sludge_q)
          if (this.$store.state.sedimentation.popup.location.e_location_sludge_trend[key] >= this.$store.state.sedimentation.latestModify.e_sc_set_sludge_q) {
            chart.xAxis.plotLines.push({
              color: 'red', 
              dashStyle: 'ShortDash',
              value: new Date(key).getTime(),
              width: 3,
            })
          }
        }
        chart.series = [{
          name: '슬러지 발생량',
          data: data
        }]
        return chart
      }
      return chart
    },
    // 계면계 수위 발생 차트 옵션
    ChartMeter: function () {
      let chart = this.chartDataMeter
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_계면계 수위 ' + this.$store.state.sedimentation.popup.numJi + '지'
      if (this.$store.state.sedimentation.popup.interface.series !== null && Object.keys(this.$store.state.sedimentation.popup.interface.series).length > 0) {
        let dataSeries = []
        for (let key in this.$store.state.sedimentation.popup.interface.series) {
          dataSeries.push([new Date(key).getTime(), this.$store.state.sedimentation.popup.interface.series[key]])
        }
        chart.series = [{
          name: '계면계 수위',
          data: dataSeries,
          color: 'rgb(110, 157, 225)'
        }]
        return chart
      }
      return chart
    }
  },
  methods: {
    /**
     * 팝업이 닫힘 버튼 선택시 
     * 타이머 종료
     */
    closePopup: function () {
      clearInterval(this.timer)
      this.chartDataSludge.series = null
      this.chartDataMeter.series = null
      this.$store.commit('sedimentation/' + CLOSE_POPUP)
    },
    // fullscreen 이벤트
    // fullscreenchanged: function () {
    //   console.log('fullscreenchange')
    //   if(document.fullscreenElement) {
    //     this.chartDataSludge.title.style.color = 'white'
    //     this.chartDataMeter.title.style.color = 'white'
    //   } else {
    //     this.chartDataSludge.title.style.color = 'transparent'
    //     this.chartDataMeter.title.style.color = 'transparent'
    //   }
    // }
  },
  created: function () {
    console.log(this.$options.name + ' created')
  },
  /**
   * 마운트시 
   * fullscreenchange 이벤트 등록
   */
  // mounted: function () {
  //   console.log(this.$options.name + ' mounted')
  //   window.addEventListener('fullscreenchange', this.fullscreenchanged)
  // },
  /**
   * 마운트 해제시 
   * fullscreenchange 이벤트 해제
   */
  // beforeDestroy () { window.removeEventListener('fullscreenchange', this.fullscreenchanged) },
  destroyed: function () {
    console.log(this.$options.name + ' destoryed')
  },
  updated: function () {
    console.log(this.$options.name + ' updated')
  },
  watch: {
    // 팝업 열림/닫힘 값 변화 감지
    '$store.state.sedimentation.popup.visible': function (newVal) {
      if (newVal) {
        this.timer = setInterval( () => {
           Promise.all([
            this.$store.dispatch(GET_SEDIMENTATION_LOCATION_BY_JI, { numJi: this.$store.state.sedimentation.popup.numJi }),
            this.$store.dispatch(PUT_SEDIMENTATION_INTERFACE, { numJi: this.$store.state.sedimentation.popup.numJi })
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
      position: relative;
      width: 1163px;
      height: 813px;
      background-image: url('../../assets/sedimentation/popup_contents.png');
      background-position-x: -5px;
      padding: 31px 24.4px 40px 33.7px;
      .chart-container{
        display: flex;
        width: 100%;
        height: 325px;
        margin-top: 10px;
        height: 325px;
        .chart-box{
          width: 100%;
          height: 100%;
          .chart{
            width: 100%;
            height: 272px;
          }
          .chart-title{
            width: 223px;
            height: 53px;
            background-image: url('../../assets/sedimentation/bottom_title_img.png');
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2.7;
            letter-spacing: normal;
            text-align: left;
            color: #fff;
            padding-left: 40px;
          }
        }
      }
      .contents{
        display: flex;
        width: 100%;
        height: 277px;
        margin-top: 36px;
        .contents-img{
          position: absolute;
          width: 912px;
          height: 277px;
          top: 175px;
          left: 194px;
          background-image: url('../../assets/sedimentation/ji_contents.png');
          .water-h-flow-img{
            position: absolute;
            width: 136px;
            height: 8px;
            top: 230px;
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
            0% {opacity:0; transform: translateX(0px);}
            50% {opacity:1; }
            90% {opacity:1; }
            100% {opacity:0; transform: translateX(150px);}
          }
          .contents-img-box{
            width: 582px;
            height: 150px;
            position: absolute;
            top: 63px;
            right: 0px;
            &__top{
              width: 113px;
              height: 69px;
              position: relative;
              top: 10px;
              background-image: url('../../assets/sedimentation/right_top_contents.png');
            }
            &__bottom{
              width: 134px;
              height: 78px;
              margin-left: 377px;
              background-image: url('../../assets/sedimentation/left_top_contents.png');
            }
          }
          .six{
            top: 104px;
            left: 312px;
          }
          .five{
            top: 123px;
            left: 291px;
          }
          .four{
            top: 142px;
            left: 271px;
          }
          .three{
            top: 161px;
            left: 250px;
          }
          .two{
            top: 180px;
            left: 229px;
          }
          .one{
            top: 198px;
            left: 207px;
          }
          .line{
            position: absolute;
            justify-content: center;
            align-items: center;
            display: flex;
            flex-flow: column;
            &__num{
              width: 27px;
              height: 27px;
              background-image: url('../../assets/sedimentation/line_arrow.png');
              font-size: 12px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              line-height: 2.2;
              letter-spacing: normal;
              text-align: center;
              color: #fff;
            }
            &__status{
              width: 45px;
              height: 35px;
            }
            .on{
              background-image: url('../../assets/sedimentation/line_on.png');
            }
            .off{
              background-image: url('../../assets/sedimentation/line_off.png');
            }
          }
          .contents-box{
            display: flex;
            width: 425px;
            position: absolute;
            left: 464px;
            padding-top: 6px;
            .marginsideauto{
                margin: 0 auto;
            }
            .box-contents{
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
                  text-align: center;
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
        }
        .contents-value{
          height: 100%;
          margin-right:31px;
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
              text-align: center;
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
      .top-title{
        width: 113px;
        height: 53px;
        margin-top: 35px;
        padding-left: 30px;
        background-image: url('../../assets/sedimentation/information_title.png');
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
      .information{
        position: absolute;
        top: 33px;
        right: 25px;
        display: flex;
        flex-direction: column;
        flex-wrap: nowrap;
        width: 100%;
        height: 67px;
        align-items: center;
        padding-right: 32px;
        .flex-container {
          width: 100%;
          height: 100%;
          margin-bottom: 5px;
          display: flex;
          flex-direction: row;
          flex-wrap: nowrap;
          align-items: center;
        }
        .top-timer-text{
          text-shadow: 0 0 9px #5cafff;
          font-size: 21px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 1.1;
          letter-spacing: normal;
          text-align: left;
          color: #c3eaff;
          margin-left: auto;
        }
        .top-timer{
          display: flex;
          justify-content: center;
          width: 272.9px;
          height: 66.2px;
          padding-top: 10px;
          margin-left: 16px;
          background-image: linear-gradient(91deg, rgba(67, 109, 132, 0) 0%, #2d67a2 54%, rgba(68, 97, 137, 0) 100%);
          &__num{
            text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
            font-family: "LAB디지털" !important;
            font-size: 40px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 1.2;
            letter-spacing: normal;
            text-align: center;
            color: #ccf1ff;
            margin-left:5px;
          }
          &__text{
            font-size: 16px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 4.13;
            letter-spacing: normal;
            text-align: left;
            color: #c3eaff;
            margin-left:3px;
          }
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
          z-index: 9;
        }
      }
    }
  }
}
</style>