<template>
  <div v-loading="loading" style="padding:10px;">

    <div>
      <el-button icon="el-icon-plus" type="primary" size="medium" @click="showForm()">添加资源</el-button>
    </div>

    <el-table :data="list" style="width: 100%;margin-top:10px;">
      <el-table-column prop="resCode" label="资源编码"></el-table-column>
      <el-table-column prop="resName" label="资源名"></el-table-column>
      <el-table-column prop="resType" label="资源类型"></el-table-column>
      <el-table-column prop="resUrl" label="资源请求URL"></el-table-column>
      <el-table-column prop="resSort" label="排序"></el-table-column>
      <el-table-column prop="showFlag" label="显示标志"></el-table-column>
      <el-table-column prop="parent" label="上级资源"></el-table-column>
      <el-table-column prop="level" label="级别"></el-table-column>
      <el-table-column prop="createUser" label="创建人"></el-table-column>
      <el-table-column prop="createTime" label="创建时间"></el-table-column>
      <el-table-column prop="updateUser" label="更新人"></el-table-column>
      <el-table-column prop="updateTime" label="更新时间"></el-table-column>
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
        <el-form-item label="资源编码">
          <el-input v-model="form.resCode" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="资源名">
          <el-input v-model="form.resName" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="资源类型">
          <el-input v-model="form.resType" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="资源请求URL">
          <el-input v-model="form.resUrl" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model="form.resSort" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="显示标志">
          <el-input v-model="form.showFlag" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="上级资源">
          <el-input v-model="form.parent" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="级别">
          <el-input v-model="form.level" auto-complete="off"></el-input>
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
        const { code, message, respData } = (await this.$http.post('/api/manager/res/list', {
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
      this.form = _.pick(form, ['id', 'resCode', 'resName', 'resType', 'resUrl', 'resSort', 'showFlag', 'parent', 'level']);
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
          const { code, message, respData } = (await this.$http.post('/api/manager/res/add', form)).body;
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
