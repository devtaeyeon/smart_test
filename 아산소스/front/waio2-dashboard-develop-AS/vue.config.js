const path = require('path')
const TerserPlugin = require('terser-webpack-plugin');
const isProd = process.env.NODE_ENV === 'production'

module.exports = {
  // webpack 설정을 보다 자세히할 때 이용하며, csv 파일을 읽을 때 필요한 raw-loader을 지정
  // chainWebpack: config => {
  //   config.module
  //     .rule('raw')
  //     .test(/\.csv$/)
  //     .use('raw-loader')
  //     .loader('raw-loader')
  //     .end()
  // },
  // node_modules에 있는 vuetify파일들도 bablel 적용
  transpileDependencies: [
    'vuetify'
  ],
  // api 요청시 proxy 설정(Cross-Origin Resource Sharing 문제 해결)
  devServer: {
    // '/api_xai'로 요청시 target으로 넘기며 '/api_xai'를 '/', 그리고 Origin을 변경함
    proxy: {
      '/api_xai': {
        target: 'http://localhost:38081',
        changeOrigin: true,
        pathRewrite: {
          '^/api_xai': '/'
        }
      }
    }
  },
  /** 웹 팩 설정*/
  // terser 플러그인 설정(prod mode 인 경우 console.log 제외)
  /** 웹 팩 설정*/
  configureWebpack: {
    devtool: 'source-map',
    optimization: {
        minimize: true,
        minimizer: isProd ? [
            new TerserPlugin({
                terserOptions: {
                    ecma: 6,
                    compress: { drop_console: true },
                    output: { comments: false, beautify: false }
                }
            })
        ] : []
    }
  },
}
