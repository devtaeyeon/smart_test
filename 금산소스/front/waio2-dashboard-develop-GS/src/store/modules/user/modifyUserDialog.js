const OPEN_DIALOG = 'OPEN_DIALOG'
const CLOSE_DIALOG = 'CLOSE_DIALOG'
export default {
  namespaced: true,
  state: {
    visible: false,
    usr_id: null,
    usr_nm: null,
    usr_pn: null,
    usr_auth: 0,
    usr_ti: 0,
    isUsrTiDisabled: false,
    isMyInfo: false
  },
  mutations: {
    [OPEN_DIALOG]: function(state, data) {
      state.visible = true
      state.usr_id = data.usr_id
      state.usr_nm = data.usr_nm
      state.usr_pn = data.usr_pn
      state.usr_auth = data.usr_auth
      state.usr_ti = data.usr_ti
      state.isUsrTiDisabled = data.isUsrTiDisabled
      state.isMyInfo = data.isMyInfo
    },
    [CLOSE_DIALOG]: function(state) {
      state.visible = false
      state.usr_id = null
      state.usr_nm = null
      state.usr_pn = null
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