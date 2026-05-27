// 정수지 store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

export const URL = {
  CLEAR_HISTORY_FLOW_OUT: 'clear/history/fr/out',
  CLEAR_HISTORY_LEVEL: 'clear/history/le',
  CLEAR_HISTORY_CL: 'clear/history/cl'
}
export const PUT_CLEAR_HISTORY_FLOW_OUT = URL.CLEAR_HISTORY_FLOW_OUT + '/put'
export const PUT_CLEAR_HISTORY_LEVEL = URL.CLEAR_HISTORY_LEVEL + '/put'
export const PUT_CLEAR_HISTORY_CL = URL.CLEAR_HISTORY_CL + '/put'
const PUT_HISTORY_FLOW_OUT = PUT_CLEAR_HISTORY_FLOW_OUT.substr(PUT_CLEAR_HISTORY_FLOW_OUT.indexOf('/') + 1)
const PUT_HISTORY_LEVEL = PUT_CLEAR_HISTORY_LEVEL.substr(PUT_CLEAR_HISTORY_LEVEL.indexOf('/') + 1)
const PUT_HISTORY_CL = PUT_CLEAR_HISTORY_CL.substr(PUT_CLEAR_HISTORY_CL.indexOf('/') + 1)
export default {
  namespaced: true,
  state: {
    processStep: 1,
    out_fr: null,
    le: null,
    h_cl: {
      in: null,
      out: null
    }
  },
  getters: {
  },
  mutations: {
    [PUT_HISTORY_FLOW_OUT]: function(state, data) {
      state.out_fr = data.out_fr
    },
    [PUT_HISTORY_LEVEL]: function(state, data) {
      state.le = data.le
    },
    [PUT_HISTORY_CL]: function(state, data) {
      state.h_cl.in = data.h_cl.in
      state.h_cl.out = data.h_cl.out
    }
  },
  actions: {
    [PUT_HISTORY_FLOW_OUT]: async function ({ commit, rootState }) {
      let nowTimestamp = Date.now()
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      await axios.put(`${DEV_SERVER}/${URL.CLEAR_HISTORY_FLOW_OUT}/${rootState.receiving.processStep}`, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
        .then(({ data }) => {
          commit(PUT_HISTORY_FLOW_OUT, data)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_HISTORY_LEVEL]: async function ({ rootState, commit }) {
      let now = new Date()
      let year = now.getFullYear()
      let month = now.getMonth()
      let day = now.getDate()
      // FIXME TODO현재 날짜 수정
      // await axios.put(`${DEV_SERVER}/${URL.CLEAR_HISTORY_LEVEL}/${rootState.receiving.processStep}`, { 'start_time': new Date(2022, 11, 24, 0, 0, 0).toISOString(), 'end_time': new Date(2022, 11, 24, 23, 59, 59).toISOString() })
      await axios.put(`${DEV_SERVER}/${URL.CLEAR_HISTORY_LEVEL}/${rootState.receiving.processStep}`, { 'start_time': new Date(year, month, day, 0, 0, 0).toISOString(), 'end_time': new Date(year, month, day, 23, 59, 59).toISOString() })
      .then(({ data }) => {
            commit(PUT_HISTORY_LEVEL, data)
          })
          .catch(error => {
            util.printError(error)
          })
    },
    [PUT_HISTORY_CL]: async function ({ commit }) {
      //TODO, FIXME 날짜 주석 제거
      let nowTimestamp = Date.now()
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      let disinfectionStep = 3 //후차염만 조회
      let processStep = 1 // 생활 단계
      // await axios.put(`${DEV_SERVER}/${URL.CLEAR_HISTORY_CL}/${processStep}/${disinfectionStep}`, { 'start_time': new Date(2012, 11, 17, 0, 0, 0).toISOString(), 'end_time': new Date(2012, 11, 18, 0, 0, 0).toISOString() })
      await axios.put(`${DEV_SERVER}/${URL.CLEAR_HISTORY_CL}/${processStep}/${disinfectionStep}`, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
          .then(({ data }) => {
            commit(PUT_HISTORY_CL, data)
          })
          .catch(error => {
            util.printError(error)
          })
    }
  }
}