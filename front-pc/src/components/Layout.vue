<template>
  <div style="display:flex;height:100%;">
    <div style="width:200px;">
      <el-menu :router="true">
        <el-menu-item index="/organization">
          <i class="el-icon-menu"></i>
          <span slot="title">企业管理</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <i class="el-icon-setting"></i>
          <span slot="title">人员管理</span>
        </el-menu-item>
        <el-menu-item index="/manufacturer">
          <i class="el-icon-setting"></i>
          <span slot="title">制造商管理</span>
        </el-menu-item>
        <el-menu-item index="/map" >
          <i class="el-icon-location"></i>
          <span slot="title">地图</span>
        </el-menu-item>
      </el-menu>
    </div>
    <router-view style="flex:1;"/>


  </div>
</template>

<script>
export default {
  data() {
    return {
      msg: 'Welcome to Your Vue.js App1',
    };
  },
};
</script>

<style scoped>
>>> .el-menu {
  height: 100%;
}
</style>
