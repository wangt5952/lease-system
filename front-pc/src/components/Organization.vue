<template>
  <div v-loading="loading" style="padding:10px;">

    <div>
      <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加资源</el-button>
    </div>

    <el-table :data="list" style="width: 100%;margin-top:10px;">
      <el-table-column prop="resCode" label="编码"></el-table-column>
      <el-table-column prop="resName" label="资源名"></el-table-column>
      <el-table-column prop="resTypeText" label="类型"></el-table-column>
      <el-table-column prop="resUrl" label="请求URL"></el-table-column>
      <el-table-column prop="groupSort" label="分组排序"></el-table-column>
      <el-table-column prop="resSort" label="组内排序"></el-table-column>
      <el-table-column prop="showFlagText" label="显示标志"></el-table-column>
      <el-table-column prop="parent" label="上级资源"></el-table-column>
      <el-table-column prop="level" label="级别"></el-table-column>
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
      <el-form :model="form" ref="form" size="medium">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="resCode" :rules="[{required:true, message:'请填写编码'}]" label="编码">
              <el-input v-model="form.resCode" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="resName" :rules="[{required:true, message:'请填写资源名'}]" label="资源名">
              <el-input v-model="form.resName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="resType" :rules="[{required:true, message:'请选择类型'}]" label="类型">
              <el-select v-model="form.resType" placeholder="请选择类型" style="width:100%;">
                <el-option v-for="o in typeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="请求URL">
              <el-input v-model="form.resUrl" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="groupSort" label="分组排序" :rules="[{required:true, message:'请填写分组排序'}]">
              <el-input v-model="form.groupSort" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="组内排序">
              <el-input v-model="form.resSort" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="resType" :rules="[{required:true, message:'请选择显示标志'}]" label="显示标志">
              <el-select v-model="form.showFlag" placeholder="请选择显示标志" style="width:100%;">
                <el-option v-for="o in showFlagList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="上级资源">
              <el-input v-model="form.parent" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="level" :rules="[{required:true, message:'请填写级别'}]" label="级别">
              <el-input v-model="form.level" auto-complete="off"></el-input>
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

      typeList: [
        { id: 'CATALOG', name: '目录' },
        { id: 'MENU', name: '菜单' },
        { id: 'PAGE', name: '页面' },
        { id: 'FUNCTION', name: '功能' },
      ],
      showFlagList: [
        { id: 'SHOW', name: '显示' },
        { id: 'HIDDEN', name: '隐藏' },
      ],
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
        const { code, message, respData } = (await this.$http.post('/api/manager/org/list', {
          currPage: this.currentPage, pageSize: this.pageSize,
        })).body;
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.total = total;
        this.list = _.map(rows, o => ({
          ...o,
          resTypeText: (_.find(this.typeList, { id: o.resType }) || { name: o.resType }).name,
          showFlagText: (_.find(this.showFlagList, { id: o.showFlag }) || {}).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, resName }) {
      try {
        await this.$confirm(`确认删除${resName}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/org/delete', [id])).body;
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
        'resCode',
        'resName',
        'resType',
        'resUrl',
        'groupSort',
        'resSort',
        'showFlag',
        'parent',
        'level',
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
          if (form.parent === '') form.parent = null;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/org/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        } else {
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/org/add', [form])).body;
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
