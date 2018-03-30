<template>
  <div style="display:flex;flex-direction:column;height:100%;">
    <div style="background:#05002a;height:64px;display:flex;align-items:center;padding:0 10px;">
      <div style="flex:1;"></div>
      <el-dropdown @command="command => this[command]()">
        <span class="el-dropdown-link" style="cursor:pointer;color:#fff;">
          {{key_user_info.userName}} <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="showPasswordForm">密码修改</el-dropdown-item>
          <template v-if="key_user_info.userType === 'INDIVIDUAL'">
            <div @click="showForm(key_user_info.id)">
              <el-dropdown-item command="showForms">
                查看个人信息
              </el-dropdown-item>
            </div>
          </template>
          <template v-if="key_user_info.userType === 'ENTERPRISE'">
            <div @click="showEnterpriseForm(key_user_info)">
              <el-dropdown-item command="showForms">
                修改企业信息
              </el-dropdown-item>
            </div>
          </template>
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

    <el-dialog title="密码修改" :visible.sync="passwordFormVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="passwordForm" ref="passwordForm">
        <el-form-item prop="password0" :rules="[{required:true, message:'请输入旧密码'}]" label="旧密码">
          <el-input type="password" v-model="passwordForm.password0" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item prop="password" :rules="[{required:true, message:'请输入新密码'}]" label="新密码">
          <el-input type="password" v-model="passwordForm.password" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item prop="password2" :rules="[{required:true, message:'请再次新密码'}]" label="确认新密码">
          <el-input type="password" v-model="passwordForm.password2" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="passwordFormVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPasswordForm">提交修改</el-button>
      </span>
    </el-dialog>

    <el-dialog title="用户登录" :visible.sync="loginFormVisible" :show-close="false" :close-on-click-modal="false">
      <el-form class="edit-form" :model="loginForm" ref="passwordForm">
        <el-form-item prop="loginName" :rules="[{required:true, message:'请输入用户名'}]" label="用户名">
          <el-input v-model="loginForm.loginName" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item prop="password" :rules="[{required:true, message:'请输入用户名'}]" label="密码">
          <el-input type="password" v-model="loginForm.password" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmLoginForm">登录</el-button>
      </span>
    </el-dialog>
    <!-- 人员信息 -->
    <el-dialog title="人员信息" :visible.sync="formVisible" width="80%" :before-close="closeForms" :close-on-click-modal="false">
      <el-table :data="list"  style="width: 100%;margin-top:10px;">
        <el-table-column prop="loginName" label="用户名"></el-table-column>
        <el-table-column prop="userTypeText" label="用户类型"></el-table-column>
        <el-table-column prop="nickName" label="昵称"></el-table-column>
        <el-table-column prop="userName" label="姓名"></el-table-column>
        <el-table-column prop="orgName" label="所属企业"></el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeForm">关闭</el-button>
      </span>
    </el-dialog>

    <el-dialog title="企业信息" :visible.sync="enterpriseVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="loginName" :rules="[{required:true, message:'请填写用户名'}]" label="用户名">
              <el-input v-model="form.loginName" placeholder="请填写用户名" auto-complete="off" :disabled="disabledForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userType" :rules="[{required:true, message:'请选择用户类型'}]" label="用户类型">
              <el-select v-model="form.userType" placeholder="请选择用户类型" style="width:100%;" :disabled="disabledForm">
                <el-option v-for="o in typeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.userStatus" placeholder="请选择状态" style="width:100%;" :disabled="disabledForm">
                <el-option v-for="o in statusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item label="姓名" prop="userName" :rules="[{required:true, message:'请填写用户姓名'}]">
              <el-input v-model="form.userName" placeholder="请输入姓名" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="昵称">
              <el-input v-model="form.nickName" placeholder="请输入昵称" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeEnterpiseForm">取消</el-button>
        <el-button type="primary" @click="saveEnterpiserForm">保存</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import _ from 'lodash';
import moment from 'moment';
import md5 from 'js-md5';

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
      { name: '车辆管理', path: '/vehicle', resCode: 'manager-vehicle-list' },
      // { name: '车辆管理', path: '/vehicle', resCode: 'vehicle_manager' },
    ],
  },
  { name: '电池/配件/设备',
    icon: 'lt lt-iconset0250',
    children: [
      { name: '电池管理', path: '/battery', resCode: 'manager-battery-list' },
      { name: '配件管理', path: '/parts', resCode: 'manager-parts-list' },
      { name: '设备管理', path: '/device', resCode: 'manager-device-list' },
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
      { name: '用户管理', path: '/user', resCode: 'manager-user-list' },
      { name: '角色管理', path: '/role', resCode: 'manager-role-list' },
      { name: '资源管理', path: '/resource', resCode: 'manager-res-listicon' },
      { name: '申请管理', path: '/apply', resCode: 'manager-user-list' },
    ],
  },
];

export default {
  data() {
    return {
      passwordFormVisible: false,
      passwordForm: {},

      loginFormVisible: this.$store.state.relogin,
      loginForm: {},
      formVisible: false,
      list: [],
      statusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'INVALID', name: '作废' },
      ],
      typeList: [
        { id: 'PLATFORM', name: '平台' },
        { id: 'ENTERPRISE', name: '企业' },
        { id: 'INDIVIDUAL', name: '个人' },
      ],
      authList: [
        { id: 'AUTHORIZED', name: '已实名' },
        { id: 'UNAUTHORIZED', name: '未实名' },
      ],
      // 企业
      enterpriseVisible: false,
      disabledForm: false,
      form: {},
      orgList: [],

    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
      key_res_info: state => state.key_res_info,
      relogin: state => state.relogin,
    }),

    menuTree() {
      const resList = _.map(this.key_res_info, 'resCode');
      const travTree = root => _.filter(_.map(root, ({ children, ...o }) => {
        if (children && children.length && (!o.resCode || resList.indexOf(o.resCode) !== -1)) {
          return { ...o, children: travTree(children) };
        }
        return o;
      }), o => (o.resCode && resList.indexOf(o.resCode) !== -1) || (o.children && o.children.length));

      return travTree(menuTree);
    },
  },
  watch: {
    relogin(newVal) {
      if (newVal) {
        this.loginFormVisible = true;
      }
    },
  },
  methods: {
    // 企业
    closeEnterpiseForm() {
      this.enterpriseVisible = false;
    },
    async showEnterpriseForm({ id }) {
      this.enterpriseVisible = true;
      this.disabledForm = true;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/user/getbypk', [id])).body;
        if (code !== '200') throw new Error(message);
        const { key_user_info } = respData;
        this.form = key_user_info;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async saveEnterpiserForm() {
      try {
        const { code, message } = (await this.$http.post('/api/manager/user/modify', this.form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('保存成功');
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      this.enterpriseVisible = false;
    },
    async getOrgList() {
      const { code, respData } = (await this.$http.post('/api/manager/org/list', {
        currPage: 1, pageSize: 999,
      })).body;
      if (code === '200') this.orgList = respData.rows;
    },
    // 取消
    closeForm() {
      this.list = [];
      this.formVisible = false;
    },
    // 关闭之前的回调
    closeForms() {
      this.formVisible = false;
      this.list = [];
    },
    showForms() {
    },
    async showForm(id) {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/user/getbypk', [id])).body;
        if (code !== '200') throw new Error(message);
        const { key_user_info } = respData;
        this.list.push(key_user_info);
        this.list = _.map(this.list, o => ({
          ...o,
          userTypeText: (_.find(this.typeList, { id: o.userType }) || {}).name,
        }));
        this.formVisible = true;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleLogout() {
      await this.$store.commit('logout');
      this.$router.replace('/login');
    },

    showPasswordForm() {
      this.passwordForm = {};
      this.passwordFormVisible = true;
    },
    async confirmPasswordForm() {
      try {
        const $form = this.$refs.passwordForm;
        await $form.validate();
        const { password0, password, password2 } = this.passwordForm;

        if (password !== password2) throw new Error('新密码两次输入不一致');
        const authTime = moment().unix() * 1000;
        const { loginName } = this.key_user_info;
        const form = {
          oldAuthStr: md5(loginName + md5(password0).toUpperCase() + authTime).toUpperCase(),
          authTime,
          newPassword: md5(password).toUpperCase(),
        };

        const { code, message } = (await this.$http.post('/api/manager/auth/modifypassword', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('修改密码成功');
        this.passwordFormVisible = false;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    async confirmLoginForm() {
      const { password, ...form } = this.loginForm;
      const loginTime = moment().unix() * 1000;
      form.loginAuthStr = md5(form.loginName + md5(password).toUpperCase() + loginTime).toUpperCase();
      form.loginTime = loginTime;

      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/auth/login', form)).body;
        if (code !== '200') throw new Error(message || code);
        const { key_login_token, key_res_info, key_user_info } = respData;
        await this.$store.commit('login', { key_login_token, key_res_info, key_user_info });
        this.$message.success({
          message: `欢迎回来，${key_user_info.userName}`,
        });
        this.$router.go(0);
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      // this.$router.push('/');
    },
  },
  async mounted() {
    this.loading = true;
    await this.getOrgList();
    this.loading = false;
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
