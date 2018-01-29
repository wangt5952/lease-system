<template>
  <div style="display:flex;height:100%;">
    <div style="width:200px;">
      <el-menu :router="true" unique-opened>
        <el-submenu index="0">
          <template slot="title">
            <i class="lt lt-jiankong"></i>
            <span>监控</span>
          </template>
          <el-menu-item index="/monitor">车辆监控</el-menu-item>
        </el-submenu>

        <el-submenu index="1">
          <template slot="title">
            <i class="lt lt-diandongche"></i>
            <span>车辆</span>
          </template>
        </el-submenu>

        <el-submenu index="2">
          <template slot="title">
            <i class="lt lt-iconset0250"></i>
            <span>电池及配件</span>
          </template>
        </el-submenu>

        <el-submenu index="3">
          <template slot="title">
            <i class="lt lt-scsxx"></i>
            <span>制造商</span>
          </template>
          <el-menu-item index="/manufacturer">制造商管理</el-menu-item>
        </el-submenu>

        <el-submenu index="4">
          <template slot="title">
            <i class="lt lt-web-icon-"></i>
            <span>企业</span>
          </template>
          <el-menu-item index="/organization">企业管理</el-menu-item>
        </el-submenu>

        <el-submenu index="5">
          <template slot="title">
            <i class="lt lt-quanxian"></i>
            <span>权限</span>
          </template>
          <el-menu-item index="/user">用户管理</el-menu-item>
          <el-menu-item index="/role">角色管理</el-menu-item>
          <el-menu-item index="/resource">资源管理</el-menu-item>
        </el-submenu>
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
