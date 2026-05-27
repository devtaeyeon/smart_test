<template>
  <div class="dashboard-info-container">
    <!-- 자율운영 -->
    <div class="top-contents">
      <!-- 공정별 제어 좌측 메뉴 -->
      <div class="left-contents">
        <div class="left-contents__title">자율운영</div>
        <button class="ai-opr-btn" @click="onClickAiOprHisPopup">AI운영이력</button>
        <div class="left-contents-scroll">
          <!-- <div class="part-contents__text"></div> -->
          <!-- 착수 AI 모드 제어 -->
          <!-- <div class="btn-contents" :class="{selected: [1].includes(this.$store.state.selectedBuildingIndex)}">
            <div class="btn-contents__text">착수</div>
            <div class="btn-contents__btn">
              <div class="control_box_operation">
                <div v-if="$store.state.receiving.latest.ai_liv_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(1, 2, 1)">AI</div>
                <div v-if="$store.state.receiving.latest.ai_liv_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(1, 1, 1)">AI추천</div>
                <div v-if="$store.state.receiving.latest.ai_liv_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(1, 0, 1)">AI분석</div>
              </div>
            </div>
          </div> -->
          <!-- 전체 운영모드 변경 버튼 -->
          <div class="ai-btn-contents">
            <div class="ai-btn-contents__text">전체</div>
            <div class="ai-btn-contents__btn">
              <div class="ai-control_box_operation">
                <div class="mode-one ai-control_box_operation__btn ai-control_box_operation__btn--on" @click="onClickAllAICheckbox(2)">AI</div>
                <div class="mode-two ai-control_box_operation__btn ai-control_box_operation__btn--on" @click="onClickAllAICheckbox(1)">AI추천</div>
                <div class="mode-three ai-control_box_operation__btn ai-control_box_operation__btn--on" @click="onClickAllAICheckbox(0)">AI분석</div>
              </div>
            </div>
          </div>
          <!-- 약품 AI 모드 제어 -->
          <div class="btn-contents" :class="{selected: this.$store.state.selectedBuildingIndex === 2}">
            <div class="btn-contents__text">약품</div>
            <div class="btn-contents__btn">
              <div class="control_box_operation">
                <div v-if="$store.state.coagulants.latest.ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(2, 2)">AI</div>
                <div v-if="$store.state.coagulants.latest.ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(2, 1)">AI추천</div>
                <div v-if="$store.state.coagulants.latest.ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(2, 0)">AI분석</div>
              </div>
            </div>
          </div>
          <!-- 혼화응집 AI 모드 제어 -->
          <div class="btn-contents" :class="{selected: this.$store.state.selectedBuildingIndex === 3}">
            <div class="btn-contents__text">혼화응집</div>
            <div class="btn-contents__btn">
              <div class="control_box_operation">
                <div v-if="$store.state.mixing.latest.ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(3, 2)">AI</div>
                <div v-if="$store.state.mixing.latest.ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(3, 1)">AI추천</div>
                <div v-if="$store.state.mixing.latest.ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(3, 0)">AI분석</div>
              </div>
            </div>
          </div>
          <!-- 침전 AI 모드 제어 -->
          <!-- <div class="btn-contents" :class="{selected: this.$store.state.selectedBuildingIndex === 4}">
            <div class="btn-contents__text">침전</div>
            <div class="btn-contents__btn">
              <div class="control_box_operation">
                <div v-if="$store.state.sedimentation.latest.ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(4, 2)">AI</div>
                <div v-if="$store.state.sedimentation.latest.ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(4, 1)">AI추천</div>
                <div v-if="$store.state.sedimentation.latest.ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(4, 0)">AI분석</div>
              </div>
            </div>
          </div> -->
          <!-- 여과 AI 모드 제어 -->
          <!-- <div class="btn-contents" :class="{selected: this.$store.state.selectedBuildingIndex === 5}">
            <div class="btn-contents__text">여과</div>
            <div class="btn-contents__btn">
              <div class="control_box_operation">
                  <div v-if="$store.state.filter.latest.ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
                  <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(5, 2)">AI</div>
                  <div v-if="$store.state.filter.latest.ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
                  <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(5, 1)">AI추천</div>
                  <div v-if="$store.state.filter.latest.ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
                  <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(5, 0)">AI분석</div>
                </div>
              </div>
          </div> -->
          <!-- 소독 AI 모드 제어 -->
          <div class="btn-contents" :class="{selected: this.$store.state.selectedBuildingIndex === 7.1}">
            <div class="btn-contents__text">소독</div>
            <div class="btn-contents__btn">
              <div class="control_box_operation control_box_operation--disinfection">
                <div v-if="$store.state.disinfection.latest.pre_ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(7.1, 2)">AI</div>
                <div v-if="$store.state.disinfection.latest.pre_ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(7.1, 1)">AI추천</div>
                <div v-if="$store.state.disinfection.latest.pre_ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(7.1, 0)">AI분석</div>
              </div>
              <!-- <div class="control_box_operation control_box_operation--disinfection">
                <div v-if="$store.state.disinfection.latest.peri_ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(7.2, 2)">AI</div>
                <div v-if="$store.state.disinfection.latest.peri_ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(7.2, 1)">AI추천</div>
                <div v-if="$store.state.disinfection.latest.peri_ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(7.2, 0)">AI분석</div>
              </div> -->
              <div class="control_box_operation control_box_operation--disinfection">
                <div v-if="$store.state.disinfection.latest.post_ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(7.3, 2)">AI</div>
                <div v-if="$store.state.disinfection.latest.post_ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(7.3, 1)">AI추천</div>
                <div v-if="$store.state.disinfection.latest.post_ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
                <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(7.3, 0)">AI분석</div>
              </div>
            </div>
          </div>

          <!-- 여과 AI 모드 제어 -->
          <!-- <div class="btn-contents" :class="{selected: this.$store.state.selectedBuildingIndex === 5}">
            <div class="btn-contents__text">여과</div>
            <div class="btn-contents__btn">
              <div class="control_box_operation">
                  <div v-if="$store.state.filter.latest.ai_opr === 2" class="control_box_operation__btn control_box_operation__btn--on">AI</div>
                  <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(5, 2)">AI</div>
                  <div v-if="$store.state.filter.latest.ai_opr === 1" class="control_box_operation__btn control_box_operation__btn--on">AI추천</div>
                  <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(5, 1)">AI추천</div>
                  <div v-if="$store.state.filter.latest.ai_opr === 0" class="control_box_operation__btn control_box_operation__btn--on">AI분석</div>
                  <div v-else class="control_box_operation__btn control_box_operation__btn--off" @click="onClickAICheckbox(5, 0)">AI분석</div>
                </div>
              </div>
          </div> -->
        </div>
      </div>
      <!-- 착수 예측 결과 -->
      <!-- <Result1 v-if="this.$store.state.selectedBuildingIndex === 0 || this.$store.state.selectedBuildingIndex === 1" :isAiOn="this.$store.state.receiving.latest.ai_opr"/> -->
      <!-- 약품 예측 결과 -->
      <Result2 v-if="this.$store.state.selectedBuildingIndex === 0 || this.$store.state.selectedBuildingIndex === 2" :isAiOn="this.$store.state.coagulants.latest.ai_opr"/>
      <!-- 혼화응집 예측 결과 -->
      <Result3 v-if="this.$store.state.selectedBuildingIndex === 3" :isAiOn="this.$store.state.mixing.latest.ai_opr"/>
      <!-- 침전 예측 결과 -->
      <!-- <Result4 v-if="this.$store.state.selectedBuildingIndex === 4" :isAiOn="this.$store.state.sedimentation.latest.ai_opr"/> -->
      <!-- 여과 예측 결과 -->
      <!-- <Result5 v-if="this.$store.state.selectedBuildingIndex === 5" :isAiOn="this.$store.state.filter.latest.ai_opr"/> -->
      <!-- GAC여과 예측 결과 -->
      <!-- <Result6 v-if="this.$store.state.selectedBuildingIndex === 6" :isAiOn="this.$store.state.gac.latest.ai_opr"/> -->
      <!-- 소독 예측 결과 -->
      <Result7 v-if="this.$store.state.selectedBuildingIndex === 7.1 || this.$store.state.selectedBuildingIndex === 7.3" :isAiOn="this.$store.getters['disinfection/isAiOperationMode']"/>
      <!-- 오존 예측 결과 -->
      <!-- <Result11 v-if="this.$store.state.selectedBuildingIndex === 11" :isAiOn="this.$store.state.ozone.latest.ai_opr"/> -->
    </div>

    <!-- EMS -->
    <div class="dashboard_info_content_container">
      <!-- 구분선 -->
      <div class="h_dash_bar"></div>
      <!-- EMS Header -->
      <div class="dashboard_info_content_header_container">
        <!-- EMS 제목 -->
        <div class="dashboard_info_content_header_text">EMS</div>
        <!-- 펌프제어, 피크관리, DR참여 탭 -->
        <div class="dashboard_info_content_tab">
          <div class="tab" @click="selectedEmsIndex = 0" :class="{'selected_ems': selectedEmsIndex === 0}" style="margin-bottom:5px;">펌프제어</div>
            <div class="tab" @click="onClickPeakControl(1)" :class="{'selected_ems': selectedEmsIndex === 1}" style="margin-bottom:5px;">피크관리</div>
            <!-- <div class="tab" @click="selectedEmsIndex = 2" :class="{'selected_ems': selectedEmsIndex === 2}">DR참여</div> -->
        </div>
      </div>
      <!-- EMS Content -->
      <div class="ems_container">
        <div class="ems_content_container">
          <!-- 펌프제어 -->
          <div v-if="this.selectedEmsIndex === 0">
            <div class="divider">
              <div class="dashboard-info-ems">
                <div class="dashboard-info-ems-title">
                  <div class="dashboard-info-ems-title__text">도수(가)</div>
                  <div class="btn-contents ems-toggle">
                    <input disabled type="checkbox" class="checkbox" :class="{ 'checked': this.$store.state.ems.latest.pump.h1_operation_mode === 1 }" style="cursor: unset;"/>
                  </div>
                </div>
                <div class="dashboard-info-ems-title" style="margin-left: 35px;">
                  <div class="dashboard-info-ems-title__text">송수(가)</div>
                  <div class="btn-contents ems-toggle">
                    <input disabled type="checkbox" class="checkbox" :class="{ 'checked': this.$store.state.ems.latest.pump.h2_operation_mode === 1 }" style="cursor: unset;"/>
                  </div>
                </div>
              </div>
              <!-- 운영 현황 -->
              <div class="ems_contents_info">
                <div class="ems_contents_info__title">운영현황</div>
                <div class="ems_contents__td">
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h1_pm1 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h1_pm2 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h1_pm3 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                </div>
                <div class="ems_contents__td" style="margin-left: 70px;">
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h2_pm1 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                </div>
              </div>
              <div class="ems_content_container__divider"></div>
              <!-- AI 분석 -->
              <div class="ems_contents_info">
                <div class="ems_contents_info__title">AI분석</div>
                <div class="ems_contents__td">
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h1_pm1 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h1_pm2 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h1_pm3 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                </div>
                <div class="ems_contents__td" style="margin-left: 70px;">
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h2_pm1 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                </div>
              </div>
            </div>          
            <div class="divider">
              <div class="dashboard-info-ems">
                <div class="dashboard-info-ems-title">
                  <div class="dashboard-info-ems-title__text">용담(가)</div>
                  <div class="btn-contents ems-toggle">
                    <input disabled type="checkbox" class="checkbox" :class="{ 'checked': this.$store.state.ems.latest.pump.h3_operation_mode === 1 }" style="cursor: unset;"/>
                  </div>
                </div>
                <div class="dashboard-info-ems-title" style="margin-left: 35px;">
                  <div class="dashboard-info-ems-title__text">운봉(가)</div>
                  <div class="btn-contents ems-toggle">
                    <input disabled type="checkbox" class="checkbox" :class="{ 'checked': this.$store.state.ems.latest.pump.h4_operation_mode === 1 }" style="cursor: unset;"/>
                  </div>
                </div>
              </div>
              <!-- 운영 현황 -->
              <div class="ems_contents_info">
                <div class="ems_contents_info__title">운영현황</div>
                <div class="ems_contents__td">
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h3_pm1 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h3_pm2 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h3_pm3 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                </div>
                <div class="ems_contents__td" style="margin-left: 70px;">
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h4_pm1 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h4_pm2 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.h4_pm3 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                </div>
              </div>
              <div class="ems_content_container__divider"></div>
              <!-- AI 분석 -->
              <div class="ems_contents_info">
                <div class="ems_contents_info__title">AI분석</div>
                <div class="ems_contents__td">
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h3_pm1 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h3_pm2 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h3_pm3 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                </div>
                <div class="ems_contents__td" style="margin-left: 70px;">
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h4_pm1 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h4_pm2 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                  <div class="ems_contents__led" :class="[ this.$store.state.ems.latest.pump.ai_h4_pm3 === 1 ? 'ems_contents__led--on' : 'ems_contents__led--off' ]"></div>
                </div>
              </div>
            </div>          
          </div>
          <!-- 피크관리 -->
          <div v-else-if="this.selectedEmsIndex === 1">
            <highcharts :options="ChartEMSPeak"/>
          </div>
          <!-- DR참여 -->
          <div v-else-if="this.selectedEmsIndex === 2">
            <highcharts :options="ChartDR"/>
          </div>
        </div>
      </div>
    </div>
    <!-- PMS -->
    <!-- <div class="dashboard_info_content_container">
      <div class="h_dash_bar"></div>
      <div class="dashboard_info_content_header_container">
        <div class="dashboard_info_content_header_text">PMS</div>
        <div class="dashboard_info_content_tab" style="margin-right: 0px;">
          <div class="tab-pms-title">평택</div>
          <div class="tab-pms" @click="$store.state.pms.selectedId = '01'" :class="{'selected_ems': this.$store.state.pms.selectedId === '01'}">#1</div>
          <div class="tab-pms" @click="$store.state.pms.selectedId = '02'" :class="{'selected_ems': this.$store.state.pms.selectedId === '02'}">#2</div>
          <div class="tab-pms" @click="$store.state.pms.selectedId = '03'" :class="{'selected_ems': this.$store.state.pms.selectedId === '03'}">#3</div>
          <div class="tab-pms" @click="$store.state.pms.selectedId = '04'" :class="{'selected_ems': this.$store.state.pms.selectedId === '04'}">#4</div>
        </div>
        <div class="dashboard_info_content_tab">
          <div class="tab-pms-title">송산</div>
          <div class="tab-pms" @click="$store.state.pms.selectedId = '05'" :class="{'selected_ems': this.$store.state.pms.selectedId === '05'}">#1</div>
          <div class="tab-pms" @click="$store.state.pms.selectedId = '06'" :class="{'selected_ems': this.$store.state.pms.selectedId === '06'}">#2</div>
        </div>
      </div>
      <div class="pms_container">
        <div class="pms_contents_container">
          <div class="pump-contents">
            <div :class="{'pump-contents__img': selectedPumpStatus !== 3}"></div>
            <div :class="{'pump-contents__img': selectedPumpStatus === 3}"></div>
            <div class="pump-contents-blue">
              <div class="pump-contents-blue__box">{{ this.$store.getters['pms/getPMSPumpState'].ai.pump_de_amp | numFormat("0.0") }} rms</div>
              <div class="pump-contents-blue__box">{{ this.$store.getters['pms/getPMSPumpState'].scada.brg_pump_de_temp | numFormat("0.0")}} °C</div>
              <div class="pump-contents-blue__line"></div>
              <div class="pump-contents-blue__circle"></div>
            </div>
            <div class="pump-contents-green">
              <div class="pump-contents-green__box">{{ this.$store.getters['pms/getPMSPumpState'].ai.pump_nde_amp | numFormat("0.0")}} rms</div>
              <div class="pump-contents-green__box">{{ this.$store.getters['pms/getPMSPumpState'].scada.brg_pump_nde_temp | numFormat("0.0")}} °C</div>
              <div class="pump-contents-green__line"></div>
              <div class="pump-contents-green__circle"></div>
            </div>
            <div class="pump-contents-yellow">
              <div class="pump-contents-yellow__box">{{ this.$store.getters['pms/getPMSPumpState'].ai.motor_de_amp | numFormat("0.0")}} rms</div>
              <div class="pump-contents-yellow__box">{{ this.$store.getters['pms/getPMSPumpState'].scada.brg_motor_de_temp | numFormat("0.0")}} °C</div>
              <div class="pump-contents-yellow__line"></div>
              <div class="pump-contents-yellow__circle"></div>
            </div>
            <div class="pump-contents-red">
              <div class="pump-contents-red__box">{{ this.$store.getters['pms/getPMSPumpState'].ai.motor_nde_amp | numFormat("0.0")}} rms</div>
              <div class="pump-contents-red__box">{{ this.$store.getters['pms/getPMSPumpState'].scada.brg_motor_nde_temp | numFormat("0.0")}} °C</div>
              <div class="pump-contents-red__line"></div>
              <div class="pump-contents-red__circle"></div>
            </div>
          </div>
        </div>
      </div>
    </div> -->
  </div>
</template>

<script>
// import Highcharts from 'highcharts'

// // 하이차트 UTC설정
// Highcharts.setOptions({
//   global: {
//     useUTC: false
//   },
//   lang: {
//     thousandsSep: ','
//   }
// })
import Result2 from '@/components/dashboard/result/Result2'
import Result3 from '@/components/dashboard/result/Result3'
// import Result6 from '@/components/dashboard/result/Result6'
import Result7 from '@/components/dashboard/result/Result7'
// import Result11 from '@/components/dashboard/result/Result11'
import { OPEN_AI_MODE_DIALOG } from '@/store/modules/dialog'
import { SERVICE_URL } from '@/store'

export default {
  data: () => ({
    selectedPumpStatus: 0, // 선택된 PMS 펌프 상태
    selectedEmsIndex: 0, // 선택된 EMS Menu Tab Index
    // EMS 피크관리 차트
    chartEMSPeak: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        height: '180px',
        zoomType: 'x'
      },
      title: {
          text: null
      },
      legend: {
        align: 'right',
        verticalAlign: 'top',
        borderWidth: 0,
        x: -20,
        y: 0,
        itemStyle: {
          color: '#ffffff',
          fontWeight: 'bold'
        },
        labelFormatter: function () {
          return '<span style="color: ' + this.color + '; width: 100px;">' + this.name + '</span>'
        }
      },
      credits: {
        enabled: false
      },
      tooltip: {
        valueDecimals: 2,
        xDateFormat: '%Y-%m-%d %H:%M:%S',
        valueSuffix: 'kW'
      },
      plotOptions: {
        series: {
          marker: {
            radius: 0
          }
        }
      },
      xAxis: {
        title: {
          text: ''
        },
        categories: ['00:00','01:00','02:00','03:00','04:00','05:00','06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00','22:00','23:00'],
        labels: {
          format: '{value:%H:%M}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        },
        tickInterval: 5
      },
      yAxis: {
        title: {
          align: 'high',
          text: 'kW',
          useHTML: true,
          offset: 0,
          rotation: 0,
          x: -15,
          y: -15,
          style: {
            color:"rgb(255,255,255,0.8)"
          }
        },
        gridLineColor: false,
        labels: {
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        }
      },
      series: null,
      exporting: {
        buttons: {
          contextButton: {
            menuItems: ['viewFullscreen', 'downloadJPEG', 'downloadCSV'],
            menuItemStyle: { padding: "0 10px", background: "none", color: "#303030"},
            symbolStroke: "rgb(110, 157, 225)",
            theme: {
              fill:"rgba(0,0,0,0)",
              states: {
                hover: {
                  fill: 'rgba(0,0,0,0)',
                },
                select: {
                  fill: 'rgba(0,0,0,0)',
                }
              }
            },
            titleKey: null
          }
        },
        filename: null,
        fallbackToExportServer: false,
      }
    },
    // DR참여 차트
    chartDR: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        height: '180px',
        zoomType: 'x'
      },
      title: {
          text: null
      },
      legend: {
        align: 'right',
        verticalAlign: 'top',
        borderWidth: 0,
        y: 0,
        itemStyle: {
          color: '#ffffff',
          fontWeight: 'bold'
        },
        labelFormatter: function () {
          return '<span style="color: ' + this.color + '; width: 100px;">' + this.name + '</span>'
        }
      },
      credits: {
        enabled: false
      },
      tooltip: {
        valueDecimals: 2,
        xDateFormat: '%Y-%m-%d %H:%M:%S',
        valueSuffix: 'kW'
      },
      plotOptions: {
        series: {
          marker: {
            radius: 0
          }
        }
      },
      xAxis: {
        title: {
          text: ''
        },
        categories: ['00:00','01:00','02:00','03:00','04:00','05:00','06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00','22:00','23:00'],
        labels: {
          format: '{value:%H:%M}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        },
        tickInterval: 5
      },
      yAxis: {
        title: {
          align: 'high',
          text: 'kW',
          useHTML: true,
          offset: 0,
          rotation: 0,
          x: -15,
          y: -15,
          style: {
            color:"rgb(255,255,255,0.8)"
          }
        },
        gridLineColor: false,
        labels: {
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        }
      },
      series: null,
      exporting: {
        buttons: {
          contextButton: {
            menuItems: ['viewFullscreen', 'downloadJPEG', 'downloadCSV'],
            menuItemStyle: { padding: "0 10px", background: "none", color: "#303030"},
            symbolStroke: "rgb(110, 157, 225)",
            theme: {
              fill:"rgba(0,0,0,0)",
              states: {
                hover: {
                  fill: 'rgba(0,0,0,0)',
                },
                select: {
                  fill: 'rgba(0,0,0,0)',
                }
              }
            },
            titleKey: null
          }
        },
        filename: null,
        fallbackToExportServer: false,
      }
    },
  }),
  components: {
    Result2,
    Result3,
    // Result6,
    Result7,
    // Result11
  },
  computed: {
    // EMS 피크관리 차트 객체 반환
    ChartEMSPeak: function () {
      let chart = this.chartEMSPeak
      chart.series = []
      if (this.$store.state.ems.latest.peak.ai_z_power !== null && Object.keys(this.$store.state.ems.latest.peak.ai_z_power).length > 0) {
        let dataSeries1 = []
        for (let i = 0; i < Object.keys(this.$store.state.ems.latest.peak.ai_z_power).length; i++) {
          dataSeries1[i] = [this.$store.state.ems.latest.peak.ai_z_power[i]]
        }
        chart.series.push({
          name: '발생전력',
          data: dataSeries1,
          color: 'rgb(110, 157, 225)'
        })
      }
      if (this.$store.state.ems.latest.peak.ai_z_power !== null && Object.keys(this.$store.state.ems.latest.peak.ai_z_power).length > 0) {
        let dataSeries1 = []
        for (let i = 0; i < Object.keys(this.$store.state.ems.latest.peak.ai_z_power).length; i++) {
          dataSeries1[i] = [this.$store.state.ems.latest.peak.ai_z_power[i]]
        }
        chart.series.push({
          name: '예상전력',
          data: dataSeries1,
          color: '#b4dffb'
        })
      }
      if (this.$store.state.ems.latest.peak.ai_z_power_peak !== null && Object.keys(this.$store.state.ems.latest.peak.ai_z_power_peak).length > 0) {
        let dataSeries1 = []
        for (let i = 0; i < Object.keys(this.$store.state.ems.latest.peak.ai_z_power_peak).length; i++) {
          dataSeries1[i] = [this.$store.state.ems.latest.peak.ai_z_power_peak[i]]
        }
        chart.series.push({
          name: 'AI 전력피크',
          data: dataSeries1,
          color: 'rgb(163, 69, 255)'
        })
      }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_EMS 피크관리'
      return chart
    },
    // EMS DR참여 차트 객체 반환
    ChartDR: function () {
      let chart = this.chartDR
      chart.series = []
      if (this.$store.state.ems.latest.dr.z_cbl !== null && Object.keys(this.$store.state.ems.latest.dr.z_cbl).length > 0) {
        let dataSeries1 = []
        for (let i = 0; i < Object.keys(this.$store.state.ems.latest.dr.z_cbl).length; i++) {
          dataSeries1[i] = [this.$store.state.ems.latest.dr.z_cbl[i]]
        }
        chart.series.push({
          name: 'CBL',
          data: dataSeries1,
          color: 'rgb(110, 157, 225)'
        })
      }
      if (this.$store.state.ems.latest.dr.ai_z_dr_power !== null && Object.keys(this.$store.state.ems.latest.dr.ai_z_dr_power).length > 0) {
        let dataSeries1 = []
        for (let i = 0; i < Object.keys(this.$store.state.ems.latest.dr.ai_z_dr_power).length; i++) {
          dataSeries1[i] = [this.$store.state.ems.latest.dr.ai_z_dr_power[i]]
        }
        chart.series.push({
          name: 'AI DR 예상전력',
          data: dataSeries1,
          color: '#b4dffb'
        })
      }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_DR참여'
      return chart
    }
  },
  methods: {
    /**
     * 운영모드를 변경할 때 실행되는 함수
     * 운영모드 제어 요청 팝업을 띄움
     * 
     * @param index 선택한 공정 index
     * @param expectedValue 변경할 운영 모드(0: AI분석, 1: AI추천, 2: AI)
     * @param receivingStep 생활착수, 공업착수 index
     */
    onClickAICheckbox: function(index, expectedValue, receivingStep) {
      if( this.$store.state.login.user.tkn !== null ) {
        this.$store.state.selectedBuildingIndex = index
        this.$store.state.dialog.aiMode.receivingIndex = receivingStep
        this.$store.dispatch('dialog/' + OPEN_AI_MODE_DIALOG, expectedValue)
      }
    },
    /**
     * 전체 AI 운전모드 변경 클릭시
     * 운전모드 변경 Dialog 오픈
     * 
     * @param index 공정index
     * @expectedValue 변경할 운전모드
     */
    onClickAllAICheckbox: function(expectedValue) {
      if( this.$store.state.login.user.tkn !== null ) {
        this.$store.state.dialog.aiMode.changeAllAIMode = true
        this.$store.dispatch('dialog/' + OPEN_AI_MODE_DIALOG, expectedValue)
      }
    },
    onClickAiOprHisPopup: function(){
      this.$store.state.aioprhistory.aiOprHistoryPopupVisible = true
    },
    onClickPeakControl: function() {
      let _url = '/peakControl'
      if (this.$store.state.login.user.tkn !== null) {
        window.open(SERVICE_URL.EMS + _url +'?token=' + this.$store.state.login.user.tkn, "_self")
      } else {
        window.open(SERVICE_URL.EMS + _url, "_self")
      }
    }
  }
}
</script>

<style lang="scss" scoped>
*::-webkit-scrollbar {
  width: 5px;
}
*::-webkit-scrollbar-track {
  background-color: #1b2e53;
  border-radius: 2.5px;
}
*::-webkit-scrollbar-thumb {
  background-color: #417db9;
  border-radius: 2.5px;
}
// DashboardInfo 최상위 엘리먼트
.dashboard-info-container {
  position: absolute;
  right: 0;
  top: 0;
  display: flex;
  flex-flow: column;
  width: 411px;
  height: 100%;
  overflow-y: auto;
  background-image: url('../../assets/dashboard_img/dashboard_info.png');
  background-size: 100% 100%;
  align-items: center;
  .dashboard_info_content_container {
    width: 396px;
    display: flex;
    flex-flow: column;
  }
  .dashboard_info_content_header_container {
    display: inline-flex;
    height: 56px;
    align-items: center;
  }
  .dashboard_info_content_header_text {
    display:flex;
    align-items: center;
    width: 86px;
    height: 47px;
    text-shadow: 0 0 9px #5cafff;
    font-family: "KHNPHUotfR";
    font-size: 18px;
    line-height: 1.5;
    text-align: center;
    color: #fff;
    padding-left: 10px;
  }
}

// 자율운영 공정별 제어 & AI 결과
.top-contents{
  display: flex;
  width: 100%;
  height: 400px;
  // 자율운영 공정별 제어(좌측 메뉴)
  .left-contents {
    width: 246px;
    height: 100%;
    // 자율운영 제목
    &__title{
      margin: 15px 0 10px;
      height: 47px;
      background-image: url('../../assets/dashboard_img/title_background.png');
      text-shadow: 0 0 9px #5cafff;
      font-family: "KHNPHUotfR";
      font-size: 18px;
      line-height: 1.6;
      text-align: center;
      color: #fff;
      background-position-x: 50%;
    }
    .ai-opr-btn {
      position: absolute;
      top: 20px;
      right: 13px;
      color: #fff;
      border: solid 1px #b4dffa;
      background-color: rgba(139, 194, 240, 0.25);
      font-size: 15px;
      width: 140px;
      height: 38px;

    }
    // 전체 ai모드 제어 박스
    .ai-control_box_operation {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 190px;
      height: 28px;
      border-radius: 14px;
      .mode-one {
        background-image: linear-gradient(to right,#6dbbe5 0%,#3a70d2 100%);
      }
      .mode-two {
        background-image: linear-gradient(to right,#5496dc 0%,#525cd2 100%);
      }
      .mode-three {
        background-image: linear-gradient(to right,#525cd2 0%,#253ec3 100%);
      }
      &__btn {
        width: 60px;
        height: 27px;
        color: #fff;
        font-size: 12px;
        text-align: center;
        padding: 5px 10px;
        border-radius: 14px;
        cursor: pointer;
        &--on {
          // box-shadow: 0 0 6px 0 #5d7ac9;
          background-color: #b4dffa;
        }
        &--off {
          background-color: #417290;
        }
      }
    }
    // 운영모드 제어 박스
    .control_box_operation {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 140px;
      height: 28px;
      padding: 0px 1px;
      border-radius: 14px;
      background-color: rgba(139, 194, 240, 0.25);
      // 소독
      &--disinfection {
        position: relative;
        &:before {
          position: absolute;
          left: -30px;
          font-size: 15px;
          color: #c3eaff;
        }
        // 전염소
        &:nth-child(1) {
          margin: 7px 0;
          &:before {
            content: '(전)'
          }
        }
        // 후염소
        &:nth-child(2) {
          margin: 7px 0;
          &:before {
            content: '(후)'
          }
        }
      }
      &__btn {
        height: 22px;
        color: #19274e;
        font-size: 11px;
        margin: 0px 1px;
        padding: 4px 10px 4px 10px;
        border-radius: 11px;
        cursor: pointer;
        &--on {
          box-shadow: 0 0 6px 0 #e8faff;
          background-color: #b4dffa;
        }
        &--off {
          background-color: #417290;
        }
      }
    }
  }
  .left-contents-scroll {
    height: 390px;
    overflow-y: auto;
    overflow-x: hidden;
    padding-right: 5px;
  }
  .left-contents-scroll::-webkit-scrollbar {
    width: 5px;
  }
  .left-contents-scroll::-webkit-scrollbar-track {
    background-color: #011527;
    border-radius: 2.5px;
  }
  .left-contents-scroll::-webkit-scrollbar-thumb {
    background-color: #417db9;
    border-radius: 2.5px;
  }
  // 선택된 공정 배경
  .selected{
    background-image: url('../../assets/dashboard_img/automatic_selected.png');
    background-size: 100% 100%;
  }
}

// 자율운영, EMS, PMS 간의 구분선
.h_dash_bar {
  margin: 10px 0 10px 10px;
  width: 354px;
  height: 7px;
  background-image: url('../../assets/dashboard_img/h_dash_bar.png');
}
// 전체 ai모드 제어 버튼
.ai-btn-contents {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 235px;
  margin-left: 8px;
  margin-bottom: 5px;
  // 운전모드 제어 텍스트
  &__text{
    // width: 55px;
    font-size: 16px;
    line-height: 2.2;
    text-align: left;
    color: #fff;
    text-shadow: 0 0 9px #5cafff;      
    padding-left: 5px;
  }
  // 운전모드 체크박스
  .checkbox{
    position:relative;
    cursor:pointer;
    appearance:none;
    width:60px;
    height:28px;
    border-radius: 14px;
    border: solid 1px #417290;
    background-color: rgba(139, 194, 240, 0.25);
    outline:none;
    transition:0.3s;
  }
  // 운전모드 체크박스(선택 안됐을 때)
  .checkbox::before{
    content:"OFF";
    position:absolute;
    height:22px;
    width:29px;
    border-radius:11px;
    background:#b4dffa;
    top:2px;
    left:2px;
    transition:0.3s ease-in-out;
    font-size: 11px;
    font-family: KHNPHUotfR;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2;
    letter-spacing: normal;
    text-align: center;
    color: #19274e;
    background-color: #417290;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checkbox:checked::before{
    content:"AI";
    transform:translateX(25px);
    background:#b4dffa;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checkbox:checked{
    border-color:#b4dffa;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checked::before{
    content:"AI";
    transform:translateX(25px);
    background:#b4dffa;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checked{
    border-color:#b4dffa;
  }   
}
// 공정별 운영모드 제어 버튼
.btn-contents {
  display: flex;
  align-items: center;
  width: 225px;
  margin-left: 8px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  margin-bottom: 5px;
  // 운전모드 제어 텍스트
  &__text{
    width: 76px;
    font-size: 16px;
    line-height: 2.2;
    text-align: left;
    color: #c3eaff;
    text-shadow: 0 0 9px #5cafff;      
    padding-left: 5px;
  }
  // 운전모드 체크박스
  .checkbox{
    position:relative;
    cursor:pointer;
    appearance:none;
    width:60px;
    height:28px;
    border-radius: 14px;
    border: solid 1px #417290;
    background-color: rgba(139, 194, 240, 0.25);
    outline:none;
    transition:0.3s;
  }
  // 운전모드 체크박스(선택 안됐을 때)
  .checkbox::before{
    content:"OFF";
    position:absolute;
    height:22px;
    width:29px;
    border-radius:11px;
    background:#b4dffa;
    top:2px;
    left:2px;
    transition:0.3s ease-in-out;
    font-size: 11px;
    font-family: KHNPHUotfR;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 2;
    letter-spacing: normal;
    text-align: center;
    color: #19274e;
    background-color: #417290;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checkbox:checked::before{
    content:"AI";
    transform:translateX(25px);
    background:#b4dffa;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checkbox:checked{
    border-color:#b4dffa;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checked::before{
    content:"AI";
    transform:translateX(25px);
    background:#b4dffa;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checked{
    border-color:#b4dffa;
  }   
}
.part-contents {
  display: flex;
  align-items: center;
  width: 225px;
  margin-left: 8px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  margin-bottom: 5px;
  // 운전모드 제어 텍스트
  &__text{
    font-size: 17px;
    line-height: 2.2;
    text-align: left;
    color: #c3eaff;
    text-shadow: 0 0 9px #5cafff;      
    display: flex;
    justify-content:space-between;
    margin-left: 10px;
    align-items: center;
    
    &::before {
      content: "";
      background-image: url('../../assets/ss_images/ai_contents_line.png');
      width: 60px;
      height: 10px;
      display: block;
      transform: rotate(180deg);
      // margin-right: 10px;
    }
    &::after {
      content: "";
      background-image: url('../../assets/ss_images/ai_contents_line.png');
      width: 60px;
      height: 10px;
      display: block;
      // margin-left: 10px;
    }
  }
}

// EMS 컨테이너
.ems_container {
  flex: 1;
  .ems_content_container {
    width: 100%;
    padding: 10px;
    &__divider {
      top: 40px;
      left: 225px;
      position: absolute;
      width: 1px;
      height: 120px;
      background-size: 100% 100%;
      background-image: url('../../assets/dashboard_img/divider.png');
    }
    // .one {
    //   top: 40px;
    //   left: 225px;
    // }
    // .two {
    //   top: 100px;
    //   left: 225px;
    // }
    .divider {
      position: relative;
    }
    .divider:nth-of-type(1) {
      margin-bottom: 20px;
    }
    // .divider:nth-of-type(2) {
    //   margin-bottom: 0;
    // }
    // .divider::after {
    //   content: "";
    //   position: absolute;
    //   width: 376px;
    //   height: 1px;
    //   background-size: 100% 100%;
    //   margin-top: 15px;
    //   background-image: url('../../assets/dashboard_img/h-divider.png');
    // }
    // .divider:nth-of-type(2)::after {
    //   display: none;
    // }
    // EMS Contents
    .ems_contents{
      // EMS 피크관리 테이블 열
      &__td {
        display: flex;
        align-items: center;
        gap: 35px;
      }
      // EMS 피크관리 LED
      &__led {
        display: flex;
        align-items: center;
        justify-content: center;
        // LED ON
        &--on {
          width: 11px;
          height: 11px;
          box-shadow: 0 0 6px 0 rgba(92, 175, 255);
          background-color: rgba(234, 100, 100);
          border-radius: 11px;
          color: rgba(234, 100, 100);
          text-align: center;
          // Hz Text
          span {
            font-size: 13px;
            margin-top: 50px;
            text-shadow: none;
          }
        }
        // LED OFF
        &--off {
          width: 11px;
          height: 11px;
          box-shadow: 0 0 6px 0 rgba(92, 175, 255);
          background-color: rgba(180, 223, 250);
          border-radius: 11px;
          color: rgba(180, 223, 250);
          text-align: center;

          // Hz Text
          span {
            font-size: 13px;
            margin-top: 50px;
            text-shadow: none;
          }
        }
      }
    }
    // EMS 위치 텍스트
    .dashboard-info-ems-title {
      display: flex;
      align-items: center;
      text-shadow: 0 0 9px #5cafff;
      color: #fff;
      font-size: 14px;
      &__text {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 66px;
        height: 32px;
        background-image: url('../../assets/dashboard_img/dashboard_info_title_background.png');
        background-position: center;
        background-size: 100% 100%;
      }
    }
    .ems-toggle {
      width: 60px;
      margin: 0 0 0 8px;
    }
    .dashboard-info-ems {
      flex: 1;
      display:flex;
      align-items: center;
      justify-content: flex-end;
    }
    .ems_contents_info {
      display: flex;
      align-items: center;
      text-align: center;
      color: #c3eaff;
      text-shadow: 0 0 9px #5cafff;
      font-size: 14px;
      height: 60px;
      padding-left: 10px;
      &__title {
        text-align: left;
        margin-right: 25px;
        width: 52px;
        color: #fff;
      }
    }
  } 
}

// PMS 컨테이너
.pms_contents_container {
  display: flex;
  width: 100%;
  height: 180px;
  // PMS Contents
  .pump-contents{
    position: relative;
    width: 100%;
    height: 160px;
    text-shadow: 0 0 9px #5cafff;
    font-size: 12px;
    color: #c3eaff;
    // PMS 파란원 Contents
    .pump-contents-blue {
      position: absolute;
      top: 0px;
      left: 10px;
      // PMS rms & ℃ 박스
      &__box {
        width: 60px;
        padding: 1px 2px;
        border: 1px solid #c3eaff;
        text-align: center;
      }
      // PMS Box와 원을 연결하는 Line
      &__line {
        position: absolute;
        top: 24px;
        right: -25px;
        width: 19px;
        height: 42px;
        border-right: 1px dotted #c3eaff;
        border-top: 1px dotted #c3eaff;
      }
      // PMS 파란원
      &__circle {
        position: absolute;
        top: 70px;
        right: -30px;
        width: 10px;
        height: 10px;
        border-radius: 5px;
        background-color: blue;
        border: 1px solid #c3eaff;
      }
    }
    // PMS 녹색원 Contents
    .pump-contents-green {
      position: absolute;
      top: 100px;
      left: 10px;
      // PMS rms & ℃ 박스
      &__box {
        width: 60px;
        padding: 1px 2px;
        border: 1px solid #c3eaff;
        text-align: center;
      }
      // PMS Box와 원을 연결하는 Line
      &__line {
        position: absolute;
        top: -37px;
        right: -130px;
        width: 125px;
        height: 61px;
        border-bottom: 1px dotted #c3eaff;
        border-right: 1px dotted #c3eaff;
      }
      // PMS 녹색원
      &__circle {
        position: absolute;
        top: -51px;
        right: -134px;
        width: 10px;
        height: 10px;
        border-radius: 5px;
        background-color: green;
        border: 1px solid #c3eaff;
      }
    }
    // PMS 노란원 Contents
    .pump-contents-yellow {
      position: absolute;
      top: 0px;
      right: 0px;
      // PMS rms & ℃ 박스
      &__box {
        width: 60px;
        padding: 1px 2px;
        border: 1px solid #c3eaff;
        text-align: center;
      }
      // PMS Box와 원을 연결하는 Line
      &__line {
        position: absolute;
        top: 19px;
        right: 69px;
        width: 101px;
        height: 21px;
        border-top: 1px dotted #c3eaff;
        border-left: 1px dotted #c3eaff;
      }
      // PMS 노란원
      &__circle {
        position: absolute;
        top: 42px;
        right: 164px;
        width: 10px;
        height: 10px;
        border-radius: 5px;
        background-color: yellow;
        border: 1px solid #c3eaff;
      }
    }
    // PMS 빨간원 Contents
    .pump-contents-red {
      position: absolute;
      top: 100px;
      right: 0px;
      // PMS rms & ℃ 박스
      &__box {
        width: 60px;
        padding: 1px 2px;
        border: 1px solid #c3eaff;
        text-align: center;
      }
      // PMS Box와 원을 연결하는 Line
      &__line {
        position: absolute;
        top: -47px;
        right: 67px;
        width: 19px;
        height: 69px;
        border-bottom: 1px dotted #c3eaff;
        border-left: 1px dotted #c3eaff;
      }
      // PMS 빨간원
      &__circle {
        position: absolute;
        top: -60px;
        right: 81px;
        width: 10px;
        height: 10px;
        border-radius: 5px;
        background-color: red;
        border: 1px solid #c3eaff;
      }
    }
    // PMS 펌프 이미지
    &__img{
      height: 160px;
      background-image: url('../../assets/dashboard_img/pump_background.png');
      background-position: center;
      background-size: contain;
      margin-top: 10px;
    }
    // PMS 펌프 에러 이미지
    &__error-img{
      height: 140px;
      background-image: url('../../assets/dashboard_img/pump_background_error.png');
      background-size: contain;
      background-position: center;
      margin-top: 5px;
    }
  }
}

// EMS & PMS 탭 메뉴
.dashboard_info_content_tab{
  display: flex;
  margin-left: auto;
  margin-right: 20px;
  height: 34px;
  font-family: KHNPHUotfR;
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2.6;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  // 공통 탭 메뉴
  .tab{
    width: 58px;
    height: 100%;
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.25);
    cursor: pointer;
  }
  // PMS 탭 메뉴
  .tab-pms{
    display:flex;
    align-items: center;
    justify-content: center;
    width: 34px;
    height: 34px;
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.25);
    cursor: pointer;
  }
  // PMS 탭 메뉴 제목
  .tab-pms-title {
    padding: 0px 10px;
    background-image: url('../../assets/dashboard_img/dashboard_info_title_background.png');
    background-position: center;
    background-size: 110% 100%;
  }
}

// EMS & PMS 선택된 Tab Menu
.selected_ems{
  box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
  background-color: rgba(139, 194, 240, 0.4) !important;
}

</style>