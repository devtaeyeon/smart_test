// gac 여과 store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'
import numeral from 'numeral'

export const URL = {
  GAC_LATEST: 'gac/latest',
  GAC_CONTROL_OPERATION: 'gac/control/operation',
  GAC_CONTROL_TI: 'gac/control/ti'
}
export const GET_GAC_LATEST = URL.GAC_LATEST + '/get'
export const PUT_GAC_CONTROL_OPERATION = URL.GAC_CONTROL_OPERATION + '/put'
export const PUT_GAC_CONTROL_TI = URL.GAC_CONTROL_TI + '/put'
const GET_LATEST = GET_GAC_LATEST.substr(GET_GAC_LATEST.indexOf('/') + 1)
const PUT_CONTROL_OPERATION = PUT_GAC_CONTROL_OPERATION.substr(PUT_GAC_CONTROL_OPERATION.indexOf('/') + 1)
const PUT_CONTROL_TI = PUT_GAC_CONTROL_TI.substr(PUT_GAC_CONTROL_TI.indexOf('/') + 1)

import { CLOSE_AI_MODE_DIALOG } from '@/store/modules/dialog'

const LOCATION_STATE = {
  1: '여과중',
  2: '휴지중',
  3: '역세중',
  4: '역세 후 휴지중',
  5: '순환중',
  6: '순환 후 휴지중',
  7: '순환 후 대기중'
}
export default {
  namespaced: true,
  state: {
    latest: {
      upd_ti: null,
      ai_opr: null,
      peak_mode: null,
      d1_in_fr: null,
      d_ser_in_fr: null,
      e1_tb_b: null,
      e2_tb_b: null,
      i_in_fr: null,
      i_out_fr: null,
      i_sp: null,
      i_tb: null,
      i_opr_cnt: null,
      ai_i_opr_cnt: null,
      i_loc_stt: null,
      i_loc_le: null,
      i_loc_ti: null,
      ai_i_loc_ti: null,
      ai_i_loc_bw_ti: null,
      ai_i_location_schedule: null,
      i_location_ti_set_max: null
    }
  },
  getters: {
    getMinAiILocationBwStartTi: function (state) {
      let minVal = 0
      let minIndex = 0
      let isFirst = true
      for (let i = 5; i <= 18; i++) {
        if (state.latest.ai_i_loc_bw_ti !== null) {
          let locationVal = state.latest.ai_i_loc_bw_ti['location'+i]
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
    getLocationState5: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location5) {
        return LOCATION_STATE[state.latest.i_loc_stt.location5]
      } else {
        return '--'
      }
    },
    getLocationState6: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location6) {
        return LOCATION_STATE[state.latest.i_loc_stt.location6]
      } else {
        return '--'
      }
    },
    getLocationState7: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location7) {
        return LOCATION_STATE[state.latest.i_loc_stt.location7]
      } else {
        return '--'
      }
    },
    getLocationState8: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location8) {
        return LOCATION_STATE[state.latest.i_loc_stt.location8]
      } else {
        return '--'
      }
    },
    getLocationState9: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location9) {
        return LOCATION_STATE[state.latest.i_loc_stt.location9]
      } else {
        return '--'
      }
    },
    getLocationState10: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location10) {
        return LOCATION_STATE[state.latest.i_loc_stt.location10]
      } else {
        return '--'
      }
    },
    getLocationState11: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location11) {
        return LOCATION_STATE[state.latest.i_loc_stt.location11]
      } else {
        return '--'
      }
    },
    getLocationState12: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location12) {
        return LOCATION_STATE[state.latest.i_loc_stt.location12]
      } else {
        return '--'
      }
    },
    getLocationState13: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location13) {
        return LOCATION_STATE[state.latest.i_loc_stt.location13]
      } else {
        return '--'
      }
    },
    getLocationState14: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location14) {
        return LOCATION_STATE[state.latest.i_loc_stt.location14]
      } else {
        return '--'
      }
    },
    getLocationState15: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location15) {
        return LOCATION_STATE[state.latest.i_loc_stt.location15]
      } else {
        return '--'
      }
    },
    getLocationState16: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location16) {
        return LOCATION_STATE[state.latest.i_loc_stt.location16]
      } else {
        return '--'
      }
    },
    getLocationState17: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location17) {
        return LOCATION_STATE[state.latest.i_loc_stt.location17]
      } else {
        return '--'
      }
    },
    getLocationState18: function (state) {
      if (state.latest.i_loc_stt && state.latest.i_loc_stt.location18) {
        return LOCATION_STATE[state.latest.i_loc_stt.location18]
      } else {
        return '--'
      }
    }
  },
  mutations: {
    [GET_LATEST]: function (state, data) {
      state.latest = data
    },
    [PUT_CONTROL_OPERATION]: function (state, data) {
      state.latest.ai_opr = data
    },
    [PUT_CONTROL_TI]: function (state, data) {
      state.latest.i_location_ti_set_max = data
    }
  },
  actions: {
    [GET_LATEST]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${URL.GAC_LATEST}`)
        .then(({ data }) => {
          commit(GET_LATEST, data.latest)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_CONTROL_OPERATION]: async function ({ commit }, { operation }) {
      await axios.put(`${DEV_SERVER}/${URL.GAC_CONTROL_OPERATION}`, { 'operation': operation })
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
    [PUT_CONTROL_TI]: async function ({ commit }, { i_location_ti_set_max }) {
      await axios.put(`${DEV_SERVER}/${URL.GAC_CONTROL_TI}`, { 'i_location_ti_set_max': i_location_ti_set_max })
      .then(() => {
        commit(PUT_CONTROL_TI, i_location_ti_set_max)
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
      })
    }
  },
}