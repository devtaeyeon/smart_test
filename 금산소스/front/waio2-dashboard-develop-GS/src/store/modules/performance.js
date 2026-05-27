import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

const GET_RESOURCES_INFO = 'GET_RESOURCES_INFO'
const SET_SELECTED_HOST = 'SET_SELECTED_HOST'
const GET_RESOURCES_MONITORING_HOSTNAME = 'GET_RESOURCES_MONITORING_HOSTNAME'
const GET_RESOURCES_MONITORING_LATEST = 'GET_RESOURCES_MONITORING_LATEST'
const PUT_NAME = 'PUT_NAME'
const RESOURCES = 'resources'
const INFO = 'info'
const MONITORING = 'monitoring'
export default {
  namespaced: true,
  state: {
    resources: [],
    monitoring: [],
    monitoringLatest: [],
    selectedHost: null,
    selectedDiskIndex: 0,
    selectedNetworkIndex: 0,
    type: {
      CPU: 1,
      MEMORY: 2,
      DISK: 3,
      ETHERNET_SENT: 4,
      ETHERNET_RECV: 5,
      ANALYSYSTEM_DB: 11,
      VISUALIZATION_API: 12,
      DAQ: 13
    }
  },
  mutations: {
    [GET_RESOURCES_INFO]: function (state, data) {
      data.resources.forEach(element => {
        element.modify = {}
        element.modify.enable = false
        element.modify.name = element.systemInfo.sys_nm
      });
      state.resources = data.resources
      if (state.resources.length > 0) {
        state.selectedHost = data.resources[0]
      }
    },
    [SET_SELECTED_HOST]: function (state, data) {
      state.selectedHost = data
      state.selectedDiskIndex = 0
      state.selectedNetworkIndex = 0
    },
    [PUT_NAME]: function (state, data) {
      state.resources.forEach(element => {
        if (element.systemInfo.host === data.host) {
          element.systemInfo.sys_nm = data.sys_nm  
          element.modify.enable = false
        }
      })
    },
    [GET_RESOURCES_MONITORING_HOSTNAME]: function (state, data) {
      state.monitoring = data.monitoring
    },
    [GET_RESOURCES_MONITORING_LATEST]: function (state, data) {
      state.monitoringLatest = data.monitoring
    }
  },
  actions: {
    [GET_RESOURCES_INFO]: async function ({ commit, dispatch }) {
      await axios.get(`${DEV_SERVER}/${RESOURCES}/${INFO}`)
        .then(({ data }) => {
          commit(GET_RESOURCES_INFO, data)
          if (data.resources.length > 0) {
            dispatch(GET_RESOURCES_MONITORING_HOSTNAME, data.resources[0].systemInfo.host)
          }
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [SET_SELECTED_HOST]: function ({ commit }, data) {
      commit(SET_SELECTED_HOST, data)
    },
    // 호스트 이름 수정
    [PUT_NAME]: async function ({ commit }, { hostname, name }) {
      await axios.put(`${DEV_SERVER}/${RESOURCES}/${INFO}` + '/' + hostname, { name })
        .then(() => {
          let data = {
            visible: true,
            title: '정보 수정',
            text1: '이름이 \'' + name +'\'으로 변경됐습니다'
          }
          commit('alertDialog/OPEN_DIALOG', data, { root: true })
          commit(PUT_NAME, { hostname: hostname, name: name })
        })
        .catch(error => {
          util.printError(error)
        })
    },
    // 전체 모니터링 대상 시스템 정보 조회
    [GET_RESOURCES_MONITORING_HOSTNAME]: async function ({ commit }, hostname) {
      await axios.get(`${DEV_SERVER}/${RESOURCES}/${MONITORING}/` + hostname)
        .then(({ data }) => {
          commit(GET_RESOURCES_MONITORING_HOSTNAME, data)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    // 최근 시스템 모니터링 정보 조회
    [GET_RESOURCES_MONITORING_LATEST]: async function({ commit }) {
      await axios.get(`${DEV_SERVER}/${RESOURCES}/${MONITORING}/latest`)
        .then(({ data }) => {
          commit(GET_RESOURCES_MONITORING_LATEST, data)
        })
        .catch(error => {
          util.printError(error)
        })
    }
  }
}