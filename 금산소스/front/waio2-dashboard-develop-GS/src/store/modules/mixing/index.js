// 혼화응집 store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

const URL = {
  MIXING_LATEST: 'mixing/latest',
  MIXING_HISTORY_FC_SP: 'mixing/history/fc/sp',
  MIXING_CONTROL_OPERATION: 'mixing/control/operation',
  MIXING_CONTROL_AI: 'mixing/control/ai',
  MIXING_G_LIMIT_CONTROL_AI: 'mixing/glimit/control/ai',
  MIXING_G_CRT_CONTROL_AI: 'mixing/gcrt/control/ai',
}
export const GET_MIXING_LATEST = URL.MIXING_LATEST + '/get'
export const PUT_MIXING_HISTORY_FC_SP = URL.MIXING_HISTORY_FC_SP + '/put'
export const PUT_MIXING_CONTROL_OPERATION = URL.MIXING_CONTROL_OPERATION + '/put'
export const PUT_MIXING_CONTROL_AI = URL.MIXING_CONTROL_AI + '/put'
export const PUT_MIXING_G_LIMIT_CONTROL_AI = URL.MIXING_G_LIMIT_CONTROL_AI + '/put'
export const PUT_MIXING_G_CRT_CONTROL_AI = URL.MIXING_G_CRT_CONTROL_AI + '/put'
const GET_LATEST = GET_MIXING_LATEST.substr(GET_MIXING_LATEST.indexOf('/') + 1)
const PUT_HISTORY_FC_SP = PUT_MIXING_HISTORY_FC_SP.substr(PUT_MIXING_HISTORY_FC_SP.indexOf('/') + 1)
const PUT_CONTROL_OPERATION = PUT_MIXING_CONTROL_OPERATION.substr(PUT_MIXING_CONTROL_OPERATION.indexOf('/') + 1)
const PUT_CONTROL_AI = PUT_MIXING_CONTROL_AI.substr(PUT_MIXING_CONTROL_AI.indexOf('/') + 1)
const PUT_G_LIMIT_CONTROL_AI = PUT_MIXING_G_LIMIT_CONTROL_AI.substr(PUT_MIXING_G_LIMIT_CONTROL_AI.indexOf('/') + 1)
const PUT_G_CRT_CONTROL_AI = PUT_MIXING_G_CRT_CONTROL_AI.substr(PUT_MIXING_G_CRT_CONTROL_AI.indexOf('/') + 1)
const SET_MODIFYED_FROM_LATEST = "setModifyedFromLatest"
const PROCESS_STEP = 1

import { CLOSE_AI_MODE_DIALOG } from '@/store/modules/dialog'
export default {
  namespaced: true,
  state: {
    isModifyMode: false,
    isGLimitModifyMode: false,
    selectedFCLocation: 1,
    isPopupVisible: false,
    latest: {
      upd_ti: null,
      ai_opr: null,
      b_te: null,
      b_te_loc1: null,
      b_te_loc2: null,
      b_de: null,
      b_dv: null,
      d_dv_loc1: null,
      d_dv_loc2: null,
      d_fc_lt: null,
      d_rq: null,
      d_pn: null,
      d_g_value_loc1: null,
      d_g_value_loc2: null,
      d_g_value_ctr_flag: 0,
      d_loc_fc_stt1: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt2: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt3: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt4: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt5: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt6: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt7: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt8: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt9: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_sp1: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_sp2: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_sp3: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_sp4: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      ai_d_loc_fc_sp1: {
        step1: null,
        step2: null
      },
      ai_d_loc_fc_sp2: {
        step1: null,
        step2: null
      },
      d_g_step1_max:null,
      d_g_step1_min:null,
      d_g_step2_max:null,
      d_g_step2_min:null,
      d_g_step1_crt:null,
      d_g_step2_crt:null,
    },
    latestModify: {
      upd_ti: null,
      ai_opr: null,
      b_te: null,
      b_te_loc1: null,
      b_te_loc2: null,
      b_de: null,
      b_dv: null,
      d_dv_loc1: null,
      d_dv_loc2: null,
      d_fc_lt: null,
      d_rq: null,
      d_pn: null,
      d_g_value_loc1: null,
      d_g_value_loc2: null,
      d_g_value_ctr_flag: 0,
      d_loc_fc_stt1: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt2: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt3: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt4: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt5: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt6: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt7: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt8: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_stt9: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_sp1: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_sp2: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_sp3: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      d_loc_fc_sp4: {
        step1: {
          1: null,
          2: null,
          3: null
        },
        step2: {
          1: null,
          2: null,
          3: null
        },
        step3: {
          1: null,
          2: null,
          3: null
        }
      },
      ai_d_loc_fc_sp1: {
        step1: null,
        step2: null
      },
      ai_d_loc_fc_sp2: {
        step1: null,
        step2: null
      }
    },
    fc_sp: {
      location1: {
        step1: null,
        step2: null,
        step3: null
      },
      location2: {
        step1: null,
        step2: null,
        step3: null
      },
      location3: {
        step1: null,
        step2: null,
        step3: null
      },
      location4: {
        step1: null,
        step2: null,
        step3: null
      },
      d_g_step1_max:null,
      d_g_step1_min:null,
      d_g_step2_max:null,
      d_g_step2_min:null,
      d_g_step1_crt:null,
      d_g_step2_crt:null,
    }
  },
  getters: {
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
    [PUT_HISTORY_FC_SP]: function (state, data) {
      state.fc_sp = data
    },
    [PUT_CONTROL_OPERATION]: function (state, data) {
      state.latest.ai_opr = data
    },
    [PUT_CONTROL_AI]: function(state, obj) {
      state.latest.d_g_value_loc1 = obj.d_g_value_loc1
      state.latest.d_g_value_loc2 = obj.d_g_value_loc2
      state.latest.d_g_value_ctr_flag = obj.d_g_value_ctr_flag
    },
    [PUT_G_LIMIT_CONTROL_AI]: function(state,obj){
      state.latest.d_g_step1_max = obj.d_g_step1_max
      state.latest.d_g_step1_min = obj.d_g_step1_min
      state.latest.d_g_step2_max = obj.d_g_step2_max
      state.latest.d_g_step2_min = obj.d_g_step2_min
    },
    [PUT_G_CRT_CONTROL_AI]: function(state,obj){
      state.latest.d_g_step1_crt = obj.d_g_step1_crt
      state.latest.d_g_step2_crt = obj.d_g_step2_crt
    }
  },
  actions: {
    [GET_LATEST]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${URL.MIXING_LATEST}` + '/' + PROCESS_STEP) // processStep 1 : 생활, 2공업
        .then(({ data }) => {
          commit(GET_LATEST, data.latest)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_HISTORY_FC_SP]: async function ({ commit }) {
      let nowTimestamp = Date.now()
      // nowTimestamp = Date.parse("2013-09-04 00:00:00"); // FIXME 추후 삭제
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      await axios.put(`${DEV_SERVER}/${URL.MIXING_HISTORY_FC_SP}/` + PROCESS_STEP, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
        .then(({ data }) => {
          commit(PUT_HISTORY_FC_SP, data.fc_sp)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_CONTROL_OPERATION]: async function ({ commit }, { operation }) {
      await axios.put(`${DEV_SERVER}/${URL.MIXING_CONTROL_OPERATION}/` + PROCESS_STEP, { 'operation': operation })
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
      })
    },
    [PUT_CONTROL_AI]: async function ({ commit }, obj) {
      await axios.put(`${DEV_SERVER}/${URL.MIXING_CONTROL_AI}/` + PROCESS_STEP, obj)
      .then(() => {
        commit(PUT_CONTROL_AI, obj)
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
    [PUT_G_LIMIT_CONTROL_AI]: async function ({ commit }, obj) {
      await axios.put(`${DEV_SERVER}/${URL.MIXING_G_LIMIT_CONTROL_AI}/` + PROCESS_STEP, obj)
      .then(() => {
        commit(PUT_G_LIMIT_CONTROL_AI, obj)
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
    [PUT_G_CRT_CONTROL_AI]: async function ({ commit }, obj) {
      await axios.put(`${DEV_SERVER}/${URL.MIXING_G_CRT_CONTROL_AI}/` + PROCESS_STEP, obj)
      .then(() => {
        commit(PUT_G_CRT_CONTROL_AI, obj)
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