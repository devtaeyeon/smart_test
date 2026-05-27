<template>
  <div class="dashboard-container">
    <!-- 좌측 상단의 원수탁도, 정수탁도, 원수유입유량, 정수유출유량 컴포넌트-->
    <MainFactor/>
    <!-- 좌측 상단의 뇌 이미지 컴포넌트 -->
    <BrainImage/>
    <!-- 우측 자율운영 정보 컴포넌트 -->
    <DashboardInfo/>
    <!-- 라인(빨강, 파랑) & 물 흐름 컴포넌트 -->
    <WaterFlow/>
    
    <!-- 공정별 건물 배치 -->
    <div class="map-contents">
      <!-- 탈수기동 건물-->
      <!-- <div class="one-building"></div> -->
      <!-- 농축조 건물-->
      <!-- <div class="two-building"></div> -->
      <!-- 정수지 건물 -->
      <!-- <div class="four-building"></div> -->
      <!-- 관리동 건물 -->
      <!-- <div class="five-building"></div> -->

      <!-- 착수 건물 -->
      <div class="map-contents__splashdown">
        <div class="splashdown-text">착수</div>
        <!-- <div class="splashdown-text" @mouseover="onBuildingMouseover(1, 1)" @mouseout="this.onBuildingMouseout" @click="$routingByIndex(1)">착수</div> -->
        <img style="opacity: 0.5;" class="down" :class="[this.$store.state.selectedBuildingIndex === 1 ? 'building-unvisible': 'building-visible']" src="../../assets/ss_images/ss_box_01.png" usemap="#splashdown_map" alt="착수건물"/>
        <!-- <img class="up" :class="[this.$store.state.selectedBuildingIndex === 1 ? 'building-visible': 'building-unvisible']" src="../../assets/ss_images/ss_box_01_up.png" usemap="#over_splashdown_map"/>
        <img class="aurora_splashdown" :class="[this.$store.state.selectedBuildingIndex === 1 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_icons/bottom-aurora.png"/> -->
      </div>
      <!-- 착수 회전 아이콘 -->
      <!-- <div class="cube splashdown_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
          <div :class="[this.$store.getters['receiving/isAiOperationMode'] ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
          <div class="cube__back_logo back_splashdown"></div>
          </div>
        </div>
      </div> -->

      <!-- 약품 건물 -->
      <div class="map-contents__drug" :class="[this.$store.state.selectedBuildingIndex === 2 ? 'zIndex10': '', this.$store.state.selectedBuildingIndex > 0 && this.$store.state.selectedBuildingIndex !== 2? 'opacity50' : '']">
        <div class="drug-text" @mouseover="onBuildingMouseover(2)" @mouseout="this.onBuildingMouseout" @click="$routingByIndex(2)">약품</div>
        <img class="down" :class="[this.$store.state.selectedBuildingIndex === 2 ? 'building-unvisible': 'building-visible']" src="../../assets/ss_images/ss_box_02.png" usemap="#drug_map" alt="약품건물"/>
        <img class="up" :class="[this.$store.state.selectedBuildingIndex === 2 ? 'building-visible': 'building-unvisible']" src="../../assets/ss_images/ss_box_02_up.png" usemap="#over_drug_map" alt="약품건물"/>
        <img class="aurora_drug" :class="[this.$store.state.selectedBuildingIndex === 2? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_icons/bottom-aurora.png" alt="약품텍스트빛효과"/>
      </div>
      <!-- 약품 회전 아이콘 -->
      <div class="cube drug_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
          <div :class="[this.$store.state.coagulants.latest.ai_opr ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
          <div class="cube__back_logo back_drug"></div>
          </div>
        </div>
      </div>
      
      <!-- 혼화응집 건물 -->
      <div class="map-contents__sedimentation-mix" :class="[this.$store.state.selectedBuildingIndex === 3 || this.$store.state.selectedBuildingIndex === 4 ? 'zIndex10': '', this.$store.state.selectedBuildingIndex > 0 && this.$store.state.selectedBuildingIndex !== 3 && this.$store.state.selectedBuildingIndex !== 4 ? 'opacity50' : '']">
        <!-- <div class="sedimentation-text" :class="[this.$store.state.selectedBuildingIndex === 3 ? 'opacity50': '']" @mouseover="onBuildingMouseover(4)" @mouseout="this.onBuildingMouseout" @click="$routingByIndex(4)">침전</div> -->
        <div class="mix-text" :class="[this.$store.state.selectedBuildingIndex === 4 ? 'opacity50': '']" @mouseover="onBuildingMouseover(3)" @mouseout="this.onBuildingMouseout" @click="$routingByIndex(3)">혼화응집</div>
        <img class="down" :class="[this.$store.state.selectedBuildingIndex === 3 || this.$store.state.selectedBuildingIndex === 4 ? 'building-unvisible': 'building-visible']" src="../../assets/ss_images/ss_box_03.png" alt="혼화응집건물"/>
        <img class="up" :class="[this.$store.state.selectedBuildingIndex === 3 || this.$store.state.selectedBuildingIndex === 4 ? 'building-visible': 'building-unvisible']" src="../../assets/ss_images/ss_box_03_up.png" alt="혼화응집건물"/>
        <!-- <img class="aurora_sedimentation" :class="[this.$store.state.selectedBuildingIndex === 4 ? 'building-unvisible': 'building-visible', this.$store.state.selectedBuildingIndex === 4 ? 'opacity0' : '']" src="../../assets/dashboard_icons/bottom-aurora.png"/> -->
        <img class="aurora_mix" :class="[this.$store.state.selectedBuildingIndex === 3 ? 'building-unvisible': 'building-visible', this.$store.state.selectedBuildingIndex === 3 ? 'opacity0' : '']" src="../../assets/dashboard_icons/bottom-aurora.png" alt="혼화응집텍스트빛효과"/>
      </div>
      <!--  침전 회전 아이콘 -->
      <!-- <div class="cube sedimentation_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
          <div :class="[this.$store.state.sedimentation.latest.ai_opr ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
          <div class="cube__back_logo back_sedimentation"></div>
          </div>
        </div>
      </div> -->
      <!--  혼화응집 회전 아이콘 -->
      <div class="cube mix_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
          <div :class="[this.$store.state.mixing.latest.ai_opr ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
          <div class="cube__back_logo back_mix"></div>
          </div>
        </div>
      </div> 

      <!-- 정수지 건물 -->
      <div class="map-contents__percolation">
        <!-- <div class="percolation-text" @mouseover="onBuildingMouseover(5)" @mouseout="this.onBuildingMouseout" @click="$routingByIndex(5)">여과</div> -->
        <div class="percolation-text">정수지</div>
        <img style="opacity: 0.5;" class="down" :class="[this.$store.state.selectedBuildingIndex === 5 ? 'building-unvisible': 'building-visible']" src="../../assets/ss_images/ss_box_05.png" usemap="#percolation_map" alt="여과건물"/>
        <!-- <img class="up" :class="[this.$store.state.selectedBuildingIndex === 5 ? 'building-visible': 'building-unvisible']" src="../../assets/ss_images/ss_box_05_up.png" usemap="#percolation_over_map"/>
        <img class="aurora_percolation" :class="[this.$store.state.selectedBuildingIndex === 5 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_icons/bottom-aurora.png"/> -->
      </div>
      <!-- 여과 회전 아이콘 -->
      <!-- <div class="cube percolation_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
          <div :class="[this.$store.state.filter.latest.ai_opr ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
          <div class="cube__back_logo back_percolation"></div>
          </div>
        </div>
      </div> -->

      <!-- 소독 건물 -->
      <div class="map-contents__disinfection" :class="[this.$store.state.selectedBuildingIndex === 7.1 ? 'zIndex10': '', this.$store.state.selectedBuildingIndex > 0 && this.$store.state.selectedBuildingIndex !== 7.1 ? 'opacity50' : '']">
        <div class="disinfection-text" @mouseover="onBuildingMouseover(7.1)" @mouseout="this.onBuildingMouseout" @click="$routingByIndex(7.1)">소독</div>
        <img class="down" :class="[this.$store.state.selectedBuildingIndex === 7.1 ? 'building-unvisible': 'building-visible']" src="../../assets/ss_images/ss_box_06.png" usemap="#disinfection_map" alt="소독건물"/>
        <img class="up" :class="[this.$store.state.selectedBuildingIndex === 7.1 ? 'building-visible': 'building-unvisible']" src="../../assets/ss_images/ss_box_06_up.png" usemap="#over_disinfection_map" alt="소독건물"/>
        <img class="aurora_disinfection" :class="[this.$store.state.selectedBuildingIndex === 7.1 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_icons/bottom-aurora.png" alt="소독텍스트빛효과"/>
      </div>
      <!-- 소독 회전 아이콘 -->
      <div class="cube disinfection_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
          <div :class="[this.$store.getters['disinfection/isAiOperationMode'] ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
          <div class="cube__back_logo back_disinfection"></div>
          </div>
        </div>
      </div>

      <!-- 펌프 건물 -->
      <!-- <div class="map-contents__pump" :class="[this.$store.state.selectedBuildingIndex === 8 ? 'zIndex10': '', this.$store.state.selectedBuildingIndex > 0 && this.$store.state.selectedBuildingIndex !== 8 ? 'opacity50' : '']">
        <div class="pump-text" @mouseover="onBuildingMouseover(8)" @mouseout="this.onBuildingMouseout" @click="onClickSending">송수</div>
        <img class="down" :class="[this.$store.state.selectedBuildingIndex === 8 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_img/pump.png"/>
        <img class="up" :class="[this.$store.state.selectedBuildingIndex === 8 ? 'building-visible': 'building-unvisible']" src="../../assets/dashboard_img/over_pump.png"/>
        <img class="aurora_pump" :class="[this.$store.state.selectedBuildingIndex === 8 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_icons/bottom-aurora.png"/>
      </div> -->
      <!-- 펌프 회전 아이콘 -->
      <!-- <div class="cube pump_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
            <div :class="[this.$store.state.isSelectedMainMenuIndex7AiOn ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
            <div class="cube__back_logo back_pump"></div>
          </div>
        </div>
      </div> -->
      <!-- 탈수기동 건물 -->
      <!-- <div class="map-contents__concentration" :class="[this.$store.state.selectedBuildingIndex === 9 ? 'zIndex10': '', this.$store.state.selectedBuildingIndex > 0 && this.$store.state.selectedBuildingIndex !== 9 ? 'opacity50' : '']">
        <div class="concentration-text" @mouseover="onBuildingMouseover(9)" @mouseout="this.onBuildingMouseout">탈수기동</div>
        <img class="down" :class="[this.$store.state.selectedBuildingIndex === 9 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_img/concentration.png"/>
        <img class="up" :class="[this.$store.state.selectedBuildingIndex === 9 ? 'building-visible': 'building-unvisible']" src="../../assets/dashboard_img/over_concentration.png"/>
        <img class="aurora_concentration" :class="[this.$store.state.selectedBuildingIndex === 9 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_icons/bottom-aurora.png"/>
      </div> -->
      <!-- 탈수기동 아이콘 -->
      <!-- <div class="cube concentration_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
            <div :class="[this.$store.state.isSelectedMainMenuIndex9AiOn ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
            <div class="cube__back_logo back_concentration"></div>
          </div>
        </div>
      </div> -->

      <!-- 농축조 건물 -->
      <!-- <div class="map-contents__dehydration" :class="[this.$store.state.selectedBuildingIndex === 10 ? 'zIndex10': '', this.$store.state.selectedBuildingIndex > 0 && this.$store.state.selectedBuildingIndex !== 10 ? 'opacity50' : '']">
        <div class="dehydration-text" @mouseover="onBuildingMouseover(10)" @mouseout="this.onBuildingMouseout">농축조</div>
        <img class="down" :class="[this.$store.state.selectedBuildingIndex === 10 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_img/dehydration.png"/>
        <img class="up" :class="[this.$store.state.selectedBuildingIndex === 10 ? 'building-visible': 'building-unvisible']" src="../../assets/dashboard_img/over_dehydration.png"/>
        <img class="aurora_dehydration" :class="[this.$store.state.selectedBuildingIndex === 10 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_icons/bottom-aurora.png"/>
      </div> -->
      <!-- 농축조 아이콘 -->
      <!-- <div class="cube dehydration_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
            <div :class="[this.$store.state.isSelectedMainMenuIndex9AiOn ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
            <div class="cube__back_logo back_dehydration"></div>
          </div>
        </div>
      </div>  -->

      <!-- 오존 건물 -->
      <!-- <div class="map-contents__o3" :class="[this.$store.state.selectedBuildingIndex === 11 ? 'zIndex10': '', this.$store.state.selectedBuildingIndex > 0 && this.$store.state.selectedBuildingIndex !== 11 ? 'opacity50' : '']">
        <div class="o3-text" @mouseover="onBuildingMouseover(11)" @mouseout="this.onBuildingMouseout" @click="$routingByIndex(11)">오존</div>
        <img class="down" :class="[this.$store.state.selectedBuildingIndex === 11 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_img/o3.png"/>
        <img class="up" :class="[this.$store.state.selectedBuildingIndex === 11 ? 'building-visible': 'building-unvisible']" src="../../assets/dashboard_img/over_o3.png"/>
        <img class="aurora_o3" :class="[this.$store.state.selectedBuildingIndex === 11 ? 'building-unvisible': 'building-visible']" src="../../assets/dashboard_icons/bottom-aurora.png"/>
      </div> -->
      <!-- 오존 회전 아이콘 -->
      <!-- <div class="cube o3_cube" :class="[this.$store.state.selectedBuildingIndex > 0 ? 'building-unvisible': 'building-visible']">
        <div class="cube__inner">
          <div class="cube__noimg">
            <div :class="[this.$store.state.ozone.latest.ai_opr ? 'cube__front_logo--on' : 'cube__front_logo--off']"></div>
            <div class="cube__back_logo back_o3"></div>
          </div>
        </div>
      </div> -->

    </div>
    <!-- 착수 팝업 -->
    <Popup1 v-if="this.$store.state.selectedBuildingIndex === 1"/>
    <!-- 약품 팝업 -->
    <Popup2 v-if="this.$store.state.selectedBuildingIndex === 2"/>
    <!-- 혼화응집 팝업 -->
    <Popup3 v-if="this.$store.state.selectedBuildingIndex === 3"/>
    <!-- 침전 팝업 -->
    <Popup4 v-if="this.$store.state.selectedBuildingIndex === 4"/>
    <!-- 여과 팝업 -->
    <Popup5 v-if="this.$store.state.selectedBuildingIndex === 5"/>
    <!-- GAC여과 팝업 -->
    <Popup6 v-if="this.$store.state.selectedBuildingIndex === 6"/>
    <!-- 소독 팝업 -->
    <Popup7 v-if="this.$store.state.selectedBuildingIndex === 7.1 || this.$store.state.selectedBuildingIndex === 7.3"/>
    <!-- 송수 팝업 -->
    <Popup8 v-if="this.$store.state.selectedBuildingIndex === 8"/>
    <!-- 탈수기동 팝업 -->
    <Popup9 v-if="this.$store.state.selectedBuildingIndex === 9"/>
    <!-- 농축조 팝업 -->
    <Popup10 v-if="this.$store.state.selectedBuildingIndex === 10"/>
    <!-- 오존 팝업 -->
    <Popup11 v-if="this.$store.state.selectedBuildingIndex === 11"/>
    <!-- 운영 이력 팝업-->
    <AIOprHistoryPopup/>
  </div>
</template>

<script>
import MainFactor from '@/components/dashboard/MainFactor'
import DashboardInfo from '@/components/dashboard/DashboardInfo'
import WaterFlow from '@/components/dashboard/WaterFlow'
import BrainImage from '@/components/dashboard/BrainImage'
import Popup1 from '@/components/dashboard/popup/Popup1'
import Popup2 from '@/components/dashboard/popup/Popup2'
import Popup3 from '@/components/dashboard/popup/Popup3'
import Popup4 from '@/components/dashboard/popup/Popup4'
import Popup5 from '@/components/dashboard/popup/Popup5'
import Popup6 from '@/components/dashboard/popup/Popup6'
import Popup7 from '@/components/dashboard/popup/Popup7'
import Popup8 from '@/components/dashboard/popup/Popup8'
import Popup9 from '@/components/dashboard/popup/Popup9'
import Popup10 from '@/components/dashboard/popup/Popup10'
import Popup11 from '@/components/dashboard/popup/Popup11'
import AIOprHistoryPopup from '@/components/dashboard/popup/AIOprHistoryPopup'
import { SET_OVERLAY } from '@/store'
import { GET_COAGULANT_LATEST } from '@/store/modules/coagulants'
import { GET_MIXING_LATEST } from '@/store/modules/mixing'
// import { GET_GAC_LATEST } from '@/store/modules/gac'
import { GET_DISINFECTION_LATEST } from '@/store/modules/disinfection'
import { GET_AI_OPR_HISTORY_TOTAL } from '@/store/modules/aioprhistory'
import { SERVICE_URL } from '@/store'
import { GET_EMS_LATEST } from '@/store/modules/ems'
// import { GET_OZONE_LATEST } from '@/store/modules/ozone'
// import { GET_PMS_LATEST } from '@/store/modules/pms'

export default {
  name: 'Dashboard',
  data: () => ({
  }),
  // Dashboard에서 이용할 Component 정의
  components: {
    MainFactor,
    DashboardInfo,
    WaterFlow,
    Popup1,
    Popup2,
    Popup3,
    Popup4,
    Popup5,
    Popup6,
    Popup7,
    Popup8,
    Popup9,
    Popup10,
    Popup11,
    BrainImage,
    AIOprHistoryPopup
  },
  // Dashboard.vue에서 이용할 함수 정의
  methods:{
    /**
     * EMS 페이지로 이동하는 함수
     * '_self' 옵션으로 새롭게 창을 띄우지 않고 이동
     */
    onClickSending: function() {
      window.open(SERVICE_URL.EMS + '/analysis', "_self")
    },
    /**
     * 선택한 공정 페이지로 이동하는 함수
     * Vue Router를 통해 선택한 공정 페이지로 이동
     *
     * @param index 선택한 공정 index
     */
    onBuildingMouseClick: function(index) {
      this.$routingByIndex(index)
    },
    /**
     * 공정 건물에 마우스를 올렸을 때 발생하는 이벤트 함수
     * 선택된 공정 건물로 상태값을 변경
     *
     * @param index 선택한 공정 index
     */
    onBuildingMouseover: function (index , receivingIndex) {
      this.$store.state.selectedBuildingIndex = index
      if(receivingIndex !== undefined){
        this.$store.state.receiving.processStep = 1
      }
    },
    /**
     * 공정 건물에 마우스가 벗어났을 때 발생하는 이벤트 함수
     * 선택된 공정 건물 상태 값을 없음(0)으로 변경
     */
    onBuildingMouseout: function () {
      this.$store.state.selectedBuildingIndex = 0
    },
  },
  /**
   * Dashboard.vue가 마운트 됐을 때 실행되는 함수
   * 1분 간격으로 API 호출하는 interval 등록
   * 호출 전 로딩바 생성 / 호출 후 로딩바 제거
   */
  mounted: function() {
    this.$store.commit(SET_OVERLAY, true)
    Promise.all([
      // this.$store.dispatch(GET_LIV_RECEIVING_LATEST),
      this.$store.dispatch(GET_COAGULANT_LATEST),
      this.$store.dispatch(GET_MIXING_LATEST),
      // this.$store.dispatch(GET_SEDIMENTATION_LATEST),
      // this.$store.dispatch(GET_FILTER_LATEST),
      // this.$store.dispatch(GET_OZONE_LATEST),
      // this.$store.dispatch(GET_GAC_LATEST),
      this.$store.dispatch(GET_DISINFECTION_LATEST),
      this.$store.dispatch(GET_AI_OPR_HISTORY_TOTAL),
      this.$store.dispatch(GET_EMS_LATEST)
      // this.$store.dispatch(GET_PMS_LATEST)
    ]).finally(() => {
      this.$store.commit(SET_OVERLAY, false)
    })
    
    this.timer = setInterval(() => {
      // this.$store.dispatch(GET_LIV_RECEIVING_LATEST),
      this.$store.dispatch(GET_COAGULANT_LATEST),
      this.$store.dispatch(GET_MIXING_LATEST),
      // this.$store.dispatch(GET_SEDIMENTATION_LATEST),
      // this.$store.dispatch(GET_FILTER_LATEST),
      // this.$store.dispatch(GET_OZONE_LATEST),
      // this.$store.dispatch(GET_GAC_LATEST),
      this.$store.dispatch(GET_DISINFECTION_LATEST),
      this.$store.dispatch(GET_AI_OPR_HISTORY_TOTAL),
      this.$store.dispatch(GET_EMS_LATEST)
      // this.$store.dispatch(GET_PMS_LATEST)
    }, 60 * 1000)
  },
  /**
   * Dashboard.vue가 제거될 때 실행되는 함수
   * 마운트에서 등록해 놓은 API 호출 interval 제거
   */
  destroyed: function () {
    console.log(this.$options.name + ' destoryed')
    clearInterval(this.timer)
  }
}
</script>

<style scoped lang="scss">
// 공정 건물 안 보이도록 함
.building-unvisible {
  display: none !important;
}
// 공정 건물을 보이도록 함
.building-visible {
  display: block !important;
}
// 대시보드 사이즈 및 배경
.dashboard-container {
  background-image: url('../../assets/gs_images/main_bg.png');
  background-size: cover;
  width: 100%;
  height: 100%;
  // min-height: 990px;
  overflow-y: auto;
}
// 레이어 10층
.zindex10 {
  z-index: 10;
}
// 불투명도 0
.opacity0 {
  opacity: 0 !important;
}
// 불투명도 50%
.opacity50 {
  opacity: 0.5 !important;
}
// 불투명도 100%
.opacity100 {
  opacity: 1 !important;
}
// 대시보드 건물 레이아웃
.map-contents {
  position: absolute;
  top: 226.5px;
  width: 1509px;
  height: 763px;
  img{
    position: absolute;
  }
  // 탈수기동 건물
  // .one-building{
  //   position: absolute;
  //   top: 252px;
  //   left: 50px;
  //   width: 196px;
  //   height: 129px;
  //   background-image: url('../../assets/dashboard_img/one_building.png');
  // }
  // 농축조 건물
  // .two-building{
  //   position: absolute;
  //   top: 172px;
  //   left: 319px;
  //   width: 227px;
  //   height: 128px;
  //   background-image: url('../../assets/dashboard_img/two_building.png');
  // }
  // 정수지 건물
  // .four-building{
  //   position: absolute;
  //   top: 339px;
  //   left: 386px;
  //   width: 487px;
  //   height: 302px;
  //   background-image: url('../../assets/dashboard_img/four_building.png');
  // }
  // 관리동 건물
  .five-building{
    position: absolute;
    top: 260px;
    left: 890px;
    width: 236px;
    height: 178px;
    background-image: url('../../assets/dashboard_img/five_building.png');
  }
  // 착수 건물
  &__splashdown{
    z-index: 3;
    position: absolute;
    left: 1176px;
    top: -108px;
    width: 205px;
    height: 157px;
    clip-path: polygon(0 -100%, 200% 0, 100% 76%, 58% 102%, 0 70%);
    // 마우스 오버시 건물 Up
    .up{
      position: absolute;
      top: -61px;
      // left: -30px;
      animation: splashdown-up 1s ease-in-out 0s normal;
    }
    // 마우스 오버시 건물 Up keyframes
    @keyframes splashdown-up {
      0% {
        transform: translateY(60px);
      }
      100% {
        transform: translateY(0);
      }
    }
    // 아이콘 하단에 반짝이는 빛
    .aurora_splashdown{
      pointer-events:none;
      top: -29px;
      left: 43px;
      animation: blink 3s infinite alternate;
    }
    // 착수 텍스트
    .splashdown-text {
      position: absolute;
      left: -19px;
      top: 45px;
      width: 250px;
      height: 37px;
      opacity: 0.8;
      background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
      text-shadow: 0 0 9px #5cafff;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      z-index: 10;
    }
  }
  // 약품 건물
  &__drug{
    z-index: 2;
    position: absolute;
    left: 989px;
    top: -9px;
    width: 205px;
    height: 166px;
    clip-path: polygon(0 -100%, 100% 0, 100% 77%, 58% 100%, 0 69%);
    .up{
      position: absolute;
      top: -51px;
      // left: -28px;
      animation: drug-up 1s ease-in-out 0s normal;
    }
    @keyframes drug-up {
      0% {
        transform: translateY(50px);
      }
      100% {
        transform: translateY(0);
      }
    }
    .aurora_drug{
      pointer-events:none;
      top: 11px;
      left: 47px;
      animation: blink 3s infinite alternate;
    }
    .drug-text {
      position: absolute;
      left: -19px;
      top: 74px;
      width: 250px;
      height: 37px;
      opacity: 0.8;
      background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
      text-shadow: 0 0 9px #5cafff;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      z-index: 10;
      cursor: pointer;
    }
  }
  // 공업약품 건물
  &__drug2{
    z-index: 2;
    position: absolute;
    left: 1082px;
    top: 32px;
    width: 205px;
    height: 166px;
    clip-path: polygon(0 -100%, 100% 0, 100% 77%, 58% 102%, 0 71%);
    .up{
      position: absolute;
      top: -51px;
      // left: -28px;
      animation: drug-up 1s ease-in-out 0s normal;
    }
    @keyframes drug-up {
      0% {
        transform: translateY(50px);
      }
      100% {
        transform: translateY(0);
      }
    }
    .aurora_drug2{
      pointer-events:none;
      top: 11px;
      left: 47px;
      animation: blink 3s infinite alternate;
    }
    .drug2-text {
      position: absolute;
      left: -19px;
      top: 74px;
      width: 250px;
      height: 37px;
      opacity: 0.8;
      background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
      text-shadow: 0 0 9px #5cafff;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      z-index: 10;
      cursor: pointer;
    }
  }
  // 침전&혼화응집 건물
  &__sedimentation-mix{
    z-index: 1;
    position: absolute;
    left: 542px;
    top: 91px;
    width: 533px;
    height: 291px;
    clip-path: polygon(0 -100%, 100% 0, 100% 54%, 42% 101%, 0 63%);
    .up{
      position: absolute;
      top: -69px;
      // left: -30px;
      animation: sedimentation-mix-up 1s ease-in-out 0s normal;
    }
    @keyframes sedimentation-mix-up {
      0% {
        transform: translateY(68px);
      }
      100% {
        transform: translateY(0);
      }
    }
    .aurora_sedimentation{
      pointer-events:none;
      top: 45px;
      left: 172px;
      animation: blink 3s infinite alternate;
    }
    .aurora_mix{
      pointer-events:none;
      top: 1px;
      left: 285px;
      animation: blink 3s infinite alternate;
    }
    .sedimentation-text {
      position: absolute;
      left: 99px;
      top: 113px;
      width: 250px;
      height: 37px;
      opacity: 0.8;
      background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
      text-shadow: 0 0 9px #5cafff;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      z-index: 10;
      cursor: pointer;
    }
    .mix-text {
      position: absolute;
      left: 222px;
      top: 67px;
      width: 250px;
      height: 37px;
      opacity: 0.8;
      background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
      text-shadow: 0 0 9px #5cafff;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      z-index: 10;
      cursor:pointer;
    }
  }
  // 공업침전&공업혼화응집 건물
  &__sedimentation2-mix2{
    z-index: 1;
    position: absolute;
    left: 711px;
    top: 172px;
    width: 535px;
    height: 300px;
    clip-path: polygon(0 -100%, 100% 0, 100% 55%, 41% 101%, 0 64%);
    .up{
      position: absolute;
      top: -55px;
      // left: -30px;
      animation: sedimentation-mix-up 1s ease-in-out 0s normal;
    }
    @keyframes sedimentation-mix-up {
      0% {
        transform: translateY(54px);
      }
      100% {
        transform: translateY(0);
      }
    }
    .aurora_sedimentation2{
      pointer-events:none;
      top: 45px;
      left: 172px;
      animation: blink 3s infinite alternate;
    }
    .aurora_mix2{
      pointer-events:none;
      top: 1px;
      left: 285px;
      animation: blink 3s infinite alternate;
    }
    .sedimentation2-text {
      position: absolute;
      left: 99px;
      top: 113px;
      width: 250px;
      height: 37px;
      opacity: 0.8;
      background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
      text-shadow: 0 0 9px #5cafff;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      z-index: 10;
      cursor: pointer;
    }
    .mix2-text {
      position: absolute;
      left: 222px;
      top: 67px;
      width: 250px;
      height: 37px;
      opacity: 0.8;
      background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
      text-shadow: 0 0 9px #5cafff;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      z-index: 10;
      cursor:pointer;
    }
  }
    // 1차여과 건물
    &__percolation{
    z-index: 2;
    position: absolute;
    left: 218px;
    top: 258px;
    width: 544px;
    height: 328px;
    clip-path: polygon(0 -100%, 100% 0, 100% 80%, 71% 102%, 0 37%);
    .up {
      position: absolute;
      // left: -30px;
      top: -60px;
      animation: percolation-up 1s ease-in-out 0s normal;
    }
    @keyframes percolation-up {
      0% {
        transform: translateY(59px);
      }
      100% {
        transform: translateY(0);
      }
    }
    .aurora_percolation{
      pointer-events:none;
      top: 63px;
      left: 215px;
      animation: blink 3s infinite alternate;
    }
    .percolation-text{
      position: absolute;
      left: 168px;
      top: 123px;
      width: 215px;
      height: 37px;
      opacity: 0.8;
      background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
      text-shadow: 0 0 9px #5cafff;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      z-index: 10;
    }
  }
    // 소독 건물
    &__disinfection{
    position: absolute;
    left: 979px;
    top: 319px;
    width: 206px;
    height: 139px;
    clip-path: polygon(0 -36%, 100% -37%, 100% 81%, 42% 120%, 0 89%);
    .up{
      position: absolute;
      top:-42px;
      // left: -24px;
      animation: disinfection-up 1s ease-in-out 0s normal;
    }
    @keyframes disinfection-up {
      0% {
        transform: translateY(42px);
      }
      100% {
        transform: translateY(0);
      }
    }
    .aurora_disinfection{
      pointer-events:none;
      top: -29px;
      animation: blink 3s infinite alternate;
    }
    .disinfection-text {
      position: absolute;
      left: -6px;
      top: 38px;
      width: 130px;
      height: 37px;
      opacity: 0.8;
      background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
      text-shadow: 0 0 9px #5cafff;
      font-size: 20px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      text-align: center;
      color: #fff;
      z-index: 10;
      cursor: pointer;
    }
  }
  // GAC 여과 건물   
  // &__gac{
  //   position: absolute;
  //   left: 142px;
  //   top: 226px;
  //   width: 598px;
  //   height: 325px;
  //   clip-path: polygon(0 -100%, 100% 0, 100% 67%, 36% 100%, 0 69%);
  //   .up{
  //     position: absolute;
  //     top: -53px;
  //     left: -31px;
  //     animation: gac-up 1s ease-in-out 0s normal;
  //   }
  //   @keyframes gac-up {
  //     0% {
  //       transform: translateY(53px);
  //     }
  //     100% {
  //       transform: translateY(0);
  //     }
  //   }
  //   .aurora_gac{
  //     pointer-events:none;
  //     top: 5px;
  //     left: 120px;
  //     animation: blink 3s infinite alternate;
  //   }
  //   .gac-text {
  //     position: absolute;
  //     left: 57px;
  //     top: 74px;
  //     width: 250px;
  //     height: 37px;
  //     opacity: 0.8;
  //     font-family: "KHNPHUotfR" !important;
  //     background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
  //     text-shadow: 0 0 9px #5cafff;
  //     font-size: 20px;
  //     font-weight: normal;
  //     font-stretch: normal;
  //     font-style: normal;
  //     line-height: 2;
  //     letter-spacing: normal;
  //     text-align: center;
  //     color: #fff;
  //     z-index: 10;
  //     cursor: pointer;  
  //   }
  // }
  // 송수 건물
  // &__pump{
  //   position: absolute;
  //   left: 778px;
  //   top: 467px;
  //   width: 199px;
  //   height: 145px;
  //   clip-path: polygon(0 -30%, 105% -30%, 105% 53%, 20% 101%, 0 81%);
  //   .aurora_pump{
  //     pointer-events:none;
  //     top: -33px;
  //     left: 46px;
  //     animation: blink 3s infinite alternate;
  //   }
  //   .up {
  //     position: absolute;
  //     top: -42px;
  //     left: -31px;
  //     animation: pump-up 1s ease-in-out 0s normal;
  //   }
  //   @keyframes pump-up {
  //     0% {
  //       transform: translateY(42px);
  //     }
  //     100% {
  //       transform: translateY(0);
  //     }
  //   }
  //   .pump-text {
  //     position: absolute;
  //     left: -25px;
  //     top: 40px;
  //     width: 250px;
  //     height: 37px;
  //     opacity: 0.8;
  //     background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
  //     text-shadow: 0 0 9px #5cafff;
  //     font-size: 20px;
  //     font-weight: normal;
  //     font-stretch: normal;
  //     font-style: normal;
  //     line-height: 2;
  //     letter-spacing: normal;
  //     text-align: center;
  //     color: #fff;
  //     z-index: 10;
  //     cursor: pointer;  
  //   }
  // }
  // 탈수기동 건물
  // &__concentration{
  //   z-index: 1;
  //   position: absolute;
  //   left: 45px;
  //   top: 240px;
  //   width: 199px;
  //   height: 145px;
  //   clip-path: polygon(59% -100%, 101% -75%, 101% 65%, 26% 100%, -1% 69%, -1% -72%);
  //   .up{
  //     position: absolute;
  //     top: -57px;
  //     left: -30px;
  //     animation: concentration-up 1s ease-in-out 0s forwards;
  //   }
  //   @keyframes concentration-up {
  //     0% {
  //       transform: translateY(56px);
  //     }
  //     100% {
  //       transform: translateY(15px);
  //     }
  //   }
  //   .aurora_concentration{
  //     pointer-events:none;
  //     top: -36px;
  //     left: 40px;
  //     animation: blink 3s infinite alternate;
  //   }
  //   .concentration{
  //     pointer-events:none;
  //     top: -36px;
  //     left: 40px;
  //     animation: blink 3s infinite alternate;
  //   }
  //   .concentration-text {
  //     position: absolute;
  //     left: -26px;
  //     top: 40px;
  //     width: 250px;
  //     height: 37px;
  //     opacity: 0.8;
  //     background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
  //     text-shadow: 0 0 9px #5cafff;
  //     font-size: 20px;
  //     font-weight: normal;
  //     font-stretch: normal;
  //     font-style: normal;
  //     line-height: 2;
  //     letter-spacing: normal;
  //     text-align: center;
  //     color: #fff;
  //     z-index: 10;
  //     cursor: pointer;  
  //   }
  // }
  // 농축조 건물
  // &__dehydration{
  //   z-index: 1;
  //   position: absolute;
  //   left: 291px;
  //   top: 148px;
  //   width: 279px;
  //   height: 140px;
  //   clip-path: polygon(46% -100%, 72% -100%, 100% -71%, 100% 70%, 58% 100%, 28% 100%, 0 65%, 0 -74%);
  //   .up{
  //     position: absolute;
  //     top: -57px;
  //     left: -30px;
  //     animation: dehydration-up 1s ease-in-out 0s normal;
  //   }
  //   @keyframes dehydration-up {
  //     0% {
  //       transform: translateY(56px);
  //     }
  //     100% {
  //       transform: translateY(0);
  //     }
  //   }
  //   .aurora_dehydration{
  //     pointer-events:none;
  //     top: -24px;
  //     left: 82px;
  //     animation: blink 3s infinite alternate;
  //   }
  //   .dehydration{
  //     pointer-events:none;
  //     top: -36px;
  //     left: 40px;
  //     animation: blink 3s infinite alternate;
  //   }
  //   .dehydration-text {
  //     position: absolute;
  //     left: 15px;
  //     top: 52px;
  //     width: 250px;
  //     height: 37px;
  //     opacity: 0.8;
  //     background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
  //     text-shadow: 0 0 9px #5cafff;
  //     font-size: 20px;
  //     font-weight: normal;
  //     font-stretch: normal;
  //     font-style: normal;
  //     line-height: 2;
  //     letter-spacing: normal;
  //     text-align: center;
  //     color: #fff;
  //     z-index: 10;
  //     cursor: pointer;  
  //   }
  // }
  // 오존 건물
  // &__o3{
  //   z-index: 1;
  //   position: absolute;
  //   left: 451px;
  //   top: 281px;
  //   width: 79px;
  //   height: 61px;
  //   clip-path: polygon(67% -100%, 100% -80%, 100% 74%, 33% 100%, 0 76%, 0 -80%);
  //   .up{
  //     position: absolute;
  //     top: -57px;
  //     left: -30px;
  //     animation: o3-up 1s ease-in-out 0s normal forwards;
  //   }
  //   @keyframes o3-up {
  //     0% {
  //       transform: translateY(56px);
  //     }
  //     100% {
  //       transform: translateY(25px);
  //     }
  //   }
  //   .aurora_o3{
  //     pointer-events:none;
  //     top: -61px;
  //     left: -17px;
  //     animation: blink 3s infinite alternate;
  //   }
  //   .o3{
  //     pointer-events:none;
  //     top: -36px;
  //     left: 40px;
  //     animation: blink 3s infinite alternate;
  //   }
  //   .o3-text {
  //     position: absolute;
  //     left: -84px;
  //     top: 14px;
  //     width: 250px;
  //     height: 37px;
  //     opacity: 0.8;
  //     background-image: linear-gradient(to right, rgba(32, 80, 105, 0) 2%, rgba(2, 23, 52, 0.6) 36%, rgba(2, 23, 52, 0.6) 64%, rgba(32, 57, 105, 0));
  //     text-shadow: 0 0 9px #5cafff;
  //     font-size: 20px;
  //     font-weight: normal;
  //     font-stretch: normal;
  //     font-style: normal;
  //     line-height: 2;
  //     letter-spacing: normal;
  //     text-align: center;
  //     color: #fff;
  //     z-index: 10;
  //     cursor: pointer;
  //   }
  // }
}

// 공정별 아이콘
// 위 아래로 움직이는 아이콘
.cube {
  z-index: 5;
  pointer-events:none;  
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  width: 80px;
  height: 80px;
  -webkit-animation: updown 2s ease-in-out 0s infinite alternate;
  animation: updown 2s ease-in-out 0s infinite alternate;
  // 아이콘 테투리 원
  &__inner {
    width: 80px;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-image: url('../../assets/dashboard_icons/cube_back.png');
  }
  // 아이콘 3차원
  &__noimg{
    width: 80px;
    height: 80px;
    -webkit-transform-style: preserve-3d;
    -webkit-transform: rotateX(0) rotateY(0) rotateZ(0);

    transform-style: preserve-3d;
    transform: rotateX(0) rotateY(0) rotateZ(0);
    // animation: name | duration | timing-function | delay | iteration-count | direction | fill-mode | play-state
    -webkit-animation: turn 5s linear 0s infinite normal;
    animation: turn 5s linear 0s infinite normal;
  }
  // 아이콘 앞면(AI 모드 ON)
  &__front_logo--on {    
    width: 80px;
    height: 80px;
    background-image: url('../../assets/dashboard_icons/cube_ai_icon.png');
    background-position: center;
    background-size: cover;
    position: absolute;
    -webkit-backface-visibility: hidden;
    backface-visibility: hidden;
  }
  // 아이콘 앞면(AI 모드 OFF)
  &__front_logo--off {    
    width: 80px;
    height: 80px;
    background-image: url('../../assets/dashboard_icons/cube_ai_off_icon.png');
    background-position: center;
    background-size: cover;
    position: absolute;
    -webkit-backface-visibility: hidden;
    backface-visibility: hidden;
  }
  // 아이콘 뒷면
  &__back_logo {
    width: 80px;
    height: 80px;
    background-position: center;
    position: absolute;
    backface-visibility: hidden;
    -webkit-backface-visibility: hidden;
    transform:  rotateY(180deg);
    -webkit-transform:  rotateY(180deg);
  }
}
// 회전 keyframe
@-webkit-keyframes turn {
  0% {
    -webkit-transform: rotateY(0);
  }
  100% {
    -webkit-transform: rotateY(360deg);
  }
}
@keyframes turn {
  0% {
    transform: rotateY(0);
  }
  100% {
    transform: rotateY(360deg);
  }
}

// 위아래 둥실둥실 keyframe
@keyframes updown {
  0% {
    transform: translateY(0px);
  }
  100% {
    transform: translateY(20px);
  }
}
@-webkit-keyframes updown {
  0% {
    transform: translateY(0px);
  }
  100% {
    transform: translateY(20px);
  }
}

// 반짝반짝 keyframe
@keyframes blink {
  0% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}

// 소독 아이콘
.disinfection_cube{
  top: 237px;
  left: 993px;
  .back_disinfection{
    background-image: url('../../assets/dashboard_icons/cube_disinfection_icon.png');
  }
}

// 여과 아이콘
.percolation_cube{
  top: 262px;
  left: 448px;
  .back_percolation{
    background-image: url('../../assets/dashboard_icons/cube_percolation_icon.png');
  }
}

// 약품 아이콘
.drug_cube{
  top: -50px;
  left: 1051px;
  .back_drug{
    background-image: url('../../assets/dashboard_icons/cube_drug_icon.png');
  }
}
// 공업약품 아이콘
.drug2_cube{
  top: -9px;
  left: 1142px;
  .back_drug{
    background-image: url('../../assets/dashboard_icons/cube_drug_icon.png');
  }
}

// 착수 아이콘
.splashdown_cube{
  top: -208px;
    left: 1199px;
  .back_splashdown{
    background-image: url('../../assets/dashboard_icons/cube_splashdown_icon.png');
  }
}

// 침전 아이콘
.sedimentation_cube{
  z-index: 2;
  top: 21px;
  left: 572px;
  .back_sedimentation{
    background-image: url('../../assets/dashboard_icons/cube_sedimentation_icon.png');
  }
}
// 공업침전 아이콘
.sedimentation2_cube{
  z-index: 2;
  top: 172px;
  left: 900px;
  .back_sedimentation{
    background-image: url('../../assets/dashboard_icons/cube_sedimentation_icon.png');
  }
}

// 혼화응집 아이콘
.mix_cube{
  top: 39px;
  left: 842px;
  .back_mix{
    background-image: url('../../assets/dashboard_icons/cube_mix_icon.png');
  }
}
// 공업혼화응집 아이콘
.mix2_cube{
  top: 125px;
  left: 1014px;
  .back_mix{
    background-image: url('../../assets/dashboard_icons/cube_mix_icon.png');
  }
}

// GAC 여과 아이콘
// .gac_cube{
//   top:268px;
//   left:223px;
//   .back_gac{
//     background-image: url('../../assets/dashboard_icons/cube_gac_icon.png');
//   }
// }

// 송수 아이콘
// .pump_cube{
//   top:389px;
//   left:838px;
//   .back_pump{
//     background-image: url('../../assets/dashboard_icons/cube_pump_icon.png');
//   }
// }

// 탈수기동 아이콘
// .concentration_cube{
//   top:150px;
//   left:100px;
//   .back_concentration{
//     background-image: url('../../assets/dashboard_icons/cube_mix_icon.png');
//   }
// }

// 농축조 아이콘
// .dehydration_cube{
//   top:70px;
//   left:386px;
//   .back_dehydration{
//     background-image: url('../../assets/dashboard_icons/cube_splashdown_icon.png');
//   }
// }

// 오존 아이콘
// .o3_cube{
//   top:170px;
//   left:450px;
//   .back_o3{
//     background-image: url('../../assets/dashboard_icons/cube_ozone_icon.png');
//   }
// }
</style>