<template>
  <div class="main">
    <div class="record-search">
      <div class="record-title">
        <!-- 사용자 관리 제목 -->
        <div class="record-title-contents">
          <div class="record-text">사용자 관리</div>
          <!-- 사용자 추가 버튼 -->
          <button class="record-add" @click="openAddUserDialog"><div class="record-add-img"></div> 추가 </button>
        </div>
      </div>
      <!-- 사용자 정보 테이블 -->
      <div class="record-table">
        <table class="search-table">
          <!-- 사용자 정보 테이블 헤더 -->
          <thead class="table-title">
            <div class="row-text">아이디</div>
            <div class="row-text">이름</div>
            <div class="row-text">부서</div>
            <div class="row-text">권한</div>
            <div class="row-text">로그인 유지시간</div>
            <div class="row-text">동작</div>
          </thead>
          <div class="search-contents-container">
          <tbody>
            <!-- 사용자 정보 리스트 -->
            <template v-for="(item, index) in paginatedData">
              <tr v-if="index % 2 === 0" class="table-contents" :key="item.usr_id">
                <td class="record-contents-text">{{ item.usr_id }}</td>
                <td class="record-contents-text">{{ item.usr_nm }}</td>
                <td class="record-contents-text">{{ item.usr_pn }}</td>
                <td class="record-contents-text">{{ item.usr_auth == 0 ? '관리자' : '운용자' }}</td>
                <td v-if="item.usr_auth == 0" class="record-contents-text">--</td>
                <td v-else class="record-contents-text">{{ item.usr_ti }}분</td>
                <td class="record-contents-text">
                  <button class="modify-box" @click="clickReviseBtn(item.usr_id, item.usr_nm, item.usr_pn, item.usr_auth, item.usr_ti)"><img class="modify-img" src="../../assets/UserManagement/modify.png" width="16" height="15" alt="수정"> 수정 </button>
                  <button class="modify-box-pw" @click="clickPwResetBtn(item.usr_id)"><img class="modify-img" src="../../assets/UserManagement/modify_pw.png" width="16" height="14" alt="비밀번호 초기화"> 비밀번호 초기화 </button>
                  <button class="modify-box-del" @click="clickDeleteBtn(item.usr_id)"><img class="modify-img" src="../../assets/UserManagement/modify_del.png" width="10" height="10" alt="삭제">삭제</button>
                </td>
              </tr>
              <tr v-else class="table-contents1" :key="item.usr_id">
                <td class="record-contents-text">{{ item.usr_id }}</td>
                <td class="record-contents-text">{{ item.usr_nm }}</td>
                <td class="record-contents-text">{{ item.usr_pn }}</td>
                <td class="record-contents-text">{{ item.usr_auth == 0 ? '관리자' : '운용자' }}</td>
                <td v-if="item.usr_auth == 0" class="record-contents-text">--</td>
                <td v-else class="record-contents-text">{{ item.usr_ti }}분</td>
                <td class="record-contents-text">
                  <button class="modify-box" @click="clickReviseBtn(item.usr_id, item.usr_nm, item.usr_pn, item.usr_auth, item.usr_ti)"><img class="modify-img" src="../../assets/UserManagement/modify.png" width="16" height="15" alt="수정"> 수정 </button>
                  <button class="modify-box-pw" @click="clickPwResetBtn(item.usr_id)"><img class="modify-img" src="../../assets/UserManagement/modify_pw.png" width="16" height="14" alt="비밀번호 초기화"> 비밀번호 초기화 </button>
                  <button class="modify-box-del" @click="clickDeleteBtn(item.usr_id)"><img class="modify-img" src="../../assets/UserManagement/modify_del.png" width="10" height="10" alt="삭제">삭제</button>
                </td>
              </tr>
            </template>
          </tbody>
          </div>
        </table>
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
    <!-- 유저 추가 Dialog -->
    <AddUserDialog/>
    <!-- 유저 삭제 Dialog -->
    <DeleteUserDialog/>
  </div>
</template>

<script>
import AddUserDialog from '@/components/dialog/user/AddUserDialog'
import DeleteUserDialog from '@/components/dialog/user/DeleteUserDialog'
import { SET_OVERLAY } from '@/store'

export default {
  name: 'UserManagement',
  data: () => ({
    pageNum: 0, // 현재 페이지 넘버
    pageSize: 15 // 현제 페이지에 표시할 갯수
  }),
  components: {
    AddUserDialog,
    DeleteUserDialog
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
      return this.$store.state.user.userList.filter((item) => {
        let container = Object.assign({}, item)
        // if (container.usr_id === 'admin') {
        //   return false
        // }
        return container
      })
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
     * 유저 추가 버튼 클릭시 실행되는 함수
     * 유저 추가 Dialog를 띄움
     */
    openAddUserDialog: function () {
      this.$store.dispatch('addUserDialog/OPEN_DIALOG')
    },
    /**
     * 유저 수정 버튼 클릭시 실행되는 함수
     * 유저 수정 Dialog를 띄움
     * 
     * @param usr_id 유저 아이디
     * @param usr_nm 유저 이름
     * @param usr_pn 부서명
     */
    clickReviseBtn: function (usr_id, usr_nm, usr_pn, usr_auth, usr_ti) {
      this.$store.dispatch('modifyUserDialog/OPEN_DIALOG', 
      { usr_id: usr_id, usr_nm: usr_nm, usr_pn: usr_pn, usr_auth: usr_auth, usr_ti: usr_ti, isUsrTiDisabled: usr_auth == 0, isMyInfo: false })
    },
    /**
     * 유저 패스워드 리셋 버튼 클릭시 실행되는 함수
     * 유저 패스워드 수정 Dialog를 띄움
     * 
     * @param usr_id 유저 아이디
     */
    clickPwResetBtn: function (usr_id) {
      this.$store.dispatch('modifyPasswordDialog/OPEN_DIALOG', { usr_id: usr_id })
    },
    /**
     * 유저 삭제 버튼 클릭시 실행되는 함수
     * 유저 삭제 확인 Dialog를 띄움
     * 
     * @param usr_id 유저 아이디
     */
    clickDeleteBtn: function (usr_id) {
      this.$store.dispatch('deleteUserDialog/OPEN_DIALOG', { usr_id: usr_id })
    }
  },
  created: function () {
    console.log(this.$options.name + ' created')
  },
  /**
   * 마운트시 실행되는 함수
   * 유저 정보 API를 조회함
   */
  mounted: function () {
    console.log(this.$options.name + ' mounted')
    this.$store.commit(SET_OVERLAY, true)
    Promise.all([
      this.$store.dispatch('user/GET_USERS')      
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
  margin: 26px 20px 23px 44px;
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
  border: solid 1px #b4dffa;
  background-color: rgba(139, 194, 240, 0.25);
  margin-top: -5px;
  padding: 10px 10px 9px 13px;
  font-size: 13px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: 1;
  letter-spacing: normal;
  text-align: center;
  color: #ffffff;
  margin-left: auto;
  margin-right: 20px;
}
.record-add:hover {
  box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  border: solid 1px #b4dffa;
  background-color: rgba(139, 194, 240, 0.4);
}
.record-add-img {
  background-image: url("../../assets/systemSettings/record-add.png");
  background-position: center;
  background-repeat: no-repeat;
  background-size: 100%;
  width: 10px;
  height: 10px;
  margin: auto 5px auto 0px;
}
.table-title {
  display: inline-flex;
}
.check-img {
  background-image: url('../../assets/UserManagement/check.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100%;
  width: 17px;
  height: 17px;
}
.reset-img {
  background-image: url('../../assets/UserManagement/reset.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100%;
  width: 17px;
  height: 15px;
  margin-left: 12.5px;
}
.cancle-img {
  background-image: url('../../assets/UserManagement/cancle.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100%;
  width: 13px;
  height: 13px;
  margin-left: 10.8px;
}
.modify-box {
  margin: 0px 5px;
  padding: 9px 10px;
  background-color: #4486de;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  text-shadow: 0 0 9px #5cafff;
  color: #fff;
}
.modify-box:hover {
  box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  background-color: #83b8ff;
}
.modify-box-pw {
  margin: 0px 5px;
  padding: 9px 10px;
  background-color: #0555b5;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  color: #fff;
  text-shadow: 0 0 9px #5cafff;
}
.modify-box-pw:hover {
  box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  background-color: #2775d3;
}
.modify-box-del {
  margin: 0px 5px;
  padding: 9px 10px;
  background-color: #1a2286;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  color: #fff;
  text-shadow: 0 0 9px #5cafff;
}
.modify-box-del:hover {
  box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  background-color: #30389a;
}
.modify-box-del > span {
  vertical-align: middle;
}
.modify-img {
  vertical-align: middle;
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
  padding: 14px 20px 16px 17px;
}
.table-title {
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
  background-image: linear-gradient(to right, rgba(16, 36, 65, 0) 0%, rgba(39, 86, 162, 0.93) 14%, rgba(18, 43, 92, 0.96) 49%, rgba(39, 86, 162, 0.93) 85%, rgba(16, 36, 65, 0) 100%);
}
.table-contents {
  display: inline-flex;
  align-items: center;
  width: 100%;
  height: 55px;
  font-size: 13px;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  vertical-align: middle;
  background-image: linear-gradient(to right, rgba(66, 144, 221, 0), rgba(66, 144, 221, 0.15) 16%, rgba(66, 144, 221, 0.15) 87%, rgba(66, 144, 221, 0));
}
.table-contents1{
  display: inline-flex;
  align-items: center;
  width: 100%;
  height: 55px;
  font-size: 13px;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  vertical-align: middle;
  background-image: linear-gradient(to right, rgba(9, 76, 181, 0) 3%, rgba(9, 76, 181, 0.15) 21%, rgba(9, 76, 181, 0.15) 82%, rgba(9, 76, 181, 0) 98%);
}
.search-table {
  width: 100%;
  border-spacing: 0px 4px;
  border-collapse: separate;
}
.row-text {
  padding-top: 15px;
  width: calc(100% / 6);
  height: 48px;
  text-shadow: 0 0 9px #5cafff;
}
/* .contents-text {
  padding-top: 16px;
  width:456px;
  height: 55px;
} */
.record-contents-text {
  width: calc(100% / 6);
  /* height: 55px; */
  text-shadow: 0 0 9px #5cafff;
}
/* .contents-modify {
  display:flex;
  align-items: center;
  justify-content: center;
  width: 447px;
  height: 55px;
} */
.search-contents-container {
  width: 100%;
  height: 730px;
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
  height: 24px;
}
input::placeholder {
  font-size: 13px;
  text-align: center;
  color: #20b1e0;
}
thead, tbody { display: block; }
</style>