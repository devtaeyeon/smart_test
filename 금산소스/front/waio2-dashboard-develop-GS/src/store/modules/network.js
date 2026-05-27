import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

const GET_NETWORK_CONFIG = 'GET_NETWORK_CONFIG'
const PUT_NETWORK_CONFIG = 'PUT_NETWORK_CONFIG'
const CONFIG = 'config'
export default {
  namespaced: true,
  state: {
    config: null,
    modifyConfig: null
  },
  mutations: {
    [GET_NETWORK_CONFIG]: function (state, data) {
      state.config = data.config
      state.modifyConfig = data.config
    },
    [PUT_NETWORK_CONFIG]: function (state, data) {
      state.config = data.config
      state.modifyConfig = data.config
    }
  },
  actions: {
    [GET_NETWORK_CONFIG]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${CONFIG}`)
        .then(({ data }) => {
          commit(GET_NETWORK_CONFIG, data)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_NETWORK_CONFIG]: async function ({ commit }, param) {
      await axios.put(`${DEV_SERVER}/${CONFIG}`, param)
        .then(({ data }) => {
          commit(PUT_NETWORK_CONFIG, data)
          let alert = {
            visible: true,
            title: '네트워크 설정',
            text1: '네크워크 정보가 수정 됐습니다'
          }
          commit('alertDialog/OPEN_DIALOG', alert, { root: true })
        })
        .catch(error => {
          util.printError(error)
        })
    }
  }
}