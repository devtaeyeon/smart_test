<template>
    <div v-if="this.$store.state.dialog.aiFilterNGACSchedule.visible" class="main-outter">
      <div class="popup-main">
        <div class="popup-contents">
          <!-- <div class="top"> -->
            <!-- <div class="top__img"></div>
            <div class="top__title">여과 / GAC 연과 운영 스케줄</div> -->
          <div class="exit-btn" @click="closePopup()"></div>
          <!-- </div> -->
          <div class="mid">
            <div class="table-schedule">
              <div class="table-schedule--title">
                여과 운영 스케줄
              </div>
              <div class="table-schedule--th">
                <!-- <div class="table-schedule--line table-schedule--line__one">
                  1계열
                </div>
                <div class="table-schedule--line table-schedule--line__two">
                  2계열
                </div> -->
              </div>
              <div class="table-schedule--th" style="margin-bottom: 10px;">
                <div class="table-schedule--ji">
                  <div>1지</div>
                  <div>2지</div>
                  <div>3지</div>
                  <div>4지</div>
                  <div>5지</div>
                  <div>6지</div>
                  <div>7지</div>
                  <div>8지</div>
                </div>
              </div>
              <div class="table-schedule--tbody">
                <div v-for="(item, index) in new Array(19)" :key="index" class="table-schedule--td">
                  <div :class="getClassByLoad(getFilterStatusByNumJiAndIndex(1, index).key).className">{{ getFilterStatusByNumJiAndIndex(1, index).key.split(' ')[1] }}</div>
                  <div><div class="circle-icon" :class="getFilterStatusByNumJiAndIndex(1, index).className"></div></div>
                  <div><div class="circle-icon" :class="getFilterStatusByNumJiAndIndex(2, index).className"></div></div>
                  <div><div class="circle-icon" :class="getFilterStatusByNumJiAndIndex(3, index).className"></div></div>
                  <div><div class="circle-icon" :class="getFilterStatusByNumJiAndIndex(4, index).className"></div></div>
                  <div><div class="circle-icon" :class="getFilterStatusByNumJiAndIndex(5, index).className"></div></div>
                  <div><div class="circle-icon" :class="getFilterStatusByNumJiAndIndex(6, index).className"></div></div>
                  <div><div class="circle-icon" :class="getFilterStatusByNumJiAndIndex(7, index).className"></div></div>
                  <div><div class="circle-icon" :class="getFilterStatusByNumJiAndIndex(8, index).className"></div></div>
                </div>
              </div>
              <div class="bottom">
                <div class="bottom--legend-box">
                  <div class="bottom--legend-title">여과</div>
                  <div class="bottom--legend-contents">
                    <div><div class="circle-icon circle-icon__1"></div>여과중</div>
                    <div><div class="circle-icon circle-icon__2"></div>역세대기중</div>
                    <div><div class="circle-icon circle-icon__3"></div>역세중</div>
                    <div><div class="circle-icon circle-icon__4"></div>여과대기중</div>
                    <div><div class="circle-icon circle-icon__5"></div>시동방수중</div>
                    <div><div class="circle-icon circle-icon__6"></div>운휴중</div>
                    <!-- <div><div class="circle-icon circle-icon__7"></div>시동방수중</div>
                    <div><div class="circle-icon circle-icon__8"></div>예외상황</div> -->
                  </div>
                </div>
              </div>
            </div>
            <!-- <div class="table-schedule" style="margin-left:0px;">
              <div class="table-schedule--title">
                GAC 여과 운영 스케줄
              </div>
              <div class="table-schedule--th">
                <div class="table-schedule--line table-schedule--line__one">
                  1계열
                </div>
                <div class="table-schedule--line table-schedule--line__two">
                  2계열
                </div>
              </div>
              <div class="table-schedule--th" style="margin-bottom: 10px;">
                <div class="table-schedule--ji">
                  <div>5지</div>
                  <div>7지</div>
                  <div>9지</div>
                  <div>11지</div>
                  <div>13지</div>
                  <div>15지</div>
                  <div>17지</div>
                  <div>19지</div>
                  <div>8지</div>
                  <div>10지</div>
                  <div>12지</div>
                  <div>14지</div>
                  <div>16지</div>
                  <div>18지</div>
                </div>
              </div>
              <div class="table-schedule--tbody">
                <div v-for="(item, index) in new Array(19)" :key="index" class="table-schedule--td">
                  <div :class="getClassByLoad(getGACFilterStatusByNumJiAndIndex(5, index).key).className">{{ getGACFilterStatusByNumJiAndIndex(5, index).key.split(' ')[1] }}</div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(5, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(7, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(9, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(11, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(13, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(15, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(17, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(6, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(8, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(10, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(12, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(14, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(16, index).className"></div></div>
                  <div><div class="circle-icon" :class="getGACFilterStatusByNumJiAndIndex(18, index).className"></div></div>
                </div>
              </div>
              <div class="bottom">
                <div class="bottom--legend-box">
                  <div class="bottom--legend-title">GAC</div>
                  <div class="bottom--legend-contents">
                    <div><div class="circle-icon circle-icon__1"></div>여과중</div>
                    <div><div class="circle-icon circle-icon__2"></div>휴지중</div>
                    <div><div class="circle-icon circle-icon__3"></div>역세중</div>
                    <div><div class="circle-icon circle-icon__4"></div>역세 후 휴지</div>
                  </div>
                </div>
              </div>
            </div> -->
          </div>
        </div>        
      </div>
    </div>
</template>
<script>
import { CLOSE_AI_FILTER_AND_GAC_SCHEDULE_POPUP } from '@/store/modules/dialog'
import { PLOT_BANDS_WINTER, PLOT_BANDS_SUMMER, PLOT_BANDS_SPRING, SPRING, SUMMER, WINTER } from '@/store'
export default {
  data: () => ({
  }),
  computed: {
    classObject: function(numJi, index) {
      console.log(numJi, index)
      let className = 'circle-icon__' + this.getFilterStatusByNumJiAndIndex(numJi, index)
      let obj = {} 
      obj[className] = true
      return obj
    }
  },
  methods: {
    getFilterStatusByNumJiAndIndex: function(numJi, index) {
      if (this.$store.state.filter.schedule.filter['location' + numJi] !== null && Object.keys(this.$store.state.filter.schedule.filter['location' + numJi]).length > 0) {
        let key = Object.keys(this.$store.state.filter.schedule.filter['location' + numJi])[index]
        return {'key': key, 'className': 'circle-icon__' + this.$store.state.filter.schedule.filter['location' + numJi][key]}
      }
      return {'key': null, 'className': 'circle-icon__' + 0}
    },
    // getGACFilterStatusByNumJiAndIndex: function(numJi, index) {
    //   if (this.$store.state.filter.schedule.gac['location' + numJi] !== null && Object.keys(this.$store.state.filter.schedule.gac['location' + numJi]).length > 0) {
    //     let key = Object.keys(this.$store.state.filter.schedule.gac['location' + numJi])[index]
    //     return {'key': key, 'className': 'circle-icon__' + this.$store.state.filter.schedule.gac['location' + numJi][key]}
    //   }
    //   return {'key': null, 'className': 'circle-icon__' + 0}
    // },
    getClassByLoad: function(timeStr) {
      let dateStart = new Date(timeStr)
      let hours = dateStart.getHours()
      let season = this.getSeason(dateStart)
      let classObj = { 'className': '' }
      switch(season) {
        case SPRING:
          PLOT_BANDS_SPRING.map((it) => {
            if(it.from <= hours && it.to > hours) {
              if (it.status === 'mid') {
                classObj = { 'className': 'load-mid' }
              } else if (it.status === 'high'){
                classObj = { 'className': 'load-high' }
              }
            }
          })
          break
        case SUMMER:
          PLOT_BANDS_SUMMER.map((it) => {
            if(it.from <= hours && it.to > hours) {
              if (it.status === 'mid') {
                classObj = { 'className': 'load-mid' }
              } else if (it.status === 'high'){
                classObj = { 'className': 'load-high' }
              }
            }
          })
          break
        case WINTER:
          PLOT_BANDS_WINTER.map((it) => {
            if(it.from <= hours && it.to > hours) {
              if (it.status === 'mid') {
                classObj = { 'className': 'load-mid' }
              } else if (it.status === 'high'){
                classObj = { 'className': 'load-high' }
              }
            }
          })
          break
      }
      return classObj
    },
    closePopup: function () {
      this.$store.commit('dialog/' + CLOSE_AI_FILTER_AND_GAC_SCHEDULE_POPUP)
    },
    getSeason: function(date) {
      if ([3, 4, 5, 9, 10].includes(date.getMonth() + 1)) {
        return SPRING
      } else if ([6, 7, 8].includes(date.getMonth() + 1)) {
        return SUMMER
      } else {
        return WINTER
      }
    }
  },
  destroyed: function () {
  },
  watch: {
  }
}
</script>
<style lang="scss" scoped>
.load-mid {
  background-color: rgba(126, 110, 250, 0.4);
}
.load-high {
  background-color: rgba(212, 110, 250, 0.4);
}
.main-outter{
  position: absolute;
  top: -84px;
  left: 0px;
  z-index: 200;
  width: 1920px;
  height: 1080px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(30,37,61,0.8);
  .popup-main{
    display: flex;
    width: 870px;
    height: 1070px;
    justify-content: center;
    align-items: center;
    background-image: linear-gradient(to bottom, rgba(33, 74, 144, 0), rgba(15, 50, 111, 0.9) 17%, rgba(15, 50, 111, 0.9) 86%, rgba(33, 74, 144, 0));
    .popup-contents{
      position: relative;
      display: flex;
      flex-direction: column;
      width: 870px;
      height: 1024px;
      background-image: url('../../assets/ss_images/background_filter_schedule.png');
      // background-size: 100% 100%;
      // background-position-x: -5px;
      // padding: 31px 24.4px 40px 33.7px;
      .exit-btn{
        position: absolute;
        top: 20px;
        right: 20px;
        width: 24px;
        height: 30px;
        background-image: url('../../assets/sedimentation/exit_btn.png');
        background-position-y: center;
        cursor: pointer;
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
          margin-left: 20px;
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
      .mid {
        display: flex;
        // flex: 1;
        // background-color: white;
        .table-schedule {
          width: 824px;
          display: flex;
          flex-direction: column;
          align-items: center;
          margin-left: 20px;
          &--title {
            width: 297px;
            height: 53px;
            display: flex;
            justify-content: center;
            align-items: center;
            text-shadow: 0 0 9px #5cafff;
            font-size: 20px;
            color: #fff;
            background-image: url('../../assets/dialog/filter/background_title.png');
            margin: 30px 0px 20px 0px;
            // margin-left: 90px;
          }
          &--th {
            display: flex;
            width: 824px;
            align-items: center;
            justify-content: center;
            margin-left: 101px;
          }
          &--line {
            width: 350px;
            height: 31px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-shadow: 0 0 9px #5cafff;
            font-size: 14px;
            color: #c3eaff;
            &__one {
              background-image: linear-gradient(to right, rgba(46, 66, 137, 0) 0%, #2e4289 54%, rgba(65, 88, 170, 0.5) 100%);
            }
            &__two {
              background-image: linear-gradient(to right, rgba(49, 115, 150, 0.5) 0%, #124b69 54%, rgba(18, 75, 105, 0) 100%);
            }
          }
          &--ji {
            width: 765px;
            height: 46px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-shadow: 0 0 9px #5cafff;
            color: #c3eaff;
            background-image: linear-gradient(to right, rgba(16, 36, 65, 0) 0%, #0f2953 14%, rgba(15, 41, 83, 0.93) 85%, rgba(16, 36, 65, 0) 100%);
            > div {
              width: 78px;
              text-align: center;
            }
          }
          &--tbody {            
            display: flex;
            flex-direction: column;
            width: 824px;
            height: 736px;
            background-image: url('../../assets/ss_images/background_tbody.png');
            background-size: 100% 100%;
          }
          &--td {
            position: relative;
            top: -9px;
            display: flex;
            text-shadow: 0 0 9px #5cafff;
            font-size: 16px;
            color: #fff;
            > div {
              display: flex;
              align-items: center;
              justify-content: center;
              width: 78px;
              height: 39px;
              text-align: center;
            }
            > div:nth-child(1) {
              width: 152px;
            }
          }
        }
      }
      .bottom {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 800px;
        height: 56px;
        // margin-left: 20px;
        &--legend-box {
          display: flex;
          align-items: center;
          width: 800px;
          height: 56px;
          background-image: url('../../assets/dialog/background_filter_bottom.png');
          background-size: 100% 100%;
          margin-top: 20px;
        }
        &--legend-title {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 106px;
          height: 52px;
          background-image: url('../../assets/dialog/background_filter_legend_title.png');
          font-size: 18px;
          text-shadow: 0 0 9px #5cafff;
          color: #fff;
        }
        &--legend-contents {
          display: flex;
          flex: 1;
          align-items: center;
          justify-content: center;
          > div {
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0px 8px;
            font-size: 14px;
            color: white;
            > div {
              margin-right: 5px;
            }
          }
        }
      }
    }
  }
}
.circle-icon {
  width: 12px;
  height: 12px;
  border-radius: 12px;
  &__1 {
    box-shadow: 0 0 6px 0 #d3fff1;
    background-color: #a246ff;
  }
  &__2 {
    box-shadow: 0 0 6px 0 #d3fff1;
    background-color: #2f3c4b;
  }
  &__3 {
    box-shadow: 0 0 6px 0 #d3fff1;
    background-color: #8098ff;
  }
  &__4 {
    box-shadow: 0 0 6px 0 #d3fff1;
    background-color: #3131c3;
  }
  &__5 {
    box-shadow: 0 0 6px 0 #d3fff1;
    background-color: #ddf1ff;
  }
  &__6{
    box-shadow: 0 0 6px 0 #d3fff1;
    background-color: #006f90;
  }
  &__7 {
    box-shadow: 0 0 6px 0 #d3fff1;
    background-color: #6cfff9;
  }
  &__8 {//임시..
    box-shadow: 0 0 6px 0 #d3fff1;
    background-color: red;
  }
}
</style>