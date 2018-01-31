<template>
  <div style="display:flex;height:100%;">
    <div style="width:200px;">
      <el-menu :router="true" unique-opened>
        <template v-for="(o, i) in menuTree">
          <el-submenu v-if="o.children" :index="`${i}`">
            <template slot="title">
              <i :class="o.icon"></i>
              <span>{{o.name}}</span>
            </template>
            <el-menu-item v-for="p in o.children" :index="p.path">{{p.name}}</el-menu-item>
          </el-submenu>
          <el-menu-item v-else :index="`${i}`">
            <template slot="title">
              <i :class="o.icon"></i>
              <span>{{o.name}}</span>
            </template>
          </el-menu-item>
        </template>
      </el-menu>
    </div>

    <div style="flex:1;display:flex;flex-direction:column;">
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
      { id: '', name: '车辆监控', path: '/monitor', resCode: 'vehicle_monitor' },
    ],
  },
  { name: '车辆', icon: 'lt lt-diandongche', children: [
    { name: ''}
  ]},
  { name: '电池及配件', icon: 'lt lt-iconset0250', resCode: 'battery_parts' },
  { name: '制造商', icon: 'lt lt-scsxx' },
  { name: '企业',
    icon: 'lt lt-web-icon-',
      children: [
      { id: '', name: '企业管理', path: '/organization' },
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

      const travTree = (root) => {
        return _.filter(_.map(root, ({ children, ...o}) => {
          if(children && children.length && (!o.resCode || resList.indexOf(o.resCode) !== -1)){
            return { ...o, children: travTree(children) }
          }
          return o;
        }), o => (o.resCode && resList.indexOf(o.resCode)) || (o.children && o.children.length))
      }

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
}
</style>
