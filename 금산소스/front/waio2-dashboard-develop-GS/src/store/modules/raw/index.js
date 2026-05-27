import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

export const URL = {
  RAW_HISTORY_TE: 'raw/history/te'
}
export const PUT_RAW_HISTORY_TE = URL.RAW_HISTORY_TE + '/put'
const PUT_HISTORY_TE = PUT_RAW_HISTORY_TE.substr(PUT_RAW_HISTORY_TE.indexOf('/') + 1)
export default {
  namespaced: true,
  state: {
    te: {
      d_te_loc1: null,
      d_te_loc2: null
    }
  },
  getters: {
  },
  mutations: {
    [PUT_HISTORY_TE]: function(state, data) {
      state.te = data.te
    }
  },
  actions: {
    [PUT_HISTORY_TE]: async function ({ commit }, processStep) {
      let nowTimestamp = Date.now()
      // nowTimestamp = Date.parse("2013-09-04 00:00:00"); // FIXME 추후 삭제
      let oneDayTimestamp = 1000 * 60 * 60 * 24
      await axios.put(`${DEV_SERVER}/${URL.RAW_HISTORY_TE}/` + processStep, { 'start_time': new Date(nowTimestamp - oneDayTimestamp).toISOString(), 'end_time': new Date(nowTimestamp).toISOString() })
        .then(({ data }) => {
          commit(PUT_HISTORY_TE, data)
        })
        .catch(error => {
          util.printError(error)
        })
    }
  }
}