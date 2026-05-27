<template>
    <v-dialog
      v-model="$store.state.alertDialog.visible"
      persistent
      max-width="352"
      >
      <div class="outter_container">
        <div style="display: flex; flex-flow: column;">
          <!-- <div class="mark"></div> -->
          <div class="alarm_title">{{ this.$store.state.alertDialog.title }}</div>
          <div v-if="this.$store.state.alertDialog.text1 !== null" class="alarm_text">{{ this.$store.state.alertDialog.text1 }}</div>
          <div v-if="this.$store.state.alertDialog.text2 !== null" class="alarm_text">{{ this.$store.state.alertDialog.text2 }}</div>
          <div v-if="this.$store.state.alertDialog.text3 !== null" class="alarm_text">{{ this.$store.state.alertDialog.text3 }}</div>
          <div class="button_container">
            <div
              class="btn btn--change"
              @click="closeDialog">
              <!-- <v-icon class="ok mr-2"></v-icon> -->
              확 인
            </div>
          </div>
        </div>
      </div>
    </v-dialog>
</template>

<script>
export default {
  name: 'AlertDialog',

  data: () => ({
  }),
  methods: {
    closeDialog: function () {
      this.$store.commit('alertDialog/CLOSE_DIALOG')
    }
  },
  created: function () {
    // console.log(this.$options.name + ' created')
  },
  mounted: function () {
    // console.log(this.$options.name + ' mounted')
  },
  destroyed: function () {
    // console.log(this.$options.name + ' destoryed')
  },
  updated: function () {
    // console.log(this.$options.name + ' updated')
  }
}
</script>

<style lang="scss" scoped>
  .outter_container {

    /* border-radius: 10px; */
    /* background-image: linear-gradient(to bottom, #04175d, #73e8fc); */
    /* background-color: rgb(19, 47, 78); */
    padding: 10px 0px;
    background-image: url('../../assets/dialog/background.png');
    background-size: 100% 100%;
  }
  .inner_container {
    width: 319px;
    height: 100%;
    border-radius: 3px;
    box-shadow: 0 3px 10px 0 rgba(14, 38, 90, 0.64);
    background-color: #1d386a;
  }
  .mark {
    margin-top: 22px;
    width: 26x;
    height: 23px;
    background-size: auto;
    background-position: center;
    background-repeat: no-repeat;
    background-image: url('../../assets/dialog/icon_alert.png');
  }
  .alarm_title {
    display: flex;
    font-size: 24px;
    // text-shadow: 0 0 9px #5cafff;
    color: #b4dffb;
    padding: 20px 0 20px 0;
    align-items: center;
    justify-content: center;
    /* padding: 0px 10px; */
  }
  // .alarm_title:before {
  //   content: "";
  //   display: block;
  //   width: 26px;
  //   height: 23px;
  //   float: left;
  //   background: url('../../assets/dialog/icon_alert.png') no-repeat;
  //   float: left;
  //   margin: 0 6px 0 0;
  // }
  .alarm_text {
    display: flex;
    font-size: 18px;
    text-align: center;
    // text-shadow: 0 0 9px #5cafff;
    color: #fff;
    align-items: center;
    justify-content: center;
    padding: 0px 35px;
  }
  .button_container {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 20px 0px;
  }
  .ok {
    width: 13px;
    height: 13px;
    background-size: auto;
    background-position: center;
    background-repeat: no-repeat;
    /* background-image: url('../../assets/userDialog/ok.png'); */
  }
  .btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 105px;
    height: 34px;
    font-size: 15px;
    color: white;
    margin: 0px 10px;
    cursor: pointer;
    &--cancel {
      border: solid 1px #b4dffa;
      background-color: rgba(185, 255, 250, 0.25);
    }
    &--change {
      border: solid 1px #b4dffa;
      background-color: rgba(139, 194, 240, 0.25);
    }
  }
</style>
