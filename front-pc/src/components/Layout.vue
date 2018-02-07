<template>
  <div style="display:flex;flex-direction:column;height:100%;">

    <div style="background:#05002a;height:64px;display:flex;align-items:center;padding:0 10px;">
      <div style="flex:1;"></div>
      <el-dropdown @command="command => this[command]()">
        <span class="el-dropdown-link" style="cursor:pointer;color:#fff;">
          {{key_user_info.userName}} <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="handleLogout">退出系统</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <div style="display:flex;flex:1;">
      <div style="width:250px;">
        <el-menu :router="true" unique-opened>
          <template v-for="(o, i) in menuTree">
            <el-submenu v-if="o.children" :key="i" :index="`${i}`">
              <template slot="title">
                <i :class="o.icon"></i>
                <span>{{o.name}}</span>
              </template>
              <el-menu-item v-for="(p, j) in o.children" :key="j" :index="p.path">{{p.name}}</el-menu-item>
            </el-submenu>
            <el-menu-item v-else :key="i" :index="`${i}`">
              <template slot="title">
                <i :class="o.icon"></i>
                <span>{{o.name}}</span>
              </template>
            </el-menu-item>
          </template>
        </el-menu>
      </div>
      <router-view style="flex:1;" />
    </div>
  </div>
</template>

<script>
import _ from 'lodash';
import {
  mapState,
} from 'vuex';

const menuTree = [
  { name: '监控',
    icon: 'lt lt-jiankong',
    children: [
      { name: '车辆监控', path: '/monitor', resCode: 'vehicle_monitor' },
    ],
  },
  { name: '车辆',
    icon: 'lt lt-diandongche',
    children: [
      { name: '车辆管理', path: '/vehicle', resCode: 'vehicle_manager' },
    ],
  },
  { name: '电池及配件',
    icon: 'lt lt-iconset0250',
    children: [
      { name: '电池管理', path: '/battery', resCode: 'battery_manager' },
      { name: '配件管理', path: '/parts', resCode: 'parts_manager' },
    ],
  },
  { name: '制造商',
    icon: 'lt lt-scsxx',
    children: [
      { name: '制造商管理', path: '/mfrs', resCode: 'mfrs_manager' },
    ],
  },
  { name: '企业',
    icon: 'lt lt-web-icon-',
    children: [
      { name: '企业管理', path: '/organization', resCode: 'org_manager' },
    ],
  },
  { name: '权限',
    icon: 'lt lt-quanxian',
    children: [
      { name: '用户管理', path: '/user', resCode: 'user_manager' },
      { name: '角色管理', path: '/role', resCode: 'role_manager' },
      { name: '资源管理', path: '/resource', resCode: 'res_manager' },
    ],
  },
];

export default {
  data() {
    return {
      msg: 'Welcome to Your Vue.js App1',
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
      key_res_info: state => state.key_res_info,
    }),

    menuTree() {
      const resList = _.map(this.key_res_info, 'resCode');
      console.log(resList);

      const travTree = root => _.filter(_.map(root, ({ children, ...o }) => {
        if (children && children.length && (!o.resCode || resList.indexOf(o.resCode) !== -1)) {
          return { ...o, children: travTree(children) };
        }
        return o;
      }), o => (o.resCode && resList.indexOf(o.resCode) !== -1) || (o.children && o.children.length));

      return travTree(menuTree);
    },
  },
  methods: {
    async handleLogout() {
      await this.$store.commit('logout');
      this.$router.replace('/login');
    },
  },
};
</script>

<style scoped>
>>> .el-menu {
  height: 100%;
  background: #1c2166;
  border-right: 0;
}

>>> .el-menu .el-submenu .el-submenu__title,
>>> .el-menu .el-submenu .el-submenu__title i,
>>> .el-menu .el-menu-item {
  color: #fff;
}

>>> .el-menu .el-submenu .el-submenu__title:hover,
>>> .el-menu .el-submenu .el-submenu__title:hover i,
>>> .el-menu .el-menu-item:hover,
>>> .el-menu .el-menu-item.is-active {
  background: #1a0d53;
  color: #4ba4f3;
}
</style>
