import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    { path: '/login', component: () => import('@/components/Login') },
    { path: '/resetPassword', component: () => import('@/components/ResetPassword') },
    { path: '/',
      component: () => import('@/components/Layout'),
      children: [
        { path: '/organization', component: () => import('@/components/Organization') },
        { path: '/resource', component: () => import('@/components/Resource') },
        { path: '/role', component: () => import('@/components/Role') },
        { path: '/user', component: () => import('@/components/User') },
        { path: '/user', component: () => import('@/components/User') },
        { path: '/mfrs', component: () => import('@/components/Manufacturer') },
        { path: '/vehicle', component: () => import('@/components/Vehicle') },
        { path: '/battery', component: () => import('@/components/Battery') },
        { path: '/parts', component: () => import('@/components/Parts') },

        { path: '/monitor', component: () => import('@/components/Monitor') },
      ],
    },
  ],
});
