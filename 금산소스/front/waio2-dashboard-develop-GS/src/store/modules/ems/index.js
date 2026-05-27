// ems store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

const URL = {
  EMS_LATEST: 'ems/latest'
}
export const GET_EMS_LATEST = URL.EMS_LATEST + '/get'
const GET_LATEST = GET_EMS_LATEST.substr(GET_EMS_LATEST.indexOf('/') + 1)
export default {
  namespaced: true,
  state: {
    latest: {
      pump: {
        h1_operation_mode: null,
        h2_operation_mode: null,
        h1_pm1: null,
        h1_pm2: null,
        h1_pm3: null,
        h1_pm4: null,
        h2_pm1: null,
        h2_pm2: null,
        h2_pm_sp1: null,
        h2_pm_sp2: null,
        ai_h1_pm1: null,
        ai_h1_pm2: null,
        ai_h1_pm3: null,
        ai_h1_pm4: null,
        ai_h2_pm1: null,
        ai_h2_pm2: null,
        ai_h2_pm_sp1: null,
        ai_h2_pm_sp2: null
      },
      peak: {
        z_power1: null,
        ai_z_power1: null,
        ai_z_power_peak1: null
      },
      dr: {
        z_cbl: null,
        ai_z_dr_power: null
      }
    }
  },
  mutations: {
    [GET_LATEST]: function (state, data) {
      state.latest = data
    }
  },
  actions: {
    [GET_LATEST]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${URL.EMS_LATEST}`)
        .then(({ data }) => {
          commit(GET_LATEST, data.latest)
        })
        .catch(error => {
          util.printError(error)
        })
    }
  }
}