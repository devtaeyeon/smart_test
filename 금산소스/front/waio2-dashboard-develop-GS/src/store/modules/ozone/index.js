// 오존 store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

const URL = {
  OZONE_LATEST: 'ozone/latest',
  OZONE_CONTROL_OPERATION: 'ozone/control/operation',
  OZONE_HISTORY_INJECTION: 'ozone/history/injection',
  OZONE_HISTORY_DE_POST: 'ozone/history/de/post',
  OZONE_HISTORY_DE: 'ozone/history/de',
  OZONE_CONTROL_AI: 'ozone/control/ai'
}
export const GET_OZONE_LATEST = URL.OZONE_LATEST + '/get'
export const PUT_OZONE_CONTROL_OPERATION = URL.OZONE_CONTROL_OPERATION + '/put'
export const PUT_OZONE_HISTORY_INJECTION = URL.OZONE_HISTORY_INJECTION + '/put'
export const PUT_OZONE_HISTORY_DE_POST = URL.OZONE_HISTORY_DE_POST + '/put'
export const PUT_OZONE_HISTORY_DE = URL.OZONE_HISTORY_DE + '/put'
export const PUT_OZONE_CONTROL_AI = URL.OZONE_CONTROL_AI + '/put'
const GET_LATEST = GET_OZONE_LATEST.substr(GET_OZONE_LATEST.indexOf('/') + 1)
const PUT_CONTROL_OPERATION = PUT_OZONE_CONTROL_OPERATION.substr(PUT_OZONE_CONTROL_OPERATION.indexOf('/') + 1)
const PUT_HISTORY_INJECTION = PUT_OZONE_HISTORY_INJECTION.substr(PUT_OZONE_HISTORY_INJECTION.indexOf('/') + 1)
const PUT_HISTORY_DE_POST = PUT_OZONE_HISTORY_DE_POST.substr(PUT_OZONE_HISTORY_DE_POST.indexOf('/') + 1)
const PUT_HISTORY_DE = PUT_OZONE_HISTORY_DE.substr(PUT_OZONE_HISTORY_DE.indexOf('/') + 1)
const PUT_CONTROL_AI = PUT_OZONE_CONTROL_AI.substr(PUT_OZONE_CONTROL_AI.indexOf('/') + 1)
const SET_MODIFYED_FROM_LATEST = "setModifyedFromLatest"

import { CLOSE_AI_MODE_DIALOG } from '@/store/modules/dialog'
export default {
  namespaced: true,
  state: {
    isModifyMode: false,
    latest: {
      upd_ti: null,
      ai_opr: null,
      io_inr: null,
      ai_io_inr: null,
      io_de: null,
      io_pre1_de: null,
      io_peri1_de: null,
      io_post1_de: null,
      io_pre2_de: null,
      io_peri2_de: null,
      io_ser_de: null,
      io_injection_min: null,
      io_og_1_min: null,
      io_og_1_max: null,
      io_og_2_min: null,
      io_og_2_max: null,
      io_og_qu: null,
      io_set: null,
      io_hrt1: null,
      io_hrt2: null,
      io_hrt3: null,
      ai_io_de1: null,
      ai_io_de2: null,
      ai_io_de3: null,
      io_hrt_q1: null,
      io_hrt_q2: null,
      io_hrt_q3: null,
      ai_io_ln2: null,
      ai_io_ln3: null,
      ai_io_k2: null,
      ai_io_k3: null,
      ai_io_c0: null,
      ai_io_id: null,
      ai_io_state: null,
      ai_io_normal: null,
      ai_io_abnormal1: null,
      ai_io_abnormal2: null
    },
    latestModify: {
      upd_ti: null,
      ai_opr: null,
      io_inr: null,
      ai_io_inr: null,
      io_de: null,
      io_pre1_de: null,
      io_peri1_de: null,
      io_post1_de: null,
      io_pre2_de: null,
      io_peri2_de: null,
      io_ser_de: null,
      io_injection_min: null,
      io_og_1_min: null,
      io_og_1_max: null,
      io_og_2_min: null,
      io_og_2_max: null,
      io_og_qu: null,
      io_set: null,
      io_hrt1: null,
      io_hrt2: null,
      io_hrt3: null,
      ai_io_de1: null,
      ai_io_de2: null,
      ai_io_de3: null,
      io_hrt_q1: null,
      io_hrt_q2: null,
      io_hrt_q3: null,
      ai_io_ln2: null,
      ai_io_ln3: null,
      ai_io_k2: null,
      ai_io_k3: null,
      ai_io_c0: null,
      ai_io_id: null,
      ai_io_state: null,
      ai_io_normal: null,
      ai_io_abnormal1: null,
      ai_io_abnormal2: null
    },
    historyInjection: null,
    historyDePost: null,
    historyDe: null
  },
  getters: {
  },
  mutations: {
    [GET_LATEST]: function (state, data) {
      state.latest = data
      const fixed_io_set = data.io_set.toFixed(2)
      const fixed_io_injection_max = data.io_injection_max.toFixed(2)
      const fixed_io_injection_min = data.io_injection_min.toFixed(2)
      const fixed_io_target_de = data.io_target_de.toFixed(2)
      const Fixed = {
        io_set: fixed_io_set,
        io_injection_max: fixed_io_injection_max,
        io_injection_min: fixed_io_injection_min,
        io_target_de: fixed_io_target_de
      }
      state.latest = Object.assign(state.latest, Fixed)
      if (state.isModifyMode === false) {
        state.latestModify = Object.assign(state.latest, Fixed)
      }
    },
    [SET_MODIFYED_FROM_LATEST]: function (state) {
      state.latestModify = Object.assign({}, state.latest)
    },
    [PUT_CONTROL_OPERATION]: function (state, data) {
      state.latest.ai_opr = data
    },
    [PUT_HISTORY_INJECTION]: function (state, data) {
      state.historyInjection = data
    },
    [PUT_HISTORY_DE_POST]: function (state, data) {
      state.historyDePost = data
    },
    [PUT_HISTORY_DE]: function (state, data) {
      state.historyDe = data
    },
  },
  actions: {
    [GET_LATEST]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${URL.OZONE_LATEST}`)
        .then(({ data }) => {
          // const getData = data.latest
          // const numFormatData = {}
          // const getDataNumFormat = Object.assign(getData, )
          commit(GET_LATEST, data.latest)
          console.log('latest', data.latest)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_CONTROL_OPERATION]: async function ({ commit }, { operation }) {
      await axios.put(`${DEV_SERVER}/${URL.OZONE_CONTROL_OPERATION}`, { 'operation': operation })
      .then(() => {
        commit('dialog/'+ CLOSE_AI_MODE_DIALOG, null, { root: true })
        let _data = {
          visible: true,
          title: '제어 성공',
          text1: '운전모드 변경요청 완료'
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
    },
    [PUT_HISTORY_INJECTION]: async function ({ commit }) {
      let nowTimestamp = Date.now()
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      await axios.put(`${DEV_SERVER}/${URL.OZONE_HISTORY_INJECTION}`, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
        .then(({ data }) => {
          commit(PUT_HISTORY_INJECTION, data.injection)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_HISTORY_DE_POST]: async function ({ commit }) {
      let nowTimestamp = Date.now()
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      await axios.put(`${DEV_SERVER}/${URL.OZONE_HISTORY_DE_POST}`, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
        .then(({ data }) => {
          commit(PUT_HISTORY_DE_POST, data.post_de)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_HISTORY_DE]: async function ({ commit }) {
      let nowTimestamp = Date.now()
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      await axios.put(`${DEV_SERVER}/${URL.OZONE_HISTORY_DE}`, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
        .then(({ data }) => {
          commit(PUT_HISTORY_DE, data.de)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_CONTROL_AI]: async function ({ commit }, { io_injection_max, io_injection_min, io_og_1_min, io_og_1_max, io_og_2_min, io_og_2_max, io_set, io_target_de }) { // eslint-disable-line no-unused-vars
      await axios.put(`${DEV_SERVER}/${URL.OZONE_CONTROL_AI}`, { io_injection_max, io_injection_min, io_og_1_min, io_og_1_max, io_og_2_min, io_og_2_max, io_set, io_target_de })
      .then(() => {
        let _data = {
          visible: true,
          title: '설정 성공',
          text1: '설정값이 변경되었습니다'
        }
        commit('alertDialog/OPEN_DIALOG', _data, { root: true })
      })
      .catch(error => {
        util.printError(error)
        let _data = {
          visible: true,
          title: '설정 실패',
          text1: '관리자에게 문의해주세요'
        }
        commit('alertDialog/OPEN_DIALOG', _data, { root: true })
        commit(SET_MODIFYED_FROM_LATEST)
      })
    },
  }
}