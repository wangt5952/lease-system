// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import Vuex from 'vuex';
import Mint from 'mint-ui';
import Resource from 'vue-resource';
import _ from 'lodash';
import BaiduMap from 'vue-baidu-map';

import {
  XInput, XButton,
  Tabbar, TabbarItem,
  ButtonTab, ButtonTabItem,
  PopupPicker, Actionsheet,
  LoadingPlugin, ToastPlugin, ConfirmPlugin,
} from 'vux';

import 'lib-flexible/flexible';

import App from './App';
import router from './router';

Vue.use(Vuex);
Vue.use(Mint);
Vue.use(Resource);
Vue.use(BaiduMap, { ak: 'NmRvD46XSX0n2jOYGNZhK2jA9Bw6yGT0' });

_.forEach([
  XInput, XButton,
  Tabbar, TabbarItem,
  ButtonTab, ButtonTabItem,
  PopupPicker, Actionsheet,
], o => Vue.component(o.name, o));

_.forEach([
  LoadingPlugin, ToastPlugin, ConfirmPlugin,
], o => Vue.use(o));

Vue.config.productionTip = false;

const store = new Vuex.Store({
  state() {
    const key_login_token = localStorage.getItem('key_login_token');
    Vue.http.headers.common['header-login-token'] = key_login_token;
    return {
      key_login_token,
      key_user_info: JSON.parse(localStorage.getItem('key_user_info') || '{}'),
      relogin: false,
      // 车辆ID
      vehicleID: localStorage.getItem('vehicleId') || '',
    };
  },
  mutations: {
    login(state, { key_login_token, key_user_info }) {
      state.relogin = false;
      localStorage.setItem('key_login_token', key_login_token);
      localStorage.setItem('key_user_info', JSON.stringify(key_user_info));
      Vue.http.headers.common['header-login-token'] = key_login_token;
      state.key_login_token = key_login_token;
      state.key_user_info = key_user_info;
    },
    update(state, { key_user_info }) {
      localStorage.setItem('key_user_info', JSON.stringify(key_user_info));
      state.key_user_info = key_user_info;
    },
    // 登出
    logout(state) {
      // 删除本地信息
      localStorage.removeItem('key_login_token');
      localStorage.removeItem('key_user_info');
      localStorage.removeItem('vehicleId');
      // 给状态赋值
      state.vehicleId = '';
      state.key_login_token = '';
      state.key_user_info = {};
      Vue.http.headers.common['header-login-token'] = undefined;
    },
    // 重新给车辆ID赋值
    setVehicleId(state, id) {
      localStorage.setItem('vehicleId', id);
      state.vehicleId = id;
    },
    relogin(state) {
      state.relogin = true;
    },
  },
});

const whiteList = ['/login', '/join', '/reset'];
router.beforeEach(async (to, from, next) => {
  const { key_login_token } = store.state;
  if (!key_login_token && whiteList.indexOf(to.path) === -1 && to.fullPath !== '/login') {
    next({
      path: '/login',
    });
  } else {
    next();
  }
});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>',
});
