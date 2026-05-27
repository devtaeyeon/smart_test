<template>
  <v-dialog
    v-model="$store.state.deleteUserDialog.visible"
    persistent
    max-width="420px"
    >
    <div class="popup-main">
      <div class="user-modify-container">사용자 삭제</div>
      <div class="modify-contents">
        사용자 {{ $store.state.deleteUserDialog.usr_id }}을(를) 삭제하시겠습니까?
        <div class="btn-confirm-box">
          <button class="confirm-box" @click="deleteUser">
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
    deleteUser: function () {
      let obj = {}
      obj.usr_id = this.$store.state.deleteUserDialog.usr_id
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('user/DELETE_USER', obj)  
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    },
    closeDialog: function () {
      this.$store.dispatch('deleteUserDialog/CLOSE_DIALOG')
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
  padding: 7px 0 0 54.7px;
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
  border-bottom: solid 1px #87f4f4;
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