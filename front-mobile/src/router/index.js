import Vue from 'vue';
import Router from 'vue-router';
// import HelloWorld from '@/components/HelloWorld';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    // { path: '/', name: 'HelloWorld', component: HelloWorld },
    { path: '/login', component: () => import('@/components/Login') },
    { path: '/join', component: () => import('@/components/Join') },
    { path: '/',
      redirect: '/tab1',
      component: () => import('@/components/MainLayout'), children: [
        { path: '/tab1', component: () => import('@/components/MainTab1') },
        { path: '/tab2', component: () => import('@/components/MainTab2') },
        { path: '/tab3', component: () => import('@/components/MainTab3') },
        { path: '/tab4', component: () => import('@/components/MainTab4') },
      ]
    },
  ],
});
