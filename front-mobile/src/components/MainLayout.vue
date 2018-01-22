<template>
  <div>

    <router-view />
    <tabbar>
      <tabbar-item link="/tab1">
        <i slot="icon" class="lt lt-jifenxinxi"></i>
        <span slot="label">车辆信息</span>
      </tabbar-item>
      <tabbar-item link="/tab2">
        <i slot="icon" class="lt lt-guanligenjin"></i>
        <span slot="label">提交报修</span>
      </tabbar-item>
      <tabbar-item link="/tab3">
        <i slot="icon" class="lt lt-huxingsousuo"></i>
        <span slot="label">查询报修</span>
      </tabbar-item>
      <tabbar-item link="/tab4">
        <i slot="icon" class="lt lt-renxiang"></i>
        <span slot="label">个人中心</span>
      </tabbar-item>
    </tabbar>
  </div>
</template>

<script>
export default {
  data() {
    return {
      msg: 'Welcome to Your Vue.js App',
    };
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

>>> .weui-tabbar__item .weui-tabbar__icon {
  width: auto;
  height: auto;

}
>>> .weui-tabbar__item i {
  color: #d6e7e0;
}

>>> .weui-tabbar__item.weui-bar__item_on i {
  color: #26bf81 !important;
}
>>> .weui-tabbar__item .weui-tabbar__label {
  color: #d6e7e0;
}
>>> .weui-tabbar__item.weui-bar__item_on .weui-tabbar__label {
  color: #26bf81 !important;
}
</style>
