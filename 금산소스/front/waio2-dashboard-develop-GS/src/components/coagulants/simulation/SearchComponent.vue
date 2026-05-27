<template>
  <div class="main-container">
    <div class="main-container__header">
      <div class="header-container">
        <div class="header-container__title">
          약품 모의 이력 조회
        </div>
      </div>
    </div>
    <div class="main-container__body">
      <div class="record-menu">
        <div class="record-menu__input-box">
          <v-menu
            ref="menuStart"
            v-model="menuStart"
            :close-on-content-click="true"
            transition="scale-transition"
            offset-y>
            <template v-slot:activator="{ on }">
              <input class="record-menu__input" type="text" v-model="$store.state.coagulants.simulation.formattedStartTime" v-on="on" readonly>
            </template>
            <v-date-picker v-model="$store.state.coagulants.simulation.formattedStartTime" no-title scrollable class="custom">
            </v-date-picker>
          </v-menu>
        </div>
        <div class="record-menu__arrow"></div>
        <div class="record-menu__input-box">
          <v-menu
            ref="menuEnd"
            v-model="menuEnd"
            :close-on-content-click="true"
            transition="scale-transition"
            offset-y>
            <template v-slot:activator="{ on }">
              <input class="record-menu__input" type="text" v-model="$store.state.coagulants.simulation.formattedEndTime" v-on="on" readonly>
            </template>
            <v-date-picker v-model="$store.state.coagulants.simulation.formattedEndTime" :min="$store.state.coagulants.simulation.formattedStartTime" no-title scrollable class="custom">
            </v-date-picker>
          </v-menu>
        </div>
        <div class="search" @click="onClickSearchBtn">
          <button class="btn-text"><img class="img-search" src="../../../assets/drugInjection/그룹 2874.png" alt="검색">검색</button>
        </div>
        <div class="reset" @click="onClickSearchBtnReset">
          <button class="btn-text"><img class="img-search" src="../../../assets/drugInjection/그룹 2873.png" alt="초기화">초기화</button>
        </div>
      </div>
      <div class="record-table">
        <div class="record-table__line1"></div>
        <div class="record-table__thead">
            <div class="record-table__th">모의<br/>시간</div>
            <div class="record-table__th">원수<br/>탁도</div>
            <div class="record-table__th">원수<br/>pH</div>
            <div class="record-table__th">원수<br/>수온</div>
            <div class="record-table__th">원수<br/>전기전도도</div>
            <div class="record-table__th">원수<br/>유량</div>
            <div class="record-table__th">침전지<br/>탁도 #1</div>
            <div class="record-table__th">침전지<br/>탁도 #2</div>
            <div class="record-table__th">
              <div>
                <div>1계열 권장응집제</div>
                <div>2계열 권장응집제</div>
              </div>
              <div>
                <div>
                  <div>종류</div>
                  <div>주입률</div>
                </div>
                <div>
                  <div>종류</div>
                  <div>주입률</div>
                </div>
              </div>
            </div>
        </div>
        <div class="record-table__line2"></div>
        <div class="record-table__tbody">
          <template v-for="(item, index) in paginatedData">
            <div :key="index" class="record-table__tr" :class="[ index % 2 === 0 ? 'record-table__tr--odd' : 'record-table__tr--even']"  @click="clickSimulationList(item.simulation_index)">
              <div class="record-table__td">{{ item.reg_time | moment('YYYY-MM-DD HH:mm:ss') }}</div>
              <div class="record-table__td">{{ item.b_tb | numFormat('0.[00]') }}</div>
              <div class="record-table__td">{{ item.b_ph | numFormat('0.[00]') }}</div>
              <div class="record-table__td">{{ item.b_te | numFormat('0.[00]') }}</div>
              <div class="record-table__td">{{ item.b_cu | numFormat('0.[00]') }}</div>
              <div class="record-table__td">{{ item.b_in_fr | numFormat('0.[00]') }}</div>
              <div class="record-table__td">{{ item.e1_tb | numFormat('0.[00]') }}</div>
              <div class="record-table__td">{{ item.e2_tb | numFormat('0.[00]') }}</div>
              <div class="record-table__td">{{ item.ai_c1_cf_coagulant === null ? '--' : item.ai_c1_cf_coagulant.toUpperCase() }}</div>
              <div class="record-table__td">{{ (item.ai_c1_cf === null || item.ai_c1_cf === '') ? '--' : toDecimalPoint(item.ai_c1_cf) }}</div>
              <div class="record-table__td">{{ item.ai_c2_cf_coagulant === null ? '--' : item.ai_c2_cf_coagulant.toUpperCase() }}</div>
              <div class="record-table__td">{{ (item.ai_c2_cf === null || item.ai_c2_cf === '') ? '--' : toDecimalPoint(item.ai_c2_cf) }}</div>
            </div>
          </template>
          <!-- <div v-for="index in [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]" :key="index" class="record-table__tr" :class="[ index % 2 === 0 ? 'record-table__tr--odd' : 'record-table__tr--even']">
            <div class="record-table__td">1</div>
            <div class="record-table__td">1</div>
            <div class="record-table__td">1</div>
            <div class="record-table__td">1</div>
            <div class="record-table__td">1</div>
            <div class="record-table__td">1</div><div class="record-table__td">1</div>
            <div class="record-table__td">1</div>
            <div class="record-table__td">1</div><div class="record-table__td">1</div>
            <div class="record-table__td">1</div>
            <div class="record-table__td">1</div>
          </div> -->
        </div>
        <div class="record-table__line2"></div>
        <div class="pagination-container">
          <div class="pagination">
            <a v-if="showedPageList.length !== 0" href="javascript:void(0);" @click="prevPage"><img src="../../../assets/coagulants/simulation/simul_search_pagenation_left_arrow.png" alt="이전"></a>
            <a href="javascript:void(0);" @click="setPage(n-1)" v-for="n in showedPageList" :key="n" :class="{active:n == pageNum + 1}">{{ n }}</a>
            <a v-if="showedPageList.length !== 0" href="javascript:void(0);" @click="nextPage"><img src="../../../assets/coagulants/simulation/simul_search_pagenation_right_arrow.png" alt="다음"></a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import moment from 'moment'
import numeral from 'numeral'
import { PUT_COAGULANT_SIMULATION } from '@/store/modules/coagulants'
export default {
  name: 'SearchComponent',
  data: () => ({
    pageNum: 0, // 현재 페이지
    pageSize: 15, // 한 페이지에 표현할 갯수
    menuStart: false, // 검색 시작 날짜
    menuEnd: false, // 검색 종료 날짜
    search: null, // 검색어
    isSearch: false, // 검색중인지 아닌지 판단
    initStartTime: null,
    initEndTime: null,
    timer: null
  }),
  computed: {
    // 전체 페이지 갯수
    pageCount () {
      let listLeng = this.calcData.length
      let listSize = this.pageSize
      let page = Math.floor(listLeng / listSize)
      if (listLeng % listSize > 0) page += 1
      return page
    },
    // 현재 페이지에 나타낼 데이터
    paginatedData () {
      const start = this.pageNum * this.pageSize
      const end = start + this.pageSize
      return this.calcData.slice(start, end)
    },
    // 검색 여부에 따라 데이터 필터링 후 반환
    calcData () {
        return this.$store.state.coagulants.simulation.searchList
    },
    // 선택된 페이지 위치에 따라 표현할 페이지 리스트 반환
    showedPageList () {
      let totalCount = 10
      let leftCount = 5
      let rightCount = 4
      if (this.pageCount <= totalCount) {
        return this.range(1, this.pageCount)
      } else {
        if (this.pageNum <= leftCount) {
          return this.range(1, totalCount)
        } else {
          if (this.pageNum >= this.pageCount - leftCount) {
            return this.range(this.pageCount - (leftCount + rightCount), this.pageCount)
          } else {
            return this.range(this.pageNum - rightCount, this.pageNum + leftCount)
          }
        }
      }
    }
  },
  methods: {
    toDecimalPoint (val) {
      return numeral(val).format('0.[00]')
    },
    onClickSearchBtnReset () {
      this.$store.state.coagulants.simulation.formattedStartTime = moment(this.initStartTime).format('YYYY-MM-DD')
      this.$store.state.coagulants.simulation.formattedEndTime = moment(this.endTime).format('YYYY-MM-DD')
      this.$store.dispatch(PUT_COAGULANT_SIMULATION, { start_time: this.initStartTime.toISOString(), end_time: this.initEndTime.toISOString() })
    },
    onClickSearchBtn () {
      let _startTime = moment(this.$store.state.coagulants.simulation.formattedStartTime)
      let _endTime = moment(this.$store.state.coagulants.simulation.formattedEndTime)
      _startTime.set({ hour: 0, minute: 0, second: 0, milliseconds: 0 })
      _endTime.set({ hour: 23, minute: 59, second: 59, milliseconds: 999})
      console.log(_startTime.toISOString(), _endTime.toISOString())
      
      this.$store.dispatch(PUT_COAGULANT_SIMULATION, { start_time: _startTime, end_time: _endTime })
    },
    /**
     * 다음 페이지 버튼을 눌렀을 때
     * 현재 페이지 = 현재 페이지 + 1
     */
    nextPage () {
      if (this.pageNum === this.pageCount - 1) {
        this.pageNum = this.pageCount - 1
      } else {
        this.pageNum += 1
      }
    },
    /**
     * 이전 페이지 버튼을 눌렀을 때
     * 이전 페이지 = 이전 페이지 - 1
     */
    prevPage () {
      if (this.pageNum === 0) {
        this.pageNum = 0
      } else {
        this.pageNum -= 1
      }
    },
    /**
     * 특정 페이지를 눌렀을 때
     */
    setPage (v) {
      this.pageNum = v
    },
    /**
     * 숫자를 리스트로 반환하는 함수
     * 
     * @param start 시작 숫자
     * @param end 종료 숫자
     * @return 배열(시작 ~ 종료)
     */
    range: function (start, end) {
      let list = []
      for (let i = start; i <= end; i++) list.push(i)
      return list
    },
    clickSimulationList: function (index) {
      this.$store.state.coagulants.simulation.searchList.map((v) => {
        if (v.simulation_index === index) {
          this.$store.state.coagulants.simulation.b_tb = numeral(v.b_tb).format('0.[00]')
          this.$store.state.coagulants.simulation.b_ph = numeral(v.b_ph).format('0.[00]')
          this.$store.state.coagulants.simulation.b_te = numeral(v.b_te).format('0.[00]')
          this.$store.state.coagulants.simulation.b_cu = numeral(v.b_cu).format('0.[00]')
          this.$store.state.coagulants.simulation.b_in_fr = numeral(v.b_in_fr).format('0.[00]')
          this.$store.state.coagulants.simulation.e1_tb = numeral(v.e1_tb).format('0.[00]')
          this.$store.state.coagulants.simulation.e2_tb = numeral(v.e2_tb).format('0.[00]')
          this.$store.state.coagulants.simulation.ai_c1_cf = v.ai_c1_cf === null ? null : numeral(v.ai_c1_cf).format('0.[00]')
          this.$store.state.coagulants.simulation.ai_c1_cf_coagulant = v.ai_c1_cf_coagulant
          this.$store.state.coagulants.simulation.ai_c2_cf = v.ai_c2_cf === null ? null : numeral(v.ai_c2_cf).format('0.[00]')
          this.$store.state.coagulants.simulation.ai_c2_cf_coagulant = v.ai_c2_cf_coagulant
        }
      })
    }
  },
  /**
   * 마운트 됐을 때 실행되는 함수
   */
  mounted: function () {
    console.log(this.$options.name + ' mounted')
    let now = moment()
    now.set({ hour: 23, minute: 59, second: 59, milliseconds: 999})
    // now.add('days', 1)
    this.$store.state.coagulants.simulation.endTime = now
    this.$store.state.coagulants.simulation.startTime = now.clone().subtract(6, 'd')
    this.$store.state.coagulants.simulation.startTime.set({ hour: 0, minute: 0, second: 0, milliseconds: 0 })
    console.log(this.$store.state.coagulants.simulation.startTime.format('YYYY-MM-DD HH:mm:ss'), this.$store.state.coagulants.simulation.endTime.format('YYYY-MM-DD HH:mm:ss'))
    this.$store.state.coagulants.simulation.formattedStartTime = this.$store.state.coagulants.simulation.startTime.format('YYYY-MM-DD')
    this.$store.state.coagulants.simulation.formattedEndTime = this.$store.state.coagulants.simulation.endTime.format('YYYY-MM-DD')

    this.initStartTime = this.$store.state.coagulants.simulation.startTime
    this.initEndTime = this.$store.state.coagulants.simulation.endTime
    
    this.$store.dispatch(PUT_COAGULANT_SIMULATION, { start_time: this.initStartTime.toISOString(), end_time: this.initEndTime.toISOString() })
    this.timer = setInterval(() => {
      this.$store.dispatch(PUT_COAGULANT_SIMULATION, { start_time: this.initStartTime.toISOString(), end_time: this.initEndTime.toISOString() })
    }, 30 * 1000)
  },
  // 마운트 해제시 API 요청 타이머 해제
  destroyed: function () {
    clearInterval(this.timer)
  }
}
</script>

<style lang="scss" scoped>
.main-container {
  display: flex;
  flex-direction: column;
  &__header {
    // display: flex;
    // flex: 1;
  }
  &__body {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 1320px;
    height: 875px;
    background-image: url('../../../assets/coagulants/simulation/body_background_search.png');
    background-size: 100% 100%;
    background-repeat: no-repeat;
    background-position: center;
  }
  &__list {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between; 
  }
  &__footer {
    margin-left: auto;
    padding-right: 16px;
    padding-bottom: 20px;
  }
}
.header-container {
  display: flex;
  width: 501px;
  height: 51px;
  background-image: url('../../../assets/coagulants/simulation/header_container.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-position: center;
  &__title {
    display: flex;
    flex: 1;
    align-items: center;
    font-size: 18px;
    color: #fff;
    padding-left: 38px;
  }
  &__button-container {
    display: flex;
    margin-left: auto;
  }
  &__button-inner-container {
    display:flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 4px;
  }
}
.button-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  padding: 8px 12px;
  cursor: pointer;
  &--sky {
    background-color: #4486de;
  }
  &--skyblue {
    background-color: #0555b5;
  }
  &--gray {
    width: 127px;
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.4);
    box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  }
  &__title {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    font-size: 14px;
    color: #fff;
  }
}
.box-config {
  display: flex;
  width: 100%;
  align-items: center;
  padding-left: 45px;
  padding-right: 38px;
  // margin-top: 23px;
  &__text {
    flex: 1;
    &--title {
      font-size: 16px;
      color: #c3eaff;
    }
  }
  &__input-box {
    width: 165px;
    border: solid 1px #9dbfff;
  }
  &__unit {
    width: 32px;
    font-size: 12px;
    color: #417db9;
    margin-left: 17px;
  }
  input {
    width: 165px;
    font-family: "LAB디지털" !important;
    font-size: 24px;
    color: #ccf1ff;
    text-align: center;
  }
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
.record-menu {
  display: flex;
  width: 100%;
  height: 77px;
  padding: 19px 20px 23px 18px;
  &__input-box {
    width: 189px;
    background-image: url('../../../assets/coagulants/simulation/simul_search_select_box_arrow.png');
    background-repeat: no-repeat;
    background-position-y: center;;
    background-position-x: 165px;
    border: solid 1px rgba(157, 191, 255, 0.5);
    background-color: rgba(157, 191, 255, 0.07);
  }
  &__input {
    width: 188px;
    height: 100%;
    // padding-bottom: 10.1px;
    padding-left: 11px;
    // padding-right: 10px;
    margin-right: 29.1px;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    // line-height: 2.23;
    letter-spacing: normal;
    text-align: left;
    color: rgba(255, 255, 255, 0.75);
  }
  &__arrow {
    width: 47px;
    height: 34px;
    margin-left: 26.3px;
    margin-right: 26.3px;
    background-image: url('../../../assets/coagulants/simulation/simul_result_label_arrow.png');
    background-size: 47px 34px;
    background-position: center;
    background-repeat: no-repeat;
  }
  .search {
    width: 80px;
    height: 34px;
    margin: 0 0px 0.9px;
    margin-left: auto;
    /* padding: 9.8px 18.6px 8.8px 16.2px; */
    text-align: center;
    padding-top: 5px;
    background-color: #0555b5;
    cursor: pointer;
  }
  .reset {
    width: 84px;
    height: 34px;
    margin: 0 0 0.9px 9px;
    /* padding: 10.5px 12.6px 9px 12.6px; */
    text-align: center;
    padding-top: 5px;
    // border: solid 1px #ffffff;
    background-color: #1a2286;
    cursor: pointer;
  }
  .btn-text {
    font-size: 13px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    // line-height: 1.66;
    letter-spacing: normal;
    text-align: left;
    color: #ffffff;
  }
  .img-search {
    vertical-align: text-top;
    margin-right :6px;
  }
}



.record-table {
  display: flex;
  flex-direction: column;
  width: 1285px;
  flex: 1;
  // background-color: rgba(34, 80, 155, 0.69);
  &__thead {
    width: 100%;
    height: 60px;
    display:flex;
    background-image: linear-gradient(to right, rgba(16, 36, 65, 0) 0%, #2756a2 14%, rgba(18, 43, 92, 0.96) 49%, rgba(39, 86, 162, 0.93) 85%, rgba(16, 36, 65, 0) 100%);
  }
  &__line1 {
    width: 100%;
    height: 1px;
    background-image: linear-gradient(to left, rgba(160, 219, 255, 0), #7cadfc 88%, rgba(129, 184, 224, 0));
  }
  &__line2 {
    width: 100%;
    height: 1px;
    background-image: linear-gradient(to right, rgba(160, 219, 255, 0), #7cadfc 12%, rgba(129, 184, 224, 0));
  }
  // &__tbody {

  // }
  &__th {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    font-size: 14px;
    color: #c3eaff;
    &:nth-child(1) {
      width: 14%;
    }
    &:nth-child(n+2):nth-child(-n+8) {
      width: 8%;
    }
    &:nth-child(9) {
      display: flex;
      flex-direction: column;
      width: 30%;
      &>div:nth-child(1) {
        width: 100%;
        height: 35px;
        display: flex;
        align-items: center;
        justify-content: center;
        > div {
          width: 50%;
        }
      }
      &>div:nth-child(2) {
        display: flex;
        width: 100%;
        height: 25px;
        background-image: linear-gradient(to right, rgba(65, 125, 185, 0) 0%, #417db9 12%, #417db9 32%, #2c68a5 51%, #417db9 69%, #417db9 88%, rgba(16, 36, 65, 0) 100%);
        > div {
          display: flex;
          width: 50%;
          > div {
            width: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
      }
    }
  }
  &__tr {
    display: flex;
    width: 100%;
    height: 44px;
    cursor: pointer;
    &--odd {
      background-image: linear-gradient(to right, rgba(66, 144, 221, 0), rgba(66, 144, 221, 0.15) 16%, rgba(66, 144, 221, 0.15) 87%, rgba(66, 144, 221, 0));
    }
    &--even {
      background-image: linear-gradient(to right, rgba(9, 76, 181, 0), rgba(9, 76, 181, 0.15) 21%, rgba(9, 76, 181, 0.15) 82%, rgba(9, 76, 181, 0));
    }
  }
  &__td {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    color: #c3eaff;
    &:nth-child(1) {
      width: 14%;
    }
    &:nth-child(n+2):nth-child(-n+8) {
      width: 8%;
    }
    &:nth-child(n+9):nth-child(-n+12) {
      width: 7.5%
    }
  }
}
.pagination-container{
  text-align: center;
  padding: 15px 0px;
  margin-top: auto;
}
.pagination {
  display: inline-block;
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.08;
  letter-spacing: normal;
  color: rgba(255, 255, 255, 0.85);
}
.pagination a {
  width: 25px;
  height: 25px;
  float: left;
  padding: 8px 10px;
  text-decoration: none;
  border-radius: 3px;
  color: #c3eaff;
  margin: 0px 4px;
}
.pagination a.active {
  background-color: rgba(92, 175, 255, 0.4);
  color: white;
}
.pagination a:hover:not(.active) {background-color: rgba(92, 175, 255, 0.4);}
.search-contents-container::-webkit-scrollbar {
  width:6px;
}
.search-contents-container::-webkit-scrollbar-thumb {
  width: 6px;
  height: 146px;
  border-radius: 3.3px;
  background-color: #21b2e0;
}

</style>