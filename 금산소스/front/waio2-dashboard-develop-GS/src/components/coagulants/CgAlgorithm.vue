<template>
  <div class="main">
    <!-- Top 네비게이터 -->
    <div class="top-center">
      <div class="top-center__contents">
        <TopNavigator />
      </div>
    </div>
    <!-- Top 제목, 운전 모드 제어 -->
    <div class="top">
      <div class="title">
        약품
        <p>세부 현황</p>
      </div>
      <div class="right">
        <div class="right-contents">
          <div class="right-contents__text-first">AI 운전 모드</div>
          <div class="right-contents__btn-first">
            <div class="btn-contents__btn">
              <div class="control_box_operation">
                <div
                  v-if="$store.state.coagulants.latest.ai_opr === 2"
                  class="control_box_operation__btn control_box_operation__btn--on"
                >
                  AI
                </div>
                <div
                  v-else
                  class="control_box_operation__btn control_box_operation__btn--off"
                  @click="onClickAICheckbox(2, 2)"
                >
                  AI
                </div>
                <div
                  v-if="$store.state.coagulants.latest.ai_opr === 1"
                  class="control_box_operation__btn control_box_operation__btn--on"
                >
                  AI추천
                </div>
                <div
                  v-else
                  class="control_box_operation__btn control_box_operation__btn--off"
                  @click="onClickAICheckbox(2, 1)"
                >
                  AI추천
                </div>
                <div
                  v-if="$store.state.coagulants.latest.ai_opr === 0"
                  class="control_box_operation__btn control_box_operation__btn--on"
                >
                  AI분석
                </div>
                <div
                  v-else
                  class="control_box_operation__btn control_box_operation__btn--off"
                  @click="onClickAICheckbox(2, 0)"
                >
                  AI분석
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 주입률 상/하한 -->
        <div class="injection-rate">
          <div class="apac">
            <div class="title-container">
              <span class="injection-rate__text--title">응집제 주입기</span>
              <span class="injection-rate__text--degree">( ppm )</span>
            </div>
            <div class="upper-limit">
              <div class="injection-rate__text--char">상한</div>
              <div
                class="injection-rate__input-box"
                :class="{ '': this.$store.state.coagulants.isModifyMode }"
              >
                <input
                  v-if="this.$store.state.coagulants.isModifyMode"
                  type="text"
                  :value="this.$store.state.coagulants.latestModify.c_cf_max"
                  v-on:input="updateInput($event, 'c_cf_max')"
                />
                <span class="injection-rate__text--num" v-else>{{
                  this.$store.state.coagulants.latestModify.c_cf_max
                    | numFormat("0.00")
                }}</span>
              </div>
            </div>
            <div class="lower-limit">
              <div class="injection-rate__text--char">하한</div>
              <div
                class="injection-rate__input-box"
                :class="{ '': this.$store.state.coagulants.isModifyMode }"
              >
                <input
                  v-if="this.$store.state.coagulants.isModifyMode"
                  type="text"
                  :value="this.$store.state.coagulants.latestModify.c_cf_min"
                  v-on:input="updateInput($event, 'c_cf_min')"
                />
                <span class="injection-rate__text--num" v-else>{{
                  this.$store.state.coagulants.latestModify.c_cf_min
                    | numFormat("0.00")
                }}</span>
              </div>
            </div>
          </div>
          <div
            v-if="$store.state.login.user.tkn !== null"
            class="modify-button"
          >
            <div class="btn-bg" @click="updateControl">
              <div
                :class="[
                  this.$store.state.coagulants.isModifyMode
                    ? 'custom-icon__checkbox'
                    : 'custom-icon__pencil',
                ]"
              ></div>
            </div>
            <div
              v-if="this.$store.state.coagulants.isModifyMode"
              class="custom-cancel-icon"
              style="margin-top: 8px;"
              @click="cancelControl"
            >
              <div class="custom-cancel-icon__cancel"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="contents">
      <!-- 왼쪽 약품 공정 이미지 -->
      <div class="contents__left">
        <CoagulantsLeftContents />
      </div>
      <!-- 오른쪽 주요인자, 알고리즘 예측, 예측결과, 침전지 탁도 정규 분포 그래프, 침전지 탁도 트랜드 -->
      <div class="contents__right">
        <CoagulantsRightContents />
      </div>
    </div>
    <!-- 원수 분류 팝업 -->
    <CoagulantsPopup />
  </div>
</template>
<script>
import CoagulantsLeftContents from "@/components/coagulants/CoagulantsLeftContents";
import CoagulantsRightContents from "@/components/coagulants/CoagulantsRightContents";
import CoagulantsPopup from "@/components/coagulants/CoagulantsPopup";
import { SET_OVERLAY } from "@/store";
import {
  GET_COAGULANT_LATEST,
  PUT_COAGULANT_CONTROL_AI,
} from "@/store/modules/coagulants";
import {
  GET_SEDIMENTATION_DISTRIBUTION_TB,
  PUT_SEDIMENTATION_HISTORY_TB,
} from "@/store/modules/sedimentation";
import TopNavigator from "@/components/core/TopNavigator";
import { OPEN_AI_MODE_DIALOG } from "@/store/modules/dialog";
export default {
  name: "CgAlgorithm",
  data: () => ({
    intervalTime: 60 * 1000, // API 요청 주기
  }),
  components: {
    CoagulantsLeftContents,
    CoagulantsRightContents,
    CoagulantsPopup,
    TopNavigator,
  },
  methods: {
    /**
     * AI 운전모드 클릭시
     * 운전모드 변경 Dialog 오픈
     *
     * @param index 공정index
     * @expectedValue 변경할 운전모드
     */
    onClickAICheckbox: function(index, expectedValue) {
      if (this.$store.state.login.user.tkn !== null) {
        this.$store.state.selectedBuildingIndex = index;
        this.$store.dispatch("dialog/" + OPEN_AI_MODE_DIALOG, expectedValue);
      }
    },

    updateInput: function(event, key) {
      this.$store.state.coagulants.latestModify[key] = event.target.value;
    },

    updateControl: function() {
      let rate_min = 0;
      let rate_max = 200;
      if (this.$store.state.coagulants.isModifyMode) {
        if (
          this.$store.state.coagulants.latestModify.c_cf_max === "" ||
          this.$store.state.coagulants.latestModify.c_cf_min === ""
        ) {
          this.$store.dispatch("alertDialog/OPEN_DIALOG", {
            title: "경고",
            text1: "값을 입력해주세요",
          });
        } else if (
          parseFloat(this.$store.state.coagulants.latestModify.c_cf_max) <
            rate_min ||
          parseFloat(this.$store.state.coagulants.latestModify.c_cf_max) >
            rate_max
        ) {
          this.$store.dispatch("alertDialog/OPEN_DIALOG", {
            title: "경고",
            text1: "주입률 설정 범위",
            text2: rate_min + " ~ " + rate_max,
          });
        } else if (
          parseFloat(this.$store.state.coagulants.latestModify.c_cf_min) <
            rate_min ||
          parseFloat(this.$store.state.coagulants.latestModify.c_cf_min) >
            rate_max
        ) {
          this.$store.dispatch("alertDialog/OPEN_DIALOG", {
            title: "경고",
            text1: "주입률 설정 범위",
            text2: rate_min + " ~ " + rate_max,
          });
        } else {
          let obj = {};
          obj.c_cf_max = parseFloat(
            this.$store.state.coagulants.latestModify.c_cf_max
          ).toFixed(2);
          obj.c_cf_min = parseFloat(
            this.$store.state.coagulants.latestModify.c_cf_min
          ).toFixed(2);
          this.$store.dispatch(PUT_COAGULANT_CONTROL_AI, obj);
          this.$store.state.coagulants.isModifyMode = !this.$store.state
            .coagulants.isModifyMode;
          this.$store.state.coagulants.latest.c_cf_max = parseFloat(
            this.$store.state.coagulants.latestModify.c_cf_max
          ).toFixed(2);
          this.$store.state.coagulants.latest.c_cf_min = parseFloat(
            this.$store.state.coagulants.latestModify.c_cf_min
          ).toFixed(2);
        }
      } else {
        this.$store.state.coagulants.isModifyMode = !this.$store.state
          .coagulants.isModifyMode;
      }
    },

    cancelControl: function() {
      this.$store.state.coagulants.latestModify = Object.assign(
        {},
        this.$store.state.coagulants.latest
      );
      this.$store.state.coagulants.isModifyMode = !this.$store.state.coagulants
        .isModifyMode;
    },
  },
  /**
   * 마운트시 실행되는 함수
   * 약품 공정에 필요한 API를 주기적으로 요청
   */
  mounted: function() {
    this.$store.state.selectedBuildingIndex = 2;
    this.$store.commit(SET_OVERLAY, true);
    Promise.all([
      this.$store.dispatch(GET_COAGULANT_LATEST),
      // this.$store.dispatch(GET_COAGULANTS_CLUSTER),
      this.$store.dispatch(GET_SEDIMENTATION_DISTRIBUTION_TB),
      this.$store.dispatch(PUT_SEDIMENTATION_HISTORY_TB),
    ]).finally(() => {
      this.$store.commit(SET_OVERLAY, false);
    });

    this.timer = setInterval(() => {
      this.$store.dispatch(GET_COAGULANT_LATEST),
        // this.$store.dispatch(GET_COAGULANTS_CLUSTER),
        this.$store.dispatch(PUT_SEDIMENTATION_HISTORY_TB);
    }, this.intervalTime);
  },
  // 마운트 해제시 API 요청 타이머 해제
  destroyed: function() {
    clearInterval(this.timer);
  },
};
</script>
<style lang="scss" scoped>
.bottom {
  display: flex;
  width: 100%;
  height: 392px;
}
.contents {
  display: flex;
  width: 100%;
  height: 870px;
  padding: 0 40px;
}
.top-center {
  display: flex;
  justify-content: center;
  position: absolute;
  top: -76px;
  left: 159px;
  width: 1585px;
  height: 249px;
  background-image: url("../../assets/splashdown/top_center_background.png");
  .timer {
    width: 72px;
    height: 72px;
    margin-top: 110px;
    margin-right: 14px;
  }
  &__contents {
    margin-top: 100px;
  }
}
.top {
  display: flex;
  width: 100%;
  height: 173px;
  .title {
    width: 230px;
    background-position: 68px 25px;
    background-image: url("../../assets/ca_images/main_title.png");
    letter-spacing: 0 !important;
    text-align: left;
    line-height: 1.5;
    font-weight: 600;
    height: 100%;
    text-shadow: 0 0 9px #5cafff;
    font-family: "KHNPHUotfR";
    font-size: 30px !important;
    font-stretch: normal;
    font-style: normal;
    color: #fff;
    margin-left: 55px;
    margin-top: 35px;
  }
  p {
    font-size: 24px;
    font-weight: 300;
  }
}
.injection-rate {
  position: relative;
  display: flex;
  justify-content: center;
  flex-direction: row;
  width: 100%;
  margin: 10px 0 0 0;
  &__input-box {
    > input {
      width: 80px;
      height: 20px;
      margin-right: 4px;
      padding-right: 5px;
      border: solid 1px rgba(157, 191, 255, 0.3);
      outline: none;
      // font-family: "LAB디지털" !important;
      text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
      color: #ccf1ff;
      font-size: 16px;
      text-align: right;
    }
  }
  &__text {
    font-family: KHNPHUotfR;
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    letter-spacing: normal;
    text-align: left;
    &--title {
      text-shadow: 0 0 9px #5cafff;
      line-height: 1.07;
      color: #c3eaff;
    }
    &--char {
      text-shadow: 0 0 9px #5cafff;
      line-height: 1.07;
      color: #fff;
    }
    &--num {
      padding: 0 5px 0 0;
      text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
      // font-family: "LAB디지털" !important;
      font-size: 16px;
      line-height: 1.19;
      text-align: right;
      color: #ccf1ff;
    }
    &--degree {
      padding: 0 0 0 5px;
      font-size: 10px;
      line-height: 3.4;
      text-align: left;
      color: #417db9;
    }
  }
  .title-container {
    height: 20px;
  }
  .apac {
    display: flex;
    flex-direction: column;
  }
  .polymax {
    display: flex;
    flex-direction: column;
    padding: 0 0 0 25px;
  }
  .upper-limit {
    width: 120px;
    height: 35px;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 15px 0 0 0;
  }
  .lower-limit {
    width: 120px;
    height: 30px;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }
  .modify-button {
    position: absolute;
    top: 26px;
    right: 10px;
    cursor: pointer;
  }
  .btn-bg {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 24px;
    height: 24px;
    border-radius: 12px;
    background-color: #b4dffa;
  }
}
</style>
