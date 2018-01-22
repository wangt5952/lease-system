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

Vue.config.productionTip = false;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>',
});
