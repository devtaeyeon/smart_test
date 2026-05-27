import Vue from 'vue'
import Vuex from 'vuex'
import HighchartsVue from 'highcharts-vue'
import vueMoment from 'vue-moment'
import moment from 'moment-timezone'
import login from '@/store/modules/login'
import rules from '@/store/modules/rules'
import alertDialog from '@/store/modules/alertDialog'
import loginHistory from '@/store/modules/loginHistory'

import addUserDialog from '@/store/modules/user/addUserDialog'
import modifyUserDialog from '@/store/modules/user/modifyUserDialog'
import modifyPasswordDialog from '@/store/modules/user/modifyPasswordDialog'
import deleteUserDialog from '@/store/modules/user/deleteUserDialog'
import loginDialog from '@/store/modules/user/loginDialog'
import user from '@/store/modules/user/user'

import alarm from '@/store/modules/alarm/alarm'
import modifyAlarmDialog from '@/store/modules/alarm/modifyAlarmDialog'

import drawer from '@/store/modules/drawer'
import performance from '@/store/modules/performance'
import network from '@/store/modules/network'

import filter from '@/store/modules/filter/index'
import receiving from '@/store/modules/receiving/index'
import clear from '@/store/modules/clear/index'
import gac from '@/store/modules/gac/index'
import mixing from '@/store/modules/mixing/index'
import sedimentation from '@/store/modules/sedimentation'
import raw from '@/store/modules/raw'
import coagulants from '@/store/modules/coagulants'
import ems from '@/store/modules/ems'
import ozone from '@/store/modules/ozone'
import pms from '@/store/modules/pms'
import disinfection from '@/store/modules/disinfection/index'

import dialog from '@/store/modules/dialog'

import VueVideoPlayer from 'vue-video-player'
import 'video.js/dist/video-js.css'

import common from '@/store/common'
import aioprhistory from '@/store/modules/aioprhistory'

Vue.use(VueVideoPlayer)

Vue.use(vueMoment, {
  moment
})
Vue.use(Vuex)
Vue.use(common)
Vue.use(HighchartsVue)

export const API_SERVER = '/api'
export const JSON_SERVER = '/json'
export const DEV_SERVER = '/api_xai'
// export const DEV_SERVER = ''

export const LATEST_DASHBOARD_ID = 'dashboard/latest'
export const DASHBOARD = 'dashboard'
export const GET_DASHBOARD = 'getDashboard'
export const POST_DASHBOARD = 'postDashboard'
export const PUT_DASHBOARD = 'putDashboard'
export const DEL_DASHBOARD = 'delDashboard'

export const DASHBOARDS = 'dashboards'
export const POST_DASHBOARDS = 'postDashboards'
export const PUT_DASHBOARDS = 'putDashboards'
export const DEL_DASHBOARDS = 'delDashboards'


export const LISTS = 'lists'
export const DEL_LISTS = 'delLists'
export const PUT_LISTS = 'putLists'
export const POST_LISTS = 'postLists'
export const CLEAR_LISTS = 'clearLists'
export const CLEAR_SHAPES = 'clearShapes'

export const PATHS = 'paths'
export const POST_PATHS = 'postPaths'
export const PUT_PATHS = 'putPaths'
export const DEL_PATHS = 'delPaths'

export const SET_OVERLAY = 'SET_OVERLAY'
export const SERVICE_URL = {
  XAI: 'http://10.93.3.30:38080',
  EMS: 'http://10.93.3.30:38085',
  //PMS: 'http://123.214.174.18:18096',
  CCTV: 'http://10.231.21.230:20102/index'
}
export const COLOR_HIGH = 'rgba(212, 110, 250, 0.4)'
export const COLOR_MID = 'rgba(126, 110, 250, 0.4)'
export const PLOT_BANDS_SPRING = [{
  color: COLOR_MID,
  from: 9,
  to: 10,
  status: 'mid'
},{
  color: COLOR_MID,
  from: 12,
  to: 13,
  status: 'mid'
},{
  color: COLOR_MID,
  from: 17,
  to: 23,
  status: 'mid'
},{
  color: COLOR_HIGH,
  from: 10,
  to: 12,
  status: 'high'
},{
  color: COLOR_HIGH,
  from: 13,
  to: 17,
  status: 'high'
}]
export const PLOT_BANDS_SUMMER = [{
  color: COLOR_MID,
  from: 9,
  to: 10,
  status: 'mid'
},{
  color: COLOR_MID,
  from: 12,
  to: 13,
  status: 'mid'
},{
  color: COLOR_MID,
  from: 17,
  to: 23,
  status: 'mid'
},{
  color: COLOR_HIGH,
  from: 10,
  to: 12,
  status: 'high'
},{
  color: COLOR_HIGH,
  from: 13,
  to: 17,
  status: 'high'
}]
export const PLOT_BANDS_WINTER = [{
  color: COLOR_MID,
  from: 9,
  to: 10,
  status: 'mid'
},{
  color: COLOR_MID,
  from: 12,
  to: 17,
  status: 'mid'
},{
  color: COLOR_MID,
  from: 20,
  to: 22,
  status: 'mid'
},{
  color: COLOR_HIGH,
  from: 10,
  to: 12,
  status: 'high'
},{
  color: COLOR_HIGH,
  from: 17,
  to: 20,
  status: 'high'
},{
  color: COLOR_HIGH,
  from: 22,
  to: 23,
  status: 'high'
}]
export const SPRING = 0
export const SUMMER = 1
export const WINTER = 2
const store = new Vuex.Store({
  modules: {
    login,
    rules,
    user,
    modifyPasswordDialog,
    modifyUserDialog,
    addUserDialog,
    alertDialog,
    deleteUserDialog,
    loginDialog,
    loginHistory,
    alarm,
    modifyAlarmDialog,
    drawer,
    performance,
    network,
    sedimentation,
    filter,
    receiving,
    clear,
    gac,
    mixing,
    raw,
    coagulants,
    dialog,
    ems,
    ozone,
    pms,
    disinfection,
    aioprhistory
  },
  state: {
    currentDashboardTitle: '스마트 정수장 AI 플랫폼 (금산)', // 타이틀
    showConfirmDialog: false, // 확인 Dialog
    // App.vue router main css
    css: {
      content: {
        paddingLeft: '0px'
      }
    },
    overlay: false, // API 요청시 OverLay Loading Image
    backgroundIndex: 1, // 배경 상태 셀렉터

    selectedBuildingIndex: 0, // 현재 선택되어 있는 공정(default. 착수)
    isSelectedMainMenuIndex7AiOn: true, // Dashboard.vue 탈수기동 AI모드
    isSelectedMainMenuIndex9AiOn: true, // Dashboard.vue 농축조 AI모드
  },
  getters: {
    /**
     * 상태에 따라 배경화면 선택
     * @param state 상태
     * @returns 
     */
    backgroundImage: function (state) {
      if (state.backgroundIndex === 1) {
        return 'common'
      } else {
        return 'disinfection'
      }
    },
  },
  mutations: {
    [SET_OVERLAY]: function (state, data) {
      state.overlay = data
    }
  },
  actions: {
  }
})


export default store