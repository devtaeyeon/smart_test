<template>
  <div class="chart-container">
    <!-- 사용자 설정 -->
    <div class="chart-contents chartwidth20" style="display: flex; flex-direction: column;">
      <div class="chart-title">
        <div class="chart-title__text">사용자 설정</div>
      </div>
      <div class="box-config">
        <div class="box-config--list">
          <div class="box-label">수집기 운행 기준 슬러지양</div>
          <div class="box-input" :class="{'box-input__active': this.$store.state.sedimentation.isModifyMode}">
            <div class="box-input--value">
              <input v-if="this.$store.state.sedimentation.isModifyMode" type="text" :value="this.$store.state.sedimentation.latestModify.e_sc_set_sludge_q" v-on:input="updateInput($event, 'e_sc_set_sludge_q')"/>
              <span v-else>{{ this.$store.state.sedimentation.latestModify.e_sc_set_sludge_q | numFormat('0.00') }}</span>
            </div>
            <div class="box-input--unit">ton/day</div>
          </div>
        </div>
        <div class="box-config--list">
          <div class="box-label">수집기 최대 대기 일</div>
          <div class="box-input" :class="{'box-input__active': this.$store.state.sedimentation.isModifyMode}">
            <div class="box-input--value"><input v-if="this.$store.state.sedimentation.isModifyMode" type="text" :value="this.$store.state.sedimentation.latestModify.e_sc_set_max_wait" v-on:input="updateInput($event, 'e_sc_set_max_wait')"/><span v-else>{{ this.$store.state.sedimentation.latestModify.e_sc_set_max_wait }}</span></div>
            <div class="box-input--unit">일</div>
          </div>
        </div>
        <div class="box-config--list">
          <div class="box-label">대차 운전주기 최소기준시간</div>
          <div class="box-input" :class="{'box-input__active': this.$store.state.sedimentation.isModifyMode}">
            <div class="box-input--value"><input v-if="this.$store.state.sedimentation.isModifyMode" type="text" :value="this.$store.state.sedimentation.latestModify.e_low_hour" v-on:input="updateInput($event, 'e_low_hour')"/><span v-else>{{ this.$store.state.sedimentation.latestModify.e_low_hour }}</span></div>
            <div class="box-input--unit">시간</div>
          </div>
        </div>
        <div class="box-config--list">
          <div class="box-label">수집기 운행 거리</div>
          <div class="box-input">
            <div class="box-input--value"><span>{{ this.$store.state.sedimentation.latestModify.e_set_lt }}</span></div>
            <div class="box-input--unit">m</div>
          </div>
        </div>
        <div class="box-config--list">
          <div class="box-label">수집기 운행 시간</div>
          <div class="box-input">
            <div class="box-input--value"><span>{{ this.$store.state.sedimentation.latestModify.e_sc_set_ti }}</span></div>
            <div class="box-input--unit">시간</div>
          </div>
        </div>
        <div v-if="$store.state.login.user.tkn !== null" class="box-config--end">
          <div v-if="this.$store.state.sedimentation.isModifyMode" class="box-config--reset" @click="cancelControl"></div>
          <div v-if="this.$store.state.sedimentation.isModifyMode" class="box-config--confirm" @click="updateControl"><div class="box-config--check"></div><span>확인</span></div>
          <div v-else class="box-config--confirm" @click="clickControl"><span>설정</span></div>
        </div>
      </div>
    </div>
    <!--총 슬러지 발생량 예측 -->
    <div class="chart-contents chartwidth40">
      <div class="chart-title">
        <div class="chart-title__text">총 슬러지 발생량 예측</div>
      </div>
      <div class="chart chart-width">
        <highcharts :options="ChartSludge" style="height:100%;"/>
        <highcharts style="height:100%;"/>
      </div>
    </div>
    <!-- 계면계 수위 -->
    <!-- <div class="chart-contents chartwidth40">
      <div class="chart-title">
        <div class="chart-title__text">계면계 수위</div>
      </div>
      <div class="chart chart-width">
        <highcharts :options="ChartMeter" style="height:100%;"/>
      </div>
    </div> -->
  </div>  
</template>
<script>
import Highcharts from 'highcharts'
import xrangeInit from 'highcharts/modules/xrange'
import { PUT_SEDIMENTATION_CONTROL_SC } from '@/store/modules/sedimentation'
xrangeInit(Highcharts)

Highcharts.setOptions({
  global: {
    useUTC: false
  },
  lang: {
    thousandsSep: ','
  }
})
export default {
  name:'SedimentationHighchart',
  data: () => ({
    // 총 슬러지 발생량 차트
    chartDataSludge: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        zoomType: 'x'
      },
      title: {
        useHTML: true,
        text: '총 슬러지 발생량 예측',
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
        xDateFormat: '%Y-%m-%d',
        valueSuffix: 'ton'
      },
      xAxis: {
        title: {
          text: ''
        },
        type: 'datetime',
        categories: [],
        labels: {
          format: '{value:%m-%d}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        },
        tickInterval: 1000 * 60 * 60 * 48 
      },
      yAxis: {
        title: {
          align: 'high',
          text: 'ton',
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
        csv: {
          dateFormat: '%Y-%m-%d'
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
    // 총 슬러지 발생량 예측 차트 옵션
    ChartSludge: function () {
      let chart = this.chartDataSludge
      if (this.$store.state.sedimentation.latest.ai_e_total_sludge !== null && Object.keys(this.$store.state.sedimentation.latest.ai_e_total_sludge).length > 0) {
        let now = new Date()
        now.setHours(0)
        now.setMinutes(0)
        now.setSeconds(0)
        chart.xAxis.plotLines = [{
          color: 'white', 
          dashStyle: 'ShortDash', 
          value: now.getTime(),
          width: 3, 
        }]
        let data = []
        for (let key in this.$store.state.sedimentation.latest.ai_e_total_sludge) {
          data.push([new Date(key).getTime() - this.$getMilliSecondFromHour(9), this.$store.state.sedimentation.latest.ai_e_total_sludge[key]])
        }
        chart.series = [{
          name: '총 슬러지 발생량',
          data: data,
          color: 'rgb(124, 147, 247)'
        }]
        chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_총 슬러지 발생량 예측'
        return chart
      }
      return chart
    },
    // 계면계 수위 차트 옵션
    ChartMeter: function () {
      let chart = this.chartDataMeter
      chart.series = []
      if (this.$store.state.sedimentation.interface.series1 !== null && Object.keys(this.$store.state.sedimentation.interface.series1).length > 0) {
        let dataSeries1 = []
        for (let key in this.$store.state.sedimentation.interface.series1) {
          dataSeries1.push([new Date(key).getTime(), this.$store.state.sedimentation.interface.series1[key]])
        }
        chart.series.push({
          name: '1계열',
          data: dataSeries1,
          color: 'rgb(110, 157, 225)'
        })
      }
      if (this.$store.state.sedimentation.interface.series2 !== null && Object.keys(this.$store.state.sedimentation.interface.series2).length > 0) {
        let dataSeries2 = []
        for (let key in this.$store.state.sedimentation.interface.series2) {
          dataSeries2.push([new Date(key).getTime(), this.$store.state.sedimentation.interface.series2[key]])
        }
        chart.series.push({
          name: '2계열',
          data: dataSeries2,
          color: 'rgb(146, 66, 235)'
        })
      }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_계면계 수위'
      return chart
    }
  },
  methods: {
    /**
     * 사용자 설정 input 값이 변경 되는 경우
     * 
     * @param event change 이벤트
     * @param key 변경되는 값
     */
    updateInput: function (event, key) {
      this.$store.state.sedimentation.latestModify[key] = event.target.value
    },
    /**
     * 사용자 설정 취소 버튼 선택시
     */
    cancelControl: function() {
      this.$store.state.sedimentation.latestModify = Object.assign({}, this.$store.state.sedimentation.latest)
      this.$store.state.sedimentation.isModifyMode = !this.$store.state.sedimentation.isModifyMode
    },
    /**
     * 숫자 소수점 2자리 validation 체크 함수
     * 
     * @param val 숫자
     * @return validation 여부
     */
    checkNumberAndTwoPointFormat: function(val) {
      const regex = /^\d{1,2}([\.](\d{1,2})?)?$/ //eslint-disable-line
      if (!regex.test(val)) {
        return false		// 0.00~99.99를 벗어나면
      }
      return true
    },
    /**
     * 숫자 양의 정수 체크 함수
     * 
     * @param val 숫자
     * @return validation 여부
     */
    checkNumber: function(val) {
      const regex = /^[0-9]{1,2}$/ //eslint-disable-line
      if (!regex.test(val)) {
        return false		// 양의 정수만
      }
      return true
    },
    /**
     * 사용자 설정 업데이트 버튼 선택시 
     * 유효성 검사 후 사용자 설정 업데이트 API 요청
     */
    updateControl: function() {      
      let sludge_min = 0
      let sludge_max = 10
      let day_min = 0
      let day_max = 10
      let hour_min = 0
      let hour_max = 48
      if (this.$store.state.sedimentation.isModifyMode) {
        if (this.$store.state.sedimentation.latestModify.e_sc_set_sludge_q === ''
          || this.$store.state.sedimentation.latestModify.e_sc_set_max_wait === '') {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
        } else if (parseFloat(this.$store.state.sedimentation.latestModify.e_sc_set_sludge_q) <= sludge_min || parseFloat(this.$store.state.sedimentation.latestModify.e_sc_set_sludge_q) >= sludge_max) {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '기준 슬러지양 설정 범위', text2: sludge_min + ' ~ ' + sludge_max })
        } else if (parseInt(this.$store.state.sedimentation.latestModify.e_sc_set_max_wait) <= day_min || parseInt(this.$store.state.sedimentation.latestModify.e_sc_set_max_wait) >= day_max) {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '최대 대기일 설정 범위', text2: day_min + ' ~ ' + day_max })
        } else if (parseInt(this.$store.state.sedimentation.latestModify.e_low_hour) < hour_min || parseInt(this.$store.state.sedimentation.latestModify.e_low_hour) > hour_max) {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '대차 운전주기 최소기준시간 범위', text2: hour_min + '~' + hour_max})
        }
          else {
          let obj = {}
          obj.e_sc_set_sludge_q = parseFloat(this.$store.state.sedimentation.latestModify.e_sc_set_sludge_q)
          obj.e_sc_set_max_wait = parseInt(this.$store.state.sedimentation.latestModify.e_sc_set_max_wait)
          obj.e_set_lt = parseInt(this.$store.state.sedimentation.latestModify.e_set_lt)
          obj.e_sc_set_ti = parseInt(this.$store.state.sedimentation.latestModify.e_sc_set_ti)
          obj.e_low_hour = parseInt(this.$store.state.sedimentation.latestModify.e_low_hour)
          this.$store.dispatch(PUT_SEDIMENTATION_CONTROL_SC, obj)
          this.$store.state.sedimentation.isModifyMode = !this.$store.state.sedimentation.isModifyMode
        }
      } else {
        this.$store.state.sedimentation.isModifyMode = !this.$store.state.sedimentation.isModifyMode
      }
    },
    clickControl: function() {
      this.$store.state.sedimentation.isModifyMode = !this.$store.state.sedimentation.isModifyMode
    }
    // fullscreenchange 이벤트
    // fullscreenchanged: function () {
    //   console.log('fullscreenchange')
    //   if(document.fullscreenElement) {
    //     this.chartDataSludge.title.style.color = 'white'
    //     this.chartDataMeter.title.text = '계면계수위'
    //   } else {
    //     this.chartDataSludge.title.style.color = 'transparent'
    //     this.chartDataMeter.title.text = null
    //   }
    // }
  },
  /**
   * 마운트시 
   * fullscreenchange 이벤트 등록
   */
  // mounted: function () {
  //   window.addEventListener('fullscreenchange', this.fullscreenchanged)
  // },
  /**
   * 마운트 해제시 
   * fullscreenchange 해제
   */
  // beforeDestroy () { window.removeEventListener('fullscreenchange', this.fullscreenchanged) },
  watch: {
    // 수집기 운행 기준 슬러지양 값 변화 감지
    // '$store.state.sedimentation.latestModify.e_sc_set_sludge_q': function(newVal, oldVal) {
    //   if (newVal === '') {
    //     this.$store.state.sedimentation.latestModify['e_sc_set_sludge_q'] = newVal
    //   } else {
    //     if (this.checkNumberAndTwoPointFormat(newVal)) {
    //       this.$store.state.sedimentation.latestModify['e_sc_set_sludge_q'] = newVal
    //     } else {
    //       this.$store.state.sedimentation.latestModify['e_sc_set_sludge_q'] = oldVal
    //     }
    //   }
    // },
    // // 수집기 최대 대기 일 값 변화 감지
    // '$store.state.sedimentation.latestModify.e_sc_set_max_wait': function(newVal, oldVal) {
    //   if (newVal === '') {
    //     this.$store.state.sedimentation.latestModify['e_sc_set_max_wait'] = newVal
    //   } else {
    //     if (this.checkNumber(newVal)) {
    //       this.$store.state.sedimentation.latestModify['e_sc_set_max_wait'] = newVal
    //     } else {
    //       this.$store.state.sedimentation.latestModify['e_sc_set_max_wait'] = oldVal
    //     }
    //   }
    // },
  }
}
</script>
<style lang="scss" scoped>
.chartwidth50{
  width: 50% !important;
  .chart-width{
    width: 912px !important;
  }
}
.chartwidth40{
  width: 80% !important;
  .chart-width{
    width: 100% !important;
  }
}
.chartwidth20{
  width: 30% !important;
}
.selected{
  box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  background-color: rgba(139, 194, 240, 0.4) !important;
}
.marginleft10{
  margin-left: 14.5px;
}
.chart{
  width: 100%;
  height: 280px;
  // background-color: black;
  margin-left: 17px;
  text-align: center;
  color:#fff;
}
.chart-container{
  display: flex;
  width: 100%;
  margin: 25px 25px 0 25px;
}
.chart-contents{
  width: 25%;
  height: 100%;
  .chart-title{
    display: flex;
    margin-top: 10px;
    .chart-title-tab{
      display: flex;
      margin-top: 5px;
      margin-left: auto;
      &__box{
        width: 30.9px;
        height: 26.4px;
        border: solid 1px #b4dffa;
        background-color: rgba(139, 194, 240, 0.25);
        font-family: "KHNPHUotfR";
        font-size: 14px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.8;
        letter-spacing: normal;
        text-align: center;
        color: #fff;
      }
    }
    &__text{
      width: 196px;
      height: 47px;
      background-image: url('../../assets/splashdown/chart_title.png');
      text-shadow: 0 0 9px #5cafff;
      font-family: "KHNPHUotfR";
      font-size: 18px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.11;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
    }
  }
}
.box-config {
  display: flex;
  flex-direction: column;
  width: 420px;
  height: 278px;
  background-image: url('../../assets/splashdown/background_box_config.png');
  margin: 0 auto;
  &--list {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 6px 0px;
  }
  &--list:nth-child(1) {
    margin-top: 15px;
  }
  &--end {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin: 6px 0px;
  }
  &--reset {
    width: 34px;
    height: 34px;
    background-image: url('../../assets/splashdown/reset_icon.png');
    background-position: center;
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.25);
    cursor: pointer;
  }
  &--confirm {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 149px;
    height: 34px;
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.25);
    font-size: 14px;
    color: #fff;
    margin-left: 5px;
    margin-right: 10px;
    cursor: pointer;
  }
  &--check {
    width: 14px;
    height: 10px;
    background-image: url('../../assets/splashdown/check_icon.png');
    background-position: center;
    margin-right: 5px;
  }
}
.box-label {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 259px;
  height: 42px;
  background-image: url('../../assets/splashdown/background_box_lablel.png');
  font-size: 16px;
  text-shadow: 0 0 9px #5cafff;
  font-family: KHNPHUotfR;
  color: white;
}
.box-input {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 148px;
  height: 42px;
  &__active {
    border: solid 1px #9dbfff;
  }
  &--value {
    width: 78px;
    text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
    font-family: "LAB디지털" !important;
    font-size: 24px;
    color: #ccf1ff;
    text-align: left;
    margin-left: 10px;
    span {
      font-family: "LAB디지털" !important;
    }
    input {
      width: 78px;
      font-family: "LAB디지털" !important;
      font-size: 24px;
      color: #ccf1ff;
      text-align: left;
    }
  }
  &--unit {
    width: 70px;
    font-family: KHNPHUotfR;
    font-size: 14px;
    color: #417db9;
    text-align: right;
    padding-right: 10px;
  }
}
</style>