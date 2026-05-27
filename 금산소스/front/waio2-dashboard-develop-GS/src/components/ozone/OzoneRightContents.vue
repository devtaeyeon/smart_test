<template>
  <div class="main">
    <div class="top-contents">
      <div class="arrow-animate-one"></div>
      <div class="arrow-animate-two"></div>
      <div class="one-contents">
        <div class="one-contents__title">주요인자</div>
        <div class="big-contents">
          <div class="contents-value">
            <div class="value-container">
              <div class="value-container__one">· 용존 오존 농도(ppm)</div>
              <div class="value-container__two">{{ this.$store.state.ozone.latest.io_de | numFormat('0.000') }}</div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 1계열 전단 용존 오존(ppm)</div>
              <div class="value-container__two">{{ this.$store.state.ozone.latest.io_pre1_de | numFormat('0.000') }}</div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 1계열 후단 용존 오존(ppm)</div>
              <div class="value-container__two">{{ this.$store.state.ozone.latest.io_post1_de | numFormat('0.000') }}</div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 2계열 중간 용존 오존(ppm)</div>
              <div class="value-container__two">{{ this.$store.state.ozone.latest.io_peri2_de | numFormat('0.000') }}</div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 2계열 후단 용존 오존(ppm)</div>
              <div class="value-container__two">{{ this.$store.state.ozone.latest.io_ser_de | numFormat('0.000') }}</div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 현재 주입률(ppm)</div>
              <div class="value-container__two">{{ this.$store.state.ozone.latest.io_injection | numFormat('0.00') }}</div>
            </div>
            <div class="value-container">
              <div class="value-container__one">· 오존 총 생산량(g/h)</div>
              <div class="value-container__two">{{ this.$store.state.ozone.latest.io_og_qu | numFormat('0') }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="two-contents">
        <div class="two-contents__title">구간별 <span class="dynalight">k</span> 계산</div>
        <div class="small-contents">
          <div class="small-contents__title-box">
            <div>샘플링 위치</div>
            <div>1</div>
            <div>2</div>
            <div>3</div>
          </div>
          <div>
            <div>· HRT(min)</div>
            <div>{{ this.$store.state.ozone.latest.io_hrt1 | numFormat('0.00')}}</div>
            <div>{{ this.$store.state.ozone.latest.io_hrt2 | numFormat('0.00')}}</div>
            <div>{{ this.$store.state.ozone.latest.io_hrt3 | numFormat('0.00')}}</div>
          </div>
          <div>
            <div>· 오존 측정값(ppm)</div>
            <div>{{ this.$store.state.ozone.latest.ai_io_de1 | numFormat('0.00')}}</div>
            <div>{{ this.$store.state.ozone.latest.ai_io_de2 | numFormat('0.00')}}</div>
            <div>{{ this.$store.state.ozone.latest.ai_io_de3 | numFormat('0.00')}}</div>
          </div>
          <div>
            <div>· ΔHRT(min)</div>
            <div>{{ this.$store.state.ozone.latest.io_hrt_q1 | numFormat('0.00')}}</div>
            <div>{{ this.$store.state.ozone.latest.io_hrt_q2 | numFormat('0.00')}}</div>
            <div>{{ this.$store.state.ozone.latest.io_hrt_q3 | numFormat('0.00')}}</div>
          </div>
          <div>
            <div>· Ln(C<sub><span class="dynalight">i</span>+1</sub>/C<sub><span class="dynalight">i</span></sub>)</div>
            <div></div>
            <div>{{ this.$store.state.ozone.latest.ai_io_ln2 | numFormat('0.00')}}</div>
            <div>{{ this.$store.state.ozone.latest.ai_io_ln3 | numFormat('0.00')}}</div>
          </div>
          <div>
            <div>· <span class="dynalight">k</span>(min<sup>-1</sup>)</div>
            <div></div>
            <div>{{ this.$store.state.ozone.latest.ai_io_k2 | numFormat('0.00')}}</div>
            <div>{{ this.$store.state.ozone.latest.ai_io_k3 | numFormat('0.00')}}</div>
          </div>
          <div class="small-contents__title-box">
            <div style="width: 100%; text-align:center;">1-2구간의 <span class="dynalight">k</span><sub>2</sub> 기반 C<sub>0</sub>및 ID 계산</div>
          </div>
          <div style="display: flex; align-items:center; justify-content:center; width:388px; height: 61px; margin: 10px 0px 10px 0px;">
            <div style="display: flex; align-items:center; justify-content:center; width: 61px; height: 61px;">C<sub>0</sub></div>
            <div class="small-contents__circle"><div style="font-size:16px;">{{ this.$store.state.ozone.latest.ai_io_c0 | numFormat('0.00')}}</div><div style="font-size:14px;">ppm</div></div>
            <div style="display: flex; align-items:center; justify-content:center; width: 61px; height: 61px;">ID</div>
            <div class="small-contents__circle"><div style="font-size:16px;">{{ this.$store.state.ozone.latest.ai_io_id | numFormat('0.00')}}</div><div style="font-size:14px;">ppm</div></div>
          </div>
        </div>
      </div>
      <div class="three-contents">
        <div class="three-contents__title">
          예측결과
          <div class="three-contents__timerbox-outter">
            <div class="timerbox">
              {{ this.$store.state.ozone.latest.upd_ti | moment('YYYY-MM-DD HH:mm:ss') }}
            </div>
          </div>
        </div>
        <div class="three-small-contents">
          <div class="three-small-contents-table">
            <div class="three-small-contents-table__line1">
              <div>운전상태</div>
              <div>결정주입률</div>
            </div>
            <div class="three-small-contents-table__line2" :class="[ this.$store.state.ozone.latest.ai_io_state === 0 ? 'three-small-contents-table__line2--puple' : 'three-small-contents-table__line2--blue']">
              <div>정상</div>
              <div>{{ this.$store.state.ozone.latest.ai_io_normal | numFormat('0.0')}}</div>
            </div>
            <div class="three-small-contents-table__line2" :class="[ this.$store.state.ozone.latest.ai_io_state === 1 ? 'three-small-contents-table__line2--puple' : 'three-small-contents-table__line2--blue']">
              <div>비정상A</div>
              <div>{{ this.$store.state.ozone.latest.ai_io_abnormal1 | numFormat('0.0')}}</div>
            </div>
            <div class="three-small-contents-table__line2" :class="[ this.$store.state.ozone.latest.ai_io_state === 2 ? 'three-small-contents-table__line2--puple' : 'three-small-contents-table__line2--blue']">
              <div>비정상B</div>
              <div>{{ this.$store.state.ozone.latest.ai_io_abnormal2 | numFormat('0.0')}}</div>
            </div>
          </div>
          <div class="three-small-contents__arrow"></div>
          <div class="three-small-contents-title">주입률 예측</div>
          <div class="three-small-contents-value">
            <div class="three-value">
              <div class="three-value__num">{{ this.$store.state.ozone.latest.ai_io_inr | numFormat('0.0') }}</div>
              <div class="three-value__unit">ppm</div>
            </div>
            <div class="three-contents-value-underbar"></div>
          </div>
        </div>
      </div>
    </div>
    <div class="bottom-contents">
      <div class="bottom-chart-box">
        <div class="bottom-chart-box__title">예측 결과(주입률) 트렌드</div>
        <div class="bottom-chart">
          <highcharts :options="ChartInjection" style="height:100%;"/>
        </div>
      </div>
      <div class="bottom-chart-box">
        <div class="select-box">
          <div class="select-box__selected" :class="[selectedLine === 1 ? 'on' : '']" @click="selectedLine = 1"># 1</div>
          <div class="select-box__selected" :class="[selectedLine === 2 ? 'on' : '']" @click="selectedLine = 2"># 2</div>
        </div>
        <div class="bottom-chart-box__title">계열별 용존 오존 농도 트렌드</div>
        <div class="bottom-chart">
          <highcharts :options="ChartOzone" style="height:100%;"/>
        </div>
      </div>
    </div>
  </div> 
</template>
<script>
import * as XLSX from 'xlsx'

export default {
  name: 'OzoneRightContents',
  data: () => ({
    selectedLine: 1,
    series1: [],
    series2: [],
    chartDataInjection: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        zoomType: 'x'
      },
      title: {
        useHTML: true,
        text: '예측 결과(주입률) 트렌드',
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
          format: '{value:%m-%d %H:%M}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        },
        tickInterval: 1000 * 60 * 60 * 6 
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
    chartDataOzone: {
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
        valueDecimals: 3,
        xDateFormat: '%Y-%m-%d %H:%M:%S',
        valueSuffix: 'ppm'
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
        tickInterval: 1000 * 60 * 60 * 6 
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
        }
      },
      series: null,
      exporting: {
        buttons: {
          contextButton: {
            menuItems: ['viewFullscreen', 'downloadJPEG'],
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
    ChartDataOzone: function () {
      let _data = []
      if (this.$store.state.ozone.historyDe !== null && this.$store.state.ozone.historyDe['series' + this.selectedLine] !== null && 
        this.$store.state.ozone.historyDe['series' + this.selectedLine].pre !== null && Object.keys(this.$store.state.ozone.historyDe['series' + this.selectedLine].pre).length > 0 &&
        this.$store.state.ozone.historyDe['series' + this.selectedLine].peri !== null && Object.keys(this.$store.state.ozone.historyDe['series' + this.selectedLine].peri).length > 0 &&
        this.$store.state.ozone.historyDe['series' + this.selectedLine].post !== null && Object.keys(this.$store.state.ozone.historyDe['series' + this.selectedLine].post).length > 0) {
        let dataSeriesPre = []
        let dataSeriesPeri = []
        let dataSeriesPost = []
        for (let key in this.$store.state.ozone.historyDe['series' + this.selectedLine].pre) {
          dataSeriesPre.push([new Date(key).getTime(), this.$store.state.ozone.historyDe['series' + this.selectedLine].pre[key]])
        }
        _data.push({
          name: '전단',
          data: dataSeriesPre,
          color: '#b4dffb'
        })
        for (let key in this.$store.state.ozone.historyDe['series' + this.selectedLine].peri) {
          dataSeriesPeri.push([new Date(key).getTime(), this.$store.state.ozone.historyDe['series' + this.selectedLine].peri[key]])
        }
        _data.push({
          name: '중간',
          data: dataSeriesPeri,
          color: '#8098ff'
        })
        for (let key in this.$store.state.ozone.historyDe['series' + this.selectedLine].post) {
          dataSeriesPost.push([new Date(key).getTime(), this.$store.state.ozone.historyDe['series' + this.selectedLine].post[key]])
        }
        _data.push({
          name: '후단',
          data: dataSeriesPost,
          color: '#a345ff'
        })
      }

      return _data
    },

    ChartInjection: function () {
      let chart = this.chartDataInjection
      if (this.$store.state.ozone.historyInjection !== null && Object.keys(this.$store.state.ozone.historyInjection).length > 0) {
        let data = []
        for (let key in this.$store.state.ozone.historyInjection) {
          data.push([new Date(key).getTime(), this.$store.state.ozone.historyInjection[key]])
        }
        chart.series = [{
          name: '예측 결과(주입률)',
          data: data,
          color: 'rgb(124, 147, 247)'
        }]
        chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_예측 결과(주입률) 트렌드'
        return chart
      }
      return chart
    },

    ChartOzone: function () {
      let chart = this.chartDataOzone
      chart.series = this.ChartDataOzone

      const exportName = chart.exporting.buttons.contextButton.menuItems[2]
      if (exportName == undefined) {
        chart.exporting.buttons.contextButton.menuItems.push({
        text: 'Download Excel',
        onclick: this.downloadExcel
        })
      }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_계열별 용존 오존 농도 트렌드'

      return chart
    }
  },

  methods: {
    downloadExcel: function() {
      const ozoneDensity = this.$store.state.ozone.historyDe
      console.log('ozoneDensity', ozoneDensity)

      for (let i = 0; i < Object.keys(ozoneDensity).length; i++) {
        for (let density_obj in ozoneDensity['series' + (i + 1)]) {
          // console.log('den', density_obj)
          for(let time in ozoneDensity['series' + (i + 1)]['pre']) {
            const excelLogs = {
              DateTime: '', 
              전단: '',
              중간: '',
              후단: ''
            };

            excelLogs.DateTime = time
            excelLogs.전단 = ozoneDensity['series' + (i + 1)]['pre'][time]
            excelLogs.중간 = ozoneDensity['series' + (i + 1)]['peri'][time]
            excelLogs.후단 = ozoneDensity['series' + (i + 1)]['post'][time]

            const density_length = Object.keys(ozoneDensity['series' + (i + 1)][density_obj]).length
            
            if ( i === 0 ) {
              if( density_length != this.series1.length ) {
                this.series1.push(excelLogs);
              }
            } else if ( i === 1 ) {
              if( density_length != this.series2.length ) {
                this.series2.push(excelLogs);
              }
            } 
            // console.log('excelLogs', excelLogs)
          }
      }
    }
      // 엑셀 워크시트로 json 내보내기
      const wb = XLSX.utils.book_new();
      const dataWS1 = XLSX.utils.json_to_sheet(this.series1);
      const dataWS2 = XLSX.utils.json_to_sheet(this.series2);
      
      XLSX.utils.book_append_sheet(wb, dataWS1, '#1'); 
      XLSX.utils.book_append_sheet(wb, dataWS2, '#2');
      XLSX.writeFile(wb, `${this.$moment().format('YYYYMMDDHHmmss')}_계열별 용존 오존 농도 트렌드.xlsx`);
    }
  },

  // mounted: function () {
  //   window.addEventListener('fullscreenchange', this.fullscreenchanged)
  // },
  // beforeDestroy () { window.removeEventListener('fullscreenchange', this.fullscreenchanged) },
}
</script>

<style lang="scss" scoped>
.dynalight {
  font-family: 'Dynalight', sans-serif !important;
}
.three-contents{
    .three-small-contents{
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      height: 356px;
      margin-top: 10px;
      background-image: url('../../assets/disinfection/three_background.png');
      background-size: 100% 100%;
      &__arrow {
        width: 219px;
        height: 47px;
        background-image: url('../../assets/ozone/three_small_contents_arrow.png');
        background-size: 100% 100%;
        margin-bottom: 20px;
      }
      .three-small-contents-table {
        display:flex;
        width: 90%;
        text-shadow: 0 0 9px #5cafff;
        font-size: 16px;
        color: #c3eaff;
        &__line1 {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          width: 86px;
          height: 127px;
          margin-right: 10px;
          > div {
            margin: 10px 0px;
          }
        }
        &__line2 {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          width: 60px;
          height: 127px;
          text-align: center;
          font-size: 13px;
          margin-right: 10px;
          
          &--blue {
            background-image: url('../../assets/ozone/line2_background_blue.png');
            background-position: center;
          background-size: contain;
          }
          &--puple {
            background-image: url('../../assets/ozone/line2_background_puple.png');
            background-position: center;
          background-size: contain;
          }
          > div {
            margin: 10px 0px;
          }
        }
      }
      .three-small-contents-title {
        text-shadow: 0 0 9px #5cafff;
        font-size: 21px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.1;
        letter-spacing: normal;
        text-align: center;
        color: #c3eaff;
      }
      .three-small-contents-value{
        width: 316px;
        padding-top: 20px;
        .margintop{
          margin-top: 40px;
        }
        .three-contents-value-underbar{
          width: 316px;
          height: 36px;
          background-image: url('../../assets/disinfection/contents_value_underbar.png');
          background-position-x: center;
          margin-bottom: 30px;
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
          width: 85px;
          height: 34px;
          background-image: url('../../assets/disinfection/small_contents_title.png');
          text-shadow: 0 0 9px #5cafff;
          font-size: 16px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 2.3;
          letter-spacing: normal;
          text-align: right;
          color: #c3eaff;
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
.arrow-animate-one{
  position: absolute;
  top: 252px;
  left: 1030px;
  background-image: url('../../assets/splashdown/arrow_img.png');
  width: 85px;
  height: 356px;
  animation-name: big-arrow-one;
  animation-duration: 5s;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
}
.arrow-animate-two{
  position: absolute;
  top: 250px;
  left: 1490px;
  background-image: url('../../assets/splashdown/arrow_img.png');
  width: 85px;
  height: 356px;
  animation-name: big-arrow-one;
  animation-duration: 5s;
  animation-delay: 1s;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  animation-fill-mode: none;
  z-index: 1;
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
.main{
  width: 1202px;
  height: 794px;
}
.bottom-contents{
  display: flex;
  width: 100%;
  height: 373px;
  padding-top: 20px;
  .bottom-chart-box{
    position: relative;
    width: 50%;
    height: 100%;
    .select-box{
      display: flex;
      position: absolute;
      top: 0px;
      right: 0px;
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
    .bottom-chart{
      width: 100%;
      height: 306px;
    }
    &__title{
      width: 260px;
      height: 47px;
      background-image: url('../../assets/mixingTank/bottom_title.png');
      text-shadow: 0 0 9px #5cafff;
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
.top-contents{
  display: flex;
  width: 100%;
  height: 420px;
  .three-contents{
    position: relative;
    left: -80px;
    width: 316px;
    height: 100%;
    .three-small-contents{
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: 310px;
      height: 356px;
      margin-top: 10px;
      background-image: url('../../assets/disinfection/three_background.png');
      .three-small-contents-title {
        position: relative;
        text-shadow: 0 0 9px #5cafff;
        font-size: 21px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.1;
        letter-spacing: normal;
        text-align: left;
        color: #c3eaff;
      }
      .three-small-contents-value{
        width: 316px;
        padding-top: 20px;
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
          width: 85px;
          height: 34px;
          background-image: url('../../assets/disinfection/small_contents_title.png');
          text-shadow: 0 0 9px #5cafff;
          font-size: 16px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 2.3;
          letter-spacing: normal;
          text-align: right;
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
      align-items: center;
      width: 310px;
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
  .two-contents{
    position: relative;
    left: -52px;
    width: 508px;
    height: 100%;
    text-shadow: 0 0 9px #5cafff;
    .small-contents{
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: 508px;
      height: 368px;
      background-image: url('../../assets/ozone/small_contents_background2.png');
      color: #c3eaff;
      font-size: 18px;
      &__circle {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 61px !important;
        height: 61px !important;
        background-image: url('../../assets/ozone/small-contents__circle.png');
      }
      &__title-box {
        display: flex;
        width: 388px;
        height: 40px;
        align-items: center;
        background-image: url('../../assets/ozone/small-contents__title-box.png');
        > div:nth-child(1) {
          width: 40%;
          text-align: left;
          padding-left: 10px;
        }
        > div:nth-child(2) {
          width: 20%;
          text-align: right;
          padding-right: 15px;
        }
        > div:nth-child(3) {
          width: 20%;
          text-align: right;
          padding-right: 15px;
        }
        > div:nth-child(4) {
          width: 20%;
          text-align: right;
          padding-right: 15px;
        }
      }
      > div:not(.small-contents__title-box) {
        display: flex;
        align-items: center;
        width: 388px;
        height: 36px;
        > div:nth-child(1) {
          width: 40%;
          text-align: left;
        }
        > div:nth-child(2) {
          width: 20%;
          text-align: right;
        }
        > div:nth-child(3) {
          width: 20%;
          text-align: right;
        }
        > div:nth-child(4) {
          width: 20%;
          text-align: right;
        }
      }
      .small-contents-title {
        position: relative;
        left: 60px;
        text-shadow: 0 0 9px #5cafff;
        font-size: 21px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.1;
        letter-spacing: normal;
        text-align: left;
        color: #c3eaff;
      }
      .small-contents-value{
        width: 316px;
        padding-top: 50px;
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
          width: 85px;
          height: 34px;
          background-image: url('../../assets/disinfection/small_contents_title.png');
          text-shadow: 0 0 9px #5cafff;
          font-size: 16px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 2.3;
          letter-spacing: normal;
          text-align: right;
          color: #c3eaff;
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
  .one-contents{
    width: 457px;
    height: 100%;
    .big-contents{
      margin-top: 10px;
      width: 457px;
      height: 356px;
      padding-top: 16px;
      background-image: url('../../assets/disinfection/big_contents_background.png');
      background-position-x: -60px;
      .contents-value{
        display: flex;
        flex-direction: column;
        justify-content: center;
        width: 327px;
        height: 307px;
        background-image: url('../../assets/disinfection/value_factor.png');
        background-size: 100% 100%;
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
            width: 222px;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2.3;
            letter-spacing: normal;
            text-align: left;
            color: #9fc4ff;
          }
          &__two{
            width: 50px;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2.3;
            letter-spacing: normal;
            text-align: right;
            color: #fff;
            margin-left: auto;
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