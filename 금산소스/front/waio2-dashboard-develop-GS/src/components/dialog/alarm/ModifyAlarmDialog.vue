<template>
  <v-dialog
    v-model="$store.state.modifyAlarmDialog.visible"
    persistent
    max-width="420px"
  >
    <div class="popup-main">
      <div class="user-modify-container">알람 설정</div>
      <div class="modify-contents">
        <div class="modify-data-box">
          <div class="modify-text"> · 아이디</div> 
          <input class="input-box" type="text" v-model="$store.state.modifyAlarmDialog.alm_id" disabled>
        </div>
        <div class="modify-data-box">
          <div class="modify-text"> · 표시명</div> 
          <input class="department-input-box" type="text" v-model="$store.state.modifyAlarmDialog.dp_nm">
        </div>
        <div v-if="$store.state.modifyAlarmDialog.cmp_val !== 'false' && $store.state.modifyAlarmDialog.cmp_val !== 'control'" class="modify-department">
          <div class="modify-text"> · 비교값</div> 
          <input class="input-box" type="text" v-model="$store.state.modifyAlarmDialog.cmp_val">
          <!-- <input v-else class="input-box" type="text" v-model="$store.state.modifyAlarmDialog.cmp_val"> -->
        </div>
        <div class="modify-department">
          <div class="modify-text"> · IWSP 전송 여부</div>
          <div class='table_title_checkbox_container'>
            <div class='radio_item_container'><input type='radio' id='radio1' value='true' v-model='$store.state.modifyAlarmDialog.scd_snd'><label for='radio1' class="radio_label">true</label></div>
            <div class='radio_item_container'><input type='radio' id='radio2' value='false' v-model='$store.state.modifyAlarmDialog.scd_snd'><label for='radio2' class="radio_label">false</label></div>
          </div>
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
  name: 'ModifyAlarmDialog',
  data: () => ({
  }),
  methods: {
    modifyUser: function () {
      let obj = {}
      obj.seq = this.$store.state.modifyAlarmDialog.seq
      obj.alm_id = this.$store.state.modifyAlarmDialog.alm_id
      obj.dp_nm = this.$store.state.modifyAlarmDialog.dp_nm
      obj.cmp_val = this.$store.state.modifyAlarmDialog.cmp_val
      obj.scd_snd = this.$store.state.modifyAlarmDialog.scd_snd
      console.log(this.$store.state.modifyAlarmDialog.dp_nm)
      if (this.$store.state.modifyAlarmDialog.dp_nm === null | this.$store.state.modifyAlarmDialog.dp_nm === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '표시명을 입력해주세요' })
        return
      }
      if (this.$store.state.modifyAlarmDialog.cmp_val === null | this.$store.state.modifyAlarmDialog.cmp_val === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
        return
      }
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('alarm/PUT_ALARM', obj)    
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    },
    closeDialog: function () {
      this.$store.dispatch('modifyAlarmDialog/CLOSE_DIALOG')
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
  padding: 7px 0 0 64.7px;
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
  color: #fff;
  margin-left: 25px;
}
.cancel-box:hover {
  background-color: #132357;
}
.table_title_checkbox_container {
  display: flex;
  align-items: center;
  flex-flow: row;
}
input[type='radio'] {
  cursor: pointer;
}
.radio_item_container {
  margin-right: 30px;
  cursor: pointer;
}
.radio_label {
  margin-left: 5px;
  cursor: pointer;
}
</style>