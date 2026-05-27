const OPEN_DIALOG = 'OPEN_DIALOG'
const CLOSE_DIALOG = 'CLOSE_DIALOG'
export default {
  namespaced: true,
  state: {
    visible: false,
    usr_id: null,
    usr_pw: null,
    usr_pw_confirm: null,
    usr_nm: null,
    usr_pn: null,
    usr_auth: 0,
    usr_ti: 0,
    isUsrTiDisabled: false
  },
  mutations: {
    [OPEN_DIALOG]: function(state) {
      state.visible = true
      state.isUsrTiDisabled = true
    },
    [CLOSE_DIALOG]: function(state) {
      state.visible = false
      state.usr_id = null
      state.usr_pw = null
      state.usr_pw_confirm = null
      state.usr_nm = null
      state.usr_pn = null
      state.usr_auth = 0
      state.usr_ti = 0
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