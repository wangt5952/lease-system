<template>
  <div v-loading="loading" style="padding:10px;">

    <div>
      <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加角色</el-button>
    </div>

    <el-table :data="list" style="width: 100%;margin-top:10px;">
      <el-table-column prop="roleName" label="角色名"></el-table-column>
      <el-table-column prop="roleIntroduce" label="说明"></el-table-column>
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

    <el-dialog title="角色信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form :model="form" ref="form" size="medium">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="roleName" :rules="[{required:true, message:'请填写角色名'}]" label="用户名">
              <el-input v-model="form.roleName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="roleIntroduce" :rules="[{required:true, message:'请填写角色说明'}]" label="说明">
              <el-input v-model="form.roleIntroduce" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
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
import {
  mapState,
} from 'vuex';

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
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
    }),
  },
  watch: {
    formVisible(v) {
      if (!v) {
        const $form = this.$refs.form;
        $form.resetFields();
      }
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
          currPage: this.currentPage, pageSize: this.pageSize,
        })).body;
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.total = total;
        this.list = _.map(rows, o => ({
          ...o,
          userTypeText: (_.find(this.typeList, { id: o.userType }) || {}).name,
          userRealNameAuthFlagText: (_.find(this.authList, { id: o.userRealNameAuthFlag }) || {}).name,
        }));
      } catch (e) {
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

    showForm(form = { }) {
      this.form = _.pick(form, [
        'id',
        'loginName',
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
          const { code, message } = (await this.$http.post('/api/manager/role/add', [form])).body;
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
  },
  async mounted() {
    await this.reload();
  },
};
</script>

<style scoped>
>>> .el-form-item {
  height: 73px;
}
</style>
