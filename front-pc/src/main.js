// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import Vuex from 'vuex';
import Resource from 'vue-resource';

import ElementUI from 'element-ui';
import BaiduMap from 'vue-baidu-map';
import VueBase64FileUpload from 'vue-base64-file-upload';

import 'element-ui/lib/theme-chalk/index.css';

import App from './App';
import router from './router';

Vue.use(Vuex);
Vue.use(Resource);
Vue.use(ElementUI, { size: 'small' });
Vue.use(BaiduMap, {
  ak: 'NmRvD46XSX0n2jOYGNZhK2jA9Bw6yGT0',
});
Vue.component('vue-base64-file-upload', VueBase64FileUpload);

Vue.config.productionTip = false;

const store = new Vuex.Store({
  state() {
    const key_login_token = localStorage.getItem('key_login_token');
    if (key_login_token) Vue.http.headers.common['header-login-token'] = key_login_token;
    return {
      key_login_token,
      key_res_info: JSON.parse(localStorage.getItem('key_res_info')) || [],
      key_user_info: JSON.parse(localStorage.getItem('key_user_info')) || {},
      relogin: false,
      // 验证码token
      smsToken: '',
      // 验证码
      smsVCode: '',
      // 验证码按钮的全局状态
      tokenButtonState: false,
      // 验证码按钮的全局样式
      tokenButtonType: 'primary',
      // 全局时间
      time: localStorage.getItem('time'),

      orgPhotoPath: 'http://106.14.172.38:8990/leaseupload/otherimg/', // 企业照片路径
      userIconPath: 'http://106.14.172.38:8990/leaseupload/usericon/pc/', // 用户图标路径
      userPidPath: 'http://106.14.172.38:8990/leaseupload/userrealname/', // 用户身份证图片

    };
  },
  mutations: {
    set(state, { key, value }) {
      state[key] = value;
    },
    login(state, info) {
      state.relogin = false;
      localStorage.setItem('key_login_token', info.key_login_token);
      localStorage.setItem('key_res_info', JSON.stringify(info.key_res_info));
      localStorage.setItem('key_user_info', JSON.stringify(info.key_user_info));
      Vue.http.headers.common['header-login-token'] = info.key_login_token;
      state.key_login_token = info.key_login_token;
      state.key_res_info = info.key_res_info;
      state.key_user_info = info.key_user_info;
    },
    // 重新获取用户信息
    reload(state, { key_user_info }) {
      localStorage.setItem('key_user_info', JSON.stringify(key_user_info));
      state.key_user_info = key_user_info;
    },
    logout(state) {
      localStorage.removeItem('key_login_token');
      localStorage.removeItem('key_res_info');
      localStorage.removeItem('key_user_info');
      state.key_login_token = '';
      state.key_res_info = [];
      state.key_user_info = {};
      Vue.http.headers.common['header-login-token'] = undefined;
    },
    // 重新登录
    relogin(state) {
      state.relogin = true;
    },
    // 验证码token
    setSmsToken(state, smsToken) {
      state.smsToken = smsToken;
    },
    // 验证码
    setSmsVCode(state, smsVCode) {
      state.smsVCode = smsVCode;
    },
    // 验证码按钮状态(true, false) 正确规范
    // ['setTokenButtonState'](state, buttonState) {
    //   state.tokenButtonState = buttonState;
    // },
    setTokenButtonState(state, buttonState) {
      state.tokenButtonState = buttonState;
    },
    // 验证码按钮样式('primary', 'info')
    setTokenButtonType(state, buttonType) {
      state.tokenButtonType = buttonType;
    },
    // 倒计时
    setTime(state, time) {
      state.time = time;
      localStorage.setItem('time', time);
    },
  },
  actions: {
    tokenButtonStyle({ commit, getters }, time) {
      commit('setTokenButtonState', true);
      let times = time;
      const timeOut = setInterval(() => {
        commit('setTokenButtonType', 'info');
        times -= 1;
        commit('setTime', times);
        if (getters.time === 0) {
          clearInterval(timeOut);
          commit('setTokenButtonType', 'primary');
          commit('setTokenButtonState', false);
        }
      }, 1 * 1000);
    },
  },
  getters: {
    time: state => state.time,
  },
});

// 白名单
const whiteList = ['/login', '/resetPasswordOne', '/resetPasswordTwo'];
// 全局导航守卫
router.beforeEach(async (to, from, next) => {
  const { key_login_token } = store.state;
  if (!key_login_token && whiteList.indexOf(to.path) === -1) return next({ path: '/login' });
  return next();
});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>',
});
