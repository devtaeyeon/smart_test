<template>
  <div class="box">
    <div class="brain-box"></div>
  </div>
</template>

<script>

</script>

<style scoped lang="scss">
.box {
  position: absolute;
  top: 45px;
  left: 65px;
  width: 132px;
  height: 132px;
  .brain-box{
    position: absolute;
    top: -11px;
    left: -37px;
    width: 260px;
    height: 185px;
    background-size: 84%;
    opacity: 0;
    background-image: url('../../assets/dashboard_img/box_brain.png');
    animation-name: brain;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
  }
  @keyframes brain{ 
    0% {opacity:0.2;}
    50% {opacity:0.9; }
    100% {opacity:0.2;}
  }
}
</style>
