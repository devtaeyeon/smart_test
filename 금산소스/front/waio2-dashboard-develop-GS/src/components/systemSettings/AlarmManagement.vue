<template>
  <div class="main">
    <div class="record-search">
      <!-- 알람 관리 제목 -->
      <div class="record-title">
        <div class="record-title-contents">
          <div class="record-text">알람관리</div>
        </div>
      </div>
      <!-- 알람 관리 테이블 -->
      <div class="record-table">
        <table class="search-table">
          <!-- 알람 관리 테이블 헤더 -->
          <thead class="table-title">
            <div class="system-table-text">아이디</div>
            <div class="system-table-text">코드명</div>
            <div class="system-table-text">표시명</div>
            <div class="system-table-text">종류</div>
            <div class="system-table-text ">비교값</div>
            <div class="system-table-text">IWSP 전송여부</div>
            <div class="system-table-text">동작</div>
          </thead>
        </table>
        <div class="search-contents-container">
          <tbody>
            <!-- 알람 내역 리스트 -->
            <template v-for="(item, index) in paginatedData">
              <!-- 토글 형태로 tr의 배경색을 변경함 -->
              <tr v-if="index % 2 === 0" class="table-contents" :key="item.seq">
                <div class="table-content-text">{{ item.alm_id }}</div>
                <div class="table-content-text">{{ item.cd_nm }}</div>
                <div class="table-content-text">{{ item.dp_nm }}</div>
                <div class="table-content-text">{{ getSelectedItem(item.alm_id).alarmType }}</div>
                <div class="table-content-text">{{ item.cmp_val }}</div>
                <div class="table-content-text">{{ item.scd_snd }}</div>
                <!-- 수정 버튼 -->
                <div class="contents-modify">
                  <button class="modify-box" @click="clickReviseBtn(item.seq, item.alm_id, item.dp_nm, item.cmp_val, item.scd_snd)"><img class="modify-img" src="../../assets/systemSettings/modify.png" width="16" height="15" alt="수정"> 수정 </button>
                </div>
              </tr>
              <tr v-else class="table-contents1" :key="item.seq">
                <div class="table-content-text">{{ item.alm_id }}</div>
                <div class="table-content-text">{{ item.cd_nm }}</div>
                <div class="table-content-text">{{ item.dp_nm }}</div>
                <div class="table-content-text">{{ getSelectedItem(item.alm_id).alarmType }}</div>
                <div class="table-content-text">{{ item.cmp_val }}</div>
                <div class="table-content-text">{{ item.scd_snd }}</div>
                <!-- 수정 버튼 -->
                <div class="contents-modify">
                  <button class="modify-box" @click="clickReviseBtn(item.seq, item.alm_id, item.dp_nm, item.cmp_val, item.scd_snd)"><img class="modify-img" src="../../assets/systemSettings/modify.png" width="16" height="15" alt="수정"> 수정 </button>
                </div>
              </tr>
            </template>
          </tbody>
        </div>
        <!-- 페이지네이션 컨테이너 -->
        <div class="pagination-container">
          <div class="pagination">
            <a href="javascript:void(0);" @click="prevPage"><img src="../../assets/UserManagement/left-arrow.png" alt="이전"></a>
            <a href="javascript:void(0);" @click="setPage(n-1)" v-for="n in showedPageList" :key="n" :class="{active:n == pageNum + 1}">{{ n }}</a>
            <a href="javascript:void(0);" @click="nextPage"><img src="../../assets/UserManagement/right-arrow.png" alt="다음"></a>
          </div>
        </div>
      </div>
    </div>
    <!-- 알람 수정 Dialog -->
    <ModifyAlarmDialog/> 
  </div>
</template>

<script>
import { SET_OVERLAY } from '@/store'
import ModifyAlarmDialog from '@/components/dialog/alarm/ModifyAlarmDialog'
export default {
  name: 'AlarmManagement',
  data: () => ({
    pageNum: 0, // 현재 페이지 넘버
    pageSize: 15 // 한 페이지에 보여줄 갯수
  }),
  components: {
    ModifyAlarmDialog
  },
  computed: {
    pageCount () {
      let listLeng = this.calcData.length
      let listSize = this.pageSize
      let page = Math.floor(listLeng / listSize)
      if (listLeng % listSize > 0) page += 1
      return page
    },
    paginatedData () {
      const start = this.pageNum * this.pageSize
      const end = start + this.pageSize
      return this.calcData.slice(start, end)
    },
    calcData () {
      return this.$store.state.alarm.alarmInfo
    },
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
    nextPage () {
      if (this.pageNum === this.pageCount - 1) {
        this.pageNum = this.pageCount - 1
      } else {
        this.pageNum += 1
      }
    },
    prevPage () {
      if (this.pageNum === 0) {
        this.pageNum = 0
      } else {
        this.pageNum -= 1
      }
    },
    setPage (v) {
      this.pageNum = v
    },
    range: function (start, end) {
      let list = []
      for (let i = start; i <= end; i++) list.push(i)
      return list
    },
    /**
     * 선택된 알람 코드에 따라 화면 출력을 다르게 하기 위한 함수
     * 
     * @param alarmCode 알람코드
     * @return 알람 객체(alarmClassification, alarmType)
     */
    getSelectedItem: function (alarmCode) {
      let classification = alarmCode.toString().substr(1, 1)
      let selectedItem = this.$store.state.alarm.alarmInfo.find((v) => v.alm_id === alarmCode)
      let typeStr = ''
      if (selectedItem.type === 0) {
          typeStr = 'ON/OFF'
        } else if (selectedItem.type === 1) {
          typeStr = 'Threshold'
        } else if (selectedItem.type === 2) {
          typeStr = 'Control'
        } else {
          typeStr = 'AI'
        }
      return {
        'alarmClassification': classification === '1' ? 'HW' : classification === '2' ? 'SW' : 'AI',
        'alarmType': typeStr
      }
    },
    /**
     * 알람 정보 수정 선택시 실행되는 함수
     * 알람 정보 수정 Dialog를 띄움
     * 
     * @param seq 알람 정보 인덱스
     * @param alm_id 알람 아이디
     * @param dp_nm 표시할 이름
     * @param cmp_val 알람 값
     * @param scd_snd scada 전송 여부
     */
    clickReviseBtn: function (seq, alm_id, dp_nm, cmp_val, scd_snd) {
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('modifyAlarmDialog/OPEN_DIALOG', { seq: seq, alm_id: alm_id, dp_nm: dp_nm, cmp_val: cmp_val, scd_snd: scd_snd })  
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    },
  },
  created: function () {
    console.log(this.$options.name + ' created')
  },
  /**
   * 마운트시 실행되는 함수
   * 알람 세팅 정보 API를 요청
   */
  mounted: function () {
    console.log(this.$options.name + ' mounted')
    this.$store.commit(SET_OVERLAY, true)
    Promise.all([
      this.$store.dispatch('alarm/GET_ALL_ALARM_SETTING')
    ]).finally(() => {
      this.$store.commit(SET_OVERLAY, false)
    })
  },
  destroyed: function () {
    console.log(this.$options.name + ' destoryed')
  },
  updated: function () {
    console.log(this.$options.name + ' updated')
  }
}
</script>

<style scoped>
.main {
  display: flex;
  margin: 26px 20px 0px 44px;
}
.record-search {
  width: 1858px;
  height: 964px;
  padding: 0 15px 15px 15px;
}
.modify-box {
  width: 68px;
  height: 34px;
  background-color: #4486de;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  color: #fff;
  text-shadow: 0 0 9px #5cafff;
}
.modify-box:hover {
  box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  background-color: #83b8ff;
}
.modify-img {
  vertical-align: text-bottom;
  margin-right: 4px;
}
.record-title-contents {
  display: flex;
  padding: 11.2px 0 15px 38px;
  width:100%;
  height: 47px;
  background-image: url('../../assets/editableDashboard/title_under.png');
  background-position-x: -8px;
}
.record-img {
  background-image: url('../../assets/drugInjection/algorithm-arrow.png');
  background-repeat: no-repeat;
  background-position: right;
  width: 15px;
  height: 15px;
  margin-left: 7.6px;
}
.record-text {
  margin-left: 16px;
  font-size: 18px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 0.77;
  letter-spacing: normal;
  text-align: left;
  color: #ffffff;
  text-shadow: 0 0 9px #5cafff;
}
.search {
  width: 80px;
  height: 34px;
  margin: 0 9px 0.9px;
  text-align: center;
  padding-top: 5px;
  background-color: #3bb7e5;
}
.reset {
  width: 84px;
  height: 34px;
  margin: 0 0 0.9px 9px;
  text-align: center;
  padding-top: 5px;
  border: solid 1px #ffffff;
}
.btn-text {
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.66;
  letter-spacing: normal;
  text-align: left;
  color: #ffffff;
}
.img-search {
  vertical-align: text-top;
  margin-right :6px;
}
.record-table {
  width: 1828px;
  height: 879px;
  margin-top: 11.5px;
  padding: 14px 20px 16px 17px ;
}
.table-title {
  font-size: 15px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  text-shadow: 0 0 9px #5cafff;
  color: rgba(255, 255, 255, 0.85);
  vertical-align: middle;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  background-image: linear-gradient(to right, rgba(16, 36, 65, 0) 0%, rgba(39, 86, 162, 0.93) 14%, rgba(18, 43, 92, 0.96) 49%, rgba(39, 86, 162, 0.93) 85%, rgba(16, 36, 65, 0) 100%);
}
.table-contents {
  display: inline-flex;
  width: 1788px;
  height: 55px;
  font-size: 13px;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  text-shadow: 0 0 9px #5cafff;
  vertical-align: middle;
  background-image: linear-gradient(to right, rgba(66, 144, 221, 0), rgba(66, 144, 221, 0.15) 16%, rgba(66, 144, 221, 0.15) 87%, rgba(66, 144, 221, 0));
}
.table-contents1 {
  display: inline-flex;
  width: 1788px;
  height: 55px;
  font-size: 13px;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  text-shadow: 0 0 9px #5cafff;
  vertical-align: middle;
  background-image: linear-gradient(to right, rgba(9, 76, 181, 0) 3%, rgba(9, 76, 181, 0.15) 21%, rgba(9, 76, 181, 0.15) 82%, rgba(9, 76, 181, 0) 98%);
}
.search-table {
  border-spacing: 0px 0px;
  border-collapse: separate;
  margin-top: 15px;
}
.system-table-text {
  padding-top: 15px;
  margin-right: 4px;
  width: 252px;
  height: 48px;
  text-shadow: 0 0 9px #5cafff;
}
.contents-modify {
  width: 256px;
  height: 55px;
  padding-top: 10px;
}
.col-text {
  width: 284px;
  height: 30px;
  background-color: #0e3283;
}
.div {
  width: 120px;
  height: 30px;
  background-color: #0b4491;
}
.contents-text {
  width:150px;
  height: 10px;
}
.table-content-text {
  padding-top: 16px;
  width: 256px;
  height: 55px;
  text-shadow: 0 0 9px #5cafff;
}
.search-contents-container {
  width: 100%;
  height: 740px;
  overflow: overlay;
}
.search-contents-container::-webkit-scrollbar {
  width:6px;
}
.search-contents-container::-webkit-scrollbar-thumb {
  width: 6px;
  height: 146px;
  border-radius: 3.3px;
  background-color: #21b2e0;
}
.pagination-container{
  text-align: center;
  padding-top: 20px;
}
.pagination {
  display: inline-block;
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.08;
  letter-spacing: normal;
  color: rgba(255, 255, 255, 1);
}
.pagination a {
  float: left;
  padding: 8px 10px;
  color:white;
  text-decoration: none;
  border-radius: 4px;
}
.pagination a.active {
  background-color: rgba(253, 254, 255, 0.3);
  color: white;
}
.pagination a:hover:not(.active) {background-color: #ddd;}
input {
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  border-bottom: solid 1px #1aabde;
  height: 30px;
}
input::placeholder {
  font-size: 13px;
  text-align: center;
  color: #20b1e0;
}
</style>