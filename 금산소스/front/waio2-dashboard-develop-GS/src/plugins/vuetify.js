import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';
import AoRw from '../assets/icons/AoRw.vue'
import AoCg from '../assets/icons/AoCg.vue'
import AoMtcc from '../assets/icons/AoMtcc.vue'
import AoSb from '../assets/icons/AoSb.vue'
import AoOz from '../assets/icons/AoOz.vue'
import AoFb from '../assets/icons/AoFb.vue'
import AoGfb from '../assets/icons/AoGfb.vue'
import AoDs from '../assets/icons/AoDs.vue'
import AoSending from '../assets/icons/AoSending.vue'
import AoDashboard from '../assets/icons/AoDashboard.vue'
import SysAlarmHistory from '../assets/icons/SysAlarmHistory.vue'
import SysPerformance from '../assets/icons/SysPerformance.vue'
import SysDashboardManagement from '../assets/icons/SysDashboardManagement.vue'
import SysAlarmManagement from '../assets/icons/SysAlarmManagement.vue'
import SysUserManagement from '../assets/icons/SysUserManagement.vue'
import SysLoginHistory from '../assets/icons/SysLoginHistory.vue'
import SysConfig from '../assets/icons/SysConfig.vue'
import WhiteCircle from '../assets/icons/WhiteCircle.vue'
import EMSAI from '../assets/icons/EMSAI.vue'
import EMSSpend from '../assets/icons/EMSSpend.vue'
import EMSReduce from '../assets/icons/EMSReduce.vue'
import EMSSetting from '../assets/icons/EMSSetting.vue'
import EMSDashboard from '../assets/icons/EMSDashboard'
import EMSReport from '../assets/icons/EMSReport'
Vue.use(Vuetify);

export default new Vuetify({
  icons: {
    values: {
      AoDashboard: {
        component: AoDashboard, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-dashboard'
        }
      },
      AoRw: {
        component: AoRw, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-rw'
        }
      },
      AoCg: {
        component: AoCg, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-cg'
        }
      },
      AoMtcc: {
        component: AoMtcc, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-mtcc'
        }
      },
      AoSb: {
        component: AoSb, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-sb'
        }
      },
      AoOz: {
        component: AoOz, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-oz'
        }
      },
      AoFb: {
        component: AoFb, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-fb'
        }
      },
      AoGfb: {
        component: AoGfb, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-gfb'
        }
      },
      AoDs: {
        component: AoDs, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-ds'
        }
      },
      SysAlarmHistory: {
        component: SysAlarmHistory, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'sys-alarm-history'
        }
      },
      SysPerformance: {
        component: SysPerformance, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'sys-performance'
        }
      },
      SysDashboardManagement: {
        component: SysDashboardManagement, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'sys-dashboard-management'
        }
      },
      SysAlarmManagement: {
        component: SysAlarmManagement, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'sys-alarm-management'
        }
      },
      SysUserManagement: {
        component: SysUserManagement, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'sys-user-management'
        }
      },
      SysLoginHistory: {
        component: SysLoginHistory, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'sys-login-history'
        }
      },
      SysConfig: {
        component: SysConfig, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'sys-config'
        }
      },
      WhiteCircle: {
        component: WhiteCircle, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'white-circle'
        }
      },
      AoSending: {
        component: AoSending, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ao-sending'
        }
      },
      EMSAI: {
        component: EMSAI, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ems-ai'
        }
      },
      EMSSpend: {
        component: EMSSpend, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ems-spend'
        }
      },
      EMSReduce: {
        component: EMSReduce, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ems-reduce'
        }
      },
      EMSSetting: {
        component: EMSSetting, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ems-setting'
        }
      },
      EMSDashboard: {
        component: EMSDashboard, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ems-dashboard'
        }
      },
      EMSReport: {
        component: EMSReport, // you can use string here if component is registered globally
        props: { // pass props to your component if needed
          name: 'ems-report'
        }
      },
    }
  }
});
