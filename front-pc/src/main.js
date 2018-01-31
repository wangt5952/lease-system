// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import Vuex from 'vuex';
import Resource from 'vue-resource';

import ElementUI from 'element-ui';
import BaiduMap from 'vue-baidu-map';

import 'element-ui/lib/theme-chalk/index.css';

import App from './App';
import router from './router';

Vue.use(Vuex);
Vue.use(Resource);
Vue.use(ElementUI);
Vue.use(BaiduMap, {
  ak: 'NmRvD46XSX0n2jOYGNZhK2jA9Bw6yGT0',
});

Vue.config.productionTip = false;


const store = new Vuex.Store({
  state: {
    key_login_token: localStorage.getItem('key_login_token'),
    key_res_info: JSON.parse(localStorage.getItem('key_res_info') || '[]'),
    key_user_info: JSON.parse(localStorage.getItem('key_user_info') || '{}'),
  },
  mutations: {
    set(state, { key, value }) {
      state[key] = value;
    },
    login(state, { key_login_token, key_res_info, key_user_info }) {
      localStorage.setItem('key_login_token', key_login_token);
      localStorage.setItem('key_res_info', JSON.stringify(key_res_info));
      localStorage.setItem('key_user_info', JSON.stringify(key_user_info));
      // Vue.http.headers.common.Authorization = token;
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
      // Vue.http.headers.common.Authorization = undefined;
    },
  },
});

const whiteList = ['/login']
router.beforeEach(async (to, from, next) => {
  const { key_login_token } = store.state;
  if(!key_login_token && whiteList.indexOf(to.path) === -1) return next({path: '/login'})
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
