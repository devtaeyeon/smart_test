<template>
  <div class="system-monitoring-main">
    <!-- 시스템 모니터링 헤더 -->
    <div class="system-monitoring-text-container">
      <div class="system-monitoring-text-contents">
        <div class="system-monitoring-text"> 시스템 모니터링 </div>
      </div>
    </div>
    <!-- 시스템 모니터링 차트 컨테이너 -->
    <div class="system-monitoring-graph-container">
      <!-- 좌측 리스트 아이템(CPU, MEM, DISK, ETH) 선택 컨테이너 -->
      <div class="system-list-item-container">
        <div :class="{ block: 0 === selectedIndex, unblock: 0 !== selectedIndex }" @click="selectItem(0)">
          <div class="inner-container">
            <div class="item-text">
              CPU
              <div class="item-text1">현재 사용률 : {{ cpuUsageValue() }}%</div>  
            </div>
          </div>
        </div>
        <div :class="{ block: 1 === selectedIndex, unblock: 1 !== selectedIndex }" @click="selectItem(1)">
          <div class="inner-container">
            <div class="item-text">
              Memory
              <div class="item-text1">현재 사용률 : {{ memoryUsageValue() }}%</div>
            </div>
          </div>
        </div>
        <div :class="{ block: 2 === selectedIndex, unblock: 2 !== selectedIndex }" @click="selectItem(2)">
          <div class="inner-container">
            <div class="item-text">Disk</div>
          </div>
        </div>
        <div :class="{ block: 3 === selectedIndex, unblock: 3 !== selectedIndex }" @click="selectItem(3)">
          <div class="inner-container">
            <div class="item-text">
              Ethernet
              <div class="item-text1">Sent 사용량 : {{ sentInterfaceUsageValue() }}</div>
              <div class="item-text1">Recv 사용량 : {{ recvInterfaceUsageValue() }}</div>
            </div>
          </div>
        </div>
      </div>
      <!-- 우측 차트(DISK) -->
      <div v-if="selectedIndex === 2" class="progress_container">
        <div class="progress_item_container" v-for="(item, index) in diskUsage" :key="index">
          <div class="progress_item_title_container">
            <div class="progress_item_title">{{ item.name }}</div>
          </div>
          <!-- 프로그레스 컨테이너 -->
          <div class="cssProgress_container">
            <!-- 프로그래스 -->
            <div class="cssProgress">
              <div class="progress2">
                <div class="cssProgress-bar cssProgress-active" :class="getProgressColorStyle(index)" :style="getProgressValueStyle(item.value)"></div>
              </div>
            </div>
            <!-- 퍼센트 -->
            <div class="progress_percentage_container">
              <div class="progress_percentage">0</div>
              <div class="progress_percentage">25</div>
              <div class="progress_percentage">50</div>
              <div class="progress_percentage">75</div>
              <div class="progress_percentage">100</div>
            </div>
          </div>
          <div class="progress_tail"></div>
        </div>
      </div>
      <!-- 우측 차트(CPU, MEM, ETH) -->
      <div v-else class="system-monitoring-graph-contents">
        <highcharts :options="graphSeries"/>
      </div>
    </div>
  </div>
</template>

<script>
import { util } from '@/service/utils'
// import Highcharts from 'highcharts'
// 하이차트 UTC 해제 및 천 단위로 콤마(,) 설정
// Highcharts.setOptions({
//   global: {
//     useUTC: false
//   },
//   lang: {
//     thousandsSep: ','
//   }
// })
export default {
  name: 'SystemMonitoring',
  mixins: [util],
  components: {
  },
  data: () => ({
    selectedIndex: 0, // 현재 선택된 모니터링 항목(기본값 CPU)
    // 차트 옵션
    chartOptions: {
      chart: {
        backgroundColor:false,
        width: 712.4,
        height: 429.5
      },
      title: {
        text: null
      },
      legend: {
        enabled: false
      },
      xAxis: {
        lineColor:'rgb(121,170,203,0.45)',
        gridLineWidth: 1,
        title: {
          text: false
        },
        crosshair: true,
        categories: [],
        type: 'datetime',
        tickInterval: 1000 * 60 * 10 * 2.5 * 1,
        gridLineColor: 'rgb(121,170,203,0.45)',
        labels: {
          format: '{value:%H:%M}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        }
      },
      yAxis: null,
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
      },
      credits: {
        enabled: false
      }
    }
  }),
  computed: {
    // CPU 사용량
    cpuUsage: function () {
      if (this.$store.state.performance.monitoring.length > 0) {
        let result = this.$store.state.performance.monitoring.filter(it => it.type === this.$store.state.performance.type.CPU)
          .map(function (key) { return [key.update_time, Number(key.value)] }).reverse()
        return result
      }
      return []
    },
    // 메모리 사용량
    memoryUsage: function () {
      if (this.$store.state.performance.monitoring.length > 0) {
        let result = this.$store.state.performance.monitoring.filter(it => it.type === this.$store.state.performance.type.MEMORY)
          .map(function (key) { return [key.update_time, Number(key.value)] }).reverse()
        return result
      }
      return []
    },
    // 이더넷 사용량(sent)
    sentInterfaceUsage: function () {
      if (this.$store.state.performance.monitoring.length > 0) {
        let result = this.$store.state.performance.monitoring.filter(
          it => it.type === this.$store.state.performance.type.ETHERNET_SENT && 
          it.name === this.$store.state.performance.selectedHost.interfaceInfo[this.$store.state.performance.selectedNetworkIndex].name)
          .map((key) => { 
            return [key.update_time, parseInt(key.value)] 
          }).reverse()
        return result
      }
      return []
    },
    // 이더넷 사용량(recv)
    recvInterfaceUsage: function () {
      if (this.$store.state.performance.monitoring.length > 0) {
        let result = this.$store.state.performance.monitoring.filter(
          it => it.type === this.$store.state.performance.type.ETHERNET_RECV && 
          it.name === this.$store.state.performance.selectedHost.interfaceInfo[this.$store.state.performance.selectedNetworkIndex].name)
          .map((key) => { 
            return [key.update_time, parseInt(key.value)] 
          }).reverse()
          console.log(result)
        return result
      }
      return []
    },
    // 디스크 사용량
    diskUsage: function () {
      if (this.$store.state.performance.monitoring.length > 0) {
        let diskGroups = this.$store.state.performance.monitoring.filter(it => it.type === this.$store.state.performance.type.DISK)
        let diskGroup = this.groupBy(diskGroups, diskGroup => diskGroup.name)
        let diskUasage = []
        for (let item of diskGroup) {
          if (item[1][0].value >= 0) {
            diskUasage.push({ name: item[0], value: item[1][0].value })
          }
        }
        return diskUasage.reverse()
      }
      return []
    },
    // 하이차트 그래프 차트 옵션
    graphSeries: function () {
      var chart = this.chartOptions
      // CPU
      if (this.selectedIndex === this.$store.state.performance.type.CPU - 1) {
        chart.exporting.filename = '시스템 모니터링 CPU'
        chart.series = [
          {
            type: "area",
            name: 'CPU 사용률',
            data: this.cpuUsage,
            color: '#c481f5',
            fillColor: {
              linearGradient: {x1: 0, y1: 1, x2: 0, y2: 0},
              stops: [
                [0, 'rgba(196, 129, 254, 0)'],
                [1, 'rgba(196, 129, 254, 0.5)']
              ]
            },
            lineColor: '#c481f5',
            marker: {
              enabled: false
            }
          }
        ],
        chart.yAxis = [
          {
            min: 0,
            max: 100,
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
            }
          },
          {
            min: 0,
            max: 100,
            title: {
            },
            opposite: false,
            visible: false
          }
        ],
        chart.legend = {
          enabled: false
        },
        chart.tooltip = {
          headerFormat: '<span style="font-size:10px">{point.key:%Y-%m-%d %H:%M:%S}</span><table>',
          pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
              '<td style="padding:0"><b>{point.y}</b></td></tr>',
          footerFormat: '</table>',
          shared: true,
          useHTML: true,
          valueSuffix: '%'
        }
      // 메모리
      } else if (this.selectedIndex === this.$store.state.performance.type.MEMORY - 1) {
        chart.exporting.filename = '시스템 모니터링 Memory'
        chart.series = [
          {
            type: 'area',
            name: '메모리 사용률',
            data: this.memoryUsage,
            color: '#c481f5',
            fillColor: {
              linearGradient: {x1: 0, y1: 1, x2: 0, y2: 0},
              stops: [
                [0, 'rgba(196, 129, 254, 0)'],
                [1, 'rgba(196, 129, 254, 0.5)']
              ]
            },
            lineColor: '#c481f5',
            marker: {
              enabled: false
            }
          }
        ],
        chart.yAxis = {
          min: 0,
          max: 100,
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
          }
        },
        chart.legend = {
          enabled: false
        },
        chart.tooltip = {
          headerFormat: '<span style="font-size:10px">{point.key:%Y-%m-%d %H:%M:%S}</span><table>',
          pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
              '<td style="padding:0"><b>{point.y}</b></td></tr>',
          footerFormat: '</table>',
          shared: true,
          useHTML: true,
          valueSuffix: '%'
        }
      // 이더넷
      } else if (this.selectedIndex === this.$store.state.performance.type.ETHERNET_SENT - 1) {
        chart.exporting.filename = '시스템 모니터링 Ethernet'
        chart.series = [
          {
            type: 'area',
            name: 'sent',
            data: this.sentInterfaceUsage,
            color: '#0bc5d5',
            fillColor: {
              linearGradient: {x1: 0, y1: 1, x2: 0, y2: 0},
              stops: [
                [0, 'rgba(11, 197, 213, 0)'],
                [1, 'rgba(11, 197, 213, 0.5)']
              ]
            },
            lineColor: '#0bc5d5',
            maerker: {
              enabled: false
            }
          },
          {
            type: 'area',
            name: 'recv',
            data: this.recvInterfaceUsage,
            color: '#c481f5',
            fillColor: {
              linearGradient: {x1: 0, y1: 1, x2: 0, y2: 0},
              stops: [
                [0, 'rgba(196, 129, 254, 0)'],
                [1, 'rgba(196, 129, 254, 0.5)']
              ]
            },
            lineColor: '#c481f5',
            maerker: {
              enabled: false
            }
          }
        ],
        chart.yAxis = {
          min: null,
          max: null,
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
          }
        },
        chart.legend = {
          layout: 'vertical',
          align: 'right',
          verticalAlign: 'top',
          x: -20,
          // y: 10,
          floating: true,
          borderWidth:0,
          enabled: true,
          labelFormatter: function () {
            return '<span style="color: ' + this.color + '">' + this.name + '</span>'
          }
        },
        chart.tooltip = {
          headerFormat: '<span style="font-size:10px">{point.key:%Y-%m-%d %H:%M:%S}</span><table>',
          pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
              '<td style="padding:0"><b>{point.y}</b></td></tr>',
          footerFormat: '</table>',
          shared: true,
          useHTML: true,
          valueSuffix: 'Byte'
        }
      }
      return chart
    }
  },
  methods: {
    /**
     * 시스템 모니터링 항목 선택하는 항목
     * 
     * @param index cpu, mem, disk, eth
     */
    selectItem: function (index) {
      this.selectedIndex = index
    },
    // cpu 사용률 최근값
    cpuUsageValue: function () {
      if (this.cpuUsage.length > 0) {
        return this.cpuUsage[this.cpuUsage.length - 1][1]
      } else {
        return 0
      }
    },
    // mem 사용률 최근값
    memoryUsageValue: function () {
      if (this.memoryUsage.length > 0) {
        return this.memoryUsage[this.memoryUsage.length - 1][1]
      } else {
        return 0
      }
    },
    // 이더넷(sent) 사용률 최근값
    sentInterfaceUsageValue: function () {
      if (this.sentInterfaceUsage.length > 0) {
        return this.bytesToSizeWithUnit(this.sentInterfaceUsage[this.sentInterfaceUsage.length - 1][1])
      } else {
        return 0
      }
    },
    // 이더넷(recv) 사용률 최근값
    recvInterfaceUsageValue: function () {
      if (this.recvInterfaceUsage.length > 0) {
        return this.bytesToSizeWithUnit(this.recvInterfaceUsage[this.recvInterfaceUsage.length - 1][1])
      } else {
        return 0
      }
    },
    /**
     * 디스크 값에 따라 너비 조절
     * 
     * @param val 디스크 사용량
     * @return 너비 %
     */
    getProgressValueStyle: function (val) {
      return 'width: ' + val + '%;'
    },
    /**
     * 디스크 파티션에 따라 색상 변경
     * 
     * @param val 파티션 인덱스
     * @return cssProgress[1,2,3,4]
     */
    getProgressColorStyle: function (val) {
      let i = val % 4
      return 'cssProgress-' + i
    }
  }
}
</script>

<style lang="scss" scoped>
// 최상위 엘리먼트
.system-monitoring-main {
  width: 931px;
  height: 522px;
  margin: 16px 24px 19.6px 16px;
  padding: 17.7px 18.5px 23.9px 19.4px;
  // 시스템 모니터링 제목
  .system-monitoring-text-contents {
    display: flex;
    margin: 0px, 0px, 16.2px, 0px;
    .system-monitoring-text {
      width: 200px;
      height: 41px;
      background-image: url('../../../assets/editableDashboard/title_under.png');
      font-size: 18px;
      font-weight: bold;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.3;
      letter-spacing: normal;
      text-align: left;
      text-shadow: 0 0 9px #5cafff;
      color: #ffffff;
      padding-left: 32px;
      background-position-y: -6px;
    }
  }
  // 시스템 모니터링 그래프
  .system-monitoring-graph-container {
    width: 100%;
    display: flex;
    height: 462px;
    justify-content: center;
    align-items: center;
    background-image: url('../../../assets/editableDashboard/system_monitoring_contents_box.png');
    background-size: 100% 100%;
    // 좌측 항목(CPU, MEM, DISK, ETH)
    .system-list-item-container {
      display:flex;
      flex-direction: column;
      justify-content: space-between;
      width: 161px;
      height: 429px;
      margin-right: 10.4px;
      .block {
        position: relative;
        z-index: 0;
        width: 157px;
        height: 103px;
        left: 2px;
        cursor: pointer;
        .inner-container {
          display:flex;
          flex-direction: column;
          align-items: center;
          width: 100%;
          height: 100%;
          z-index: 1;
          background-color: #0A1C66;
          align-items: center;
          .item-text {
            font-size: 28px;
            font-weight: bold;
            color: #ffffff;
            text-align: center;
            margin: auto;
          }
          .item-text1 {
            font-size: 13px;
            font-weight: bold;
            font-stretch: normal;
            font-style: normal;
            letter-spacing: normal;
            text-align: center;
            color: #ffffff;
          }
        }
      }
      .block::before,
      .block::after {
        content: '';
        position: absolute;
        left: -2px;
        top: -2px;
        background: linear-gradient(45deg, #29f39b, #16cbca, #02a2fa,#16cbca, #29f39b, #16cbca, #02a2fa, #16cbca, #28f09c);
        background-size: 400%;
        width: calc(100% + 4px);
        height: calc(100% + 4px);
        z-index: -1;
        -webkit-animation: steam 20s linear infinite;
        animation: steam 20s linear infinite;
      }
      .unblock {
        position: relative;
        z-index: 0;
        width: 161px;
        height: 103px;
        background-color: rgba(43,92,149,0.45);
        cursor: pointer;
        .inner-container {
          display:flex;
          flex-direction: column;
          align-items: center;
          width: 100%;
          height: 100%;
          z-index: 1;
          align-items: center;
          .item-text {
            font-size: 28px;
            font-weight: bold;
            color: rgba(103,158,186,0.45);
            text-align: center;
            margin: auto;
          }
          .item-text1 {
            font-size: 13px;
            font-weight: bold;
            font-stretch: normal;
            font-style: normal;
            letter-spacing: normal;
            text-align: center;
            color: rgba(103,158,186,0.45);
          }
        }
      }
    }
    // 우측 차트 컨테이너(DISK)
    .progress_container {
      width: 712px;
      height: 457px;
      border-radius: 4px;
      display: flex;
      flex-flow: column;
      overflow-x: hidden;
      overflow-y: auto;
      &::-webkit-scrollbar {
        display: none;
      }
      .progress_item_container {
        display: flex;
        flex-flow: row;
        padding-bottom: 14px;
        border-bottom: 1px dashed #ddd;
        &:last-child {
          border-bottom: none;
        }
        // 파티션
        .progress_item_title_container {
          display: flex;
          width: 108px;
          align-items: center;
          justify-content: center;
          padding-top: 22px;
          padding-left: 10px;
          padding-right: 10px;
          .progress_item_title {
            text-shadow: 0 0 9px #5cafff;
            color: #FFFFFF;
            font-size: 18px;
            font-weight: bold;
          }
        }
        // 프로그레스 컨테이너
        .cssProgress_container {
          width: calc(100% - 192px);
          padding-top: 40px;
          display: flex;
          flex-flow: column;
          // 퍼센트 컨테이너
          .progress_percentage_container {
            display: flex;
            flex-flow: row;
            align-items: center;
            justify-content: space-between;
            .progress_percentage {
              color: #aaa;
              font-size: 14px;
            }
          }
        }
        .progress_tail {
          width: 82px;
        }
      }
    }
  }
}

// 프로그레스
.cssProgress {
  width: 100%;
}
.cssProgress .progress1,
.cssProgress .progress2,
.cssProgress .progress3 {
  position: relative;
  overflow: hidden;
  width: 100%;
  font-family: "Roboto", sans-serif;
}
.cssProgress .cssProgress-bar {
  display: block;
  float: left;
  width: 0%;
  height: 100%;
  background: #3798d9;
  box-shadow: inset 0px -1px 2px rgba(0, 0, 0, 0.1);
  -webkit-transition: width 0.8s ease-in-out;
  transition: width 0.8s ease-in-out;
}
.cssProgress .cssProgress-label {
  position: absolute;
  overflow: hidden;
  left: 0px;
  right: 0px;
  color: rgba(0, 0, 0, 0.6);
  font-size: 0.7em;
  text-align: center;
  text-shadow: 0px 1px rgba(0, 0, 0, 0.3);
}
.cssProgress .cssProgress-0 {
  background-color: #56b8d6 !important;
}
.cssProgress .cssProgress-1 {
  background-color: #2071c1 !important;
}
.cssProgress .cssProgress-2 {
  background-color: #512db7 !important;
}
.cssProgress .cssProgress-3 {
  background-color: #962296 !important;
}
.cssProgress .cssProgress-right {
  float: right !important;
}
.cssProgress .cssProgress-label-left {
  margin-left: 10px;
  text-align: left !important;
}
.cssProgress .cssProgress-label-right {
  margin-right: 10px;
  text-align: right !important;
}
.cssProgress .cssProgress-label2 {
  display: block;
  margin: 2px 0;
  padding: 0 8px;
  font-size: 0.8em;
}
.cssProgress .cssProgress-label2.cssProgress-label2-right {
  text-align: right;
}
.cssProgress .cssProgress-label2.cssProgress-label2-center {
  text-align: center;
}
.cssProgress .cssProgress-stripes,
.cssProgress .cssProgress-active,
.cssProgress .cssProgress-active-right {
  background-image: -webkit-linear-gradient(135deg, rgba(255, 255, 255, 0.125) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, 0.125) 50%, rgba(255, 255, 255, 0.125) 75%, transparent 75%, transparent);
  background-image: linear-gradient(-45deg, rgba(255, 255, 255, 0.125) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, 0.125) 50%, rgba(255, 255, 255, 0.125) 75%, transparent 75%, transparent);
  background-size: 35px 35px;
}
.cssProgress .cssProgress-active {
  -webkit-animation: cssProgressActive 2s linear infinite;
  -ms-animation: cssProgressActive 2s linear infinite;
  animation: cssProgressActive 2s linear infinite;
}
.cssProgress .cssProgress-active-right {
  -webkit-animation: cssProgressActiveRight 2s linear infinite;
  -ms-animation: cssProgressActiveRight 2s linear infinite;
  animation: cssProgressActiveRight 2s linear infinite;
}
@-webkit-keyframes cssProgressActive {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 35px 35px;
  }
}
@-ms-keyframes cssProgressActive {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 35px 35px;
  }
}
@keyframes cssProgressActive {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 35px 35px;
  }
}
@-webkit-keyframes cssProgressActiveRight {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: -35px -35px;
  }
}
@-ms-keyframes cssProgressActiveRight {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: -35px -35px;
  }
}
@keyframes cssProgressActiveRight {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: -35px -35px;
  }
}
.progress2 {
  background-color: #2B4365;
  border-radius: 16px;
}
.progress2 .cssProgress-bar {
  height: 32px;
  border-radius: 16px;
}
.progress2 .cssProgress-label {
  line-height: 18px;
}
</style>