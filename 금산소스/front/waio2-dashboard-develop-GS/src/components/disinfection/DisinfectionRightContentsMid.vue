<template>
  <div class="main">
    <div class="top-contents">
      <div class="arrow-animate-one"></div>
      <div class="arrow-animate-two"></div>
      <div class="one-contents">
        <div class="one-contents__title">주요인자</div>
        <div class="big-contents">
          <div class="contents-value">
            <!-- <div class="value-container">
              <div class="value-container__one">· 수온(°C)</div>
              <div class="value-container__two">{{ this.$store.state.disinfection.latest.b_te | numFormat('0.00') }}</div>
            </div> -->
            <!-- <div class="value-container">
              <div class="value-container__one">· 기온(°C)</div>
              <div class="value-container__two">{{ this.$store.state.disinfection.latest.air_te | numFormat('0.00') }}</div>
            </div> -->
            <!-- <div class="value-container">
              <div class="value-container__one">· 정수 pH</div>
              <div class="value-container__two">{{ this.$store.state.disinfection.latest.h_ph | numFormat('0.00') }}</div>
            </div> -->
            <!-- <div class="value-container">
              <div class="value-container__one"></div>
              <div class="value-container__title">생활차염</div>
              <div class="value-container__title">차염B</div>
            </div> -->
            <!-- <div class="value-container">
              <div class="value-container__one">· 적용 유량(m<sup>3</sup>/h)</div>
              <div class="value-container__two">{{ this.$store.state.disinfection.latest.b_in_fr | numFormat('0,0') }}</div> -->
              <!-- <div class="value-container__two">{{ this.$store.state.disinfection.latest.d_ser_in_fr | numFormat('0,0') }}</div> -->
            <!-- </div> -->
            <div class="value-container">
              <div class="value-container__one">. 정수 pH</div>
              <div class="value-container__two">{{ this.$store.state.disinfection.latest.h_ph | numFormat('0.00') }}</div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 침전지 잔류 염소(ppm)</div>
              <div class="value-container__two">{{ this.$store.state.disinfection.latest.g_e_residual_cl | numFormat('0.00') }}</div>
              <!-- <div class="value-container__two">{{ this.$store.state.disinfection.latest.d_ser_cl | numFormat('0.0') }}</div> -->
            </div>
            <div class="value-container">
              <div class="value-container__one">· 여과수 통합 잔류 염소(ppm)</div>
              <div class="value-container__two">{{ this.$store.state.disinfection.latest.g_f_out_residual_cl | numFormat('0.00') }}</div>
              <!-- <div class="value-container__two">{{ this.$store.state.disinfection.latest.d_ser_cl | numFormat('0.0') }}</div> -->
            </div>
            <div class="value-container">
              <div class="value-container__one">· 현재 주입률(ppm)</div>
              <div class="value-container__two">{{ this.$store.state.disinfection.latest.g_peri_chol_rate | numFormat('0.00') }}</div>
              <!-- <div class="value-container__two">{{ this.$store.state.disinfection.latest.g_pre2_chlorination | numFormat('0.0') }}</div> -->
            </div>
          </div>
        </div>
      </div>
      <div class="three-contents">
        <div class="three-contents__title">
          예측결과
          <div class="three-contents__timerbox-outter">
            <div class="timerbox">
              {{ this.$store.state.disinfection.latest.upd_ti | moment('YYYY-MM-DD HH:mm:ss') }}
            </div>
          </div>
        </div>
        <div class="three-small-contents">
          <div class="little-title-two">AI 다음목표 주입률 예측</div>
          <div class="real-box real-two">
            <div class="real-text">현재 주입률</div>
            <div class="real-value">{{ this.$store.state.disinfection.latest.g_peri_chol_rate | numFormat('0.00') }}</div>
          </div> 
          <div class="three-small-contents-value">
            <div class="three-small-contents-value__title">차염</div>
            <div class="three-value">
              <div class="three-value__num">{{ this.$store.state.disinfection.latest.ai_g_chol_rate | numFormat('0.00') }}</div>
              <div class="three-value__unit">ppm</div>
            </div>
            <div class="three-contents-value-underbar"></div>
          </div>
        </div>
      </div>
    </div>
    <div class="bottom-contents">
      <div class="contents">
        <div class="contents__title">차염 주입률 예측 트렌드</div>
        <div class="contents-gragh">
          <highcharts :options="ChartOptionCholRate" style="height:100%;"/>
        </div>
      </div>
      <div class="contents paddingleft">
        <div class="contents__title">여과지 잔류염소 트렌드</div>
        <div class="contents-gragh">
          <highcharts :options="ChartOptionCl" style="height:100%;"/>
        </div>
      </div>
    </div>
  </div>  
</template>

<script>
import * as XLSX from 'xlsx'

export default {
  data() {
    return {
      selectedLine: 1,
      series1: [],
      series2: [],
      chartOptionCholRate: {
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
          valueSuffix: 'ppm'
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
            text: 'ppm',
            useHTML: true,
            offset: 0,
            rotation: 0,
            x: -10,
            y: -20,
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
      chartOptionCl: {
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
          valueSuffix: 'ppm'
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
            text: 'ppm',
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
          },
          min: 0,
          max: 1.5
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
    } 
  },
  computed: {
    ChartOptionCholRate: function () {
      let chart = this.chartOptionCholRate
      let now = new Date()
      chart.xAxis.plotLines = [{
        color: 'white', 
        dashStyle: 'ShortDash', 
        value: now.getTime(),
        width: 3, 
      }]
      chart.series = []
      if (this.$store.state.disinfection.cholrateTrend !== null && Object.keys(this.$store.state.disinfection.cholrateTrend).length > 0) {
        let dataSeries1 = []
        for (let key in this.$store.state.disinfection.cholrateTrend) {
          dataSeries1.push([new Date(key).getTime(), this.$store.state.disinfection.cholrateTrend[key]])
        }
        chart.series.push({
          name: '주입률',
          data: dataSeries1,
          color: 'rgb(110, 157, 225)'
        })
      }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_전차염 주입률 예측 트렌드'
      return chart
    },

    ChartOptionCl: function () {
      let chart = this.chartOptionCl
      chart.series = []
      if (this.$store.state.sedimentation.clTrend.series1 !== null && Object.keys(this.$store.state.sedimentation.clTrend.series1).length > 0) {
        let dataSeries1 = []
        for (let key in this.$store.state.sedimentation.clTrend.series1) {
          dataSeries1.push([new Date(key).getTime(), this.$store.state.sedimentation.clTrend.series1[key]])
        }
        chart.series.push({
          name: '잔류염소',
          data: dataSeries1,
          color: 'rgb(110, 157, 225)',
          marker: {
            symbol: 'circle'
          }
        })
      }
      // if (this.$store.state.sedimentation.clTrend.series2 !== null && Object.keys(this.$store.state.sedimentation.clTrend.series2).length > 0) {
      //   let dataSeries2 = []
      //   for (let key in this.$store.state.sedimentation.clTrend.series2) {
      //     dataSeries2.push([new Date(key).getTime(), this.$store.state.sedimentation.clTrend.series2[key]])
      //   }
      //   chart.series.push({
      //     name: '2계열',
      //     data: dataSeries2,
      //     color: 'rgb(146, 66, 235)',
      //     marker: {
      //       symbol: 'circle'
      //     }
      //   })
      // }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_침전수 잔류염소 트렌드'
      return chart
    }
  },

  methods: {
    downloadExcel: function() {
      const eva = this.$store.state.disinfection.pre_evaporation
      
      for (let i = 0; i < Object.keys(eva).length; i++) {
        for (let eva_obj in eva['series' + (i + 1)]) {
          const excelLogs = {
            DateTime: '', 
            증발량: '',
          };
          // console.log('eva_obj', eva_obj, eva['series' + (i + 1)][eva_obj])

        excelLogs.DateTime = this.$moment(eva_obj).format('YYYY-MM-DD HH:mm')
        excelLogs.증발량 = eva['series' + (i + 1)][eva_obj]

        const eva_length = Object.keys(eva['series' + (i + 1)]).length
        
        if ( i === 0 ) {
          if( eva_length != this.series1.length ) {
            this.series1.push(excelLogs);
          }
        } else if ( i === 1 ) {
            if( eva_length != this.series2.length ) {
              this.series2.push(excelLogs);
            }
          }
        }
      }
      // 엑셀 워크시트로 json 내보내기
      const wb = XLSX.utils.book_new();
      const dataWS1 = XLSX.utils.json_to_sheet(this.series1);
      const dataWS2 = XLSX.utils.json_to_sheet(this.series2);
      
      XLSX.utils.book_append_sheet(wb, dataWS1, '#1'); 
      XLSX.utils.book_append_sheet(wb, dataWS2, '#2');
      XLSX.writeFile(wb, `${this.$moment().format('YYYYMMDDHHmmss')}_차염 증발량 예측 비교 트렌드.xlsx`);
    }
  },

  // mounted: function () {
  //   window.addEventListener('fullscreenchange', this.fullscreenchanged)
  // },
  // beforeDestroy () { window.removeEventListener('fullscreenchange', this.fullscreenchanged) },
}
</script>
<style lang="scss" scoped>
//현재 주입률 박스
.real-two {
  top: 100px;
  left: 135px;
}
.real-box {
  display: flex;
  align-items: center;
  position: absolute;
  .real-text {
    text-shadow: 0 0 9px #a0d0ff;
    font-family: KHNPHUotfR;
    font-size: 16px;
    text-align: left;
    color: #23a7c7;
  }
  .real-value {
    text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
    font-family: "LAB디지털" !important;
    font-size: 23px;
    text-align: center;
    color: #23a7c7;
    margin-left: 7px;
  }
} 
.little-title-one{
  position: absolute;
  text-shadow: 0 0 9px #5cafff;
  font-size: 21px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.1;
  letter-spacing: normal;
  text-align: left;
  color: #c3eaff;
  top: 70px;
  left: 64px;
}
.little-title-two{
  position: absolute;
  text-shadow: 0 0 9px #5cafff;
  font-size: 21px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.1;
  letter-spacing: normal;
  text-align: left;
  color: #c3eaff;
  top: 70px;
  left: 48px;
}
.arrow-animate-one{
  position: absolute;
  top: 252px;
  left: 1035px;
  background-image: url('../../assets/splashdown/arrow_img.png');
  width: 85px;
  height: 356px;
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
// .arrow-animate-two {
  //   position: absolute;
  //   top: 250px;
  //   z-index: 10;
  //   left: 1377px;
  //   background-image: url('../../assets/splashdown/arrow_img.png');
  //   width: 85px;
  //   height: 356px;
  //   animation-name: big-arrow-two;
  //   animation-duration: 5s;
  //   animation-timing-function: linear;
  //   animation-iteration-count: infinite;
// }
// @keyframes big-arrow-two{ 
  //   0% {opacity:0;}
  //   12% {opacity:0;}
  //   24% {opacity:0;}
  //   36% {opacity:0;}
  //   48% {opacity:0; transform: translateX(-5px);}
  //   60% {opacity:0.5; transform: translateX(-2px);}
  //   72% {opacity:1; transform: translateX(0px);}
  //   84% {opacity:0.5; transform: translateX(2px);}
  //   100% {opacity:0; transform: translateX(5px);}
// }
.main{
  width: 1202px;
  height: 794px;
  padding-left: 25px;
}
.bottom-contents{
  display: flex;
  width: 100%;
  height: 373px;
  padding-top: 20px;
  .paddingleft{
    padding-left: 10px;
  }
  .contents{
    position: relative;
    width: 50%;
    height: 100%;
    .select-box{
      position: absolute;
      right: 20px;
      top: 0px;
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
    .contents-gragh{
      width: 100%;
      height: 306px;
      background-size: contain;
    }
    &__title{
      height:47px;
      background-image: url('../../assets/disinfection/gragh_title.png');
      text-shadow: 0 0 9px #5cafff;
      font-size: 18px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.11;
      letter-spacing: normal;
      text-align: left;
      color: #fff;
    }
  }
}
.top-contents{
  display: flex;
  width: 100%;
  height: 420px;
  .three-contents{
    position: relative;
    width: 100%;
    height: 100%;
    .three-small-contents{
      width: 100%;
      height: 356px;
      margin-top: 10px;
      background-image: url('../../assets/ss_images/three_background.png');
      .three-small-contents-value{
        width:415px;
        height: 355px;
        padding-top: 61px;
        margin: 0 auto;
        .margintop{
          margin-top: 40px;
        }
        .three-contents-value-underbar{
          width: 100%;
          height: 36px;
          background-image: url('../../assets/ss_images/contents_value_underbar.png');
          background-position-x: center;
        }
        .three-value{
          display: flex;
          justify-content: center;
          width: 100%;
          height: 42px;
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
          }
          &__unit{
            font-size: 16px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 4;
            letter-spacing: normal;
            text-align: left;
            color: #417db9;
            margin-left: 10px;
          }
        }
        &__title{
          width: 145px;
          height: 34px;
          background-image: url('../../assets/disinfection/small_contents_title.png');
          text-shadow: 0 0 9px #5cafff;
          font-size: 16px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 2.3;
          letter-spacing: normal;
          text-align: left;
          padding-left: 52px;
          color: #c3eaff;
          padding-left:56px;
          margin-top: 50px;
        }
      }
    }
    &__timerbox-outter {
      display: flex;
      position: relative;
      margin-left: auto;
      top: -3px;
    }
    &__title{
      display: flex;
      align-items: center;
      width: 100%;
      height: 53px;
      padding-left: 40px;
      background-image: url('../../assets/ss_images/contents_right_title.png');
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
  // .two-contents{
    //   position: relative;
    //   left: -30px;
    //   width: 424px;
    //   height: 100%;
    //   .small-contents{
      //     width: 424px;
      //     height: 356px;
      //     margin-top: 10px;
      //     background-image: url('../../assets/disinfection/small_contents_background.png');
      //     .small-contents-value{
        //       width: 316px;
  //       height: 355px;
  //       padding-top: 20px;
  //       .roling-box{
  //         height: 57px;
  //         display: flex;
  //         justify-content: center;
  //       }
  //       .chart{
  //         width: 100%;
  //         height: 135px;
  //       }
  //       .chart-value{
  //         display: flex;
  //         width: 100%;
  //         padding-left: 10px;
  //         .value-box{
  //           display: flex;
  //           flex-flow: column;
  //           align-items: center;
  //           margin: 0 4px;
  //           &__text{
  //             text-shadow: 0 0 9px #5cafff;
  //             font-size: 16px;
  //             font-weight: normal;
  //             font-stretch: normal;
  //             font-style: normal;
  //             line-height: 1.56;
  //             letter-spacing: normal;
  //             text-align: left;
  //             color: #fff;
  //           }
  //           &__text1{
  //             text-shadow: 0 0 9px #5cafff;
  //             font-size: 12px;
  //             font-weight: normal;
  //             font-stretch: normal;
  //             font-style: normal;
  //             line-height: 1;
  //             letter-spacing: normal;
  //             text-align: center;
  //             color: #fff;
  //           }
  //           &__value{
  //             margin-top: 8px;
  //             width: 64.6px;
  //             height: 41.6px;
  //             border: solid 1px rgba(157, 191, 255, 0.3);
  //             text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
  //             font-family: "LAB디지털" !important;
  //             font-size: 20px;
  //             font-weight: normal;
  //             font-stretch: normal;
  //             font-style: normal;
  //             line-height: 1.8;
  //             letter-spacing: normal;
  //             text-align: center;
  //             color: #ccf1ff;
  //           }
  //         }
  //       }
  //       .margintop{
  //         margin-top: 40px;
  //       }
  //       .contents-value-underbar{
          //         width: 316px;
          //         height: 36px;
          //         background-image: url('../../assets/disinfection/contents_value_underbar.png');
          //         background-position-x: center;
        //       }
  //       .value{
          //         display: flex;
          //         justify-content: center;
          //         width: 100%;
          //         height: 42px;
          //         &__num{
            //           text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
            //           font-family: "LAB디지털" !important;
            //           font-size: 40px;
            //           font-weight: normal;
            //           font-stretch: normal;
            //           font-style: normal;
            //           line-height: 1.2;
            //           letter-spacing: normal;
            //           text-align: center;
            //           color: #ccf1ff;
          //         }
  //         &__unit{
            //           font-size: 16px;
            //           font-weight: normal;
            //           font-stretch: normal;
            //           font-style: normal;
            //           line-height: 4;
            //           letter-spacing: normal;
            //           text-align: left;
  //           color: #417db9;
  //           margin-left: 10px;
  //         }
  //       }
  //       &__title{
  //         width: 85px;
  //         height: 34px;
  //         background-image: url('../../assets/disinfection/small_contents_title.png');
          //         text-shadow: 0 0 9px #5cafff;
          //         font-size: 16px;
          //         font-weight: normal;
          //         font-stretch: normal;
          //         font-style: normal;
          //         line-height: 2.3;
          //         letter-spacing: normal;
          //         text-align: right;
  //         color: #c3eaff;
  //       }
  //     }
  //   }
  //   &__title{
  //     width: 297px;
  //     height: 53px;
  //     padding-left: 40px;
  //     background-image: url('../../assets/disinfection/contents_right_title.png');
      //     text-shadow: 0 0 9px #5cafff;
      //     font-size: 18px;
      //     font-weight: normal;
      //     font-stretch: normal;
      //     font-style: normal;
      //     line-height: 2.8;
      //     letter-spacing: normal;
      //     text-align: left;
      //     color: #fff;
    //   }
  // }
  .one-contents{
    width: 508px;
    height: 100%;
    .big-contents{
      margin-top: 10px;
      width: 508px;
      height: 356px;
      padding-top: 16px;
      background-image: url('../../assets/disinfection/big_contents_background.png');
      .contents-value{
        display: flex;
        flex-direction: column;
        justify-content: space-evenly;
        width: 372px;
        height: 307px;
        background-image: url('../../assets/disinfection/value_factor.png');
        padding: 15px 13px;
        .value-container{
          display: flex;
          width: 100%;
          &__title{
            flex: 1;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            letter-spacing: normal;
            text-align: center;
            color: #c3eaff;
          }
          &__one{
            width: 235px;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            letter-spacing: normal;
            text-align: left;
            color: #9fc4ff;
          }
          &__two{
            flex: 1;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            letter-spacing: normal;
            text-align: center;
            color: #fff;
          }
        }
      }
    }
    &__title{
      width: 297px;
      height: 53px;
      padding-left: 40px;
      background-image: url('../../assets/disinfection/contents_right_title.png');
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
}
</style>