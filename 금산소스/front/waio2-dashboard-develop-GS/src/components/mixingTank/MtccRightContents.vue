<template>
  <div class="main">
    <div class="top-contents">
      <div class="arrow-animate-one"></div>
      <!-- 주요인자 -->
      <div class="top-contents-container">
        <div class="top-contents-container__title">주요인자</div>
        <div class="top-container">
          <div class="top-one-contents-box">
            <div class="toggle-wrap">
              <div class="top-one-contents-box__text">· 교반강도(G)</div>
              <div class="btn-contents" style="width: 60px;">
                <div class="btn-contents__btn">
                  <input id="authchk" type="checkbox" :disabled="$store.state.login.user.tkn === null" class="checkbox" @click="openPopup" :class="{ 'checked': this.$store.state.mixing.latest.d_g_value_ctr_flag == 1 }">
                  <label for="authchk"></label>
                </div>
              </div>
            </div>
            <!-- 지별 제어 영역 추가 -->
            <div class="ji-contents-wrap">
              <p class="ji-contents-wrap__title">{{ this.$store.state.mixing.selectedFCLocation }}지</p>
              <div class="top-one-contents-value" v-if="this.$store.state.mixing.selectedFCLocation == 1">
                <!-- <div class="top-one-contents-value__text">#1</div> -->
                <input ref="gValueInputBox" v-if="this.$store.state.mixing.isModifyMode && this.$store.state.mixing.latest.d_g_value_ctr_flag == 0" type="text" class="top-one-contents-value__input" :value="this.$store.state.mixing.latestModify.d_g_value_loc1" v-on:input="updateInput($event, 'd_g_value_loc1')"/>
                <span class="top-one-contents-value__span" v-else>{{ this.$store.state.mixing.latest.d_g_value_ctr_flag == 0 ? this.$store.state.mixing.latest.d_g_value_loc1 : this.$store.state.mixing.latest.ai_d_g_value_loc1 | numFormat('0.0')}}</span>
                <div class="top-one-contents-value__unit">sec<sup>-1</sup></div>
              </div>
              <div class="top-one-contents-value" v-else>
                <!-- <div class="top-one-contents-value__text">#1</div> -->
                <input v-if="this.$store.state.mixing.isModifyMode && this.$store.state.mixing.latest.d_g_value_ctr_flag == 0" type="text" class="top-one-contents-value__input" :value="this.$store.state.mixing.latestModify.d_g_value_loc2" v-on:input="updateInput($event, 'd_g_value_loc2')"/>
                <span class="top-one-contents-value__span" v-else>{{ this.$store.state.mixing.latest.d_g_value_ctr_flag == 0 ? this.$store.state.mixing.latest.d_g_value_loc2 : this.$store.state.mixing.latest.ai_d_g_value_loc2 | numFormat('0.0')}}</span>
                <div class="top-one-contents-value__unit">sec<sup>-1</sup></div>
              </div>
              <div v-if="$store.state.login.user.tkn !== null && $store.state.mixing.latest.d_g_value_ctr_flag == 0" class="box-config--end">
                <div v-if="this.$store.state.mixing.isModifyMode" class="box-config--reset" @click="cancelControl"></div>
                <div v-if="this.$store.state.mixing.isModifyMode" class="box-config--confirm" @click="updateControl"><div class="box-config--check"></div><span>확인</span></div>
                <div v-else class="box-config--confirm" @click="checkControl"><span>설정</span></div>
              </div>
              <div class="auto-btn" v-show="$store.state.login.user.tkn !== null && $store.state.mixing.latest.d_g_value_ctr_flag == 1"><button class="auto-btn__detail" @click="openAutoDetail()">자동모드 상세</button></div> 
            </div>
          </div>
          <div class="top-two-contents-box">
            <div class="top-two-contents-value">
              <div class="top-two-contents-value__text">· 원수 수온 ({{ this.$store.state.mixing.selectedFCLocation == 1 ? 1 : 2 }}지)</div>
              <div class="top-two-contents-value__value">{{ this.$store.state.mixing.selectedFCLocation == 1 ? this.$store.state.mixing.latest.b_te_loc1 : this.$store.state.mixing.latest.b_te_loc2 | numFormat('0.00') }}</div>
              <div class="top-two-contents-value__unit">℃</div>
            </div>
            <div class="top-two-contents-value">
              <div class="top-two-contents-value__text">· 점성 계수 ({{ this.$store.state.mixing.selectedFCLocation == 1 ? 1 : 2 }}지)</div>
              <div class="top-two-contents-value__value">{{ this.$store.state.mixing.selectedFCLocation == 1 ? this.$store.state.mixing.latest.d_dv_loc1 : this.$store.state.mixing.latest.d_dv_loc2 | numFormat('0.00000') }}</div>
              <!-- 20260306 이현수 조영우 과장 변경 요청사항 점성계수 단위 '세제곱 미터' 에서 '제곱미터'로 수정 -->
              <div class="top-two-contents-value__unit">N·s/m<sup>2</sup></div>
            </div>
            <div class="top-two-contents-value">
              <div class="top-two-contents-value__text">· 임펠러 직경</div>
              <div class="top-two-contents-value__value">{{ this.$store.state.mixing.latest.d_im_d | numFormat('0.00') }}</div>
              <div class="top-two-contents-value__unit">m</div>
            </div>
            <div class="top-two-contents-value">
              <div class="top-two-contents-value__text">· Power Number</div>
              <div class="top-two-contents-value__value">{{ this.$store.state.mixing.latest.d_pw | numFormat('0.00') }}</div>
              <div class="top-two-contents-value__unit"></div>
            </div>
            <div class="top-two-contents-value">
              <div class="top-two-contents-value__text">· 조 체적</div>
              <div class="top-two-contents-value__value">{{ this.$store.state.mixing.latest.d_v | numFormat('0.00') }}</div>
              <div class="top-two-contents-value__unit"><span>m</span><sup>3</sup></div>
            </div>
          </div>
        </div>
      </div>
      <!-- AI 응집기 설정 속도 예측 -->
      <div class="top-two-contents-container">
        <div class="top-two-contents-container__title">
          AI 응집기 설정 속도 예측
          <div class="top-two-contents-container__timerbox-outter">
            <!-- 예측 결과 시간 -->
            <div class="timerbox">
              {{ this.$store.state.mixing.latest.upd_ti | moment('YYYY-MM-DD HH:mm:ss') }}
            </div>
          </div>
        </div>
        <div class="top-container">
          <div class="top-container__wrap">
            <div class="G-Value-contents">
              <div class="top-two-contents-value__G-text">현재G값</div>
              <div class="top-two-contents-value__G-Value">{{ this.getFCLocationG(this.$store.state.mixing.selectedFCLocation).step1['1'] | numFormat('0')  }}</div>
              <div class="top-two-contents-value__G-unit">S<sup>-1</sup></div>
            </div>
            <div class="top-two-contents-value">
              <div class="top-two-contents-value__text">· #1</div>
              <div class="top-two-contents-value__value"> {{ this.getAIFCLocationG(this.$store.state.mixing.selectedFCLocation) | numFormat('0') }}</div>
              <div class="top-two-contents-value__unit">S<sup>-1</sup></div>
            </div>
            <div class="contents-value-underbar"></div>
            <div class="G-Value-contents">
              <div class="top-two-contents-value__G-text">현재G값</div>
              <div class="top-two-contents-value__G-Value">{{ this.getFCLocationG(this.$store.state.mixing.selectedFCLocation).step2['1'] | numFormat('0') }}</div>
              <div class="top-two-contents-value__G-unit">S<sup>-1</sup></div>
            </div>            
            <div class="top-two-contents-value">
              <div class="top-two-contents-value__text">· #2</div>
              <div class="top-two-contents-value__value">{{ this.getAIFCLocationG(this.$store.state.mixing.selectedFCLocation) | numFormat('0') }}</div>
              <div class="top-two-contents-value__unit">S<sup>-1</sup></div>
            </div>
          </div>          
          <div class="top-container__wrap">
            <div class="top-container__real-box">
              <div class="top-container__real-text">#1 현재 응집기 속도</div>
              <div class="top-container__real-value">{{ this.getFCLocationSpeed(this.$store.state.mixing.selectedFCLocation).step1['1'] | numFormat('0.00') }}</div>
              <div class="top-container__real-unit">RPM</div>
            </div>
            <div class="top-two-contents-value">
              <div class="top-two-contents-value__text">· #1</div>
              <div class="top-two-contents-value__value">{{ this.getAIFCLocationSpeed(this.$store.state.mixing.selectedFCLocation).step1 | numFormat('0.00') }}</div>
              <div class="top-two-contents-value__unit">RPM</div>
            </div>
            <div class="contents-value-underbar"></div>
            <div class="top-container__real-box">
              <div class="top-container__real-text">#2 현재 응집기 속도</div>
              <div class="top-container__real-value"> {{ this.getFCLocationSpeed(this.$store.state.mixing.selectedFCLocation).step2['1'] | numFormat('0.00') }} </div>
              <div class="top-container__real-unit">RPM</div>
            </div>
            <div class="top-two-contents-value">
              <div class="top-two-contents-value__text">· #2</div>
              <div class="top-two-contents-value__value">{{ this.getAIFCLocationSpeed(this.$store.state.mixing.selectedFCLocation).step2 | numFormat('0.00') }}</div>
              <div class="top-two-contents-value__unit">RPM</div>
            </div>
          </div>
        </div> 
      </div>
    </div>
    <div class="bottom-contents">
      <!-- 원수 수온 차트 -->
      <div class="bottom-chart-box">
        <div class="bottom-chart-box__title">원수 수온</div>
        <div class="bottom-chart">
          <highcharts :options="ChartRawTemp" style="height:100%;"/>
        </div>
      </div>
      <!-- 응집기 설정 속도 예측 차트 -->
      <div class="bottom-chart-box">
        <div class="bottom-chart-box__title">응집기 설정 속도 예측</div>
        <div class="select-box">
          <div v-for="index in [1,2]" :key="index" class="select-box__element" @click="selectedLocation = index" :class="{ 'select-box__element--focused': selectedLocation === index}">{{ index }}지</div>
          </div>
        <div class="bottom-chart">
          <highcharts :options="ChartAGSpeed" style="height:100%;"/>
        </div>
      </div>
    </div>
    <!-- 교반강도 변경 팝업 d_g_value_ctr_flag-->
    <div v-if="this.$store.state.mixing.isPopupVisible" class="popup">
      <div class="popup__inner">
        <div class="popup__title">
          <div class="popup__icon"></div>
          <span>교반강도 변경</span>
        </div>
        <div class="popup__body">
          <!-- <span class="popup__body--large">{{ this.IsAIMode ? 'AI분석모드' : 'AI모드'}}</span>로 변경하시겠습니까? -->
          <span v-if="this.$store.state.mixing.latest.d_g_value_ctr_flag == 0" class="popup__body--large">자동모드로 변경하시겠습니까?</span>
          <span v-else-if="this.$store.state.mixing.latest.d_g_value_ctr_flag == 1" class="popup__body--large">수동모드로 변경하시겠습니까? <br> 교반강도(G)값을 입력 후 ‘확인'버튼을 클릭해주세요.</span>
        </div>
        <div class="popup__bottom">
          <div class="btn btn--cancel" @click="closePopup()">취소</div>
          <div class="btn btn--change" @click="changeGValue()">변경</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import * as XLSX from 'xlsx'
import { PUT_MIXING_CONTROL_AI } from '@/store/modules/mixing'

export default {
  name: 'MtccRightContents',
  data: () => ({
    // d_g_value_loc1: null,
    selectedLocation: 1, // 현재 선택된 지
    ji2: [],
    ji3: [],
    ji4: [],
    ji5: [],
    ji6: [],
    ji7: [],
    ji8: [],
    ji9: [],
    // 원수 수온 차트
    chartDataRawTemp: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        zoomType: 'x'
      },
      title: {
        useHTML: true,
        text: '원수 수온',
        style: {
          color: 'transparent'
        }
      },
      legend: {
        enabled: true,
        align: 'right',
        verticalAlign: 'top',
        borderWidth: 0,
        y: 0,
        itemStyle: {
          color: '#ffffff',
          fontWeight: 'bold'
        },
        labelFormatter: function () {
          return '<span style="color: ' + this.color + '; width: 100px;">' + this.name + '</span>'
        }
      },
      credits: {
        enabled: false
      },
      tooltip: {
        valueDecimals: 2,
        xDateFormat: '%Y-%m-%d %H:%M:%S',
        valueSuffix: '℃'
      },
      xAxis: {
        title: {
          text: ''
        },
        type: 'datetime',
        labels: {
          format: '{value:%m-%d %H:%M}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        },
        tickInterval: 1000 * 60 * 60 * 6 // 48시간
      },
      yAxis: {
        title: {
          align: 'high',
          text: '℃',
          useHTML: true,
          offset: 0,
          rotation: 0,
          x: -15,
          y: -15,
          style: {
            color:"rgb(255,255,255,0.8)"
          }
        },
        gridLineColor: false,
        labels: {
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        }
      },
      series: null,
      exporting: {
        buttons: {
          contextButton: {
            menuItems: ['viewFullscreen', 'downloadJPEG', 'downloadCSV'],
            menuItemStyle: { padding: "0 10px", background: "none", color: "#303030"},
            symbolStroke: "rgb(110, 157, 225)",
            theme: {
              fill:"rgba(0,0,0,0)",
              states: {
                hover: {
                  fill: 'rgba(0,0,0,0)',
                },
                select: {
                  fill: 'rgba(0,0,0,0)',
                }
              }
            },
            titleKey: null
          }
        },
        filename: null,
        fallbackToExportServer: false,
      }
    },
    // 응집기 설정 속도 예측 차트
    chartDataAGSpeed: {
      chart: {
        type: 'spline',
        backgroundColor: false,
        zoomType: 'x'
      },
      title: {
        useHTML: true,
        text: '응집기 설정 속도 예측',
        style: {
          color: 'transparent'
        }
      },
      legend: {
        enabled: true,
        align: 'right',
        verticalAlign: 'top',
        borderWidth: 0,
        y: 0,
        itemStyle: {
          color: '#ffffff',
          fontWeight: 'bold'
        },
        labelFormatter: function () {
          return '<span style="color: ' + this.color + '; width: 100px;">' + this.name + '</span>'
        }
      },
      credits: {
        enabled: false
      },
      tooltip: {
        valueDecimals: 2,
        xDateFormat: '%Y-%m-%d %H:%M:%S',
        valueSuffix: 'RPM'
      },
      xAxis: {
        title: {
          text: ''
        },
        type: 'datetime',
        labels: {
          format: '{value:%m-%d %H:%M}',
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        },
        tickInterval: 1000 * 60 * 60 * 6 // 48시간
      },
      yAxis: {
        title: {
          align: 'high',
          text: 'RPM',
          useHTML: true,
          offset: 0,
          rotation: 0,
          x: -15,
          y: -15,
          style: {
            color:"rgb(255,255,255,0.8)"
          }
        },
        gridLineColor: false,
        labels: {
          style:{
            fontFamily:'NanumSquare',
            fontSize: 13,
            color:"rgb(255,255,255,0.8)"
          }
        }
      },
      series: null,
      exporting: {
        buttons: {
          contextButton: {
            menuItems: ['viewFullscreen', 'downloadJPEG'],
            menuItemStyle: { padding: "0 10px", background: "none", color: "#303030"},
            symbolStroke: "rgb(110, 157, 225)",
            theme: {
              fill:"rgba(0,0,0,0)",
              states: {
                hover: {
                  fill: 'rgba(0,0,0,0)',
                },
                select: {
                  fill: 'rgba(0,0,0,0)',
                }
              }
            },
            titleKey: null
          }
        },
        filename: null,
        fallbackToExportServer: false,
      }
    }
  }),

  computed: {
    // 원수 수온 차트 옵션
    ChartRawTemp: function () {
      let chart = this.chartDataRawTemp
      chart.series = []
      //1지 수온
      if (this.$store.state.raw.te.d_te_loc1 !== null && Object.keys(this.$store.state.raw.te.d_te_loc1).length > 0) {
        let _data = []
        let min = null
        let max = null
        for (let key in this.$store.state.raw.te.d_te_loc1) {
          _data.push([new Date(key).getTime(), this.$store.state.raw.te.d_te_loc1[key]])
          if (min === null) {
            min = this.$store.state.raw.te.d_te_loc1[key]
          } else {
            if (this.$store.state.raw.te.d_te_loc1[key] < min) {
              min = this.$store.state.raw.te.d_te_loc1[key]
            }
          }
          if (max === null) {
            max = this.$store.state.raw.te.d_te_loc1[key]
          } else {
            if (this.$store.state.raw.te.d_te_loc1[key] > max) {
              max = this.$store.state.raw.te.d_te_loc1[key]
            }
          }
        }
        let yAxis = chart.yAxis
        if (min) { 
          yAxis.min = min - 0.5
        }
        if (max) {
          yAxis.max = max + 0.5
        }
        chart.yAxis = yAxis
        chart.series.push({
          name: '1지 수온',
          data: _data,
          color: '#b4dffb'
        })
      }
      //2지 수온
      if (this.$store.state.raw.te.d_te_loc2 !== null && Object.keys(this.$store.state.raw.te.d_te_loc2).length > 0) {
        let _data = []
        let min = null
        let max = null
        for (let key in this.$store.state.raw.te.d_te_loc2) {
          _data.push([new Date(key).getTime(), this.$store.state.raw.te.d_te_loc2[key]])
          if (min === null) {
            min = this.$store.state.raw.te.d_te_loc2[key]
          } else {
            if (this.$store.state.raw.te.d_te_loc2[key] < min) {
              min = this.$store.state.raw.te.d_te_loc2[key]
            }
          }
          if (max === null) {
            max = this.$store.state.raw.te.d_te_loc2[key]
          } else {
            if (this.$store.state.raw.te.d_te_loc2[key] > max) {
              max = this.$store.state.raw.te.d_te_loc2[key]
            }
          }
        }
        let yAxis = chart.yAxis
        if (min) { 
          yAxis.min = min - 0.5
        }
        if (max) {
          yAxis.max = max + 0.5
        }
        chart.yAxis = yAxis
        chart.series.push({
          name: '2지 수온',
          data: _data,
          color: '#8098ff'
        })
      }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_원수 수온'

      return chart
    },
    // 선택된 지의 속도
    ChartDataAGSpeedByLocation: function () {
      return this.$store.state.mixing.fc_sp['location' + this.selectedLocation]
    },
    // 응집기 설정 속도 예측 차트 옵션
    ChartAGSpeed: function () {
      let chart = this.chartDataAGSpeed
      let now = new Date 
      // let now = new Date("2013-09-04 00:00:00")  // FIXME 추후 삭제 
      chart.xAxis.plotLines = [{
        color: 'white',
        dashStyle: 'ShortDash',
        value: now.getTime(),
        width: 3,
      }]
      chart.series = []
      if (this.ChartDataAGSpeedByLocation.step1 !== null && Object.keys(this.ChartDataAGSpeedByLocation.step1).length > 0) {
        let _data = []
        for (let key in this.ChartDataAGSpeedByLocation.step1) {
          _data.push([new Date(key).getTime(), this.ChartDataAGSpeedByLocation.step1[key]])
        }
        chart.series.push({
          name: '#1',
          data: _data,
          color: '#b4dffb'
        })
      }
      if (this.ChartDataAGSpeedByLocation.step2 !== null && Object.keys(this.ChartDataAGSpeedByLocation.step2).length > 0) {
        let _data = []
        for (let key in this.ChartDataAGSpeedByLocation.step2) {
          _data.push([new Date(key).getTime(), this.ChartDataAGSpeedByLocation.step2[key]])
        }
        chart.series.push({
          name: '#2',
          data: _data,
          color: '#8098ff'
        })
      }
      // if (this.ChartDataAGSpeedByLocation.step3 !== null && Object.keys(this.ChartDataAGSpeedByLocation.step3).length > 0) {
      //   let _data = []
      //   for (let key in this.ChartDataAGSpeedByLocation.step3) {
      //     _data.push([new Date(key).getTime(), this.ChartDataAGSpeedByLocation.step3[key]])
      //   }
      //   chart.series.push({
      //     name: '#3',
      //     data: _data,
      //     color: '#a345ff'
      //   })
      // }
      chart.exporting.filename = this.$moment().format('YYYYMMDDHHmmss') + '_응집기 설정 속도 예측'

      const exportName = chart.exporting.buttons.contextButton.menuItems[2]
      if (exportName == undefined) {
        chart.exporting.buttons.contextButton.menuItems.push({
        text: 'Download Excel',
        onclick: this.downloadExcel
        })
      }

      return chart
    }
  },

  methods: {
    /**
     * 숫자 양의 정수 체크 함수
     * 
     * @param val 숫자
     * @return validation 여부
     */
    checkNumber: function(val) {
      const regex = /^[0-9]{1,2}$/ //eslint-disable-line
      if (!regex.test(val)) {
        return false		// 양의 정수만
      }
      return true
    },
    /**
     * 사용자 설정 input 값이 변경 되는 경우
     * 
     * @param event change 이벤트
     * @param key 변경되는 값
     */
    updateInput: function (event, key) {
      this.$store.state.mixing.latestModify[key] = event.target.value
    },
    /**
     * 선택된 지의 속도를 반환함
     * 
     * @param location 지
     * @return 선택된 지의 현재 속도
     */
    getFCLocationSpeed: function (location) {
      return this.$store.state.mixing.latest['d_loc_fc_sp' + location]
    },
    /**
     * 선택된 현재 G값을 반환함
     * 
     * @param location 지
     * @return 선택된 지의 현재 속도
     */
    getFCLocationG: function (location) {
      return this.$store.state.mixing.latest['d_loc_fc_g' + location]
    },
    /**
     * 선택한 지의 속도 반환
     * 
     * @param location 지
     * @return 현재 속도
     */
    getAIFCLocationSpeed: function(location) {
      return this.$store.state.mixing.latest['ai_d_loc_fc_sp' + location]
    },
    /**
     * 선택한 지의 예측 G값 반환
     * 
     * @param location 지
     * @return 현재 속도
     */
    getAIFCLocationG: function(location) {
      return this.$store.state.mixing.latest['ai_d_loc_fc_g' + location]
    },
    
    cancelControl: function() {
      this.$store.state.mixing.latestModify = Object.assign({}, this.$store.state.mixing.latest)
      this.$store.state.mixing.isModifyMode = !this.$store.state.mixing.isModifyMode
    },
    openAutoDetail: function(){
      this.$store.state.dialog.autoModeDetailPopup.visible = true;
      this.$store.state.mixing.isModifyMode = true
    },
    /**
     * 사용자 설정 업데이트 버튼 선택시 
     * 유효성 검사 후 사용자 설정 업데이트 API 요청
     */
    updateControl: function() {      
      let min = 0
      let max = 100
      if (this.$store.state.mixing.isModifyMode) {
        if (this.$store.state.mixing.latestModify.d_g_value_loc1 === ''
          || this.$store.state.mixing.latestModify.d_g_value_loc2 === '') {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '값을 입력해주세요' })
        } else if (parseFloat(this.$store.state.mixing.latestModify.d_g_value_loc1) <= min 
                  || parseFloat(this.$store.state.mixing.latestModify.d_g_value_loc1) >= max
                  || parseFloat(this.$store.state.mixing.latestModify.d_g_value_loc2) <= min
                  || parseFloat(this.$store.state.mixing.latestModify.d_g_value_loc2) >= max) {
          this.$store.dispatch('alertDialog/OPEN_DIALOG', { title: '경고', text1: '설정 범위', text2: min + ' ~ ' + max })
        } else {
          let obj = {}
          obj.d_g_value_loc1 = parseFloat(this.$store.state.mixing.latestModify.d_g_value_loc1)
          obj.d_g_value_loc2 = parseFloat(this.$store.state.mixing.latestModify.d_g_value_loc2)
          obj.d_g_value_ctr_flag = this.$store.state.mixing.latest.d_g_value_ctr_flag
          this.$store.dispatch(PUT_MIXING_CONTROL_AI, obj)
          this.$store.state.mixing.isModifyMode = !this.$store.state.mixing.isModifyMode
        }
      } else {
        this.$store.state.mixing.isModifyMode = !this.$store.state.mixing.isModifyMode
      }
    },
    checkControl: function() {
      this.$store.state.mixing.isModifyMode = !this.$store.state.mixing.isModifyMode
    },
    openPopup: function(){
      this.$store.state.mixing.isPopupVisible = !this.$store.state.mixing.isPopupVisible
    },
    closePopup: function(){
      this.$store.state.mixing.isPopupVisible = !this.$store.state.mixing.isPopupVisible
    },
    changeGValue: function() {//d_g_value_ctr_flag 변경
      if(this.$store.state.mixing.latest.d_g_value_ctr_flag == 0){
        //수동(0)->자동(1) 변경 요청 : ctr_flag 변경 및 Control API호출.
        this.$store.state.mixing.latest.d_g_value_ctr_flag = 1;
        let obj = {}
        obj.d_g_value_ctr_flag = this.$store.state.mixing.latest.d_g_value_ctr_flag
        this.$store.dispatch(PUT_MIXING_CONTROL_AI, obj)
        this.closePopup();
      }
      else{
        //자동(1) ->수동(0) 변경 요청 :ctr_flag 변경 및 텍스트 입력 focus.
        //(control API 호출은, G값 inputbox 하단 'v확인'버튼 클릭시 작용)
        this.$store.state.mixing.latest.d_g_value_ctr_flag = 0;
        this.closePopup();
        this.$store.state.mixing.isModifyMode = true;
        this.$nextTick(() => {
          this.$refs.gValueInputBox.focus();
        });
      }
    },
    downloadExcel: function() {
      const speedPrediction = this.$store.state.mixing.fc_sp

      console.log('speedPrediction', speedPrediction)
      
      for (let i = 0; i < Object.keys(speedPrediction).length; i++) {
        for (let speedPrediction_obj in speedPrediction['location' + (i + 2)]) {
          for (let time in speedPrediction['location' + (i + 2)]['step1']) {
            const excelLogs = {
              DateTime: '', 
              계열1: '',
              계열2: '',
              계열3: ''
            };

            excelLogs.DateTime = time
            excelLogs.계열1 = speedPrediction['location' + (i + 2)]['step1'][time]
            excelLogs.계열2 = speedPrediction['location' + (i + 2)]['step2'][time]
            excelLogs.계열3 = speedPrediction['location' + (i + 2)]['step3'][time]

            const speedPrediction_length = Object.keys(speedPrediction['location' + (i + 2)][speedPrediction_obj]).length
      
            if ( i === 0 ) {
              if( speedPrediction_length != this.ji2.length ) {
                this.ji2.push(excelLogs);
              }
            } else if ( i === 1 ) {
              if( speedPrediction_length != this.ji3.length ) {
                this.ji3.push(excelLogs);
              }
            } else if ( i === 2 ) {
              if( speedPrediction_length != this.ji4.length ) {
                this.ji4.push(excelLogs);
              }
            } else if ( i === 3 ) {
              if( speedPrediction_length != this.ji5.length ) {
                this.ji5.push(excelLogs);
              }
            } else if ( i === 4 ) {
              if( speedPrediction_length != this.ji6.length ) {
                this.ji6.push(excelLogs);
              }
            } else if ( i === 5 ) {
              if( speedPrediction_length != this.ji7.length ) {
                this.ji7.push(excelLogs);
              }
            } else if ( i === 6 ) {
              if( speedPrediction_length != this.ji8.length ) {
                this.ji8.push(excelLogs);
              }
            } else if ( i === 7 ) {
              if( speedPrediction_length != this.ji9.length ) {
                this.ji9.push(excelLogs);
              }
            } 
          }
          // console.log('ji2', this.ji2)
        }      
      }

      // 엑셀 워크시트로 json 내보내기
      const wb = XLSX.utils.book_new();

      const dataWS2 = XLSX.utils.json_to_sheet(this.ji2);
      const dataWS3 = XLSX.utils.json_to_sheet(this.ji3);
      const dataWS4 = XLSX.utils.json_to_sheet(this.ji4);
      const dataWS5 = XLSX.utils.json_to_sheet(this.ji5);
      const dataWS6 = XLSX.utils.json_to_sheet(this.ji6);
      const dataWS7 = XLSX.utils.json_to_sheet(this.ji7);
      const dataWS8 = XLSX.utils.json_to_sheet(this.ji8);
      const dataWS9 = XLSX.utils.json_to_sheet(this.ji9);
      
      XLSX.utils.book_append_sheet(wb, dataWS2, '2지'); 
      XLSX.utils.book_append_sheet(wb, dataWS3, '3지');
      XLSX.utils.book_append_sheet(wb, dataWS4, '4지');
      XLSX.utils.book_append_sheet(wb, dataWS5, '5지');
      XLSX.utils.book_append_sheet(wb, dataWS6, '6지');
      XLSX.utils.book_append_sheet(wb, dataWS7, '7지');
      XLSX.utils.book_append_sheet(wb, dataWS8, '8지');
      XLSX.utils.book_append_sheet(wb, dataWS9, '9지');

      XLSX.writeFile(wb, `${this.$moment().format('YYYYMMDDHHmmss')}_응집기 설정 속도 예측.xlsx`);
    }
  },
  /**
   * 마운트 해제시 
   * fullscreenchange 이벤트 해제
   */
  // beforeDestroy () { window.removeEventListener('fullscreenchange', this.fullscreenchanged) },
  /**
   * 사용자 설정 취소 버튼 선택시
   */
  watch: {

  }
}
</script>
<style lang="scss" scoped>
.arrow-animate-one{
  position: absolute;
  top: 252px;
  left: 1198px;
  background-image: url('../../assets/splashdown/arrow_img.png');
  width: 85px;
  height: 356px;
  animation-name: big-arrow-one;
  animation-duration: 5s;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
}
@keyframes big-arrow-one{ 
  0% {opacity:0; transform: translateX(-5px);}
  12% {opacity:0.5; transform: translateX(-2px);}
  24% {opacity:1; transform: translateX(0px);}
  36% {opacity:0.5; transform: translateX(2px);}
  48% {opacity:0; transform: translateX(5px);}
  60% {opacity:0;}
  72% {opacity:0;}
  84% {opacity:0;}
  100% {opacity:0;}
}
.main{
  width: 1202px;
  height: 794px;
  padding-left: 25px;
}
.bottom-contents{
  display: flex;
  width: 100%;
  height: 373px;
  padding-top: 20px;
  .bottom-chart-box{
    position: relative;
    width: 50%;
    height: 100%;
    .select-box{
      display: flex;
      position: absolute;
      right: 0px;
      top: 0px;
      &__element{
        display:flex;
        flex-direction: column;
        justify-content: center;
        align-content: center;
        width: 31px;
        height: 27px;
        border: solid 1px #b4dffa;
        background-color: rgba(139, 194, 240, 0.25);
        font-size: 12px;
        line-height: 1.8;
        text-align: center;
        color: #fff;
        cursor: pointer;
        &--focused {
          box-shadow: 0 0 10px 0 rgba(172, 207, 255, 0.7);
          border: solid 1px #b4dffa;
          background-color: rgba(139, 194, 240, 0.4);
        }
      }
    }
    .bottom-chart{
      width: 100%;
      height: 306px;
    }
    &__title{
      width: 196px;
      height: 47px;
      background-image: url('../../assets/mixingTank/bottom_title.png');
      text-shadow: 0 0 9px #5cafff;
      font-size: 18px;
      line-height: 1.11;
      text-align: center;
      color: #fff;
    }
  }
}
.top-contents{
  display: flex;
  width: 100%;
  height: 420px;
  .top-two-contents-container{
    position: relative;
    left: -46px;
    width: 558px;
    height: 100%;
    &__timerbox-outter {
      display: flex;
      position: relative;
      margin-left: auto;
      top: -3px;
    }
    &__title{
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: auto;
      width: 550px;
      height: 53px;
      background-image: url('../../assets/mixingTank/right_top_title.png');
      text-shadow: 0 0 9px #5cafff;
      font-size: 18px;
      line-height: 2.8;
      text-align: left;
      color: #fff;
      padding-left: 25px;
    }
    .top-container{
      display: flex;
      width:558px;
      height: 356px;
      background-image: url('../../assets/mixingTank/top_container_two.png');
      margin-top: 13px;
      padding-top: 20px;
      .G-Value-contents {
        display: flex;
        align-items: center;
        justify-content: flex-end;
      }      
      .contents-value-underbar {
        width: 100%;
        height: 6px;
        margin: 10px 0;
        background: url(../../assets/gs_images/underbar.png) no-repeat;
      }
      &__real-box {
        display: flex;
        align-items: center;
        padding-left: 28px;
      }
      &__real-text {
        text-align: left;
        color: #23a7c7;
        margin-left: 10px;
        text-shadow: 0 0 9px #a0d0ff;
      }
      &__real-value {
        font-size: 23px;
        text-align: left;
        color: #23a7c7;
        margin-left: 10px;
        text-shadow: 0 0 9px #a0d0ff;
        font-family: "LAB디지털" !important;
      }
      &__real-unit {
        font-size: 14px;
        color: #23a7c7;
        margin-left: 10px;
        text-shadow: 0 0 9px #5cafff;
      }          
      .top-two-contents-value{
        display: flex;
        height: 47px;
        align-items: baseline;
        &__text{
          width: 100px;
          text-shadow: 0 0 9px #5cafff;
          font-size: 18px;
          line-height: 2.3;
          text-align: left;
          color: #9fc4ff;
          margin-left: 25px;
        }
        &__value{
          width: 83px;
          font-size: 35px;
          text-align: right;
          color: #ccf1ff;
          font-family: "LAB디지털" !important;
        }
        &__unit{
          width: 46px;
          font-size: 14px;
          text-align: left;
          color: #417db9;
          margin-left: 16px;
        }
        &__G-text {
          text-align: left;
          color: #23a7c7;
          margin-left: 10px;
          text-shadow: 0 0 9px #a0d0ff;
        }
        &__G-Value {
          font-size: 23px;
          text-align: left;
          color: #23a7c7;
          margin-left: 10px;
          text-shadow: 0 0 9px #a0d0ff;
          font-family: "LAB디지털" !important;
        }
        &__G-unit {
          font-size: 14px;
          color: #23a7c7;
          margin-left: 10px;
          text-shadow: 0 0 9px #5cafff;
        }
      }
    }
  }
  .top-contents-container{
    width: 665px;
    height: 100%;
    .top-container{
      display: flex;
      width:665px;
      height: 356px;
      background-image: url('../../assets/mixingTank/top_container_one.png');
      margin-top: 13px;
      padding-top: 20px;
      .top-two-contents-box{
        width: 352px;
        height: 312px;
        padding-left: 30px;
        .top-two-contents-value{
          display: flex;
          height: 47px;
          align-items: center;
          &__text{
            width: 175px;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            line-height: 2.3;
            text-align: left;
            color: #9fc4ff;
          }
          &__value{
            width: 83px;
            text-shadow: 0 0 9px #5cafff;
            font-size: 20px;
            line-height: 2.35;
            text-align: right;
            color: #fff;
          }
          &__unit{
            width: 46px;
            font-size: 14px;
            line-height: 2.5;
            text-align: left;
            color: #417db9;
            margin-left: 16px;
          }
        }
      }
      .top-one-contents-box{
        width: 200px;
        height: 312px;
        .auto-btn {
          display: flex;
          justify-content: center;
          margin: 6px 0;
          &__detail {
            width: 150px;
            height: 30px;
            color: #fff;
            border: solid 1px #b4dffa;
            background-color: rgba(139, 194, 240, 0.25);
            font-size: 14px;
          }
        }            
        .top-one-contents-value{
          display: flex;
          justify-content: flex-end;
          height: 45px;
          align-items: center;
          &__text{
            width: 38px;
            text-shadow: 0 0 9px #5cafff;
            font-size: 18px;
            line-height: 2.3;
            text-align: right;
            color: #9fc4ff;
          }
          &__input{
            width: 64px;
            height: 34px;
            // padding: 10px 22px 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center !important;
            opacity: 1;
            border: solid 1px rgba(157, 191, 255, 0.5);
            background-color: rgba(157, 191, 255, 0.07);
            font-size: 15px;
            line-height: 1.07;
            text-align: left;
            color: #fff;
            margin: 0 auto;
          }
          &__span{
            width: 64px;
            height: 34px;
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 1;
            border: solid 1px rgba(157, 191, 255, 0.5);
            background-color: rgba(157, 191, 255, 0.07);
            font-size: 15px;
            line-height: 1.07;
            text-align: left;
            color: #fff;
          }
          &__unit{
            width: 50px;
            font-size: 14px;
            line-height: 2.5;
            text-align: left;
            color: #417db9;
            margin-left: 20px;
          }
        }
        &__text{
          text-shadow: 0 0 9px #5cafff;
          font-size: 18px;
          line-height: 1;
          text-align: left;
          color: #9fc4ff;
        }
      }
    }
    &__title{
      width: 297px;
      height: 53px;
      background-image: url('../../assets/mixingTank/right_top_title.png');
      text-shadow: 0 0 9px #5cafff;
      font-size: 18px;
      line-height: 2.8;
      text-align: left;
      color: #fff;
      padding-left: 25px;
    }
  }
}
.box-config {
  display: flex;
  flex-direction: column;
  width: 420px;
  height: 278px;
  background-image: url('../../assets/splashdown/background_box_config.png');
  margin: 0 auto;
  &--list {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 6px 0px;
  }
  &--list:nth-child(1) {
    margin-top: 15px;
  }
  &--end {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 6px 0px;
  }
  &--reset {
    width: 34px;
    height: 34px;
    background-image: url('../../assets/splashdown/reset_icon.png');
    background-position: center;
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.25);
    cursor: pointer;
  }
  &--confirm {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 149px;
    height: 34px;
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.25);
    font-size: 14px;
    color: #fff;
    margin-left: 5px;
    margin-right: 5px;
    cursor: pointer;
  }
  &--check {
    width: 14px;
    height: 10px;
    background-image: url('../../assets/splashdown/check_icon.png');
    background-position: center;
    margin-right: 5px;
}
}
  // 10.20 토글 추가
  .toggle-wrap {
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    line-height: 0;
    height: 40px;
  }
  .btn-contents {
  display: flex;
  align-items: center;
  width: 225px;
  margin-left: 8px;
  // 운전모드 제어 텍스트
  &__text{
    width: 76px;
    font-size: 15px;
    // line-height: 2.2;
    text-align: left;
    color: #c3eaff;
    text-shadow: 0 0 9px #5cafff;      
    padding-left: 5px;
  }
  // 운전모드 체크박스
  .checkbox{
    position:relative;
    cursor:pointer;
    appearance:none;
    width:60px;
    height:28px;
    border-radius: 14px;
    border: solid 1px #417290;
    background-color: rgba(139, 194, 240, 0.25);
    outline:none;
    transition:0.3s;
  }
  // 운전모드 체크박스(선택 안됐을 때)
  .checkbox::before{
    content:"수동";
    position:absolute;
    height:22px;
    width:29px;
    border-radius:11px;
    background:#b4dffa;
    top:2px;
    left:2px;
    transition:0.3s ease-in-out;
    font-size: 11px;
    font-family: KHNPHUotfR;
    line-height: 2;
    text-align: center;
    color: #19274e;
    background-color: #417290;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checked::before{
    content:"자동";
    transform:translateX(25px);
    background:#b4dffa;
  }
  // 운전모드 체크박스(선택 됐을 때)
  .checked{
    border-color:#b4dffa;
  }
}
// 팝업 관련 css추가
.popup{
  position: absolute;
  top: -85px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  justify-content: center;
  align-items: center;
  width: 1920px;
  height: 1150px;
  background-color: rgba(30,37,61,0.8);
  z-index: 200;
  &__inner{
    // width: 463px;
    // height: 213px;
    background-image: url('../../assets/dialog/background.png');
    padding: 25px;
    background-size: 100% 100%;
  }
  &__title {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    font-size: 24px;
    color: #b4dffb;
  }
  &__icon {
    width: 17px;
    height: 28px;
    margin-right: 15px;
    background-image: url('../../assets/dialog/ai_header_icon.png');
    background-size: 100% 100%;
  }
  &__body {
    padding: 10px 15px;
    font-size: 16px;
    color: #fff;
    &--large {
      font-size: 20px;
    }
  }
  &__bottom {
    display:flex;
    align-items: center;
    justify-content: flex-end;
    padding: 10px 15px;
  }
}
.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 105px;
  height: 34px;
  font-size: 15px;
  color: white;
  margin: 0px 10px;
  cursor: pointer;
  &--cancel {
    border: solid 1px #b4dffa;
    background-color: rgba(185, 255, 250, 0.25);
  }
  &--change {
    border: solid 1px #b4dffa;
    background-color: rgba(139, 194, 240, 0.25);
  }
}
// 24.02.16 지별 제어 추가
.ji-contents-wrap {
  width: 202px;
  height: 100%;
  background-image: url(../../assets/gs_images/ji_box.png);
  padding-top: 15px;
  margin-top: 12px;
  &__title {
    text-shadow: 0 0 9px #5cafff;
    font-size: 16px;
    text-align: center;
    color: #fff;
    background-image: url(../../assets/gs_images/ji_title.png);
    background-position: 50% 100%;
    height: 30px;
    margin-bottom: 10px;
  }
}
</style>