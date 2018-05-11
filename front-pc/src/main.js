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
      orgPhotoPath: 'http://192.168.1.123:8090/leaseupload/otherimg/', // 企业照片路径
      userIconPath: 'http://192.168.1.123:8090/leaseupload/usericon/', // 用户图标路径
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
    relogin(state) {
      state.relogin = true;
    },
  },
});

// 白名单
const whiteList = ['/login', '/resetPassword'];
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
