<template>
    <v-dialog
      v-model="$store.state.showConfirmDialog"
      max-width="352"
      >
      <div class="container">
        <div class="inner_container">
          <div style="display: flex; flex-flow: column;">
            <div class="mark"></div>
            <div class="alarm_text">{{message}}</div>
            <div class="button_container">
              <v-btn
                class="font-weight-bold"
                depressed color="#0000"
                style="width: 94px; background-color: #5879FB; color:#fff; margin-top:26px;"
                @click="$store.stateshowConfirmDialog=false">
                <v-icon class="ok mr-2"></v-icon>
                확 인
              </v-btn>
            </div>
          </div>
        </div>
      </div>
    </v-dialog>
</template>

<script>
export default {
  name: 'ConfirmDialog',

  props: [
    'message',
    'confirmType'
  ]
  ,
  data: () => ({

  }),
  methods: {
    onConfirm: function () {
      this.$emit('confirmResult', { confirmType: this.confirmType })
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

<style scoped>
  .container {
    width: 352px;
    border-radius: 3px;
    box-shadow: 0 3px 10px 0 rgba(14, 38, 90, 0.64);
    background-color: rgba(29, 62, 106, 0.6) !important;
    padding-top: 15px;
    padding-bottom: 15px;
    padding-left: 17px;
    padding-right: 17px;
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
    width: 27x;
    height: 24px;
    background-size: auto;
    background-position: center;
    background-repeat: no-repeat;
    background-image: url('../../assets/dialog/icon_alert.png');
  }
  .alarm_text {
    display: flex;
    color: #fff;
    margin-top: 22px;
    align-items: center;
    justify-content: center;
    padding: 0px 10px;
  }
  .button_container {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 28px;
  }
  .ok {
    width: 13px;
    height: 13px;
    background-size: auto;
    background-position: center;
    background-repeat: no-repeat;
    background-image: url('../../assets/userDialog/ok.png');
  }
</style>
