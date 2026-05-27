<template>
  <div style="height:100%;">
    <highcharts :options="ChartSchedule" ref='escChart' style="height:100%;"/>
    <!-- <button @click="rangeTest">Range Test</button> -->
  </div>
</template>

<script>
import moment from 'moment'
import * as XLSX from 'xlsx'

export default {
  name:'ESCScheduleChart',
  data() {
    return {
      isInit: true,
      excel: [],
    // 수집기 운영 스케줄 차트
      chartDataSchedule: {
        chart: {
          type: 'xrange',
          backgroundColor: false,
        },
        title: {
          text: null,
          style: {
            color: 'white'
          }
        },
        legend: {
          enabled: false,
        },
        credits: {
          enabled: false
        },
        tooltip: {
          headerFormat: '<span style="font-size:16px">{point.x:%Y-%m-%d %H:%M:%S} ~ {point.x2:%Y-%m-%d %H:%M:%S}</span>',
          pointFormat: ''
        },
        xAxis: {
          title: {
            text: ''
          },
          type: 'datetime',
          labels: {
            format: '{value:%m-%d}',
            style:{
              fontFamily:'NanumSquare',
              fontSize: 13,
              color:"rgb(255,255,255,0.8)"
            }
          },
          scrollbar: {
            enabled: true,
            height: 7,
            barBorderColor: 'rgba(128, 182, 255, 0.6)',
            barBackgroundColor: 'rgba(0, 0, 0, 0)',
            barBorderWidth: 7,
            buttonBorderWidth: 6,
            buttonBorderColor: 'rgba(128, 182, 255, 0.6)',
            buttonBackgroundColor: 'rgba(0, 0, 0, 0)',
            buttonArrowColor: 'rgba(255, 255, 255, 1)',
            trackBorderWidth: 0,
            trackBackgroundColor: 'rgba(0, 0, 0, 0)',
            rifleColor: 'rgba(0, 0, 0, 0)'
          },
          tickInterval: 1000 * 60 * 60 * 24,
        },
        yAxis: {
          title: {
            text: false
          },
          categories: ['4지','3지','2지','1지'],
          gridLineColor: false,
          labels: {
            style:{
              fontFamily:'NanumSquare',
              fontSize: 13,
              color:"rgb(255,255,255,0.8)"
            }
          },
          max: 3
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
        },
        plotOptions: {
          series: {
            dataLabels: {
              enabled: true,
              align: 'left',
              y: -1,
              formatter: function() {
                if (this.color !== '#ff0000') {
                  return '<strong>' + moment(this.x).format('MM-DD HH:mm') + '</strong>'
                }
              }
            }
          }
        }
      }
    } 
  },

  computed: {
    // 수집이 운영 스케줄 차트 옵션
    ChartSchedule: function () {
      let chart = this.chartDataSchedule
      let now = new Date()

      chart.xAxis.plotLines = [{
        color: 'white', 
        dashStyle: 'ShortDash',
        value: now.getTime(), 
        width: 3, 
      }]
      let minMax = this.$getMinMaxTimestampAIELocationSchedule(this.$store.state.sedimentation.latest)
      //1. 가장 작은 시간
      let xAxisMinValue = minMax[0] // - this.$getMilliSecondFromHour(12)
      //2. 가장 큰 시간
      let xAxisMaxValue = minMax[1]  // + this.$getMilliSecondFromHour(12)
      chart.xAxis.min = xAxisMinValue
      chart.xAxis.max = xAxisMaxValue
      let colorXRange = '#b4dffb'
      let colorInbal = '#ff0000'
      let inbalTimeLength = 1000 * 60 * 10
      let data = []
      for (let i = 1; i < 5; i ++) {
        // 현재 대차 스케줄
        if (this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].start !== null 
        && this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].stop !== null) {    
          data.push({
            x: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].start).getTime(),
            x2: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].stop).getTime(),
            y: 4 - i,
            color: colorXRange
          })
        }
        // 다음 대차 스케줄
        if (this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].next_start !== null 
        && this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].next_end !== null) {    
          data.push({
            x: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].next_start).getTime(),
            x2: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].next_end).getTime(),
            y: 4 - i,
            color: colorXRange
          })
        }
        // inbal 스케줄
        if (this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].inbal !== null 
        && this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].inbal !== '') {
          data.push({
            x: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].inbal).getTime(),
            x2: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].inbal).getTime() + inbalTimeLength,
            y: 4 - i,
            color: colorInbal
          })
        }
      }
      chart.series = [{
        name: '수집기 운영 스케줄',
        pointWidth: 15,
        borderWidth: 0,
        borderRadius: 0,
        dataLabels: {
          enabled: true
        },
        data: data
      }] 
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_수집기 운영 스케줄'

      const exportName = chart.exporting.buttons.contextButton.menuItems[2]
      if (exportName == undefined) {
        chart.exporting.buttons.contextButton.menuItems.push({
        text: 'Download Excel',
        onclick: this.downloadXLSX
        })
      }

      return chart
    },
  },

  methods: {
    /**
     * 초기화 과정에서 차트 데이터가 로딩되어 updated 함수가 실행되면
     * x축 스크롤바 범위를 지정하기 위한 함수
     * isInit 변수 값에 따라 setExtremes 함수 호출
     */
    initExtremesRange: function () {
      let minMax = this.$getMinMaxTimestampAIELocationSchedule(this.$store.state.sedimentation.latest)
      // let milliOfHour = 1000 * 60 * 60
      let offset = parseInt((minMax[1]- minMax[0]) / 2)
      if (this.isInit) {
        this.$refs.escChart.chart.xAxis[0].setExtremes(minMax[0], minMax[0] + offset)
        this.isInit = false
      } else {
        this.$refs.escChart.chart.xAxis[0].setExtremes(this.$refs.escChart.chart.xAxis[0].userMin, this.$refs.escChart.chart.xAxis[0].userMin + offset)
      }
    },

    downloadXLSX: function() {
      /* Add your custom logic to download CSV file with desired data*/
      let ret = []
      for (let i = 1; i < 5; i ++) {
        // 현재 대차 스케줄
        if (this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].start !== null 
        && this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].stop !== null) {    
          ret.push({
            x: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].start).getTime(),
            x2: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].stop).getTime(),
            y: i,
          })
        }
        // 다음 대차 스케줄
        if (this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].next_start !== null 
        && this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].next_end !== null) {    
          ret.push({
            x: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].next_start).getTime(),
            x2: new Date(this.$store.state.sedimentation.latest['e_sc' + i + '_schedule'].next_end).getTime(),
            y: i,
          })
        }
      }

      // 엑셀에 필요한 정보 custom
      for (const excel of ret) {
        const excelLogs = {
            DateTime: '', 
            수집기운영스케줄: ''
        };
        excelLogs.DateTime = this.$moment(excel.x).format('YYYY-MM-DD HH:mm') + ' ~ ' + this.$moment(excel.x2).format('YYYY-MM-DD HH:mm');
        excelLogs.수집기운영스케줄 = excel.y + '지';
        
        if( ret.length != this.excel.length ) {
          this.excel.push(excelLogs);
        }
      }

      // 엑셀 워크시트로 json 내보내기
      const dataWS = XLSX.utils.json_to_sheet(this.excel);
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, dataWS, '수집기운영스케줄');
      XLSX.writeFile(wb, `${this.$moment().format('YYYYMMDDHHmmss')}_수집기 운영 스케줄.xlsx`);
    }
  },

  updated: function () {
    this.initExtremesRange()
  }
}
</script>
