<template>
  <v-app :class='this.$store.getters.backgroundImage'>
    <!-- API 요청시 Loading 화면 -->
    <v-overlay :value="$store.state.overlay" z-index='9999'>
      <v-progress-circular
        indeterminate
        size='64'
      ></v-progress-circular>
    </v-overlay>
    <!-- Navigation -->
    <Drawer style="z-index:99999;"/>
    <!-- Header -->
    <AppBar/>
    <!-- URL Path에 따른 Router View 설정-->
    <v-main :style="$store.state.css.content">
      <router-view></router-view>
    </v-main>
    <!-- 로그인 Dialog -->
    <LoginDialog/>
    <!-- 경고 메세지 Dialog(설정 완료, 설정 실패, 제어 성공, 제어 실패 등등...) -->
    <AlertDialog/>
    <!-- 비밀번호 변경 Dialog -->
    <ModifyPasswordDialog/>
    <!-- 유저 정보 변경 Dialog -->
    <ModifyUserDialog/>
    <!-- 운전 모드 변경 Dialog -->
    <AIModeDialog/>
    <!-- 침전지 운전 모드 변경 Dialog -->
    <AIModeDialogOfJi/>
    <!-- 혼화응집 교반강도 자동모드 상세 Dialog -->
    <AutoModeDetailPopup/>
    <!-- 알람 API를 결과값(type)에 따라 팝업 발생 -->
    <template v-for="(item, index) in $store.state.alarm.alarm">
      <AlarmNotifyDialog :key="index" :index="index" :seq="item.seq" :alm_ty="item.alm_ty" :alm_id="item.alm_id" :alm_ntf_ti="item.alm_ntf_ti" :url="item.url" :message="item.message" :isVisible="item.isVisible" :ctr_list="item.ctr_list"/>
    </template>
  </v-app>
</template>

<script>
import Drawer from '@/components/core/Drawer'
import AppBar from '@/components/core/AppBar'
import AlertDialog from '@/components/dialog/AlertDialog'
import LoginDialog from '@/components/dialog/LoginDialog'
import ModifyUserDialog from '@/components/dialog/user/ModifyUserDialog'
import ModifyPasswordDialog from '@/components/dialog/user/ModifyPasswordDialog'
import AIModeDialog from '@/components/dialog/AIModeDialog'
import AlarmNotifyDialog from '@/components/dialog/AlarmNotifyDialog'
import { GET_ALARM_NOTIFY } from './store/modules/alarm/alarm'
import AIModeDialogOfJi from '@/components/dialog/AIModeDialogOfJi'
import AutoModeDetailPopup from '@/components/dialog/AutoModeDetailPopup'

export default {
  // 컴포넌트 이름
  name: 'App',

  // 자식 컴포넌트 정의
  components: {
    Drawer,
    AppBar,
    AlertDialog,
    LoginDialog,
    ModifyUserDialog,
    ModifyPasswordDialog,
    AIModeDialog,
    AlarmNotifyDialog,
    AIModeDialogOfJi,
    AutoModeDetailPopup
  },

  // 컴포넌트 데이터
  data: () => ({
    //
  }),
  methods: {
    
  },
  // 컴포넌트가 DOM에 추가될 때, 실행되는 라이프 사이클 훅
  mounted: function() {
    // 10초 간격으로 알람 API 호출
    this.$store.dispatch(GET_ALARM_NOTIFY)
    this.timer = setInterval(() => {
      this.$store.dispatch(GET_ALARM_NOTIFY)
    }, 10 * 1000)
  },
  // 컴포넌트가 제거 될 때 실행되는 라이프 사이클 훅
  destroyed: function() {
    // 알람 API 호출 제거
    clearInterval(this.timer)
  }
};
</script>
<style lang="scss">
// Noto Sans 폰트
@font-face {
  font-family:'Noto Sans CJK KR';
  src: url('assets/font/NotoSansCJKkr-Regular.ttf') format('truetype');
}
// 한국수력원자력 폰트
@font-face {
  font-family:'KHNPHUotfR';
  src: url('assets/font/KHNPHUotfR.ttf') format('truetype');
}
// LAB디지털 폰트(숫자 디지털 표시)
@font-face {
  font-family:'LAB디지털';
  src: url('assets/font/LAB디지털.ttf') format('truetype');
}
// 나눔 스퀘어 폰트
@font-face {
  font-family:'NanumSquare';
  src: url('assets/font/NanumSquareR.ttf') format('truetype')
      url('assets/font/NanumSquareR.otf') format('opentype');
}
@font-face {
  font-family:'NanumSquare';
  src: url('assets/font/NanumSquareB.ttf') format('truetype')
      url('assets/font/NanumSquareB.otf') format('opentype');
  font-weight: 700;
}
@font-face {
  font-family:'NanumSquare';
  src: url('assets/font/NanumSquareEB.ttf') format('truetype')
    url('assets/font/NanumSquareEB.otf') format('opentype');
  font-weight: 800;
}
@font-face {
  font-family:'NanumSquare';
  src: url('assets/font/NanumSquareL.ttf') format('truetype')
      url('assets/font/NanumSquareL.ttf') format('opentype');
  font-weight: 300;
}
// 단위에 필요한 폰트(k, k+1, i, i+1)
@font-face {
  font-family: 'Dynalight';
  src: url('assets/font/Dynalight-Regular.ttf') format('truetype');
}
// 모든 기본 폰트는 한수원 폰트
* {
  font-family: 'KHNPHUotfR', sans-serif !important;
}
// 로그인시 password 입력란은 폰트 적용 안함
input[type=password] {
  font-family: none !important;
}
// 대시보드 배경화면
.common {
  background: #101320 !important;
  background-position: center;
  background-repeat: no-repeat;
  background-size: auto;
  overflow: hidden;
  cursor: default;
  user-select : none;
}
// 공정별 배경화면
.disinfection {
  background-image: url('assets/disinfection/disinfection_background.png') !important;
  background-position: center;
  background-repeat: no-repeat;
  background-size: auto;
  overflow: hidden;
  cursor: default;
  user-select : none;
}
// 전체화면이 아닐 때 스크롤은 보이지 않지만 스크롤은 가능하도록 설정
html {
  overflow-y: auto !important;
}
// 크롬에서 스크롤바를 보이지 않도록 설정
html::-webkit-scrollbar {
  display: none;
}
// Vuetify Navigation 커스터마이징
.drawer-custom.v-navigation-drawer--mini-variant {
  overflow: visible;
}
.drawer-custom.v-navigation-drawer {
  overflow: visible;
}
.v-application--is-ltr .v-list--dense .v-list-group--sub-group .v-list-group__header {
  padding: 0px 16px !important;
}
.v-application--is-ltr .v-list-group--sub-group .v-list-group__header {
  padding: 0px 16px !important;
}
.v-application--is-ltr .v-list-group--no-action.v-list-group--sub-group > .v-list-group__items > .v-list-item {
  padding-left: 57px;
}
.v-application--is-ltr .v-list-group--no-action > .v-list-group__items > .v-list-item {
  padding-left: 16px;
}  

/* LoginDialog Custom CSS */
.login-background .custom-label-color .theme--light.v-label {
  font-size: 16px;
  color: white !important;
}
.login-background .custom-label-color input {
  font-size: 16px;
  color: white !important;
}
.login-background .v-text-field > .v-input__control > .v-input__slot:before {
  font-size: 16px;
  border-color: white !important;
}

/* v-date-picker 색상 변경 */
.custom .theme--light.v-picker__body {
  background-color: rgb(26, 69, 130);
}
.custom .theme--light.v-btn {
  color: #fff;
}
.custom .theme--light.v-date-picker-header .v-date-picker-header__value:not(.v-date-picker-header__value--disabled) button:not(:hover):not(:focus) {
  color: #fff;
}
.custom .theme--light.v-date-picker-table th, .theme--light.v-date-picker-table .v-date-picker-table--date__week {
  color: #fff;
}
/* v-select css 변경 */
.v-input__control {
  height: 100%;
}
.v-input--is-focused .v-input{
  border: 1px solid #5cafff;
}
.v-text-field--outlined > .v-input__control > .v-input__slot{
  min-height: 35px;
  border: 1px solid #496097;
}
// 사용자 정보 수정,추가 input 
.modify-contents .v-text-field--outlined > .v-input__control > .v-input__slot{
  border: 1px solid #87f4f4;
  border-radius: 0;
}
.modify-contents .v-text-field.v-text-field--enclosed:not(.v-text-field--rounded) > .v-input__control > .v-input__slot {
  padding:0;
}
.modify-contents .v-input {
  width: 270px;
}
.modify-contents .theme--light.v-select .v-select__selection--comma {
  color: #fff !important;
}
// 
.v-text-field--outlined.v-input--is-focused fieldset {
  border: none;
  height: 35px !important;
}
.v-text-field.v-text-field--enclosed:not(.v-text-field--rounded) > .v-input__control > .v-input__slot {
  padding:0 0 0 10px !important;
}
.v-select__slot {
  height: 35px;
}
.theme--light.v-select .v-select__selection--comma {
  font-size: 16px;
  color: #c9edfb !important;
}
.v-input__icon {
  height: 35px !important;
  background: url('~@/assets/alarmHistory/under_arrow.png')center no-repeat;
}
.v-input__append-inner {
  margin: 0 !important;
  align-self: center;
}
.v-select.v-select--is-menu-active .v-input__icon {
  rotate: 180deg !important;
}
.theme--light.v-sheet {
  background-color: #1a3462;
}
.theme--light.v-list-item:not(.v-list-item--active):not(.v-list-item--disabled) {
  color: #fff !important;
}
.theme--light.v-list-item--active::before {
  opacity: 0.2 !important;
}
.v-list {
  padding: 0;
}
.v-list .v-list-item--active {
  color: #b4dffa !important;
}
.v-list-item {
  min-height: 35px;
}
.v-list-item .v-list-item__title {
  color: #b4dffa;
}
/* v-dialog(LSTM) 팝업시 스크롤바가 생기는 이슈 */
.v-dialog {
  overflow: hidden !important;
}

/* 공정별 우측 상단의 AI운전모드, EMS운전모드 제어 컴포넌트*/
.right{
  width: 250px;
  margin-left: auto;
  margin-right: 45px;
  z-index: 150;
  .right-contents{
    display: flex;
    justify-content: center;
    width: 100%;
    margin-top: 20px;
    &__wrap {
          display: flex;
          justify-content: flex-end;
          align-items: center;
          margin-bottom: 5px;
        } 
    // AI 운전 모드 글자 스타일
    &__text-first{
      text-shadow: 0 0 9px #5cafff;
      font-family: "KHNPHUotfR";
      font-size: 18px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      letter-spacing: normal;
      text-align: center;
      color: #c3eaff;
    }
    // AI 운전 모드 버튼 위치
    &__btn-first{
      // width: 60px;
      flex: 1;
      height: 28px;
      margin-left: 8px;
      // AI 운전 모드 버튼 스타일
      .checkbox{
        position:relative;
        cursor:pointer;
        appearance:none;
        width:60px;
        height:28px;
        border-radius: 14px;
        border: solid 1px #417290;
        background-color: rgba(139, 194, 240, 0.25);
        outline:none;
        transition:0.3s;
      }
      // AI 운전 모드 버튼 스타일(선택 안됐을 때, OFF)
      .checkbox::before{
        content:"OFF";
        position:absolute;
        height:22px;
        width:29px;
        border-radius:11px;
        background:#b4dffa;
        top:2px;
        left:2px;
        transition:0.3s ease-in-out;
        font-size: 11px;
        font-family: KHNPHUotfR;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 2;
        letter-spacing: normal;
        text-align: center;
        color: #19274e;
        background-color: rgba(122, 155, 175, 0.25);
      }
      // AI 운전 모드 버튼 스타일(선택 됐을 때, ON)
      .checked::before{
        content:"AI";
        transform:translateX(25px);
        background:#b4dffa;
      }
      // AI 운전 모드 버튼 스타일(선택 됐을 때, ON)
      .checked{
        border-color:#b4dffa;
      } 
    }
    // EMS 운전 모드 글자 스타일
    &__text-second{
      text-shadow: 0 0 9px #5cafff;
      font-family: "KHNPHUotfR";
      font-size: 18px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      letter-spacing: normal;
      text-align: left;
      color: #80b6ff;
    }
    // EMS 운전 모드 버튼 위치
    &__btn-second{
      width: 36px;
      height: 28px;
      margin-left: auto;
      // EMS 운전 모드 버튼 스타일
      .checkbox{
        position:relative;
        cursor:pointer;
        appearance:none;
        width:60px;
        height:28px;
        border-radius: 14px;
        border: solid 1px #417290;
        background-color: rgba(139, 194, 240, 0.25);
        outline:none;
        transition:0.3s;
      }
      // EMS 운전 모드 버튼 스타일(선택 안됐을 때, OFF)
      .checkbox::before{
        content:"OFF";
        position:absolute;
        height:22px;
        width:29px;
        border-radius:11px;
        background-color: rgba(122, 155, 175, 0.25);
        top:2px;
        left:2px;
        transition:0.3s ease-in-out;
        font-size: 11px;
        font-family: KHNPHUotfR;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: 2;
        letter-spacing: normal;
        text-align: center;
        color: #19274e;
      }
      // EMS 운전 모드 버튼 스타일(선택 됐을 때, ON)
      .checked::before{
        content:"AI";
        transform:translateX(25px);
        background:#80b6ff;
      }
      // AI 운전 모드 버튼 스타일(선택 됐을 때, ON)
      .checked{
        border-color:#80b6ff;
      } 
    }
    // 최대 여과 지속 시간 설정
    &__text-third {
      text-shadow: 0 0 9px #5cafff;
      font-family: "KHNPHUotfR";
      font-size: 14px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      letter-spacing: normal;
      text-align: left;
      color: #c3eaff;
    }
    &__input-box {
      width: 52px;
      height: 25px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: solid 1px rgba(157, 191, 255, 0.3);
      margin: 0px 5px;
      > input {
        width: 63px;
        height: 24px;
        color: #c3eaff;
        text-align: center;
      }
      span {
        color: #c3eaff;
      }
    }
    &__icon {
      height: 52px;
      position: absolute;
      top: 70px;
      right: 40px;
      display: flex;
      justify-content: space-between;
      flex-direction: column;
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
      background-image: url('assets/disinfection/icon_pencil.png');
    }
    &__checkbox {
      width: 16px;
      height: 12px;
      background-image: url('assets/disinfection/icon_checkbox.png');
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
      background-image: url('assets/disinfection/icon_cancel.png');
    }
  }
}
// 대시보드 AI운전모드 스타일
.control_box_operation {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 140px;
  height: 28px;
  padding: 0px 1px;
  border-radius: 14px;
  background-color: rgba(139, 194, 240, 0.25);
  // AI운전모드 버튼 스타일
  &__btn {
    height: 22px;
    color: #19274e;
    font-size: 11px;
    margin: 0px 1px;
    padding: 4px 10px 4px 10px;
    border-radius: 11px;
    cursor: pointer;
    // AI운전모드 버튼 스타일(선택 됐을 때)
    &--on {
      box-shadow: 0 0 6px 0 #e8faff;
      background-color: #b4dffa;
    }
    // AI운전모드 버튼 스타일(선택 안됐을 때)
    &--off {
      background-color: #417290;
    }
  }
}
// 공정별 예측결과 시간 스타일
.timerbox {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 185px;
  height: 37px;
  font-family: "LAB디지털" !important;
  font-size: 18px;
  color: #b4dffa;
  padding: 4px;
  border: solid 1px #4e6b93;
  background-color: rgba(157, 191, 255, 0.07);
}
.v-app-bar.v-app-bar--fixed {
  z-index: 151 !important;
}
</style>