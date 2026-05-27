<template>
  <v-dialog
    v-model="$store.state.modifyUserDialog.visible"
    persistent
    max-width="420px"
  >
    <div v-if="$store.state.modifyUserDialog.isMyInfo" class="popup-main">
      <div class="user-modify-container">사용자 정보 수정</div>
      <div class="modify-contents">
        <div class="modify-data-box">
          <div class="modify-text"> · ID</div> 
          <input class="input-box" type="text" v-model="$store.state.modifyUserDialog.usr_id" disabled>
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 이름</div> 
          <input class="input-box" type="text" v-model="$store.state.modifyUserDialog.usr_nm" disabled>
        </div>
        <div class="modify-department">
          <div class="modify-text"> · 부서</div> 
          <input class="department-input-box" type="text" v-model="$store.state.modifyUserDialog.usr_pn" maxlength="20">
        </div>
        <div class="btn-confirm-box">
          <button class="confirm-box" @click="modifyUserMyInfo">
            확인
          </button>
          <button class="cancel-box" @click="closeDialog">
            취소
          </button>
        </div>
      </div>
    </div>
    <div v-else class="popup-main">
      <div class="user-modify-container">사용자 정보 수정</div>
      <div class="modify-contents">
        <div class="modify-data-box">
          <div class="modify-text"> · ID</div> 
          <input class="input-box" type="text" v-model="$store.state.modifyUserDialog.usr_id" disabled>
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 이름</div> 
          <input class="input-box" type="text" v-model="$store.state.modifyUserDialog.usr_nm" disabled>
        </div>
        <div class="modify-department">
          <div class="modify-text"> · 부서</div> 
          <input class="department-input-box" type="text" v-model="$store.state.modifyUserDialog.usr_pn" maxlength="20">
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 권한</div> 
          <v-select outlined
          :menu-props="{offsetY: true,nudgeBottom: 0}"
          v-model="$store.state.modifyUserDialog.usr_auth"
          :items="authList"
          item-text="text"
          item-value="usr_auth"
          @change="setUsrTi">
          </v-select>
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 로그인 유지시간</div> 
          <v-select outlined
          :menu-props="{offsetY: true,nudgeBottom: 0}"
          v-model="$store.state.modifyUserDialog.usr_ti"
          :items="filteredUsrTiList"
          item-text="text"
          item-value="usr_ti"
          :disabled="$store.state.modifyUserDialog.isUsrTiDisabled">
          </v-select>
        </div>
        <div class="btn-confirm-box">
          <button class="confirm-box" @click="modifyUser">
            확인
          </button>
          <button class="cancel-box" @click="closeDialog">
            취소
          </button>
        </div>
      </div>
    </div>
  </v-dialog>
</template>
<script>
import { SET_OVERLAY } from '@/store'
export default {
  data: () => ({
    authList: [{usr_auth : 0, text: '관리자'},{usr_auth : 1, text: '운용자'}],
    usrTiList: [{usr_ti : 0, text: '--'},{usr_ti : 10, text: '10분'},{usr_ti : 20, text: '20분'}
    ,{usr_ti : 30, text: '30분'},{usr_ti : 40, text: '40분'}
    ,{usr_ti : 50, text: '50분'},{usr_ti : 60, text: '60분'}
    ],
  }),
  computed: {
    // 권한에 따라 usrTiList를 필터링
    filteredUsrTiList() {
      let isAdmin = this.$store.state.modifyUserDialog.usr_auth == 0;
      // 관리자는 전체 리스트, 운용자는 '--' 제외
      return isAdmin
        ? this.usrTiList
        : this.usrTiList.filter(item => item.usr_ti != 0);
    }
  },
  methods: {
    modifyUserMyInfo: function () {
      let obj = {}
      obj.usr_id = this.$store.state.modifyUserDialog.usr_id
      obj.usr_nm = this.$store.state.modifyUserDialog.usr_nm
      obj.usr_pn = this.$store.state.modifyUserDialog.usr_pn
      if (this.$store.state.modifyUserDialog.usr_pn === null | this.$store.state.modifyUserDialog.usr_pn === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '부서명을 입력해주세요' })
        return
      }
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('user/MODIFY_USER_MYINFO', obj)
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    },
    modifyUser: function () {
      let obj = {}
      obj.usr_id = this.$store.state.modifyUserDialog.usr_id
      obj.usr_nm = this.$store.state.modifyUserDialog.usr_nm
      obj.usr_pn = this.$store.state.modifyUserDialog.usr_pn
      obj.usr_auth = this.$store.state.modifyUserDialog.usr_auth
      obj.usr_ti = this.$store.state.modifyUserDialog.usr_ti
      if (this.$store.state.modifyUserDialog.usr_pn === null | this.$store.state.modifyUserDialog.usr_pn === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '부서명을 입력해주세요' })
        return
      }
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('user/MODIFY_USER', obj)
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    },
    closeDialog: function () {
      this.$store.dispatch('modifyUserDialog/CLOSE_DIALOG')
    },
    setUsrTi: function (selectedAuth) {
      if (selectedAuth == 0) {
        this.$store.state.modifyUserDialog.usr_ti = 0
        this.$store.state.modifyUserDialog.isUsrTiDisabled = true;
      } else {
        this.$store.state.modifyUserDialog.usr_ti = 30  // 사용자 디폴트 30분
        this.$store.state.modifyUserDialog.isUsrTiDisabled = false;
      }
    }
  }
}
</script>
<style scoped>
.popup-main{
  /* width: 380px; */
  /* height: 400px; */
  border-radius: 10px;
  /* background-color: #1d498f; */
  background-color: rgb(19, 47, 78);
  padding: 11px 0px 14px 0;
}
.user-modify-container {
  font-size: 16px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: left;
  text-shadow: 0 0 9px #5cafff;
  color: #ffffff;
  width: 215px;
  height: 36px;
  padding: 7px 0 0 38.7px;
  height: 47px;
  background-image: url('../../../assets/editableDashboard/title_under.png');
  /* background-image: linear-gradient(to right, rgba(0, 9, 81, 0.56), rgba(0, 7, 79, 0)); */
}
.modify-contents {
  font-size: 15px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: left;
  text-shadow: 0 0 9px #5cafff;
  color: #ffffff;
  line-height: 2.4;
  padding:18px 12.3px
}
.modify-data-box,.modify-department,.btn-box-container {
  display: inline-flex;
  margin-bottom: 15px;
}
.modify-data-box {
  height: 36px;
}
.modify-text {
  width: 120px;
}
.input-box {
  width: 270.4px;
  height: 34px;
  border: solid 1px #87f4f4;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: left;
  text-shadow: 0 0 9px #5cafff;
  color: #fff;
  padding-left: 11px;
}
.department-input-box {
  width: 270.4px;
  height: 34px;
  border-bottom: solid 1px #fff;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: left;
  text-shadow: 0 0 9px #5cafff;
  color: #fff;
  padding-left: 11px;
}
input::placeholder {
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: left;
  text-shadow: 0 0 9px #5cafff;
  color: #fff;
  margin-left: 11px;
}
.btn-confirm-box {
  margin-top: 20px;
}
.confirm-box {
  width: 183px;
  height: 40px;
  background-color: #beeeff;
  color: #171b48;
}
.confirm-box:hover {
  background-color: #9BD3E5;
}
.cancel-box {
  width: 183px;
  height: 40px;
  background-color: #1c3482;;
  color: #fff;
  margin-left: 25px;
}
.cancel-box:hover {
  background-color: #132357;
}
</style>