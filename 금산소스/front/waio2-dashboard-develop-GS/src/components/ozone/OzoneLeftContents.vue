<template>
  <div class="main">
    <div class="contents-img">
      <div class="left-one-line-water-h-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="left-one-line-water-v-flow-2-top">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="left-one-line-water-v-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
        <div class="buble delay4"></div>
      </div>
      <div class="left-two-line-water-v-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
        <div class="buble delay4"></div>
      </div>
      <div class="left-one-line-water-h-flow-2-left">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="center-one-line-water-v-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="center-two-line-water-v-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="center-three-line-water-v-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="center-one-line-water-h-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="center-two-line-water-h-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="center-one-line-water-h-flow-2-right">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="center-two-line-water-h-flow-2-right">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="right-one-line-water-v-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
        <div class="buble delay4"></div>
      </div>
      <div class="right-two-line-water-v-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
        <div class="buble delay4"></div>
      </div>
      <div class="right-two-line-water-h-flow">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="right-one-line-water-h-flow-2-left">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="right-one-line-water-v-flow-2-top">
        <div class="buble delay1"></div>
        <div class="buble delay2"></div>
        <div class="buble delay3"></div>
      </div>
      <div class="box-type1-outter1">
        <div class="box-type1">
          <div class="box-type1__title">현재 주입률</div>
          <div class="box-type1__body">
            <div class="box-type1__value">{{ this.$store.state.ozone.latest.io_inr | numFormat('0.00') }}</div>
            <div class="box-type1__unit">ppm</div>
          </div>
        </div>
      </div>
      <div class="box-type1-outter2">
        <div class="box-type1">
          <div class="box-type1__title">용존 오존 농도</div>
          <div class="box-type1__body">
            <div class="box-type1__value">{{ this.$store.state.ozone.latest.io_de | numFormat('0.000') }}</div>
            <div class="box-type1__unit">ppm</div>
          </div>
        </div>
      </div>
      <div class="contents-img-box contents-img-box__one">
        <div class="contents-img-box__num">{{ this.$store.state.ozone.latest.io_pre1_de | numFormat('0.000') }}</div>
        <div class="contents-img-box__text"><span>ppm</span></div>
      </div>
      <div class="contents-img-box contents-img-box__two">
        <div class="contents-img-box__num">{{ this.$store.state.ozone.latest.io_peri1_de | numFormat('0.000') }}</div>
        <div class="contents-img-box__text"><span>ppm</span></div>
      </div>
      <div class="contents-img-box contents-img-box__three">
        <div class="contents-img-box__num">{{ this.$store.state.ozone.latest.io_post1_de | numFormat('0.000') }}</div>
        <div class="contents-img-box__text"><span>ppm</span></div>
      </div>
      <div class="contents-img-box contents-img-box__four">
        <div class="contents-img-box__num">{{ this.$store.state.ozone.latest.io_pre2_de | numFormat('0.000') }}</div>
        <div class="contents-img-box__text"><span>ppm</span></div>
      </div>
      <div class="contents-img-box contents-img-box__five">
        <div class="contents-img-box__num">{{ this.$store.state.ozone.latest.io_peri2_de | numFormat('0.000') }}</div>
        <div class="contents-img-box__text"><span>ppm</span></div>
      </div>
      <div class="contents-img-box contents-img-box__six">
        <div class="contents-img-box__num">{{ this.$store.state.ozone.latest.io_ser_de | numFormat('0.000') }}</div>
        <div class="contents-img-box__text"><span>ppm</span></div>
      </div>
    </div>
    <div class="box-control-outter">
      <div class="box-control">
        <div v-if="$store.state.login.user.tkn !== null" class="box-control__icon">
          <div class="custom-icon" @click="updateControl">
             <div :class="[ this.$store.state.ozone.isModifyMode ? 'custom-icon__checkbox' : 'custom-icon__pencil' ]"></div>
          </div>
          <div v-if="this.$store.state.ozone.isModifyMode" class="custom-cancel-icon" style="margin-top: 5px;" @click="cancelControl">
          <div class='custom-cancel-icon__cancel'></div>
        </div>
        </div>
        <div class="box-control__row1">
          <div class="box-control__text">
            · 주입률 기준 설정
          </div>
          <div class="box-control__number-outter">
            <div class="box-control__number">
              <input v-if="this.$store.state.ozone.isModifyMode" type="text" :value="this.$store.state.ozone.latestModify.io_set" v-on:input="updateInput($event, 'io_set')"/>
              <span v-else>{{ this.$store.state.ozone.latestModify.io_set | numFormat('0.00') }}</span>
            </div>
          </div>
          <div class="box-control__unit">
            ppm
          </div>
        </div>
        <div class="box-control__row1">
          <div class="box-control__text">
            · 최고 주입률 설정
          </div>
          <div class="box-control__number-outter">
            <div class="box-control__number">
              <input v-if="this.$store.state.ozone.isModifyMode" type="text" :value="this.$store.state.ozone.latestModify.io_injection_max" v-on:input="updateInput($event, 'io_injection_max')"/>
              <span v-else>{{ this.$store.state.ozone.latestModify.io_injection_max | numFormat('0.00') }}</span>
            </div>
          </div>
          <div class="box-control__unit">
            ppm
          </div>
        </div>
        <div class="box-control__row1">
          <div class="box-control__text">
            · 최저 주입률 설정
          </div>
          <div class="box-control__number-outter">
            <div class="box-control__number">
              <input v-if="this.$store.state.ozone.isModifyMode" type="text" 
              :value="this.$store.state.ozone.latestModify.io_injection_min" 
              v-on:input="updateInput($event, 'io_injection_min')"/>
              <span v-else>{{ this.$store.state.ozone.latestModify.io_injection_min | numFormat('0.00') }}</span>
            </div>
          </div>
          <div class="box-control__unit">
            ppm
          </div>
        </div>
        <div class="box-control__row1">
          <div class="box-control__text">
            · 목표 잔류 오존 농도 설정
          </div>
          <div class="box-control__number-outter">
            <div class="box-control__number">
              <input v-if="this.$store.state.ozone.isModifyMode" type="text" :value="this.$store.state.ozone.latestModify.io_target_de" v-on:input="updateInput($event, 'io_target_de')"/>
              <span v-else>{{ this.$store.state.ozone.latestModify.io_target_de | numFormat('0.00') }}</span>
            </div>
          </div>
          <div class="box-control__unit">
            ppm
          </div>
        </div>

        <div class="box-control__divider">
        </div>
        <div class="box-control__row2">
          <div class="box-control__text">
            · 오존발생기 운영 기준
          </div>
          <div style="display: flex; flex-direction: column; height: 60px;">
            <div style="display: flex; align-items: center; justify-contents: center; margin: 3px 0 5px 0; height: 24px;">
            <div class="box-control__number-outter">
              <div class="box-control__number">
                <input v-if="this.$store.state.ozone.isModifyMode" type="text" :value="this.$store.state.ozone.latestModify.io_og_1_min | numFormat('0.[00]')" v-on:input="updateInput($event, 'io_og_1_min')"/>
                <span v-else>{{ this.$store.state.ozone.latestModify.io_og_1_min | numFormat('0.[00]') }}</span>
              </div>
              <div class="box-control__slush">
              ~
              </div>
              <div class="box-control__number">
                <input v-if="this.$store.state.ozone.isModifyMode" type="text" :value="this.$store.state.ozone.latestModify.io_og_1_max | numFormat('0.[00]')" v-on:input="updateInput($event, 'io_og_1_max')"/>
                <span v-else>{{ this.$store.state.ozone.latestModify.io_og_1_max | numFormat('0.[00]') }}</span>
              </div>
            </div>
            <div class="box-control__unit">
              g/h
            </div>
            </div>
            <div style="display: flex; align-items: center; justify-contents: center; margin: 3px 0px; height: 24px;">
            <div class="box-control__number-outter">
              <div class="box-control__number">
                <input v-if="this.$store.state.ozone.isModifyMode" type="text" :value="this.$store.state.ozone.latestModify.io_og_2_min | numFormat('0.[00]')" v-on:input="updateInput($event, 'io_og_2_min')"/>
                <span v-else>{{ this.$store.state.ozone.latestModify.io_og_2_min | numFormat('0.[00]') }}</span>
              </div>
              <div class="box-control__slush">
              ~
              </div>
              <div class="box-control__number">
                <input v-if="this.$store.state.ozone.isModifyMode" type="text" :value="this.$store.state.ozone.latestModify.io_og_2_max | numFormat('0.[00]')" v-on:input="updateInput($event, 'io_og_2_max')"/>
                <span v-else>{{ this.$store.state.ozone.latestModify.io_og_2_max | numFormat('0.[00]') }}</span>
              </div>
            </div>
            <div class="box-control__unit">
              g/h
            </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>  
</template>
<script>
import { PUT_OZONE_CONTROL_AI } from '@/store/modules/ozone'

export default {
  name: 'OzoneLeftContents',
  data: () => ({
    // isModifyMode: false
  }),
  methods: {
    updateInput: function (event, key) {
      this.$store.state.ozone.latestModify[key] = event.target.value
    },
    checkNumberFormat: function(val) {
      const regex = /^\d{1,2}([\.](\d{1,2})?)?$/ //eslint-disable-line
      if (!regex.test(val)) {
        return false		//  0.00 ~ 99.99
      }
      return true
    },
    checkNumberFormat2: function(val) {
      const regex = /^\d{1,4}([\.](\d{1,2})?)?$/ //eslint-disable-line
      if (!regex.test(val)) {
        return false		// 0.00~9999.99를 벗어나면
      }
      return true
    },
    updateControl: function() {
      let injection_min = 0
      let injection_max = 10
      let ozone_min = 0
      let ozone_max = 10000
      if (this.$store.state.ozone.isModifyMode) {
        if (this.$store.state.ozone.latestModify.io_injection_max === ''
           || this.$store.state.ozone.latestModify.io_injection_min === ''
           || this.$store.state.ozone.latestModify.io_og_1_min === ''
           || this.$store.state.ozone.latestModify.io_og_1_max === ''
           || this.$store.state.ozone.latestModify.io_og_2_min === ''
           || this.$store.state.ozone.latestModify.io_og_2_max === ''
           || this.$store.state.ozone.latestModify.io_set === ''
           || this.$store.state.ozone.latestModify.io_target_de === '') {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
        } else {
          let obj = {}
          obj.io_injection_max = parseFloat(this.$store.state.ozone.latestModify.io_injection_max).toFixed(2)
          obj.io_injection_min = parseFloat(this.$store.state.ozone.latestModify.io_injection_min).toFixed(2)
          obj.io_og_1_min = parseFloat(this.$store.state.ozone.latestModify.io_og_1_min)
          obj.io_og_1_max = parseFloat(this.$store.state.ozone.latestModify.io_og_1_max)
          obj.io_og_2_min = parseFloat(this.$store.state.ozone.latestModify.io_og_2_min)
          obj.io_og_2_max = parseFloat(this.$store.state.ozone.latestModify.io_og_2_max)
          obj.io_set = parseFloat(this.$store.state.ozone.latestModify.io_set).toFixed(2)
          obj.io_target_de = parseFloat(this.$store.state.ozone.latestModify.io_target_de).toFixed(2)
          
          if ((obj.io_set < injection_min || obj.io_set > injection_max)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '주입률 기준 설정 범위', text2: injection_min + ' ~ ' + injection_max})
            return
          }
          if ((obj.io_injection_max < injection_min || obj.io_injection_max > injection_max)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '최고 주입률 설정 범위', text2: injection_min + ' ~ ' + injection_max})
            return
          }
          if ((obj.io_injection_min < injection_min || obj.io_injection_min > injection_max)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '최저 주입률 설정 범위', text2: injection_min + ' ~ ' + injection_max})
            return
          }
          if ((obj.io_og_1_min < ozone_min || obj.io_og_1_min > ozone_max)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '오존발생기 1구간 최저값 범위', text2: ozone_min + ' ~ ' + ozone_max})
            return
          }
          if ((obj.io_og_1_max < ozone_min || obj.io_og_1_max > ozone_max)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '오존발생기 1구간 최대값 범위', text2: ozone_min + ' ~ ' + ozone_max})
            return
          }
          if ((obj.io_og_2_min < ozone_min || obj.io_og_2_min > ozone_max)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '오존발생기 2구간 최저값 범위', text2: ozone_min + ' ~ ' + ozone_max})
            return
          }
          if ((obj.io_og_2_max < ozone_min || obj.io_og_2_max > ozone_max)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '오존발생기 2구간 최대값 범위', text2: ozone_min + ' ~ ' + ozone_max})
            return
          }
          if ((obj.io_target_de < injection_min || obj.io_target_de > injection_max)) {
            this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '목표 잔류 오존 농도 설정 범위', text2: injection_min + ' ~ ' + injection_max})
            return
          }
          this.$store.dispatch(PUT_OZONE_CONTROL_AI, obj)
          this.$store.state.ozone.isModifyMode = !this.$store.state.ozone.isModifyMode
          this.$store.state.ozone.latest.io_injection_max = parseFloat(this.$store.state.ozone.latestModify.io_injection_max).toFixed(2)
          this.$store.state.ozone.latest.io_injection_min = parseFloat(this.$store.state.ozone.latestModify.io_injection_min).toFixed(2)
          this.$store.state.ozone.latest.io_og_1_min = parseFloat(this.$store.state.ozone.latestModify.io_og_1_min)
          this.$store.state.ozone.latest.io_og_1_max = parseFloat(this.$store.state.ozone.latestModify.io_og_1_max)
          this.$store.state.ozone.latest.io_og_2_min = parseFloat(this.$store.state.ozone.latestModify.io_og_2_min)
          this.$store.state.ozone.latest.io_og_2_max = parseFloat(this.$store.state.ozone.latestModify.io_og_2_max)
          this.$store.state.ozone.latest.io_set = parseFloat(this.$store.state.ozone.latestModify.io_set).toFixed(2)
          this.$store.state.ozone.latest.io_target_de = parseFloat(this.$store.state.ozone.latestModify.io_target_de).toFixed(2)
        }
      } else {
        this.$store.state.ozone.isModifyMode = !this.$store.state.ozone.isModifyMode
      }
    },
    cancelControl: function() {
      this.$store.state.ozone.latestModify = Object.assign({}, this.$store.state.ozone.latest)
      this.$store.state.ozone.isModifyMode = !this.$store.state.ozone.isModifyMode
    }
  },
  watch: {
    '$store.state.ozone.latestModify.io_injection_max': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.ozone.latestModify['io_injection_max'] = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.ozone.latestModify['io_injection_max'] = newVal
        } else {
          this.$store.state.ozone.latestModify['io_injection_max'] = oldVal
        }
      }
    },
    '$store.state.ozone.latestModify.io_injection_min': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.ozone.latestModify['io_injection_min'] = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.ozone.latestModify['io_injection_min'] = newVal
        } else {
          this.$store.state.ozone.latestModify['io_injection_min'] = oldVal
        }
      }
    },
    '$store.state.ozone.latestModify.io_og_1_min': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.ozone.latestModify['io_og_1_min'] = newVal
      } else {
        if (this.checkNumberFormat2(newVal)) {
          this.$store.state.ozone.latestModify['io_og_1_min'] = newVal
        } else {
          this.$store.state.ozone.latestModify['io_og_1_min'] = oldVal
        }
      }
    },
    '$store.state.ozone.latestModify.io_og_1_max': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.ozone.latestModify['io_og_1_max'] = newVal
      } else {
        if (this.checkNumberFormat2(newVal)) {
          this.$store.state.ozone.latestModify['io_og_1_max'] = newVal
        } else {
          this.$store.state.ozone.latestModify['io_og_1_max'] = oldVal
        }
      }
    },
    '$store.state.ozone.latestModify.io_og_2_min': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.ozone.latestModify['io_og_2_min'] = newVal
      } else {
        if (this.checkNumberFormat2(newVal)) {
          this.$store.state.ozone.latestModify['io_og_2_min'] = newVal
        } else {
          this.$store.state.ozone.latestModify['io_og_2_min'] = oldVal
        }
      }
    },
    '$store.state.ozone.latestModify.io_og_2_max': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.ozone.latestModify['io_og_2_max'] = newVal
      } else {
        if (this.checkNumberFormat2(newVal)) {
          this.$store.state.ozone.latestModify['io_og_2_max'] = newVal
        } else {
          this.$store.state.ozone.latestModify['io_og_2_max'] = oldVal
        }
      }
    },
    '$store.state.ozone.latestModify.io_set': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.ozone.latestModify['io_set'] = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.ozone.latestModify['io_set'] = newVal
        } else {
          this.$store.state.ozone.latestModify['io_set'] = oldVal
        }
      }
    },
    '$store.state.ozone.latestModify.io_target_de': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.ozone.latestModify['io_target_de'] = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.ozone.latestModify['io_target_de'] = newVal
        } else {
          this.$store.state.ozone.latestModify['io_target_de'] = oldVal
        }
      }
    },
  }
}
</script>
<style lang="scss" scoped>
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
.box-control-outter {
  margin-top: 25px;
}
.box-control {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 592px;
  height: 254px;
  background-image: url('../../assets/ozone/background-control-box.png');
  background-size: 100% 100%;
  padding: 20px 50px;
  &__icon {
    position: absolute;
    top: 30px;
    right: 20px;
    cursor: pointer;
    z-index: 20;
  }
  &__text {
    flex: 1;
    width: 180px;
    text-shadow: 0 0 9px #5cafff;
    font-size: 16px;
    color: #9fc4ff;
  }
  &__slush {
    width: 40px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-shadow: 0 0 9px #5cafff;
    font-size: 16px;
    color: #9fc4ff;
  }
  &__number-outter {
    display: flex;
    justify-content: flex-end;
    width: 200px;
  }
  &__number {
    width: 74px;
    font-family: "LAB디지털" !important;
    text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
    color: #ccf1ff;
    font-size: 24px;
    text-align: center;
    > span {
      font-family: inherit !important;
    }
    > input {
      width: 100%;
      height: 26px;
      text-align: center;
      border: solid 1px rgba(157, 191, 255, 0.5);
      background-color: rgba(157, 191, 255, 0.07);
      font-family: inherit !important;
      color: inherit !important;
    }
  }
  &__unit {
    width: 70px;
    color: #417db9;
    font-size: 16px;
    text-align: center;
  }
  &__divider {
    width: 482px;
    height: 7px;
    background-image: url('../../assets/ozone/background-control-box-divider.png');
    background-size: 100% 100%;
  }
  &__row1 {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 33.3%;
  }
  &__row2 {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 66.6%
  }
}
.main{
  display: flex;
  flex-flow: column;
  width: 100%;
  height: 100%;
  .contents-container{
    display: flex;
    width: 586px;
    height: 475px;
  }
  .contents-img{
    position: relative;
    left: -36px;
    width: 615px;
    height: 506px;
    background-image: url('../../assets/ozone/left_contents.png');
    margin-top: 25px;
    .select-box{
      display: flex;
      justify-content: space-between;
      margin: 0 10px;
      &__element{
        display:flex;
        flex-direction: column;
        justify-content: center;
        align-content: center;
        width: 60px;
        height: 30px;
        border: solid 1px #b4dffa;
        background-color: rgba(139, 194, 240, 0.25);
        font-size: 14px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.8;
        letter-spacing: normal;
        text-align: center;
        color: #fff;
        cursor: pointer;
        &--focused {
          box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
          border: solid 1px #b4dffa;
          background-color: rgba(139, 194, 240, 0.4);
        }
      }
    }
    .contents-img-box{
      position: absolute;
      display: flex;
      width: 97px;
      height: 31px;
      justify-content: center;
      align-items: center;
      &__one{
        top: 167px;
        left: 104px;
      }
      &__two{
        top: 278px;
        left: 104px;
      }
      &__three{
        top: 390px;
        left: 104px;
      }
      &__four{
        top: 167px;
        left: 498px;
      }
      &__five{
        top: 278px;
        left: 498px;
      }
      &__six{
        top: 390px;
        left: 498px;
      }
      &__text {
        width: 35px;
        display: flex;
        flex-direction: column;
        font-size: 12px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        letter-spacing: normal;
        text-align: center;
        color: #417db9;
      }
      &__num{
        flex: 1;
        text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
        font-family: "LAB디지털" !important;
        font-size: 20px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        letter-spacing: normal;
        text-align: right;
        color: #ccf1ff;
      }
    }
  }
}
.left-one-line-water-h-flow{ 
  position: absolute;
  width: 136px;
  height: 8px;
  top: 227px;
  left: 0px;
  .buble {
    position: absolute;
    width: 136px;
    height: 8px;
    background-image: url('../../assets/splashdown/water_h_flow.png');
    background-position: -35px 50%;
    animation-name: left-one-line-h-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes left-one-line-h-flow{ 
  0% {opacity:0; transform: translateX(0px);}
  50% {opacity:1; }
  90% {opacity:0; }
  100% {opacity:0; transform: translateX(120px);}
}
.right-two-line-water-h-flow{ 
  position: absolute;
  width: 136px;
  height: 8px;
  top: 353px;
  left: 470px;
  .buble {
    position: absolute;
    width: 136px;
    height: 8px;
    background-image: url('../../assets/splashdown/water_h_flow.png');
    background-position: -35px 50%;
    animation-name: right-two-line-water-h-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes right-two-line-water-h-flow{ 
  0% {opacity:0; transform: translateX(0px);}
  50% {opacity:1; }
  90% {opacity:0; }
  100% {opacity:0; transform: translateX(120px);}
}
.left-one-line-water-v-flow-2-top{
  position: absolute;
  width: 8px;
  height: 136px;
  top: 150px;
  left: 181px;
  .buble {
    position: absolute;
    width: 8px;
    height: 136px;
    background-image: url('../../assets/splashdown/water_v_flow.png');
    background-position: 50% 124px;
    animation-name: left-one-line-water-v-flow-2-top;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes left-one-line-water-v-flow-2-top{ 
  0% {opacity:0; transform: translateY(0px);}
  40% {opacity:1; }
  50% {opacity:0; }
  100% {opacity:0; transform: translateY(-120px);}
}
.right-one-line-water-v-flow-2-top{
  position: absolute;
  width: 8px;
  height: 136px;
  top: 150px;
  left: 425px;
  .buble {
    position: absolute;
    width: 8px;
    height: 136px;
    background-image: url('../../assets/splashdown/water_v_flow.png');
    background-position: 50% 124px;
    animation-name: right-one-line-water-v-flow-2-top;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes right-one-line-water-v-flow-2-top{ 
  0% {opacity:0; transform: translateY(0px);}
  40% {opacity:1; }
  50% {opacity:0; }
  100% {opacity:0; transform: translateY(-120px);}
}
.center-three-line-water-v-flow{
  position: absolute;
  width: 8px;
  height: 106px;
  top: 384px;
  left: 304px;
  .buble {
    position: absolute;
    width: 8px;
    height: 54px;
    background-image: url('../../assets/splashdown/water_v_flow_bottom.png');
    background-size: 100% 100%;
    background-position-y: -35px;
    animation-name: center-three-line-water-v-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes center-three-line-water-v-flow{ 
  0% {opacity:0; transform: translateY(0px);}
  50% {opacity:1; }
  65% {opacity:0; }
  100% {opacity:0; transform: translateY(130px);}
}
.center-two-line-water-v-flow{
  position: absolute;
  width: 8px;
  height: 120px;
  top: 225px;
  left: 304px;
  overflow: hidden;
  .buble {
    position: absolute;
    width: 8px;
    height: 54px;
    background-image: url('../../assets/splashdown/water_v_flow_bottom.png');
    background-size: 100% 100%;
    background-position-y: -35px;
    animation-name: center-two-line-water-v-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes center-two-line-water-v-flow{ 
  0% {opacity:0; transform: translateY(0px);}
  20% {opacity:1; }
  90% {opacity:0; }
  100% {opacity:0; transform: translateY(130px);}
}
.center-one-line-water-v-flow{
  position: absolute;
  width: 8px;
  height: 80px;
  top: 0px;
  left: 305px;
  overflow: hidden;
  .buble {
    position: absolute;
    width: 8px;
    height: 54px;
    background-image: url('../../assets/splashdown/water_v_flow_bottom.png');
    background-size: 100% 100%;
    background-position-y: -35px;
    animation-name: center-one-line-water-v-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes center-one-line-water-v-flow{ 
  0% {opacity:0; transform: translateY(0px);}
  20% {opacity:1; }
  50% {opacity:0; }
  100% {opacity:0; transform: translateY(130px);}
}
.left-one-line-water-v-flow{
  position: absolute;
  width: 8px;
  height: 80px;
  top: 217px;
  left: 129px;
  overflow: hidden;
  .buble {
    position: absolute;
    width: 8px;
    height: 54px;
    background-image: url('../../assets/splashdown/water_v_flow_bottom.png');
    background-size: 100% 100%;
    background-position-y: -35px;
    animation-name: left-one-line-water-v-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
  .delay4 {
    animation-delay: 3.75s;
  }
}
@keyframes left-one-line-water-v-flow{ 
  0% {opacity:0; transform: translateY(0px);}
  35% {opacity:1; }
  45% {opacity:0; }
  100% {opacity:0; transform: translateY(89px);}
}
.left-two-line-water-v-flow{
  position: absolute;
  width: 8px;
  height: 80px;
  top: 302px;
  left: 129px;
  overflow: hidden;
  .buble {
    position: absolute;
    width: 8px;
    height: 54px;
    background-image: url('../../assets/splashdown/water_v_flow_bottom.png');
    background-size: 100% 100%;
    background-position-y: -35px;
    animation-name: left-two-line-water-v-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
  .delay4 {
    animation-delay: 3.75s;
  }
}
@keyframes left-two-line-water-v-flow{ 
  0% {opacity:0; transform: translateY(0px);}
  35% {opacity:1; }
  45% {opacity:0; }
  100% {opacity:0; transform: translateY(89px);}
}
.right-one-line-water-v-flow{
  position: absolute;
  width: 8px;
  height: 80px;
  top: 217px;
  left: 480px;
  overflow: hidden;
  .buble {
    position: absolute;
    width: 8px;
    height: 54px;
    background-image: url('../../assets/splashdown/water_v_flow_bottom.png');
    background-size: 100% 100%;
    background-position-y: -35px;
    animation-name: right-one-line-water-v-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
  .delay4 {
    animation-delay: 3.75s;
  }
}
@keyframes right-one-line-water-v-flow{ 
  0% {opacity:0; transform: translateY(0px);}
  35% {opacity:1; }
  45% {opacity:0; }
  100% {opacity:0; transform: translateY(89px);}
}
.right-two-line-water-v-flow{
  position: absolute;
  width: 8px;
  height: 80px;
  top: 302px;
  left: 480px;
  overflow: hidden;
  .buble {
    position: absolute;
    width: 8px;
    height: 54px;
    background-image: url('../../assets/splashdown/water_v_flow_bottom.png');
    background-size: 100% 100%;
    background-position-y: -35px;
    animation-name: right-two-line-water-v-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
  .delay4 {
    animation-delay: 3.75s;
  }
}
@keyframes right-two-line-water-v-flow{ 
  0% {opacity:0; transform: translateY(0px);}
  35% {opacity:1; }
  45% {opacity:0; }
  100% {opacity:0; transform: translateY(89px);}
}
.center-one-line-water-h-flow{
  position: absolute;
  width: 54px;
  height: 8px;
  top: 403px;
  left: 225px;
  .buble {
    position: absolute;
    width: 54px;
    height: 8px;
    background-image: url('../../assets/splashdown/water_h_flow.png');
    background-position: -35px 50%;
    animation-name: center-one-line-water-h-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes center-one-line-water-h-flow{ 
  0% {opacity:0; transform: translateX(0px);}
  20% {opacity:1; }
  30% {opacity:0; }
  100% {opacity:0; transform: translateX(120px);}
}
.center-two-line-water-h-flow{
  position: absolute;
  width: 54px;
  height: 8px;
  top: 333px;
  left: 235px;
  .buble {
    position: absolute;
    width: 54px;
    height: 8px;
    background-image: url('../../assets/ozone/water_h_flow_two.png');
    background-size: 100% 100%;
    background-position-x: 36px;
    animation-name: center-two-line-water-h-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes center-two-line-water-h-flow{ 
  0% {opacity:0; transform: translateX(0px);}
  20% {opacity:1; }
  30% {opacity:0; }
  100% {opacity:0; transform: translateX(-120px);}
}

.right-one-line-water-h-flow-2-left{
  position: absolute;
  width: 136px;
  height: 8px;
  top: 227px;
  left: 560px;
  .buble {
    position: absolute;
    width: 54px;
    height: 8px;
    background-image: url('../../assets/ozone/water_h_flow_two.png');
    background-size: 100% 100%;
    background-position-x: 36px;
    animation-name: right-one-line-water-h-flow-2-left;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes right-one-line-water-h-flow-2-left{ 
  0% {opacity:0; transform: translateX(0px);}
  50% {opacity:1; }
  90% {opacity:0; }
  100% {opacity:0; transform: translateX(-120px);}
}
.left-one-line-water-h-flow-2-left{
  position: absolute;
  width: 136px;
  height: 8px;
  top: 353px;
  left: 90px;
  .buble {
    position: absolute;
    width: 54px;
    height: 8px;
    background-image: url('../../assets/ozone/water_h_flow_two.png');
    background-size: 100% 100%;
    background-position-x: 36px;
    animation-name: left-one-line-water-h-flow-2-left;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes left-one-line-water-h-flow-2-left{ 
  0% {opacity:0; transform: translateX(0px);}
  50% {opacity:1; }
  90% {opacity:0; }
  100% {opacity:0; transform: translateX(-120px);}
}
.center-one-line-water-h-flow-2-right{ 
  position: absolute;
  width: 54px;
  height: 8px;
  top: 333px;
  left: 320px;
  .buble {
    position: absolute;
    width: 54px;
    height: 8px;
    background-image: url('../../assets/splashdown/water_h_flow.png');
    background-position: -35px 50%;
    animation-name: center-one-line-water-h-flow-2-right;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes center-one-line-water-h-flow-2-right{ 
  0% {opacity:0; transform: translateX(0px);}
  20% {opacity:1; }
  30% {opacity:0; }
  100% {opacity:0; transform: translateX(120px);}
}
.center-two-line-water-h-flow-2-right{ 
  position: absolute;
  width: 54px;
  height: 8px;
  top: 403px;
  left: 330px;
  .buble {
    position: absolute;
    width: 54px;
    height: 8px;
    background-image: url('../../assets/ozone/water_h_flow_two.png');
    background-size: 100% 100%;
    background-position-x: 36px;
    animation-name: center-two-line-water-h-flow;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    opacity: 0;
  }
  .delay1 {
    animation-delay: 0s;
  }
  .delay2 {
    animation-delay: 1.25s;
  }
  .delay3 {
    animation-delay: 2.5s;
  }
}
@keyframes center-two-line-water-h-flow-2-right{ 
  0% {opacity:0; transform: translateX(0px);}
  20% {opacity:1; }
  30% {opacity:0; }
  100% {opacity:0; transform: translateX(120px);}
}
.box-type1-outter1 {
  position: absolute;
  top: 86px;
  left: 243px;
}
.box-type1-outter2 {
  position: absolute;
  top: 174px;
  left: 243px;
}
.box-type1 {
    display: flex;
    width: 130px;
    height: 42px;
    flex-direction: column;
    &__title {
      width: 100%;
      text-align: center;
      font-size: 16px;
      padding: 0px 0px 10px 0px;
      color: #fff;
      text-shadow: 0 0 9px #5cafff;
    }
    &__body {
      display: flex;
      width: 100%;
    }
    &__value {
      flex: 1;
      text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
      font-family: "LAB디지털" !important;
      font-size: 24px;
      color: #ccf1ff;
      text-align: right;
    }
    &__unit {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40%;
      font-family: "KHNPHUotfR";
      font-size: 16px;
      color: #417db9;
      text-align: center;
    }
  }
</style>