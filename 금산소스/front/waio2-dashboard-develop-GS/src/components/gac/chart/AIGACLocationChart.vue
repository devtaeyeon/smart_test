<template>
  <div style="height: 100%">
    <highcharts :options="ChartSchedule" ref="highcharts" style="height: 100%"/>
  </div>
</template>

<script>
import { PLOT_BANDS_WINTER, PLOT_BANDS_SUMMER, PLOT_BANDS_SPRING, SPRING, SUMMER, WINTER } from '@/store'
import moment from 'moment'
import * as XLSX from 'xlsx'

export default {
  name:'AIGACLocationChart',
  props: [
    'selectedLine'
  ],
  data() {
    return {
      isInit: true,
      excel: [],
      // AI 운영 스케줄 차트
      chartDataSchedule: {
        chart: {
          type: 'xrange',
          backgroundColor: false,
          // zoomType: 'x'
        },
        title: {
          text: null,
          style: {
            color: 'white'
          }
        },
        credits: {
          enabled: false
        },
        tooltip: {
          headerFormat: '<span style="font-size:16px">{point.x:%Y-%m-%d %H:%M:%S} ~ {point.x2:%Y-%m-%d %H:%M:%S}</span>',
          pointFormat: ''
        },
        legend: {
          enabled: false
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
          tickInterval: 1000 * 60 * 60 * 24
        },
        yAxis: {
          title: {
            text: false
          },
          gridLineColor: false,
          labels: {
            style:{
              fontFamily:'NanumSquare',
              fontSize: 13,
              color:"rgb(255,255,255,0.8)"
            }
          },
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
              // style: {
              //   textOutline: 'transparent'
              // },
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
    ChartData: function () {
      let colorXRange = '#b4dffb'
      let colorBW = '#ff0000'
      // let bwTimeLength = 1000 * 60 * 10
      let opacityRange = 1
      let _data = []
      for (let i = 5, j = 6; i < 18; i += 2, j --) {
        if (this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].start !== '0' && this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].end !== '0') {
          _data.push({
            x: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].start).getTime(),
            x2: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].end).getTime(),
            y: j,
            color: colorXRange,
            opacity: opacityRange,
          })
        } else {
          _data.push({
            x: null,
            x2: null,
            y: j,
            color: colorXRange,
            opacity: opacityRange,
          })
        }
        if (this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].next_start !== '0' && this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].next_end !== '0') {
          _data.push({
            x: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].next_start).getTime(),
            x2: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].next_end).getTime(),
            y: j,
            color: colorXRange,
            opacity: opacityRange,
          })
        } else {
          _data.push({
            x: null,
            x2: null,
            y: j,
            color: colorXRange,
            opacity: opacityRange,
          })
        }
        if (this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].bw_start !== '0' && this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].bw_end !== '0') {
          _data.push({
            x: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].bw_start).getTime(),
            x2: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + (i + (this.selectedLine - 1))].bw_end).getTime(),
            y: j,
            color: colorBW,
          })
        }
      }
      return _data
    },
    ChartSchedule: function () {
      let chart = this.chartDataSchedule
      if (this.$store.state.gac.latest.ai_i_location_schedule !== null) {
        let now = new Date()
        chart.xAxis.plotLines = [{
          color: 'white',
          dashStyle: 'ShortDash', 
          value: now.getTime(),
          width: 3, 
        }]
        let minMax = this.$getMinMaxTimestampAIFLocationSchedule(this.$store.state.gac.latest.ai_i_location_schedule)
        // 1. 가장 작은 시간
        let xAxisMinValue = minMax[0] //- this.$getMilliSecondFromHour(12)
        // 2. 가장 큰 시간
        let xAxisMaxValue = minMax[1] //+ this.$getMilliSecondFromHour(12)
        chart.xAxis.min = xAxisMinValue
        chart.xAxis.max = xAxisMaxValue
        // 3. 가장 작은 시간 ~ 가장 큰 시간 사이의 밴드 표현
        // 3.1. 가장 작은 시간의 년, 월, 일 ~ 가장 큰 시간 사이의 년, 월, 일사이를 loop + 1일
        let dateStart = new Date(xAxisMinValue)
        dateStart.setHours(0)
        dateStart.setMinutes(0)
        dateStart.setSeconds(0)
        let dateEnd = new Date(xAxisMaxValue)
        chart.xAxis.plotBands = []
        while(dateStart.getTime() < dateEnd.getTime()) {
          // 3.2. 해당 날짜에 해당되는 plotBands 추가
          // 3.3. plotBands에 년, 원, 일 timestamp 추가
          let season = this.getSeason(dateStart)
          switch(season) {
            case SPRING:
              PLOT_BANDS_SPRING.map((it) => {
                let obj = {}
                obj.color = it.color
                obj.from = (this.$getMilliSecondFromHour(it.from) + dateStart.getTime())
                // console.log(new Date(obj.from))
                obj.to = (this.$getMilliSecondFromHour(it.to) + dateStart.getTime())
                chart.xAxis.plotBands.push(obj)
              })
              break
            case SUMMER:
              PLOT_BANDS_SUMMER.map((it) => {
                let obj = {}
                obj.color = it.color
                obj.from = (this.$getMilliSecondFromHour(it.from) + dateStart.getTime())
                // console.log(new Date(obj.from))
                obj.to = (this.$getMilliSecondFromHour(it.to) + dateStart.getTime())
                chart.xAxis.plotBands.push(obj)
              })
              break
            case WINTER:
              PLOT_BANDS_WINTER.map((it) => {
                let obj = {}
                obj.color = it.color
                obj.from = (this.$getMilliSecondFromHour(it.from) + dateStart.getTime())
                // console.log(new Date(obj.from))
                obj.to = (this.$getMilliSecondFromHour(it.to) + dateStart.getTime())
                chart.xAxis.plotBands.push(obj)
              })
              break
          }
          dateStart.setDate(dateStart.getDate() + 1)
        }
        if (this.selectedLine === 1) {
          chart.yAxis.categories = ['17지','15지','13지','11지','9지','7지','5지']
        } else {
          chart.yAxis.categories = ['18지','16지','14지','12지','10지','8지','6지']
        }
        chart.navigator = {
          enabled: false,
          series: {
            type: 'xrange',
            lineWidth: 0.1,
            opacity: 0.5,
            dataGrouping: {
              smoothed: true
            },
            marker: {
              enabled: false
            }
          },
        },
        chart.series = [{
          name: 'GAC여과',
          pointWidth: 15,
          borderWidth: 0,
          borderRadius: 0,
          data: this.ChartData,
          dataLabels: {
            enabled: true
          }
        }]
        chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_GAC여과 AI 운영 스케줄'

        const exportName = chart.exporting.buttons.contextButton.menuItems[2]
        if (exportName == undefined) {
          chart.exporting.buttons.contextButton.menuItems.push({
          text: 'Download Excel',
          onclick: this.downloadExcel
          })
        }
        return chart
      }
      return chart
    }
  },

  methods: {
    initExtremesRange: function () {
      let minMax = this.$getMinMaxTimestampAIFLocationSchedule(this.$store.state.gac.latest.ai_i_location_schedule)
      let offset = parseInt((minMax[1]- minMax[0]) / 2)
      if (this.isInit) {
        this.$refs.highcharts.chart.xAxis[0].setExtremes(minMax[0], minMax[0] + offset)
        this.isInit = false
      } else {
        // console.log(this.$refs.escChart.chart.xAxis[0].min)
        // console.log(this.$refs.escChart.chart.xAxis[0].userMin)
        // console.log(this.$refs.escChart.chart.xAxis[0].dataMin)
        this.$refs.highcharts.chart.xAxis[0].setExtremes(this.$refs.highcharts.chart.xAxis[0].userMin, this.$refs.highcharts.chart.xAxis[0].userMin + offset)
      }
    },
    /**
     * 날짜에 따른 중간부하, 최대부하 계절 반환
     * 
     * @param date 날짜
     * @return 계절
     */
    getSeason: function(date) {
      if ([3, 4, 5, 9, 10].includes(date.getMonth() + 1)) {
        return SPRING
      } else if ([6, 7, 8].includes(date.getMonth() + 1)) {
        return SUMMER
      } else {
        return WINTER
      }
    },

    downloadExcel: function() {
      // let chart = this.chartDataSchedule
      /* Add your custom logic to download CSV file with desired data*/
      let ret = []

      for (let i = 5; i < 19; i ++) {
        if (this.$store.state.gac.latest.ai_i_location_schedule['location' + i].start !== '0' 
        && this.$store.state.gac.latest.ai_i_location_schedule['location' + i].end !== '0') {
          ret.push({
            x: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + i ].start).getTime(),
            x2: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + i ].end).getTime(),
            y: i
          })
        }
        if (this.$store.state.gac.latest.ai_i_location_schedule['location' + i ].next_start !== '0' 
        && this.$store.state.gac.latest.ai_i_location_schedule['location' + i ].next_end !== '0') {
          ret.push({
            x: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + i ].next_start).getTime(),
            x2: new Date(this.$store.state.gac.latest.ai_i_location_schedule['location' + i ].next_end).getTime(),
            y: i
          })
        }
      }
      
      // 엑셀에 필요한 정보 custom
      for (const excel of ret) {
        const excelLogs = {
            DateTime: '', 
            AI운영스케줄: ''
        };
        excelLogs.DateTime = this.$moment(excel.x).format('YYYY-MM-DD HH:mm') + ' ~ ' + this.$moment(excel.x2).format('YYYY-MM-DD HH:mm');
        excelLogs.AI운영스케줄 = excel.y + '지';
        
        if( ret.length != this.excel.length ) {
          this.excel.push(excelLogs);
        }
      }

      // 엑셀 워크시트로 json 내보내기
      const dataWS = XLSX.utils.json_to_sheet(this.excel);
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, dataWS, 'GAC여과 AI 운영 스케줄');
      XLSX.writeFile(wb, `${this.$moment().format('YYYYMMDDHHmmss')}_GAC여과 AI 운영 스케줄.xlsx`);
    }
  },
  updated: function () {
    console.log("updated!!!!!!!!!!!!")
    this.initExtremesRange()
  }
}
</script>

<style scoped>

</style>
