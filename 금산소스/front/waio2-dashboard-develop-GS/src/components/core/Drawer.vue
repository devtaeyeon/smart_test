<template>
  <div class="drawer-container">
    <!-- Drawer 손잡이 -->
    <div v-if="$store.state.drawer.drawer.handler" class="drawer-handler" @mouseover="mainMenuMouseOver" @mouseleave="mainMenuMouseOut">
      <div class="tri"></div>
    </div>
    <!-- Drawer 메뉴 -->
    <div :style="{'width': drawerWidth}" style="height: 100%; display:flex;" @mouseover="mainMenuMouseOver" @mouseleave="mainMenuMouseOut">
      <!-- 메인 메뉴(메인, 자율운영, 스마트EMS, 스마트PMS, 지능형 영상, 운영관리) -->
      <div class="left-main-menu-container" :style="{'width': leftMenuWidth}">
        <div class="logo-container" v-show="leftMenuWidth !== '15px'">
          <div class="logo-icon"></div>
        </div>
        <div class="left-main-menu-item-container" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 0}" v-show="leftMenuWidth !== '15px'" @click="onClickMainMenu(0)">
          <div class="left-main-menu-item-icon dashboard" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 0}"></div>
          <div class="left-main-menu-item-text" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 0}">메인</div>
        </div>
        <div class="left-main-menu-item-container" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 2}" v-show="leftMenuWidth !== '15px'" @click="onClickMainMenu(2)">
          <div class="left-main-menu-item-icon ao" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 2}"></div>
          <div class="left-main-menu-item-text" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 2}">자율운영</div>
        </div>
        <div class="left-main-menu-item-container" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 3}" v-show="leftMenuWidth !== '15px'" @click="onClickMainMenu(3)">
          <div class="left-main-menu-item-icon ems" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 3}"></div>
          <div class="left-main-menu-item-text" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 3}">스마트 EMS</div>
        </div>
        <!-- <div class="left-main-menu-item-container" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 4}" v-show="leftMenuWidth !== '15px'" @click="onClickMainMenu(4)">
          <div class="left-main-menu-item-icon pms" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 4}"></div>
          <div class="left-main-menu-item-text" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 4}">스마트 PMS</div>
        </div> -->
        <!-- <div class="left-main-menu-item-container" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 6}" v-show="leftMenuWidth !== '15px'" @click="onClickMainMenu(6)">
          <div class="left-main-menu-item-icon cctv" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 6}"></div>
          <div class="left-main-menu-item-text" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 6}">지능형 영상</div>
        </div> -->
        <div class="left-main-menu-item-container" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 5}" v-show="leftMenuWidth !== '15px'" @click="onClickMainMenu(5)">
          <div class="left-main-menu-item-icon system" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 5}"></div>
          <div class="left-main-menu-item-text" :class="{selected: $store.state.drawer.selectedMainMenuIndex === 5}">운영 관리</div>
        </div>
      </div>
      <!-- 자율운영 메뉴(착수, 약품, 혼화/응집, 침전, 여과, 오존, GAC여과, 소독, 송수) -->
      <div v-if="$store.state.drawer.selectedMainMenuIndex === 2" class="right-menu-container" :style="{ 'width': rightMenuWidth }" @mouseleave="rightMenuMouseOut">
        <v-navigation-drawer
          :permanent="$store.state.drawer.drawer.parmanet"
          :expand-on-hover="$store.state.drawer.drawer.expandOnHover"
          color="rgba(0, 0, 0, 0)"
        >
          <div class="right-menu-header-container" style="width: 100%; height: 63px; padding-top:20px;">
            <div class="right-menu-header-icon" :class="{ao: rightHeaderIcon==='ao'}"></div>
            <div class="right-menu-header-text">{{ rightHeaderText }}</div>
          </div>

          <v-divider></v-divider>
          <div class="list-wrapper">
            <v-list
              dense
              width=159
              >
              <template v-for="item in menuItems">
                <v-list-group
                  v-if="item.children"
                  append-icon=""
                  :key="item.key"
                  :to="item.route"
                  color="#00b9ed"
                  v-model="item.active"
                >
                  <template v-slot:activator>
                    <v-list-item-action>
                      <v-icon>{{ item.icon }}</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                      <v-list-item-title>
                        {{ item.text }}
                      </v-list-item-title>
                    </v-list-item-content>
                  </template>
                  <template v-for="(child, i) in item.children">
                    <v-list-item
                      :key="i"
                      :to="child.route"
                      v-model="child.active"
                      @click="goURL(item.process, child.processStep)"
                      link
                      >
                      <v-list-item-action>
                        <v-icon>{{ child.icon }}</v-icon>
                      </v-list-item-action>
                      <v-list-item-content>
                        <v-list-item-title>
                          {{ child.text }}
                        </v-list-item-title>
                      </v-list-item-content>
                    </v-list-item>
                  </template>
                </v-list-group>
                <v-list-item
                  v-else-if="item.text === '송수'"
                  :key="item.text"
                  @click="goEMSURL(item.route)"
                  link
                >
                  <v-list-item-action>
                    <v-icon>{{ item.icon }}</v-icon>
                  </v-list-item-action>
                  <v-list-item-content>
                    <v-list-item-title>
                      {{ item.text }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item
                  v-else-if="item.usr_auth >= $store.state.login.user.usr_auth"
                  :key="item.text"
                  :to="item.route"
                  link
                  v-model="item.active"
                >
                  <v-list-item-action>
                    <v-icon>{{ item.icon }}</v-icon>
                  </v-list-item-action>
                  <v-list-item-content>
                    <v-list-item-title>
                      {{ item.text }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </template>
            </v-list>
          </div>
        </v-navigation-drawer>
      </div>
      <!-- EMS 메뉴(대시보드, AI분석, 에너지 사용 현황, 에너지 절감 관리, 설정) -->
      <div v-else-if="$store.state.drawer.selectedMainMenuIndex === 3" class="right-menu-container" :style="{ 'width': rightMenuWidth }" @mouseleave="rightMenuMouseOut">
        <v-navigation-drawer
          :permanent="$store.state.drawer.drawer.parmanet"
          :expand-on-hover="$store.state.drawer.drawer.expandOnHover"
          color="rgba(0, 0, 0, 0)"
        >
          <div class="right-menu-header-container" style="width: 100%; height: 63px; padding-top:20px;">
            <div class="right-menu-header-icon" :class="{ao: rightHeaderIcon==='ao'}"></div>
            <div class="right-menu-header-text">{{ rightHeaderText }}</div>
          </div>
          <v-divider></v-divider>
          <div class="list-wrapper">
            <v-list
              dense
              width=199
              >
              <template v-for="item in menuItems">
                <v-list-group
                  v-if="item.children"
                  append-icon=""
                  prepend-icon=""
                  :key="item.text"
                  color="#00b9ed"
                  no-action
                >
                  <template v-slot:activator>
                    <v-list-item-action>
                      <v-icon>{{ item.icon }}</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                      <v-list-item-title>
                        {{ item.text }}
                      </v-list-item-title>
                    </v-list-item-content>
                  </template>
                  <template v-for="child in item.children">
                      <v-list-group
                        v-if="child.children"
                        append-icon=""
                        prepend-icon=""
                        :key="child.text"
                        color="#00b9ed"
                        no-action
                        sub-group
                      >
                        <template v-slot:activator>
                          <v-list-item-action>
                            <v-icon>{{ child.icon }}</v-icon>
                          </v-list-item-action>
                          <v-list-item-content>
                            <v-list-item-title>
                              {{ child.text }}
                            </v-list-item-title>
                          </v-list-item-content>
                        </template>
                        <template v-for="childOfChild in child.children">
                          <v-list-item
                            :key="childOfChild.route"
                            @click="goEMSURL(childOfChild.route)"
                            link
                            >
                            <v-list-item-content>
                              <v-list-item-title>
                                {{ childOfChild.text }}
                              </v-list-item-title>
                            </v-list-item-content>
                          </v-list-item>
                        </template>
                      </v-list-group>
                      <v-list-item
                        v-else
                        :key="child.text"
                        @click="goEMSURL(child.route)"
                        link
                        >
                        <v-list-item-action>
                          <v-icon>{{ child.icon }}</v-icon>
                        </v-list-item-action>
                        <v-list-item-content>
                          <v-list-item-title>
                            {{ child.text }}
                          </v-list-item-title>
                        </v-list-item-content>
                      </v-list-item>
                    <!-- </div> -->
                  </template>
                </v-list-group>
                <v-list-item
                  v-else
                  :key="item.text"
                  @click="goEMSURL(item.route)"
                  link
                >
                  <v-list-item-action>
                    <v-icon>{{ item.icon }}</v-icon>
                  </v-list-item-action>
                  <v-list-item-content>
                    <v-list-item-title>
                      {{ item.text }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </template>
            </v-list>
          </div>
        </v-navigation-drawer>
      </div>
      <!-- PMS 메뉴(대시보드, 착수, 약품, 혼화응집, 침전, 여과, GAC, 오존, 소독, 송수, 통계/이력) -->
      <!-- <div v-else-if="$store.state.drawer.selectedMainMenuIndex === 4" class="right-menu-container" :style="{ 'width': rightMenuWidth }" @mouseleave="rightMenuMouseOut">
        <v-navigation-drawer
          :permanent="$store.state.drawer.drawer.parmanet"
          :expand-on-hover="$store.state.drawer.drawer.expandOnHover"
          color="rgba(0, 0, 0, 0)"
        >
          <div class="right-menu-header-container" style="width: 100%; height: 63px; padding-top:20px;">
            <div class="right-menu-header-icon" :class="{ao: rightHeaderIcon==='ao'}"></div>
            <div class="right-menu-header-text">{{ rightHeaderText }}</div>
          </div>

          <v-divider></v-divider>
          <div class="list-wrapper">
            <v-list
              dense
              width=159
              >
              <template v-for="item in menuItems">
                <v-list-group
                  v-if="item.children"
                  append-icon=""
                  :key="item.key"
                  color="#00b9ed"
                  @click="goPMSURL(item.route)"
                >
                  <template v-slot:activator>
                    <v-list-item-action>
                      <v-icon>{{ item.icon }}</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                      <v-list-item-title>
                        {{ item.text }}
                      </v-list-item-title>
                    </v-list-item-content>
                  </template>
                  <template v-for="(child, i) in item.children">
                    <v-list-item
                      :key="i"
                      @click="goPMSURL(child.route)"
                      link
                      >
                      <v-list-item-action>
                        <v-icon>{{ child.icon }}</v-icon>
                      </v-list-item-action>
                      <v-list-item-content>
                        <v-list-item-title>
                          {{ child.text }}
                        </v-list-item-title>
                      </v-list-item-content>
                    </v-list-item>
                  </template>
                </v-list-group>
                <v-list-item
                  v-else-if="item.usr_auth >= $store.state.login.user.usr_auth"
                  :key="item.text"
                  @click="goPMSURL(item.route)"
                  link
                >
                  <v-list-item-action>
                    <v-icon>{{ item.icon }}</v-icon>
                  </v-list-item-action>
                  <v-list-item-content>
                    <v-list-item-title>
                      {{ item.text }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </template>
            </v-list>
          </div>
        </v-navigation-drawer>
      </div> -->
      <!-- 운영관리 메뉴(알람이력, 성능모니터링, 알람관리, 사용자 관리, 로그인 이력, 네트워크 설정) -->
      <div v-else class="right-menu-container" :style="{ 'width': rightMenuWidth }" @mouseleave="rightMenuMouseOut">
        <v-navigation-drawer
          :permanent="$store.state.drawer.drawer.parmanet"
          :expand-on-hover="$store.state.drawer.drawer.expandOnHover"
          color="rgba(0, 0, 0, 0)"
        >
          <div class="right-menu-header-container" style="width: 100%; height: 63px; padding-top:20px;">
            <div class="right-menu-header-icon" :class="{ao: rightHeaderIcon==='ao'}"></div>
            <div class="right-menu-header-text">{{ rightHeaderText }}</div>
          </div>

          <v-divider></v-divider>
          <div class="list-wrapper">
            <v-list
              dense
              width=159
              >
              <template v-for="item in menuItems">
                <v-list-group
                  v-if="item.children"
                  append-icon=""
                  :key="item.key"
                  :to="item.route"
                  color="#00b9ed"
                >
                  <template v-slot:activator>
                    <v-list-item-action>
                      <v-icon>{{ item.icon }}</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                      <v-list-item-title>
                        {{ item.text }}
                      </v-list-item-title>
                    </v-list-item-content>
                  </template>
                  <template v-for="(child, i) in item.children">
                    <v-list-item
                      :key="i"
                      :to="child.route"
                      link
                      >
                      <v-list-item-action>
                        <v-icon>{{ child.icon }}</v-icon>
                      </v-list-item-action>
                      <v-list-item-content>
                        <v-list-item-title>
                          {{ child.text }}
                        </v-list-item-title>
                      </v-list-item-content>
                    </v-list-item>
                  </template>
                </v-list-group>
                <v-list-item
                  v-else-if="item.usr_auth >= $store.state.login.user.usr_auth"
                  :key="item.text"
                  :to="item.route"
                  link
                >
                  <v-list-item-action>
                    <v-icon>{{ item.icon }}</v-icon>
                  </v-list-item-action>
                  <v-list-item-content>
                    <v-list-item-title>
                      {{ item.text }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </template>
            </v-list>
          </div>
        </v-navigation-drawer>
      </div>
    </div>

    <div style="position:absolute; display:none">
      <form ref="form">
        <input name="token" id="token" v-model="$store.state.login.user.tkn">
        <label for="token"></label>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { SERVICE_URL } from '@/store'

export default {
  name: 'Drawer',
  data: () => ({
    drawerWidth: '15px', // 서랍 너비 초기값
    rightHeaderIcon: '', // 서랍 2단계 헤더 아이콘
    rightHeaderText: '' // 서랍 2단계 헤더 제목
  }),
  computed: {
    // 왼쪽 메뉴 너비
    leftMenuWidth: function () {
      return this.$store.state.drawer.mainLeftMenuWidth
    },
    // 오른쪽 메뉴 너비
    rightMenuWidth: function () {
      return this.$store.state.drawer.mainRightMenuWidth
    },
    // 메뉴 아이템 설정
    menuItems: function () {
      // 자율 운영 메뉴
      if (this.$store.state.drawer.selectedMainMenuIndex === 2) {
        return this.$store.state.drawer.drawer.items
      // 스마트 EMS 메뉴
      } else if (this.$store.state.drawer.selectedMainMenuIndex === 3){
        return this.$store.state.drawer.drawer.itemsEMS
      // 스마트 PMS 메뉴
      } else if (this.$store.state.drawer.selectedMainMenuIndex === 4){
        return this.$store.state.drawer.drawer.itemsPMS
      // 운영 관리 메뉴
      } else {
        return this.$store.state.drawer.drawerSystem.items
      }
    }
  },
  methods: {
    /**
     * 메인 메뉴 항목 선택시 실행되는 함수
     * 선택되는 항목에 따라 Drawer 너비와 Header 아이콘, Header 텍스트 설정
     */
    onClickMainMenu: function (index) {
      this.$store.state.drawer.selectedMainMenuIndex = index
      this.$store.state.drawer.mainRightMenuWidth = '0px'
      this.drawerWidth = '103px'
      // 자율운영 대시보드
      if (index === 0) {
        if (this.$router.history.current.name !== 'Dashboard') {
          this.$router.push('/')
        }
      // 자율운행
      } else if (index === 2) {
        this.rightHeaderIcon = "ao"
        this.rightHeaderText = "자율운영"
        this.drawerWidth = '262px'
        this.$store.state.drawer.mainRightMenuWidth = '159px'
      // 스마트 EMS
      } else if (index === 3) {
        this.rightHeaderIcon = "ao"
        this.rightHeaderText = "스마트EMS"
        this.drawerWidth = '302px'
        this.$store.state.drawer.mainRightMenuWidth = '199px'
      // 스마트 PMS
      } else if (index === 4) {
        this.rightHeaderIcon = "ao"
        this.rightHeaderText = "스마트PMS"
        this.drawerWidth = '262px'
        this.$store.state.drawer.mainRightMenuWidth = '159px'
      // 운영관리
      } else if (index === 5) {
        this.rightHeaderIcon = "sys"
        this.rightHeaderText = "운영 관리"
        this.drawerWidth = '262px'
        this.$store.state.drawer.mainRightMenuWidth = '159px'
      // 지능형 CCTV
      } else if (index === 6) {
        window.open(SERVICE_URL.CCTV, "_self")
      } else {
        this.drawerWidth = '103px'
        this.$store.state.drawer.mainRightMenuWidth = '0px'
      }
      console.log('leftMenuWidth: ', + this.$store.state.drawer.mainLeftMenuWidth, 'rightMenuWidth: ', + this.$store.state.drawer.mainRightMenuWidth)
    },
    /**
     * 메인 메뉴 마우스 오버시 실행되는 함수
     * Drawer 2단, 3단을 표현하기 위해 Drawer 너비를 조절함
     */
    mainMenuMouseOver: function () {
      this.$store.state.drawer.mainLeftMenuWidth = '103px'
      if (this.$store.state.drawer.mainRightMenuWidth === '0px') {
        this.drawerWidth = '103px'
      } else if (this.$store.state.drawer.mainRightMenuWidth === '199px') {
        this.drawerWidth = '302px'
      }else {
        this.drawerWidth = '262px'
      }
    },
    /**
     * 메인 메뉴 마우스 아웃시 실행되는 함수
     * 너비를 줄여 Drawer를 닫음
     */
    mainMenuMouseOut: function () {
      this.drawerWidth = '15px'
      this.$store.state.drawer.mainLeftMenuWidth = '15px'
    },
    /**
     * Drawer 2단 마우스 아웃시 실행되는 함수
     * 메인 메뉴만 보이도록 설정
     */
    rightMenuMouseOut: function () {
      this.drawerWidth = '103px'
      this.$store.state.drawer.mainRightMenuWidth = '0px'
    },
    /**
     * EMS 메뉴로 이동하는 함수
     * 로그인 여부에 따라 AccessToken을 넘김
     */
    goEMSURL: function(url) {
      console.log(url)
      if (this.$store.state.login.user.tkn !== null) {
        window.open(SERVICE_URL.EMS + url +'?token=' + this.$store.state.login.user.tkn, "_self")
      } else {
        window.open(SERVICE_URL.EMS + url, "_self")
      }
    },
    goURL: function(process, processStep) {
      if(processStep !== undefined){
        switch(process){
          case 'receiving':
            this.$store.state[process].processStep = processStep
            break;
          case 'disinfection':
            this.$store.state[process].disinfectionStep = processStep
            break;
        }
      }
    },
    /**
     * PMS 메뉴로 이동하는 함수
     * 로그인 여부에 따라 AccessToken을 넘김
     */
    // goPMSURL: function(url) {
    //   console.log(url)
    //   if (this.$store.state.login.user.tkn !== null) {
    //     window.open(SERVICE_URL.PMS + url +'?token=' + this.$store.state.login.user.tkn, "_self")
    //   } else {
    //     window.open(SERVICE_URL.PMS + url, "_self")
    //   }
    // },
  },
  created () {
    console.log(this.$route.query.token)
    // EMS & PMS로 부터 전달받은 토큰이 있는지 확인하여 'X-ACCESS-TOKEN' 헤더의 값으로 설정
    if (this.$route.query.token !== undefined) {
      console.log(this.$route.query.token)
      this.$store.state.login.user.tkn = this.$route.query.token
      axios.defaults.headers.common['X-ACCESS-TOKEN'] = this.$route.query.token
      Promise.all([
        this.$store.dispatch('login/LOGIN_PUT'),
        // 필요시 토큰으로 유저 정보 요청
        this.$store.dispatch('user/GET_USER'),
      ]).finally(() => {
      })
    }
  }
}
</script>

<style scoped lang="scss">
// 최상위 엘리먼트
.drawer-container {
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 100;
  // 메인 메뉴
  .left-main-menu-container {
    height: 100%;
    display: flex;
    flex-flow: column;
    background-image: linear-gradient(to right, rgba(19, 46, 69, 0.5) 2%, rgba(10, 80, 124, 0.7) 48%, #08214a 95%);
    // Kwater 로고 컨테이너
    .logo-container {
      width: 100%;
      height: 63px;
      display: flex;
      align-items: center;
      justify-content: center;
      // 로고 아이콘
      .logo-icon {
        width: 55px;
        height: 25px;
        background-image: url('../../assets/drawer/icon_logo.png');
        background-size: 100%;
      }
    }
    // 메인 메뉴 컨테이너
    .left-main-menu-item-container {
      width: 100%;
      height: 68px;
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
    }
    // 메인 메뉴 컨테이너(선택된)
    .left-main-menu-item-container.selected{
      background-color: rgba(255, 255, 255, 0.1);
    }
    // 메인 메뉴 아이템 텍스트(선택된)
    .left-main-menu-item-text {
      text-align: center;
      width: 100%;
      color: #828eb6;
      font-size: 11px;
    }
    // 메인 메뉴 아이템 텍스트
    .left-main-menu-item-text.selected {
      text-align: center;
      width: 100%;
      color: #fff;
      font-size: 11px;
    }
    // 메인 메뉴 아이콘
    .left-main-menu-item-icon {
      margin-top: 10px;
      margin-bottom: 12px;
    }
    // 메인 메뉴 대시보드 아이콘
    .left-main-menu-item-icon.dashboard {
      width: 33px;
      height: 24px;
      background-image: url('../../assets/drawer/icon_main_menu_dashboard.png');
      background-size: 100%;
    }
    // 메인 메뉴 대시보드 아이콘(선택된)
    .left-main-menu-item-icon.dashboard.selected {
      width: 33px;
      height: 24px;
      background-image: url('../../assets/drawer/icon_main_menu_dashboard_selected.png');
      background-size: 100%;
    }
    // 메인 메뉴 인공지능 아이콘
    .left-main-menu-item-icon.iAi {
      width: 27px;
      height: 27px;
      background-image: url('../../assets/drawer/icon_main_menu_iAi.png');
      background-size: 100%;
    }
    // 메인 메뉴 인공지능 아이콘(선택된)
    .left-main-menu-item-icon.iAi.selected {
      width: 27px;
      height: 27px;
      background-image: url('../../assets/drawer/icon_main_menu_iAi_selected.png');
      background-size: 100%;
    }
    // 메인 메뉴 아이콘
    .left-main-menu-item-icon.ao {
      width: 27px;
      height: 27px;
      background-image: url('../../assets/drawer/icon_main_menu_ao.png');
      background-size: 100%;
    }
    // 메인 메뉴 아이콘(선택된)
    .left-main-menu-item-icon.ao.selected {
      width: 27px;
      height: 27px;
      background-image: url('../../assets/drawer/icon_main_menu_ao_selected.png');
      background-size: 100%;
    }
    // EMS 아이콘
    .left-main-menu-item-icon.ems {
      width: 33px;
      height: 30px;
      background-image: url('../../assets/drawer/icon_main_menu_ems.png');
      background-size: 100%;
    }
    // EMS 아이콘(선택된)
    .left-main-menu-item-icon.ems.selected {
      width: 33px;
      height: 30px;
      background-image: url('../../assets/drawer/icon_main_menu_ems_selected.png');
      background-size: 100%;
    }
    // PMS 아이콘
    .left-main-menu-item-icon.pms {
      width: 33px;
      height: 30px;
      background-image: url('../../assets/drawer/icon_main_menu_pms.png');
      background-size: 100%;
    }
    // PMS 아이콘(선택된)
    .left-main-menu-item-icon.pms.selected {
      width: 33px;
      height: 30px;
      background-image: url('../../assets/drawer/icon_main_menu_pms_selected.png');
      background-size: 100%;
    }
    // 시스템 아이콘
    .left-main-menu-item-icon.system {
      width: 33px;
      height: 32px;
      background-image: url('../../assets/drawer/icon_main_menu_system.png');
      background-size: 100%;
    }
    // 시스템 아이콘(선택된)
    .left-main-menu-item-icon.system.selected {
      width: 33px;
      height: 32px;
      background-image: url('../../assets/drawer/icon_main_menu_system_selected.png');
      background-size: 100%;
    }
    // CCTV 아이콘
    .left-main-menu-item-icon.cctv {
      width: 41px;
      height: 28px;
      background-image: url('../../assets/drawer/icon_main_menu_cctv.png');
    }
    // CCTV 아이콘(선택된)
    .left-main-menu-item-icon.cctv.selected {
      width: 41px;
      height: 28px;
      background-image: url('../../assets/drawer/icon_main_menu_cctv_selected.png');
    }
  }
}

// veutify v-navigation-drawer 커스터마이징
.v-navigation-drawer {
  background-image: linear-gradient(91deg, rgba(11, 50, 95, 0.7) 1%, #021f3b 96%);
  .list-wrapper {
    display: flex;
  }
}
.v-list {
  padding: 0px;
}
.v-list-item:not(.v-list-item--active):not(.v-list-item--disabled) {
  color: #fff !important;
  opacity: 1 !important;
}
.v-list-item:not(.v-list-item--active):not(.v-list-item--disabled) .v-list-item__title{
  color: #fff !important;
  opacity: 1 !important;
}
.v-list-item--active{
  color: #36c7ff !important;
  opacity: 1 !important;
  border-right: 4px solid;
}
.v-list-item__action {
  margin-right: 16px !important;
}
.theme--light.v-list-item--active:hover::before, .theme--light.v-list-item--active::before {
  opacity: 0;
}
.v-list-item--active .v-icon{
  color: #36c7ff !important;
  opacity: 1 !important;
}
.v-application .primary--text {
  color: #36c7ff !important;
  caret-color: #36c7ff !important;
}
.blank:before{
  content: "";
}

// Drawer 손잡이
.drawer-handler {
  display:flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 65px;
  right: -13px;
  width: 13px;
  height: 31px;
  background-size: 100%;
  background-repeat: no-repeat;
  background-color: rgb(8, 33, 74);
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
  z-index: 100;
  // 삼각형
  .tri {
    width: 0px;
    height: 0px;
    border-top: 5px solid transparent;
    border-left: 5px solid white;
    border-bottom: 5px solid transparent;
  }
}

// Drawer 2단 컨테이너
.right-menu-container {
  height: 100%;
  background-image: linear-gradient(91deg, rgba(11, 50, 95, 0.7) 1%, #021f3b 95%);
  // 헤더 컨테이너
  .right-menu-header-container {
    display: inline-flex;
    align-items: center;
  }
  // 헤더 아이콘
  .right-menu-header-icon {
    margin-left: 13px;
    margin-right: 5px;
  }
  // 헤더 아이콘(메인)
  .right-menu-header-icon.ao {
    width: 17px;
    height: 16px;
    background-image: url('../../assets/drawer/icon_right_menu_ao.png');
    background-size: 100%;
  }
  // 헤더 아이콘(시스템)
  .right-menu-header-icon.sys {
    width: 17px;
    height: 16px;
    background-image: url('../../assets/drawer/icon_right_menu_sys.png');
    background-size: 100%;
  }
  // 헤더 텍스트
  .right-menu-header-text {
    font-size: 15px;
    color: #ffffff;
  }
}


</style>
