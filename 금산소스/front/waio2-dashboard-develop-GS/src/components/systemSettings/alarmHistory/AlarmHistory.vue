<template>
  <div class="main">
    <!-- 좌측 7일간 알람 이력 -->
    <AlarmHistory7Days/>
    <!-- 우측 전체 알람 이력 -->
    <TotalAlarmRecord/>
    <!--상세 팝업-->
    <CoagulantHistoryDetailPopup v-if="this.$store.state.alarm.popupVisible && this.$store.state.alarm.alarmDetail.procCd.includes('C')"/>
    <MixingHistoryDetailPopup v-if="this.$store.state.alarm.popupVisible && this.$store.state.alarm.alarmDetail.procCd.includes('D')"/>
    <DisinfectionHistoryDetailPopup v-if="this.$store.state.alarm.popupVisible && this.$store.state.alarm.alarmDetail.procCd.includes('G')"/> 
  </div>
</template>

<script>
import AlarmHistory7Days from '@/components/systemSettings/alarmHistory/AlarmHistory7Days';
import TotalAlarmRecord from '@/components/systemSettings/alarmHistory/TotalAlarmRecord';
import CoagulantHistoryDetailPopup from '@/components/systemSettings/alarmHistory/historyDetailPopup/CoagulantHistoryDetailPopup'
import MixingHistoryDetailPopup from '@/components/systemSettings/alarmHistory/historyDetailPopup/MixingHistoryDetailPopup'
import DisinfectionHistoryDetailPopup from '@/components/systemSettings/alarmHistory/historyDetailPopup/DisinfectionHistoryDetailPopup'

export default {
  name: 'AlarmHistory',
  components: {
    AlarmHistory7Days,
    TotalAlarmRecord,
    CoagulantHistoryDetailPopup,
    MixingHistoryDetailPopup,
    DisinfectionHistoryDetailPopup,
  },
  data: () => ({
  }),
  created: function () {
    console.log(this.$options.name + ' created')
  },
  mounted: function () {
    console.log(this.$options.name + ' mounted')
  },
  destroyed: function () {
    console.log(this.$options.name + ' destoryed')
  },
  updated: function () {
    console.log(this.$options.name + ' updated')
  }
}
</script>

<style scoped>
.main {
  padding: 20px 20px 0px 44px;
  display: inline-flex;
  /* width: 100%;
  height: 100%; */
}
</style>