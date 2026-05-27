<template>
  <div class="main">
    <div class="water-v-flow-one-img three">
      <div class="buble-v delay1"></div>
      <div class="buble-v delay2"></div>
      <div class="buble-v delay3"></div>
    </div>
    <!-- <div class="btn-tab">
      <div class=" btn-tab__box-abled">생활</div>
      <div class=" btn-tab__box-abled">공업</div>
    </div> -->
    <div class="contents-img">
      <p class="unit-title unit-one">1호기</p>     
      <!-- 1호기 물 흐름 -->             
      <div :style="{display : this.getDisplayStylePump1}" class="apac-line">
        <div class="water-flow-vertical one-line-v1">
          <div class="buble-v delay1"></div>
          <div class="buble-v delay2"></div>
        </div>
        <div class="water-big-right-horizontal">
          <div class="buble delay1"></div>
          <div class="buble delay2"></div>
          <div class="buble delay3"></div>
          <div class="buble delay4"></div>
        </div>
        <div class="water-flow-vertical one-line-v3">
          <div class="buble-v delay3"></div>
          <div class="buble-v delay4"></div>
        </div>
      </div>
      <p class="unit-title unit-two">2호기</p>      
      <!-- 2호기 물 흐름 -->
      <div :style="{display : this.getDisplayStylePump2}" class="polymax-line">
        <div class="water-flow-vertical one-line-v2">
          <div class="buble-v delay1"></div>
          <div class="buble-v delay2"></div>
        </div>
        <div class="water-big-left-horizontal">
          <div class="buble delay1"></div>
          <div class="buble delay2"></div>
          <div class="buble delay3"></div>
          <div class="buble delay4"></div>
        </div>
        <div class="water-flow-vertical one-line-v4">
          <div class="buble-v delay3"></div>
          <div class="buble-v delay4"></div>
        </div>
      </div>        
    </div>
    <div class="contents-wrap">
      <div class="contents-value">
        <div class="contents-value__row">
          <div class="contents-value__title">주입률상한</div>
          <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_post_set_max" v-on:input="updateInput($event, 'g_post_set_max')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_post_set_max | numFormat('0.00') }}</span></div>
        </div>
        <div class="contents-value__row">
          <div class="contents-value__title">주입률하한</div>
          <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_post_set_min" v-on:input="updateInput($event, 'g_post_set_min')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_post_set_min | numFormat('0.00') }}</span></div>
        </div>
        <div class="contents-value__row">
          <div class="contents-value__title">1회 변경 주입률</div>
          <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_post_chg_limit_for_onetime" v-on:input="updateInput($event, 'g_post_chg_limit_for_onetime')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_post_chg_limit_for_onetime | numFormat('0.00') }}</span></div>
        </div>
        <div class="contents-value__row">
          <div class="contents-value__title">정수지 유입<br/>목표 잔류염소</div>
          <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_h_in_obj_residual_cl" v-on:input="updateInput($event, 'g_h_in_obj_residual_cl')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_h_in_obj_residual_cl | numFormat('0.00') }}</span></div>
        </div>
        <div class="contents-value__row">
          <div class="contents-value__title" style="width: 160px;">주입률 변경 후 잔류염소<br/>미변동 시 대기시간</div>
          <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_post_chol_rate_holding_time" v-on:input="updateInput($event, 'g_post_chol_rate_holding_time')" maxlength="5"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_post_chol_rate_holding_time | numFormat('0') }}</span></div> 
          </div>
        <div class="contents-value__row">
          <div class="contents-value__title">정수지 유입<br/>잔류염소 홀딩범위</div>
          <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_h_in_residual_cl_holding" v-on:input="updateInput($event, 'g_h_in_residual_cl_holding')" maxlength="5"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_h_in_residual_cl_holding | numFormat('0.00') }}</span></div>
        </div>
        <div class="contents-value__row">
          <div class="contents-value__title">보정주기</div>
          <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_post_calib_cycle" v-on:input="updateInput($event, 'g_post_calib_cycle')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_post_calib_cycle | numFormat('0') }}</span></div> 
        </div>
        <div class="contents-value__row">
          <div class="contents-value__title">주입 후 경과 시간</div>
          <div class="contents-value__number"><span>{{ this.$store.state.disinfection.latest.g_elapsed_time }}</span></div>
        </div>
      </div>
      <div class="contents-value-icon" v-if="$store.state.login.user.tkn !== null">
        <div class="custom-icon" @click="updateControl">
          <div :class="[ this.$store.state.disinfection.isModifyMode ? 'custom-icon__checkbox' : 'custom-icon__pencil' ]"></div>
        </div>
        <div v-if="this.$store.state.disinfection.isModifyMode" class="custom-cancel-icon" style="margin-top: 3px;" @click="cancelControl">
          <div class='custom-cancel-icon__cancel'></div>
        </div>
      </div>
    </div>     
    <!-- <div class="contents-value">
      <div v-if="$store.state.login.user.tkn !== null" class="contents-value__icon">
        <div class="custom-icon" @click="updateControl">
          <div :class="[ this.$store.state.disinfection.isModifyMode ? 'custom-icon__checkbox' : 'custom-icon__pencil' ]"></div>
        </div>
        <div v-if="this.$store.state.disinfection.isModifyMode" class="custom-cancel-icon" style="margin-top: 3px;" @click="cancelControl">
          <div class='custom-cancel-icon__cancel'></div>
        </div>
      </div>
      <div class="contents-value__row">
        <div class="contents-value__title">주입률 상한</div>
        <div class="contents-value__title">주입률 하한</div>
        <div class="contents-value__title">1회 변경<br/>주입률</div>
        <div class="contents-value__title">보정주기</div>
        <div class="contents-value__title">정수지 유입<br/>목표 잔류염소</div>
        <div class="contents-value__title">주입 후<br/>경과 시간</div>
      </div>
      <div class="contents-value__row">
        <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_post_set_max" v-on:input="updateInput($event, 'g_post_set_max')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_post_set_max | numFormat('0.00') }}</span></div>
        <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_post_set_min" v-on:input="updateInput($event, 'g_post_set_min')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_post_set_min | numFormat('0.00') }}</span></div>
        <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_post_chg_limit_for_onetime" v-on:input="updateInput($event, 'g_post_chg_limit_for_onetime')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_post_chg_limit_for_onetime | numFormat('0.00') }}</span></div>
        <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_post_calib_cycle" v-on:input="updateInput($event, 'g_post_calib_cycle')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_post_calib_cycle | numFormat('0') }}</span></div> 
        <div class="contents-value__number"><input v-if="this.$store.state.disinfection.isModifyMode" type="text" :value="this.$store.state.disinfection.latestModify.g_h_in_obj_residual_cl" v-on:input="updateInput($event, 'g_h_in_obj_residual_cl')"/><span v-else>{{ this.$store.state.disinfection.latestModify.g_h_in_obj_residual_cl | numFormat('0.00') }}</span></div>
        <div class="contents-value__number"><span>{{ this.$store.state.disinfection.latest.g_elapsed_time }}</span></div>
      </div>
    </div> -->
  </div>  
</template>
<script>
import { PUT_DISINFECTION_CONTROL_POST } from '@/store/modules/disinfection'
export default {
  data: () => ({
    // isModifyMode: false
  }),
  computed:{
    getDisplayStylePump1(){
      if (this.$store.state.disinfection.latest.g_pump_1_run == 1) {
          return 'block'
        }
      return 'none'
    },
    getDisplayStylePump2(){
      if (this.$store.state.disinfection.latest.g_pump_2_run == 1) {
          return 'block'
        }
      return 'none'
    },
  },
  methods: {
    updateInput: function (event, key) {
      this.$store.state.disinfection.latestModify[key] = event.target.value
    },
    checkNumberFormat: function(val) {
      const regex = /^\d{1,2}([\.](\d{1,2})?)?$/ //eslint-disable-line
      if (!regex.test(val)) {
        return false		// 0.00~99.99를 벗어나면
      }
      return true
    },
    updateControl: function() {
      let post_min_limit = 0
      let post_max_limit = 100
      let cl_min_limit = 0
      let cl_max_limit = 10
      let cycle_min_limit = 0
      let cycle_max_limit = 60
      if (this.$store.state.disinfection.isModifyMode) {
        if (this.$store.state.disinfection.latestModify.g_post_set_max === ''
           || this.$store.state.disinfection.latestModify.g_post_set_min === ''
           || this.$store.state.disinfection.latestModify.g_post_chg_limit_for_onetime === ''
           || this.$store.state.disinfection.latestModify.g_post_calib_cycle === ''
           || this.$store.state.disinfection.latestModify.g_h_in_obj_residual_cl === ''
           || this.$store.state.disinfection.latestModify.g_h_in_residual_cl_holding === ''
           || this.$store.state.disinfection.latestModify.g_post_chol_rate_holding_time === '') {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
        } else {
          let obj = {}
          obj.g_post_set_max = parseFloat(this.$store.state.disinfection.latestModify.g_post_set_max)
          obj.g_post_set_min = parseFloat(this.$store.state.disinfection.latestModify.g_post_set_min)
          obj.g_post_chg_limit_for_onetime = parseFloat(this.$store.state.disinfection.latestModify.g_post_chg_limit_for_onetime)
          obj.g_post_calib_cycle = this.$store.state.disinfection.latestModify.g_post_calib_cycle
          obj.g_h_in_obj_residual_cl = parseFloat(this.$store.state.disinfection.latestModify.g_h_in_obj_residual_cl)
          obj.g_h_in_residual_cl_holding = parseFloat(this.$store.state.disinfection.latestModify.g_h_in_residual_cl_holding)
          obj.g_post_chol_rate_holding_time = this.$store.state.disinfection.latestModify.g_post_chol_rate_holding_time
          if ((obj.g_post_set_max < post_min_limit || obj.g_post_set_max > post_max_limit)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '주입률 상한 설정 범위', text2: post_min_limit + ' ~ ' + post_max_limit})
            return
          }
          if ((obj.g_post_set_min < post_min_limit || obj.g_post_set_min > post_max_limit)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '주입률 하한 설정 범위', text2: post_min_limit + ' ~ ' + post_max_limit})
            return
          }
          if ((obj.g_post_chg_limit_for_onetime < post_min_limit || obj.g_post_chg_limit_for_onetime > post_max_limit)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '1회 변경 주입률 설정 범위', text2: post_min_limit + ' ~ ' + post_max_limit})
            return
          }
          if ((obj.g_h_in_obj_residual_cl < cl_min_limit || obj.g_h_in_obj_residual_cl > cl_max_limit)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '정수지 유입 목표 잔류염소 설정 범위', text2: cl_min_limit + ' ~ ' + cl_max_limit})
            return
          }
          if ((obj.g_post_calib_cycle < cycle_min_limit || obj.g_post_calib_cycle > cycle_max_limit)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '보정 주기 설정 범위', text2: cycle_min_limit + ' ~ ' + cycle_max_limit})
            return
          }
          this.$store.dispatch(PUT_DISINFECTION_CONTROL_POST, obj)
          this.$store.state.disinfection.isModifyMode = !this.$store.state.disinfection.isModifyMode
        }
      } else {
        this.$store.state.disinfection.isModifyMode = !this.$store.state.disinfection.isModifyMode
      }
    },
    cancelControl: function() {
      this.$store.state.disinfection.latestModify = Object.assign({}, this.$store.state.disinfection.latest)
      this.$store.state.disinfection.isModifyMode = !this.$store.state.disinfection.isModifyMode
    }
  },
  watch: {
    // '$store.state.disinfection.latestModify.g_post_set_max': function(newVal, oldVal) {
    //   if (newVal === '') {
    //     this.$store.state.disinfection.latestModify['g_post_set_max'] = newVal
    //   } else {
    //     if (this.checkNumberFormat(newVal)) {
    //       this.$store.state.disinfection.latestModify['g_post_set_max'] = newVal
    //     } else {
    //       this.$store.state.disinfection.latestModify['g_post_set_max'] = oldVal
    //     }
    //   }
    // },
    // '$store.state.disinfection.latestModify.g_post_set_min': function(newVal, oldVal) {
    //   if (newVal === '') {
    //     this.$store.state.disinfection.latestModify['g_post_set_min'] = newVal
    //   } else {
    //     if (this.checkNumberFormat(newVal)) {
    //       this.$store.state.disinfection.latestModify['g_post_set_min'] = newVal
    //     } else {
    //       this.$store.state.disinfection.latestModify['g_post_set_min'] = oldVal
    //     }
    //   }
    // },
    // '$store.state.disinfection.latestModify.g_post_chg_limit_for_onetime': function(newVal, oldVal) {
    //   if (newVal === '') {
    //     this.$store.state.disinfection.latestModify['g_post_chg_limit_for_onetime'] = newVal
    //   } else {
    //     if (this.checkNumberFormat(newVal)) {
    //       this.$store.state.disinfection.latestModify['g_post_chg_limit_for_onetime'] = newVal
    //     } else {
    //       this.$store.state.disinfection.latestModify['g_post_chg_limit_for_onetime'] = oldVal
    //     }
    //   }
    // },
    // '$store.state.disinfection.latestModify.g_post_calib_cycle': function(newVal, oldVal) {
    //   if (newVal === '') {
    //     this.$store.state.disinfection.latestModify['g_post_calib_cycle'] = newVal
    //   } else {
    //     if (this.checkNumberFormat(newVal)) {
    //       this.$store.state.disinfection.latestModify['g_post_calib_cycle'] = newVal
    //     } else {
    //       this.$store.state.disinfection.latestModify['g_post_calib_cycle'] = oldVal
    //     }
    //   }
    // },
    // '$store.state.disinfection.latestModify.g_h_in_obj_residual_cl': function(newVal, oldVal) {
    //   if (newVal === '') {
    //     this.$store.state.disinfection.latestModify['g_h_in_obj_residual_cl'] = newVal
    //   } else {
    //     if (this.checkNumberFormat(newVal)) {
    //       this.$store.state.disinfection.latestModify['g_h_in_obj_residual_cl'] = newVal
    //     } else {
    //       this.$store.state.disinfection.latestModify['g_h_in_obj_residual_cl'] = oldVal
    //     }
    //   }
    // },
  }
}
</script>
<style lang="scss" scoped>
.main{
  display: flex;
  align-items: center;
  flex-flow: column;
  width: 592px;
  height: 100%;
.three{
  top: 0px;
  left: 1318px;
}
.water-v-flow-one-img{
  position: absolute;
  width: 6px;
  height: 50px;
  .buble-v {
    position: absolute;
    width: 6px;
    height: 41px;
    background-image: url('../../assets/disinfection/v_water_flow.png');
    background-position:50% -28px;
    animation-name: arrow-two;
    animation-duration: 4s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 2s;
  }
  .delay3 {
    animation-delay: 4s;
  }
}
@keyframes arrow-two{ 
  0% {opacity:0; transform: translateY(0px);}
  20% {opacity:0; }
  90% {opacity:1; }
  100% {opacity:0; transform: translateY(50px);}
}  
  .contents-wrap {
    position: relative;
  }   
  .contents-value {
    display: flex;
    width: 560px;
    overflow-x: auto;
    padding: 20px 0;
    background-image: url('../../assets/disinfection/contents_right_bottom.png');
    text-align: center;
    &__row {
      position: relative;
      margin: 0 5px;
      > div {
        width: 130px;
        height: 30px;
        box-sizing: border-box;
        > input {
          width: 100%;
          height: 30px;
          text-align: center;
          border: solid 1px rgba(157, 191, 255, 0.5);
          background-color: rgba(157, 191, 255, 0.07);
          font-family: inherit !important;
          color: inherit !important;
        }
      }
    }
    &__title {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      font-size: 14px;
      text-shadow: 0 0 9px #5cafff;
      font-family: KHNPHUotfR;
      color: #fff;
      margin: 10px 0 25px 0;
    }
    &__line {
      width: 74px;
      height: 24px;
      display: flex;
      justify-content: flex-end;
      align-items: flex-end;
      text-shadow: 0 0 9px #5cafff;
      font-family: KHNPHUotfR;
      font-size: 16px;
      color: #9fc4ff;
      margin: 4px 8px;
    }
    &__number {
      display: flex;
      justify-content: center;
      align-items: center;
      text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
      font-family: "LAB디지털" !important;
      font-size: 24px;
      color: #ccf1ff;
      > span {
        font-family: "LAB디지털" !important;
      }
    }
    .under-line{
      height: 7px;
      margin: 10px 0;
      background-image: url('../../assets/disinfection/mixed_water_under_line.png');
      background-position: 145px;
      object-fit: contain;
    }
    .mixed-water-data{
      display: flex;
      justify-content: center;
      align-items:center ;
      &__num{
        margin:0 23px;
        text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
        font-family: "LAB디지털" !important;
        font-size: 24px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.21;
        letter-spacing: normal;
        text-align: center;
        color: #ccf1ff;
      }
      &__text{
        width: 92px;
        height: 34px;
        text-shadow: 0 0 9px #5cafff;
        font-family: "KHNPHUotfR";
        font-size: 16px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1;
        letter-spacing: normal;
        text-align: center;
        color: #fff;
      }
    }
  }
  .contents-value::-webkit-scrollbar {
    height: 10px;
  }
  .contents-value::-webkit-scrollbar-track {
    background-color: #011527;
    border-radius: 5px;
  }
  .contents-value::-webkit-scrollbar-thumb {
    background-color: #417db9;
    border-radius: 5px;
  }
  .contents-value-icon {
    position: absolute;
    top: 5px;
    right: -25px;
    cursor: pointer;
    z-index: 20;
  }
  .btn-tab{
    display: flex;
    justify-content:space-evenly;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2.3;
    letter-spacing: normal;
    text-align: center;
    color: #fff;
    width: 260px;
    .box-center-margin{
      // margin: 0 16px;
    }
    &__box-abled{
      width: 120px;
      height: 35px;
      box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
      border: solid 1px #72a3d6;
      background-color: #447fbc;
      color: #fff;
      line-height: 35px;
      cursor: pointer;
    }
    &__box-disabled{
      width: 120px;
      height: 35px;
      border: solid 1px #457fbc;
      background-color: #1a3462;
      color: #a7c2e7;
      line-height: 35px;
      cursor: pointer;
    }
  }
  .contents-img{
    width: 100%;
    height: 527px;
    background-image: url('../../assets/gs_images/sub_04.png');
    background-position-x: center;
    margin: 15px 0;
    position: relative;
    .apac-line{
      position: absolute;
      top: 0px;
      left: 41px;
    }
    .polymax-line{
      position: absolute;
      top: 0px;
      left: 337px;
    }    
    // 타이틀 추가
    .unit-title {
      color: #fff;
      display: inline-block;
      position: absolute;
    }
    .unit-one {
      top: 12px;
      left: 135px;
    }
    .unit-two {
      top: 12px;
      left: 420px;
    }    
    .one-line-h1{
      top: 491px;
      left: 163px;
    }
    .one-line-h2{
      top: 491px;
      left: 106px;
    }
    .one-line-h3{
      top: 610px;
      left: 183px;
    }
    .one-line-h4{
      top: 610px;
      left: 82px;
    }
    .one-line-v1{
      top: 243px;
      left: 110px;
    }
    .one-line-v2{
      top: 243px;
      left: 98px;
    }
    .one-line-v3{
      top: 288px;
      left: 251px;
    }
    .one-line-v4{
      top: 288px;
      left: -45px;
    }
    .two-line-h{
      top: 491px;
      left: 480px;
    }
    .two-line-v1{
      top: 440px;
      left: 571px;
    }
    .two-line-v2{
      top: 490px;
      left: 460px;
    }
    // 좌측 가로 물흐름
    .water-right-horizontal{
      position: absolute;
      width: 100px;
      height: 8px;
      .buble {
        position: absolute;
        width: 100px;
        height: 8px;
        background-image: url('../../assets/disinfection/water_h_flow_one.png');
        background-position: -38px 50%;
        animation-name: arrow-one;
        animation-duration: 5s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 0s;
      }
      .delay2 {
        animation-delay: 1s;
      }
      .delay3 {
        animation-delay: 2s;
      }
    }
    @keyframes arrow-one{ 
      0% {opacity:0; transform: translateX(0px);}
      20% {opacity:0; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateX(80px);}
    }
    .water-big-right-horizontal{
      position: absolute;
      width: 100px;
      height: 8px;
      top: 293px;
      left: 112px;
      .buble {
        position: absolute;
        width: 100px;
        height: 8px;
        background-image: url('../../assets/disinfection/water_h_flow_one.png');
        background-position: -38px 50%;
        animation-name: big-arrow-one;
        animation-duration: 8s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 2s;
      }
      .delay2 {
        animation-delay: 4s;
      }
      .delay3 {
        animation-delay:6s;
      }
      .delay4 {
        animation-delay: 8s;
      }
    }
    @keyframes big-arrow-one{ 
      0% {opacity:0; transform: translateX(0px);}
      20% {opacity:1; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateX(125px);}
    }
    // 우측 가로 물흐름
    .water-left-horizontal{
      position: absolute;
      width: 100px;
      height: 8px;
      .buble {
        position: absolute;
        width: 100px;
        height: 8px;
        background-image: url('../../assets/disinfection/water_h_flow_two.png');
        background-position: 88px 50%;
        animation-name: arrow-three;
        animation-duration: 6s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 0s;
      }
      .delay2 {
        animation-delay: 1s;
      }
      .delay3 {
        animation-delay: 2s;
      }
    }
    @keyframes arrow-three{ 
      0% {opacity:0; transform: translateX(0px);}
      20% {opacity:0; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateX(-80px);}
    }
    .water-big-left-horizontal{
      position: absolute;
      width: 100px;
      height: 8px;
      top: 293px;
      left: 0;
      .buble {
        position: absolute;
        width: 100px;
        height: 8px;
        background-image: url('../../assets/disinfection/water_h_flow_two.png');
        background-position: 88px 50%;
        animation-name: big-arrow-two;
        animation-duration: 8s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 2s;
      }
      .delay2 {
        animation-delay: 4s;
      }
      .delay3 {
        animation-delay: 6s;
      }
      .delay4 {
        animation-delay: 8s;
      }
    }
    @keyframes big-arrow-two{ 
      0% {opacity:0; transform: translateX(0px);}
      20% {opacity:1; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateX(-137px);}
    }
    // 세로 물흐름
    .water-flow-vertical{
      position: absolute;
      width: 8px;
      height: 60px;
      .buble-v {
        position: absolute;
        width: 8px;
        height: 60px;
        background-image: url('../../assets/disinfection/water_v_flow_one.png');
        background-position:50% -20px;
        animation-name: arrow-two;
        animation-duration: 4s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
        opacity: 0;
      }
      .delay1 {
        animation-delay: 0s;
      }
      .delay2 {
        animation-delay: 2s;
      }
      .delay3 {
        animation-delay: 8s;
      }
      .delay4 {
       animation-delay: 10s;
      }
    }
    @keyframes arrow-two{ 
            0% {opacity:0; transform: translateY(0px);}
      10% {opacity:1; }
      90% {opacity:1; }
      100% {opacity:0; transform: translateY(50px);}
    }  
  }
}
.custom-icon {
  width: 24px;
  height: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background-color: #b4dffa;
  cursor: pointer;
  &__pencil {
    width: 15px;
    height: 15px;
    background-image: url('../../assets/disinfection/icon_pencil.png');
  }
  &__checkbox {
    width: 16px;
    height: 12px;
    background-image: url('../../assets/disinfection/icon_checkbox.png');
    background-position-x: 1px;
  }
}
.custom-cancel-icon {
  width: 24px;
  height: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background-color: #649fff;
  cursor: pointer;
  &__cancel {
    width: 16px;
    height: 16px;
    background-image: url('../../assets/disinfection/icon_cancel.png');
    
  }
}
</style>