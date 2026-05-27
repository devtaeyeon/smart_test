import axios from 'axios'
import { DEV_SERVER } from '@/store'
import { util } from '@/service/utils'

const GET_ALL_ALARM_SETTING = 'GET_ALL_ALARM_SETTING'
const PUT_ALARM = 'PUT_ALARM'
const PUT_ALARMS_SEARCH = 'PUT_ALARMS_SEARCH'
const ALARMS = 'alarms'
const SETTING = 'setting'
const SEARCH = 'search'
export const URL = {
  ALARM_NOTIFY: 'alarm/notify',
  ALARM_INFO: 'alarm/info',
  ALARM_CONTROL: 'alarm/control',
  ALARM_CANCEL: 'alarm/cancel',
  ALARM_CONFIRM: 'alarm/confirm',
  ALARM_PUT_ALM_CTR_HIS: 'alarm/almCtrHis',
  ALARM_PUT_CTR_HIS_SEARCH: 'alarm/ctrHisListSearch',
  ALARM_PUT_DETAIL : 'alarm/detail'
}
export const GET_ALARM_NOTIFY = URL.ALARM_NOTIFY + '/get'
export const GET_ALARM_INFO = URL.ALARM_INFO + '/get'
export const PUT_ALARM_CONTROL = URL.ALARM_CONTROL + '/put'
export const PUT_ALARM_CANCEL = URL.ALARM_CANCEL + '/put'
export const PUT_ALARM_CONFIRM = URL.ALARM_CONFIRM + '/put'
export const DEL_NOTIFY = 'alarm/DEL_NOTIFY'
export const PUT_ALARM_CTR_HIS_SEARCH = URL.ALARM_PUT_CTR_HIS_SEARCH + '/put'
export const PUT_ALARM_DETAIL = URL.ALARM_PUT_DETAIL +'/put'
export const PUT_ALARM_ALM_CTR_HIS = URL.ALARM_PUT_ALM_CTR_HIS + '/put'
const GET_NOTIFY = GET_ALARM_NOTIFY.substr(GET_ALARM_NOTIFY.indexOf('/') + 1)
const GET_INFO = GET_ALARM_INFO.substr(GET_ALARM_INFO.indexOf('/') + 1)
const PUT_CONTROL = PUT_ALARM_CONTROL.substr(PUT_ALARM_CONTROL.indexOf('/') + 1)
const PUT_CANCEL = PUT_ALARM_CANCEL.substr(PUT_ALARM_CANCEL.indexOf('/') + 1)
const PUT_CONFIRM = PUT_ALARM_CONFIRM.substr(PUT_ALARM_CONFIRM.indexOf('/') + 1)
const PUT_ALM_CTR_HIS = PUT_ALARM_ALM_CTR_HIS.substr(PUT_ALARM_ALM_CTR_HIS.indexOf('/') + 1)
const PUT_CTR_HIS_SEARCH = PUT_ALARM_CTR_HIS_SEARCH.substr(PUT_ALARM_CTR_HIS_SEARCH.indexOf('/') + 1)
const PUT_DETAIL = PUT_ALARM_DETAIL.substr(PUT_ALARM_DETAIL.indexOf('/') + 1)

export default {
  namespaced: true,
  state: {
    alarmInfo: [],
    alarmInfoV2: [],
    alarms: [], // 알람이력
    alarms7Days: [],
    startTime: null,
    endTime: null,
    formattedStartTime: null,
    formmatedEndTime: null,
    alarm: [], // notify push 알림
    noActionAlarmList: [],
    almCtrHisList:[],
    almCtrHisListCnt: {
      totalCnt: 0,
      controlCnt : 0,
      cancelCnt : 0,
      noActionCnt : 0,
      autoControlCnt: 0,
    },
    popupVisible: false,
    selectedProcessName: '',
    selectedClassification: 'total',
    cfChangeFlag : true,
    alarmDetail: {
      ctrListByAlm: [],
      factorMap: {},
      procCd: ""
    },
  },
  mutations: {
    [GET_ALL_ALARM_SETTING]: function (state, data) {
      state.alarmInfo = data.alarm_info
    },
    [PUT_ALARM]: function (state, data) {
      let find = state.alarmInfo.find((element) => element.seq === data.seq)
      find.dp_nm = data.dp_nm
      find.cmp_val = data.cmp_val
      find.scd_snd = data.scd_snd
    },
    [GET_NOTIFY]: function (state, data) {
      if(data !== undefined){
        // 알림 수신시 알람에 70초를 더함(10초를 밑에서 뺴면 1분임)
        data.map((it) => {
          it.timeLeft = 310
          it.isVisible = true
        })
        if (state.alarm.length === 0) {
          state.alarm = data
        } else {
          // 수신된 알림이 저장된 알림 리스트에 있는지 확인하여 없는 값들만 추가함
          for(let j=0; j<data.length; j++) {
            let _data = data[j]
            let isDuplication = false
            for(let i=0; i< state.alarm.length; i++) {
              let _alarm = state.alarm[i]
              if (_alarm.seq === _data.seq) {
                isDuplication = true
                break
              }
            }
            if (!isDuplication) {
              state.alarm.push(_data)
            }
          }
        }
      }
      
      // 1분이 지난 알림은 리스트에서 제거
      // state.alarm = state.alarm.filter((it) => {
      //   it.timeLeft -= 10
      //   // console.log(it.seq, it.timeLeft)
      //   return it.timeLeft > 0
      // })
      // 1분이 지난 알림은 isVisible을 false로 변환 && 무반응 알림 리스트에 추가
      // 5분이 지난 알림은 리스트에서 제거.
      let tempAlmArray = [];
      state.alarm.forEach((it) => {
        it.timeLeft -= 10
        if(it.timeLeft == 240){ //1분이 지난 알람
          if(it.isVisible){ //1분이 지날때까지 처리되지 않은 제어 알람 - noActionAlarmList 추가
            if(it.alm_ty == 2 || it.alm_ty == 4){
              state.noActionAlarmList.push(it)
            }
          }
          //1분이 지난 모든 알람은 팝업 숨김 처리
          it.isVisible = false;
        }
        //5분이 지난 알람은 리스트에서 제외
        if(it.timeLeft > 0) {
          tempAlmArray.push(it)
        }
      })
      state.alarm = tempAlmArray;
    },
    'DEL_NOTIFY': function (state, data) {
      state.alarm.forEach((it) => {
        if (it.seq === data) {
          it.isVisible = false;
        }
      })
    },
    [GET_INFO]: function (state, data) {
      state.alarmInfoV2 = data
    },
    'EMPTY_NO_ACTION_ALARM_LIST': function (state, currentNoActionAlarmList) {
      currentNoActionAlarmList.forEach((almItem) => {
        state.noActionAlarmList = state.noActionAlarmList.filter((state) => state.seq !== almItem.seq);
      });
    },
    [PUT_CTR_HIS_SEARCH] : function (state, data) {
      if (data.isInit) {
        state.alarms7Days = data.almCtrHisList
      }
      state.almCtrHisList = data.almCtrHisList
    },
    [PUT_DETAIL]: function (state, data){
      state.alarmDetail = data.alarmDetail
    }
  },
  actions: {
    [PUT_ALARMS_SEARCH]: async function({ commit }, { start_time, end_time, isInit }) {
      await axios.put(`${DEV_SERVER}/${ALARMS}/${SEARCH}`, { start_time, end_time })
        .then(({ data }) => {
          data.isInit = isInit
          commit(PUT_ALARMS_SEARCH, data)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    // 알람 설정 정보 요청 - 관리자 전용 API
    [GET_ALL_ALARM_SETTING]: async function({ commit, dispatch, state }) {
      await axios.get(`${DEV_SERVER}/${ALARMS}/${SETTING}`)
        .then(({ data }) => {
          commit(GET_ALL_ALARM_SETTING, data)
          dispatch(PUT_ALARMS_SEARCH, { start_time: state.startTime.valueOf(), end_time: state.endTime.valueOf(), isInit: true})
        })
        .catch(error => {
          util.printError(error)
        })
    },
    // 알람 설정 정보 수정 API
    [PUT_ALARM]: async function ({ commit }, { seq, dp_nm, cmp_val, scd_snd }) {
      await axios.put(`${DEV_SERVER}/${ALARMS}/${SETTING}` + '/' + seq, { dp_nm, cmp_val, scd_snd })
        .then(() => {
          commit(PUT_ALARM, { seq, dp_nm, cmp_val, scd_snd })
          commit('modifyAlarmDialog/CLOSE_DIALOG', null, { root: true })
          let data = {
            visible: true,
            title: '알람 정보 수정',
            text1: '알람 정보가 수정 됐습니다'
          }
          commit('alertDialog/OPEN_DIALOG', data, { root: true })
        })
        .catch(error => {
          util.printError(error)
        })
    },
    // v2
    [GET_NOTIFY]: async function ({ commit, dispatch }) {
      await axios.get(`${DEV_SERVER}/${URL.ALARM_NOTIFY}`)
        .then(({ data }) => {
          //알람 팝업 및 알람 시간 계산
          commit(GET_NOTIFY, data.alarm);
          //70초 초과 무반응 알람 취소 처리 및 히스토리 생성
          dispatch('PUT_NO_ACTION_ALM_CTR_HIS')
        })
        .catch(error => {
          commit(GET_NOTIFY, []);
          util.printError(error);
          //70초 초과 무반응 알람 취소 처리 및 히스토리 생성
          dispatch('PUT_NO_ACTION_ALM_CTR_HIS')
        })
    },
    'PUT_NO_ACTION_ALM_CTR_HIS': async function ({ commit , state, rootState, dispatch }){
      if (state.noActionAlarmList.length === 0) {
        return
      }
      //noActionAlarmList에서 ctrList 추출 후, controller 전송
      let currentNoActionAlarmList = [...state.noActionAlarmList];
      let almCtrList = []
      currentNoActionAlarmList.forEach((it) => almCtrList.push(...it.ctr_list.map((item) =>({...item, alm_seq: it.seq, ctr_yn: ''}))));

      //Kafka Flag CANCEL update
      currentNoActionAlarmList.forEach((it) => dispatch(PUT_CANCEL, {alm_id: it.alm_id, alm_ntf_ti: it.alm_ntf_ti, no_action_flg :'Y' }));

      axios.defaults.headers.common['X-ACCESS-TOKEN'] = rootState.login.user.tkn === null ? "" : rootState.login.user.tkn
      await axios.put(`${DEV_SERVER}/${URL.ALARM_PUT_ALM_CTR_HIS}/put`, almCtrList)
        .then(()=>{
          commit('EMPTY_NO_ACTION_ALARM_LIST', currentNoActionAlarmList)
        }).catch(error =>{
          util.printError(error);
          commit('EMPTY_NO_ACTION_ALARM_LIST', currentNoActionAlarmList)
        })
    },
    [PUT_ALM_CTR_HIS] : async function ({ commit, rootState }, { ctrList, almSeq, ctrYn }){
      // console.log('PUT_ALM_CTR_HIS'+ ctrList, almSeq, ctrYn );
      let almCtrList = []
      ctrList.forEach((it) => almCtrList.push({...it, alm_seq : almSeq, ctr_yn : ctrYn}));
      axios.defaults.headers.common['X-ACCESS-TOKEN'] = rootState.login.user.tkn === null ? "" : rootState.login.user.tkn
      await axios.put(`${DEV_SERVER}/${URL.ALARM_PUT_ALM_CTR_HIS}/put`, almCtrList)
      .then(()=>{
        commit()
      }).catch(error =>{
        util.printError(error)
      })
    },
    // v2
    [GET_INFO]: async function ({ commit }) {
      await axios.get(`${DEV_SERVER}/${URL.ALARM_INFO}`)
        .then(({ data }) => {
          commit(GET_INFO, data.alarm_info)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    // v2
    [PUT_CONTROL]: async function ({ commit }, { alm_id, alm_ntf_ti}) { // eslint-disable-line no-unused-vars
      await axios.put(`${DEV_SERVER}/${URL.ALARM_CONTROL}`, { alm_id, alm_ntf_ti})
        .then(() => {
          // commit()
        })
        .catch(error => {
          if (error.response && error.response.status === 409) {
            let _data = {
              visible: true,
              title: '',
              text1: '이미 만료된 알람입니다.'
            }
            commit('alertDialog/OPEN_DIALOG', _data, { root: true })
          } else {
          util.printError(error)
          }
        })
    },
    // v2
    [PUT_CANCEL]: async function ({ commit }, { alm_id, alm_ntf_ti, no_action_flg}) { // eslint-disable-line no-unused-vars
      var url = `${DEV_SERVER}/${URL.ALARM_CANCEL}`
      if(no_action_flg){ //무반응 알람 해당할 시, 파라미터에 플래그 추가
        url += `/?noActionFlag=${encodeURIComponent(no_action_flg)}`
      }
      await axios.put(url, { alm_id, alm_ntf_ti})
        .then(() => {
          // commit()
        })
        .catch(error => {
          if (error.response && error.response.status === 409) {
            let _data = {
              visible: true,
              title: '',
              text1: '이미 만료된 알람입니다.'
            }
            commit('alertDialog/OPEN_DIALOG', _data, { root: true })
          } else {
          util.printError(error)
          }
        })
    },
    [PUT_CONFIRM]: async function ({ commit }, { alm_id, alm_ntf_ti}) { // eslint-disable-line no-unused-vars
      await axios.put(`${DEV_SERVER}/${URL.ALARM_CONFIRM}`, { alm_id, alm_ntf_ti})
        .then(() => {
          // commit()
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_CTR_HIS_SEARCH]: async function({ commit }, { start_time, end_time, isInit }) {
      await axios.put(`${DEV_SERVER}/${URL.ALARM_PUT_CTR_HIS_SEARCH}`, { start_time, end_time })
        .then(({ data }) => {
          data.isInit = isInit
          commit(PUT_CTR_HIS_SEARCH, data)
        })
        .catch(error => {
          util.printError(error)
        })
    },
    [PUT_DETAIL]: async function({ state, commit }, ctrInfo) {
      await axios.put(`${DEV_SERVER}/${URL.ALARM_PUT_DETAIL}`, ctrInfo)
        .then(({ data }) => {
          commit(PUT_DETAIL, data)
        })
        .catch(error => {
          util.printError(error)
          state.alarmDetail = {
            ctrListByAlm: [],
            factorMap: {},
            procCd: ""
          }
        })
    }
  },
}