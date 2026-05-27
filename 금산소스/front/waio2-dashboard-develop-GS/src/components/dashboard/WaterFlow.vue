<template>
  <div class="waterwall_back">
    <!-- 빨간선 화살표 움직임 -->
    <div class="waterway_red no1"></div>
    <div class="waterway_red no2"></div>
    <div class="waterway_red no3"></div>
    <div class="waterway_red no4"></div>
    <div class="waterway_red no5"></div>
    <div class="waterway_red no6"></div>

    <!-- 파란선 화살표 움직임(왼쪽) -->
    <div class="waterway_blue_up no1"></div>
    <div class="waterway_blue_up no2"></div>
    <div class="waterway_blue_up no3"></div>
    <div class="waterway_blue_up no4"></div>
    <div class="waterway_blue_up no5"></div>
    <div class="waterway_blue_up no6"></div>
    <div class="waterway_blue_up no7"></div>
    <div class="waterway_blue_up no8"></div>
    <div class="waterway_blue_up no9"></div>
    <div class="waterway_blue_up no10"></div>
    <div class="waterway_blue_up no11"></div>
    <div class="waterway_blue_up no12"></div>
    <div class="waterway_blue_up no13"></div>
    <div class="waterway_blue_up no14"></div>
    <div class="waterway_blue_up no15"></div>
    <div class="waterway_blue_up no16"></div>
    <div class="waterway_blue_up no17"></div>
    <div class="waterway_blue_up no18"></div>

    <!-- 파란선 화살표 움직임(아래쪽) -->
    <div class="waterway_blue_down no1"></div>
    <div class="waterway_blue_down no2"></div>
    <div class="waterway_blue_down no3"></div>
    <div class="waterway_blue_down no4"></div>
    <div class="waterway_blue_down no5"></div>
    <div class="waterway_blue_down no6"></div>
    <div class="waterway_blue_down no7"></div>
    <div class="waterway_blue_down no8"></div>
    <div class="waterway_blue_down no9"></div>
    <div class="waterway_blue_down no10"></div>
    <div class="waterway_blue_down no11"></div>
    <div class="waterway_blue_down no12"></div>
    <div class="waterway_blue_down no13"></div>
    <div class="waterway_blue_down no14"></div>
    <div class="waterway_blue_down no15"></div>
    <div class="waterway_blue_down no16"></div>
    <div class="waterway_blue_down no17"></div>
    <div class="waterway_blue_down no18"></div>
  </div>
</template>

<style scoped lang="scss">
// 최상위 엘리먼트
.waterwall_back {
  position: absolute;
  top: 210px;
  left: 568px;
  width: 679px;
  height: 512px;
  background-image: url('../../assets/gs_images/waterflow.png');
  // 파란선 화살표 움직임(위쪽)
  .waterway_blue_up {
    position: absolute;
    top: 8px;
    left: 10px;
    width: 15px;
    height: 18px;
    background-size: 100%;
    background-position: center;
    offset-path: path('M661.26 .44 495.93 85.94 656.84 162.39 651.53 196.9 499.31 272.13 440.91 246.46 295.78 307.52 484.27 403.98 315.24 489.82 69.23 361.51 .2 391.59');
    &.no1 {
      background-image: url('../../assets/arrow.png');
      // animation: name | duration | timing-function | delay | iteration-count | direction | fill-mode | play-state
      animation: move-blue-up 18.4s linear 0s normal infinite backwards;
    }
    &.no2 {
      background-image: url('../../assets/arrow2.png');
      animation: move-blue-up 18.4s linear 0.1s normal infinite backwards;
    }
    &.no3 {
      background-image: url('../../assets/arrow3.png');
      animation: move-blue-up 18.4s linear 0.2s normal infinite backwards;
    }
    &.no4 {
      background-image: url('../../assets/arrow.png');
      // animation: name | duration | timing-function | delay | iteration-count | direction | fill-mode | play-state
      animation: move-blue-up 18.4s linear 3s normal infinite backwards;
    }
    &.no5 {
      background-image: url('../../assets/arrow2.png');
      animation: move-blue-up 18.4s linear 3.1s normal infinite backwards;
    }
    &.no6 {
      background-image: url('../../assets/arrow3.png');
      animation: move-blue-up 18.4s linear 3.2s normal infinite backwards;
    }
    &.no7 {
      background-image: url('../../assets/arrow.png');
      // animation: name | duration | timing-function | delay | iteration-count | direction | fill-mode | play-state
      animation: move-blue-up 18.4s linear 6s normal infinite backwards;
    }
    &.no8 {
      background-image: url('../../assets/arrow2.png');
      animation: move-blue-up 18.4s linear 6.1s normal infinite backwards;
    }
    &.no9 {
      background-image: url('../../assets/arrow3.png');
      animation: move-blue-up 18.4s linear 6.2s normal infinite backwards;
    }
    &.no10 {
      background-image: url('../../assets/arrow.png');
      // animation: name | duration | timing-function | delay | iteration-count | direction | fill-mode | play-state
      animation: move-blue-up 18.4s linear 9s normal infinite backwards;
    }
    &.no11 {
      background-image: url('../../assets/arrow2.png');
      animation: move-blue-up 18.4s linear 9.1s normal infinite backwards;
    }
    &.no12 {
      background-image: url('../../assets/arrow3.png');
      animation: move-blue-up 18.4s linear 9.2s normal infinite backwards;
    }
    &.no13 {
      background-image: url('../../assets/arrow.png');
      // animation: name | duration | timing-function | delay | iteration-count | direction | fill-mode | play-state
      animation: move-blue-up 18.4s linear 12s normal infinite backwards;
    }
    &.no14 {
      background-image: url('../../assets/arrow2.png');
      animation: move-blue-up 18.4s linear 12.1s normal infinite backwards;
    }
    &.no15 {
      background-image: url('../../assets/arrow3.png');
      animation: move-blue-up 18.4s linear 12.2s normal infinite backwards;
    }
    // &.no16 {
    //   background-image: url('../../assets/arrow.png');
    //   // animation: name | duration | timing-function | delay | iteration-count | direction | fill-mode | play-state
    //   animation: move-blue-up 22.4s linear 15s normal infinite backwards;
    // }
    // &.no17 {
    //   background-image: url('../../assets/arrow2.png');
    //   animation: move-blue-up 22.4s linear 15.1s normal infinite backwards;
    // }
    // &.no18 {
    //   background-image: url('../../assets/arrow3.png');
    //   animation: move-blue-up 22.4s linear 15.2s normal infinite backwards;
    // }
  }
  // 파란선 화살표 움직임(아래쪽)
  // .waterway_blue_down {
  //   position: absolute;
  //   top: 229px;
  //   left: 429px;
  //   width: 15px;
  //   height: 18px;
  //   background-size: 100%;
  //   background-position: center;
  //   offset-path: path('M698.92 .45 789.16 41.82 738.61 68.37 681.42 125.56 632.4 151.09 761.08 215.43 715.63 243.01 411.29 395.69 339.8 435.52 463.38 501.9 .23 738.84');
  //   &.no1 {
  //     background-image: url('../../assets/arrow.png');
  //     // animation: name | duration | timing-function | delay | iteration-count | direction | fill-mode | play-state
  //     animation: move-blue-down 20s linear 0s normal infinite backwards;
  //   }
  //   &.no2 {
  //     background-image: url('../../assets/arrow2.png');
  //     animation: move-blue-down 20s linear 0.1s normal infinite backwards;
  //   }
  //   &.no3 {
  //     background-image: url('../../assets/arrow3.png');
  //     animation: move-blue-down 20s linear 0.2s normal infinite backwards;
  //   }
  //   &.no4 {
  //     background-image: url('../../assets/arrow.png');
  //     animation: move-blue-down 20s linear 3.0s normal infinite backwards;
  //   }
  //   &.no5 {
  //     background-image: url('../../assets/arrow2.png');
  //     animation: move-blue-down 20s linear 3.1s normal infinite backwards;
  //   }
  //   &.no6 {
  //     background-image: url('../../assets/arrow3.png');
  //     animation: move-blue-down 20s linear 3.2s normal infinite backwards;
  //   }
  //   &.no7 {
  //     background-image: url('../../assets/arrow.png');
  //     animation:  move-blue-down 20s linear 6.0s normal infinite backwards;
  //   }
  //   &.no8 {
  //     background-image: url('../../assets/arrow2.png');
  //     animation: move-blue-down 20s linear 6.1s normal infinite backwards;
  //   }
  //   &.no9 {
  //     background-image: url('../../assets/arrow3.png');
  //     animation: move-blue-down 20s linear 6.2s normal infinite backwards;
  //   }
  //   &.no10 {
  //     background-image: url('../../assets/arrow.png');
  //     animation:  move-blue-down 20s linear 9.0s normal infinite backwards;
  //   }
  //   &.no11 {
  //     background-image: url('../../assets/arrow2.png');
  //     animation: move-blue-down 20s linear 9.1s normal infinite backwards;
  //   }
  //   &.no12 {
  //     background-image: url('../../assets/arrow3.png');
  //     animation: move-blue-down 20s linear 9.2s normal infinite backwards;
  //   }
  //   &.no13 {
  //     background-image: url('../../assets/arrow.png');
  //     animation:  move-blue-down 20s linear 12.0s normal infinite backwards;
  //   }
  //   &.no14 {
  //     background-image: url('../../assets/arrow2.png');
  //     animation: move-blue-down 20s linear 12.1s normal infinite backwards;
  //   }
  //   &.no15 {
  //     background-image: url('../../assets/arrow3.png');
  //     animation: move-blue-down 20s linear 12.2s normal infinite backwards;
  //   }
  //   &.no16 {
  //     background-image: url('../../assets/arrow.png');
  //     animation:  move-blue-down 20s linear 15.0s normal infinite backwards;
  //   }
  //   &.no17 {
  //     background-image: url('../../assets/arrow2.png');
  //     animation: move-blue-down 20s linear 15.1s normal infinite backwards;
  //   }
  //   &.no18 {
  //     background-image: url('../../assets/arrow3.png');
  //     animation: move-blue-down 20s linear 15.2s normal infinite backwards;
  //   }
  // }
  // 빨간선 화살표 움직임
  // .waterway_red { 
  //   position: absolute;
  //   top: 10px;
  //   left: 1125px;
  //   width: 15px;
  //   height: 18px;
  //   background-size: 100%;
  //   background-position: center;
  //   offset-path: path('M204.31 .46 1.22 87.78 117.75 142.93 1.22 208.03');
  //   &.no1 {
  //     background-image: url('../../assets/arrow.png');
  //     // animation: name | duration | timing-function | delay | iteration-count | direction | fill-mode | play-state
  //     animation: move 6s linear 0s normal infinite backwards;
  //   }
  //   &.no2 {
  //     background-image: url('../../assets/arrow2.png');
  //     animation: move 6s linear 0.1s normal infinite backwards;
  //   }
  //   &.no3 {
  //     background-image: url('../../assets/arrow3.png');
  //     animation: move 6s linear 0.2s normal infinite backwards;
  //   }
  //   &.no4 {
  //     background-image: url('../../assets/arrow.png');
  //     animation: move 6s linear 3s normal infinite backwards;
  //   }
  //   &.no5 {
  //     background-image: url('../../assets/arrow2.png');
  //     animation: move 6s linear 3.1s normal infinite backwards;
  //   }
  //   &.no6 {
  //     background-image: url('../../assets/arrow3.png');
  //     animation: move 6s linear 3.2s normal infinite backwards;
  //   }
  // }
}
// 파란선 화살표 투명도 제어(왼쪽)
@keyframes move-blue-up {
  0% {
    offset-distance: 0%;
    opacity: 0;
  }
  1% {
    opacity: 1;
  }
  10% {
    opacity: 1;
  }
  20% {
    opacity: 1;
  }
  30% {
    opacity: 1;
  }
  40% {
    opacity: 0;
  }
  50% {
    opacity: 0;
  }
  60% {
    opacity: 1;
  }
  65% {
    opacity: 1;
  }
  70% {
    opacity: 0;
  }
  75% {
    opacity: 1;
  }
  80% {
    opacity:1;
  }
  85% {
    opacity:1;
  }
  90% {
    opacity:1;
  }
  100% {
    offset-distance: 100%;
    opacity: 0;
  }
}
// 파란선 화살표 투명도 제어(아래쪽)
// @keyframes move-blue-down {
//   0% {
//     offset-distance: 0%;
//     opacity: 0;
//   }
//   1% {
//     opacity: 1;
//   }
//   10% {
//     opacity: 1;
//   }
//   20% {
//     opacity: 1;
//   }
//   30% {
//     opacity: 1;
//   }
//   40% {
//     opacity: 0;
//   }
//   50% {
//     opacity: 0;
//   }
//   60% {
//     opacity: 0;
//   }
//   65% {
//     opacity: 1;
//   }
//   70% {
//     opacity: 0;
//   }
//   75% {
//     opacity: 1;
//   }
//   80% {
//     opacity:1;
//   }
//   85% {
//     opacity:1;
//   }
//   90% {
//     opacity:1;
//   }
//   100% {
//     offset-distance: 100%;
//     opacity: 1;
//   }
// }
// 빨간선 화살표 투명도 제어
// @keyframes move {
//   0% {
//     offset-distance: 0%;
//     display: none;
//     opacity: 0;
//   }
//   1% {
//     opacity: 1;
//   }
//   80% {
//     opacity: 0;
//   }
//   100% {
//     opacity: 1;
//     offset-distance: 100%;
//   }
// }
</style>