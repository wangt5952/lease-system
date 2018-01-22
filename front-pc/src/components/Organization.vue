<template>
  <div v-loading="loading" style="padding:10px;">

    <div>
      <el-button icon="el-icon-plus" type="primary" size="medium" @click="showForm()">添加企业</el-button>
    </div>

    <el-table :data="list" style="width: 100%;margin-top:10px;">
      <el-table-column prop="name" label="企业名称"></el-table-column>
      <el-table-column prop="mobile" label="社会信用代码" width="180"></el-table-column>
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

    <el-dialog title="人员信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form :model="form" label-width="100px">
        <el-form-item label="企业名称">
          <el-input v-model="form.name" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="社会信用代码">
          <el-input v-model="form.mobile" auto-complete="off"></el-input>
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
        this.list = (await this.$http.get('/api/organization')).body.list;
      } catch (e) {
        const message = e.statusText;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, name }) {
      try {
        await this.$confirm(`确认删除${name}, 是否继续?`, '提示', { type: 'warning' });
        await this.$http.delete(`/api/organization/${id}`);
        await this.reload();
        this.$message.success('删除成功');
      } catch (e) {
        const message = e.statusText;
        this.$message.error(message);
      }
    },

    showForm(form = { }) {
      this.form = _.pick(form, ['id', 'name', 'code']);
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
          await this.$http.put(`/api/organization/${id}`, form);
        } else {
          const { ...form } = this.form;
          await this.$http.post('/api/organization', form);
        }
        await this.reload();
        this.form = {};
        this.formVisible = false;
      } catch (e) {
        const message = e.statusText;
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
