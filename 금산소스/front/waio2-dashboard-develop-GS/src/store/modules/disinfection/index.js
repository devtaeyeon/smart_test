// 소독 store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

const OPEN_LSTM_DIALOG = 'OPEN_LSTM_DIALOG'
const CLOSE_LSTM_DIALOG = 'CLOSE_LSTM_DIALOG'
const OPEN_PRE_DIALOG = 'OPEN_PRE_DIALOG'
const URL = {
  DISINFECTION_LATEST: 'disinfection/latest',
  DISINFECTION_HISTORY_EVAPORATION: 'disinfection/history/evaporation',
  DISINFECTION_HISTORY_CHOLRATE: 'disinfection/history/cholrate',
  DISINFECTION_CONTROL_OPERATION_PRE: 'disinfection/control/operation/pre',
  DISINFECTION_CONTROL_OPERATION_PERI: 'disinfection/control/operation/peri',
  DISINFECTION_CONTROL_OPERATION_POST: 'disinfection/control/operation/post',
  DISINFECTION_CONTROL_PRE: 'disinfection/control/pre',
  DISINFECTION_CONTROL_RERI: 'disinfection/control/peri',
  DISINFECTION_CONTROL_POST: 'disinfection/control/post',
  DISINFECTION_AI_G_CONSUMPTION : 'disinfection/consumption'
}
export const GET_DISINFECTION_LATEST = URL.DISINFECTION_LATEST + '/get'
export const PUT_DISINFECTION_HISTORY_EVAPORATION = URL.DISINFECTION_HISTORY_EVAPORATION + '/put'
export const PUT_DISINFECTION_HISTORY_CHOLRATE = URL.DISINFECTION_HISTORY_CHOLRATE + '/put'
export const PUT_DISINFECTION_CONTROL_OPERATION_PRE = URL.DISINFECTION_CONTROL_OPERATION_PRE + '/put'
export const PUT_DISINFECTION_CONTROL_OPERATION_PERI = URL.DISINFECTION_CONTROL_OPERATION_PERI + '/put'
export const PUT_DISINFECTION_CONTROL_OPERATION_POST = URL.DISINFECTION_CONTROL_OPERATION_POST + '/put'
export const PUT_DISINFECTION_CONTROL_PRE = URL.DISINFECTION_CONTROL_PRE + '/put'
export const PUT_DISINFECTION_CONTROL_PERI = URL.DISINFECTION_CONTROL_RERI + '/put'
export const PUT_DISINFECTION_CONTROL_POST = URL.DISINFECTION_CONTROL_POST + '/put'
export const PUT_DISINFECTION_AI_G_CONSUMPTION = URL.DISINFECTION_AI_G_CONSUMPTION  + '/put'
const GET_LATEST = GET_DISINFECTION_LATEST.substr(GET_DISINFECTION_LATEST.indexOf('/') + 1)
const PUT_HISTORY_EVAPORATION = PUT_DISINFECTION_HISTORY_EVAPORATION.substr(PUT_DISINFECTION_HISTORY_EVAPORATION.indexOf('/') + 1)
const PUT_HISTORY_CHOLRATE = PUT_DISINFECTION_HISTORY_CHOLRATE.substr(PUT_DISINFECTION_HISTORY_CHOLRATE.indexOf('/') + 1)
const PUT_CONTROL_OPERATION_PRE = PUT_DISINFECTION_CONTROL_OPERATION_PRE.substr(PUT_DISINFECTION_CONTROL_OPERATION_PRE.indexOf('/') + 1)
const PUT_CONTROL_OPERATION_PERI = PUT_DISINFECTION_CONTROL_OPERATION_PERI.substr(PUT_DISINFECTION_CONTROL_OPERATION_PERI.indexOf('/') + 1)
const PUT_CONTROL_OPERATION_POST = PUT_DISINFECTION_CONTROL_OPERATION_POST.substr(PUT_DISINFECTION_CONTROL_OPERATION_POST.indexOf('/') + 1)
const PUT_CONTROL_PRE = PUT_DISINFECTION_CONTROL_PRE.substr(PUT_DISINFECTION_CONTROL_PRE.indexOf('/') + 1)
const PUT_CONTROL_PERI = PUT_DISINFECTION_CONTROL_PERI.substr(PUT_DISINFECTION_CONTROL_PERI.indexOf('/') + 1)
const PUT_CONTROL_POST = PUT_DISINFECTION_CONTROL_POST.substr(PUT_DISINFECTION_CONTROL_POST.indexOf('/') + 1)
const PUT_AI_G_CONSUMPTION = PUT_DISINFECTION_AI_G_CONSUMPTION.substr(PUT_DISINFECTION_AI_G_CONSUMPTION.indexOf('/') + 1)
const SET_MODIFYED_FROM_LATEST = "setModifyedFromLatest"
const PROCESS_STEP = 1
import { CLOSE_AI_MODE_DIALOG } from '@/store/modules/dialog'

export default {
  namespaced: true,
  state: {
    isModifyMode: false,
    dialog: {
      lstm: {
        visible: false
      },
      pre: {
        visible: false
      }
    },
    disinfectionStep: 1,
    latest: {
      upd_ti: null,
      pre_ai_opr: null,
      peri_ai_opr: null,
      post_ai_opr: null,
      d1_target_cl: null,
      d2_target_cl: null,
      d1_cl: null,
      e_ser_cl: null,
      d1_in_fr: null,
      d_ser_in_fr: null,
      e1_target_cl: null,
      e2_target_cl: null,
      g_ser_trg_cl: null,
      e1_cl: null,
      d_ser_cl: null,
      h_in_cl: null,
      h_out_cl: null,
      h_ph: null,
      h_tb: null,
      g_pre_chlorination: null,
      g_pre_set_max: null,
      g_pre_set_min: null,
      g_pre_chg_limit_for_onetime: null,
      g_pre_calib_cycle: null,
      g_d_obj_residual_cl: null,
      g_d_residual_cl_holding: null,
      g_peri_chlorination: null,
      g_peri_set_max: null,
      g_peri_set_min: null,
      g_peri_chg_limit_for_onetime: null,
      g_peri_calib_cycle : null,
      g_f_out_obj_residual_cl : null,
      g_ser_inr: null,
      g_post_set_max: null,
      g_post_set_min: null,
      g_post_chg_limit_for_onetime:null,
      g_post_calib_cycle: null,
      g_post_chol_rate_holding_time: null,
      g_h_in_obj_residual_cl: null,
      g_h_in_residual_cl_holding: null,
      ai_g_pre_corrected: null,
      ai_g_peri_corrected: null,
      g_inr_crt: null,
      ai_g_pre_evaporation: null,
      g_ser_cl_eva: null,
      ai_g_pre_chlorination: null,
      ai_g_peri_chlorination: null,
      ai_g_ser_inr: null,
      g_pre_chol_rate_liv_dashboard:null,
      g_peri_chol_rate_dashboard:null,
      g_post_chol_rate_dashboard:null,
      g_pre_chol_rate_ind_dashboard:null,
      g_pump_1_run: null,
      g_pump_2_run: null,
      g_elapsed_time: null,
      g_h_in_residual_cl: null,
      g_h_out_residual_cl: null,
      g_h_in_residual_cl_prep: null,
      g_b_residual_cl: null,
      g_f_out_residual_cl: null,
    },
    latestModify: {
      upd_ti: null,
      pre_ai_opr: null,
      peri_ai_opr: null,
      post_ai_opr: null,
      d1_target_cl: null,
      d2_target_cl: null,
      d1_cl: null,
      e_ser_cl: null,
      d1_in_fr: null,
      d_ser_in_fr: null,
      e1_target_cl: null,
      e2_target_cl: null,
      g_ser_trg_cl: null,
      e1_cl: null,
      d_ser_cl: null,
      h_in_cl: null,
      h_out_cl: null,
      h_ph: null,
      h_tb: null,
      g_pre_chlorination: null,
      g_pre_set_max: null,
      g_pre_set_min: null,
      g_pre_chg_limit_for_onetime: null,
      g_pre_calib_cycle: null,
      g_d_obj_residual_cl: null,
      g_d_residual_cl_holding: null,
      g_peri_chlorination: null,
      g_peri_set_max: null,
      g_peri_set_min: null,
      g_peri_chg_limit_for_onetime: null,
      g_peri_calib_cycle : null,
      g_f_out_obj_residual_cl : null,
      g_ser_inr: null,
      g_post_set_max: null,
      g_post_set_min: null,
      g_post_chg_limit_for_onetime:null,
      g_post_calib_cycle: null,
      g_post_chol_rate_holding_time: null,
      g_h_in_obj_residual_cl: null,
      g_h_in_residual_cl_holding: null,
      ai_g_pre_corrected: null,
      ai_g_peri_corrected: null,
      g_inr_crt: null,
      ai_g_pre_evaporation: null,
      g_ser_cl_eva: null,
      ai_g_pre_chlorination: null,
      ai_g_peri_chlorination: null,
      ai_g_ser_inr: null,
      g_pre_chol_rate_liv_dashboard:null,
      g_peri_chol_rate_dashboard:null,
      g_post_chol_rate_dashboard:null,
      g_pre_chol_rate_ind_dashboard:null,
      g_pump_1_run: null,
      g_pump_2_run: null,
      g_elapsed_time: null,
      g_h_in_residual_cl: null,
      g_h_out_residual_cl: null,
      g_h_in_residual_cl_prep: null,
      g_b_residual_cl: null,
      g_f_out_residual_cl: null,
    },
    pre_evaporation: null,
    cholrateTrend: null,
    ai_g_consumption: null,
  },
  getters: {
    isAiOperationMode: function (state) {
      if (state.latest.pre_ai_opr >= 1  || state.latest.post_ai_opr >= 2) {
        return true
      } else {
        return false
      }
    }
  },
  mutations: {
    [OPEN_PRE_DIALOG]: function(state) {
      state.dialog.pre.visible = true
    },
    [OPEN_LSTM_DIALOG]: function(state) {
      state.dialog.lstm.visible = true
    },
    [CLOSE_LSTM_DIALOG]: function(state) {
      state.dialog.lstm.visible = false
    },
    [GET_LATEST]: function (state, data) {
      state.latest = data
      if (state.isModifyMode === false) {
        state.latestModify = Object.assign({}, data)
      }
    },
    [SET_MODIFYED_FROM_LATEST]: function (state) {
      state.latestModify = Object.assign({}, state.latest)
    },
    [PUT_HISTORY_EVAPORATION]: function (state, data) {
      state.pre_evaporation = data
    },
    [PUT_HISTORY_CHOLRATE]: function (state, data) {
      state.cholrateTrend = data
    },
    [PUT_AI_G_CONSUMPTION]: function (state, data) {
      state.ai_g_consumption = data
    },
    [PUT_CONTROL_PRE]: function (state, { g_pre_set_max, g_pre_set_min, g_pre_chg_limit_for_onetime, g_pre_calib_cycle, g_d_obj_residual_cl, g_d_residual_cl_holding }){
      state.latest.g_pre_set_max = g_pre_set_max
      state.latest.g_pre_set_min = g_pre_set_min
      state.latest.g_pre_chg_limit_for_onetime = g_pre_chg_limit_for_onetime
      state.latest.g_pre_calib_cycle = g_pre_calib_cycle
      state.latest.g_d_obj_residual_cl = g_d_obj_residual_cl
      state.latest.g_d_residual_cl_holding = g_d_residual_cl_holding
    },
    [PUT_CONTROL_POST]: function (state, { g_post_set_max, g_post_set_min, g_post_chg_limit_for_onetime, g_post_calib_cycle, g_h_in_obj_residual_cl, g_post_chol_rate_holding_time, g_h_in_residual_cl_holding }){
      state.latest.g_post_set_max = g_post_set_max
      state.latest.g_post_set_min = g_post_set_min
      state.latest.g_post_chg_limit_for_onetime = g_post_chg_limit_for_onetime
      state.latest.g_post_calib_cycle = g_post_calib_cycle
      state.latest.g_post_chol_rate_holding_time = g_post_chol_rate_holding_time
      state.latest.g_h_in_obj_residual_cl = g_h_in_obj_residual_cl 
      state.latest.g_h_in_residual_cl_holding = g_h_in_residual_cl_holding 
    }
  },
  actions: {
    [OPEN_PRE_DIALOG]: function ({ commit }) {
      commit(OPEN_PRE_DIALOG)
    },
    [OPEN_LSTM_DIALOG]: function ({ commit }) {
      commit(OPEN_LSTM_DIALOG)
    },
    [CLOSE_LSTM_DIALOG]: function ({ commit }) {
      commit(CLOSE_LSTM_DIALOG)
    },
    [GET_LATEST]: async function ({ state, commit, rootState }) {
      if(rootState.drawer.selectedMainMenuIndex === 0){//대시보드에서 호출하는 경우 1단계 데이터 호출
        state.disinfectionStep = 1;
      }
      await axios.get(`${DEV_SERVER}/${URL.DISINFECTION_LATEST}/${PROCESS_STEP}/` + state.disinfectionStep)
        .then(({ data }) => {
          commit(GET_LATEST, data.latest)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_HISTORY_EVAPORATION]: async function ({state, commit }) {
      let yesterDayTimestamp = new Date().getTime() - 1000 * 60 * 60 * 24
      let yesterDay = new Date(yesterDayTimestamp)
      let year = yesterDay.getFullYear()
      let month = yesterDay.getMonth()
      let day = yesterDay.getDate()
      // FIXME 현재 날짜 수정
      await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_HISTORY_EVAPORATION}/${PROCESS_STEP}/${state.disinfectionStep}`, { 'start_time': new Date(year, month, day, 0, 0, 0).toISOString(), 'end_time': new Date().toISOString() })
      // await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_HISTORY_EVAPORATION}/${PROCESS_STEP}/${state.disinfectionStep}`, { 'start_time': new Date(2023, 7, 19, 0, 0, 0).toISOString(), 'end_time': new Date(2023, 7, 20, 23, 0, 0).toISOString() })
      .then(({ data }) => {
          commit(PUT_HISTORY_EVAPORATION, data.pre_evaporation)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_HISTORY_CHOLRATE]: async function ({ state, commit }) {
      let nowTimestamp = Date.now()
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      //FIXME 현재 날짜 수정
      await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_HISTORY_CHOLRATE}/${PROCESS_STEP}/${state.disinfectionStep}`, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
      // await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_HISTORY_CHOLRATE}/${PROCESS_STEP}/${state.disinfectionStep}`, { 'start_time': new Date('2013-09-03 00:00:00.000').toISOString(), 'end_time': new Date('2013-09-04 00:00:00.000').toISOString() })
        .then(({ data }) => {
          commit(PUT_HISTORY_CHOLRATE, data.cholrate)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_CONTROL_OPERATION_PRE]: async function ({ commit }, { operation }) {
      await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_CONTROL_OPERATION_PRE}/${PROCESS_STEP}`, { 'operation': operation })
      .then(() => {
        commit('dialog/' +CLOSE_AI_MODE_DIALOG, null, { root: true })
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
        commit(SET_MODIFYED_FROM_LATEST)
      })
    },
    [PUT_CONTROL_OPERATION_PERI]: async function ({ commit }, { operation }) {
      await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_CONTROL_OPERATION_PERI}/${PROCESS_STEP}`, { 'operation': operation })
      .then(() => {
        commit('dialog/' +CLOSE_AI_MODE_DIALOG, null, { root: true })
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
        commit(SET_MODIFYED_FROM_LATEST)
      })
    },
    [PUT_CONTROL_OPERATION_POST]: async function ({ commit }, { operation }) {
      await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_CONTROL_OPERATION_POST}/${PROCESS_STEP}`, { 'operation': operation })
      .then(() => {
        commit('dialog/' +CLOSE_AI_MODE_DIALOG, null, { root: true })
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
        commit(SET_MODIFYED_FROM_LATEST)
      })
    },
    [PUT_CONTROL_PRE]: async function ({ commit }, { g_pre_set_max, g_pre_set_min, g_pre_chg_limit_for_onetime, g_pre_calib_cycle, g_d_obj_residual_cl, g_d_residual_cl_holding}) { // eslint-disable-line no-unused-vars
      await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_CONTROL_PRE}/${PROCESS_STEP}`, { g_pre_set_max, g_pre_set_min, g_pre_chg_limit_for_onetime, g_pre_calib_cycle, g_d_obj_residual_cl, g_d_residual_cl_holding })
      .then(() => {
        let _data = {
          visible: true,
          title: '설정 성공',
          text1: '설정값이 변경되었습니다'
        }
        commit(PUT_CONTROL_PRE,  { g_pre_set_max, g_pre_set_min, g_pre_chg_limit_for_onetime, g_pre_calib_cycle, g_d_obj_residual_cl, g_d_residual_cl_holding})
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
    [PUT_CONTROL_PERI]: async function ({ commit }, { g_peri_set_max, g_peri_set_min, g_peri_chg_limit_for_onetime, g_peri_calib_cycle, g_f_out_obj_residual_cl }) { // eslint-disable-line no-unused-vars
      await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_CONTROL_RERI}/${PROCESS_STEP}`, { g_peri_set_max, g_peri_set_min, g_peri_chg_limit_for_onetime, g_peri_calib_cycle, g_f_out_obj_residual_cl })
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
    [PUT_CONTROL_POST]: async function ({ commit }, { g_post_set_max, g_post_set_min, g_post_chg_limit_for_onetime, g_post_calib_cycle, g_h_in_obj_residual_cl, g_post_chol_rate_holding_time, g_h_in_residual_cl_holding }) { // eslint-disable-line no-unused-vars
      await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_CONTROL_POST}/${PROCESS_STEP}`, { g_post_set_max, g_post_set_min, g_post_chg_limit_for_onetime, g_post_calib_cycle, g_h_in_obj_residual_cl, g_post_chol_rate_holding_time, g_h_in_residual_cl_holding })
      .then(() => {
        let _data = {
          visible: true,
          title: '설정 성공',
          text1: '설정값이 변경되었습니다'
        }
        commit(PUT_CONTROL_POST, { g_post_set_max, g_post_set_min, g_post_chg_limit_for_onetime, g_post_calib_cycle, g_h_in_obj_residual_cl, g_post_chol_rate_holding_time, g_h_in_residual_cl_holding })
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
    [PUT_AI_G_CONSUMPTION]: async function ({ commit, state }){
      //FIXME 현재 날짜 수정
      let nowTimestamp = Date.now();
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_AI_G_CONSUMPTION}/${state.disinfectionStep}`, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
      // await axios.put(`${DEV_SERVER}/${URL.DISINFECTION_AI_G_CONSUMPTION}/${state.disinfectionStep}`, { 'start_time': new Date('2013-09-03 00:00:00.000').toISOString(), 'end_time': new Date('2013-09-04 00:00:00.000').toISOString() })
        .then(({ data }) => {
        commit(PUT_AI_G_CONSUMPTION, data.ai_g_consumption)
      })
      .catch(error => {
        util.printError(error)
      })
    },
  }
}