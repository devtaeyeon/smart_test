<template>
  <div class="system-software-service1">
    <!-- 분석 시스템 헤더 컨테이너 -->
    <div class="main-list-header-container">
      <div class="main-list-header-text2">분석 시스템</div>
    </div>
    <!-- 분석 시스템 상태 컨테이너 -->
    <div class="software-service-container1">
      <!-- 분석 플랫폼 -->
      <div class="software-service-contents1">
        <div class="software-service-box1">
          <div class="software-arrow-img1"></div>
          <div class="software-box-text">분석 플랫폼</div>
        </div>
        <div class="software-box-contents">
          <div class="software-box-standby">
            <div class="box-contents-text">Resource Manager</div>
            <div v-if="isActiveResourceManager1" class="active-box-active">active</div>
            <div v-else class="standby-box-standby">standby</div>
          </div>
          <div class="box-connect-img"></div>
          <div class="software-box-active">
            <div class="box-contents-text">Resource Manager</div>
            <div v-if="isActiveResourceManager2" class="active-box-active">active</div>
            <div v-else class="standby-box-standby">standby</div>
          </div>
        </div>
        <div class="double-connect-img"></div>
        <div class="software-box-active-node">
          Active Node : {{ getNodeManagerCount }}
        </div>
      </div>
      <!-- HDFS -->
      <div class="software-service-contents2">
        <div class="software-service-box1">
          <div class="software-arrow-img1"></div>
          <div class="software-box-text">HDFS</div>
        </div>
        <div class="software-box-contents">
          <div class="software-box-standby">
            <div class="box-contents-text">Name Node</div>
            <div v-if="isActiveNameNode1" class="active-box-active">active</div>
            <div v-else class="standby-box-standby">standby</div>
          </div>
          <div class="box-connect-img"></div>
          <div class="software-box-active">
            <div class="box-contents-text">Name Node</div>
            <div v-if="isActiveNameNode2" class="active-box-active">active</div>
            <div v-else class="standby-box-standby">standby</div>
          </div>
        </div>
        <div class="double-connect-img"></div>
        <div class="software-box-active-node">
          Live Data Node : {{ getDataNodeCount }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AnalysisSystem',
  computed: {
    // NameNode1 연결 상태
    isActiveNameNode1() {
      if (this.$store.state.performance.monitoringLatest.length > 0) {
        if (this.$store.state.performance.monitoringLatest.filter(it => 
          it.type === this.$store.state.performance.type.ANALYSYSTEM_DB && 
          it.hostname === 'analysis1' && it.name === 'Name Node' &&
          it.value === 'ACTIVE').length > 0) {
          return true
        }
      }
      return false
    },
    // NameNode2 연결 상태
    isActiveNameNode2() {
      if (this.$store.state.performance.monitoringLatest.length > 0) {
        if (this.$store.state.performance.monitoringLatest.filter(it =>  
          it.type === this.$store.state.performance.type.ANALYSYSTEM_DB && 
          it.hostname === 'analysis2' && it.name === 'Name Node' &&
          it.value === 'ACTIVE').length > 0) {
          return true
        }
      }
      return false
    },
    // ResourceManager1 연결 상태
    isActiveResourceManager1() {
      if (this.$store.state.performance.monitoringLatest.length > 0) {
        if (this.$store.state.performance.monitoringLatest.filter(it => 
          it.type === this.$store.state.performance.type.ANALYSYSTEM_DB && 
          it.hostname === 'analysis1' && it.name === 'Resource Manager' &&
          it.value === 'ACTIVE').length > 0) {
          return true
        }
      }
      return false
    },
    // ResourceManager2 연결 상태
    isActiveResourceManager2() {
      if (this.$store.state.performance.monitoringLatest.length > 0) {
        if (this.$store.state.performance.monitoringLatest.filter(it => 
          it.type === this.$store.state.performance.type.ANALYSYSTEM_DB && 
          it.hostname === 'analysis2' && it.name === 'Resource Manager' &&
          it.value === 'ACTIVE').length > 0) {
          return true
        }
      }
      return false
    },
    // NodeManager 개수
    getNodeManagerCount() {
      if (this.$store.state.performance.monitoringLatest.length > 0) {
        let find = this.$store.state.performance.monitoringLatest.find((it) => 
          it.type === this.$store.state.performance.type.ANALYSYSTEM_DB && 
          it.name === 'Node Manager')
        if (find !== undefined && find !== null) {
          return find.value
        } else {
          return 0
        }
      }
      return 0
    },
    // DataNode 개수
    getDataNodeCount() {
      if (this.$store.state.performance.monitoringLatest.length > 0) {
        let find = this.$store.state.performance.monitoringLatest.find((it) => 
          it.type === this.$store.state.performance.type.ANALYSYSTEM_DB && 
          it.name === 'Data Node')
        if (find !== undefined && find !== null) {
          return find.value
        } else {
          return 0
        }
      }
      return 0
    }
  }
}
</script>
<style scoped>
.performance-monitoring-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-left: 44px;
  padding-top: 26px;
}
.hardware-monitoring-container {
  width: 1853px;
  height: 620px;
  background-color: rgba(0, 6, 77, 0.45);
}
.performace-monitoring-header-container {
  width: 1360px;
  height: 64px;
  display: inline-flex;
  background-image: linear-gradient(to right, rgba(0, 9, 81, 0.56), rgba(0, 7, 79, 0));
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
  background-image: url('../../../assets/performanceMonitoring/icon_header_hw.png');
  background-size: 100%;
}
.header-icon.software {
  width: 20px;
  height: 18px;
  background-image: url('../../../assets/performanceMonitoring/icon_header_sw.png');
  background-size: 100%;
}
.header-text {
  font-size: 18px;
  text-shadow: 0 0 9px #5cafff;
  color: #ffffff;
}
.hardware-monitoring-content-container {
  width: 100%;
  height: 556px;
  display: inline-flex;
}
.software-monitoring-content-container {
  width: 100%;
  height: 257px;
  display: inline-flex;
}

.main-list-header-container {
  width: 100%;
  height: 41px;
  display: inline-flex;
  align-items: center;
}
.main-list-header-icon {
  margin-left: 23px;
  margin-right: 6px;
  width: 13px;
  height: 14px;
  background-image: url('../../../assets/common/icon_main_list_header.png');
  background-size: 100%;
}
.main-list-header-text {
  margin-top: 5px;
  width: 200px;
  font-size: 18px;
  font-weight: bold;
  text-shadow: 0 0 9px #5cafff;
  color: #ffffff;
}
.main-list-header-icon2 {
  margin-left: 14.8px;
  margin-right: 6px;
  width: 13px;
  height: 14px;
  background-image: url('../../../assets/common/icon_main_list_header.png');
  background-size: 100%;
}
.main-list-header-text2 {
  margin-top: 5px;
  width: 200px;
  height: 41px;
  font-size: 18px;
  font-weight: bold;
  color: #ffffff;
  background-image: url('../../../assets/editableDashboard/title_under.png');
  background-position-x: -30px;
  background-position-y: -6px;
  padding-left: 13px;
  text-shadow: 0 0 9px #5cafff;
}
.main-list-divider3 {
  width: 369px;
  height: 1px;
  background-image: url('../../../assets/editableDashboard/bg_dashline01.png');
  background-repeat: repeat-x;
}
.main-list-divider4 {
  width: 954.4px;
  height: 1px;
  background-image: url('../../../assets/editableDashboard/bg_dashline01.png');
  background-repeat: repeat-x;
}

.system-monitoring-container {
  display: flex;
  position: absolute;
  left: 942px;
}
.software-monitoring-container {
  margin-top: 23px; 
  width: 1853px;
  height: 321px;
  background-color: rgba(0, 6, 77, 0.45);
}
.system-software-service {
  margin: 5px 0px 0px 17px;
  width: 392px;
  height: 226px;
  /* background-color: rgba(0, 6, 77, 0.45); */
  display: inline-flex;
  flex-flow: column;
  align-items: center
}
.system-software-service1 {
  margin: 14px 0px 0px 17px;
  width: 990.4px;
  height: 226px;
  /* background-color: rgba(0, 6, 77, 0.45); */
  display: inline-flex;
  flex-flow: column;
  align-items: center
}
.software-service-container {
  width: 392px;
  height: 149px;
  padding: 6px 22px 0 19px;
  display: inline-flex;
}
.software-service-container1 {
  width: 990.4px;
  height: 174px;
  padding: 9px 38px 0 31.4px;
  display: inline-flex;
  background-image: url('../../../assets/editableDashboard/software_service_container1.png');
  margin-top: 10px;
  background-size: 98% 95%;
}
.software-service-content1 {
  width: 164px;
  height: 100%;
}
.software-service-content2 {
  margin-left: 23px;
  width: 164px;
  height: 100%;
}
.software-service-contents1 {
  width: 449px;
  height: 100%;
}
.software-service-contents2 {
  margin-left: 23px;
  width: 449px;
  height: 100%;
}
.software-service-box {
  width: 164px;
  height: 19px;
  background-color: rgba(255, 255, 255, 0.3);
  display: flex;
}
.software-service-box1 {
  width: 449px;
  height: 19px;
  background-color: rgba(255, 255, 255, 0.3);
  display: flex;
}
.software-arrow-img {
  background-image: url("../../../assets/common/icon_right_white_arrow.png");
  background-size: 100%;
  width: 6px;
  height: 7px;
  margin-top: 5px;
  margin-left: 35.8px;
}
.software-arrow-img1 {
  background-image: url("../../../assets/common/icon_right_white_arrow.png");
  background-size: 100%;
  width: 6px;
  height: 7px;
  margin-top: 5px;
  margin-left: 195px;
}
.software-box-text {
  font-size: 11px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-shadow: 0 0 9px #5cafff;
  color: #ffffff;
  line-height: 1.9;
  text-align: center;
  margin-left: 5.5px;
}
.connect-confirm-box {
  display: inline-flex;
  width: 100%;
  margin-top: 10px;
}
.software-box-contents {
  display: inline-flex;
  width: 449px;
  margin-top: 17px;
}
.software-box-standby {
  display: inline-flex;
  width: 203px;
  height: 40px;
  background-color: #0b4491;
  padding: 8.5px 9px 0 13.1px;;
}
.software-box-active {
  display: inline-flex;
  width: 203px;
  height: 40px;
  background-color: #0b4491;
  padding: 8.5px 11px 0 13.1px;;
}
.box-contents-text {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2;
  letter-spacing: normal;
  text-align: center;
  text-shadow: 0 0 9px #5cafff;
  color: #fff;
}
.active-box-active {
  font-size: 10px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2.28;
  letter-spacing: normal;
  text-align: center;
  color: #1c3482;
  width: 66px;
  height: 23px;
  border-radius: 14px;
  background-color: #87f4f5;
  margin-left: auto;
}
.standby-box-standby {
  font-size: 10px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2.28;
  letter-spacing: normal;
  text-align: center;
  color: #1c3482;
  width: 66px;
  height: 23px;
  border-radius: 14px;
  background-color: #356db9;
  margin-left: auto;
}
.box-connect-img {
  width: 28.2px;
  height: 9.1px;
  background-image: url('../../../assets/performanceMonitoring/connect.png');
  background-size: 100%;
  margin: 15px 7.6px 0 7.2px;
}
.double-connect-img {
  width: 257.1px;
  height: 18.5px;
  background-image: url('../../../assets/performanceMonitoring/double-connect.png');
  background-size: 100%;
  margin:6.9px 0 6.6px 95px;
}
.software-box-active-node {
  width: 203px;
  height: 40px;
  margin-left: 123.4px;
  background-color: #356db9;
  text-shadow: 0 0 9px #5cafff;
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 3.6;
  letter-spacing: normal;
  text-align: center;
  color: #ffffff;
}
.system-monitoring-container {
  margin-top: 16px;
}
</style>