const OPEN_DIALOG = 'OPEN_DIALOG'
const CLOSE_DIALOG = 'CLOSE_DIALOG'
export default {
  namespaced: true,
  state: {
    visible: false,
    usr_id: null,
    usr_pw: null,
    usr_pw_confirm: null
  },
  mutations: {
    [OPEN_DIALOG]: function(state, data) {
      state.usr_id = data.usr_id
      state.visible = true
    },
    [CLOSE_DIALOG]: function(state) {
      state.visible = false
      state.usr_id = null
      state.usr_pw = null
      state.usr_pw_confirm = null
    }
  },
  actions: {
    [OPEN_DIALOG]: function ({ commit }, data) {
      commit(OPEN_DIALOG, data)
    },
    [CLOSE_DIALOG]: function ({ commit }) {
      commit(CLOSE_DIALOG)
    }
  }
}