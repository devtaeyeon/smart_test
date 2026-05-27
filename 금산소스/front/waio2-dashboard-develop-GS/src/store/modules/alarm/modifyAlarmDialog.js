const OPEN_DIALOG = 'OPEN_DIALOG'
const CLOSE_DIALOG = 'CLOSE_DIALOG'
export default {
  namespaced: true,
  state: {
    visible: false,
    seq: null,
    alm_id: null,
    dp_nm: null,
    cmp_val: null,
    scd_snd: null
  },
  mutations: {
    [OPEN_DIALOG]: function(state, data) {
      state.visible = true
      state.seq = data.seq
      state.alm_id = data.alm_id
      state.dp_nm = data.dp_nm
      state.cmp_val = data.cmp_val
      state.scd_snd = data.scd_snd
    },
    [CLOSE_DIALOG]: function(state) {
      state.visible = false
      state.seq = null
      state.alm_id = null
      state.dp_nm = null
      state.cmp_val = null
      state.scd_snd = null
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