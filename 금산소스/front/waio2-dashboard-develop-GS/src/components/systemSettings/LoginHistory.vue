<template>
  <div class="main">
    <div class="record-search">
      <!-- 로그인 이력 제목 -->
      <div class="record-title">
        <div class="record-title-contents">
          <div class="record-text">로그인 이력</div>
        </div>
      </div>
      <!-- 로그인 이력 테이블 -->
      <div class="record-table">
        <table class="search-table">
          <thead class="table-title">
            <div class="row-text">동작 분류</div>
            <div class="row-text">접속 시간</div>
            <div class="row-text">아이디</div>
            <div class="row-text">아이피</div>
          </thead>
        </table>
        <div class="search-contents-container">
          <tbody>
            <template v-for="(item, index) in paginatedData">
              <!-- 로그인 이력 리스트 -->
              <template v-if="index % 2 ===0">
                <!-- 로그인/로그아웃 여부에 따른 로직 -->
                <tr v-if="item.lgn_ty === 1" :key="item.history_index" class="table-contents-online">
                  <div class="contents-online">{{ loginType(item.lgn_ty) }}</div>
                  <div class="login-contents-text">{{ item.lgn_ti | moment('YYYY-MM-DD HH:mm:ss') }}</div>
                  <div class="login-contents-text">{{ item. usr_id}}</div>
                  <div class="login-contents-text">{{ item.lgn_addr }}</div>
                </tr>
                <tr v-else :key="item.history_index" class="table-contents-offline">
                  <div class="contents-offline">{{ loginType(item.lgn_ty) }}</div>
                  <div class="login-contents-text">{{ item.lgn_ti | moment('YYYY-MM-DD HH:mm:ss') }}</div>
                  <div class="login-contents-text">{{ item.usr_id}}</div>
                  <div class="login-contents-text">{{ item.lgn_addr }}</div>
                </tr>
              </template>
              <template v-else>
                <tr v-if="item.lgn_ty === 1" :key="item.history_index" class="table-contents-online-two">
                  <div class="contents-online">{{ loginType(item.lgn_ty) }}</div>
                  <div class="login-contents-text">{{ item.lgn_ti | moment('YYYY-MM-DD HH:mm:ss') }}</div>
                  <div class="login-contents-text">{{ item. usr_id}}</div>
                  <div class="login-contents-text">{{ item.lgn_addr }}</div>
                </tr>
                <tr v-else :key="item.history_index" class="table-contents-offline-two">
                  <div class="contents-offline">{{ loginType(item.lgn_ty) }}</div>
                  <div class="login-contents-text">{{ item.lgn_ti | moment('YYYY-MM-DD HH:mm:ss') }}</div>
                  <div class="login-contents-text">{{ item.usr_id}}</div>
                  <div class="login-contents-text">{{ item.lgn_addr }}</div>
                </tr>
              </template>
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
  </div>
</template>

<script>
import { SET_OVERLAY } from '@/store'

export default {
  name: 'LoginHistory',
  data: () => ({
    pageNum: 0, // 현재 페이지 넘버
    pageSize: 15 // 한 페이지에 표현할 갯수
  }),
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
      return this.$store.state.loginHistory.history
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
    /**
     * 로그인/로그아웃을 판단하는 함수
     * 
     * @param type 로그인 타입
     * @return 로그인/로그아웃
     */
    loginType: function (type) {
      return type === 1 ? '로그인' : '로그아웃'
    },
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
    }
  },
  created: function () {
    console.log(this.$options.name + ' created')
  },
  /**
   * 마운트시 실행되는 함수
   * 로그인 이력 API를 조회함
   */
  mounted: function () {
    console.log(this.$options.name + ' mounted')
    this.$store.commit(SET_OVERLAY, true)
    Promise.all([
      this.$store.dispatch('loginHistory/history')
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
.record-add {
  display: flex;
  width: 71px;
  height: 34px;
  border: solid 1px #ffffff;
  margin-top: -5px;
  padding: 10.5px 15.6px 9.5px 13.2px;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1;
  letter-spacing: normal;
  text-align: center;
  color: #ffffff;
  margin-left: auto;
  margin-right: 20px;
}
.record-add-img {
  background-image: url("../../assets/systemSettings/record-add.png");
  background-position: center;
  background-repeat: no-repeat;
  background-size: 100%;
  width: 10.7px;
  height: 12.7px;
  margin-right: 5.4px;
}
.table-contents1 {
  border: solid 2px #26bae3;
  width: 1788px;
  height: 55px;
  margin: 8px 0 0 0;
  font-size: 13px;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: #20b1e0;
  vertical-align: middle;
  background-color: #091a3b;
}
.modify-box {
  width: 68px;
  height: 34px;
  background-color: #8888fc;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  color: #1c3482;
}
.modify-img {
  vertical-align: text-bottom;
  margin-right: 4px;
}
.arrow-img{
  background-image: url('../../assets/UserManagement/right-arrow.png');
  background-repeat: no-repeat;
  background-position: left;
  width: 10px;
  height: 21px;
  margin-top: 5px;
}
.record-title-contents {
  display: flex;
  padding: 11.2px 0 15px 33px;
  width:100%;
  height: 47px;
  background-image: url('../../assets/editableDashboard/title_under.png');
  background-position-x: -8px;
}
.record-img {
  background-image: url('../../assets/drugInjection/algorithm-arrow.png');
  background-repeat: no-repeat;
  background-position: right;
  background-position-y: 5px;
  width: 15px;
  height: 22px;
  margin-left: 7.6px;
}
.record-text {
  margin-left: 16px;
  font-size: 18px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: left;
  color: #ffffff;
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
  padding: 23px 20px 16px 17px ;
}
.table-title {
  display: inline-flex;
  font-size: 15px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: rgba(255, 255, 255, 0.85);
  vertical-align: middle;
  width: 100%;
  justify-content: center;
  text-shadow: 0 0 9px #5cafff;
  background-image: linear-gradient(to right, rgba(16, 36, 65, 0) 0%, rgba(39, 86, 162, 0.93) 14%, rgba(18, 43, 92, 0.96) 49%, rgba(39, 86, 162, 0.93) 85%, rgba(16, 36, 65, 0) 100%);
}
.table-contents-online {
  display: inline-flex;
  width: 1788px;
  height: 55px;
  font-size: 13px;
  text-shadow: 0 0 9px #5cafff;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  line-height: 2;
  color: #fff;
  vertical-align: middle;
  background-image: linear-gradient(to right, rgba(66, 144, 221, 0), rgba(66, 144, 221, 0.15) 16%, rgba(66, 144, 221, 0.15) 87%, rgba(66, 144, 221, 0));
}
.table-contents-online-two {
  display: inline-flex;
  width: 1788px;
  height: 55px;
  font-size: 13px;
  text-shadow: 0 0 9px #5cafff;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  line-height: 2;
  color: #fff;
  vertical-align: middle;
  background-image: linear-gradient(to right, rgba(9, 76, 181, 0) 3%, rgba(9, 76, 181, 0.15) 21%, rgba(9, 76, 181, 0.15) 82%, rgba(9, 76, 181, 0) 98%);
}
.table-contents-offline {
  display: inline-flex;
  width: 1788px;
  height: 55px;
  font-size: 13px;
  text-shadow: 0 0 9px #5cafff;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  line-height: 2;
  color: #fff;
  vertical-align: middle;
  background-image: linear-gradient(to right, rgba(66, 144, 221, 0), rgba(66, 144, 221, 0.15) 16%, rgba(66, 144, 221, 0.15) 87%, rgba(66, 144, 221, 0));
}
.table-contents-offline-two {
  display: inline-flex;
  width: 1788px;
  height: 55px;
  font-size: 13px;
  text-shadow: 0 0 9px #5cafff;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  line-height: 2;
  color: #fff;
  vertical-align: middle;
  background-image: linear-gradient(to right, rgba(9, 76, 181, 0) 3%, rgba(9, 76, 181, 0.15) 21%, rgba(9, 76, 181, 0.15) 82%, rgba(9, 76, 181, 0) 98%);
}
.search-table {
  border-spacing: 0px 0px;
  border-collapse: separate;
}
.row-text {
  padding-top: 15px;
  width: 444px;
  height: 48px;
  text-shadow: 0 0 9px #5cafff;
}
.div {
  width: 120px;
  height: 30px;
  background-color: #0b4491;
}
.contents-online {
  background-image: url("../../assets/systemSettings/online.png");
  background-repeat: no-repeat;
  background-size: auto;
  background-position: left;
  background-position-x: 180px;
  width: 447px;
  height: 55px;
  padding-top: 16px;
}
.contents-offline {
  background-image: url("../../assets/systemSettings/offline.png");
  background-repeat: no-repeat;
  background-size: auto;
  background-position: left;
  background-position-x: 170px;
  width: 140px;
  color:#5277a8;
  width: 447px;
  height: 55px;
  padding-top: 16px;
}
.login-contents-text {
  padding-top: 16px;
  width:447px;
  height: 55px;
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
  height: 30px;
}
input::placeholder {
  font-size: 13px;
  text-align: center;
  color: #20b1e0;
}
</style>