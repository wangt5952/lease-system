import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    { path: '/login', component: () => import('@/components/Login') },
    { path: '/',
      component: () => import('@/components/Layout'),
      children: [
        { path: '/organization', component: () => import('@/components/Organization') },
        { path: '/resource', component: () => import('@/components/Resource') },
        { path: '/role', component: () => import('@/components/Role') },
        { path: '/user', component: () => import('@/components/User') },
        { path: '/user', component: () => import('@/components/User') },
        { path: '/manufacturer', component: () => import('@/components/Manufacturer') },
        { path: '/map', component: () => import('@/components/Map') },
      ],
    },
  ],
});
