<template>
  <div class="chart-container">
    <!-- 원수 유입 유량 예측 차트 -->
    <div class="chart-contents">
      <div class="chart-title">
        <div class="chart-title__text">원수 유입 유량 예측</div>
      </div>
      <div class="chart">
        <highcharts ref="chartRawWaterFlow" :options="ChartRawWaterFlow" style="height:100%;"/>
      </div>
    </div>
    <!-- 정수지 수위 밴드 차트 -->
    <div class="chart-contents marginleft10">
      <div class="chart-title">
        <div class="chart-title__text">정수지 수위 밴드</div>
        <div class="chart-title-tab">
          <div class="chart-title-tab__box" @click="selectedLocation = 1" :class="{'selected': selectedLocation === 1}">#1</div>
          <div class="chart-title-tab__box" @click="selectedLocation = 2" :class="{'selected': selectedLocation === 2}">#2</div>
          <div class="chart-title-tab__box" @click="selectedLocation = 3" :class="{'selected': selectedLocation === 3}">#3</div>
          <!-- <div class="chart-title-tab__box" @click="selectedLocation = 4" :class="{'selected': selectedLocation === 4}">#4</div> -->
        </div>
      </div>
      <div class="chart">
        <highcharts ref="chartClearWaterLevel" :options="ChartClearWaterLevel" style="height:100%;"/>
      </div>
    </div>
    <!-- 착수정 유출 유량 차트 -->
    <div class="chart-contents marginleft10">
      <div class="chart-title">
        <div class="chart-title__text">정수지 유출 유량</div>
      </div>
      <div class="chart">
        <highcharts ref="chartReceivingWaterFlow" :options="ChartReceivingWaterFlow" style="height:100%;"/>
      </div>
    </div>
  </div>  
</template>
<script>
import * as XLSX from 'xlsx'

export default {
  name:'ReceivingHighchart',
  data() {
    return {
      selectedLocation: 1, // 선택된 계열
      location1: [],
      location2: [],
      location3: [],
      location4: [],
      // 원수 유입 유량 차트
      chartDataRawWaterFlow: {
        chart: {
          type: 'spline',
          backgroundColor: false,
          zoomType: 'x'
        },
        title: {
          useHTML: true,
          text: '원수 유입 유량 예측',
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
          useHTML: true,
          valueSuffix: 'm<sup>3</sup>/h'
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
          tickInterval: 1000 * 60 * 60 * 10 // 48시간
        },
        yAxis: {
          title: {
            align: 'high',
            text: 'm<sup>3</sup>/h',
            useHTML: true,
            offset: 0,
            rotation: 0,
            y: -15,
            x: -10,
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
              menuItemStyle: { padding: "0 10px", background: "none", color: "#303030" },
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
      // 착수정 유출 유량 차트
      chartDataReceivingWaterFlow: {
        chart: {
          type: 'spline',
          backgroundColor: false,
          zoomType: 'x'
        },
        title: {
          useHTML: true,
          text: '착수정 유출 유량',
          style: {
            color: 'transparent'
          }
        },
        legend: {
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
          useHTML: true,
          valueSuffix: 'm<sup>3</sup>/h'
        },
        xAxis: {
          title: '',
          type: 'datetime',
          labels: {
            format: '{value:%m-%d %H:%M}',
            style:{
              fontFamily:'NanumSquare',
              fontSize: 13,
              color:"rgb(255,255,255,0.8)"
            }
          },
        },
        yAxis: {
          title: {
            align: 'high',
            text: 'm<sup>3</sup>/h',
            useHTML: true,
            offset: 0,
            rotation: 0,
            y: -15,
            x: -10,
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
      // 정수지 수위 밴드 차트
      chartDataClearLevel: {
        chart: {
          backgroundColor: false,
          zoomType: 'x'
        },
        title: {
          useHTML: true,
          text: '정수지 수위 밴드',
          style: {
            color: 'transparent'
          }
        },
        legend: {
          enabled: true,
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
          valueSuffix: 'm'
        },
        xAxis: {
          title: '',
          type: 'datetime',
          labels: {
            format: '{value:%m-%d %H:%M}',
            style:{
              fontFamily:'NanumSquare',
              fontSize: 13,
              color:"rgb(255,255,255,0.8)"
            }
          },
          tickInterval: 1000 * 60 * 60 * 6 // 48시간
        },
        yAxis: {
          title: {
            align: 'high',
            text: 'm',
            useHTML: true,
            offset: 0,
            rotation: 0,
            y: -15,
            x: -10,
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
    } 
  },

  computed: {
    // 정수지 유출 유량 차트
    ChartReceivingWaterFlow: function () {
      let chart = this.chartDataReceivingWaterFlow
      chart.series = []
      if (this.$store.state.receiving.out_fr.series1 !== null) {
        let dataSeries1 = []
        for (let key in this.$store.state.receiving.out_fr.series1) {
          dataSeries1.push([new Date(key).getTime(), this.$store.state.receiving.out_fr.series1[key]])
        }
        chart.series.push({
          name: '정수지',
          data: dataSeries1,
          color: 'rgb(110, 157, 225)'
        })
      }
      // if (this.$store.state.receiving.out_fr.series2 !== null) {
      //   let dataSeries2 = []
      //   for (let key in this.$store.state.receiving.out_fr.series1) {
      //     dataSeries2.push([new Date(key).getTime(), this.$store.state.receiving.out_fr.series2[key]])
      //   }
      //   chart.series.push({
      //     name: '2계열',
      //     data: dataSeries2,
      //     color: 'rgb(146, 66, 235)'
      //   })
      // }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_착수정 유출 유량'

      return chart
    },
    // 정수지 수위 밴드 차트
    ChartClearWaterLevel: function () {
      let chart = this.chartDataClearLevel
      chart.xAxis.plotLines = [{
        color: 'white', // Color value
        dashStyle: 'ShortDash', // Style of the plot line. Default to solid
        value: new Date().getTime(), // Value of where the line will appear
        width: 3, // Width of the line    
      }]
      chart.series = []
      if (this.$store.state.clear.le !== null && Object.keys(this.$store.state.clear.le).length > 0) {
        if (this.$store.state.clear.le.h_bnd_uplmt && this.$store.state.clear.le.h_bnd_lolmt) {
          let _data = []
          for (let key in this.$store.state.clear.le.h_bnd_uplmt) {
            _data.push([new Date(key).getTime(), this.$store.state.clear.le.h_bnd_uplmt[key], this.$store.state.clear.le.h_bnd_lolmt[key]])
          }
          chart.series.push({
            name: '범위',
            type: 'arearange',
            fillOpacity: 0.3,
            zIndex: 0,
            data: _data,
            color: '#55a3ff',
          })
        }
        // if (this.$store.state.clear.le.wide_up && this.$store.state.clear.le.wide_down) {
        //   let _data = []
        //   for (let key in this.$store.state.clear.le.wide_up) {
        //     _data.push([new Date(key).getTime(), this.$store.state.clear.le.wide_up[key], this.$store.state.clear.le.wide_down[key]])
        //   }
        //   chart.series.push({
        //     name: '넓은범위',
        //     type: 'arearange',
        //     fillOpacity: 0.3,
        //     zIndex: 0,
        //     data: _data,
        //     color: '#0073fa'
        //   })
        // }
        chart.series.push({
          name: '수위',
          data: this.ChartDataClearLevelFromLocation,
          color: '#45d6e2'
        })
      }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_정수지 수위 밴드'

      const exportName = chart.exporting.buttons.contextButton.menuItems[2]
      if (exportName == undefined) {
        chart.exporting.buttons.contextButton.menuItems.push({
        text: 'Download Excel',
        onclick: this.downloadExcel
        })
      }
      
      return chart
    },
    // 정수지 수위 밴드 데이터
    ChartDataClearLevelFromLocation: function () {
      let data = []
      if (this.$store.state.clear.le !== null 
      && this.$store.state.clear.le['location' + this.selectedLocation] !== undefined 
      && this.$store.state.clear.le['location' + this.selectedLocation] !== null 
      && Object.keys(this.$store.state.clear.le['location' + this.selectedLocation]).length > 0) {
        for (let key in this.$store.state.clear.le['location' + this.selectedLocation]) {
          data.push([new Date(key).getTime(), this.$store.state.clear.le['location' + this.selectedLocation][key]])
        }
      }
      return data
    },
    // 원수 유입 유량 차트
    ChartRawWaterFlow: function () {
      let chart = this.chartDataRawWaterFlow
      chart.xAxis.plotLines = [{
        color: 'white', // Color value
        dashStyle: 'ShortDash', // Style of the plot line. Default to solid
        value: new Date().getTime(), // Value of where the line will appear
        width: 3, // Width of the line
      }]
      chart.series = []
      if (this.$store.state.receiving.latest.ai_b_in_fr_rtd !== null && Object.keys(this.$store.state.receiving.latest.ai_b_in_fr_rtd).length > 0) {
        let dataSeries1 = []
        for (let key in this.$store.state.receiving.latest.ai_b_in_fr_rtd) {
          dataSeries1.push([new Date(key).getTime(), parseInt(this.$store.state.receiving.latest.ai_b_in_fr_rtd[key])])
        }
        chart.series.push({
          name: '유입 유량',
          data: dataSeries1,
          color: 'rgb(110, 157, 225)'
        })
      }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_원수 유입 유량 예측'

      return chart
    }
  },

  methods: {
    downloadExcel: function() {
      let ret = this.$store.state.clear.le
      console.log('ret', ret)


      for (let ret_obj in ret) {
        for (let time in ret['h_bnd_lolmt']) {
          // console.log('ret_obj', ret_obj)
          // console.log('time', time)
          // console.log('length', Object.keys(ret['h_bnd_lolmt']).length)
          if(ret_obj == 'location1' && Object.keys(ret['h_bnd_lolmt']).length != this.location1.length) {
            const excelLogs1 = {
              DateTime: '',
              범위_low: '',
              범위_high: '',
              넓은범위_low: '',
              넓은범위_high: '',
              수위: ''
            };
            excelLogs1.DateTime = time
            excelLogs1.범위_low = ret['h_bnd_lolmt'][time]
            excelLogs1.범위_high = ret['h_bnd_uplmt'][time]
            excelLogs1.넓은범위_low = ret['wide_down'][time]
            excelLogs1.넓은범위_high = ret['wide_up'][time]
            excelLogs1.수위 = ret['location1'][time]
            
            this.location1.push(excelLogs1);
          }
          if(ret_obj == 'location2' && Object.keys(ret['h_bnd_lolmt']).length != this.location2.length) {
            const excelLogs2 = {
              DateTime: '',
              범위_low: '',
              범위_high: '',
              넓은범위_low: '',
              넓은범위_high: '',
              수위: ''
            };
            excelLogs2.DateTime = time
            excelLogs2.범위_low = ret['h_bnd_lolmt'][time]
            excelLogs2.범위_high = ret['h_bnd_uplmt'][time]
            excelLogs2.넓은범위_low = ret['wide_down'][time]
            excelLogs2.넓은범위_high = ret['wide_up'][time]
            excelLogs2.수위 = ret['location2'][time]

            this.location2.push(excelLogs2);
          } 
          if(ret_obj == 'location3' && Object.keys(ret['h_bnd_lolmt']).length != this.location3.length) {
            const excelLogs3 = {
              DateTime: '',
              범위_low: '',
              범위_high: '',
              넓은범위_low: '',
              넓은범위_high: '',
              수위: ''
            };
            excelLogs3.DateTime = time
            excelLogs3.범위_low = ret['h_bnd_lolmt'][time]
            excelLogs3.범위_high = ret['h_bnd_uplmt'][time]
            excelLogs3.넓은범위_low = ret['wide_down'][time]
            excelLogs3.넓은범위_high = ret['wide_up'][time]
            excelLogs3.수위 = ret['location3'][time]

            this.location3.push(excelLogs3);
          } 
          if(ret_obj == 'location4' && Object.keys(ret['h_bnd_lolmt']).length != this.location4.length) {
            const excelLogs4 = {
              DateTime: '',
              범위_low: '',
              범위_high: '',
              넓은범위_low: '',
              넓은범위_high: '',
              수위: ''
            };
            excelLogs4.DateTime = time
            excelLogs4.범위_low = ret['h_bnd_lolmt'][time]
            excelLogs4.범위_high = ret['h_bnd_uplmt'][time]
            excelLogs4.넓은범위_low = ret['wide_down'][time]
            excelLogs4.넓은범위_high = ret['wide_up'][time]
            excelLogs4.수위 = ret['location4'][time]

            this.location4.push(excelLogs4);
          }
        }
      }


      // 엑셀 워크시트로 json 내보내기
      const wb = XLSX.utils.book_new();

      const dataWS1 = XLSX.utils.json_to_sheet(this.location1);
      const dataWS2 = XLSX.utils.json_to_sheet(this.location2);
      const dataWS3 = XLSX.utils.json_to_sheet(this.location3);
      const dataWS4 = XLSX.utils.json_to_sheet(this.location4);

      XLSX.utils.book_append_sheet(wb, dataWS1, '#1'); 
      XLSX.utils.book_append_sheet(wb, dataWS2, '#2');
      XLSX.utils.book_append_sheet(wb, dataWS3, '#3'); 
      XLSX.utils.book_append_sheet(wb, dataWS4, '#4');

      XLSX.writeFile(wb, `${this.$moment().format('YYYYMMDDHHmmss')}_정수지 수위 밴드.xlsx`);

    }
  }
}
</script>

<style lang="scss" scoped>
// 선택된 박스
.selected{
  box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  background-color: rgba(139, 194, 240, 0.4) !important;
}
// 마진 설정
.marginleft10{
  margin-left: 14.5px;
}
// 차트 컨테이너
.chart-container{
  display: flex;
  width: 100%;
  margin: 15px 25px 0;
  // 차트 컨텐츠
  .chart-contents{
    width: 33%;
    height: 100%;
    // 차트 제목
    .chart-title{
      display: flex;
      margin-top: 10px;
      // 계열 선택 탭
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
          cursor: pointer;
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
    // 차트
    .chart{
      width: 600px;
      height: 318px;
      margin-left: 17px;
    }
  }
}
.btn-tab{
    display: flex;
    margin-left: 134px;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2.3;
    letter-spacing: normal;
    text-align: center;
    color: #fff;
    position: absolute;
    margin-left: 206px;
    .box-center-margin{
      margin: 0 16px;
    }
    &__box-abled{
      width: 90px;
      height: 34px;
      box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
      border: solid 1px #b4dffa;
      background-color: rgba(139, 194, 240, 0.4);
      cursor: pointer;
    }
    &__box-disabled{
      width: 90px;
      height: 34px;
      border: solid 1px #b4dffa;
      background-color: rgba(139, 194, 240, 0.25);
      cursor: pointer;
    }
  }
</style>