<template>
  <v-dialog
    v-model="$store.state.addUserDialog.visible"
    persistent
    max-width="420px"
  >
    <div class="popup-main">
      <div class="user-modify-container">사용자 생성</div>
      <div class="modify-contents">
        <div class="modify-data-box">
          <div class="modify-text"> · ID</div> 
          <input class="department-input-box" type="text" v-model="$store.state.addUserDialog.usr_id">
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 비밀번호</div> 
          <input class="department-input-box" type="password" v-model="$store.state.addUserDialog.usr_pw">
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 비밀번호 확인</div> 
          <input class="department-input-box" type="password" v-model="$store.state.addUserDialog.usr_pw_confirm">
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 이름</div> 
          <input class="department-input-box" type="text" v-model="$store.state.addUserDialog.usr_nm">
        </div>
        <div class="modify-department">
          <div class="modify-text"> · 부서</div> 
          <!-- <input class="department-input-box" type="text" v-model="$store.state.addUserDialog.usr_pn"> -->
          <input class="department-input-box" type="text" v-model="$store.state.addUserDialog.usr_pn">
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 권한</div> 
          <v-select outlined
          :menu-props="{offsetY: true,nudgeBottom: 0}"
          v-model="$store.state.addUserDialog.usr_auth"
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
          v-model="$store.state.addUserDialog.usr_ti"
          :items="filteredUsrTiList"
          item-text="text"
          item-value="usr_ti"
          :disabled="$store.state.addUserDialog.isUsrTiDisabled">
          </v-select>
        </div>
        <div class="btn-confirm-box">
          <button class="confirm-box" @click="addUser">
            확인
          </button>
          <button class="cancle-box" @click="closeAddUserDialog">
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
      let isAdmin = this.$store.state.addUserDialog.usr_auth == 0;
      // 관리자는 전체 리스트, 운용자는 '--' 제외
      return isAdmin
        ? this.usrTiList
        : this.usrTiList.filter(item => item.usr_ti != 0);
    }
  },
  methods: {
    addUser: function () {
      let obj = {}
      obj.usr_id = this.$store.state.addUserDialog.usr_id
      obj.usr_nm = this.$store.state.addUserDialog.usr_nm
      obj.usr_pw = this.$store.state.addUserDialog.usr_pw
      obj.usr_pn = this.$store.state.addUserDialog.usr_pn
      obj.usr_auth = this.$store.state.addUserDialog.usr_auth
      obj.usr_ti = this.$store.state.addUserDialog.usr_ti
      if (this.$store.state.addUserDialog.usr_id === null | this.$store.state.addUserDialog.usr_id === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '아이디를 입력해주세요' })
        return
      }
      if (this.$store.state.addUserDialog.usr_nm === null | this.$store.state.addUserDialog.usr_nm === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '이름을 입력해주세요' })
        return
      }
      if (this.$store.state.addUserDialog.usr_pw === null | 
          this.$store.state.addUserDialog.usr_pw === '' | 
          this.$store.state.addUserDialog.usr_pw_confirm === null | 
          this.$store.state.addUserDialog.usr_pw_confirm === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '비밀번호를 입력해주세요' })
        return
      }
      if (this.$store.state.addUserDialog.usr_pw !== this.$store.state.addUserDialog.usr_pw_confirm) {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '비밀번호가 일치하지 않습니다' })
        return
      }

      // 비밀번호 유효성 검사 추가
      // 8자리 이상 20자리 미만의 영문, 숫자, 특수문자 포함
      let passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*~])[A-Za-z\d!@#$%^&*~]{8,20}$/;
      console.log(passwordRegex.test('asdfasdf1~!')); 
      if (!passwordRegex.test(this.$store.state.addUserDialog.usr_pw)) {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', {
          title: '경고',
          text1: '비밀번호는 8자리 이상 20자리 미만이며, 영문, 숫자, 특수문자를 포함해야 합니다.'
        });
        return;
      }
      if (this.$store.state.addUserDialog.usr_pn === null | this.$store.state.addUserDialog.usr_pn === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '부서명을 입력해주세요' })
        return
      }
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('user/ADD_USER', obj)
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    },
    closeAddUserDialog: function () {
      this.$store.dispatch('addUserDialog/CLOSE_DIALOG')
    },
    setUsrTi: function (selectedAuth) {
      if (selectedAuth == 0) {
        this.$store.state.addUserDialog.usr_ti = 0
        this.$store.state.addUserDialog.isUsrTiDisabled = true;
      } else {
        this.$store.state.addUserDialog.usr_ti = 30  // 사용자 디폴트 30분
        this.$store.state.addUserDialog.isUsrTiDisabled = false;
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
  padding: 7px 0 0 19.7px;
  height: 47px;
  background-image: url('../../../assets/editableDashboard/title_under.png');
  background-position-x: -10px;
  padding-left: 47px;
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
  text-shadow: 0 0 9px #5cafff;
  color: #171b48;
}
.cancle-box {
  width: 183px;
  height: 40px;
  background-color: #1c3482;
  text-shadow: 0 0 9px #5cafff;
  color: #fff;
  margin-left: 25px;
}
</style>