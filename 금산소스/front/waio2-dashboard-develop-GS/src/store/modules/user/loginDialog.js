const OPEN_DIALOG = 'OPEN_DIALOG'
const CLOSE_DIALOG = 'CLOSE_DIALOG'
export default {
  namespaced: true,
  state: {
    visible: false,
    usr_id: null,
    usr_pw: null,
  },
  mutations: {
    [OPEN_DIALOG]: function(state) {
      state.visible = true
    },
    [CLOSE_DIALOG]: function(state) {
      state.visible = false
      state.usr_id = null
      state.usr_pw = null
    }
  },
  actions: {
    [OPEN_DIALOG]: function ({ commit }) {
      commit(OPEN_DIALOG)
    },
    [CLOSE_DIALOG]: function ({ commit }) {
      commit(CLOSE_DIALOG)
    }
  }
}