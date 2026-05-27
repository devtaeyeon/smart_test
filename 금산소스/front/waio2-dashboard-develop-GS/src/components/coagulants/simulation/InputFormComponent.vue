<template>
  <div class="main-container">
    <div class="main-container__header">
      <div class="header-container">
        <div class="header-container__title">
          알고리즘 모의
        </div>
        <div class="header-container__button-container">
          <div class="header-container__button-inner-container">
            <div class="button-container button-container--sky" @click="loadCurrentValue">
              <div class="button-container__title">
                현재값 불러오기
              </div>
            </div>
            <div class="button-container button-container--skyblue" style="margin-left: 7px;" @click="reset">
              <div class="button-container__title" >
                초기화
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="main-container__body">
      <div class="main-container__list">
        <div class="box-config" style="margin-top:33px">
          <div class="box-config__text box-config__text--title">· 원수 탁도</div>
          <div class="box-config__input-box">
            <input type="number" v-model="$store.state.coagulants.simulation.b_tb" v-on:input="updateInput($event, 'b_tb')"/>
          </div>
          <div class="box-config__unit">
            NTU
          </div>
        </div>
        <div class="box-config">
          <div class="box-config__text box-config__text--title">· 원수 pH</div>
          <div class="box-config__input-box">
            <input type="number" v-model="$store.state.coagulants.simulation.b_ph" v-on:input="updateInput($event, '')"/>
          </div>
          <div class="box-config__unit">
            <!-- NTU -->
          </div>
        </div>
        <div class="box-config">
          <div class="box-config__text box-config__text--title">· 원수 수온</div>
          <div class="box-config__input-box">
            <input type="number" v-model="$store.state.coagulants.simulation.b_te" v-on:input="updateInput($event, '')"/>
          </div>
          <div class="box-config__unit">
            °C
          </div>
        </div>
        <div class="box-config">
          <div class="box-config__text box-config__text--title">· 원수 전기전도도</div>
          <div class="box-config__input-box">
            <input type="number" v-model="$store.state.coagulants.simulation.b_cu" v-on:input="updateInput($event, '')"/>
          </div>
          <div class="box-config__unit">
            μS/cm
          </div>
        </div>
        <div class="box-config">
          <div class="box-config__text box-config__text--title">· 원수 유량</div>
          <div class="box-config__input-box">
            <input type="number" v-model="$store.state.coagulants.simulation.b_in_fr" v-on:input="updateInput($event, '')"/>
          </div>
          <div class="box-config__unit">
            m<sup>3</sup>/h
          </div>
        </div>
        <div class="box-config">
          <div class="box-config__text box-config__text--title">· 침전지 탁도 #1</div>
          <div class="box-config__input-box">
            <input type="number" v-model="$store.state.coagulants.simulation.e1_tb" v-on:input="updateInput($event, '')"/>
          </div>
          <div class="box-config__unit">
            NTU
          </div>
        </div>
        <div class="box-config" style="margin-bottom:22px;">
          <div class="box-config__text box-config__text--title">· 침전지 탁도 #2</div>
          <div class="box-config__input-box">
            <input type="number" v-model="$store.state.coagulants.simulation.e2_tb" v-on:input="updateInput($event, '')"/>
          </div>
          <div class="box-config__unit">
            NTU
          </div>
        </div>
      </div>
      <div class="main-container__footer">
        <div class="footer-container">
          <div class="button-container button-container--gray" @click="runSimulation">
            <div class="button-container__title">
              알고리즘 모의
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { POST_COAGULANT_SIMULATION } from '@/store/modules/coagulants'
import numeral from 'numeral'
export default {
  name: 'InputFormComponent',
  data: () => ({

  }),
  methods: {
    loadCurrentValue: function () {
      this.$store.state.coagulants.simulation.b_tb = numeral(this.$store.state.coagulants.latest.b_tb).format('0.[00]')
      this.$store.state.coagulants.simulation.b_ph = numeral(this.$store.state.coagulants.latest.b_ph).format('0.[00]')
      this.$store.state.coagulants.simulation.b_te = numeral(this.$store.state.coagulants.latest.b_te).format('0.[00]')
      this.$store.state.coagulants.simulation.b_cu = numeral(this.$store.state.coagulants.latest.b_cu).format('0.[00]')
      this.$store.state.coagulants.simulation.b_in_fr = numeral(this.$store.state.coagulants.latest.b_in_fr).format('0.[00]')
      this.$store.state.coagulants.simulation.e1_tb = numeral(this.$store.state.coagulants.latest.e1_tb_b).format('0.[00]')
      this.$store.state.coagulants.simulation.e2_tb = numeral(this.$store.state.coagulants.latest.e2_tb_b).format('0.[00]')
    },
    reset: function () {
      this.$store.state.coagulants.simulation.b_tb = ''
      this.$store.state.coagulants.simulation.b_ph = ''
      this.$store.state.coagulants.simulation.b_te = ''
      this.$store.state.coagulants.simulation.b_cu = ''
      this.$store.state.coagulants.simulation.b_in_fr = ''
      this.$store.state.coagulants.simulation.e1_tb = ''
      this.$store.state.coagulants.simulation.e2_tb = ''
      this.$store.state.coagulants.simulation.ai_c1_cf = ''
      this.$store.state.coagulants.simulation.ai_c1_cf_coagulant = ''
      this.$store.state.coagulants.simulation.ai_c2_cf = ''
      this.$store.state.coagulants.simulation.ai_c2_cf_coagulant = ''
    },
    runSimulation: function () {     
      if (this.$store.state.coagulants.simulation.b_tb === '' 
        || this.$store.state.coagulants.simulation.b_ph === ''  
        || this.$store.state.coagulants.simulation.b_te === '' 
        || this.$store.state.coagulants.simulation.b_cu === '' 
        || this.$store.state.coagulants.simulation.b_in_fr === '' 
        || this.$store.state.coagulants.simulation.e1_tb === '' 
        || this.$store.state.coagulants.simulation.e2_tb === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
      } else {
        let obj = {}
        obj.b_tb = parseFloat(this.$store.state.coagulants.simulation.b_tb)
        obj.b_ph = parseFloat(this.$store.state.coagulants.simulation.b_ph)
        obj.b_te = parseFloat(this.$store.state.coagulants.simulation.b_te)
        obj.b_cu = parseFloat(this.$store.state.coagulants.simulation.b_cu)
        obj.b_in_fr = parseFloat(this.$store.state.coagulants.simulation.b_in_fr)
        obj.e1_tb = parseFloat(this.$store.state.coagulants.simulation.e1_tb)
        obj.e2_tb = parseFloat(this.$store.state.coagulants.simulation.e2_tb)
        obj.start_time = this.$store.state.coagulants.simulation.startTime.toISOString()
        obj.end_time = this.$store.state.coagulants.simulation.endTime.toISOString()
        this.$store.dispatch(POST_COAGULANT_SIMULATION, obj)
      }
    },
    checkNumberFormat: function(val) {
      const regex = /^(\d*)[\.]?(\d{1,2})?$/g //eslint-disable-line
      if (regex.test(val)) {
        return true
      }
      return false
    },
    updateInput: function (event, key) {
      this.$store.state.coagulants.simulation[key] = event.target.value
    }
  },
  watch: {
    '$store.state.coagulants.simulation.b_tb': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.coagulants.simulation.b_tb = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.coagulants.simulation.b_tb = newVal
        } else {
          this.$store.state.coagulants.simulation.b_tb = oldVal
        }
      }
    },
    '$store.state.coagulants.simulation.b_ph': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.coagulants.simulation.b_ph = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.coagulants.simulation.b_ph = newVal
        } else {
          this.$store.state.coagulants.simulation.b_ph = oldVal
        }
      }
    },
    '$store.state.coagulants.simulation.b_te': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.coagulants.simulation.b_te = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.coagulants.simulation.b_te = newVal
        } else {
          this.$store.state.coagulants.simulation.b_te = oldVal
        }
      }
    },
    '$store.state.coagulants.simulation.b_cu': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.coagulants.simulation.b_cu = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.coagulants.simulation.b_cu = newVal
        } else {
          this.$store.state.coagulants.simulation.b_cu = oldVal
        }
      }
    },
    '$store.state.coagulants.simulation.b_in_fr': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.coagulants.simulation.b_in_fr = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.coagulants.simulation.b_in_fr = newVal
        } else {
          this.$store.state.coagulants.simulation.b_in_fr = oldVal
        }
      }
    },
    '$store.state.coagulants.simulation.e1_tb': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.coagulants.simulation.e1_tb = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.coagulants.simulation.e1_tb = newVal
        } else {
          this.$store.state.coagulants.simulation.e1_tb = oldVal
        }
      }
    },
    '$store.state.coagulants.simulation.e2_tb': function(newVal, oldVal) {
      if (newVal === '') {
        this.$store.state.coagulants.simulation.e2_tb = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.$store.state.coagulants.simulation.e2_tb = newVal
        } else {
          this.$store.state.coagulants.simulation.e2_tb = oldVal
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.main-container {
  display: flex;
  flex-direction: column;
  &__header {
    // display: flex;
    // flex: 1;
  }
  &__body {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: 0px auto 0px auto;
    width: 495px;
    height: 450px;
    background-image: url('../../../assets/coagulants/simulation/body_background.png');
    background-size: 100% 100%;
    background-repeat: no-repeat;
    background-position: center;
  }
  &__list {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between; 
  }
  &__footer {
    margin-left: auto;
    padding-right: 16px;
    padding-bottom: 20px;
  }
}
.header-container {
  display: flex;
  width: 501px;
  height: 51px;
  background-image: url('../../../assets/coagulants/simulation/header_container.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-position: center;
  &__title {
    display: flex;
    flex: 1;
    align-items: center;
    font-size: 18px;
    color: #fff;
    padding-left: 38px;
  }
  &__button-container {
    display: flex;
    margin-left: auto;
  }
  &__button-inner-container {
    display:flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 4px;
  }
}
.button-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  padding: 8px 12px;
  cursor: pointer;
  &--sky {
    background-color: #4486de;
  }
  &--skyblue {
    background-color: #0555b5;
  }
  &--gray {
    width: 127px;
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.4);
    box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  }
  &__title {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    font-size: 14px;
    color: #fff;
  }
}
.box-config {
  display: flex;
  width: 100%;
  align-items: center;
  padding-left: 45px;
  padding-right: 38px;
  // margin-top: 23px;
  &__text {
    flex: 1;
    &--title {
      font-size: 16px;
      color: #c3eaff;
    }
  }
  &__input-box {
    width: 164px;
    border: solid 1px #9dbfff;
  }
  &__unit {
    width: 32px;
    font-size: 12px;
    color: #417db9;
    margin-left: 17px;
  }
  input {
    width: 163px;
    font-family: "LAB디지털" !important;
    font-size: 24px;
    color: #ccf1ff;
    text-align: center;
  }
}
.box-input {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 148px;
  height: 42px;
  &__active {
    border: solid 1px #9dbfff;
  }
  &--value {
    width: 78px;
    text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
    font-family: "LAB디지털" !important;
    font-size: 24px;
    color: #ccf1ff;
    text-align: left;
    margin-left: 10px;
    span {
      font-family: "LAB디지털" !important;
    }
    input {
      width: 78px;
      font-family: "LAB디지털" !important;
      font-size: 24px;
      color: #ccf1ff;
      text-align: left;
    }
  }
  &--unit {
    width: 70px;
    font-family: KHNPHUotfR;
    font-size: 14px;
    color: #417db9;
    text-align: right;
    padding-right: 10px;
  }
}
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
}
</style>