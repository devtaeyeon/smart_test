// 원수 store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

const URL = {
  PMS_LATEST: 'pms/latest'
}
export const GET_PMS_LATEST = URL.PMS_LATEST + '/get'
const GET_LATEST = GET_PMS_LATEST.substr(GET_PMS_LATEST.indexOf('/') + 1)
export default {
  namespaced: true,
  state: {
    latest: {
      ai:[],
      scada:[]
    },
    selectedId: "01"
  },
  getters: {
    getPMSPumpState: function (state) {
      let selectedAI = state.latest.ai.filter(it => {
        if (it.moter_id.includes(state.selectedId)) {
          return true
        }
      })[0]
      let selectedMoter = state.latest.scada.filter(it => {
        if (it.pump_scada_id.includes(state.selectedId)) {
          return true
        }
      })[0]
      if (selectedAI === undefined) {
        selectedAI = { motor_de_amp: "-", motor_nde_amp: "-", pump_de_amp: "-", pump_nde_amp: "-" }
      }
      if (selectedMoter === undefined) {
        selectedMoter = { brg_motor_de_temp: "-", brg_motor_nde_temp: "-", brg_pump_de_temp: "-", brg_pump_nde_temp: "-" }
      }
      return { ai: selectedAI, scada: selectedMoter }
    }
  },
  mutations: {
    [GET_LATEST]: function (state, data) {
      state.latest = data
    }
  },
  actions: {
    [GET_LATEST]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${URL.PMS_LATEST}`)
        .then(({ data }) => {
          commit(GET_LATEST, data)
        })
        .catch(error => {
          util.printError(error)
        })
    }
  }
}