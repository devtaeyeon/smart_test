// 여과 store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'
import numeral from 'numeral'
import moment from 'moment'

export const URL = {
  FILTER_LATEST: 'filter/latest',
  FILTER_LOCATION: 'filter/location',
  FILTER_CONTROL_OPERATION: 'filter/control/operation',
  FILTER_SCHEDULE: 'filter/schedule',
  FILTER_CONTROL_TI: 'filter/control/ti'
}
export const GET_FILTER_LATEST = URL.FILTER_LATEST + '/get'
export const GET_FILTER_LOCATION_BY_JI = URL.FILTER_LATEST + '/numJi/get'
export const PUT_FILTER_CONTROL_OPERATION = URL.FILTER_CONTROL_OPERATION + '/put'
export const GET_FILTER_SCHEDULE = URL.FILTER_SCHEDULE + '/get'
export const PUT_FILTER_CONTROL_TI = URL.FILTER_CONTROL_TI + '/put'
const GET_LATEST = GET_FILTER_LATEST.substr(GET_FILTER_LATEST.indexOf('/') + 1)
const GET_LOCATION_BY_JI = GET_FILTER_LOCATION_BY_JI.substr(GET_FILTER_LOCATION_BY_JI.indexOf('/') + 1)
const PUT_CONTROL_OPERATION = PUT_FILTER_CONTROL_OPERATION.substr(PUT_FILTER_CONTROL_OPERATION.indexOf('/') + 1)
const GET_SCHEDULE = GET_FILTER_SCHEDULE.substr(GET_FILTER_SCHEDULE.indexOf('/') + 1)
const PUT_CONTROL_TI = PUT_FILTER_CONTROL_TI.substr(PUT_FILTER_CONTROL_TI.indexOf('/') + 1)
const OPEN_POPUP = 'OPEN_POPUP'
export const CLOSE_POPUP = 'CLOSE_POPUP'
const SET_MODIFYED_FROM_LATEST = "setModifyedFromLatest"
const PROCESS_STEP = 1

import { CLOSE_AI_MODE_DIALOG } from '@/store/modules/dialog'

const LOCATION_STATE = {
  0: '',
  1: '여과중',
  2: '역세대기중',
  3: '역세중',
  4: '여과대기중',
  5: '시동방수중',
  6: '운휴중',
}
export default {
  namespaced: true,
  state: {
    isModifyMode: false,
    latest: {
      upd_ti: null,
      ai_opr: null,
      peak_mode: null,
      d1_in_fr: null,
      d_ser_in_fr: null,
      e1_tb_b: null,
      e2_tb_b: null,
      f_sp: null,
      f_out_fr: null,
      f_location_ti_set_max: null,
      f_loc_stt: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      ai_f_loc_le: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      f_loc_le: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      f_loc_tb: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      f_loc_ti: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      ai_f_loc_ti: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      ai_f_loc_bw_ti: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      ai_f_location_schedule: {
        location1: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location2: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location3: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location4: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location5: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location6: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location7: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location8: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        }
      }
    },
    latestModify: {
      upd_ti: null,
      ai_opr: null,
      peak_mode: null,
      d1_in_fr: null,
      d_ser_in_fr: null,
      e1_tb_b: null,
      e2_tb_b: null,
      f_sp: null,
      f_out_fr: null,
      f_location_ti_set_max: null,
      f_loc_stt: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      ai_f_loc_le: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      f_loc_le: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      f_loc_tb: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      f_loc_ti: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      ai_f_loc_ti: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      ai_f_loc_bw_ti: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      ai_f_location_schedule: {
        location1: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location2: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location3: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location4: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location5: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location6: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location7: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        },
        location8: {
          start: null,
          end: null,
          bw_start: null,
          bw_end: null,
          next_start: null,
          next_end: null
        }
      }
    },
    popup: {
      visible: false,
      numJi: null,
      location: {
        upd_ti: null,
        d1_in_fr: null,
        d_ser_in_fr: null,
        f_out_fr: null,
        f_sp: null,
        f_loc_le: null,
        ai_f_loc_le: null,
        f_loc_tb: null,
        f_loc_ti: null,
        ai_f_loc_ti: null,
        ai_f_loc_bw_ti: null,
        ai_f_location_end_ti: null,
        f_loc_bw_wt_ti: null,
        ai_f_location_schedule: {
          location1: {
            start: null,
            end: null,
            bw_start: null,
            bw_end: null,
            next_start: null,
            next_end: null
          },
          location2: {
            start: null,
            end: null,
            bw_start: null,
            bw_end: null,
            next_start: null,
            next_end: null
          },
          location3: {
            start: null,
            end: null,
            bw_start: null,
            bw_end: null,
            next_start: null,
            next_end: null
          },
          location4: {
            start: null,
            end: null,
            bw_start: null,
            bw_end: null,
            next_start: null,
            next_end: null
          },
          location5: {
            start: null,
            end: null,
            bw_start: null,
            bw_end: null,
            next_start: null,
            next_end: null
          },
          location6: {
            start: null,
            end: null,
            bw_start: null,
            bw_end: null,
            next_start: null,
            next_end: null
          },
          location7: {
            start: null,
            end: null,
            bw_start: null,
            bw_end: null,
            next_start: null,
            next_end: null
          },
          location8: {
            start: null,
            end: null,
            bw_start: null,
            bw_end: null,
            next_start: null,
            next_end: null
          }
        }
      }
    },
    schedule: {
      filter: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      },
      gac: {
        location1: null,
        location2: null,
        location3: null,
        location4: null,
        location5: null,
        location6: null,
        location7: null,
        location8: null
      }
    }
  },
  getters: {
    getPopupFilterStartTime: function (state) {
      if (state.popup.location.ai_f_location_schedule.start !== null && state.popup.location.ai_f_location_schedule.start !== '0' ) {
        return moment(state.popup.location.ai_f_location_schedule.start).format('MM-DD HH:mm')
      } else {
        return '--'
      }
    },
    getPopupFilterEndTime: function (state) {
      if (state.popup.location.ai_f_location_schedule.end !== null && state.popup.location.ai_f_location_schedule.end !== '0' ) {
        return moment(state.popup.location.ai_f_location_schedule.end).format('MM-DD HH:mm')
      } else {
        return '--'
      }
    },
    getPopupBWStartTime: function (state) {
      if (state.popup.location.ai_f_location_schedule.bw_start !== null && state.popup.location.ai_f_location_schedule.bw_start !== '0' ) {
        return moment(state.popup.location.ai_f_location_schedule.bw_start).format('MM-DD HH:mm')
      } else {
        return '--'
      }
    },
    getAiLocationWaterLevelLater10Minutes: function (state) {
      if (state.popup.location.ai_f_loc_le !== null) {
        let key = Object.keys(state.popup.location.ai_f_loc_le)[0]
        let value = state.popup.location.ai_f_loc_le[key]
        if (value !== null) {
          value = value > 2.1 ? 2.1 : value
          return numeral(value).format('0.00')
        }
        return '--'
      } else {
        return null
      }
    },
    getAiLocationWaterLevelLater24Hours: function (state) {
      if (state.popup.location.ai_f_loc_le !== null) {
        // 10분(index: 0) 1시간(index: 5)
        let hour = 24
        let key = Object.keys(state.popup.location.ai_f_loc_le)[(hour * 6) - 1]
        let value = state.popup.location.ai_f_loc_le[key]
        if (value !== null) {
          value = value > 2.1 ? 2.1 : value
          return numeral(value).format('0.00')
        }
        return '--'
      } else {
        return null
      }
    },
    getMinAiFLocationBwStartTi: function (state) {
      let minVal = 0
      let minIndex = 0
      let isFirst = true
      for (let i = 1; i <= 8; i++) {
        //TODO: 임시적으로 주석처리한 부분 풀기.
        if (state.latest.ai_f_loc_bw_ti !== null) {
          // if (state.latest.ai_f_loc_bw_ti == null) {
          let locationVal = state.latest.ai_f_loc_bw_ti['location'+i]
          if (locationVal !== null) {
            if (isFirst) {
                if (locationVal > 0) {
                  minVal = locationVal
                  minIndex = i
                  isFirst = false
                }
            } else {
              if (locationVal < minVal) {
                if (locationVal > 0) {
                  minVal = locationVal
                  minIndex = i
                }
              }
            }
          }
        }
      }
      minVal = minVal < 0 ? 0 : minVal
      return { 'minVal': numeral(minVal / 60).format('0'), 'minIndex': minIndex }
    },
    getLocationState1: function (state) {
      if (state.latest.f_loc_stt && state.latest.f_loc_stt.location1) {
        return LOCATION_STATE[state.latest.f_loc_stt.location1]
      } else {
        return '--'
      }
    },
    getLocationState2: function (state) {
      if (state.latest.f_loc_stt && state.latest.f_loc_stt.location2) {
        return LOCATION_STATE[state.latest.f_loc_stt.location2]
      } else {
        return '--'
      }
    },
    getLocationState3: function (state) {
      if (state.latest.f_loc_stt && state.latest.f_loc_stt.location3) {
        return LOCATION_STATE[state.latest.f_loc_stt.location3]
      } else {
        return '--'
      }
    },
    getLocationState4: function (state) {
      if (state.latest.f_loc_stt && state.latest.f_loc_stt.location4) {
        return LOCATION_STATE[state.latest.f_loc_stt.location4]
      } else {
        return '--'
      }
    },
    getLocationState5: function (state) {
      if (state.latest.f_loc_stt && state.latest.f_loc_stt.location5) {
        return LOCATION_STATE[state.latest.f_loc_stt.location5]
      } else {
        return '--'
      }
    },
    getLocationState6: function (state) {
      if (state.latest.f_loc_stt && state.latest.f_loc_stt.location6) {
        return LOCATION_STATE[state.latest.f_loc_stt.location6]
      } else {
        return '--'
      }
    },
    getLocationState7: function (state) {
      if (state.latest.f_loc_stt && state.latest.f_loc_stt.location7) {
        return LOCATION_STATE[state.latest.f_loc_stt.location7]
      } else {
        return '--'
      }
    },
    getLocationState8: function (state) {
      if (state.latest.f_loc_stt && state.latest.f_loc_stt.location8) {
        return LOCATION_STATE[state.latest.f_loc_stt.location8]
      } else {
        return '--'
      }
    }
  },
  mutations: {
    [GET_LATEST]: function (state, data) {
      state.latest = data
      if (state.isModifyMode === false) {
        state.latestModify = Object.assign({}, data)
      }
    },
    [SET_MODIFYED_FROM_LATEST]: function (state) {
      state.latestModify = Object.assign({}, state.latest)
    },
    [GET_LOCATION_BY_JI]: function (state, data) {
      state.popup.location = data
    },
    [GET_SCHEDULE]: function (state, data) {
      state.schedule = data
    },
    [OPEN_POPUP]: function (state, data) {
      state.popup.visible = true
      state.popup.numJi = data
    },
    [PUT_CONTROL_OPERATION]: function (state, data) {
      state.latest.ai_opr = data
    },
    [CLOSE_POPUP]: function (state) {
      state.popup = {
        visible: false,
        numJi: null,
        location: {
          upd_ti: null,
          d1_in_fr: null,
          d_ser_in_fr: null,
          f_out_fr: null,
          f_sp: null,
          f_loc_le: null,
          ai_f_loc_le: null,
          f_loc_tb: null,
          f_loc_ti: null,
          ai_f_loc_ti: null,
          ai_f_loc_bw_ti: null,
          ai_f_location_end_ti: null,
          f_loc_bw_wt_ti: null,
          ai_f_location_schedule: {
            start: null,
            end: null,
            bw_start: null,
            bw_end: null
          }
        }
      }
    },
    [PUT_CONTROL_TI]: function (state, data) {
      state.latest.f_location_ti_set_max = data
    }
  },
  actions: {
    [GET_LATEST]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${URL.FILTER_LATEST}/` + PROCESS_STEP)
        .then(({ data }) => {
          commit(GET_LATEST, data.latest)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [GET_LOCATION_BY_JI]: async function ({ commit }, obj) {
      await axios.get(`${DEV_SERVER}/${URL.FILTER_LOCATION}/${obj.numJi}/` + PROCESS_STEP)
        .then(({ data }) => {
          commit(GET_LOCATION_BY_JI, data.location)
          commit(OPEN_POPUP, obj.numJi)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [GET_SCHEDULE]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${URL.FILTER_SCHEDULE}/` + PROCESS_STEP)
        .then(({ data }) => {
          commit(GET_SCHEDULE, data.schedule)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_CONTROL_OPERATION]: async function ({ commit }, { operation }) {
      await axios.put(`${DEV_SERVER}/${URL.FILTER_CONTROL_OPERATION}/` + PROCESS_STEP, { 'operation': operation })
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
    [PUT_CONTROL_TI]: async function ({ commit }, { f_location_ti_set_max }) {
      await axios.put(`${DEV_SERVER}/${URL.FILTER_CONTROL_TI}/` + PROCESS_STEP, { 'f_location_ti_set_max': f_location_ti_set_max })
      .then(() => {
        commit(PUT_CONTROL_TI, f_location_ti_set_max)
        commit('dialog/'+ CLOSE_AI_MODE_DIALOG, null, { root: true })
        let _data = {
          visible: true,
          title: '제어 성공',
          text1: '최대 여과지속시간 설정 완료'
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
    }
  }
}