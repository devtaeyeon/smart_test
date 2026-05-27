<template>
  <div v-if="$store.state.network.modifyConfig !== null" class="network-setting-container">
    <!-- 네트워크 설정 헤더 컨테이너 -->
    <div class="header-container">
      <div class="header-text">네트워크 설정 입력</div>
      <!-- 수정 버튼 -->
      <button v-if="!this.isModifyMode" class="modify-box" @click="onClickModifyBtn()">
        <img class="modify-img" src="../../assets/networkSetting/modify.png" alt="수정">
        수정
      </button>
      <!-- 완료 버튼 -->
      <button v-else class="modify-box" @click="onClickConfirmBtn()">
        <img class="modify-img" src="../../assets/networkSetting/modify.png" alt="완료">
        완료
      </button>
    </div>
    <div>
      <!-- SCADA 서버 1 -->
      <div class="list-conatiner">
        <!-- 좌측 이미지 컨테이너 -->
        <div class="left-container">
          <img src="../../assets/networkSetting/scada.png" alt="스카다">
          <div class="custom-title">
            <span>SCADA 서버 - 1</span>
          </div>
        </div>
        <!-- 우측 네트워크 설정 변경 컨테이너 -->
        <div class="right-container">
          <div class="arrow-img">
          </div>
          <div class="network-info-container">
            <div class="custom-title">
              <span>SCADA 서버 - 1</span>
            </div>
            <div class="network-panel">
              <div class="info-box">
                <div class="left-box ip">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    IP
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.scd_addr1" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.scd_addr1"/>
                </div>
              </div>
              <div class="info-box">
                <div class="left-box port">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    수집데이터 Port
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.scd_port1" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.scd_port1"/>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- SCADA 서버 2 -->
      <div class="list-conatiner">
        <!-- 좌측 이미지 컨테이너 -->
        <div class="left-container">
          <img src="../../assets/networkSetting/scada.png" alt="스카다">
          <div class="custom-title">
            <span>SCADA 서버 - 2</span>
          </div>
        </div>
        <!-- 우측 네트워크 설정 변경 컨테이너 -->
        <div class="right-container">
          <div class="arrow-img">
          </div>
          <div class="network-info-container">
            <div class="custom-title">
              <span>SCADA 서버 - 2</span>
            </div>
            <div class="network-panel">
              <div class="info-box">
                <div class="left-box ip">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    IP
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.scd_addr2" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.scd_addr2"/>
                </div>
              </div>
              <div class="info-box">
                <div class="left-box port">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    수집데이터 Port
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.scd_port2" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.scd_port2"/>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 분석 시스템 1 -->
      <div class="list-conatiner">
        <!-- 좌측 이미지 컨테이너 -->
        <div class="left-container">
          <img src="../../assets/networkSetting/analysis.png" alt="분석시스템">
          <div class="custom-title">
            <span>분석 시스템 - 1</span>
          </div>
        </div>
        <!-- 우측 네트워크 설정 변경 컨테이너 -->
        <div class="right-container">
          <div class="arrow-img">
          </div>
          <div class="network-info-container">
            <div class="custom-title">
              <span>분석 시스템 - 1</span>
            </div>
            <div class="network-panel">
              <div class="info-box">
                <div class="left-box ip">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    IP
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.anl_addr1" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.anl_addr1"/>
                </div>
              </div>
              <div class="info-box">
                <div class="left-box resource-manager">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    Resource Manager Port
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.anl_rm_port1" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.anl_rm_port1"/>
                </div>
              </div>
              <div class="info-box">
                <div class="left-box node-manager">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    Node Manager Port
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.anl_nm_port1" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.anl_nm_port1"/>
                </div>
              </div>
              <div class="info-box">
                <div class="left-box name-node">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    Name Node Port
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.anl_nn_port1" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.anl_nn_port1"/>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 분석 시스템 2 -->
      <div class="list-conatiner">
        <!-- 좌측 이미지 컨테이너 -->
        <div class="left-container">
          <img src="../../assets/networkSetting/analysis.png" alt="분석시스템">
          <div class="custom-title">
            <span>분석 시스템 - 2</span>
          </div>
        </div>
        <!-- 우측 네트워크 설정 변경 컨테이너 -->
        <div class="right-container">
          <div class="arrow-img">
          </div>
          <div class="network-info-container">
            <div class="custom-title">
              <span>분석 시스템 - 2</span>
            </div>
            <div class="network-panel">
              <div class="info-box">
                <div class="left-box ip">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    IP
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.anl_addr2" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.anl_addr2"/>
                </div>
              </div>
              <div class="info-box">
                <div class="left-box resource-manager">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    Resource Manager Port
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.anl_rm_port2" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.anl_rm_port2"/>
                </div>
              </div>
              <div class="info-box">
                <div class="left-box node-manager">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    Node Manager Port
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.anl_nm_port2" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.anl_nm_port2"/>
                </div>
              </div>
              <div class="info-box">
                <div class="left-box name-node">
                </div>
                <div class="right-box">
                  <div class="info-title">
                    Name Node Port
                  </div>
                  <input v-if="!this.isModifyMode" class="info-input-box" type="text" v-model="$store.state.network.modifyConfig.anl_nn_port2" disabled/>
                  <input v-else type="text" class="info-input-box-modify" v-model="$store.state.network.modifyConfig.anl_nn_port2"/>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { util } from '@/service/utils'
import { SET_OVERLAY } from '@/store'

export default {
  name: 'ConfigNetwork',
  mixins: [util],
  data: () => ({
    isModifyMode: false // 수정 모드 여부
  }),
  computed: {
  },
  methods: {
    /**
     * 수정 버튼 클릭시
     * 수정 모드로 변경
     */
    onClickModifyBtn: function () {
      this.isModifyMode = true
    },
    /**
     * 수정 완료 버튼 클릭시
     * validation 체크 후 네트워크 정보 수정 API 요청
     */
    onClickConfirmBtn: function () {
      if (this.$store.state.network.modifyConfig.scd_addr1 === null | this.$store.state.network.modifyConfig.scd_addr1 === '' |
        this.$store.state.network.modifyConfig.scd_addr2 === null | this.$store.state.network.modifyConfig.scd_addr2 === '' | 
        this.$store.state.network.modifyConfig.anl_addr1 === null | this.$store.state.network.modifyConfig.anl_addr1 === '' |
        this.$store.state.network.modifyConfig.anl_addr2 === null | this.$store.state.network.modifyConfig.anl_addr2 === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: 'IP를 입력해주세요' })
        return
      }
      if (!this.validateIPaddress(this.$store.state.network.modifyConfig.scd_addr1) |
        !this.validateIPaddress(this.$store.state.network.modifyConfig.scd_addr2) |
        !this.validateIPaddress(this.$store.state.network.modifyConfig.anl_addr1) |
        !this.validateIPaddress(this.$store.state.network.modifyConfig.anl_addr2)) {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: 'IP의 형식이 올바르지 않습니다' })
        return
      }
      if (this.$store.state.network.modifyConfig.scd_port1 === null | this.$store.state.network.modifyConfig.scd_port1 === '' |
        this.$store.state.network.modifyConfig.scd_port2 === null | this.$store.state.network.modifyConfig.scd_port2 === '' |
        this.$store.state.network.modifyConfig.anl_rm_port1 === null | this.$store.state.network.modifyConfig.anl_rm_port1 === '' |
        this.$store.state.network.modifyConfig.anl_nm_port1 === null | this.$store.state.network.modifyConfig.anl_nm_port1 === '' |
        this.$store.state.network.modifyConfig.anl_nn_port1 === null | this.$store.state.network.modifyConfig.anl_nn_port1 === '' |
        this.$store.state.network.modifyConfig.anl_rm_port2 === null | this.$store.state.network.modifyConfig.anl_rm_port2 === '' |
        this.$store.state.network.modifyConfig.anl_nm_port2 === null | this.$store.state.network.modifyConfig.anl_nm_port2 === '' |
        this.$store.state.network.modifyConfig.anl_nn_port2 === null | this.$store.state.network.modifyConfig.anl_nn_port2 === '') {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: 'Port를 입력해주세요' })
        return
      }
      if (!this.validateNumber(this.$store.state.network.modifyConfig.scd_port1) |
        !this.validateNumber(this.$store.state.network.modifyConfig.scd_port2) |
        !this.validateNumber(this.$store.state.network.modifyConfig.anl_rm_port1) |
        !this.validateNumber(this.$store.state.network.modifyConfig.anl_nm_port1) |
        !this.validateNumber(this.$store.state.network.modifyConfig.anl_nn_port1) |
        !this.validateNumber(this.$store.state.network.modifyConfig.anl_rm_port2) |
        !this.validateNumber(this.$store.state.network.modifyConfig.anl_nm_port2) |
        !this.validateNumber(this.$store.state.network.modifyConfig.anl_nn_port2)) {
        this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: 'Port값에 숫자를 입력해주세요' })
        return
      }
      
      this.$store.commit(SET_OVERLAY, true)
      Promise.all([
        this.$store.dispatch('network/PUT_NETWORK_CONFIG', this.$store.state.network.modifyConfig)  
      ]).finally(() => {
        this.$store.commit(SET_OVERLAY, false)
      })
      this.isModifyMode = false
    }
  },
  created: function () {
    console.log(this.$options.name + ' created')
  },
  /**
   * 마운트시 실행되는 함수
   * 네트워크 정보 API를 요청
   */
  mounted: function () {
    console.log(this.$options.name + ' mounted')
    this.$store.commit(SET_OVERLAY, true)
    Promise.all([
      this.$store.dispatch('network/GET_NETWORK_CONFIG')  
    ]).finally(() => {
      this.$store.commit(SET_OVERLAY, false)
    })
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
.network-setting-container {
  position: relative;
  top: 26px;
  left: 42px;
  width: 1858px;
  height: 964px;
}
.header-container {
  display: flex;
  align-items: center;
  height: 58px;
  margin: 0px 16px;
  background-position-y: 13px;
  background-position-x: -28px;
  background-image: url('../../assets/editableDashboard/title_under.png');
  .header-icon {
    width: 11px;
    height: 11px;
    margin-right: 12px;
    background-image: url('../../assets/networkSetting/header_icon.png');
    background-size: 100%;
  }
  .header-text {
    font-size: 18px;
    color: #ffffff;
    text-shadow: 0 0 9px #5cafff;
  }
  .modify-box {
    width: 71px;
    height: 34px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    margin-left: auto;
    color: #fff;
    text-shadow: 0 0 9px #5cafff;
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.25);
    .modify-img {
      margin-right: 5px;
    }
    &:hover {
      box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
      border: solid 1px #b4dffa;
      background-color: rgba(139, 194, 240, 0.4);
    }
  }
}
.list-conatiner {
  display:flex;
  height: 211px;
  margin: 11px 16px;
  .left-container {
    position: relative;
    display: flex;
    width: 215px;
    height: 211px;
    > img {
      margin: auto;
    }
    > div {
      position: absolute;
      bottom: 0px;
    }
  }
  .right-container {
    display: flex;
    flex:1;
    height: 100%;
    .arrow-img {
      width: 66px;
      height: 100%;
      background-image: url('../../assets/networkSetting/right_container_background_arrow.png');
      opacity: 0.3;
      background-size: 100%;
    }
    .network-info-container {
      display: flex;
      flex-direction: column;
      flex: 1;
      background-color: rgba(34,80,155,0.2);
      padding: 20px;
      .network-panel {
        display: flex;
        flex: 1;
        justify-content: space-around;
        padding: 0 40px;
        .info-box {
          margin: auto 0;
        }
      }
    }
  }
}
.custom-title {
  display: flex;
  width: 215px;
  height: 36px;
  font-size: 16px;
  color: #fff;
  text-shadow: 0 0 9px #5cafff;
  > span {
    margin: auto;
  }
}
.info-box {
  display: flex;
  width: 314px;
  height: 86px;
  .left-box {
    width: 86px;
    height: 86px;
  }
  .ip {
    background-image: url('../../assets/networkSetting/ip.png');
    background-size: 100%;
  }
  .port {
    background-image: url('../../assets/networkSetting/port.png');
    background-size: 100%;
  }
  .resource-manager {
    background-image: url('../../assets/networkSetting/resource_manager.png');
    background-size: 100%;
  }
  .node-manager {
    background-image: url('../../assets/networkSetting/node_manager.png');
    background-size: 100%;
  }
  .name-node {
    background-image: url('../../assets/networkSetting/name_node.png');
    background-size: 100%;
  }
  .right-box {
    display:flex;
    flex-direction: column;
    justify-content: space-between;
    flex: 1;
    font-size: 16px;
    margin: 3px 0px 5px 20px;
    .info-title {
      color: #fff;
      text-shadow: 0 0 9px #5cafff;
      &:before {
        width: 9px;
        height: 10px;
        margin-right: 5px;
        background-image: url("../../assets/networkSetting/icon_right_white_arrow.png");
        background-size: 100%;
        display:inline-block;
        content:'';
      }
    }
  .info-input-box {
    width: 100%;
    height: 34px;
    border: solid 1px #87f4f4;
    font-size: 16px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    letter-spacing: normal;
    text-align: left;
    text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
    font-family: "LAB디지털" !important;
    color: #fff;
    text-shadow: 0 0 9px #5cafff;
    padding-left: 11px;
    }
  }
  .info-input-box-modify {
    width: 100%;
    height: 34px;
    border-bottom: solid 1px #fff;
    font-size: 16px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    letter-spacing: normal;
    text-align: left;
    text-shadow: 0 0 5px rgba(209, 250, 255, 0.5);
    font-family: "LAB디지털" !important;
    color: #fff;
    text-shadow: 0 0 9px #5cafff;
    padding-left: 11px;
  }
}
</style>