import Vue from 'vue'
import VueRouter from 'vue-router'
import Dashboard from '@/components/dashboard/Dashboard'
import MtccAlgorithm from '@/components/mixingTank/MtccAlgorithm'
import PerformanceMonitoring from '@/components/PerformanceMonitoring/PerformanceMonitoring'
import AlarmManagement from '@/components/systemSettings/AlarmManagement'
import UserManagement from '@/components/systemSettings/UserManagement'
import LoginHistory from '@/components/systemSettings/LoginHistory'
import ConfigNetwork from '@/components/systemSettings/ConfigNetwork'
import AlarmHistory from '@/components/systemSettings/alarmHistory/AlarmHistory'
import CgAlgorithm from '@/components/coagulants/CgAlgorithm'
import CgSimulation from '@/components/coagulants/CgSimulation'
import ReceivingAlgorithm from '@/components/receiving/ReceivingAlgorithm' 
import SedimentationAlgorithm from '@/components/sedimentation/SedimentationAlgorithm'
import FilterAlgorithm from '@/components/filter/FilterAlgorithm'
import GACAlgorithm from '@/components/gac/GACAlgorithm'
import OzoneAlgorithm from '@/components/ozone/OzoneAlgorithm'
import DisinfectionAlgorithm from '@/components/disinfection/DisinfectionAlgorithm'
import store from '@/store'
import drawer from '@/store/modules/drawer.js'
import NotFoundComponent from '@/components/errorPage/NotFoundPage'
import AIOprHistoryRecord from '@/components/systemSettings/AIOprHistoryRecord'


Vue.use(VueRouter)

const routes = [
  { path: '/', name: 'Dashboard', component: Dashboard },
  { path: '/receivingAlgorithm', component: ReceivingAlgorithm, name: 'ReceivingAlgorithm' },
  { path: '/sedimentationAlgorithm', component: SedimentationAlgorithm, name: 'SedimentationAlgorithm' },
  { path: '/gacAlgorithm', component: GACAlgorithm, name: 'GACAlgorithm' },
  { path: '/filterAlgorithm', component: FilterAlgorithm, name: 'FilterAlgorithm' },
  { path: '/cgAlgorithm', component: CgAlgorithm, name: 'CgAlgorithm' },
  { path: '/cgSimulation', component: CgSimulation, name: 'CgSimulation' },
  { path: '/mtccAlgorithm', component: MtccAlgorithm, name: 'MtccAlgorithm'},
  { path: '/performanceMonitoring', component: PerformanceMonitoring, name: 'PerformanceMonitoring' },
  { path: '/alarmManagement', component: AlarmManagement, name: 'AlarmManagement', meta: { requiresAuth: true } },
  { path: '/userManagement', component: UserManagement, name: 'UserManagement', meta: { requiresAuth: true }},
  { path: '/loginHistory', component: LoginHistory, name: 'LoginHistory', meta: { requiresAuth: true } },
  { path: '/alarmHistory', component: AlarmHistory, name: 'AlarmHistory' },
  { path: '/configNetwork', component: ConfigNetwork, name: 'ConfigNetwork', meta: { requiresAuth: true } },
  { path: '/ozoneAlgorithm', component: OzoneAlgorithm, name: 'OzoneAlgorithm' },
  { path: '/preDisinfectionAlgorithm', component: DisinfectionAlgorithm, name: 'PreDisinfectionAlgorithm' }, //  소독 전차염
  { path: '/periDisinfectionAlgorithm', component: DisinfectionAlgorithm, name: 'PeriDisinfectionAlgorithm' }, //  소독 중차염
  { path: '/postDisinfectionAlgorithm', component: DisinfectionAlgorithm, name: 'PostDisinfectionAlgorithm' }, //  소독 후차염
  { path: '/aiOprHistoryRecord', component: AIOprHistoryRecord, name: 'AIOprHistoryRecord' }, //  AI 운영 이력
  { path: '/:catchAll(.*)', component: NotFoundComponent, name: 'NotFoundComponent'}
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})
router.beforeEach((to, from, next) => {
  console.log(from, to)

  // 상단 네비게이션 선택시 좌측 메뉴가 선택된 상태로 Open
  for (let i = 0; i < drawer.state.drawer.items.length; i++) {
    let item = drawer.state.drawer.items[i]
    if (item.children) {
      for(let j = 0; j < item.children.length; j++) {
        let childrenItem = item.children[j]
        if (childrenItem.route === to.path) {
          item.active = true
          childrenItem.active = true
          //공정 단계 지정 필요시 아래 로직에서 분기화
          if(item.process !== undefined){
            switch(item.process){
              case 'receiving':
                store.state[item.process].processStep = childrenItem.processStep
                break;
              case 'disinfection':
                store.state[item.process].disinfectionStep = childrenItem.processStep
                break;
            }
          }
        }
      }
    }
  }

  if (to.name === 'Dashboard') {
    store.state.backgroundIndex = 1
    store.state.drawer.selectedMainMenuIndex = 0
    store.state.selectedBuildingIndex = 0
  } else if (to.name === 'AlarmHistory' || to.name === 'PerformanceMonitoring' || to.name === 'AlarmManagement'  || to.name === 'UserManagement'  || to.name === 'LoginHistory' || to.name === 'ConfigNetwork' || to.name === 'AIOprHistoryRecord') {
    store.state.backgroundIndex = 2
    store.state.drawer.selectedMainMenuIndex = 5
  } else {
    store.state.backgroundIndex = 2
    store.state.drawer.selectedMainMenuIndex = 2
  }

   //주소 이동시, index를 별도 명시해야 하는 경우 (컴포넌트 공유시)
  // if(to.name === 'ReceivingAlgorithm'){
  //   store.state.receiving.processStep = 1
  // }else if(to.name ==='IndReceivingAlgorithm'){
  //   store.state.receiving.processStep = 2
  // }else if (to.name ==='PreDisinfectionAlgorithm'){
  //   store.state.disinfection.disinfectionStep = 1
  // }else if (to.name === 'PeriDisinfectionAlgorithm'){
  //   store.state.disinfection.disinfectionStep = 2
  // }else if (to.name ==='PostDisinfectionAlgorithm'){
  //   store.state.disinfection.disinfectionStep = 3
  // }


  if (to.name !== from.name) {
    let router = routes.filter((it) => it.name === to.name)
    // 경로가 router 에 존재하는 경우
    if (router.length > 0) {
      // 권한이 필요한 페이지 체크
      if (to.matched.some(record => record.meta.requiresAuth)) {
        // 로그인이 안됐을 경우 / 로 이동
        if (store.state.login.user.usr_id === null) {
          next('/')
        }
      }
      next()
    // 경로가 router에 존재하지 않는 경우 / 로 이동
    } else {
      next('/')
    }
  }

})
export default router
