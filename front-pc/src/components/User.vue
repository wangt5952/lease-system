<template>
  <div v-loading="loading" style="padding:10px;display:flex:1;display:flex;flex-direction:column;">

    <div style="display:flex;">
      <div style="margin-right:10px;">
        <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加人员</el-button>
      </div>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="search.keyStr" placeholder="登录名/手机号码/昵称/姓名/身份证号/所属企业Code/所属企业名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.userStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.userType" placeholder="请选择类型" style="width:100%;">
            <el-option v-for="o in searchTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="list" height="100%"  style="width: 100%;margin-top:10px;">
      <el-table-column prop="loginName" label="用户名"></el-table-column>
      <el-table-column prop="userMobile" label="手机号"></el-table-column>
      <el-table-column prop="userTypeText" label="用户类型"></el-table-column>
      <el-table-column prop="userIcon" label="用户LOGO"></el-table-column>
      <el-table-column prop="nickName" label="昵称"></el-table-column>
      <el-table-column prop="userName" label="姓名"></el-table-column>
      <el-table-column prop="userRealNameAuthFlagText" label="实名认证"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="{row}">
          <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
          <el-button icon="el-icon-edit" size="mini" type="text" @click="showAssignRoleForm(row)">分配角色</el-button>
          <el-tooltip content="删除" placement="top">
            <el-button icon="el-icon-delete" size="mini" type="text" @click="handleDelete(row)"></el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total" style="margin-top:10px;"
      @size-change="handleSizeChange"
      @current-change="reload"
      :current-page.sync="currentPage"
      :page-sizes="pageSizes"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>

    <el-dialog title="人员信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="loginName" :rules="[{required:true, message:'请填写用户名'}]" label="用户名">
              <el-input v-model="form.loginName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userMobile" :rules="[{required:true, message:'请填写手机号码'}]" label="手机号码">
              <el-input v-model="form.userMobile" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userType" :rules="[{required:true, message:'请选择用户类型'}]" label="用户类型">
              <el-select v-model="form.userType" placeholder="请选择用户类型" style="width:100%;">
                <el-option v-for="o in typeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="LOGO路径">
              <el-input v-model="form.userIcon" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="密码" :rules="[{required:true, message:'请填写密码'}]">
              <el-input v-model="form.password" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="昵称">
              <el-input v-model="form.nickName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="姓名">
              <el-input v-model="form.userName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userRealNameAuthFlag" :rules="[{required:true, message:'请选择实名认证类型'}]" label="实名认证">
              <el-select v-model="form.userRealNameAuthFlag" placeholder="请选择实名认证类型" style="width:100%;">
                <el-option v-for="o in authList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身份证号">
              <el-input v-model="form.userPid" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身份证正面">
              <el-input v-model="form.userIcFront" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身份证背面">
              <el-input v-model="form.userIcBack" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="手举身份证合照">
              <el-input v-model="form.userIcGroup" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.userStatus" placeholder="请选择状态" style="width:100%;">
                <el-option v-for="o in statusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeForm">取消</el-button>
        <el-button type="primary" @click="saveForm">{{form.id ? '保存' : '添加'}}</el-button>
      </span>
    </el-dialog>

    <el-dialog :title="`分配角色 ( ${assignRoleForm.name} ) `" :visible.sync="assignRoleFormVisible" :close-on-click-modal="false">
      <el-form :model="form" ref="form" v-loading="loading && assignRoleFormVisible">
        <el-row :gutter="10">
          <el-col :span="24">
            <el-form-item label="角色">
              <el-select v-model="assignRoleForm.list" :placeholder="`请选择角色`" style="width:100%;" multiple>
                <el-option v-for="o in roleList" :key="o.id" :label="o.roleName" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer" >
        <el-button @click="closeAssignRoleForm">取消</el-button>
        <el-button :disabled="loading" type="primary" @click="saveAssignRoleForm">{{form.id ? '保存' : '添加'}}</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import _ from 'lodash';
import {
  mapState,
} from 'vuex';

export default {
  data() {
    return {
      loading: false,
      list: [],

      search: {
        userType: '',
        userStatus: '',
      },

      pageSizes: [10, 50, 100, 200],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      formVisible: false,
      form: {},

      typeList: [
        { id: 'PLATFORM', name: '平台' },
        { id: 'ENTERPRISE', name: '企业' },
        { id: 'INDIVIDUAL', name: '个人' },
      ],
      authList: [
        { id: 'AUTHORIZED', name: '已实名' },
        { id: 'UNAUTHORIZED', name: '未实名' },
      ],
      statusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结' },
        { id: 'INVALID', name: '作废' },
      ],

      searchTypeList: [
        { id: '', name: '全部类型'},
        { id: 'PLATFORM', name: '平台' },
        { id: 'ENTERPRISE', name: '企业' },
        { id: 'INDIVIDUAL', name: '个人' },
      ],
      searchStatusList: [
        { id: '', name: '全部状态'},
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结' },
        { id: 'INVALID', name: '作废' },
      ],

      assignRoleFormVisible: false,
      assignRoleForm: { list: [] },

      roleList: [],
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
    }),
  },
  watch: {
    search: {
      async handler() {
        await this.reload();
      },
      deep: true,
    },
  },
  methods: {
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },

    async reload() {
      this.loading = true;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/user/list', {
          currPage: this.currentPage, pageSize: this.pageSize, ...this.search,
        })).body;
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.total = total;
        this.list = _.map(rows, o => ({
          ...o,
          userTypeText: (_.find(this.typeList, { id: o.userType }) || {}).name,
          userRealNameAuthFlagText: (_.find(this.authList, { id: o.userRealNameAuthFlag }) || {}).name,
        }));
        this.loading = false;
      } catch (e) {
        this.loading = false;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, name }) {
      try {
        await this.$confirm(`确认删除${name}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/user/delete', [id])).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('删除成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    showForm(form = { }) {
      this.form = _.pick(form, [
        'id',
        'loginName',
        'userMobile',
        'userType',
        'userIcon',
        'password',
        'nickName',
        'userName',
        'userRealNameAuthFlag',
        'userPid',
        'userIcFront',
        'userIcBack',
        'userIcGroup',
        'userStatus',
      ]);
      this.formVisible = true;
    },
    closeForm() {
      this.form = {};
      this.formVisible = false;
    },
    async saveForm() {
      const { loginName } = this.key_user_info;
      try {
        const $form = this.$refs.form;
        await $form.validate();

        if (this.form.id) {
          const { ...form } = this.form;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/user/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        } else {
          const { ...form } = this.form;
          form.create_user = loginName;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/user/add', [form])).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('添加成功');
        }
        await this.reload();
        this.closeForm();
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    async showAssignRoleForm({ id, loginName }) {
      this.assignRoleFormVisible = true;

      this.assignRoleForm = { id, name: loginName, list: [] };

      this.loading = true;
      const { code, respData } = (await this.$http.post('/api/manager/user/getbypk', [id])).body;
      this.loading = false;
      if (code === '200') {
        const { key_role_info } = respData;
        this.assignRoleForm.list = _.map(key_role_info, 'id');
      }
    },
    closeAssignRoleForm() {
      this.assignRoleFormVisible = false;
    },

    async saveAssignRoleForm() {
      try {
        const { id, list } = this.assignRoleForm;
        const { code, message } = (await this.$http.post('/api/manager/user/refuserandroles', { userId: id, roleIds: list.join(',') })).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('分配成功');
        this.closeAssignRoleForm();
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
  },
  async mounted() {
    const { code, respData } = (await this.$http.post('/api/manager/role/list', {
      currPage: 1, pageSize: 999,
    })).body;
    if (code === '200') this.roleList = respData.rows;
    await this.reload();
  },
};
</script>

<style scoped>
.edit-form >>> .el-form-item {
  height: 73px;
}
</style>
