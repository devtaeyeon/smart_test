<template>
  <div v-if="this.$store.state.dialog.aiModeOfJi.visible" class="popup">
    <div class="popup__inner">
      <div class="popup__title">
        <div class="popup__icon"></div>
        <span>침전지 {{ this.$store.state.dialog.aiModeOfJi.number }}지</span>
      </div>
      <div class="popup__body" v-if="this.$store.state.dialog.aiModeOfJi.sedimentationIndex == 1">
        <span v-if="this.$store.state.sedimentation.latest['e_sc_set' + this.$store.state.dialog.aiModeOfJi.number] === 0" class="popup__body--large">AI모드에 추가하시겠습니까?</span>
        <span v-if="this.$store.state.sedimentation.latest['e_sc_set' + this.$store.state.dialog.aiModeOfJi.number] === 1" class="popup__body--large">AI모드에서 제외하시겠습니까?</span>
      </div>
      <div class="popup__bottom">
        <div class="btn btn--cancel" @click="closePopup()">취소</div>
        <div class="btn btn--change" @click="changeAIMode()">변경</div>
      </div>
    </div>
  </div>
</template>
<script>
import { CLOSE_AI_MODE_OF_JI_DIALOG } from '@/store/modules/dialog'
import { PUT_SEDIMENTATION_CONTROL_LOCATION } from '../../store/modules/sedimentation'

export default {
  data: () => ({
  }),
  methods: {
    closePopup: function () {
      this.$store.dispatch('dialog/' + CLOSE_AI_MODE_OF_JI_DIALOG)
    },
    changeAIMode: function () {
      let _data = {}
      _data.numJi = this.$store.state.dialog.aiModeOfJi.number
      if(this.$store.state.dialog.aiModeOfJi.sedimentationIndex == 1){
        _data.ai = this.$store.state.sedimentation.latest['e_sc_set' + this.$store.state.dialog.aiModeOfJi.number] === 0 ? 1 : 0
        this.$store.dispatch(PUT_SEDIMENTATION_CONTROL_LOCATION, _data)
      }
    }
  },
  mounted: function() {
  },
  destroyed: function() {
  }
}
</script>
<style lang="scss" scoped>
.popup{
  position: absolute;
  // top: -85px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 1080px;
  background-color: rgba(30,37,61,0.8);
  z-index: 200;
  &__inner{
    width: 463px;
    height: 213px;
    background-image: url('../../assets/dialog/background.png');
    padding: 25px;
  }
  &__title {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    font-size: 24px;
    color: #b4dffb;
  }
  &__icon {
    width: 17px;
    height: 28px;
    margin-right: 15px;
    background-image: url('../../assets/dialog/ai_header_icon.png');
    background-size: 100% 100%;
  }
  &__body {
    padding: 10px 15px;
    font-size: 16px;
    color: #fff;
    &--large {
      font-size: 20px;
    }
  }
  &__bottom {
    display:flex;
    align-items: center;
    justify-content: flex-end;
    padding: 10px 15px;
  }
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