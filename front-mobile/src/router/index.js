import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    { path: '/',component: () => import('@/components/MainLayout')},
    { path: '/login', component: () => import('@/components/Login')},
    { path: '/join', component: () => import('@/components/Join')},
    { path: '/reset', component: () => import('@/components/Reset')},
    { path: '/car_info',component: () => import('@/components/Info')},
  ],
});
