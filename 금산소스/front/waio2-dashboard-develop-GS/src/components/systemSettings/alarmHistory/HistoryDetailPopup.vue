<template>
    <div v-if="this.$store.state.alarm.popupVisible" class="popup-wrap">
      <div class="popup-main">
        <div class="popup-contents">
          <div class="top">
            <div class="top__img"></div>
            <div class="top__title"> {{ this.$store.state.alarm.selectedProcessName }} </div>
            <div class="top__exit-btn" @click="closePopup()"></div>
          </div>
          <div class="chart-container">
            <div class="table-contents-wrap" :style=" { maxHeight: containerHeight + 'px', overflowY: containerHeight > 500 ? 'auto' : 'hidden' }">
              <table class="table-detail">
                <colgroup>
                  <col style="width: 20%;">
                  <col style="width: 28%;">
                  <col style="width: 12%;">
                  <col style="width: 12%;">
                </colgroup>
                <thead class="table-title">
                  <th>태그값</th>
                  <th>태그설명</th>
                  <th>이전값</th>
                  <th>제어값</th>
                </thead>
                <tbody>
                  <tr v-for="ctr in this.$store.state.alarm.ctrListByAlm" :key="ctr.tag_sn" class="table-contents">
                    <td>{{ ctr.tag_sn }}</td>
                    <td>{{ ctr.tag_dp }}</td>
                    <td>{{ ctr.tag_cmp_val }}</td>
                    <td>{{ ctr.tag_val }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>
<script>
export default {
  name: 'HistoryDetailPopup',
  data() {
    return {
      containerHeight: 500 // 팝업 높이 설정
    }
  },
  computed: {
  },
  methods: {
    closePopup(){
      this.$store.state.alarm.popupVisible = false;
    },
    updateContainerHeight() {
      const contentHeight = /* 여기에서 컨텐츠의 높이를 가져오는 로직 */
      this.containerHeight = Math.min(contentHeight, 500); // 최대 500px로 제한
    },
    mounted() {
    // 페이지가 로드되거나 컨텐츠가 업데이트될 때마다 호출
    this.updateContainerHeight();
    },
  },
  created: function () {
  },
  destroyed: function () {
    console.log(this.$options.name + ' destoryed')
  },
  updated: function () {
    console.log(this.$options.name + ' updated')
  }
}
</script>
<style lang="scss" scoped>
  *::-webkit-scrollbar {
    width: 5px;
  }
  *::-webkit-scrollbar-track {
    background-color: #011527;
    border-radius: 2.5px;
  }
  *::-webkit-scrollbar-thumb {
    background-color: #417db9;
    border-radius: 2.5px;
  }
.popup-wrap  {
  position: absolute;
  top: -115px;
  left: 0;
  z-index: 200;
  width: 100%;
  height: 100%;
  min-height: 1156px;
  background-color: rgba(30,37,61,0.8);
  .popup-main {
    position: absolute;
    width: 800px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background-image: url('../../../assets/sedimentation/popup_main.png');
    background-size: 100% 100%;
    .popup-contents{
      padding: 30px;
      height: 100%;
      .chart-container {
        width: 100%;
        margin: 20px 0;
        color: #b4dffb;
        font-size: 14px;
        .table-contents-wrap {
          overflow-y: auto !important;
        }
        .table-detail {
          border-collapse: collapse;
          border-spacing: 0;
          width: 100%;
        }
        .table-title {
          width: 100%;
          color: #fff;
          height: 45px;
          background-image: linear-gradient(to right, #11316b 0%, rgba(39, 86, 162,1) 15%, rgba(39, 86, 162,1) 85%, #11316b 100%);
          position: sticky;
          top: 0;
        }
        td {
          text-align: center;
          height: 45px;
        }
        .table-contents:nth-child(odd) {
          background-image: linear-gradient(90deg,rgba(9,76,181,0) 3%,rgba(9,76,181,.15) 21%,rgba(9,76,181,.15) 82%,rgba(9,76,181,0) 100%);
        }
        .table-contents:nth-child(even) {
          background-image: linear-gradient(90deg,rgba(66,144,221,0),rgba(66,144,221,.15) 16%,rgba(66,144,221,.15) 87%,rgba(66,144,221,0));
        }
      }
      .top{
        display: flex;
        width: 100%;
        height: 30px;
        margin-top: 20px;
        &__img{
          width: 19px;
          height: 30px;
          background-image: url('../../../assets/sedimentation/top_title_img.png');
        }
        &__title{
          margin-left: 10px;
          font-size: 24px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 1.5;
          letter-spacing: normal;
          text-align: left;
          color: #b4dffb;
        }
        &__exit-btn{
          margin-left: auto;
          width: 24px;
          height: 30px;
          background-image: url('../../../assets/sedimentation/exit_btn.png');
          background-position-y: center;
          cursor: pointer;
          z-index: 9;
        }
      }
    }
  }
}
</style>