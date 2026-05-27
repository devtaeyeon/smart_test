<template>
  <v-dialog
    v-model="$store.state.modifyPasswordDialog.visible"
    persistent
    max-width="420px"
  >
    <div class="popup-main">
      <div class="user-modify-container">비밀번호 초기화</div>
      <div class="modify-contents">
        <div class="modify-data-box">
          <div class="modify-text"> · ID</div> 
          <input class="input-box" type="text" v-model="$store.state.modifyPasswordDialog.usr_id" disabled>
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 비밀번호</div> 
          <input class="department-input-box" type="password" v-model="$store.state.modifyPasswordDialog.usr_pw">
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 비밀번호 확인</div> 
          <input class="department-input-box" type="password" v-model="$store.state.modifyPasswordDialog.usr_pw_confirm">
        </div>
        <div class="btn-confirm-box">
          <button class="confirm-box" @click="modifyPassword">
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
  }),
  methods: {
    modifyPassword: function () {
      let obj = {}
      obj.usr_id = this.$store.state.modifyPasswordDialog.usr_id
      obj.usr_pw = this.$store.state.modifyPasswordDialog.usr_pw
      obj.usr_pw_confirm = this.$store.state.modifyPasswordDialog.usr_pw_confirm
      obj.usr_auth = 0
      if (this.$store.state.modifyPasswordDialog.usr_pw === null | 
          this.$store.state.modifyPasswordDialog.usr_pw === '' | 
          this.$store.state.modifyPasswordDialog.usr_pw_confirm === null | 
          this.$store.state.modifyPasswordDialog.usr_pw_confirm === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '비밀번호를 입력해주세요' })
        return
      }
      if (this.$store.state.modifyPasswordDialog.usr_pw !== this.$store.state.modifyPasswordDialog.usr_pw_confirm) {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '비밀번호가 일치하지 않습니다' })
        return
      }
      // 비밀번호 유효성 검사 추가
      // 8자리 이상 20자리 미만의 영문, 숫자, 특수문자 포함
      let passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*~])[A-Za-z\d!@#$%^&*~]{8,20}$/;
      console.log(passwordRegex.test('asdfasdf1~!')); 
      if (!passwordRegex.test(this.$store.state.modifyPasswordDialog.usr_pw)) {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', {
          title: '경고',
          text1: '비밀번호는 8자리 이상 20자리 미만이며, 영문, 숫자, 특수문자를 포함해야 합니다.'
        });
        return;
      }
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('user/MODIFY_PASSWORD', obj)    
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    },
    closeDialog: function () {
      this.$store.dispatch('modifyPasswordDialog/CLOSE_DIALOG')
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
  padding: 7px 0 0 58.7px;
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
  text-shadow: 0 0 9px #5cafff;
  color: #fff;
  margin-left: 25px;
}
.cancel-box:hover {
  background-color: #132357;
}
</style>