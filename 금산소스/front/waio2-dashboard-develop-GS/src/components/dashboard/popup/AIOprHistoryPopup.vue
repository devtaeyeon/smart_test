<template>
  <div class="popup" v-if="this.$store.state.aioprhistory.aiOprHistoryPopupVisible">
    <div class="popup__title">
      <div class="popup__icon"></div>
      <span>AI운영이력</span>
    </div>
    <div class="btn" @click="closePopup()"></div>
    <div class="popup__body">
      <div class="popup__contents">
        <div class="popup__icon-value">
          <img src="../../../assets/gs_images/icon_coagulants.png" alt="약품아이콘">
          <div class="contents-title">약품</div>
        </div>
        <highcharts ref="coagulantChart" class="chart" :options="coagulantChart"/>
        <div class="ai-contents">
          <ul class="ai-contents__wrap">
            <li class="ai-contents__list">
              <div class="mode mode-one">AI</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.C_NONE_2.days}} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.C_NONE_2.hours}} 시간</div>
            </li>
            <li class="ai-contents__list">
              <div class="mode mode-two">AI추천</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.C_NONE_1.days}} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.C_NONE_1.hours}} 시간</div>
            </li>
            <li class="ai-contents__list">
              <div class="mode mode-three">AI분석</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.C_NONE_0.days}} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.C_NONE_0.hours}} 시간</div>
            </li>
          </ul>
        </div>
      </div>
      <div class="popup__contents">
        <div class="popup__icon-value">
          <img src="../../../assets/gs_images/icon_mixingtank.png" alt="혼화응집아이콘">
          <div class="contents-title">혼화응집</div>
        </div>
        <highcharts class="chart" :options="mixingChart"/>
        <div class="ai-contents">
          <ul class="ai-contents__wrap">
            <li class="ai-contents__list">
              <div class="mode mode-one">AI</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.D_NONE_2.days}} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.D_NONE_2.hours}} 시간</div>
            </li>
            <li class="ai-contents__list">
              <div class="mode mode-two">AI추천</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.D_NONE_1.days}} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.D_NONE_1.hours}} 시간</div>
            </li>
            <li class="ai-contents__list">
              <div class="mode mode-three">AI분석</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.D_NONE_0.days}} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.D_NONE_0.hours}} 시간</div>
            </li>
          </ul>
        </div>
      </div>
      <div class="popup__contents">
        <div class="popup__icon-value">
          <img src="../../../assets/gs_images/icon_disinfection.png" alt="소독아이콘">
          <div class="contents-title">전차염</div>
        </div>
        <highcharts class="chart" :options="disinfectionPreChart"/>
        <div class="ai-contents">
          <ul class="ai-contents__wrap">
            <li class="ai-contents__list">
              <div class="mode mode-one">AI</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.G_PRE_2.days }} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.G_PRE_2.hours }} 시간</div>
            </li>
            <li class="ai-contents__list">
              <div class="mode mode-two">AI추천</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.G_PRE_1.days }} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.G_PRE_1.hours }} 시간</div>
            </li>
            <li class="ai-contents__list">
              <div class="mode mode-three">AI분석</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.G_PRE_0.days }} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.G_PRE_0.hours }} 시간</div>
            </li>
          </ul>
        </div>
      </div>
      <div class="popup__contents">
        <div class="popup__icon-value">
          <img src="../../../assets/gs_images/icon_disinfection.png" alt="소독아이콘">
          <div class="contents-title">후차염</div>
        </div>
        <highcharts class="chart" :options="disinfectionPostChart"/>
        <div class="ai-contents">
          <ul class="ai-contents__wrap">
            <li class="ai-contents__list">
              <div class="mode mode-one">AI</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.G_POST_2.days }} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.G_POST_2.hours }} 시간</div>
            </li>
            <li class="ai-contents__list">
              <div class="mode mode-two">AI추천</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.G_POST_1.days }} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.G_POST_1.hours }} 시간</div>
            </li>
            <li class="ai-contents__list">
              <div class="mode mode-three">AI분석</div>
              <div class="day">{{ $store.state.aioprhistory.aiOprTotal.G_POST_0.days }} 일</div>
              <div class="hour">{{ $store.state.aioprhistory.aiOprTotal.G_POST_0.days }} 시간</div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'AIOprHistoryPopup',
  data: () => ({
  }),
  computed: {
    coagulantChart: function () {
      let chart = this.makeChart();
      chart.series = []
      let _series = {colorByPoint:true, innerSize : '79%', data: []}
      _series.data.push({name : 'AI', color: '#ec6c6b', y: this.$store.state.aioprhistory.aiOprTotal.C_NONE_2.totalHours })
      _series.data.push({name : '부분AI', color: '#ff9c39', y: this.$store.state.aioprhistory.aiOprTotal.C_NONE_1.totalHours })
      _series.data.push({name : 'AI추천', color: '#269bbe', y: this.$store.state.aioprhistory.aiOprTotal.C_NONE_0.totalHours })
      chart.series.push(_series)
      
      chart.title.text = this.$store.state.aioprhistory.aiOprTotal.C_NONE_SUM.days + "일" +"<br/>"+ this.$store.state.aioprhistory.aiOprTotal.C_NONE_SUM.hours +"시간"
      return chart
    },
    mixingChart: function () {
      let chart = this.makeChart();
      chart.series = []
      let _series = {colorByPoint:true, innerSize : '79%', data: []}
      _series.data.push({name : 'AI', color: '#ec6c6b', y: this.$store.state.aioprhistory.aiOprTotal.D_NONE_2.totalHours })
      _series.data.push({name : '부분AI', color: '#ff9c39', y: this.$store.state.aioprhistory.aiOprTotal.D_NONE_1.totalHours })
      _series.data.push({name : 'AI추천', color: '#269bbe', y: this.$store.state.aioprhistory.aiOprTotal.D_NONE_0.totalHours })
      chart.series.push(_series)

      chart.title.text = this.$store.state.aioprhistory.aiOprTotal.D_NONE_SUM.days + "일" +"<br/>"+ this.$store.state.aioprhistory.aiOprTotal.D_NONE_SUM.hours +"시간"

      return chart
    },
    disinfectionPreChart: function () {
      let chart = this.makeChart();
      chart.series = []
      let _series = {colorByPoint:true, innerSize : '79%', data: []}
      _series.data.push({name : 'AI', color: '#ec6c6b', y: this.$store.state.aioprhistory.aiOprTotal.G_PRE_2.totalHours })
      _series.data.push({name : '부분AI', color: '#ff9c39', y: this.$store.state.aioprhistory.aiOprTotal.G_PRE_1.totalHours })
      _series.data.push({name : 'AI추천', color: '#269bbe', y: this.$store.state.aioprhistory.aiOprTotal.G_PRE_0.totalHours })
      chart.series.push(_series)

      chart.title.text = this.$store.state.aioprhistory.aiOprTotal.G_PRE_SUM.days + "일" +"<br/>"+ this.$store.state.aioprhistory.aiOprTotal.G_PRE_SUM.hours +"시간"

      return chart
    },
    disinfectionPostChart: function () {
      let chart = this.makeChart();
      chart.series = []
      let _series = {colorByPoint:true, innerSize : '79%', data: []}
      _series.data.push({name : 'AI', color: '#ec6c6b', y: this.$store.state.aioprhistory.aiOprTotal.G_POST_2.totalHours })
      _series.data.push({name : '부분AI', color: '#ff9c39', y: this.$store.state.aioprhistory.aiOprTotal.G_POST_1.totalHours })
      _series.data.push({name : 'AI추천', color: '#269bbe', y: this.$store.state.aioprhistory.aiOprTotal.G_POST_0.totalHours })
      chart.series.push(_series)

      chart.title.text = this.$store.state.aioprhistory.aiOprTotal.G_POST_SUM.days + "일" +"<br/>"+ this.$store.state.aioprhistory.aiOprTotal.G_POST_SUM.hours +"시간"

      return chart
    },
  },
  methods: {
    closePopup: function () {
      this.$store.state.aioprhistory.aiOprHistoryPopupVisible = false;
    },
    makeChart: function(){
      const chartOption = {
        chart: {
          type: 'pie',
          backgroundColor:false,
          height: '100px',
        },
        legend: {
          enabled: false
        },
        tooltip:{
          enabled: false
        },
        credits:{
          enabled: false
        },
        exporting: {
          enabled: false,
          buttons: {
            contextButton: {
                enabled: false 
            }
          }
        },
        title:{
          text: '',
          align: 'center',
          verticalAlign: 'middle',
          y: 10,
          style: {
            color: '#FFFFFF',
            textAlign: 'center',
            fontSize: '13px', 
          }
        },
        plotOptions: {
          series:{
            allowPointSelect: false,
          },
          pie: {
            size: '200%',
            dataLabels: {
              enabled: false
            },
            borderColor: null,
          },
        },
      }
      return chartOption;
    }
  },
  mounted: function() {
  },
  destroyed: function() {
  },
  watch: {
  }
}
</script>
<style lang="scss" scoped>
.popup{
  position: fixed;
  bottom: 11px;
  right: 421px;
  width: 497px;
  height: 664px;
  z-index: 9999;
  background-size: 100% 100%;
  background-image: url('../../../assets/gs_images/ai_popup.png');
  &__title {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 20px 15px;
    font-size: 22px;
    color: #b4dffb;
  }
  &__icon {
    width: 17px;
    height: 28px;
    margin-right: 15px;
    background-image: url('../../../assets/dialog/ai_header_icon.png');
    background-size: 100% 100%;
  }
  &__body {
    // padding: 20px;
  }
  &__contents {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 25px 0;
  }
  &__icon-value {
    width: 70px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .total {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    width: 90px;
    height: 90px;
    border-radius: 50%;
    margin-left: 20px;
    color: #fff;
    font-size: 14px;
    &__wrap {
      padding-top: 3px;
    }
    span {
      font-size: 12px;
    }
  }
  //
  .chart {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    width: 90px;
    height: 90px;
    border-radius: 50%;
    margin: 0 20px;
    &__wrap {
      padding-top: 3px;
    }
  }
  // ai
  .total-one {
    border: 8px solid #ec6c6b;
  }
  // ai추천
  .total-two {
    border: 8px solid #ff9c39;
  }
  // ai분석
  .total-three {
    border: 8px solid #269bbe;
  }
  .total-day {

  }
  .ai-contents {
    display: flex;
    &__wrap {
      padding: 0;
    }
    &__list {
      display: flex;
      list-style: none;
      font-size: 14px;
      color: #fff;
      margin: 8px 0;
    }
    .mode {
      width: 50px;
      font-weight: 600;
      text-align: right;
    }
    // ai
    .mode-one {
      color: #ec6c6b;
    }
    // ai추천
    .mode-two {
      color: #ff9c39;
    }
    // ai분석
    .mode-three {
      color: #269bbe;
    }
    .day {
      width: 80px;
      text-align: right;
    }
    .hour {
      width: 50px;
      margin-left: 20px;
      text-align: right;
    }
  }
  .contents-title {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 32px;
    margin-top: 7px;
    font-size: 14px;
    color: #fff;
    background-size: 100% 100%;
    background-image: url('../../../assets/gs_images/icon_title.png');
  }
}
.btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 24px;
  height: 30px;
  background-image: url('../../../assets/sedimentation/exit_btn.png');
  background-position-y: center;
  cursor: pointer;
  z-index: 9;
}
</style>