import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

const HISTORY = 'history'

const LOGIN = 'login'
export default {
  namespaced: true,
  state: {
    history: []
  },
  mutations: {
    [HISTORY]: function (state, data) {
      state.history = data.login
    }
  },
  actions: {
    // 로그인 이력 조회 API
    [HISTORY]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${LOGIN}`)
        .then(({ data }) => {
          commit(HISTORY, data)
        })
        .catch(error => {
          util.printError(error)
        })
    },
  }
}