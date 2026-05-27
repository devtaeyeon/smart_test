export default {
  state: {
    usr_id: [
      v => !!v || '아이디를 입력해주세요'
    ],
    usr_pw: [
      v => !!v || '비밀번호를 입력해주세요'
    ],
    tel: [
      v => !!v || '전화번호를 입력해주세요'
    ],
    email: [
      v => !!v || '이메일을 입력해주세요'
    ],
    usr_pn: [
      v => !!v || '부서명을 입력해주세요'
    ],
    position: [
      v => !!v || '직책을 입력해주세요'
    ],
    usr_nm: [
      v => !!v || '이름을 입력해주세요'
    ],
    search: [
      v => !!v || '검색어를 입력해주세요'
    ],
    dp_nm: [
      v => !!v || '표시명을 입력해주세요'
    ],
    cmp_val: [
      v => !!v || '비교값을 입력해주세요',
      v => v <= 100 || '100보다 작은 수를 입력해주세요.',
      v => v >= 1 || '1보다 큰 수를 입력해주세요'
    ],
    ip_address: [
      v => !!v || 'IP 주소를 입력해주세요.'
    ],
    port: [
      v => String(v).length > 0 || 'Port를 입력해주세요.'
    ],
    number: [
      v => {
        const pattern = /^\d+$/
        return pattern.test(v) || '숫자를 입력해주세요.'
      }
    ],
    float: [
      v => {
        const pattern = /^\d+([.]\d+){0,1}$/
        return pattern.test(v) || '정수 또는 실수를 입력해주세요.'
      }
    ],
    required: [
      v => !!v || '값을 입력해주세요'
    ],
    diatom_avg: [
      v => {
        const pattern = /^\d+-\d+$/
        return pattern.test(v) || '범위값(ex.220-240)을 입력해주세요'
      }
    ]
}
}