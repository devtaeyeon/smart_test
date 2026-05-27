<template>
  <div class="column-chart-container">
    <highcharts :options="graphSeries"/>
  </div>
</template>

<script>
// import Highcharts from 'highcharts'
// Highcharts.setOptions({
//   global: {
//     useUTC: false
//   },
//   lang: {
//     thousandsSep: ','
//   }
// })

export default {
  name: 'AlarmDetailtrand',
  data: () => ({
    chartOptions: {
      chart: {
        type: 'column',
        backgroundColor:false,
        height: '322px'
      },
      title: {
          text: null
      },
      xAxis: {
        lineColor:'rgb(121,170,203,0.45)',
        gridLineWidth: 1,
        title: {
          text: false
        },
        categories: [],
        crosshair: true,
        gridLineColor: 'rgb(121,170,203,0.45)',
        labels: {
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        }
      },
      yAxis: {
        title: {
          text: false
        },
        // visible: true,
        gridLineColor: false,
        labels: {
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        }
      },
      plotOptions: {
        column: {
          pointPadding: 0.2,
          borderWidth: 0,
          dataLabels: {
            enabled: true,
            style: {
              color: 'white',
              textOutline: 0,
            }
          },
          enableMouseTracking: false
        }
      },
      series: [],
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
  props: [
    'graphData',
    'selectedIndex'
  ],
  components: {
    
  },
  computed: {
    graphSeries: function () {
      var chart = this.chartOptions
      chart.series = []
      chart.xAxis.categories = []
      let _series = {}
      _series.data = []
      _series.pointWidth = 30
      let selectedGraphData = null
      if (this.selectedIndex === 'auto_control') {
        selectedGraphData = this.graphData.auto_control
        _series.name = ''
      } else if (this.selectedIndex === 'user_control') {
        selectedGraphData = this.graphData.user_control
        _series.name = ''
      } else {
        selectedGraphData = this.graphData.total
        _series.name = ''
      }
      selectedGraphData.forEach((it) => {
        let almNm = `${it.process_name} AI 제어 ${it.ctr_yn == 'A' ? '(자동)' : it.alm_ty == 2 ? '(반자동)' : '(임계치)'}`
        chart.xAxis.categories.push(almNm) 
        _series.data.push(it.count)
      })
      chart.series.push(_series)
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_세부 알람 트렌드'

      return chart
    }
  },
  methods: {
  },
  created: function () {
    console.log(this.$options.name + ' created')
  },
  mounted: function () {
    console.log(this.$options.name + ' mounted')
  },
  destroyed: function () {
    console.log(this.$options.name + ' destoryed')
  },
  updated: function () {
    console.log(this.$options.name + ' updated')
  }
}
</script>

<style lang="scss" scoped>


.column-chart-container {
  display:flex;
  flex-direction: column;
  width: 100%;
  height: 322px;
  padding: 21px 0 21px 0;

  align-items: center;
  color: white;
}
</style>