<template>
  <div v-loading="loading" style="padding:10px;">

    <div>
      <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加组织</el-button>
    </div>

    <el-table :data="list" style="width: 100%;margin-top:10px;">
      <el-table-column prop="orgCode" label="编码"></el-table-column>
      <el-table-column prop="orgName" label="组织名称"></el-table-column>
      <el-table-column prop="orgTypeText" label="类别"></el-table-column>
      <el-table-column prop="orgIntroduce" label="介绍"></el-table-column>
      <el-table-column prop="orgAddress" label="地址"></el-table-column>
      <el-table-column prop="orgContacts" label="联系人"></el-table-column>
      <el-table-column prop="orgPhone" label="联系电话"></el-table-column>
      <el-table-column prop="orgBusinessLicences" label="营业执照号码"></el-table-column>
      <el-table-column prop="orgStatusText" label="状态"></el-table-column>
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

    <el-dialog title="企业信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form :model="form" ref="form">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="orgCode" :rules="[{required:true, message:'请填写编码'}]" label="编码">
              <el-input v-model="form.orgCode" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="orgName" :rules="[{required:true, message:'请填写组织名称'}]" label="组织名称">
              <el-input v-model="form.orgName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="orgType" :rules="[{required:true, message:'请选择类型'}]" label="类型">
              <el-select v-model="form.orgType" placeholder="请选择类型" style="width:100%;">
                <el-option v-for="o in typeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="介绍">
              <el-input v-model="form.orgIntroduce" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="orgAddress" label="地址">
              <el-input v-model="form.orgAddress" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系人">
              <el-input v-model="form.orgContacts" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话">
              <el-input v-model="form.orgPhone" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="营业执照号码">
              <el-input v-model="form.orgBusinessLicences" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="orgStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.orgStatus" placeholder="请选择状态" style="width:100%;">
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
        { id: 'PLATFORM', name: '平台' },
        { id: 'ENTERPRISE', name: '企业' },
      ],
      statusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
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
          orgTypeText: (_.find(this.typeList, { id: o.orgType }) || { name: o.orgType }).name,
          orgStatusText: (_.find(this.statusList, { id: o.orgStatus }) || { name: o.orgStatus }).name,
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
        'orgCode',
        'orgName',
        'orgType',
        'orgIntroduce',
        'orgAddress',
        'orgContacts',
        'orgPhone',
        'orgBusinessLicences',
        'orgStatus',
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
