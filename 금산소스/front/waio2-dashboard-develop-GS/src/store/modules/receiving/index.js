// 착수 store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

export const URL = {
  RECEIVING_LATEST: 'receiving/latest',
  RECEIVING_HISTORY_FLOW_OUT: 'receiving/history/fr/out',
  RECEIVING_CONTROL_OPERATION: 'receiving/control/operation',
  RECEIVING_CONTROL_LEVEL: 'receiving/control/le',
}
export const GET_RECEIVING_LATEST = URL.RECEIVING_LATEST + '/get'
export const PUT_RECEIVING_HISTORY_FLOW_OUT = URL.RECEIVING_LATEST + '/put'
export const PUT_RECEIVING_CONTROL_OPERATION = URL.RECEIVING_CONTROL_OPERATION + '/put'
export const PUT_RECEIVING_CONTROL_LEVEL = URL.RECEIVING_CONTROL_LEVEL + '/put'
export const GET_LIV_RECEIVING_LATEST = URL.RECEIVING_LATEST + '/liv' +'/get'
const GET_LATEST = GET_RECEIVING_LATEST.substr(GET_RECEIVING_LATEST.indexOf('/') + 1)
const PUT_HISTORY_FLOW_OUT = PUT_RECEIVING_HISTORY_FLOW_OUT.substr(PUT_RECEIVING_HISTORY_FLOW_OUT.indexOf('/') + 1)
const PUT_CONTROL_OPERATION = PUT_RECEIVING_CONTROL_OPERATION.substr(PUT_RECEIVING_CONTROL_OPERATION.indexOf('/') + 1)
const PUT_CONTROL_LEVEL = PUT_RECEIVING_CONTROL_LEVEL.substr(PUT_RECEIVING_CONTROL_LEVEL.indexOf('/') + 1)
const GET_LIV_LATEST = GET_LIV_RECEIVING_LATEST.substr(GET_LIV_RECEIVING_LATEST.indexOf('/') + 1)
const SET_MODIFYED_FROM_LATEST = "setModifyedFromLatest"

import { CLOSE_AI_MODE_DIALOG } from '@/store/modules/dialog'

export default {
  namespaced: true,
  state: {
    isModifyMode: false,
    processStep: 1,
    latest: {
      upd_ti: null,
      ai_opr: null,
      ai_liv_opr: null,
      ai_ind_opr:null,
      ems_mode: null,
      h_target_le_max: null,
      h_target_le_min: null,
      b_in_fr_i: null,
      b_in_fr_q: null,
      ai_b_in_fri: null,
      ai_b2_in_fr: null,
      ai_b_in_fr_rtd: null,
      b_in_pr: null,
      b1_vv_po: null,
      b2_vv_po: null,
      b1_out_fr: null,
      b2_out_fr: null,
      ai_b1_vv_po: null,
      ai_b2_vv_po: null,
      h_location_le1: null,
      h_location_le2: null,
      h_location_le3: null,
      h_location_le4: null,
      h_out_fr: null,
      ai_h_out_fr: null,
      bi_cl_fr : null,
      b_pump_on: null,
      ai_b_pump_cnt: null,
      ai_b_pump_cnt_dashboard: null
    },
    latestModify: {
      upd_ti: null,
      ai_opr: null,
      ai_liv_opr: null,
      ai_ind_opr:null,
      ems_mode: null,
      h_target_le_max: null,
      h_target_le_min: null,
      b_in_fr_i: null,
      b_in_fr_q: null,
      ai_b_in_fri: null,
      ai_b2_in_fr: null,
      ai_b_in_fr_rtd: null,
      b_in_pr: null,
      b1_vv_po: null,
      b2_vv_po: null,
      b1_out_fr: null,
      b2_out_fr: null,
      ai_b1_vv_po: null,
      ai_b2_vv_po: null,
      h_location_le1: null,
      h_location_le2: null,
      h_location_le3: null,
      h_location_le4: null,
      h_out_fr: null,
      ai_h_out_fr: null,
      bi_cl_fr : null,
      b_pump_on: null,
      ai_b_pump_cnt: null,
      ai_b_pump_cnt_dashboard: null
    },
    out_fr: {
      series1: null,
      series2: null
    }
  },
  getters: {
    isAiOperationMode: function(state){
      if(state.latest.ai_ind_opr === 2 && state.latest.ai_liv_opr === 2){
        return true
      } else {
        return false
      }
    }
  },
  mutations: {
    [GET_LATEST]: function(state, data) {
      state.latest = data
      const fixed_h_target_le_max = data.h_target_le_max.toFixed(1)
      const fixed_h_target_le_min = data.h_target_le_min.toFixed(1)
      const Fixed = {
        h_target_le_max: fixed_h_target_le_max,
        h_target_le_min: fixed_h_target_le_min,
      }
      state.latest = Object.assign(state.latest, Fixed)
      if (state.isModifyMode === false) {
        state.latestModify = Object.assign(state.latest, Fixed)
      }
    },
    [SET_MODIFYED_FROM_LATEST]: function (state) {
      state.latestModify = Object.assign({}, state.latest)
    },
    [PUT_HISTORY_FLOW_OUT]: function(state, data) {
      state.out_fr.series1 = data.out_fr.series1
      state.out_fr.series2 = data.out_fr.series2
    },
    [PUT_CONTROL_OPERATION]: function (state, data) {
      state.latest.ai_opr = data
    }
  },
  actions: {
    [GET_LATEST]: async function ({ state, commit }) {
      await axios.get(`${DEV_SERVER}/${URL.RECEIVING_LATEST}/`+ state.processStep)
        .then(({ data }) => {
          commit(GET_LATEST, data.latest)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [GET_LIV_LATEST]: async function ( { commit } ) {
      await axios.get(`${DEV_SERVER}/${URL.RECEIVING_LATEST}/1`)
        .then(({ data }) => {
          commit(GET_LATEST, data.latest)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_HISTORY_FLOW_OUT]: async function ({ state, commit }) {
      let nowTimestamp = Date.now()
      // nowTimestamp = Date.parse("2022-12-25 10:00:00"); // FIXME 현재 날짜 수정
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      await axios.put(`${DEV_SERVER}/${URL.RECEIVING_HISTORY_FLOW_OUT}/${state.processStep}`, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
      // await axios.put(`${DEV_SERVER}/${URL.RECEIVING_HISTORY_FLOW_OUT}/${state.processStep}`, { 'start_time': new Date(2021, 9, 17, 18, 0, 0).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
        .then(({ data }) => {
          commit(PUT_HISTORY_FLOW_OUT, data)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_CONTROL_OPERATION]: async function ({ commit, rootState }, { operation }) {
      await axios.put(`${DEV_SERVER}/${URL.RECEIVING_CONTROL_OPERATION}/${rootState.dialog.aiMode.receivingIndex}`, { 'operation': operation })
      .then(() => {
        // commit(PUT_CONTROL_OPERATION, operation)
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
    [PUT_CONTROL_LEVEL]: async function ({ state, commit }, { h_target_le_max, h_target_le_min }) {
      await axios.put(`${DEV_SERVER}/${URL.RECEIVING_CONTROL_LEVEL}/${state.processStep}`, { h_target_le_max, h_target_le_min })
      .then(() => {
        let _data = {
          visible: true,
          title: '설정 성공',
          text1: '설정값이 변경되었습니다.'
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
    }
  }
}