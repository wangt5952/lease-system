import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    { path: '/login', component: () => import('@/components/Login') },
    { path: '/join', component: () => import('@/components/Join') },
    { path: '/reset', component: () => import('@/components/Reset') },
    { path: '/', component: () => import('@/components/MainLayout') },
    { path: '/info/:id', component: () => import('@/components/Info') },
    { path: '/track', component: () => import('@/components/Track') },
    { path: '/mycar', component: () => import('@/components/MyCar') },
    { path: '/profile', component: () => import('@/components/Profile') },
    { path: '/parts/:id', component: () => import('@/components/Parts') },
    { path: '/authentication_step1', component: () => import('@/components/Authentication1') },
    { path: '/authentication_step10', component: () => import('@/components/Authentication10') },
    { path: '/authentication_step2/:id', component: () => import('@/components/Authentication2') },
    { path: '/authentication_step3', component: () => import('@/components/Authentication3') },
    { path: '/nickName', component: () => import('@/components/NickName') },
    { path: '/repassword', component: () => import('@/components/RePassword') },
    { path: '/upload', component: () => import('@/components/Upload') },
  ],
});
