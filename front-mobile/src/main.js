// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import Vuex from 'vuex';
import Resource from 'vue-resource';
import _ from 'lodash';

import {
  XInput, XButton,
  Tabbar, TabbarItem,
  ButtonTab, ButtonTabItem,
  PopupPicker, Actionsheet,
  LoadingPlugin, ToastPlugin
} from 'vux';

import 'lib-flexible/flexible';

import App from './App';
import router from './router';

Vue.use(Vuex);
Vue.use(Resource);

_.forEach([
  XInput, XButton,
  Tabbar, TabbarItem,
  ButtonTab, ButtonTabItem,
  PopupPicker, Actionsheet,
], o => Vue.component(o.name, o));

_.forEach([
  LoadingPlugin, ToastPlugin
], o => Vue.use(o));

Vue.config.productionTip = false;

const store = new Vuex.Store({
  state() {
    const key_login_token = localStorage.getItem('key_login_token');
    Vue.http.headers.common['header-login-token'] = key_login_token;
    return {
      key_login_token,
      key_res_info: JSON.parse(localStorage.getItem('key_res_info') || '[]'),
      key_user_info: JSON.parse(localStorage.getItem('key_user_info') || '{}'),
      relogin: false,
    };
  },
  mutations: {
    login(state, { key_login_token, key_res_info, key_user_info }) {
      state.relogin = false;
      localStorage.setItem('key_login_token', key_login_token);
      localStorage.setItem('key_res_info', JSON.stringify(key_res_info));
      localStorage.setItem('key_user_info', JSON.stringify(key_user_info));
      Vue.http.headers.common['header-login-token'] = key_login_token;
      state.key_login_token = key_login_token;
      state.key_res_info = key_res_info;
      state.key_user_info = key_user_info;
    },
    logout(state) {
      const key_login_token = '';
      const key_res_info = [];
      const key_user_info = [];
      localStorage.setItem('key_login_token', key_login_token);
      localStorage.setItem('key_res_info', JSON.stringify(key_res_info));
      localStorage.setItem('key_user_info', JSON.stringify(key_user_info));
      state.key_login_token = key_login_token;
      state.key_res_info = key_res_info;
      state.key_user_info = key_user_info;
      Vue.http.headers.common['header-login-token'] = undefined;
    },
    relogin(state) {
      state.relogin = true;
    },
  },
});

const whiteList = ['/login'];
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
