import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'
import router from '@/router'

const LOGIN_POST = 'LOGIN_POST'
const LOGIN_PUT = 'LOGIN_PUT'

const LOGIN = 'login'
const USERS = 'users' 
const LOGOUT = 'logout'
const GET_SERVER_TIME = 'getServerTime'

import StatusCodes from 'http-status'
export default {
  namespaced: true,
  state: {
    // 로그인된 계정
    user: {
      // 로그인 제어
      tkn: null,
      // 유저 권한(0: 관리자, 1: 일반사용자, 2:미로그인)
      usr_auth: 2,
      usr_id: null,
      usr_pn: null,
      usr_nm: null,
      expr_ti: null,  // 프론트에서 계산하는 실제 만료시간
      usr_ti: null    // 사용자별로 설정한 로그인 유지시간
    },
    serverTime: null
  },
  mutations: {
    [LOGIN_POST]: function (state, data) {
      state.user.tkn = data.tkn
      state.user.expr_ti = data.expr_ti
      axios.defaults.headers.common['X-ACCESS-TOKEN'] = data.tkn
    },
    [LOGIN_PUT]: function (state, data) {
      state.user.tkn = data.tkn
      state.user.expr_ti = data.expr_ti
      axios.defaults.headers.common['X-ACCESS-TOKEN'] = data.tkn
    },
    [USERS]: function (state, data) {
      if (state.user.usr_id === null) {
        if (data.usr_nm !== undefined) state.user.usr_nm = data.usr_nm
        if (data.usr_pn !== undefined) state.user.usr_pn = data.usr_pn
        if (data.usr_auth !== undefined) state.user.usr_auth = data.usr_auth
        if (data.usr_id !== undefined) state.user.usr_id = data.usr_id
        if (data.usr_ti !== undefined) state.user.usr_ti = data.usr_ti
      }
      else if (data.usr_id === state.user.usr_id) {
        if (data.usr_nm !== undefined) state.user.usr_nm = data.usr_nm
        if (data.usr_pn !== undefined) state.user.usr_pn = data.usr_pn
        if (data.usr_auth !== undefined) state.user.usr_auth = data.usr_auth
        if (data.usr_id !== undefined) state.user.usr_id = data.usr_id
        if (data.usr_ti !== undefined) state.user.usr_ti = data.usr_ti
      }
    },
    [LOGOUT]: function (state) {
      axios.defaults.headers.common['X-ACCESS-TOKEN'] = null
      for (let key in state.user) {
        if (key === 'usr_auth') {
          state.user[key] = 2
        } else {
          state.user[key] = null
        }
      }
      if(router.history.current.path !== '/') router.push('/')
},
    [GET_SERVER_TIME]: function (state, data) {
      state.serverTime = data
    }
  },
  actions: {
    // 로그인 API
    [LOGIN_POST]: async function ({ commit, dispatch }, { usr_id, usr_pw }) {
      let localtime = new Date().getTime()
      const status = await axios.post(`${DEV_SERVER}/${LOGIN}`, { usr_id, usr_pw, localtime })
        .then(({ data, status }) => {
          data.usr_id = usr_id
          commit(LOGIN_POST, data)
          return status
        })
        .catch(error => {
          util.printError(error)
          let data = {
            visible: true,
            title: '로그인',
            text1: '로그인에 실패했습니다'
          }
          commit('alertDialog/OPEN_DIALOG', data, { root: true })
        })
      if (status === StatusCodes.CREATED) {
        await axios.get(`${DEV_SERVER}/${USERS}` + '/' + usr_id)
          .then(({ data }) => {
            commit(USERS, data.users)
            let _data = {
              visible: true,
              title: '로그인',
              text1: usr_id + '님이 로그인 했습니다'
            }
            commit('loginDialog/CLOSE_DIALOG', null, { root: true })
            commit('alertDialog/OPEN_DIALOG', _data, { root: true })
            dispatch('user/GET_USERS', null, { root: true })
          })
          .catch(error => {
            util.printError(error)
            let data = {
              visible: true,
              title: '사용자 조회',
              text1: '사용자 조회에 실패했습니다'
            }
            commit('alertDialog/OPEN_DIALOG', data, { root: true })
          })
      }
    },
    [LOGIN_PUT]: async function({ commit }) {
      let localtime = new Date().getTime()
      await axios.put(`${DEV_SERVER}/${LOGIN}`, { localtime })
        .then(({ status, data }) => {
          if(status === StatusCodes.CREATED) {
            commit(LOGIN_PUT, data)
          }
        })
        .catch(error => {
          util.printError(error)
          commit(LOGOUT)
        })
    },
    // 로그아웃 API
    [LOGOUT]: async function ({ commit }, payload) {
      await axios.delete(`${DEV_SERVER}/${LOGOUT}`)
        .then((res) => {
          if (res.status === StatusCodes.NO_CONTENT) {
            commit(LOGOUT)
            // status ( 0: 자동로그아웃, 1: 로그아웃버튼클릭)
            if (payload.status === 0 ) {
              let d = {
                visible: true,
                title: '자동 로그아웃',
                text1: '안전한 서비스 보안을 위해 로그인 후',
                text2: '일정시간 동안 서비스 이용이 없어',
                text3: '자동 로그아웃 되었습니다.'
              }
              commit('alertDialog/OPEN_DIALOG', d, { root: true })
            } else {
              let d = {
                visible: true,
                title: '로그아웃',
                text1: '로그아웃 되었습니다.'
              }
              commit('alertDialog/OPEN_DIALOG', d, { root: true })
            }
          }
        })
        .catch(error => {
          util.printError(error)
          commit(LOGOUT)
        })
},
    //서버 시간 세팅
    [GET_SERVER_TIME]: async function ({ commit }){
      await axios.get(`${DEV_SERVER}/${GET_SERVER_TIME}`)
        .then(({ data }) =>{
          commit(GET_SERVER_TIME, data.serverTime)
        }).
        catch(error =>{
          util.printError(error)
        })
    }
  }
}