// dialog store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

export const OPEN_AI_MODE_DIALOG = 'OPEN_AI_MODE_DIALOG'
export const CLOSE_AI_MODE_DIALOG = 'CLOSE_AI_MODE_DIALOG'
export const CLOSE_ALL_AI_MODE_DIALOG = 'CLOSE_ALL_AI_MODE_DIALOG'
export const OPEN_ALARM_NOTIFY_POPUP = 'OPEN_ALARM_NOTIFY_POPUP'
export const CLOSE_ALARM_NOTIFY_POPUP = 'CLOSE_ALARM_NOTIFY_POPUP'
export const OPEN_AI_FILTER_AND_GAC_SCHEDULE_POPUP = 'OPEN_AI_FILTER_AND_GAC_SCHEDULE_POPUP'
export const CLOSE_AI_FILTER_AND_GAC_SCHEDULE_POPUP = 'CLOSE_AI_FILTER_AND_GAC_SCHEDULE_POPUP'
export const OPEN_AI_MODE_OF_JI_DIALOG = 'OPEN_AI_MODE_OF_JI_DIALOG'
export const CLOSE_AI_MODE_OF_JI_DIALOG = 'CLOSE_AI_MODE_OF_JI_DIALOG'
export const CHANGE_ALL_AI_MODE = 'CHANGE_ALL_AI_MODE'
export const CLOSE_AUTO_MODE_DETAIL_POPUP = 'CLOSE_AUTO_MODE_DETAIL_POPUP'

export default {
  namespaced: true,
  state: {
    aiMode: {
      visible: false,
      expectedValue: null,
      disinfectionIndex: null,
      receivingIndex: null,
      changeAllAIMode: false
    },
    aiModeOfJi: {
      visible: false,
      number: null,
      sedimentationIndex: null,
    },
    aiFilterNGACSchedule: {
      visible: false
    },
    alarmNotify: {
      visible: true
    },
    autoModeDetailPopup:{
      visible: false,
      mixingIndex: null,
    }
  },
  mutations: {
    [OPEN_AI_MODE_DIALOG]: function(state, data) {
      state.aiMode.expectedValue = data
      state.aiMode.visible = true
    },
    [OPEN_AI_MODE_OF_JI_DIALOG]: function(state) {
      state.aiModeOfJi.visible = true
    },
    [CLOSE_AI_MODE_DIALOG]: function(state) {
      state.aiMode.visible = false
    },
    [CLOSE_ALL_AI_MODE_DIALOG]: function(state) {
      state.aiMode.visible = false
      state.aiMode.changeAllAIMode = false
    },
    [CLOSE_AI_MODE_OF_JI_DIALOG]: function(state) {
      state.aiModeOfJi.visible = false
    },
    [OPEN_AI_FILTER_AND_GAC_SCHEDULE_POPUP]: function(state) {
      state.aiFilterNGACSchedule.visible = true
    },
    [CLOSE_AI_FILTER_AND_GAC_SCHEDULE_POPUP]: function(state) {
      state.aiFilterNGACSchedule.visible = false
    },
    [OPEN_ALARM_NOTIFY_POPUP]: function(state) {
      state.alarmNotify.visible = true
    },
    [CLOSE_ALARM_NOTIFY_POPUP]: function(state) {
      state.alarmNotify.visible = false
    },
    [CLOSE_AUTO_MODE_DETAIL_POPUP]: function(state) {
      state.autoModeDetailPopup.visible = false
    },
  },
  actions: {
    [OPEN_AI_MODE_DIALOG]: function ({ commit }, value) {
      commit(OPEN_AI_MODE_DIALOG, value)
    },
    [OPEN_AI_MODE_OF_JI_DIALOG]: function ({ commit }) {
      commit(OPEN_AI_MODE_OF_JI_DIALOG)
    },
    [CLOSE_AI_MODE_DIALOG]: function ({ commit }) {
      commit(CLOSE_AI_MODE_DIALOG)
    },
    [CLOSE_ALL_AI_MODE_DIALOG]: function ({ commit }) {
      commit(CLOSE_ALL_AI_MODE_DIALOG)
    },
    [OPEN_AI_FILTER_AND_GAC_SCHEDULE_POPUP]: function ({ commit }) {
      commit(OPEN_AI_FILTER_AND_GAC_SCHEDULE_POPUP)
    },
    [CLOSE_AI_FILTER_AND_GAC_SCHEDULE_POPUP]: function ({ commit }) {
      commit(CLOSE_AI_FILTER_AND_GAC_SCHEDULE_POPUP)
    },
    [OPEN_ALARM_NOTIFY_POPUP]: function ({ commit }) {
      commit(OPEN_ALARM_NOTIFY_POPUP)
    },
    [CLOSE_ALARM_NOTIFY_POPUP]: function ({ commit }) {
      commit(CLOSE_ALARM_NOTIFY_POPUP)
    },
    [CLOSE_AI_MODE_OF_JI_DIALOG]: function ({commit}) {
      commit(CLOSE_AI_MODE_OF_JI_DIALOG)
    },
    [CLOSE_AUTO_MODE_DETAIL_POPUP]: function({commit}) {
      commit(CLOSE_AUTO_MODE_DETAIL_POPUP)
    },
    [CHANGE_ALL_AI_MODE]: async function ({ commit, state }){
      await axios.put(`${DEV_SERVER}/`+'dashboard/control/operation', {operation: state.aiMode.expectedValue})
        .then(()=>{
          commit(CLOSE_ALL_AI_MODE_DIALOG)
          let _data = {
            visible: true,
            title: '제어 성공',
            text1: '전체 공정 운전모드 변경요청 완료'
          }
          commit('alertDialog/OPEN_DIALOG', _data, { root: true })
        })
        .catch(error => {
          util.printError(error)
          let _data = {
            visible: true,
            title: '제어 실패',
            text1: '관리자에게 문의해주세요'
          }
          commit('alertDialog/OPEN_DIALOG', _data, { root: true })
        })
    }
  }
}