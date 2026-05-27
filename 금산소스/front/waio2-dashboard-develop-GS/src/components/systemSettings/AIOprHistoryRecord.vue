<template>
  <!-- 제목 -->
  <div class="ai-record">
    <p class="record-text-one">AI운영이력(상세)</p>
    <div class="start_end_date">
      <div class="line-box">
        <div class="line-box__title">기간</div>
        <v-select outlined
        :menu-props="{
          offsetY: true,
          nudgeBottom: 0
        }"
        :items="this.periodList"
        v-model="selectedPeriod"
        @change="setDate"
        >
        </v-select>
      </div>
      <!-- 시작 날짜 -->
      <div class="line-box__title">시작일</div>
      <v-menu
        ref="menuStart"
        v-model="menuStart"
        :close-on-content-click="false"
        transition="scale-transition"
        offset-y>
        <template v-slot:activator="{ on }">
          <input id="startTime" class="selected-date" type="text" v-model="$store.state.aioprhistory.formattedStartTime" v-on="on" readonly>
          <label for="startTime"></label>
        </template>
        <v-date-picker v-model="$store.state.aioprhistory.formattedStartTime" no-title scrollable class="custom" :max="maxDate" @input="inputByCalendar"></v-date-picker>
      </v-menu>
      <div class="right-arrow-img"></div>
      <!-- 종료 날짜 -->
      <div class="line-box__title">종료일</div>
      <v-menu
        ref="menuEnd"
        v-model="menuEnd"
        :close-on-content-click="false"
        transition="scale-transition"
        offset-y>
        <template v-slot:activator="{ on }">
          <input id="endTime" class="selected-date" type="text" v-model="$store.state.aioprhistory.formattedEndTime" v-on="on" readonly>
          <label for="endTime"></label>
        </template>
        <v-date-picker v-model="$store.state.aioprhistory.formattedEndTime" :min="$store.state.aioprhistory.formattedStartTime" :max="maxDate" no-title scrollable class="custom" @input="inputByCalendar"></v-date-picker>
      </v-menu>
      <!-- 검색 버튼 -->
      <button class="search-btn" @click="onClickSearchBtn">
        <!-- <div class="search-icon-img"></div> -->
        <div class="search-btn-text">조회</div>
      </button>      
    </div>
    <!-- 조회기간 -->
    <div class="ai-period">
      <div>조회기간 : </div>
      <div>{{ this.searchStartDate }}</div>
      <div style="padding: 0 10px;">~</div>
      <div>{{ this.searchEndDate }}</div>
    </div>
    <!-- 상세 테이블 영역 -->
    <div class="ai-table">
      <table class="search-table">
      <colgroup>
        <col style="width: 10%;">
        <col style="width: 20%;">
        <col style="width: 20%;">
        <col style="width: 20%;">
        <col style="width: 20%;">
      </colgroup>
      <thead class="table-title">
        <th class="row-text">구분</th>
        <th class="row-text">약품</th>
        <th class="row-text">혼화응집</th>
        <th class="row-text">전차염</th>
        <th class="row-text">후차염</th>
      </thead>
      <tbody class="search-contents-container">
        <tr class="table-contents">
          <td class="none">전체</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_SUM.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_SUM.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_SUM.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_SUM.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_SUM.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_SUM.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_SUM.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_SUM.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_SUM.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_SUM.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_SUM.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_SUM.totalHours }})</td>
        </tr>
        <tr class="table-contents">
          <td class="none">AI</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_2.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_2.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_2.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_2.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_2.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_2.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_2.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_2.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_2.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_2.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_2.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_2.totalHours }})</td>
        </tr>
        <tr class="table-contents">
          <td class="none">AI추천</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_1.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_1.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_1.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_1.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_1.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_1.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_1.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_1.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_1.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_1.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_1.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_1.totalHours }})</td>
        </tr>
        <tr class="table-contents">
          <td class="none">AI분석</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_0.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_0.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_0.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_0.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_0.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.D_NONE_0.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_0.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_0.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.G_PRE_0.totalHours }})</td>
          <td>{{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_0.days }}일 {{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_0.hours }}시간 ({{ this.$store.state.aioprhistory.aiOprHistorySearch.G_POST_0.totalHours }})</td>
        </tr>
      </tbody>
      </table>
    </div>
    <div class="record-text-two">자율운영<p>AI운영 가동시간(조회기간내)</p></div>
    <!-- 자율운영 차트 영역 -->
    <div v-if="this.$store.state.aioprhistory.aiOprHistorySearch.C_NONE_SUM.totalHours == 0">
      <p class="ai-chart-text">조회된 데이터가 없습니다.</p>
    </div>
    <div v-else class="ai-chart">
      <highcharts :options="aiHistoryChart"/>
    </div>
  </div>
</template>
<script>
import moment from 'moment'
import { SET_OVERLAY } from '@/store'
import { PUT_AI_OPR_HISTORY_SEARCH } from '@/store/modules/aioprhistory'

export default {
  name: 'AIOprHistoryRecord',
  data: () => ({
    menuStart: false, // 검색 시작 날짜
    menuEnd: false, // 검색 종료 날짜
    startTime: '00:00:00',
    endTime: '23:59:59',
    searchStartDate: '',
    searchEndDate: '',
    selectedPeriod: '7일',
    periodList: ['--', '7일', '30일', '1년'],
    chartOptions:{
      chart: {
        type: 'column',
        backgroundColor:false,
      },
      title: null,
      xAxis: {
        categories: ['약품', '혼화응집', '전차염', '후차염'],
        gridLineWidth: 0,
        labels: {
          style: {
            color: '#FFFFFF'
          }
        },
        plotLines: [
          {
              color: '#C0C0C0',
              width: 1, 
              value: 0.5
          },
          {
              color: '#C0C0C0',
              width: 1,
              value: 1.5
          },
          {
              color: '#C0C0C0',
              width: 1,
              value: 2.5
          },
          {
              color: '#C0C0C0',
              width: 1,
              value: 3.5
          }
        ]
      },
      yAxis: {
        min: 0,
        labels: {
          style: {
            color: '#FFFFFF'
          }
        },
        gridLineWidth: 0,
        title: null
      },
      credits:{
        enabled: false
      },
      legend: {
        layout: 'horizontal',
        align: 'right',
        verticalAlign: 'top',
        borderWidth: 1,
        borderColor:null,
        itemStyle:{
          color:'#FFFFFF'
        }
      },
      exporting: {
        enabled: false,
        buttons: {
          contextButton: {
            enabled: false 
          }
        }
      },
      plotOptions: {
        column: {
            pointPadding: 0.2,
            borderWidth: 0,
            dataLabels:{
              enabled: true,
              format: '{y}',
              style: {
                fontSize: '10px',
                color:'#FFFFFF'
              }
            }
        }
      },
      series:[]
    }
  }),
  computed: {
    aiHistoryChart: function(){
      let chart = this.chartOptions
      chart.series= []
      const { aiOprHistorySearch } = this.$store.state.aioprhistory
      const aiData = [];
      const aiSuggestData= [];
      const aiAnalysisData = [];

      for(const key in aiOprHistorySearch){
        const item = aiOprHistorySearch[key];
        if (key.endsWith('_2')) {
          aiData.push(item.totalHours)
        } else if (key.endsWith('_1')) {
          aiSuggestData.push(item.totalHours)
        } else if (key.endsWith('_0')) {
          aiAnalysisData.push(item.totalHours)
        }
      }

      let aiSeries = {name : 'AI', color: '#ec6c6b', data: aiData}
      let aiSuggestSeries = {name : 'AI추천', color: '#ff9c39', data: aiSuggestData}
      let aiAnalysisSeries = {name : 'AI분석', color: '#269bbe', data: aiAnalysisData}
      chart.series.push(aiSeries, aiSuggestSeries, aiAnalysisSeries)

      return chart
    },
    maxDate: function(){
      let now = moment()
      let yesterDay = now.clone().subtract(1, 'd')
      return yesterDay.format('YYYY-MM-DD')
    }
  },
  mounted: function () {
    let now = moment()
    this.$store.state.aioprhistory.endTime = now.clone().subtract(1, 'd')
    this.$store.state.aioprhistory.startTime = now.clone().subtract(7, 'd')
    this.$store.state.aioprhistory.formattedStartTime = this.$store.state.aioprhistory.startTime.format('YYYY-MM-DD')
    this.$store.state.aioprhistory.formattedEndTime = this.$store.state.aioprhistory.endTime.format('YYYY-MM-DD')

    this.$store.commit(SET_OVERLAY, true)
    Promise.all([
    this.$store.dispatch(PUT_AI_OPR_HISTORY_SEARCH , { start_time: this.$store.state.aioprhistory.startTime.valueOf(), end_time: this.$store.state.aioprhistory.endTime.valueOf()})
    ]).finally(() => {
      this.searchStartDate = this.$store.state.aioprhistory.formattedStartTime
      this.searchEndDate = this.$store.state.aioprhistory.formattedEndTime
      this.$store.commit(SET_OVERLAY, false)
    })
  },
  methods: {
    onClickSearchBtn () {
      let _startTime = moment(this.$store.state.aioprhistory.formattedStartTime)
      let _endTime = moment(this.$store.state.aioprhistory.formattedEndTime)
      this.$store.commit(SET_OVERLAY, true)
      // TODO
      Promise.all([
      this.$store.dispatch(PUT_AI_OPR_HISTORY_SEARCH , { start_time: _startTime.valueOf(), end_time: _endTime.valueOf()})
      ]).finally(() => {
        this.searchStartDate = this.$store.state.aioprhistory.formattedStartTime
        this.searchEndDate = this.$store.state.aioprhistory.formattedEndTime
        this.$store.commit(SET_OVERLAY, false)
      })
    },
    setDate(selectedValue){
      if(selectedValue == '--'){
        return;
      }
      let now = moment()
      if(selectedValue == '7일'){
        this.$store.state.aioprhistory.endTime = now.clone().subtract(1, 'd')
        this.$store.state.aioprhistory.startTime = now.clone().subtract(7, 'd')
      }else if(selectedValue == '30일'){
        this.$store.state.aioprhistory.endTime = now.clone().subtract(1, 'd')
        this.$store.state.aioprhistory.startTime = now.clone().subtract(30, 'd')
      }else if(selectedValue == '1년'){
        this.$store.state.aioprhistory.endTime = now.clone().subtract(1, 'd')
        this.$store.state.aioprhistory.startTime = now.clone().subtract(1, 'y')
      }
      this.$store.state.aioprhistory.formattedStartTime = this.$store.state.aioprhistory.startTime.format('YYYY-MM-DD')
      this.$store.state.aioprhistory.formattedEndTime = this.$store.state.aioprhistory.endTime.format('YYYY-MM-DD')
    },
    inputByCalendar(){
      this.selectedPeriod = '--'
    }
  },
  destroyed: function () {
    console.log(this.$options.name + ' destoryed')
  },
  updated: function () {
    console.log(this.$options.name + ' updated')
  },
  watch: {

  }
}
</script>
<style lang="scss" scoped>
.v-input {
  max-width: 155px;
  height: 35px !important;
  color: #417db9 !important;
  border-radius: 0;
  border: none;
  padding-right: 20px;
}
.ai-record {
  padding: 20px 40px;
  width: 100%;
  height: 100%;
  .record-text-one {
    width: 197px;
    height: 47px;
    background-image: url('../../assets/editableDashboard/title_under.png');
    text-shadow: 0 0 9px #5cafff;
    font-size: 18px;
    text-align: center;
    color: #fff;
    margin-bottom: 0;
  }
  .record-text-two {
    width: 197px;
    height: 67px;
    background-position-y: 20px;
    background-image: url('../../assets/editableDashboard/title_under.png');
    text-shadow: 0 0 9px #5cafff;
    font-size: 18px;
    text-align: center;
    color: #fff;
    margin-bottom: 0;
    > p { 
      font-size: 14px;
    }
  }
  .ai-period {
    display: flex;
    justify-content: center;
    align-items: center;
    background-image: url('../../assets/gs_images/ai_contents_right_title.png');
    background-position: 50% 50%;
    width: 100%;
    height: 52px;
    margin-bottom: 10px;
    > div {
      color: #fff;
      padding: 0 5px;
    }
  }
  // 시작날짜, 종료날짜 검색
  .start_end_date {  
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 40px;
    .line-box {
      display: flex;
      justify-content: space-evenly;
      align-items: center;
      &__title {
        text-shadow: 0 0 9px #5cafff;
        font-size: 16px;
        text-align: left;
        color: #fff;
        padding-right: 20px;
      }
    }
    // 날짜 Input
    .selected-date {
      font-size: 14px;
      font-weight: normal;
      text-shadow: 0 0 9px #5cafff;
      text-align: left;
      color: #ffffff;
      width: 189px;
      height: 34px;
      border-bottom: solid 1px #ffffff;
      -webkit-appearance: none;
      -moz-appearance: none;
      background: transparent;
      background-image: url('~@/assets/alarmHistory/under_arrow.png');
      background-repeat: no-repeat;
      background-position-x: 170px;
      padding: 0 5px;
      background-position-y: 12px;
    }
    // 검색 버튼
    .search-btn {
      display: flex;
      margin-left: 15px;
      margin-right: 5px;
      width: 80px;
      height: 34px;
      background-color: #3bb7e5;
      justify-content: center;
      align-items: center;
      border: solid 1px #b4dffa;
      background-color: rgba(139, 194, 240, 0.25);
    }
    // 검색 버튼 아이콘
    .search-icon-img {
      background-image: url('~@/assets/alarmHistory/search_icon.png');
      width: 13.8px;
      height: 15.4px;
      margin-right: 7.3px;
    }
    // 검색 버튼 텍스트
    .search-btn-text {
      font-size: 14px;
      text-shadow: 0 0 9px #5cafff;
      color: #fff;
    }
    // arrow
    .right-arrow-img {
      background-image: url('~@/assets/alarmHistory/right_arrow.png');
      background-repeat: no-repeat;
      width: 23.2px;
      height: 9.1px;
      margin: 0 30px;
    }
  }
  // 알람 이력 테이블
  .ai-table {
    margin-bottom: 30px;
    // 검색 결과 테이블
    .search-table {
      width: 100%;
      border-collapse: collapse;
      // thead tr
      .table-title {
        width: 100%;
        height: 55px;
        font-size: 15px;
        text-align: center;
        color: rgba(255, 255, 255, 0.85);
        background-image: linear-gradient(to right, rgba(16, 36, 65, 0) 0%, #2551a3 25%,#2551a3 75%, rgba(16, 36, 65, 0) 100%);
        // thread th
        .row-text {
        }
      }
      // tbody
      .search-contents-container {
        // tbody tr
        .table-contents {
        height: 55px;
        font-size: 14px;
        color: #fff;
        text-align: center;
        border-bottom: 1px solid #3a547c;
        }
        .table-contents > td {
          border-left: 1px solid #3a547c;
        }
        .table-contents > td.none {
          border:none;
        }
      }
    }
  }
  .ai-chart {
    width: 100%;
    height: 100%;
  }
  .ai-chart-text {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items:center;
    color: white;
    transform: translateY(60px);
  }
}
</style>