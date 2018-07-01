<template>
  <div v-loading="loading && !formVisible && !assignResFormVisible" style="padding:10px">
    <div style="display:flex;">
      <div style="margin-right:10px;">
        <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加角色</el-button>
      </div>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:400px;" v-model="search.keyStr" placeholder="角色名"></el-input>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="list" class="roleHeight">
      <el-table-column prop="roleNameText" label="角色名" width="350"></el-table-column>
      <el-table-column prop="roleIntroduce" label="说明" width="350"></el-table-column>
      <el-table-column label="操作" width="500">
        <template slot-scope="{row}">
          <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
          <el-button icon="el-icon-edit" size="mini" type="text" @click="showAssignResForm(row)">分配资源</el-button>
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

    <el-dialog title="角色信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form" v-loading="loading && formVisible">
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item prop="roleName" :rules="[{required:true, message:'请填写角色名'}]" label="角色名">
              <el-input v-model="form.roleName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="roleIntroduce" :rules="[{required:true, message:'请填写角色说明'}]" label="说明">
              <el-input v-model="form.roleIntroduce" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer" >
        <el-button @click="closeForm">取消</el-button>
        <el-button :disabled="loading" type="primary" @click="saveForm">{{form.id ? '保存' : '添加'}}</el-button>
      </span>
    </el-dialog>


    <el-dialog :title="`分配资源 ( ${assignResForm.name} ) `" :visible.sync="assignResFormVisible" :close-on-click-modal="false">
      <el-form :model="form" ref="form" v-loading="loading && assignResFormVisible">
        <el-row :gutter="10">
          <el-col :span="24">
            <el-tree :data="resTree" ref="resTree" node-key="id" :props="{label: 'resName'}" show-checkbox></el-tree>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer" >
        <el-button @click="closeAssignResForm">取消</el-button>
        <el-button :disabled="loading" type="primary" @click="saveAssignResForm">{{form.id ? '保存' : '添加'}}</el-button>
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

      search: {},

      pageSizes: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      formVisible: false,
      form: {},

      assignResFormVisible: false,
      assignResForm: {},

      resList: [],
      resTypeList: [
        { id: 'CATALOG', name: '目录' },
        { id: 'MENU', name: '菜单' },
        { id: 'PAGE', name: '页面' },
        { id: 'FUNCTION', name: '功能' },
      ],
      roleNameText: [
        { id: 'personal', name: '个人' },
        { id: 'enterprise', name: '企业' },
        { id: 'admin', name: '管理员' },
      ]
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
    }),
    // 递归循环调用自身
    resTree() {
      const { resList: list } = this;
      const buildChildren = (parent) => {
        const childrenList = parent ? _.filter(list, { parent }) : _.filter(list, o => !o.parent);
        if (!childrenList.length) return null;
        return _.map(childrenList, (o) => {
          const children = buildChildren(o.id);
          if (!children) return o;
          return { ...o, children };
        });
      };
      return buildChildren(null);
    },
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
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/role/list', {
          currPage: this.currentPage, pageSize: this.pageSize, ...this.search,
        })).body;

        if (code === '40106') {
          this.$store.commit('relogin');
          throw new Error('认证超时，请重新登录');
        }
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.total = total;
        this.list = _.map(rows, o => ({
          ...o,
          userTypeText: (_.find(this.typeList, { id: o.userType }) || {}).name,
          userRealNameAuthFlagText: (_.find(this.authList, { id: o.userRealNameAuthFlag }) || {}).name,
          roleNameText: (_.find(this.roleNameText, { id: o.roleName }) || {}).name,
        }));
      } catch (e) {
        this.loading = false;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, roleName }) {
      try {
        await this.$confirm(`确认删除${roleName}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/role/delete', [id])).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('删除成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    async showForm(form = { }) {
      this.form = _.pick(form, [
        'id',
        'roleName',
        'roleIntroduce',
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
          const { code, message } = (await this.$http.post('/api/manager/role/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        } else {
          const { ...form } = this.form;
          form.create_user = loginName;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/role/addone', form)).body;
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
    // 分配资源按钮
    async showAssignResForm({ id, roleName }) {
      this.assignResFormVisible = true;
      this.assignResForm.id = id;
      this.assignResForm.name = roleName;

      this.loading = true;
      const { code, respData } = (await this.$http.post('/api/manager/role/getbypk', [id])).body;
      let keys = [];
      if (code === '200') {
        const { key_res_info } = respData;
        keys = _.map(key_res_info, 'id');
      }
      this.$refs.resTree.setCheckedKeys(keys);
      this.loading = false;
    },
    closeAssignResForm() {
      this.assignResFormVisible = false;
    },
    // 保存所分配分配资源
    async saveAssignResForm() {
      try {
        const { id } = this.assignResForm;
        const resourceIds = this.$refs.resTree.getCheckedKeys().join(',');
        if (resourceIds) {
          const { code, message } = (await this.$http.post('/api/manager/role/refroleandresources', { roleId: id, resourceIds })).body;
          if (code !== '200') throw new Error(message);
        } else {
          const { code, message } = (await this.$http.post('/api/manager/role/refroleandresources', { roleId: id, deleteAllFlg: true })).body;
          if (code !== '200') throw new Error(message);
        }
        // 关闭页面
        this.$message.success('分配成功');
        this.closeAssignResForm();
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 获取所有权限
    async getResList() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/res/list', {
          currPage: 1, pageSize: 999,
        })).body;
        if (code !== '200') throw new Error(message);
        this.resList = respData.rows;
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
  },
  async mounted() {
    this.loading = true;
    await this.getResList();
    await this.reload();
    this.loading = false;
  },
};
</script>

<style scoped>
.edit-form >>> .el-form-item {
  min-height: 73px;
}
>>> .roleHeight {
  position: relative;
  overflow-x: hidden;
  overflow-y: scroll;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  width: 100%;
  max-width: 100%;
  color: #606266;
  height: 95%;
  max-height: 95%;
}
</style>
