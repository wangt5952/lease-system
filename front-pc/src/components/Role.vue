<template>
  <div v-loading="loading" style="padding:10px;">

    <div>
      <el-button icon="el-icon-plus" type="primary" size="medium" @click="showForm()">添加人员</el-button>
    </div>

    <el-table :data="list" style="width: 100%;margin-top:10px;">
      <el-table-column prop="roleName" label="角色名"></el-table-column>
      <el-table-column prop="userMobile" label="手机号"></el-table-column>
      <el-table-column prop="userType" label="用户类型"></el-table-column>
      <el-table-column prop="userIcon" label="用户LOGO"></el-table-column>
      <el-table-column prop="nickName" label="昵称"></el-table-column>
      <el-table-column prop="userName" label="姓名"></el-table-column>
      <el-table-column prop="userRealNameAuthFlag" label="用户实名认证标志"></el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="{row}">
          <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
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

    <el-dialog title="资源信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form :model="form" label-width="100px">
        <el-form-item label="loginName">
          <el-input v-model="form.loginName" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="userMobile">
          <el-input v-model="form.userMobile" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="medium" @click="closeForm">取消</el-button>
        <el-button size="medium" type="primary" @click="saveForm">{{form.id ? '保存' : '添加'}}</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import _ from 'lodash';

export default {
  data() {
    return {
      loading: false,
      list: [],

      pageSizes: [10, 50, 100, 200],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      formVisible: false,
      form: {},

    };
  },
  methods: {
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },

    async reload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/role/list', {
          currPage: this.currentPage, pageSize: this.pageSize
        })).body;
        if(code != '200') throw new Error(message);
        const { total, rows } = respData;
        this.total = total;
        this.list = rows;
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, name }) {
      try {
        await this.$confirm(`确认删除${name}, 是否继续?`, '提示', { type: 'warning' });
        await this.$http.delete(`/api/user/${id}`);
        await this.reload();
        this.$message.success('删除成功');
      } catch (e) {
        const message = e.statusText;
        this.$message.error(message);
      }
    },

    showForm(form = { }) {
      this.form = _.pick(form, ['id', 'loginName', 'userMobile']);
      this.formVisible = true;
    },
    closeForm() {
      this.form = {};
      this.formVisible = false;
    },
    async saveForm() {
      try {
        if (this.form.id) {
          const { id, ...form } = this.form;
          await this.$http.put(`/api/user/${id}`, form);
        } else {
          const { ...form } = this.form;
          const { code, message, respData } = (await this.$http.post('/api/manager/user/add', form)).body;
          if(code != '200') throw new Error(message);
        }
        await this.reload();
        this.form = {};
        this.formVisible = false;
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
  },
  async mounted() {
    await this.reload();
  },
};
</script>

<style scoped>

</style>
