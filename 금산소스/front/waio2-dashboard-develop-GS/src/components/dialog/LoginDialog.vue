<template>
    <v-dialog
      v-model="$store.state.loginDialog.visible"
      fullscreen
      hide-overlay
      >
        <div class="login-background">
          <div class="close-icon-login" @click="closeDialog"></div>
          <div class="login-left-img"></div>
          <div class="main">
            <div class="login-img">
            </div>
            <div class="login-title">
              스마트 정수장 AI 플랫폼
            </div>
            <!-- <div id="kwater_container">
              <div class='cube'>
                <div class="front_logo"></div>
                <div :class="backgroundLogo"></div>
              </div>
            </div> -->
            <!-- <div id="plant"></div> -->
            <div class="login">
              <v-form
                id="form"
                ref="form"
                autocomplete="off"
                @submit.prevent="onSubmit($store.state.loginDialog.usr_id, $store.state.loginDialog.usr_pw)"
                >
                <v-row no-gutters>
                  <v-col lg="12">
                    <v-text-field
                      id="usr_id"
                      label="아이디"
                      name="usr_id"
                      type="text"
                      height="42"
                      v-model="$store.state.loginDialog.usr_id"
                      hide-details
                      required
                      dense
                      flat
                      class="custom-label-color"
                      style="margin-bottom: 5px;"
                    ></v-text-field>
                    <div style="height:10px"></div>
                    <v-text-field
                      id="usr_pw"
                      label="비밀번호"
                      name="usr_pw"
                      type="password"
                      height="42"
                      v-model="$store.state.loginDialog.usr_pw"
                      hide-details
                      required
                      dense
                      flat
                      class="custom-label-color"
                      color="white"
                      style="margin-bottom: 15px;"
                    ></v-text-field>
                  </v-col>
                </v-row>
                <v-row no-gutters>
                  <v-col lg="12">
                    <v-btn type="submit" color="#1c3482" style="height:50px; width:100%; color:white; font-size:16px;">로그인</v-btn>
                  </v-col>
                </v-row>
              </v-form>
            </div>
          </div>
      </div>
    </v-dialog>
</template>

<script>
import { SET_OVERLAY } from '@/store'

export default {
  name: 'LoginDialog',
  data: () => ({
  }),
  computed: {
  },
  methods: {
    onSubmit: function (usr_id, usr_pw) {
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('login/LOGIN_POST', { usr_id, usr_pw })
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    },
    closeDialog: function () {
      this.$store.dispatch('loginDialog/CLOSE_DIALOG')
    }
  },
  created: function () {
    console.log(this.$options.name + ' created')
  },
  mounted: function () {
    console.log(this.$options.name + ' mounted')
  },
  destroyed: function () {
    console.log(this.$options.name + ' destoryed')
  },
  updated: function () {
    console.log(this.$options.name + ' updated')
  }
}
</script>

<style lang="scss" scoped>
  .login-background {
    display:flex;
    width: 100%;
    height: 100%;
    background-color: rgb(228, 236, 242);
    background-image: url('../../assets/background.png');
    background-size: 100%;
    z-index: 1000;
    overflow: hidden;
  }
  .login-left-img {
    position: absolute;
    left: 213px;
    top: 225px;
    width: 718px;
    height: 628px;
    background-image: url('../../assets/login/login_left.png');
    background-size: 100%;
  }
  .main {
    width: 330px;
    margin: auto 400px auto auto;
  }
  .login-img {
    width: 144px;
    height: 94px;
    margin: auto;
    margin-bottom: 25px;
    background-image: url('../../assets/login/login_img.png');
    background-size: 100%;
  }
  .login-title {
    font-size: 25px;
    font-weight: bold;
    color: #fff;
    text-align: center;
  }
  #form {
    margin: auto;
    // width: 320px;
  }
  .custom-label-color input{
    color: white !important;
  }
  .close-icon-login {
    width: 32px;
    height: 32px;
    position: absolute;
    right: 42px;
    top: 42px;
    border-radius: 0px !important;
    background-size: 100% 100%;
    background-position: center;
    background-repeat: no-repeat;
    background-image: url('../../assets/login/close_icon.png');
    cursor:pointer;
  }
</style>
