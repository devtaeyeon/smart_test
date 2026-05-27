<template>
  <v-dialog
    v-model="$store.state.disinfection.dialog.lstm.visible"
    width="824"
  >
    <div class="popup-main">
      <div class="user-modify-container"><img class="imgposition" src="@/assets/dialog/exclamationmark.png" alt="LSTM">LSTM</div>
      <div class="modify-contents">
        <div class="text">연속된 층 Layer에서 점진적으로 의미있는 표현을 배우는 데 강점이 있으며, 데이터로부터 표현을 학습하는 새로운 방식의 머신러닝 알고리즘</div>
        <div class="img">
          <div class="imgtext1">Input layer</div>
          <div class="imgtext2">Hidden layer 1</div>
          <div class="imgtext3">Hidden layer N</div>
          <div class="imgtext4">Output layer</div>
        </div>
      </div>
      <!-- <div class="btn-confirm-box">
          <div class="cancle-box" @click="closeAddUserDialog">
            취소
          </div>
          <div class="confirm-box" @click="addUser">
            확인
          </div>
      </div> -->
    </div>
  </v-dialog>
</template>
<script>
import { SET_OVERLAY } from '@/store'
export default {
  methods: {
    addUser: function () {
      let obj = {}
      obj.usr_id = this.$store.state.addUserDialog.usr_id
      obj.usr_nm = this.$store.state.addUserDialog.usr_nm
      obj.usr_pw = this.$store.state.addUserDialog.usr_pw
      obj.usr_pn = this.$store.state.addUserDialog.usr_pn
      obj.usr_auth = 0
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
    }
  }
}
</script>
<style scoped>
.popup-main{
  /* width: 824px; */
  height: 628px;
  /* margin: auto; */
  border-radius: 10px;
  background-color: #0f3272;
  padding: 11px 0px 14px 0;
}
.user-modify-container {
 font-size: 24px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2;
  letter-spacing: normal;
  text-align: left;
  color: #ffffff;
  height: 62px;
  padding: 7px 0 0 19.7px;
  background-image: linear-gradient(to right, rgba(0, 9, 81, 0.56), rgba(0, 7, 79, 0));
}
.modify-contents {
  font-size: 15px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: left;
  color: #ffffff;
  line-height: 2.4;
  padding:18px 12.3px
}
.imgposition{
  vertical-align: sub;
  margin-right: 10px;
}
.img{
  width: 746px;
  height: 325px;
  background-image: url('../../assets/dialog/LSTM.png');
  margin: auto;
}
.imgtext1 {
  position: absolute;
  top: 720px;
    right: 1254px;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.63;
    letter-spacing: normal;
    text-align: left;
    color: #ffffff;
}
.imgtext2 {
  position: absolute;
  top: 764px;
    right: 1019px;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.63;
    letter-spacing: normal;
    text-align: left;
    color: #ffffff;
}
.imgtext3 {
  position: absolute;
    position: absolute;
  top: 764px;
    right: 800px;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.63;
    letter-spacing: normal;
    text-align: left;
    color: #ffffff;
}
.imgtext4 {
  position: absolute;
    top: 650px;
    right: 590px;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.63;
    letter-spacing: normal;
    text-align: left;
    color: #ffffff;
}
.text {
  font-size: 18px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.56;
  letter-spacing: normal;
  text-align: left;
  color: #ffffff;
  padding-bottom: 60px;
}
.btn-confirm-box {
  display: flex;
  width: 824px;
  height: 61px;
  margin: 29px 0 0;
  /* padding: 26.5px 501px 0.5px 4.6px; */
  background-image: linear-gradient(to left, rgba(0, 9, 81, 0.56), rgba(0, 7, 79, 0));
}
.confirm-box {
    width: 94px;
    height: 34px;
    background-color: #0e81f4;
    color: #fff;
    margin: auto 24 auto auto;
    margin: auto 0;
    margin-right: 24px;
    text-align: center;
    line-height: 2;
}
.cancle-box {
  width: 94px;
  height: 34px;
  background-color: #3bb7e5;
  color: #fff;
  text-align: center;
  margin-left: auto;
  margin: auto 8px auto auto;
  line-height: 2;
}
</style>