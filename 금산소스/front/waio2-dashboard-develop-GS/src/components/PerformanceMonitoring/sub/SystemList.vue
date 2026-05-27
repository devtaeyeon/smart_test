<template>
  <div class="system-list-container">
    <!-- 헤더 -->
    <div class="main-list-header-container">
      <div class="main-list-header-text">시스템 리스트</div>
    </div>
    <!-- 메인 리스트 컨테이너 -->
    <div v-if="$store.state.performance.selectedHost !== null" class="main-list-container">
      <template v-for="(item, index) in $store.state.performance.resources">
        <div v-if="$store.state.performance.selectedHost.systemInfo.host === item.systemInfo.host && !item.modify.enable" class="system-list-item-container" :key="index" @click="onClickHostname(item)">
          <div class="block">
            <div class="block-inner-container">
              <div class="system-list-item-icon" :class="{ on: isPowerOn(item.systemInfo.sys_upd_ti) }"></div>
              <div class="system-list-item-text">{{ item.systemInfo.sys_nm }}</div>
              <div v-if="$store.state.login.user.usr_id !== null" class="system-list-item-modify-icon" @click="onClickModifyBtn(item)"></div>
            </div>
          </div>
          <div class="system-list-divider"></div>
        </div>
        <div v-else-if="item.modify.enable" class="system-list-item-container" :key="index" @click="onClickHostname(item)">
          <div class="modify">
            <div class="modify-inner-container">
              <input class="input-box" type="text" v-model="item.modify.name"/>
              <div class="system-list-item-confirm-icon" @click="changeName(item)"></div>
              <div class="system-list-item-cancel-icon" @click="onClickModifyCancelBtn(item)"></div>
            </div>
            <div class="system-list-divider"></div>
          </div>
          <div class="system-list-divider"></div>
        </div>
        <div v-else class="system-list-item-container" :key="index" @click="onClickHostname(item)">
          <div class="unblock">
            <div class="unblock-inner-container">
              <div class="system-list-item-icon" :class="{ on: isPowerOn(item.systemInfo.sys_upd_ti) }"></div>
              <div class="system-list-item-text">{{ item.systemInfo.sys_nm }}</div>
              <div v-if="$store.state.login.user.usr_id !== null" class="system-list-item-modify-icon" @click="onClickModifyBtn(item)"></div>
            </div>
          </div>
          <div class="system-list-divider"></div>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import moment from 'moment'
import { SET_OVERLAY } from '@/store'

export default {
  name: 'SystemList',
  data: () => ({
    now: moment()
  }),
  computed: {
  },
  methods: {
    /**
     * 호스트 이름을 선택했을 때 실행되는 함수
     * 해당 호스트에 해당하는 API를 요청함
     */
    onClickHostname: function(data) {
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('performance/SET_SELECTED_HOST', data),
        this.$store.dispatch('performance/GET_RESOURCES_MONITORING_HOSTNAME', data.systemInfo.host)
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    },
    /**
     * 파워 ON/OFF를 체크하는 함수
     * update_time이 5분 이내면 ON, 아니면 OFF
     * 
     * @param data API 응답값의 최신 update_time
     * @return true or false
     */
    isPowerOn: function(data) {
      return moment.duration(this.now.diff(moment(data))).asMinutes() < 5 ? true : false
    },
    /**
     * 호스트 이름 수정 아이콘을 선택했을 때
     * 
     * @param data 선택된 호스트 객체
     */
    onClickModifyBtn: function(data) {
      data.modify.enable = true
      data.modify.name = data.systemInfo.sys_nm
    },
    /**
     * 호스트 이름 수정을 취소 했을 때
     * 
     * @param data 선택된 호스트 객체
     */
    onClickModifyCancelBtn: function(data) {
      data.modify.enable = false
      data.modify.name = data.systemInfo.sys_nm
    },
    /**
     * 호스트 이름을 변경하는 경우
     * 변경된 이름으로 호스트 이름 변경 API 요청
     * 
     * @param data 선택한 호스트 객체
     */
    changeName: function(data) {
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('performance/PUT_NAME', { hostname: data.systemInfo.host, name: data.modify.name })
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
    }
  }
}
</script>
<style lang="scss" scoped>
// 최상위 컨테이너
.system-list-container {
  margin-top: 16px;
  margin-left: 17px;
  width: 347px;
  height: 522px;
  display: flex;
  flex-flow: column;
  align-items: center;
  // 헤더 컨테이너
  .main-list-header-container {
    width: 100%;
    height: 59px;
    background-image: url('../../../assets/editableDashboard/title_under.png');
    background-position-x: -3px;
    background-position-y: 13px;
    display: inline-flex;
    align-items: center;
    .main-list-header-text {
      margin-top: 5px;
      width: 200px;
      font-size: 18px;
      font-weight: bold;
      text-shadow: 0 0 9px #5cafff;
      color: #ffffff;
      margin-left: 40px;
    }
  }
  // 메인 리스트 컨테이너
  .main-list-container {
    width: 335px;
    height: 462px;
    overflow-x: hidden;
    overflow-y: auto;
    display: flex;
    flex-flow: column;
    align-items: center;
    background-image: url('../../../assets/editableDashboard/system_list_contents_box.png');
    // 시스템 리스트 아이템 컨테이너
    .system-list-item-container {
      margin-top: 10px;
      padding: 2px 2px;
      width: 316px;
      height: 69px;
      cursor: pointer;
      // 스크롤바 설정
      &::-webkit-scrollbar {
        display: none;
      }
      // 아이템간 구분선
      .system-list-divider {
        margin: 4px 0px;
        width: 318px;
        height: 7px;
        background-image: url('../../../assets/editableDashboard/line.png');
      }
      // 선택된 아이템
      .block {
        position: relative;
        z-index: 0;
        width: 312px;
        height: 65px;
        &::before, &::after {
          content: '';
          position: absolute;
          left: -2px;
          top: -2px;
          background: linear-gradient(45deg, #29f39b, #16cbca, #02a2fa,#16cbca, #29f39b, #16cbca, #02a2fa, #16cbca, #28f09c);
          background-size: 400%;
          width: calc(100% + 4px);
          height: calc(100% + 4px);
          z-index: -1;
          -webkit-animation: steam 20s linear infinite;
          animation: steam 20s linear infinite;
        }
        .block-inner-container {
          position: relative;
          width: 100%;
          height: 100%;
          z-index: 1;
          background-color: #0A1C66;
          display: inline-flex;
          align-items: center;
        }
      }
      // 선택 안된 아이템
      .unblock {
        position: relative;
        z-index: 0;
        width: 312px;
        height: 65px;
        .unblock-inner-container {
          position: relative;
          width: 100%;
          height: 100%;
          z-index: 1;
          display: inline-flex;
          align-items: center;
        }
      }
    }
  }
}









.system-list-item-icon {
  margin-left: 20px;
  margin-right: 17px;
  width: 26px;
  height: 27px;
  background-image: url('../../../assets/performanceMonitoring/icon_system_list_item.png');
  background-size: 100%;
}
.system-list-item-icon.on {
  margin-left: 20px;
  margin-right: 17px;
  width: 26px;
  height: 27px;
  background-image: url('../../../assets/performanceMonitoring/icon_system_list_item_selected.png');
  background-size: 100%;
}
.system-list-item-text {
  font-size: 16px;
  font-weight: bold;
  color: #ffffff;
}
.system-list-item-modify-icon {
  margin-left: auto;
  margin-right: 10px;
  width: 30px;
  height: 30px;
  background-image: url('../../../assets/performanceMonitoring/icon_system_list_modify_mouseout.png');
  background-size: 100%;
  &:hover {
    background-image: url('../../../assets/performanceMonitoring/icon_system_list_modify_mouseover.png');
  }
}
.modify {
  position: relative;
	z-index: 0;
	width: 312px;
  height: 65px;
  .modify-inner-container {
    position: relative;
    width: 100%;
    height: 100%;
    z-index: 1;
    display: inline-flex;
    align-items: center;
  }
  .input-box {
    width: 180px;
    height: 34px;
    border-bottom: solid 1px #fff;
    font-size: 15px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    letter-spacing: normal;
    text-align: left;
    text-shadow: 0 0 9px #5cafff;
    color: #fff;
    padding-left: 11px;
  }
  .system-list-item-confirm-icon {
    margin-left: auto;
    margin-right: 10px;
    width: 30px;
    height: 30px;
    background-image: url('../../../assets/performanceMonitoring/icon_system_list_confirm_mouseout.png');
    background-size: 100%;
    &:hover {
      background-image: url('../../../assets/performanceMonitoring/icon_system_list_confirm_mouseover.png');
    }
  }
  .system-list-item-refresh-icon {
    margin-right: 10px;
    width: 30px;
    height: 30px;
    background-image: url('../../../assets/performanceMonitoring/icon_system_list_refresh_mouseout.png');
    background-size: 100%;
    &:hover {
      background-image: url('../../../assets/performanceMonitoring/icon_system_list_refresh_mouseover.png');
    }
  }
  .system-list-item-cancel-icon {
    margin-right: 10px;
    width: 30px;
    height: 30px;
    background-image: url('../../../assets/performanceMonitoring/icon_system_list_cancel_mouseout.png');
    background-size: 100%;
    &:hover {
      background-image: url('../../../assets/performanceMonitoring/icon_system_list_cancel_mouseover.png');
    }
  }
}
</style>