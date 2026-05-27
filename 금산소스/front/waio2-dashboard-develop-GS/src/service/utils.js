export const util = {
  methods: {
    // getMaxListById: function(name) {
    //   return Math.max(this.$store.state.svgLists.filter(it => {if (it.id.includes(name)) { return it }}).map(it => parseInt(it.id.replace(/[^0-9]/g,''))))
    // }
    getTimeString: function (stamp) {
      let start = new Date(stamp + (9 * 60 * 60 * 1000)).toISOString()
      return start.split('T')[0] + ' ' + start.split('T')[1].split('.')[0]
    },
    getMaxId: function (obj) {
      return (Math.max.apply(Math, obj.map(function(o) {
        return o.id;
      })));
    },
    getNextId: function (obj) {
      return (Math.max.apply(Math, obj.map(function(o) {
        return o.id;
      })) + 1);
    },
    fillZero: function (width, str) {
      // console.log('str: ', str)
      // return str.length >= width ? str : new Array(width - str.length+1).join('0') + str // 남는 길이만큼 0으로 채움
      return str.length >= width ? str : '0' + str // 남는 길이만큼 0으로 채움
    },
    fillZeros: function (n, digits) {
      let zero = ''
      n = n.toString()
      if (n.length < digits) {
        for (let i = 0; i < digits - n.length; i++) {
          zero += '0'
        }
      }
      return zero + n
    },
    getNowTimeStamp: function () {
      let d = new Date()
      let s = this.fillZeros(d.getFullYear(), 4) + '-' +
        this.fillZeros(d.getMonth() + 1, 2) + '-' +
        this.fillZeros(d.getDate(), 2) + ' ' +
        this.fillZeros(d.getHours(), 2) + ':' +
        this.fillZeros(d.getMinutes(), 2) + ':' +
        this.fillZeros(d.getSeconds(), 2)
        return s
      // let s = util.fillZeros(d.getFullYear(), 4) + '-' +
      //   util.fillZeros(d.getMonth() + 1, 2) + '-' +
      //   util.fillZeros(d.getDate(), 2) + ' ' +
      //   util.fillZeros(d.getHours(), 2) + ':' +
      //   util.fillZeros(d.getMinutes(), 2) + ':' +
      //   util.fillZeros(d.getSeconds(), 2)
      //   return s
    },
    getPathColor: function (pathColorIndex) {
      if (pathColorIndex === 0) {
        return '#6BA1F8'
      } else if (pathColorIndex === 1) {
        return '#50BCA3'
      } else {
        return '#888772'
      }
    },
    getPathColorIndex: function (pathColor) {
      if (pathColor === '#6BA1F8') {
        return 0
      } else if (pathColor === '#50BCA3') {
        return 1
      } else {
        return 2
      }
    },
    getMaxListById: function (list, name) {
      console.log('getMaxListById')
      console.log('list: ', list)

      if (list === undefined || list === null) {
        return 0
      }

      console.log('1: ', list.filter(it => {if (it.id.includes(name)) { return it }}))
      console.log('2: ', list.filter(it => {if (it.id.includes(name)) { return it }}).map(it => parseInt(it.id.replace(/[^0-9]/g,''))))
      console.log('3: ', Math.max.apply(Math, list.filter(it => {if (it.id.includes(name)) { return it }}).map(it => parseInt(it.id.replace(/[^0-9]/g,'')))))
      let idGroup = list.filter(it => {if (it.id.includes(name)) { return it }}).map(it => parseInt(it.id.replace(/[^0-9]/g,'')))
      console.log('idGroup: ', idGroup.length)
      if (idGroup.length === 0) {
        return 0
      } else {
        return Math.max.apply(Math, list.filter(it => {if (it.id.includes(name)) { return it }}).map(it => parseInt(it.id.replace(/[^0-9]/g,''))))
      }
    },
    getPathLength: function (points) {
      let length = 0
      for(let i = 0; i < points.length - 1; ++i) {
        length += Math.sqrt((points[i].x - points[i + 1].x) * (points[i].x - points[i + 1].x) + (points[i].y - points[i + 1].y) * (points[i].y - points[i + 1].y))
      }
      return length
    },
    getPathPtr: function (points, speedOffset = 3) {
      let wholeLength = this.getPathLength(points)
      // console.log('wholeLength: ', wholeLength)
      let x, y, x1, y1, x2, y2, dx, dy, length = 0
      // dx = dy = length / 1000
      dx = dy = 0
      // let stdLength = 1000 // 기준 길이

      // let stdLengthRate = stdLength / wholeLength

      let ptr = []
      ptr.push({ x: points[0].x, y: points[0].y })
      for (let i = 0; i < points.length - 1; ++i) {
        x1 = points[i].x
        y1 = points[i].y
        x2 = points[i + 1].x
        y2 = points[i + 1].y
        // console.log('x1: ', x1, 'y1: ', y1, 'x2: ', x2, 'y2: ', y2)
        length = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
        let coff = ((length / wholeLength) * 100 * speedOffset)
        // console.log('length: ', length, 'coff: ', coff)
        dx = (x2 - x1) / coff
        dy = (y2 - y1) / coff
        x = x1 + dx
        y = y1 + dy
        // console.log('dx: ', dx, 'dy: ', dy, 'x: ', x, 'y: ', y, 'dist: ', (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y))
        while ((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y) < length * length) {
          ptr.push({x: x, y: y})
          x += dx
          y += dy
        }
      }
      return ptr
    },
    toMMSS: function (totalSecond) {
      var sec_num = parseInt(totalSecond, 10); // don't forget the second param
      var hours   = Math.floor(sec_num / 3600);
      var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
      var seconds = sec_num - (hours * 3600) - (minutes * 60);
  
      // if (hours   < 10) {hours   = "0"+hours;}
      if (minutes < 10) {minutes = "0"+minutes;}
      if (seconds < 10) {seconds = "0"+seconds;}
      // return hours+':'+minutes+':'+seconds;
      return minutes+':'+seconds;
    },
    bytesToSizeWithUnit: function (bytes) {
      var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
      if (bytes == 0) return '0 Byte'
      var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)))
      return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i]
    },
    bytesToSize: function (bytes) {
      if (bytes == 0) return '0'
      var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)))
      return Math.round(bytes / Math.pow(1024, i), 2)
    },
    groupBy: function (list, keyGetter) {
      const map = new Map()
      list.forEach((item) => {
        const key = keyGetter(item)
        const collection = map.get(key)
        if (!collection) {
          map.set(key, [item])
        } else {
          collection.push(item)
        }
      })
      return map
    },
    validateIPaddress: function (inputText) {
      let ipformat = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
      if (ipformat.test(inputText)) {
        return true
      } else {
        return false
      }
    },
    validateNumber: function (inputText) {
      var format= /^[0-9]+$/
      if (format.test(inputText)) {
        return true
      } else {
        return false
      }
    }
  },
  printError(error) {
    if (error.response) {
      // 요청이 이루어졌으며 서버가 2xx의 범위를 벗어나는 상태 코드로 응답했습니다.
      console.log(error.response.data)
      console.log(error.response.status)
      console.log(error.response.headers)
    } else if (error.request) {
      // 요청이 이루어 졌으나 응답을 받지 못했습니다.
      // `error.request`는 브라우저의 XMLHttpRequest 인스턴스 또는
      // Node.js의 http.ClientRequest 인스턴스입니다.
      console.log(error.request);
    } else {
      // 오류를 발생시킨 요청을 설정하는 중에 문제가 발생했습니다.
      console.log('Error', error.message)
    }
    console.log(error.config)
  },
  objectInitialize:  function (obj) {
    for( let key in obj ) {
      // if (typeof obj[key] === "number")
      //   self.obj[key] = null;   // 정수는 0 으로 초기화
      // else if (typeof obj[key] === "string")
      //   self.obj[key] = null;   // 문자는 '' 으로 초기화
      // else if (typeof obj[key] === "boolean")
      //   self.obj[key] = null;   // boolean 는 false 으로 초기화
      // else if (typeof obj[key] === "array")
      //   self.obj[key] = null;   // 배열은 [] 으로 초기화
      if (typeof obj[key] === "object") {
        util.objectInitialize(obj[key], true)
      } else if (typeof obj[key] === "boolean") {
        obj[key] = false
      } else {
        obj[key] = null
      }
      console.log(key, obj[key])
    }
  }
}