<template>
  <div v-if="this.$store.state.dialog.aiMode.visible" class="popup">
    <!-- 전체 공정 AI 모드 변경-->
    <div v-if="this.$store.state.dialog.aiMode.changeAllAIMode" class="popup__inner">
      <div class="popup__title">
        <div class="popup__icon"></div>
        <span>전체 공정</span>
      </div>
      <div class="popup__body">
        <!-- <span class="popup__body--large">{{ this.IsAIMode ? 'AI분석모드' : 'AI모드'}}</span>로 변경하시겠습니까? -->
        <span v-if="this.$store.state.dialog.aiMode.expectedValue === 0" class="popup__body--large">전체 공정을 AI분석모드로 변경하시겠습니까?</span>
        <span v-if="this.$store.state.dialog.aiMode.expectedValue === 1" class="popup__body--large">전체 공정을 AI추천모드로 변경하시겠습니까?</span>
        <span v-if="this.$store.state.dialog.aiMode.expectedValue === 2" class="popup__body--large">전체 공정을 AI모드로 변경하시겠습니까?</span>
      </div>
      <div class="popup__bottom">
        <div class="btn btn--cancel" @click="closePopup()">취소</div>
        <div class="btn btn--change" @click="changeAllAIMode()">변경</div>
      </div>
    </div>
    <!-- 일반 공정 모드 변경-->
    <div v-else class="popup__inner">
      <div class="popup__title">
        <div class="popup__icon"></div>
        <span>{{ this.GetTitle + ' ' }}</span>
      </div>
      <div class="popup__body">
        <!-- <span class="popup__body--large">{{ this.IsAIMode ? 'AI분석모드' : 'AI모드'}}</span>로 변경하시겠습니까? -->
        <span v-if="this.$store.state.dialog.aiMode.expectedValue === 0" class="popup__body--large">AI분석모드로 변경하시겠습니까?</span>
        <span v-if="this.$store.state.dialog.aiMode.expectedValue === 1" class="popup__body--large">AI추천모드로 변경하시겠습니까?</span>
        <span v-if="this.$store.state.dialog.aiMode.expectedValue === 2" class="popup__body--large">AI모드로 변경하시겠습니까?</span>
      </div>
      <div class="popup__bottom">
        <div class="btn btn--cancel" @click="closePopup()">취소</div>
        <div class="btn btn--change" @click="changeAIMode()">변경</div>
      </div>
    </div>
  </div>
</template>
<script>
import { CLOSE_ALL_AI_MODE_DIALOG, CHANGE_ALL_AI_MODE } from '@/store/modules/dialog'
import { PUT_MIXING_CONTROL_OPERATION } from '@/store/modules/mixing'
import { PUT_DISINFECTION_CONTROL_OPERATION_PRE } from '@/store/modules/disinfection'
import { PUT_DISINFECTION_CONTROL_OPERATION_POST } from '@/store/modules/disinfection'
import { PUT_COAGULANTS_CONTROL_OPERATION } from '@/store/modules/coagulants'



export default {
  data: () => ({
  }),
  computed: {
    GetTitle() {
      switch(this.$store.state.selectedBuildingIndex) {
        case 1:
          return '착수'
        case 2:
          return '약품'
        case 3:
          return '혼화응집'
        case 4:
          return '침전'
        case 5:
          return '여과'
        case 6:
          return 'GAC여과'
        // case 7:
        //   if (this.$store.state.dialog.aiMode.disinfectionIndex === 'pre') {
        //     return '소독 전염소'
        //   } else if (this.$store.state.dialog.aiMode.disinfectionIndex === 'peri') {
        //     return '소독 중염소'
        //   } else if (this.$store.state.dialog.aiMode.disinfectionIndex === 'post') {
        //     return '소독 후염소'
        //   } else {
        //     return '소독'
        //   }
        case 7.1:
          return '전차염'
        case 7.2:
          return '중차염'
        case 7.3:
          return '후차염'
        // case 11:
        //   return '오존'
        // case 16:
        //   return '공업3차약품'
        // case 17:
        //   return '공업3차혼화응집'
        // case 18:
        //   return '공업3차침전'
        default:
          return ''
      }
    },
    SelectedBuildingURL() {
      switch(this.$store.state.selectedBuildingIndex) {
        case 2:
          return PUT_COAGULANTS_CONTROL_OPERATION
        case 3:
          return PUT_MIXING_CONTROL_OPERATION
        // case 7:
        //   if (this.$store.state.dialog.aiMode.disinfectionIndex === 'pre') {
        //     return PUT_DISINFECTION_CONTROL_OPERATION_PRE
        //   } else if (this.$store.state.dialog.aiMode.disinfectionIndex === 'peri') {
        //     return PUT_DISINFECTION_CONTROL_OPERATION_PERI
        //   } else if (this.$store.state.dialog.aiMode.disinfectionIndex === 'post') {
        //     return PUT_DISINFECTION_CONTROL_OPERATION_POST
        //   } else {
        //     return ''
        //   }
        case 7.1:
            return PUT_DISINFECTION_CONTROL_OPERATION_PRE
        case 7.3:
            return PUT_DISINFECTION_CONTROL_OPERATION_POST
        default:
          return ''
      }
    }
  },
  methods: {
    closePopup: function () {
      this.$store.dispatch('dialog/' + CLOSE_ALL_AI_MODE_DIALOG)
    },
    changeAIMode: function () {
      this.$store.dispatch(this.SelectedBuildingURL, { operation: this.$store.state.dialog.aiMode.expectedValue })
    },
    changeAllAIMode: function () {
      this.$store.dispatch('dialog/' + CHANGE_ALL_AI_MODE)
    },
  },
  mounted: function() {
  },
  destroyed: function() {
  },
  watch: {
    '$store.state.dialog.aiMode.visible': function (newVal) {
      if (newVal) {
        if (this.$store.state.dialog.aiMode.expectedValue === 0) {
          this.$speak("AI분석모드로 변경하시겠습니까?")
        } else if (this.$store.state.dialog.aiMode.expectedValue === 1) {
          this.$speak("AI추천모드로 변경하시겠습니까?")
        } else if (this.$store.state.dialog.aiMode.expectedValue === 2) {
          this.$speak("AI모드로 변경하시겠습니까?")
        }
      }
    }
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