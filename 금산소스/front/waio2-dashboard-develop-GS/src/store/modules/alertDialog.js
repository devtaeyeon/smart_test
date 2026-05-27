const OPEN_DIALOG = 'OPEN_DIALOG'
const CLOSE_DIALOG = 'CLOSE_DIALOG'
export default {
  namespaced: true,
  state: {
    visible: false,
    title: null,
    text1: null,
    text2: null,
    text3: null
  },
  mutations: {
    [OPEN_DIALOG]: function(state, data) {
      state.title = data.title
      state.text1 = data.text1
      state.text2 = data.text2
      state.text3 = data.text3
      state.visible = true
    },
    [CLOSE_DIALOG]: function(state) {
      state.visible = false
      state.title = null
      state.text1 = null
      state.text2 = null
      state.text3 = null
    }
  },
  actions: {
    [OPEN_DIALOG]: function ({ commit }, data) {
      commit(OPEN_DIALOG, data)
    }
  }
}