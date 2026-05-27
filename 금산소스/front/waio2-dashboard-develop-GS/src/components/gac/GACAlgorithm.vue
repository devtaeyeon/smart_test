<template>
  <div class="main">
    <div class="top-center">
      <div class="top-center__contents">
        <TopNavigator/>
      </div>
    </div>
    <div class="top">
      <div class="title-down">
        <div class="title-down__text">운영지수<span class="title-down__digital">{{ this.$store.state.gac.latest.i_opr_cnt }}</span>지</div> 
        </div>
      <div class="title">GAC 여과 전체 현황</div>
      <div class="right">
        <div class="right-contents">
          <div class="right-contents__text-first">AI 운전 모드</div>
          <div class="right-contents__btn-first">
            <div class="control_box_operation">
              <div v-if="$store.state.gac.latest.ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(6, 2)">AI</div>
              <div v-if="$store.state.gac.latest.ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(6, 1)">AI추천</div>
              <div v-if="$store.state.gac.latest.ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
              <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(6, 0)">AI분석</div>
            </div>
          </div>
        </div>
        <div class="right-contents">
          <div class="right-contents__text-third">최대 여과지속시간 설정</div>
          <div class="right-contents__input-box">
            <input v-if="isModifyMode" type="text" :value="this.i_location_ti_set_max" v-on:input="updateInput($event)"/><span v-else>{{ this.$store.state.gac.latest.i_location_ti_set_max / 60 | numFormat('0') }}</span>
          </div>
          <div class="right-contents__icon">
            <div v-if="$store.state.login.user.tkn !== null" class="custom-icon" @click="updateControl">
              <div :class="[ isModifyMode ? 'custom-icon__checkbox' : 'custom-icon__pencil' ]"></div>
            </div>
            <div v-if="isModifyMode" class="custom-cancel-icon" style="margin-left: 3px;" @click="cancelControl">
              <div class='custom-cancel-icon__cancel'></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="contents">
      <GACContents/>
    </div>
    <div class="bottom">
      <GACChart/>
    </div>
  </div>  
</template>
<script>
import GACContents from '@/components/gac/GACContents' 
import GACChart from '@/components/gac/GACChart'
import { SET_OVERLAY } from '@/store'
import { GET_GAC_LATEST, PUT_GAC_CONTROL_TI } from '@/store/modules/gac'
import TopNavigator from '@/components/core/TopNavigator'
import { OPEN_AI_MODE_DIALOG } from '@/store/modules/dialog'
import { GET_FILTER_SCHEDULE } from '@/store/modules/filter'
export default {
  name:'GACAlgorithm',
  data: () => ({
    isModifyMode: false,
    i_location_ti_set_max: 0
  }),
  components: {
    GACContents,
    GACChart,
    TopNavigator
  },
  methods: {
    onClickAICheckbox: function(index, expectedValue) {
      if( this.$store.state.login.user.tkn !== null ) {
        this.$store.state.selectedBuildingIndex = index
        this.$store.dispatch('dialog/' + OPEN_AI_MODE_DIALOG, expectedValue)
      }
    },
    onClickPeakCheckbox: function() {
      this.$store.state.gac.latest.peak_mode = !this.$store.state.gac.latest.peak_mode
    },
    updateControl: function() {
      if (this.isModifyMode) {
        if (this.i_location_ti_set_max === '') {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
        } else {
          let obj = {}
          obj.i_location_ti_set_max = parseInt(this.i_location_ti_set_max) * 60
          Promise.all([
            this.$store.dispatch(PUT_GAC_CONTROL_TI, obj)
          ]).finally(() => {
            this.isModifyMode = false
          })
        }
      } else {
        this.i_location_ti_set_max = this.$store.state.gac.latest.i_location_ti_set_max / 60
        this.isModifyMode = !this.isModifyMode
      }
      
    },
    cancelControl: function () {
      this.i_location_ti_set_max = this.$store.state.gac.latest.i_location_ti_set_max / 60
      this.isModifyMode = false
    },
    checkNumberFormat: function(val) {
      // const regex = /^[1-9][0-9]?$|^1\d{2}$|^200$/ //eslint-disable-line
      //if (!regex.test(val)) {
      //  return false		// 0~100를 벗어나면
      // }
      if (Number(val) < 0 || Number(val) > 360) {
        console.log(Number(val))
        return false
      }
      return true
    },
    updateInput: function (event) {
      this.i_location_ti_set_max = event.target.value
    },
  },
  mounted: function() {
    this.$store.state.selectedBuildingIndex = 6
    this.$store.commit(SET_OVERLAY, true)
    Promise.all([
      this.$store.dispatch(GET_GAC_LATEST),
      this.$store.dispatch(GET_FILTER_SCHEDULE)
    ]).finally(() => {
      this.$store.commit(SET_OVERLAY, false)
    })
    
    this.timer = setInterval(() => {
      this.$store.dispatch(GET_GAC_LATEST),
      this.$store.dispatch(GET_FILTER_SCHEDULE)
    }, 60 * 1000)
  },
  destroyed: function () {
    clearInterval(this.timer)
  },
  watch: {
    i_location_ti_set_max: function(newVal, oldVal) {
      console.log(newVal, oldVal)
      if (newVal === '') {
        this.i_location_ti_set_max = newVal
      } else {
        if (this.checkNumberFormat(newVal)) {
          this.i_location_ti_set_max = newVal
        } else {
          this.i_location_ti_set_max = oldVal
        }
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.bottom{
  display: flex;
  width: 100%;
  height: 392px;
}
.contents{
  display: flex;
  width: 100%;
  height: 415px;
}
.top-center{
  display: flex;
  justify-content: center;
  position: absolute;
  top:-76px;
  left: 159px;
  width: 1692px;
  height: 249px;
  background-image: url('../../assets/splashdown/top_center_background.png');
  .timer{
    width: 72px;
    height: 72px;
    margin-top: 110px;
    margin-right: 14px;
  }
  &__contents{
    margin-top: 100px;
  }
}
.top{
  display: flex;
  width: 100%;
  height: 173px;
  .title-down{
    position: absolute;
    width: 177px;
    height: 53px;
    background-image: url('../../assets/percolation/title_down.png');
    left: 35px;
    top: 145px;
    &__text{
      text-shadow: 0 0 9px #5cafff;
      font-size: 18px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 3;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
    }
    &__digital{
      text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
      font-family: "LAB디지털" !important;
      font-size: 24px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.17;
      letter-spacing: normal;
      text-align: center;
      color: #ccf1ff;
      margin: 0 5px;
    }
  }
  .title{
    width: 330px;
    height: 100%;
    background-image: url('../../assets/percolation/main_title.png');
    text-shadow: 0 0 9px #5cafff;
    font-family: "KHNPHUotfR";
    font-size: 30px !important;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2;
    letter-spacing: normal;
    text-align: left;
    color: #fff;
    margin-left: 55px;
    margin-top: 35px;
    background-position-x: right;
  }
}
</style>