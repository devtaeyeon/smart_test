<template>
  <div class="performance-monitoring-container">
    <!-- 하드웨어 모니터링 컨테이너 -->
    <div class="hardware-monitoring-container">
      <!-- 헤더 -->
      <div class="performace-monitoring-header-container">
        <div class="header-icon hardware"></div>
        <div class="header-text">Hardware Monitoring</div>
      </div>
      <!-- 컨텐츠 -->
      <div class="hardware-monitoring-content-container">
        <!-- 시스템 리스트 -->
        <SystemList/>
        <!-- 시스템 정보 -->
        <SystemInfo/>
        <!-- 시스템 모니터링 -->
        <div class="system-monitoring-container">
            <SystemMonitoring />
          </div>
      </div>
    </div>
    <!-- 소프트웨어 모니터링 컨테이너 -->
    <div class="software-monitoring-container">
      <!-- 헤더 -->
      <div class="performace-monitoring-header-container">
        <div class="header-icon software"></div>
        <div class="header-text">software Monitoring</div>
      </div>
      <div class="software-monitoring-content-container">
        <!-- 시각화 서비스 -->
        <VisualizationService />
        <!-- 데이터 수집 Agent -->
        <DataCollectionAgent />
        <!-- 분석 시스템 -->
        <AnalysisSystem />
      </div>
    </div>
  </div>
</template>

<script>
import SystemList from '@/components/PerformanceMonitoring/sub/SystemList'
import SystemInfo from '@/components/PerformanceMonitoring/sub/SystemInfo'
import SystemMonitoring from '@/components/PerformanceMonitoring/sub/SystemMonitoring'
import VisualizationService from '@/components/PerformanceMonitoring/sub/VisualizationService'
import DataCollectionAgent from '@/components/PerformanceMonitoring/sub/DataCollectionAgent'
import AnalysisSystem from '@/components/PerformanceMonitoring/sub/AnalysisSystem'
export default {
  name: 'PerformanceMonitoring',
  components: {
    SystemList,
    SystemInfo,
    SystemMonitoring,
    VisualizationService,
    DataCollectionAgent,
    AnalysisSystem
  },
  data: () => ({
    hardwareTimer: null, // 하드웨어 API 요청 타이머
    hardwareTimerInterval: 60000, // 하드웨어 API 요청 주기
    softwareTimer: null, // 소프트웨어 API 요청 타이머
    softwareTimerInterval: 60000, // 소프트웨어 API 요청 주기
  }),
  created: function () {
    console.log(this.$options.name + ' created')
  },
  /**
   * 마운트시 실행되는 함수
   * 리소스 정보, 리소스 모니터링 최신 정보를 1분 간격으로 요청함
   */
  mounted: function () {
    console.log(this.$options.name + ' mounted')
    Promise.all([
      this.$store.dispatch('performance/GET_RESOURCES_INFO'),
      this.$store.dispatch('performance/GET_RESOURCES_MONITORING_LATEST')
    ]).finally(() => {
    })
    this.softwareTimer = setInterval(() => {
      this.$store.dispatch('performance/GET_RESOURCES_MONITORING_LATEST')
    }, this.softwareTimerInterval)
  },
  /**
   * 해제되는 경우 실행되는 함수
   * 하드웨어 & 소프트웨어 API 요청 타이머 해제
   */
  destroyed: function () {
    console.log(this.$options.name + ' destoryed')
    clearInterval(this.hardwareTimer)
    clearInterval(this.softwareTimer)
  },
  updated: function () {
    console.log(this.$options.name + ' updated')
  },
  watch: {
    /**
     * $store.state.performance.selectedHost 값의 변화를 모니터링
     * 선택된 호스트에 따라 API요청을 변경해줌
     */
    '$store.state.performance.selectedHost': function () {
      if (this.$store.state.performance.selectedHost.systemInfo.host !== null) {
        if (this.hardwareTimer !== null) {
          clearInterval(this.hardwareTimer)
        }
        this.hardwareTimer = setInterval(() => {
          Promise.all([
            this.$store.dispatch('performance/GET_RESOURCES_MONITORING_HOSTNAME', this.$store.state.performance.selectedHost.systemInfo.host)
          ]).finally(() => {
          })
        }, this.hardwareTimerInterval)
      }
    }
  }
}
</script>

<style scoped lang="scss">
// 최상위 엘리먼트
.performance-monitoring-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-left: 44px;
  padding-top: 26px;
  .hardware-monitoring-container {
    width: 1853px;
    height: 620px;
    .hardware-monitoring-content-container {
      width: 100%;
      height: 556px;
      display: inline-flex;
    }
  }
  .software-monitoring-container {
    margin-top: 23px; 
    width: 1853px;
    height: 321px;
    .software-monitoring-content-container {
      width: 100%;
      height: 257px;
      display: inline-flex;
    }
  }
  .performace-monitoring-header-container {
    width: 1360px;
    height: 64px;
    display: inline-flex;
    background-image: url('../../assets/editableDashboard/middle_title.png');
    background-position-y: 11px;
    padding-top: 4px;
    align-items: center;
  }
  .header-icon {
    margin-left: 26px;
    margin-right: 16px;
  }
  .header-icon.hardware {
    width: 18px;
    height: 18px;
    background-image: url('../../assets/performanceMonitoring/icon_header_hw.png');
    background-size: 100%;
  }
  .header-icon.software {
    width: 20px;
    height: 18px;
    background-image: url('../../assets/performanceMonitoring/icon_header_sw.png');
    background-size: 100%;
  }
  .header-text {
    font-size: 18px;
    text-shadow: 0 0 9px #5cafff;
    color: #ffffff;
  }
}







</style>