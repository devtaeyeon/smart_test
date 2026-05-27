// user store
import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'
import StatusCodes from 'http-status'

const GET_USERS = 'GET_USERS'
const GET_USER = 'GET_USER'
const ADD_USER = 'ADD_USER'
const MODIFY_USER_MYINFO = 'MODIFY_USER_MYINFO'
const MODIFY_USER = 'MODIFY_USER'
const MODIFY_PASSWORD = 'MODIFY_PASSWORD'
const DELETE_USER = 'DELETE_USER'

const USERS = 'users'
const MYINFO = 'myInfo'
const PW = 'pw'
export default {
  namespaced: true,
  state: {
    userList: [],
    addUserDialog: {
      visible: false
    }
  },
  mutations: {
    [GET_USERS]: function (state, data) {
      state.userList = data.login
    },
    [ADD_USER]: function (state, data) {
      state.userList.push(data)
    },
    [MODIFY_USER_MYINFO]: function (state, data) {
      let find = state.userList.find((element) => element.usr_id === data.usr_id)
      find.usr_nm = data.usr_nm
      find.usr_pn = data.usr_pn
    },
    [MODIFY_USER]: function (state, data) {
      let find = state.userList.find((element) => element.usr_id === data.usr_id)
      find.usr_nm = data.usr_nm
      find.usr_pn = data.usr_pn
      find.usr_auth = data.usr_auth
      find.usr_ti = data.usr_ti
    },
    [DELETE_USER]: function (state, usr_id) {
      for (let i = 0; i < state.userList.length; i++) {
        let element = state.userList[i]
        if (element.usr_id === usr_id) {
          state.userList.splice(i, 1)
        }
      }
    }
  },
  actions: {
    // 유저 리스트 조회 API
    [GET_USERS]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${USERS}`)
        .then(({ data }) => {
          commit(GET_USERS, data)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    // 특정 유저 조회 API
    [GET_USER]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${USERS}` + '/' + 'admin')
        .then(({ data }) => {
          commit('login/users', data.users, { root: true })
        })
        .catch(error => {
          util.printError(error)
          let data = {
            visible: true,
            title: '사용자 조회',
            text1: '이미 만료된 사용자입니다'
          }
          commit('alertDialog/OPEN_DIALOG', data, { root: true })
        })
    },
    // 유저 생성 API
    [ADD_USER]: async function ({ commit }, { usr_id, usr_nm, usr_pw, usr_pn, usr_auth, usr_ti }) {
      await axios.post(`${DEV_SERVER}/${USERS}`, { usr_id, usr_nm, usr_pw, usr_pn, usr_auth, usr_ti })
        .then(() => {
          commit(ADD_USER, { usr_id, usr_nm, usr_pw, usr_pn, usr_auth, usr_ti })
          let data = {
            visible: true,
            title: '사용자 생성',
            text1: usr_id + '가 생성 됐습니다'
          }
          commit('alertDialog/OPEN_DIALOG', data, { root: true })
          commit('addUserDialog/CLOSE_DIALOG', null, { root: true })
        })
        .catch(error => {
          util.printError(error)
          if (error.response.status === StatusCodes.CONFLICT) {
            let data = {
              visible: true,
              title: '유저생성',
              text1: '동일한 아이디가 존재합니다'
            }
            commit('alertDialog/OPEN_DIALOG', data, { root: true })
          }
        })
    },
    // 유저 수정 API - MyInfo
    [MODIFY_USER_MYINFO]: async function ({ commit }, { usr_id, usr_pn, usr_nm }) {
      await axios.put(`${DEV_SERVER}/${USERS}/${MYINFO}` + '/' + usr_id, { usr_pn, usr_nm })
        .then(() => {
          commit(MODIFY_USER_MYINFO, { usr_id, usr_pn, usr_nm })
          commit('modifyUserDialog/CLOSE_DIALOG', null, { root: true })
          let data = {
            visible: true,
            title: '사용자 정보 수정',
            text1: '사용자가 정보가 수정 됐습니다'
          }
          commit('alertDialog/OPEN_DIALOG', data, { root: true })
          commit('login/users', { usr_id: usr_id, usr_pn: usr_pn, usr_nm: usr_nm }, { root: true })
        })
        .catch(error => {
          console.log(error)
          util.printError(error)
        })
    },
    // 유저 수정 API
    [MODIFY_USER]: async function ({ commit }, { usr_id, usr_pn, usr_nm, usr_auth, usr_ti }) {
      await axios.put(`${DEV_SERVER}/${USERS}` + '/' + usr_id, { usr_pn, usr_nm, usr_auth, usr_ti })
        .then(() => {
          commit(MODIFY_USER, { usr_id, usr_pn, usr_nm, usr_auth, usr_ti })
          commit('modifyUserDialog/CLOSE_DIALOG', null, { root: true })
          let data = {
            visible: true,
            title: '사용자 정보 수정',
            text1: '사용자가 정보가 수정 됐습니다'
          }
          commit('alertDialog/OPEN_DIALOG', data, { root: true })
          commit('login/users', { usr_id: usr_id, usr_pn: usr_pn, usr_nm: usr_nm, usr_auth: usr_auth, usr_ti: usr_ti }
            , { root: true })
        })
        .catch(error => {
          console.log(error)
          util.printError(error)
        })
    },
    // 유저 암호 변경 API
    [MODIFY_PASSWORD]: async function ({ commit }, { usr_id, usr_pw }) {
      await axios.put(`${DEV_SERVER}/${USERS}/${PW}` + '/' + usr_id, { usr_pw })
        .then(() => {
          commit('modifyPasswordDialog/CLOSE_DIALOG', null, { root: true })
          let data = {
            visible: true,
            title: '사용자 비밀번호 초기화',
            text1: '사용자 비밀번호가 초기화 됐습니다'
          }
          commit('alertDialog/OPEN_DIALOG', data, { root: true })
        })
        .catch(error => {
          util.printError(error)
        })
    },
    // 유저 삭제 API
    [DELETE_USER]: async function ({ commit }, { usr_id }) {
      await axios.delete(`${DEV_SERVER}/${USERS}/` + usr_id)
        .then(() => {
          commit('deleteUserDialog/CLOSE_DIALOG', null, { root: true })
          let data = {
            visible: true,
            title: '사용자 삭제',
            text1: usr_id + '가 삭제 됐습니다'
          }
          commit('alertDialog/OPEN_DIALOG', data, { root: true })
          commit(DELETE_USER, usr_id)
        })
        .catch(error => {
          util.printError(error)
        })
    },
  }
}