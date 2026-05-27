<template>
  <div class="main">
    <!-- 상단 컨텐츠(주요인자, 알고리즘 예측, 예측 결과) -->
    <div class="top-contents">
      <div class="arrow-animate-one"></div>
      <div class="arrow-animate-two"></div>
      <!-- 주요인자 -->
      <div class="one-contents">
        <div class="one-contents__title">주요인자</div>
        <div class="big-contents">
          <div class="contents-value">
            <div class="value-container">
              <div class="value-container__one">· 원수 탁도</div>
              <div class="value-container__two">{{ this.$store.state.coagulants.latest.b_tb | numFormat('0.00') }}</div>
              <div class="value-container__three">NTU</div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 원수 pH</div>
              <div class="value-container__two">{{ this.$store.state.coagulants.latest.b_ph | numFormat('0.00') }}</div>
              <div class="value-container__three"></div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 원수 수온</div>
              <div class="value-container__two">{{ this.$store.state.coagulants.latest.b_te | numFormat('0.00') }}</div>
              <div class="value-container__three">℃</div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 원수 전기전도도</div>
              <div class="value-container__two">{{ this.$store.state.coagulants.latest.b_cu | numFormat('0.00') }}</div>
              <div class="value-container__three">μS/cm</div>
            </div>
          </div>
        </div>
      </div>
      <div class="arrow-animate-one"></div>
      <!-- 예측 결과 -->
      <div class="two-contents">
        <div class="two-contents__title">예측 결과</div>
        <div class="small-contents">
          <div class="top-info">* 침전지 탁도 1NTU이하로 운영</div>          
          <div class="real-box">
            <div class="real-text">현재주입률</div>
            <div class="real-value">{{this.$store.state.coagulants.latest.c_injector1 == 1 ? this.$store.state.coagulants.latest.c1_cf : this.$store.state.coagulants.latest.c2_cf  | numFormat('0.00') }}</div>
          </div>
          <div class="small-contents-value">
            <div class="small-contents-value__title"> <span>{{this.$store.state.coagulants.latest.c_injector1 == 1 ? '1' : '2'}}호기</span> {{ this.$store.state.coagulants.latest.c_cf_coagulant }}</div>
            <div class="value">
              <div class="value__num">{{ this.$store.state.coagulants.latest.ai_c_cf_norm_co | numFormat('0.00') }}</div>
              <div class="value__unit">ppm</div>
            </div>
            <div class="contents-value-underbar"></div>
            <div class="user-value-wrap">
              <div class="user-value">* 사용자 보정 값</div>
              <div class="user-value-num">
                <!-- <span class="user-text">50.00</span> -->
                <input class="user-num" v-if="this.$store.state.coagulants.isModifyModeUser" type="text"
                :value="this.$store.state.coagulants.latestModify.c_user_correct" v-on:input="updateInput($event, 'c_user_correct')" maxlength="5">
                <span v-else>{{ this.$store.state.coagulants.latestModify.c_user_correct | numFormat('0.00') }}</span>
              </div>
              <div v-if="$store.state.login.user.tkn !== null" class="contents-value__icon">
                <div class="custom-icon" @click="updateControl">
                  <div :class="[ this.$store.state.coagulants.isModifyModeUser ? 'custom-icon__checkbox' : 'custom-icon__pencil' ]"></div>
                </div>
                <div v-if="this.$store.state.coagulants.isModifyModeUser" class="custom-cancel-icon" style="margin-top: 3px;" @click="cancelControl">
                  <div class='custom-cancel-icon__cancel'></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>        
      <div class="arrow-animate-two"></div>
      <!-- 최종 결과 -->
      <div class="three-contents">
        <div class="three-contents__title">최종 결과
          <div class="three-contents__timerbox-outter">
            <!-- 예측 결과 시간 -->
            <div class="timerbox">
              {{ this.$store.state.coagulants.latest.upd_ti  | moment('YYYY-MM-DD HH:mm:ss') }}
            </div>
          </div>
        </div>
        <div class="three-small-contents">
          <div class="three-small-contents-value">
            <div class="three-small-contents-value__title"><span>{{this.$store.state.coagulants.latest.c_injector1 == 1 ? '1' : '2'}}호기</span> {{ this.$store.state.coagulants.latest.c_cf_coagulant }}</div>
            <div class="three-value">
              <div class="three-value__num">{{ this.$store.state.coagulants.latest.ai_c_cf | numFormat('0.00') }}</div>
              <div class="three-value__unit">ppm</div>
            </div>
            <div class="three-contents-value-underbar"></div>
          </div>
        </div>
      </div>        
      <!-- 예측 결과 -->
      <!-- <div class="three-contents">
        <div class="three-contents__title">
          예측 결과
          <div class="three-contents__timerbox-outter">
            예측 결과 시간
            <div class="timerbox">
              {{ this.$store.state.coagulants.latest.upd_ti  | moment('YYYY-MM-DD HH:mm:ss') }}
            </div>
          </div>
        </div>
        <div class="three-small-contents">  
          <div class="real-box real-one">
            <div class="real-text">현재 주입률</div>
            <div class="real-value">{{this.$store.state.coagulants.latest.c_injector1 == 1 ? this.$store.state.coagulants.latest.c1_cf : this.$store.state.coagulants.latest.c2_cf  | numFormat('0.00') }}</div>
          </div>       
          <div class="three-small-contents-value">
            <div class="three-small-contents-value__title">
              <span>{{this.$store.state.coagulants.latest.c_injector1 == 1 ? '1' : '2'}}호기</span> {{ this.$store.state.coagulants.latest.c_cf_coagulant }}</div>
            <div class="three-value">
              <div class="three-value__num">{{ this.$store.state.coagulants.latest.ai_c_cf | numFormat('0.00') }}</div>
              <div class="three-value__unit">ppm</div>
            </div>
            <div class="three-contents-value-underbar"></div>
          </div>
        </div>
      </div> -->
    </div>
    <!-- 하단 컨텐츠(그래프) -->
    <div class="bottom-contents">
      <!-- 막여과 유입수 탁도 정규 분포 그래프 -->
      <div class="contents">
        <div class="contents__title">막여과 유입수 탁도 정규 분포</div>
        <div class="contents-gragh">
          <highcharts :options="ChartScatter" style="height:100%;"/>
        </div>
      </div>
      <!-- 막여과 유입수 탁도 트랜드 -->
      <div class="contents paddingleft">
        <div class="contents__title">막여과 유입수 탁도</div>
        <div class="contents__real-box">
          <div class="contents__real-text">현재 막여과 유입수 탁도</div>
          <div class="contents__real-value">{{ this.$store.state.coagulants.latest.e1_tb_b | numFormat('0.00') }}</div>
        </div>
        <div class="contents-gragh">
          <highcharts :options="ChartTrend" style="height:100%;"/>
        </div>
      </div>
    </div>
  </div>  
</template>

<script>
import Highcharts from 'highcharts'
import { PUT_COAGULANT_CONTROL_USER_CORRECT } from '@/store/modules/coagulants'

export default {
  data: () => ({
    // 막여과 유입수 탁도 정규 분포 차트 옵션
    chartScatterOption: {
      chart: {
        type: 'scatter',
        zoomType: 'x',
        selectionMarkerFill: 'rgba(173, 92, 51, 0.25)',
        backgroundColor: 'transparent',
      },
      title: {
        useHTML: true,
        text: '막여과 유입수 탁도 정규 분포',
        style: {
          color: 'transparent'
        }
      },
      series: [
      ],
      legend: {
        enabled: false,
        align: 'right',
        verticalAlign: 'top',
        borderWidth: 0,
        y: 0,
        itemStyle: {
          color: 'rgb(255,255,255,0.8)',
          fontWeight: 'bold'
        }
      },
      credits: {
        enabled: false
      },
      xAxis: {
        labels: {
          style: {
            color: 'rgb(255,255,255,0.8)'
          }
        },
        tickInterval: 0.5,
        min: 0
      },
      yAxis: {
        title: {
          text: ''
        },
        labels: {
          style: {
            color: 'rgb(255,255,255,0.8)'
          }
        },
        gridLineColor: false
      },
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
    // 막여과 유입수 탁도 트랜트 차트 옵션
    chartDataTrend: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        zoomType: 'x'
      },
      title: {
        useHTML: true,
        text: '막여과 유입수 탁도 트랜드',
        style: {
          color: 'transparent'
        }
      },
      legend: {
        enabled: false,
        align: 'right',
        verticalAlign: 'top',
        borderWidth: 0,
        y: 0,
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
     /* tooltip: {
        valueDecimals: 2,
        xDateFormat: '%Y-%m-%d %H:%M:%S',
        valueSuffix: 'NTU'
      },*/
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
        tickInterval: 1000 * 60 * 60 * 3 // 48시간
      },
      yAxis: {
        title: {
          align: 'high',
          text: 'NTU',
          useHTML: true,
          offset: 0,
          rotation: 0,
          x: -10,
          y: -15,
          style: {
            color:"rgb(255,255,255,0.8)"
          }
        },
      //  max: 1.2,
       /* plotLines: [{
          color: 'white',
          dashStyle: 'ShortDash',
          value: 1,
          width: 3,
          label: {
            text: '운영기준',
            style: {
              color: 'white'
            },
            rotation: 360,
            align: 'right',
            y: -7,
            x: -3
          }
        }], */
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
  components: {
    
  },
  computed: {
    // 클래스 설명
    // ClassDescription() {
    //   if (this.$store.state.coagulants.latest.ai_clst_id !== null && this.$store.state.coagulants.class_info !== null) {
    //     if (this.$store.state.coagulants.class_info[this.$store.state.coagulants.latest.ai_clst_id] !== undefined) {
    //       return this.$store.state.coagulants.class_info[this.$store.state.coagulants.latest.ai_clst_id].description
    //     } else { 
    //       return null
    //     }
    //   }
    //   return null
    // },
    // 1계열 막여과 유입수 탁도 정규 분포 차트 데이터
    ScatterData1() {
      if (this.$store.state.sedimentation.tbScatter.series1 && Object.keys(this.$store.state.sedimentation.tbScatter.series1).length > 0) {
        let ar = []
        Object.keys(this.$store.state.sedimentation.tbScatter.series1).forEach((key) => {
          let parseKey = parseFloat(key)
          ar.push([parseKey, this.$store.state.sedimentation.tbScatter.series1[parseKey]])
        })
        return ar
      }
      return null
    },
    // 2계열 막여과 유입수 탁도 정규 분포 차트 데이터
    // ScatterData2() {
    //   if (this.$store.state.sedimentation.tbScatter.series2 && Object.keys(this.$store.state.sedimentation.tbScatter.series2).length > 0) {
    //     let ar = []
    //     Object.keys(this.$store.state.sedimentation.tbScatter.series2).forEach((key) => {
    //       let parseKey = parseFloat(key)
    //       ar.push([parseKey, this.$store.state.sedimentation.tbScatter.series2[parseKey]])
    //     })
    //     return ar
    //   }
    //   return null
    // },
    // 막여과 유입수 탁도 정규 분포 차트 옵션
    ChartScatter() {
      let chart = this.chartScatterOption
      chart.series = [
        {
          // name: 'PACS 1호기',
          name: '막여과 유입수 탁도',
          data: this.ScatterData1,
          color: '#ed9af1',
          visible: true,
          marker: {
            symbol: 'circle'
          },
          showInLegend: false
        },
        // {
        //   name: 'PACS 2호기',
        //   data: this.ScatterData2,
        //   color: '#25d0cc',
        //   visible: true,
        //   marker: {
        //     symbol: 'circle'
        //   }
        // }
      ]
      chart.tooltip = {
        useHTML: true,
        style: {
            pointerEvents: 'none'
        },
        formatter: function() {
          return `<span style="font-weight: bold; text-align: center;">
                  x: ${this.x}<br/>
                  y: ${this.y}</span>`;
        }
      }

      chart.plotOptions = {
        series: {
          tooltip: {
            pointFormatter: function() {
              return false;
            }
          }
        }
      }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_막여과 유입수 탁도 정규 분포'
      return chart
    },
    // 선택된 클러스터 정보
    // selectedClusterInfo () {
    //   if (this.$store.state.coagulants.latest.ai_clst_id !== null && this.$store.state.coagulants.cluster_info !== null) {
    //     return this.$store.state.coagulants.cluster_info.find((elem) => {
    //       console.log(elem.cluster_id, this.$store.state.coagulants.latest.ai_clst_id)
    //       if (elem.cluster_id === this.$store.state.coagulants.latest.ai_clst_id) {
    //         return true
    //       } else {
    //         return { tb_avg: '--', ph_avg: '--', cu_avg: '--', te_avg: '--' }
    //       }
    //     })
    //   } else {
    //     return { tb_avg: '--', ph_avg: '--', cu_avg: '--', te_avg: '--' }
    //   }
    // },
    // 막여과 유입수 탁도 트랜드 차트 옵션
    ChartTrend: function () {
      let chart = this.chartDataTrend
      chart.series = []
      if (this.$store.state.sedimentation.tbTrend.series1 && Object.keys(this.$store.state.sedimentation.tbTrend.series1).length > 0) {
        let dataSeries1 = []
        for (let key in this.$store.state.sedimentation.tbTrend.series1) {
          dataSeries1.push([new Date(key).getTime(), this.$store.state.sedimentation.tbTrend.series1[key]])
        }
        chart.series.push({
          name: '막여과 유입수 탁도',
          data: dataSeries1,
          color: 'rgb(110, 157, 225)',
          showInLegend: false
        })
      }
      chart.tooltip = {
        useHTML: true,
        style: {
            pointerEvents: 'none'
        },
        formatter: function() {
          return `${Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x)}<br/>
                  <span style="font-weight: bold; text-align: center;">
                  ${Highcharts.numberFormat(this.y, 2)} NTU</span>`;
        }
      }
      chart.plotOptions = {
        series: {
          tooltip: {
            pointFormatter: function() {
              return false;
            }
          }
        }
      }

      // if (this.$store.state.sedimentation.tbTrend.series2 !== null && Object.keys(this.$store.state.sedimentation.tbTrend.series2).length > 0) {
      //   let dataSeries2 = []
      //   for (let key in this.$store.state.sedimentation.tbTrend.series2) {
      //     dataSeries2.push([new Date(key).getTime(), this.$store.state.sedimentation.tbTrend.series2[key]])
      //   }
      //   chart.series.push({
      //     name: '2계열',
      //     data: dataSeries2,
      //     color: 'rgb(146, 66, 235)'
      //   })
      // }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_막여과 유입수 탁도'
      return chart
    }
  },
  methods: {
    updateInput: function (event, key) {
      this.$store.state.coagulants.latestModify[key] = event.target.value
    },
    updateControl: function() {      
      if (this.$store.state.coagulants.isModifyModeUser) {
        if (this.$store.state.coagulants.latestModify.c_user_correct === '') {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
        } else {
          let obj = {}
          obj.c_user_correct = parseFloat(this.$store.state.coagulants.latestModify.c_user_correct).toFixed(2)
          this.$store.dispatch(PUT_COAGULANT_CONTROL_USER_CORRECT, obj)
          this.$store.state.coagulants.isModifyModeUser = !this.$store.state.coagulants.isModifyModeUser
          this.$store.state.coagulants.latest.c_user_correct = parseFloat(this.$store.state.coagulants.latestModify.c_user_correct).toFixed(2)
        }
      } else {
        this.$store.state.coagulants.isModifyModeUser = !this.$store.state.coagulants.isModifyModeUser
      }
    },
    cancelControl: function() {
      this.$store.state.coagulants.latestModify = Object.assign({}, this.$store.state.coagulants.latest)
      this.$store.state.coagulants.isModifyModeUser = !this.$store.state.coagulants.isModifyModeUser
    },
  },
  /**
   * 마운트시 실행되는 함수
   * fullscreenchanged 이벤트 등록
   */
  mounted: function () {
    // this.carousel = this.$el.querySelector('#cube')
    // this.rotateCube()
    // window.addEventListener('fullscreenchange', this.fullscreenchanged)
  },
  /**
   * 마운트 해제시 실행되는 함수
   * fullscreenchanged 이벤트 해제
   */
  // beforeDestroy () { window.removeEventListener('fullscreenchange', this.fullscreenchanged) },
  watch: {
    /**
     * $store.state.coagulants.latest.ai_clst_id 값 모니터링
     * 해당 값 변경시 회전 애니메이션
     */
    // '$store.state.coagulants.latest.ai_clst_id': function (newVal, oldVal) {
    //   if (oldVal !== newVal) {
    //     this.rotateCube()
    //   }
    // }
  }
}
</script>
<style lang="scss" scoped>
.real-one {
  top: 0;
  right: 10px;
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
    &__title {
      padding-left: 12px;
      color: #23a7c7;
      text-shadow: 0 0 9px #a0d0ff;
    }
  }
  .real-value {
    text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
    font-family: "LAB디지털" !important;
    font-size: 23px;
    text-align: center;
    color: #23a7c7;
    margin-left: 3px;
  }
} 
.top-info{
  text-shadow: 0 0 9px #a0d0ff;
  font-size: 14px;
  color: #c7e6ff;
  position: absolute;
  right: 80px;
  top: 15px;
}
.scroll_container {
  width: 280px;
  height: 41px;
  height: 100%;
  margin-left: 5px;
}
.select-cube{
  position: absolute;
  width: 296px;
  height: 50px;
  background-image: url('../../assets/drugInjection/cg_roling.png');
  background-size: 100% 100%;
  top: 169px;
  left: 8px;
}
#cube {
  display: block;
  position: relative;
  margin: 60px auto;
  height: 41px;  
  width: 280px;
  -webkit-transform-style: preserve-3d;
  -webkit-transform: rotateX(0) rotateY(0) rotateZ(0);
  transform-style: preserve-3d;
  transform: rotateX(0) rotateY(0) rotateZ(0);
  transition: transform 2s;
}
@-webkit-keyframes infiniteRotation {
  0% { -webkit-transform: rotateX(0) ;}
  100% { -webkit-transform: rotateX(360deg) ;}
}
@keyframes infiniteRotation {
  0% { transform: rotateX(0) ;}
  100% { transform: rotateX(360deg) ;}
}
.carousel {
  width: 100%;
  height: 100%;
  position: absolute;
  transform: translateZ(-75px);
  transform-style: preserve-3d;
  transition: transform 1s;
}
.carousel__cell {
  position: absolute;
  width: 100%;
  height: 41px;
  text-shadow: 0 0 9px #5cafff;
  font-size: 18px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2.5;
  letter-spacing: normal;
  color: #fff;
  text-align: center;
  transition: transform 1s, opacity 1s;
  backface-visibility: hidden;
}
.carousel__cell::before{
  content: "";
  opacity: 0.5;
  position: absolute;
  background-image: url('../../assets/drugInjection/swiper_roling_background.png');
  top: 0px;
  left: 0px;
  right: 0px;
  bottom: 0px;
}
.select-box{
  position: absolute;
  display: flex;
  bottom: 353px;
  right: 40px;
  select{
    width: 94.5px;
    height: 34px;
    border: solid 1px rgba(157, 191, 255, 0.3);
    opacity: 0.7;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.07;
    letter-spacing: normal;
    text-align: left;
    padding-left: 15px;
    color: #fff;
    margin-left: 20px;
    background-image: url('../../assets/drugInjection/box_under_arrow.png');
    background-position-y: center;
    background-position-x: 70px;
  }
  &__text{
    text-shadow: 0 0 9px #5cafff;
    font-size: 18px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2;
    letter-spacing: normal;
    text-align: left;
    color: #fff;
  }
}
.arrow-animate-one{
  position: absolute;
  top: 78px;
  left: 326px;
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
.arrow-animate-two {
  position: absolute;
  top: 78px;
  left: 740px;
  background-image: url('../../assets/splashdown/arrow_img.png');
  width: 85px;
  height: 356px;
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
  48% {opacity:0; transform: translateX(-5px);}
  60% {opacity:0.5; transform: translateX(-2px);}
  72% {opacity:1; transform: translateX(0px);}
  84% {opacity:0.5; transform: translateX(2px);}
  100% {opacity:0; transform: translateX(5px);}
}
.main {
  width: 1202px;
  height: 794px;
  padding-left: 25px;
}
.bottom-contents {
  display: flex;
  width: 100%;
  height: 373px;
  padding-top: 20px;
  .paddingleft{
    padding-left: 10px;
    position: relative;
  }
  .contents {
    width: 50%;
    height: 100%;
    .contents-gragh {
      width: 100%;
      height: 306px;
      background-size: contain;
      background-position-y: bottom;
    }
    &__title {
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
    &__real-box {
      display: flex;
      align-items: center;
      position: absolute;
      top: 0;
      right: 10px;
    }
    &__real-text {
      text-shadow: 0 0 9px #a0d0ff;
      font-family: KHNPHUotfR;
      font-size: 16px;
      text-align: left;
      color: #23a7c7;
    }
    &__real-value {
      text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
      font-family: "LAB디지털" !important;
      font-size: 23px;
      text-align: center;
      color: #23a7c7;
      margin-left: 7px;
    }
  }
}
.top-contents{
  display: flex;
  justify-content: space-between;
  width: 100%;
  height: 420px;
  position: relative;
  .three-contents{
    position: relative;
    width: 316px;
    height: 100%;
    .three-small-contents{
      width: 424px;
      height: 356px;
      margin-top: 10px;
      background-image: url('../../assets/disinfection/three_background.png');
      .three-small-contents-value{
        width: 316px;
        height: 355px;
        padding-top: 80px;
        .margintop{
          margin-top: 40px;
        }
        .three-contents-value-underbar{
          width: 316px;
          height: 36px;
          background-image: url('../../assets/disinfection/contents_value_underbar.png');
          background-position-x: center;
        }
        .three-value{
          display: flex;
          justify-content:center;
          width: 100%;
          height: 42px;
          margin-left: 25px;
          &__line{
            width: 100px;
            text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
            font-family: "LAB디지털" !important;
            font-size: 25px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2;
            letter-spacing: normal;
            text-align: center;
            color: #ccf1ff;
            margin-right: 44px;
          }
          &__num{
            // margin-left: auto;
            text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
            font-family: "LAB디지털" !important;
            font-size: 35px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2;
            letter-spacing: normal;
            text-align: center;
            color: #ccf1ff;
          }
          &__unit{
            width: 78px;
            font-size: 16px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height:5;
            letter-spacing: normal;
            text-align: left;
            color: #417db9;
            margin-left: 10px;
          }
        }
        &__title{
          height: 34px;
          background-image: url('../../assets/disinfection/small_contents_title.png');
          text-shadow: 0 0 9px #5cafff;
          font-size: 16px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 2;
          letter-spacing: normal;
          text-align: left;
          padding-left: 56px;
          color: #c3eaff;
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
      justify-content: center;
      align-items: center;
      margin-right: auto;
      width: 100%;
      height: 53px;
      padding-left: 40px;
      background-image: url('../../assets/disinfection/contents_right_title.png');
      text-shadow: 0 0 9px #5cafff;
      font-size: 18px;
      line-height: 2.8;
      text-align: left;
      color: #fff;
    }  
  }
  .two-contents{
    position: relative;
    width: 424px;
    height: 100%;
    .small-contents{
      width: 424px;
      height: 356px;
      margin-top: 10px;
      background-image: url('../../assets/disinfection/small_contents_background.png');
      .small-contents-value{
        width: 316px;
        height: 355px;
        padding-top: 80px;
        .roling-box{
          height: 57px;
          display: flex;
          justify-content: center;
        }
        .chart{
          width: 100%;
          height: 135px;
        }
        .chart-value{
          display: flex;
          width: 100%;
          padding-left: 10px;
          .value-box{
            display: flex;
            flex-flow: column;
            align-items: center;
            margin: 0 4px;
            &__text{
              text-shadow: 0 0 9px #5cafff;
              font-size: 16px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              line-height: 1.56;
              letter-spacing: normal;
              text-align: left;
              color: #fff;
            }
            &__text1{
              text-shadow: 0 0 9px #5cafff;
              font-size: 12px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              line-height: 1;
              letter-spacing: normal;
              text-align: center;
              color: #fff;
            }
            &__value{
              margin-top: 8px;
              width: 64.6px;
              height: 41.6px;
              border: solid 1px rgba(157, 191, 255, 0.3);
              text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
              font-family: "LAB디지털" !important;
              font-size: 20px;
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
        .margintop{
          margin-top: 40px;
        }
        .contents-value-underbar{
          width: 316px;
          height: 36px;
          background-image: url('../../assets/disinfection/contents_value_underbar.png');
          background-position-x: center;
        }
        .value{
          display: flex;
          justify-content: center;
          width: 100%;
          height: 42px;
          &__num{
            text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
            font-family: "LAB디지털" !important;
            font-size: 35px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2;
            letter-spacing: normal;
            text-align: center;
            color: #ccf1ff;
          }
          &__unit{
            font-size: 16px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 5;
            letter-spacing: normal;
            text-align: left;
            color: #417db9;
            margin-left: 10px;
          }
        }
        &__title{
          height: 34px;
          background-image: url('../../assets/disinfection/small_contents_title.png');
          text-shadow: 0 0 9px #5cafff;
          font-size: 16px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 2;
          letter-spacing: normal;
          text-align: left;
          padding-left: 56px;
          color: #c3eaff;
        }
        .user-value-wrap {
          display: flex;
          flex-direction: column;
          align-items: center;
          margin-top: 20px;
          position: relative;
          .user-value {
            width: 159px;
            height: 45px;
            text-align: center;
            line-height: 3.4;
            font-size: 14px;
            color: #fff;
            background-image: url('../../assets/gs_images/user-value.png');
            margin-bottom: 10px;
          }
          .user-value-num 
            > span {
              text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
              font-family: "LAB디지털" !important;
              font-size: 25px;
              color: #ccf1ff;
            }
            .user-num {
              width: 140px;
              height: 40px;
              color: #c3eaff;
              text-align: center;
              outline: none;
              border: solid 1px rgba(157, 191, 255, 0.3);
            }
          }
          .contents-value__icon {
            position: absolute;
            top: 0;
            right: 45px;
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
      .small-contents-value span {
        padding-right: 10px;
        font-size: 18px;
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
    // 현재주입률 박스
    .real-box {
      display: flex;
      align-items: center;
      position: absolute;
      top: 65px;
      right: 120px;
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
  }
  .one-contents{
    width: 440px;
    height: 100%;
    .big-contents{
      margin-top: 10px;
      width: 458px;
      height: 356px;
      padding-top: 16px;
      background-image: url('../../assets/splashdown/result_background.png');
      .paddingtop{
        padding-top: 13px ;
      }
      .contents-value{
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        width: 340px;
        height: 307px;
        background-image: url('../../assets/drugInjection/value_factor.png');
        padding: 0 20px;
        .value-container{
          display: flex;
          width: 100%;
          &__title{
            width: 50px;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2.61;
            letter-spacing: normal;
            text-align: right;
            color: #c3eaff;
            margin: 15px 0 5px 5px;
          }
          &__one{
            width: 150px;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2.61;
            letter-spacing: normal;
            text-align: left;
            color: #9fc4ff;
          }
          &__two{
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2.61;
            letter-spacing: normal;
            text-align: right;
            color: #fff;
            margin-left: auto;
          }
          &__three{
            width: 33px;
            font-size: 14px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 3.36;
            letter-spacing: normal;
            text-align: left;
            color: #417db9;
            margin-left: 10px;
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