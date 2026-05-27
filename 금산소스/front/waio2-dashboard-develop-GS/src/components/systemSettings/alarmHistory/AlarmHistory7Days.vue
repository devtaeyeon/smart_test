<template>
  <div class="lately-alarm-history">
    <div class="main-list-header-container">
      <div class="main-list-header-text">제어 방식별 제어 이력 <p>(조회 기간 내)</p></div>
    </div>
    <!-- 하드웨어 알람 컨테이너 -->
    <div class="hw-contents-container">
      <!-- 알람 건수를 감싸는 원 형태의 도트 -->
      <div class="dotted-circle-container" @click="selectIndex('total')">
        <div class="hw-dotted-circle" style="top: 126px;right: 132.5px;"></div>
        <div class="hw-dotted-circle" style="top: 112px;right: 142.3px;"></div>
        <div class="hw-dotted-circle" style="top: 96px; right: 148.3px;"></div>
        <div class="hw-dotted-circle" style="top: 78px; right: 151.5px;"></div>
        <div class="hw-dotted-circle" style="top: 61px;right: 148.3px;"></div>
        <div class="hw-dotted-circle" style="top: 45px;right: 142.3px;"></div>
        <div class="hw-dotted-circle" style="top: 33px;right: 132.5px;"></div>
        <div class="hw-dotted-circle" style="top: 23px;right: 119px;"></div>
        <div class="hw-dotted-circle" style="top: 16px;right: 103.5px;"></div>
        <div class="hw-dotted-circle" style="top: 14px;"></div>
        <div class="hw-dotted-circle" style="top: 16px;right: 68.5px;"></div>
        <div class="hw-dotted-circle" style="top: 23px;right: 54px;"></div>
        <div class="hw-dotted-circle" style="top: 33px;right: 40.5px;"></div>
        <div class="hw-dotted-circle" style="top: 45px;right: 30.5px;"></div>
        <div class="dotted-circle-other-fill" style="top: 61px;right: 24.3px;"></div>
        <div class="dotted-circle-other-fill" style="top: 78px;right: 22.5px;"></div>
        <div class="dotted-circle-other-fill" style="top: 96px;right: 24.3px;"></div>
        <div class="dotted-circle-other-fill" style="top: 112px;right: 30.5px;"></div>
        <div class="dotted-circle-other-fill" style="top: 126px;right: 40.5px;"></div>
        <div class="circle-radius-contents">
          <div class="circle-title-text">전체</div>
          <div class="circle-contents-text">{{ alarmCountTotal }}건</div>
        </div>
      </div>
      <!-- 알람 건수 프로그레스 바 -->
      <div class="contents-progress-box">
        <div class="alarm-progress-title">
          <div class="hw-img"></div>
          <div class="progress-title-text">전체 ({{ alarmCountTotal }}건)</div>
        </div>
        <div v-for="(item, index) in alarmListTotalTop3" :key="index" class="alarm-progress-contents">
          <div v-if="index === 0" class="alarm-one-img"></div>
          <div v-else-if="index === 1" class="alarm-two-img"></div>
          <div v-else class="alarm-three-img"></div>
          <div class="alarm-progress-text">{{ item.process_name }} AI 제어 {{ item.ctr_yn == 'A' ? '(자동)' : item.alm_ty == 2 ? '(반자동)' : '(임계치)' }}</div>         
          <progress class="hw-progress-bar" :value="item.percent" max="100"></progress>
          <div class="hw-progress-percent">{{ item.percent | numFormat('0.[00]')}}%</div>
        </div>
      </div>
    </div>
    <!-- 소프트웨어 알람 컨테이너 -->
    <div class="sw-contents-container">
      <!-- 알람 건수를 감싸는 원 형태의 도트 -->
      <div class="dotted-circle-container" @click="selectIndex('auto_control')">
        <div class="sw-dotted-circle" style="top: 126px;right: 132.5px;"></div>
        <div class="sw-dotted-circle" style="top: 112px;right: 142.3px;"></div>
        <div class="sw-dotted-circle" style="top: 96px; right: 148.3px;"></div>
        <div class="sw-dotted-circle" style="top: 78px; right: 151.5px;"></div>
        <div class="sw-dotted-circle" style="top: 61px;right: 148.3px;"></div>
        <div class="sw-dotted-circle" style="top: 45px;right: 142.3px;"></div>
        <div class="sw-dotted-circle" style="top: 33px;right: 132.5px;"></div>
        <div class="sw-dotted-circle" style="top: 23px;right: 119px;"></div>
        <div class="sw-dotted-circle" style="top: 16px;right: 103.5px;"></div>
        <div class="sw-dotted-circle" style="top: 14px;"></div>
        <div class="sw-dotted-circle" style="top: 16px;right: 68.5px;"></div>
        <div class="sw-dotted-circle" style="top: 23px;right: 54px;"></div>
        <div class="sw-dotted-circle" style="top: 33px;right: 40.5px;"></div>
        <div class="sw-dotted-circle" style="top: 45px;right: 30.5px;"></div>
        <div class="dotted-circle-other-fill" style="top: 61px;right: 24.3px;"></div>
        <div class="dotted-circle-other-fill" style="top: 78px;right: 22.5px;"></div>
        <div class="dotted-circle-other-fill" style="top: 96px;right: 24.3px;"></div>
        <div class="dotted-circle-other-fill" style="top: 112px;right: 30.5px;"></div>
        <div class="dotted-circle-other-fill" style="top: 126px;right: 40.5px;"></div>
        <div class="circle-radius-contents">
          <div class="circle-title-text">자동 제어</div>
          <div class="circle-contents-text">{{ alarmCountAutoControl }}건</div>
        </div>
      </div>
      <!-- 알람 건수 프로그레스 바 -->
      <div class="contents-progress-box">
        <div class="alarm-progress-title">
          <div class="sw-img"></div>
          <div class="progress-title-text">자동 제어 ({{ alarmCountAutoControl }}건)</div>
        </div>
        <div v-for="(item, index) in alarmListAutoControlTop3" :key="index" class="alarm-progress-contents">
          <div v-if="index === 0" class="alarm-one-img"></div>
          <div v-else-if="index === 1" class="alarm-two-img"></div>
          <div v-else class="alarm-three-img"></div>
          <div class="alarm-progress-text">{{ item.process_name }} AI 제어 {{ item.ctr_yn == 'A' ? '(자동)' : item.alm_ty == 2 ? '(반자동)' : '(임계치)' }}</div>
          <progress class="sw-progress-bar" :value="item.percent" max="100"></progress>
          <div class="sw-progress-percent">{{ item.percent | numFormat('0.[00]') }}%</div>
        </div>
      </div>
    </div>
    <!-- 임계치 알람 컨테이너 -->
    <div class="ai-contents-container">
      <!-- 알람 건수를 감싸는 원 형태의 도트 -->
      <div class="dotted-circle-container" @click="selectIndex('user_control')">
        <div class="ai-dotted-circle" style="top: 126px;right: 132.5px;"></div>
        <div class="ai-dotted-circle" style="top: 112px;right: 142.3px;"></div>
        <div class="ai-dotted-circle" style="top: 96px; right: 148.3px;"></div>
        <div class="ai-dotted-circle" style="top: 78px; right: 151.5px;"></div>
        <div class="ai-dotted-circle" style="top: 61px;right: 148.3px;"></div>
        <div class="ai-dotted-circle" style="top: 45px;right: 142.3px;"></div>
        <div class="ai-dotted-circle" style="top: 33px;right: 132.5px;"></div>
        <div class="ai-dotted-circle" style="top: 23px;right: 119px;"></div>
        <div class="ai-dotted-circle" style="top: 16px;right: 103.5px;"></div>
        <div class="ai-dotted-circle" style="top: 14px;"></div>
        <div class="ai-dotted-circle" style="top: 16px;right: 68.5px;"></div>
        <div class="ai-dotted-circle" style="top: 23px;right: 54px;"></div>
        <div class="ai-dotted-circle" style="top: 33px;right: 40.5px;"></div>
        <div class="ai-dotted-circle" style="top: 45px;right: 30.5px;"></div>
        <div class="dotted-circle-other-fill" style="top: 61px;right: 24.3px;"></div>
        <div class="dotted-circle-other-fill" style="top: 78px;right: 22.5px;"></div>
        <div class="dotted-circle-other-fill" style="top: 96px;right: 24.3px;"></div>
        <div class="dotted-circle-other-fill" style="top: 112px;right: 30.5px;"></div>
        <div class="dotted-circle-other-fill" style="top: 126px;right: 40.5px;"></div>
        <div class="circle-radius-contents">
          <div class="circle-title-text">사용자 제어</div>
          <div class="circle-contents-text">{{ alarmCountUserControl }}건</div>
        </div>
      </div>
      <!-- 알람 건수 프로그레스 바 -->
      <div class="contents-progress-box">
        <div class="alarm-progress-title">
          <div class="ai-img"></div>
          <div class="progress-title-text">사용자 제어 ({{ alarmCountUserControl }}건)</div>
        </div>
        <div v-for="(item, index) in alarmListUserControlTop3" :key="index" class="alarm-progress-contents">
          <div v-if="index === 0" class="alarm-one-img"></div>
          <div v-else-if="index === 1" class="alarm-two-img"></div>
          <div v-else class="alarm-three-img"></div>
          <div class="alarm-progress-text">{{ item.process_name }} AI 제어 {{ item.ctr_yn == 'A' ? '(자동)' : item.alm_ty == 2 ? '(반자동)' : '(임계치)' }}</div>
          <progress class="ai-progress-bar" :value="item.percent" max="100"></progress>
          <div class="ai-progress-percent">{{ item.percent | numFormat('0.[00]') }}%</div>
        </div>
      </div>
    </div>
    <!-- 세부 알림 트랜드 차트 컨테이너 -->
    <div class="main-list-header-container">
      <div class="main-list-header-chart">세부 제어 트렌드</div>
    </div>
    <!-- 세부 알림 트랜트 차트 -->
    <AlarmDetailtrand :graphData="this.alarms7DaysFormatted" :selectedIndex="selectedIndex"/>
  </div>
</template>

<script>
import AlarmDetailtrand from '@/components/systemSettings/alarmHistory/AlarmDetailtrand';
export default {
  name: 'AlarmHistory7Days',
  data: () => ({
    selectedIndex: 'total', // 선택된 알람 종류(total, auto_control, user_control)
  }),
  components: {
    AlarmDetailtrand
  },
  computed: {
    //전체 알림 상위 3개 추출
    alarmListTotalTop3() {
      if (this.alarms7DaysFormatted.total.length > 0) {
        let _array = this.alarms7DaysFormatted.total
        _array.sort(function(a, b) {
          return a.count < b.count ? 1 : a.count > b.count ? -1 : 0
        })
        _array.map((it) => {
          it.percent = (it.count / this.alarmCountTotal) * 100
        })
        console.log(_array.slice(0, 3))
        return _array.slice(0, 3)
      }
      return []
    },
    // 자동 제어 알림 상위 3개 추출
    alarmListAutoControlTop3() {
      if (this.alarms7DaysFormatted.auto_control.length > 0) {
        let _array = this.alarms7DaysFormatted.auto_control
        _array.sort(function(a, b) {
          return a.count < b.count ? 1 : a.count > b.count ? -1 : 0
        })
        _array.map((it) => {
          it.percent = (it.count / this.alarmCountAutoControl) * 100
        })
        console.log(_array.slice(0, 3))
        return _array.slice(0, 3)
      }
      return []
    },
    //사용자 제어 알림 상위 3개 추출
    alarmListUserControlTop3() {
      if (this.alarms7DaysFormatted.user_control.length > 0) {
        let _array = this.alarms7DaysFormatted.user_control
        _array.sort(function(a, b) {
          return a.count < b.count ? 1 : a.count > b.count ? -1 : 0
        })
        _array.map((it) => {
          it.percent = (it.count / this.alarmCountUserControl) * 100
        })
        console.log(_array.slice(0, 3))
        return _array.slice(0, 3)
      }
      return []
    },
    // 전체 알림 개수
    alarmCountTotal() {
      let count = 0
      if (this.alarms7DaysFormatted.total.length > 0) {
        this.alarms7DaysFormatted.total.forEach((it) => {
          count += it.count
        })
      }
      return count
    },
    // 자동 제어 알림 개수
    alarmCountAutoControl() {
      let count = 0
      if (this.alarms7DaysFormatted.auto_control.length > 0) {
        this.alarms7DaysFormatted.auto_control.forEach((it) => {
          count += it.count
        })
      }
      return count
    },
    // 사용자 제어 알림 개수
    alarmCountUserControl() {
      let count = 0
      if (this.alarms7DaysFormatted.user_control.length > 0) {
        this.alarms7DaysFormatted.user_control.forEach((it) => {
          count += it.count
        })
      }
      return count
    },
    // 알람 데이터 포멧 형태 변경
    alarms7DaysFormatted () {
      let _it = {}
      _it.auto_control = []
      _it.user_control = []
      _it.total = []
      if (this.$store.state.alarm.alarms7Days.length > 0) {
        this.$store.state.alarm.alarms7Days.forEach((it) => {      
          let selectedAlmId = it.alm_id
          let classification = it.ctr_yn === 'A' ? 'auto_control' : 'user_control'
          it.classification = classification
          if (classification === 'auto_control') {
            let item = _it.auto_control.find((v) => v.alm_id === selectedAlmId)
            if (item !== null && item !== undefined) {
              item.count += 1
            } else {
              _it.auto_control.push({ ...it, count: 1 })
            }
          } 
          else if(classification === 'user_control'){ //user control
            let item = _it.user_control.find((v) => v.alm_id === selectedAlmId)
            if (item !== null && item !== undefined) {
              item.count += 1
            } else {
              _it.user_control.push({ ...it, count: 1 })
            }
          }
          //total
          let item = _it.total.find((v) => v.alm_id === selectedAlmId && v.classification === classification )
          if (item !== null && item !== undefined){
            item.count +=1
          } else{
            _it.total.push({ ...it, count: 1 })
          }

        })

      }
      return _it
    }
  },
  methods: {
    /**
     * 알람 종류를 선택할 때 실행되는 함수
     * 
     * @param index 알람 종류(total, auto_control, user_control)
     */
    selectIndex: function (index) {
      // this.setNewCnt(index)
      this.selectedIndex = index
      this.$store.state.alarm.selectedClassification = index
      this.$store.state.alarm.cfChangeFlag = true
    }
  }
}
</script>

<style scoped lang="scss">
// 최상위 엘리먼트
.lately-alarm-history {
  width: 650px;
  height: 964px;
  flex-flow: column;
  align-items: center;
  display: flex;
  // 헤더 컨테이너
  .main-list-header-container {
    width: 100%;
    height: 59px;
    display: inline-flex;
    align-items: center;
    .main-list-header-text {
      width: 200px;
      height: 65px;
      font-size: 18px;    
      text-align: center;
      text-shadow: 0 0 9px #5cafff;
      background-image: url('../../../assets/editableDashboard/title_under.png');
      color: #ffffff;
      background-position-y: 20px;
    }
    .main-list-header-text p {
      font-size: 14px;
    }
    .main-list-header-chart {
      width: 200px;
      height: 47px;
      font-size: 18px;
      text-align: center;
      text-shadow: 0 0 9px #5cafff;
      background-image: url('../../../assets/editableDashboard/title_under.png');
      color: #ffffff;
    }
  }
  // 하드웨어 컨텐츠 컨테이너
  .hw-contents-container {
    display: inline-flex;
    margin-top: 19.5px;
  }
  // 소프트웨어 컨텐츠 컨테이너
  .sw-contents-container {
    display: inline-flex;
    margin-top: 12px;
  }
  // 인공지능 컨텐츠 컨테이너
  .ai-contents-container {
    display: inline-flex;
    margin: 12px 0 21.3px 0px;
  }
  // 원 토트 컨테이너
  .dotted-circle-container {
    display: flex;
    width: 181.7px;
    height: 152px;
    justify-content: center;
    position: relative;
    // 하드웨어 원 도트
    .hw-dotted-circle {
      width: 8.8px;
      height: 8.8px;
      background-color: #24c5dc;
      border-radius: 100%;
      position: absolute;
    }
    // 소프트웨어 원 도트
    .sw-dotted-circle {
      width: 8.8px;
      height: 8.8px;
      background-color:#c481f5;
      border-radius: 100%;
      position: absolute;
    }
    // 인공지능 원 도트
    .ai-dotted-circle {
      width: 8.8px;
      height: 8.8px;
      background-color:#6ca6f7;
      border-radius: 100%;
      position: absolute;
    }
    // 안 채워져 있는 도트
    .dotted-circle-other-fill {
      width: 8.8px;
      height: 8.8px;
      background-color: rgb(64,96,142);
      border-radius: 100%;
      position: absolute;
    }
    // 알람 건수 표시하는 원 컨텐츠
    .circle-radius-contents {
      display:flex;
      flex-direction: column;
      justify-content: center;
      width: 101.5px;
      height: 101.5px;
      background-color: rgba(36,197,220,0.2);
      border-radius: 100%;
      margin-top: 33.4px;
      text-align: center;
      cursor: pointer;
      // 알람 종류 제목 글자
      .circle-title-text {
        font-size: 16px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        letter-spacing: normal;
        text-shadow: 0 0 9px #5cafff;
        color: #dedede;
      }
      // 알림 건수 글자
      .circle-contents-text {
        font-size: 20px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        letter-spacing: normal;
        text-shadow: 0 0 9px #5cafff;
        color: #ffffff;
      }
    }
  }
  // 알람 종류별 상위 3건
  .contents-progress-box {
    width: 428px;
    height: 152px;
    background-image: url('../../../assets/editableDashboard/alarmhistory_contents_box.png');
    padding: 20px 10px 0 10px;
    // 프로그레스 타이틀(HW, SW, AI)
    .alarm-progress-title {
      display: inline-flex;
      .progress-title-text{
        font-size: 16px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        letter-spacing: normal;
        text-shadow: 0 0 9px #5cafff;
        color: #ffffff;
        line-height: 1.2;
      }
      // 하드웨어 알림 아이콘
      .hw-img {
        width: 15.8px;
        height: 15.8px;
        background-image: url('../../../assets/alarmHistory/hw-icon.png');
        background-repeat: no-repeat;
        background-position: center;
        background-size: 100%;
        margin-right: 5.3px;
      }
      // 소프트웨어 알림 아이콘
      .sw-img {
        width: 14.5px;
        height: 13.1px;
        background-image: url('../../../assets/alarmHistory/sw-icon.png');
        background-repeat: no-repeat;
        background-position: center;
        background-size: 100%;
        margin-right: 5.3px;
      }
      // 인공지능 알림 아이콘
      .ai-img {
        width: 13.9px;
        height: 15.3px;
        background-image: url('~@/assets/alarmHistory/ai-icon.png');
        background-repeat: no-repeat;
        background-position: center;
        background-size: 100%;
        margin-right: 5.3px;
      }
    }
    // 프로그레스
    progress {
      margin-left: auto;
      -webkit-appearance: none;
      -moz-appearance: none;
      appearance: none;
      display: inline-flex;
      width: 142px;
      height: 8px;
      border-radius: 8px;
      // 하드웨어 진행도
      &.hw-progress-bar::-moz-progress-bar {
        border-radius: 100px;
        background: linear-gradient(to right, rgba(31,234,132,1), rgba(9,41,222, 1));
      }
      // 하드웨어 전체 진행도 배경
      &.hw-progress-bar::-webkit-progress-bar {
        background: #7a97c1;
        border-radius: 100px;
      }
      &.hw-progress-bar::-webkit-progress-value {
        border-radius: 100px;
        background: linear-gradient(to right, rgba(31,234,132,1), rgba(9,41,222, 1));
      }
      // 인공지능 진행도
      &.sw-progress-bar::-moz-progress-bar {
        border-radius: 100px;
        background: linear-gradient(to right, rgba(52,165,247,1), rgba(157,63,206, 1));
      }
      // 인공지능 전체 진행도 배경
      &.sw-progress-bar::-webkit-progress-bar {
        background: #7a97c1;
        border-radius: 100px;
      }

      &.sw-progress-bar::-webkit-progress-value {
        border-radius: 100px;
        background: linear-gradient(to right, rgba(52,165,247,1), rgba(157,63,206, 1));
      }
      // 인공지능 진행도
      &.ai-progress-bar::-moz-progress-bar {
        border-radius: 100px;
        background: linear-gradient(to right, rgba(250,228,136,1), rgba(43,123,250, 1));
      }
      // 인공지능 전체 진행도 배경
      &.ai-progress-bar::-webkit-progress-bar {
        background: #7a97c1;
        border-radius: 100px;
      }

      &.ai-progress-bar::-webkit-progress-value {
        border-radius: 100px;
        background: linear-gradient(to right, rgba(250,228,136,1), rgba(43,123,250, 1));
      }
    }
    

    
  }
  
}



.alarm-progress-contents {
  margin-top: 7px;
  font-size: 11px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  color: #ffffff;
  display: flex;
  align-items: center;
}
.alarm-progress-text {
  margin-left: 8.6px;
}
.hw-progress-percent {
  font-family:"LAB디지털" !important;
  color: #25dde5;
  text-align: right;
  font-size: 17px;
  margin-left: 5px;
  width: 46px;
}
.sw-progress-percent {
  font-family:"LAB디지털" !important;
  color: #d293ff;
  text-align: right;
  font-size: 17px;
  margin-left: 11px;
  width: 46px;
}
.ai-progress-percent {
  font-family:"LAB디지털" !important;
  color: #88b7fc;
  text-align: right;
  font-size: 17px;
  margin-left: 11px;
  width: 46px;
}
.alarm-one-img {
  background-image: url('../../../assets/alarmHistory/alarm_title_one.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100%;
  width: 16px;
  height: 16px;
}
.alarm-two-img {
  background-image: url('../../../assets/alarmHistory/alarm_title_two.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100%;
  width: 16px;
  height: 16px;
}
.alarm-three-img {
  background-image: url('../../../assets/alarmHistory/alarm_title_three.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100%;
  width: 16px;
  height: 16px;
}
</style>