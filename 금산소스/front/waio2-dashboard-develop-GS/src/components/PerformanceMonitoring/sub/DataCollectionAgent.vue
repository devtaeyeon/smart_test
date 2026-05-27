<template>
  <div class="system-software-service">
    <!-- 데이터 수집 에이전트 헤더 컨테이너 -->
    <div class="main-list-header-container">
      <div class="main-list-header-text2">데이터 수집 Agent</div>
    </div>
    <!-- 데이터 수집 에이전트 상태 컨테이너 -->
    <div class="software-service-container">
      <!-- 데이터 수집 에이전트 1번 -->
      <div class="software-service-content1">
        <div class="software-service-box">
          <div class="software-arrow-img"></div>
          <div class="software-box-text">데이터 수집 Agent1</div>
        </div>
        <div class="power-socket" :class="{ on: isConnection1, off: !isConnection1 }"></div>
        <div class="connect-confirm-box">
          <div :class="{ connect: isConnection1, unconnect: !isConnection1 }">connect</div>
          <div :class="{ connect: !isConnection1, unconnect: isConnection1 }">disconnect</div>
        </div>
      </div>
      <!-- 데이터 수집 에이전트 2번 -->
      <div class="software-service-content2">
        <div class="software-service-box">
          <div class="software-arrow-img"></div>
          <div class="software-box-text">데이터 수집 Agent2</div>
        </div>
        <div class="power-socket" :class="{ on: isConnection2, off: !isConnection2 }"></div>
        <div class="connect-confirm-box">
          <div :class="{ connect: isConnection2, unconnect: !isConnection2 }">connect</div>
          <div :class="{ connect: !isConnection2, unconnect: isConnection2 }">disconnect</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DataCollectionAgent',
  computed: {
    // 데이터 수집 에이전트 1번 연결 상태
    isConnection1() {
      if (this.$store.state.performance.monitoringLatest.length > 0) {
        if (this.$store.state.performance.monitoringLatest.filter(it => 
          it.type === this.$store.state.performance.type.DAQ && 
          it.name === 'collector1' &&
          it.value === 'ON').length > 0) {
          return true
        }
      }
      return false
    },
    // 데이터 수집 에이전트 2번 연결 상태
    isConnection2() {
      if (this.$store.state.performance.monitoringLatest.length > 0) {
        if (this.$store.state.performance.monitoringLatest.filter(it => 
          it.type === this.$store.state.performance.type.DAQ && 
          it.name === 'collector2' &&
          it.value === 'ON').length > 0) {
          return true
        }
      }
      return false
    }
  }
}
</script>
<style scoped>
.system-software-service {
  margin: 5px 0px 0px 17px;
  width: 392px;
  height: 226px;
  display: inline-flex;
  flex-flow: column;
  align-items: center
}
.main-list-header-container {
  width: 100%;
  height: 59px;
  display: inline-flex;
  align-items: center;
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
.software-service-container {
  width: 392px;
  height: 167px;
  padding: 9px 22px 0 19px;
  display: inline-flex;
  background-image: url('../../../assets/editableDashboard/software_service_container.png');
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
.software-service-box {
  width: 164px;
  height: 19px;
  background-color: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
}
.software-box-text {
  font-size: 11px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  color: #ffffff;
  text-shadow: 0 0 9px #5cafff;
  text-align: center;
  margin: auto;
}
.software-box-text:before {
  width: 6px;
  height: 7px;
  margin-right: 5px;
  background-image: url("../../../assets/common/icon_right_white_arrow.png");
  background-size: 100%;
  display:inline-block;
  content:'';
}
.power-socket.on {
  margin: 8px auto;
  width: 84px;
  height: 84px;
  background-image: url('../../../assets/performanceMonitoring/power-on.png');
  background-size: 100%;
}
.power-socket.off{
  margin: 8px auto;
  width: 84px;
  height: 84px;
  background-image: url('../../../assets/performanceMonitoring/power-off.png');
  background-size: 100%;
}
.connect-confirm-box {
  display: inline-flex;
  width: 100%;
}
.connect {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2;
  letter-spacing: normal;
  text-align: center;
  color: #87f4f4;
  text-shadow: 0 0 9px #5cafff;
  width: 78px;
  height: 27px;
  border: solid 1px #87f4f4;
}
.unconnect {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2;
  letter-spacing: normal;
  text-align: center;
  color: #517384;
  margin-left: 8px;
  width: 78px;
  height: 27px;
  border: solid 1px #517384;
}
</style>