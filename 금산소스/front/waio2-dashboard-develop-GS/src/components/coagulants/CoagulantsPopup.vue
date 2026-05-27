<template>
  <div v-if="this.$store.state.coagulants.popup.visible" class="main">
    <div class="popup-main">
      <div class="popup-contents">
        <div class="top">
          <div class="top__img"></div>
          <div class="top__title">원수 분류</div>
          <div class="top__exit-btn" @click="closePopup()"></div>
        </div>
        <div class="record-table">
          <table class="search-table">
            <thead class="table-title">
              <div class="row-text">No</div>
              <div class="row-text">군집명</div>
              <div class="row-text">탁도</div>
              <div class="row-text">pH</div>
              <div class="row-text">수온</div>
              <div class="row-text">전기전도도</div>
              <div class="row-text">동작</div>
            </thead >
            <div class="search-contents-container">
            <tbody>
              <template v-for="item in $store.state.coagulants.popup.class_info">
                <tr class="table-contents" :key="item.class_index">
                  <td class="record-contents-text">{{ item.class_index }}</td>
                  <td v-if="!item.isModify" class="record-contents-text">{{ item.description }}</td>
                  <td v-else class="record-contents-text"><input type="text" class="info-input-box-modify" v-model="item.description"/></td>
                  <td v-if="!item.isModify" class="record-contents-text">{{ item.tb }}</td>
                  <td v-else class="record-contents-text"><input type="text" class="info-input-box-modify" v-model="item.tb"/></td>
                  <td v-if="!item.isModify" class="record-contents-text">{{ item.ph }}</td>
                  <td v-else class="record-contents-text"><input type="text" class="info-input-box-modify" v-model="item.ph"/></td>
                  <td v-if="!item.isModify" class="record-contents-text">{{ item.te }}</td>
                  <td v-else class="record-contents-text"><input type="text" class="info-input-box-modify" v-model="item.te"/></td>
                  <td v-if="!item.isModify" class="record-contents-text">{{ item.cu }}</td>
                  <td v-else class="record-contents-text"><input type="text" class="info-input-box-modify" v-model="item.cu"/></td>
                  <td class="contents-modify">
                    <button v-if="!item.isModify" class="modify-box" @click="clickModifyBtn(item.class_index)"><img class="modify-img" src="../../assets/UserManagement/modify.png" width="16" height="15" alt="수정"> 수정 </button>
                    <button v-else class="ok-box" @click="clickOkBtn(item.class_index)"><img class="modify-img" src="../../assets/UserManagement/ok.png" width="16" height="15" alt="완료"> 완료 </button>
                  </td>
                </tr>
              </template>
            </tbody>
            </div>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { PUT_COAGULANTS_RAWWATER } from '@/store/modules/coagulants'
export default {
  data: () => ({
  }),
  methods: {
    // X 버튼 클릭시 수정 팝업을 닫음
    closePopup: function () {
      this.$store.commit('coagulants/CLOSE_POPUP')
    },
    /**
     * 수정 버튼 클릭시
     * 변경 모드로 전환한다
     * 
     * @param class_index 클래스 인덱스
     */
    clickModifyBtn: function(class_index) {
      let elem = {}
      elem.class_index = this.$store.state.coagulants.popup.class_info[class_index - 1].class_index
      elem.cu = this.$store.state.coagulants.popup.class_info[class_index - 1].cu
      elem.description = this.$store.state.coagulants.popup.class_info[class_index - 1].description
      elem.ph = this.$store.state.coagulants.popup.class_info[class_index - 1].ph
      elem.tb = this.$store.state.coagulants.popup.class_info[class_index - 1].tb
      elem.te = this.$store.state.coagulants.popup.class_info[class_index - 1].te
      elem.isModify = true
      this.$store.state.coagulants.popup.class_info[class_index - 1] = elem
      this.$store.state.coagulants.popup.class_info = this.$store.state.coagulants.popup.class_info.slice(0)
    },
    /**
     * 수정 완료 버튼 클릭시
     * 수정된 정보를 업데이트하는 API를 요청한다
     * 
     * @param class_index 클래스 인덱스
     */
    clickOkBtn: function(class_index) {
      this.$store.state.coagulants.popup.class_info.map(it => {
        if (it.class_index === class_index) {
          it.isModify = false
        }
      })
      this.$store.state.coagulants.class_info = this.$store.state.coagulants.popup.class_info
      let elem = this.$store.state.coagulants.class_info[class_index - 1]
      this.$store.dispatch(PUT_COAGULANTS_RAWWATER, elem)
    }
  }
}
</script>
<style lang="scss" scoped>
.main{
  position: absolute;
  top: -85px;
  z-index: 200;
  width: 1920px;
  height: 1080px;
  display: flex;
  justify-content: center;
  align-items: center;
  // background-image: url('../../assets/disinfection/disinfection_background.png');
  background-color: rgba(30,37,61,0.8);
  .popup-main{
    display: flex;
    width: 1175px;
    height: 893px;
    justify-content: center;
    align-items: center;
    background-image: url('../../assets/sedimentation/popup_main.png');
    .popup-contents{
      width: 1163px;
      height: 813px;
      background-image: url('../../assets/sedimentation/popup_contents.png');
      background-position-x: -5px;
      padding: 31px 24.4px 40px 33.7px;
      .chart-container{
        display: flex;
        width: 100%;
        margin-top: 10px;
        height: 325px;
        .chart-box{
          width: 50%;
          height: 100%;
          .chart{
            width: 100%;
            height: 272px;
          }
          .chart-title{
            width: 223px;
            height: 53px;
            background-image: url('../../assets/sedimentation/bottom_title_img.png');
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 2.7;
            letter-spacing: normal;
            text-align: left;
            color: #fff;
            padding-left: 40px;
          }
        }
      }
      .contents{
        display: flex;
        width: 100%;
        height: 277px;
        margin-top: 15px;
        .contents-img{
          position: absolute;
          width: 912px;
          height: 277px;
          left: 574px;
          background-image: url('../../assets/sedimentation/ji_contents.png');
          // clip-path: polygon(0% 0%, 0% 100%, 87% 100%, 87% 84%, 103% 32%, 100% 0%);
          .water-h-flow-img{
            position: absolute;
            width: 136px;
            height: 8px;
            top: 230px;
            .buble {
              position: absolute;
              width: 136px;
              height: 8px;
              background-image: url('../../assets/splashdown/water_h_flow.png');
              background-position: -35px 50%;
              animation-name: arrow-one;
              animation-duration: 5s;
              animation-timing-function: linear;
              animation-iteration-count: infinite;
              opacity: 0;
            }
            .delay1 {
              animation-delay: 0s;
            }
            .delay2 {
              animation-delay: 1s;
            }
            .delay3 {
              animation-delay: 2s;
            }
          }
          @keyframes arrow-one{ 
            0% {opacity:0; transform: translateX(0px);}
            50% {opacity:1; }
            90% {opacity:1; }
            100% {opacity:0; transform: translateX(150px);}
          }
          .contents-img-box{
            width: 582px;
            height: 150px;
            position: absolute;
            top: 63px;
            right: 0px;
            &__top{
              width: 113px;
              height: 69px;
              // margin-left: 75px;
              position: relative;
              top: 10px;
              background-image: url('../../assets/sedimentation/right_top_contents.png');
            }
            &__bottom{
              width: 134px;
              height: 78px;
              margin-left: 377px;
              background-image: url('../../assets/sedimentation/left_top_contents.png');
            }
          }
          .four{
            top: 119px;
            left: 303px;
          }
          .three{
            top: 141px;
            left: 276px;
          }
          .two{
            top: 165px;
            left: 249px;
          }
          .one{
            top: 187px;
            left: 222px;
          }
          .line{
            position: absolute;
            justify-content: center;
            align-items: center;
            display: flex;
            flex-flow: column;
            &__num{
              width: 27px;
              height: 27px;
              background-image: url('../../assets/sedimentation/line_arrow.png');
              font-size: 12px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              line-height: 2.2;
              letter-spacing: normal;
              text-align: center;
              color: #fff;
            }
            &__status{
              width: 45px;
              height: 35px;
            }
            .on{
              background-image: url('../../assets/sedimentation/line_on.png');
            }
            .off{
              background-image: url('../../assets/sedimentation/line_off.png');
            }
          }
          .contents-box{
            display: flex;
            width: 425px;
            position: absolute;
            left: 464px;
            padding-top: 6px;
            .marginsideauto{
                margin: 0 auto;
            }
            .box-contents{
              &__text{
                text-shadow: 0 0 9px #5cafff;
                font-size: 16px;
                font-weight: normal;
                font-stretch: normal;
                font-style: normal;
                line-height: 1.56;
                letter-spacing: normal;
                text-align: center;
                color: #fff;
              }
              &__box{
                display: flex;
                justify-content: center;
                width: 131px;
                height: 43px;
                margin-bottom: 21px;
                border: solid 1px rgba(157, 191, 255, 0.3);
                .width50{
                  width: 50%;
                }
                .box-text{
                  text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
                  font-family: "LAB디지털" !important;
                  font-size: 24px;
                  font-weight: normal;
                  font-stretch: normal;
                  font-style: normal;
                  line-height: 1.8;
                  letter-spacing: normal;
                  text-align: center;
                  color: #ccf1ff;
                }
                .box-unit{
                  width: 50%;
                  font-size: 16px;
                  font-weight: normal;
                  font-stretch: normal;
                  font-style: normal;
                  line-height: 2.7;
                  letter-spacing: normal;
                  text-align: center;
                  color: #417db9;
                }
              }
            }
          }
        }
        .contents-value{
          height: 100%;
          margin-right:31px;
          &__text{
            text-shadow: 0 0 9px #5cafff;
            font-size: 16px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 1.56;
            letter-spacing: normal;
            text-align: center;
            color: #fff;
          }
          &__box{
            display: flex;
            justify-content: center;
            width: 131px;
            height: 43px;
            margin-bottom: 21px;
            border: solid 1px rgba(157, 191, 255, 0.3);
            .width50{
              width: 50%;
            }
            .box-text{
              text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
              font-family: "LAB디지털" !important;
              font-size: 24px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              line-height: 1.8;
              letter-spacing: normal;
              text-align: center;
              color: #ccf1ff;
            }
            .box-unit{
              width: 50%;
              font-size: 16px;
              font-weight: normal;
              font-stretch: normal;
              font-style: normal;
              line-height: 2.7;
              letter-spacing: normal;
              text-align: center;
              color: #417db9;
            }
          }
        }
      }
      .information{
        display: flex;
        width: 100%;
        height: 67px;
        align-items: center;
        margin-top: 18px;
        padding-right: 32px;
        .top-title{
          width: 113px;
          height: 53px;
          padding-left: 30px;
          background-image: url('../../assets/sedimentation/information_title.png');
          text-shadow: 0 0 9px #5cafff;
          font-size: 18px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 2.8;
          letter-spacing: normal;
          text-align: left;
          color: #fff;
        }
        .top-timer-text{
          text-shadow: 0 0 9px #5cafff;
          font-size: 21px;
          font-weight: normal;
          font-stretch: normal;
          font-style: normal;
          line-height: 1.1;
          letter-spacing: normal;
          text-align: left;
          color: #c3eaff;
          margin-left: auto;
        }
        .top-timer{
          display: flex;
          justify-content: center;
          width: 272.9px;
          height: 66.2px;
          padding-top: 10px;
          margin-left: 16px;
          background-image: linear-gradient(91deg, rgba(67, 109, 132, 0) 0%, #2d67a2 54%, rgba(68, 97, 137, 0) 100%);
          &__num{
            text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
            font-family: "LAB디지털" !important;
            font-size: 40px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 1.2;
            letter-spacing: normal;
            text-align: center;
            color: #ccf1ff;
            margin-left:5px;
          }
          &__text{
            font-size: 16px;
            font-weight: normal;
            font-stretch: normal;
            font-style: normal;
            line-height: 4.13;
            letter-spacing: normal;
            text-align: left;
            color: #c3eaff;
            margin-left:3px;
          }
        }
      }
      .top{
        display: flex;
        width: 100%;
        height: 30px;
        &__img{
          width: 19px;
          height: 30px;
          background-image: url('../../assets/sedimentation/top_title_img.png');
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
          background-image: url('../../assets/sedimentation/exit_btn.png');
          background-position-y: center;
          cursor: pointer;
        }
      }
    }
  }
}
.record-table {
  width: 1100px;
  height: 879px;
  margin-top: 11.5px;
  padding: 14px 20px 16px 17px;
}
.table-title {
  font-size: 15px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: rgba(255, 255, 255, 0.85);
  vertical-align: middle;
  width: 100%;
  // justify-content: center;
  background-image: linear-gradient(to right, rgba(16, 36, 65, 0) 0%, rgba(39, 86, 162, 0.93) 14%, rgba(18, 43, 92, 0.96) 49%, rgba(39, 86, 162, 0.93) 85%, rgba(16, 36, 65, 0) 100%);
}
.table-contents:nth-child(even) {
  display: inline-flex;
  width: 1100px;
  height: 60px;
  /* margin: 8px 0 0 0; */
  font-size: 13px;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  vertical-align: middle;
  /* background-color: #0f2755; */
  background-image: linear-gradient(to right, rgba(66, 144, 221, 0), rgba(66, 144, 221, 0.15) 16%, rgba(66, 144, 221, 0.15) 87%, rgba(66, 144, 221, 0));
}
.table-contents:nth-child(odd){
  display: inline-flex;
  width: 1100px;
  height: 60px;
  /* margin: 8px 0 0 0; */
  font-size: 13px;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  vertical-align: middle;
  /* background-color: #0f2755; */
  background-image: linear-gradient(to right, rgba(9, 76, 181, 0) 3%, rgba(9, 76, 181, 0.15) 21%, rgba(9, 76, 181, 0.15) 82%, rgba(9, 76, 181, 0) 98%);
}
.search-table {
  border-spacing: 0px 4px;
  border-collapse: separate;
  /* padding: 15px 20px 0 14px; */
}
.row-text {
  padding-top: 15px;
  /* margin-right: 4px; */
  height: 48px;
  text-shadow: 0 0 9px #5cafff;
  display: inline-block;
  /* background-color: #0e3283; */
  &:nth-child(n) {
    width: 157px;
  }
  &:nth-child(1) {
    width: 57px;
  }
  &:nth-child(2) {
    width: 257px;
  }
}
.contents-text {
  padding-top: 16px;
  width:456px;
  height: 55px;
}
.record-contents-text {
  padding-top: 19px;
  // width:200px;
  height: 60px;
  text-shadow: 0 0 9px #5cafff;
  &:nth-child(n) {
    width: 157px;
  }
  &:nth-child(1) {
    width: 57px;
  }
  &:nth-child(2) {
    width: 257px;
  }
}
.contents-modify {
  display:flex;
  align-items: center;
  justify-content: center;
  width: 157px;
  height: 55px;
  /* padding-top: 10px; */
}
.modify-box {
  /* width: 100px; */
  /* height: 34px; */
  margin: 0px 5px;
  padding: 9px 10px;
  /* background-color: #8888fc; */
  background-color: #4486de;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  text-shadow: 0 0 9px #5cafff;
  color: #fff;
}
.ok-box {
  /* width: 100px; */
  /* height: 34px; */
  margin: 0px 5px;
  padding: 9px 10px;
  /* background-color: #8888fc; */
  background-color: #5cafff;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  text-shadow: 0 0 9px #5cafff;
  color: #fff;
}
// .modify-box:hover {
//   box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
//   background-color: #83b8ff;
// }
.modify-img {
  vertical-align: middle;
  margin-right: 4px;
}
.info-input-box-modify {
  width: 95%;
  height: 34px;
  border-bottom: solid 1px #fff;
  // font-size: 16px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: center;
  text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
  // font-family: "LAB디지털" !important;
  color: #fff;
  text-shadow: 0 0 9px #5cafff;
  padding-left: 11px;
}
</style>