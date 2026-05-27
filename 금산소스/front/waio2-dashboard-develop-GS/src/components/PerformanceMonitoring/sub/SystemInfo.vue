<template>
  <div class="system-information-container">
    <!-- 시스템 정보 헤더 -->
    <div class="main-list-header-container">
      <div class="main-list-header-text">시스템 정보</div>
    </div>
    <!-- 시스템 정보 테이블 -->
    <div v-if="$store.state.performance.selectedHost !==null" class="system-information-item">
      <div class="system-information-box">
        <div class="system-information-name"><span>Hostname</span></div>
        <div class="system-information-content"><span>{{ $store.state.performance.selectedHost.systemInfo.host }}</span></div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>OS</span></div>
        <div class="system-information-content" :title="$store.state.performance.selectedHost.systemInfo.os"><span>{{ $store.state.performance.selectedHost.systemInfo.os }}</span></div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>System Model</span></div>
        <div class="system-information-content"><span>{{ $store.state.performance.selectedHost.systemInfo.md }}</span></div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>Processor</span></div>
        <div class="system-information-content" :title="$store.state.performance.selectedHost.systemInfo.prc_nm"><span>{{ $store.state.performance.selectedHost.systemInfo.prc_nm }}</span></div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>Max Frequency</span></div>
        <div class="system-information-content"><span>{{ (($store.state.performance.selectedHost.systemInfo.max_freq / 1000) / 1000) / 1000 | numFormat('0.00')}}GHz</span></div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>Core/Package(s)</span></div>
        <div class="system-information-content"><span>{{ $store.state.performance.selectedHost.systemInfo.core_cnt }}cores / {{ $store.state.performance.selectedHost.systemInfo.prc_cnt }}ea</span></div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>Memory Capacity</span></div>
        <div class="system-information-content"><span>{{ (( $store.state.performance.selectedHost.systemInfo.tot_mem / 1024) / 1024) / 1024 | numFormat('0.00')}}Gbyte</span></div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>Disk Model</span></div>
        <div class="system-information-content">
          <select class="system-information-select" v-model="$store.state.performance.selectedDiskIndex">
            <option v-for="(item, index) in $store.state.performance.selectedHost.diskInfo" :key="index" :value="index">
              {{ item.model }}
            </option>
          </select>
        </div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>Disk Size</span></div>
        <div class="system-information-content"><span>{{ (( $store.state.performance.selectedHost.diskInfo[$store.state.performance.selectedDiskIndex].size / 1000) / 1000) / 1000 | numFormat('0.00')}}GB</span></div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>Interface</span></div>
        <div class="system-information-content">
          <select class="system-information-select" v-model="$store.state.performance.selectedNetworkIndex">
            <option v-for="(item, index) in $store.state.performance.selectedHost.interfaceInfo" :key="index" :value="index">
              {{ item.name }}
            </option>
          </select>
        </div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>IPv4</span></div>
        <div class="system-information-content"><span>{{ $store.state.performance.selectedHost.interfaceInfo[$store.state.performance.selectedNetworkIndex].ipv4 }}</span></div>
      </div>
      <div class="system-information-box">
        <div class="system-information-name"><span>MAC address</span></div>
        <div class="system-information-content"><span>{{ $store.state.performance.selectedHost.interfaceInfo[$store.state.performance.selectedNetworkIndex].mac }}</span></div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'SystemInfo',
  data: () => ({
  }),
  computed: {
  },
  methods: {
  }
}
</script>
<style lang="scss" scoped>
// 최상위 엘리먼트
.system-information-container{
  margin-top: 16px;
  margin-left: 17px;
  width: 497px;
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
  // 시스템 정보 테이블 아이템
  .system-information-item {
    width: 497px;
    height: 462px;
    display: flex;
    flex-flow: column;
    align-items: center;
    justify-content: center;
    background-image: url('../../../assets/editableDashboard/system_info_contents_box1.png');
    background-size: 100% 100%;
    .system-information-box {
      display: flex;
      font-size: 14px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      letter-spacing: normal;
      text-align: left;
      text-shadow: 0 0 9px #5cafff;
      color: #ffffff;
      // 시스템 정보 이름
      .system-information-name {
        display: flex;
        width: 161px;
        height: 32px;
        background-color: #0e3283;
        padding: 0 0 0 13px;
        > span {
          margin: auto 0;
        }
      }
      // 시스템 정보 컨텐츠
      .system-information-content {
        display: flex;
        width: 298px;
        height: 32px;
        margin: 0 0 5px 10px;
        border: solid 1px rgba(157, 191, 255, 0.5);
        background-color: rgba(157, 191, 255, 0.07);
        padding: 0 0 0 13px;
        > span {
          margin: auto 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        // Select 박스
        .system-information-select {
          width: 271.6px;
          padding-right: 12.6px;
          font-size: 14px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 1.63;
          letter-spacing: normal;
          text-align: left;
          text-shadow: 0 0 9px #5cafff;
          color: #ffffff;
          text-overflow: ellipsis;
          white-space: nowrap;
          background: url('../../../assets/performanceMonitoring/select_down_arrow.png') no-repeat 100% 50%;
          background-position-x: 260px;
          border-radius: 0px;
          appearance: none;
          cursor: pointer;
          option {
            background-color: #194B7E;
          }
        }
      }
    }
  }
}
</style>