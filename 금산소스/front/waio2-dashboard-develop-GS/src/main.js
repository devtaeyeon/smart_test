import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import numeral from 'numeral'
import numFormat from 'vue-filter-number-format'

import Highcharts from 'highcharts'
import HighchartsMore from "highcharts/highcharts-more"
import Exporting from "highcharts/modules/exporting"
import ExportingData from "highcharts/modules/export-data"
import OfflineExporting from "highcharts/modules/offline-exporting"
import xrangeInit from 'highcharts/modules/xrange'
import stockInit from 'highcharts/modules/stock'
HighchartsMore(Highcharts)
xrangeInit(Highcharts)
stockInit(Highcharts)
Exporting(Highcharts)
ExportingData(Highcharts)
OfflineExporting(Highcharts)


// 하이차트 UTC설정
Highcharts.setOptions({
  global: {
    useUTC: false
  },
  lang: {
    thousandsSep: ','
  }
})
Vue.use(Highcharts)

Vue.filter('numFormat', numFormat(numeral))

Vue.config.productionTip = false

new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app')
