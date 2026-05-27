<template>
  <div class="gragh-main">
    <div class="gragh-contents">
      <div class="gragh-contents__title">증발량 예측에 따른 전염소 주입률 예측 트렌드</div>
      <div class="left-timer"> {{ this.LeftSecond }}</div>
      <div class="btn-tab">
        <div :class="[ leftChartSelectedIndex === 1 ? 'btn-tab__box-abled' : 'btn-tab__box-disabled' ]" @click="leftChartSelectedIndex = 1">차염1</div>
        <div :class="[ leftChartSelectedIndex === 2 ? 'btn-tab__box-abled' : 'btn-tab__box-disabled' ]" @click="leftChartSelectedIndex = 2">차염2</div>
      </div>
      <div class="gragh-contents__content">
        <highcharts :options="LeftChartOptions"/>
      </div>
    </div>
    <div class="gragh-contents">
      <div class="left-timer"> {{ this.LeftSecond }}</div>
      <div class="btn-tab">
        <div :class="[ rightChartSelectedIndex === 1 ? 'btn-tab__box-abled' : 'btn-tab__box-disabled' ]" @click="rightChartSelectedIndex = 1">차염1</div>
        <div :class="[ rightChartSelectedIndex === 2 ? 'btn-tab__box-abled' : 'btn-tab__box-disabled' ]" @click="rightChartSelectedIndex = 2">차염2</div>
      </div>
      <div class="gragh-contents__title">침전수 잔류염소 트렌드</div>
      <div class="gragh-contents__content">
        <highcharts :options="RightChartOptions"/>
      </div>
    </div>
  </div>
</template>
<script>

export default {
  data: () => ({
    leftChartSelectedIndex: 1,
    // leftChartLineData1: [],
    // leftChartColumnData1: [],
    // leftChartLineData2: [],
    // leftChartColumnData2: [],
    rightChartSelectedIndex: 1,
    // rightChartData1: [],
    // rightChartData2: [],
    leftChart: {
      chart: {
        // type: 'spline',
        backgroundColor: false,
        height: 250
      },
      title: {
        text: null
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
        // symbolWidth: 30,
        // symbolRadius: 0,
        labelFormatter: function () {
          return '<span style="color: ' + this.color + '">' + this.name + '</span>'
        }
      },
      plotOptions: {
        spline: {
          // lineWidth: 7,
          // states: {
          //     hover: {
          //         lineWidth: 5
          //     }
          // },
          marker: {
              enabled: false
          }
        }
      },
      yAxis: [{
        // gridLineWidth: 0,
        title: {
          text: '증발량',
          style: {
            color: '#ffffff'
          }
        },
        labels: {
          style: {
            color: '#ffffff'
          }
        },
        min: 0,
        // plotLines: [{
        //   color: 'red', // Color value
        //   dashStyle: 'dash', // Style of the plot line. Default to solid
        //   value: 0.8, // Value of where the line will appear
        //   width: 2 // Width of the line    
        // }]
      },{
        // gridLineWidth: 0,
        title: {
          text: '주입률',
          style: {
            color: '#ffffff'
          }
        },
        labels: {
          style: {
            color: '#ffffff'
          }
        },
        min: 0,
        // plotLines: [{
        //   color: 'red', // Color value
        //   dashStyle: 'dash', // Style of the plot line. Default to solid
        //   value: 0.8, // Value of where the line will appear
        //   width: 2 // Width of the line    
        // }]
        opposite: true
      }],
      series: [],
      credits: {
        enabled: false
      }
    },
    rightChart: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        height: 250
      },
      title: {
        text: null
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
        // symbolWidth: 30,
        // symbolRadius: 0,
        labelFormatter: function () {
          return '<span style="color: ' + this.color + '">' + this.name + '</span>'
        }
      },
      plotOptions: {
        spline: {
          // lineWidth: 7,
          // states: {
          //     hover: {
          //         lineWidth: 5
          //     }
          // },
          marker: {
              enabled: false
          }
        }
      },
      xAxis: {},
      series: [],
      credits: {
        enabled: false
      }
    }
  }),
  props: [
    'leftSecond'
  ],
 computed: {
    LeftSecond: function () {
      const timeLeft = this.leftSecond;
      let minutes = Math.floor(timeLeft / 60);
      let seconds = timeLeft % 60;

      if (minutes < 10) {
        minutes = `0${minutes}`;
      }
      if (seconds < 10) {
        seconds = `0${seconds}`;
      }

      return `${minutes}:${seconds}`;
    },
    LeftChartOptions: function() {
      let minDate = new Date(this.RightChartData[0][0])
      let maxDate = new Date(this.RightChartData[2879][0])
      let chart = this.leftChart
      let xAxis = {
        // lineColor:'rgb(121,170,203,0.45)',
        // gridLineWidth: 1,
        title: {
          text: false
        },
        min: Date.UTC(minDate.getFullYear(), minDate.getMonth(), minDate.getDate(), minDate.getHours(), minDate.getMinutes(), minDate.getSeconds()),
        max: Date.UTC(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate(), maxDate.getHours() - 18, maxDate.getMinutes(), maxDate.getSeconds()),
        // categories : ['01','02','03','04','05','06','07','08','09','10','11','12'],
        crosshair: true,
        categories: [],
        type: 'datetime',
        // milliseconds, seconds, minutes, hours, days
        tickInterval: 1000 * 60 * 60 * 6 * 1,
        labels: {
          // format: '{value:%Y-%m-%d}',
          format: '{value:%m-%d %H:%M}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        },
        plotLines: [{
          color: '#FF0000',
          width: 2,
          value: new Date(this.$store.getters.getCurrentSodocDataArray[0]),
          zIndex: 10
        }]
      }
      chart.xAxis = xAxis
      let series = [{
        type: 'column',
        name: '증발량',
        color: '#a5cceb',
        marker: {
          enabled: false
        },
        data: this.LeftChartColumnData
      },{
        type: 'spline',
        name: '주입률',
        color: '#7a91f5',
        marker: {
          enabled: false
        },
        data: this.LeftChartLineData,
        yAxis: 1
      }]
      chart.series = series
      return chart
    },
    RightChartOptions: function() {
      let minDate = new Date(this.RightChartData[0][0])
      let maxDate = new Date(this.RightChartData[2879][0])
      // console.log(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate(), maxDate.getHours(), maxDate.getMinutes(), maxDate.getSeconds())
      let chart = this.rightChart
      let xAxis = {
        // lineColor:'rgb(121,170,203,0.45)',
        // gridLineWidth: 1,
        title: {
          text: false
        },
        min: Date.UTC(minDate.getFullYear(), minDate.getMonth(), minDate.getDate(), minDate.getHours(), minDate.getMinutes(), minDate.getSeconds()),
        max: Date.UTC(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate(), maxDate.getHours() - 18, maxDate.getMinutes(), maxDate.getSeconds()),
        // categories : ['01','02','03','04','05','06','07','08','09','10','11','12'],
        crosshair: true,
        // categories: [],
        type: 'datetime',
        // milliseconds, seconds, minutes, hours, days
        tickInterval: 1000 * 60 * 60 * 6 * 1,
        labels: {
          format: '{value:%m-%d %H:%M}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        },
        plotLines: [{
          color: '#ffffff',
          width: 2,
          dashStyle: 'dash',
          value: new Date(this.$store.getters.getCurrentSodocDataArray[0]),
          zIndex: 10
        }]
      }
      chart.xAxis = xAxis
      let yAxis = {
        // gridLineWidth: 0,
        title: {
          text: '잔류염소',
          style: {
            color: '#ffffff'
          }
        },
        labels: {
          style: {
            color: '#ffffff'
          }
        },
        min: 0,
        max: 0.7,
        plotLines: [{
          color: 'red', // Color value
          // dashStyle: 'dash', // Style of the plot line. Default to solid
          value: 0.6, // Value of where the line will appear
          width: 2 // Width of the line    
        }]
      }
      chart.yAxis = yAxis
      let series = [{
        type: 'spline',
        name: '잔류염소',
        color: '#7a91f5',
        marker: {
          enabled: false
        },
        data: this.RightChartData
      }]
      // console.log(this.RightChartData.length, this.RightChartData[1440])
      chart.series = series
      return chart
    },
    ChartData: function () {
      let rows = this.$store.state.sodocData.split('\r\n').slice(this.$store.state.sodocDataSelectedIndex - 1440, this.$store.state.sodocDataSelectedIndex + 1440)
      // console.log(new Date(rows[0].split(',')[0]), new Date(rows[2879].split(',')[0]))
      let leftChartColumnData1 = []
      let leftChartLineData1 = []
      let leftChartColumnData2 = []
      let leftChartLineData2 = []
      let rightChartData1 = []
      let rightChartData2 = []
      for (let i = 0; i < rows.length; i++) {
        let tmp = rows[i].split(',')
        leftChartColumnData1.push([new Date(tmp[0]), parseFloat(tmp[38])]) // 증발량
        leftChartLineData1.push([new Date(tmp[0]), parseFloat(tmp[8])]) // 주입률
        leftChartColumnData2.push([new Date(tmp[0]), parseFloat(tmp[39])])
        leftChartLineData2.push([new Date(tmp[0]), parseFloat(tmp[10])]) // 주입기#3번 값임
        if ( i > 1440 ) {
          rightChartData1.push([new Date(tmp[0]), null])
          rightChartData2.push([new Date(tmp[0]), null])
        } else {
          rightChartData1.push([new Date(tmp[0]), parseFloat(tmp[15])])
          rightChartData2.push([new Date(tmp[0]), parseFloat(tmp[16])])
        }
      }
      return [leftChartColumnData1, leftChartLineData1, leftChartColumnData2, leftChartLineData2, rightChartData1, rightChartData2]
    },
    LeftChartColumnData: function() {
      if (this.leftChartSelectedIndex === 1) {
        return this.ChartData[0]
      } else {
        return this.ChartData[2]
      }
    },
    LeftChartLineData: function () {
      if (this.leftChartSelectedIndex === 1) {
        return this.ChartData[1]
      } else {
        return this.ChartData[3]
      }
    },
    RightChartData: function () {
      if (this.rightChartSelectedIndex === 1) {
        return this.ChartData[4]
      } else {
        return this.ChartData[5]
      }
      // let rows = this.$store.state.sodocData.split('\r\n').slice(this.$store.state.sodocDataSelectedIndex - 1440, this.$store.state.sodocDataSelectedIndex + 1440)
      // let rightChartData1 = []
      // for (let i = 0; i < rows.length; i++) {
      //   let tmp = rows[i].split(',')
      //   if ( i > 1440 ) {
      //     rightChartData1.push([new Date(tmp[0]), null])
      //   } else {
      //     rightChartData1.push([new Date(tmp[0]), parseFloat(tmp[15])])
      //   }
      // }
      // return rightChartData1
    }
  },
  mounted: function() {
    // let rows = this.$store.state.sodocData.split('\r\n')
    // for(let i = 0; i < rows.length; i++) {
    //   if (i > 2880) {
    //     break
    //   }
    //   let tmp = rows[i].split(',')
    //   this.leftChartColumnData1.push([new Date(tmp[0]), parseFloat(tmp[38])])
    //   this.leftChartLineData1.push([new Date(tmp[0]), parseFloat(tmp[8])])
    //   this.leftChartColumnData2.push([new Date(tmp[0]), parseFloat(tmp[39])])
    //   this.leftChartLineData2.push([new Date(tmp[0]), parseFloat(tmp[10])]) // 주입기#3번 값임
    //   if ( i > 1440 ) {
    //     this.rightChartData1.push([new Date(tmp[0]), null])
    //     this.rightChartData2.push([new Date(tmp[0]), null])
    //   } else {
    //     this.rightChartData1.push([new Date(tmp[0]), parseFloat(tmp[15])])
    //     this.rightChartData2.push([new Date(tmp[0]), parseFloat(tmp[16])])
    //   }
    // }
  }
}
</script>

<style scoped lang="scss">
.gragh-main{
  display: flex;
  width: 100%;
  height: 352px;
  .gragh-contents{
    position: relative;
    width: 50%;
    .left-timer {
      position: absolute;
      font-family: "LAB디지털" !important;
      font-size: 20px;
      top:5px;
      right:150px;
      color: #fff;
    }
    .btn-tab{
      display: flex;
      position: absolute;
      top:5px;
      right:20px;
      // width: 90px;
      // height: 120px;
      font-family: 'KHNPHUotfR';
      font-size: 14px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      // line-height: 2.3;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      &__box-abled{
        // width: 90px;
        // height: 34px;
        // margin: 0 0 5px;
        padding: 5px 10px;
        box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
        border: solid 1px #b4dffa;
        background-color: rgba(139, 194, 240, 0.4);
        cursor: pointer;
      }
      &__box-disabled{
        // width: 90px;
        // height: 34px;
        // margin-bottom: 5px;
        padding: 5px 10px;
        border: solid 1px #b4dffa;
        background-color: rgba(139, 194, 240, 0.25);
        cursor: pointer;
      }
    }
    &__title{
      margin-top: 10px;
      text-shadow: 0 0 9px #5cafff;
      font-family: "KHNPHUotfR";
      font-size: 18px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.11;
      letter-spacing: normal;
      text-align: left;
      color: #fff;
      height: 47px;
      background-image: url('../../../assets/disinfection/gragh_title.png');
    }
    &__content{
      width: 100%;
      height: 250px;
      // background-color: #000; // 그래프 넣으면 지워야함
      // border: 1px solid #fff; // 그래프 넣으면 지워야함
      font-family: KHNPHUotfR;
      font-size: 14px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      // line-height: 1.07;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
    }
  }
}
</style>