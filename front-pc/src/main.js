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

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>',
});
