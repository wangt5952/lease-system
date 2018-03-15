<template>
  <div v-loading="loading" style="padding:10px;">
    <div style="display:flex;">
      <div style="margin-right:10px;">
        <el-button icon="el-icon-plus" type="primary" size="small" @click="addButton">添加设备</el-button>
      </div>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="search.keyStr" placeholder="设备编码"></el-input>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="list" style="width:100%;margin-top:10px">
      <el-table-column prop="deviceId" label="编号"></el-table-column>
      <el-table-column prop="deviceTypeListText" label="设备类别"></el-table-column>
      <el-table-column prop="perSet" label="请求间隔时间(单位:秒)"></el-table-column>
      <el-table-column prop="resetTypeText" label="硬件复位标志"></el-table-column>
      <el-table-column prop="requestTypeText" label="主动请求数据标志"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="{row}">
          <el-button icon="el-icon-edit" size="mini" type="text" @click="editButton(row)">编辑</el-button>
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

    <el-dialog title="设备信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="deviceId" :rules="[{required:true, message:'请输入编号'}]" label="编号">
              <el-input v-model="form.deviceId" placeholder="请输入编号" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="deviceType" :rules="[{required:true, message:'请输入设备类别'}]" label="设备类别">
              <el-select v-model="form.deviceType" placeholder="请选择设备类别" style="width:100%;">
                <el-option v-for="o in deviceTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              prop="perSet"
              :rules="[
                {required:true, message:'请输入请求间隔时间'},
                { type: 'number', message: '间隔时间必须为整数数值'}
              ]"
              label="请求间隔时间 (单位:秒)">
              <el-input v-model.number="form.perSet" placeholder="请输入请求间隔时间" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="request" :rules="[{required:true, message:'请选择硬件复位标志'}]" label="硬件复位标志">
              <el-select v-model="form.reset" placeholder="请选择硬件复位标志" style="width:100%;">
                <el-option v-for="o in resetTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="reset" :rules="[{required:true, message:'请选择主动请求数据标志'}]" label="主动请求数据标志">
              <el-select v-model="form.request" placeholder="请选择主动请求数据标志" style="width:100%;">
                <el-option v-for="o in requestTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeForm">取消</el-button>
        <el-button type="primary" v-if="editButtonVisible" @click="saveForm(form)">保存</el-button>
        <el-button type="primary" v-if="addButtonVisible" @click="addForm">添加</el-button>
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
      formVisible: false,
      editButtonVisible: false,
      addButtonVisible: false,
      search: {},
      form: {},

      pageSizes: [1, 10, 50, 100, 200],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      list: [],
      deviceTypeList: [
        { id: 'VEHICLE', name: '车辆' },
        { id: 'BATTERY', name: '电池' },
        { id: 'PARTS', name: '配件' },
      ],
      resetTypeList: [
        { id: 0, name: '无处理' },
        { id: 1, name: '复位重启' },
      ],
      requestTypeList: [
        { id: 0, name: '无处理' },
        { id: 1, name: '主动请求' },
      ],
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
        const { code, message, respData } = (await this.$http.post('/api/manager/device/list', {
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
          resetTypeText: (_.find(this.resetTypeList, { id: o.reset }) || {}).name,
          requestTypeText: (_.find(this.requestTypeList, { id: o.request }) || {}).name,
          deviceTypeListText: (_.find(this.deviceTypeList, { id: o.deviceType }) || {}).name,
        }));
        this.loading = false;
      } catch (e) {
        this.loading = false;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    addButton(form = {}) {
      const $form = this.$refs.form;
      if ($form) $form.resetFields();
      this.form = _.pick(form, [
        'deviceId',
        'deviceType',
        'perSet',
        'request',
        'reset',
      ]);
      this.formVisible = true;
      this.editButtonVisible = false;
      this.addButtonVisible = true;
    },
    async editButton(form) {
      const forms = _.pick(form, [
        'deviceId',
        'deviceType',
        'perSet',
        'request',
        'reset',
      ]);
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/device/getbypk', forms)).body;
        if (code !== '200') throw new Error(message);
        this.form = respData;
        this.formVisible = true;
        this.addButtonVisible = false;
        this.editButtonVisible = true;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    closeForm() {
      this.formVisible = false;
    },
    async saveForm(form) {
      const $form = this.$refs.form;
      await $form.validate();
      try {
        const { code, message } = (await this.$http.post('/api/manager/device/modify', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('保存成功');
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      this.formVisible = false;
      await this.reload();
    },
    async addForm() {
      const $form = this.$refs.form;
      await $form.validate();
      const { ...form } = this.form;
      try {
        const { code, message } = (await this.$http.post('/api/manager/device/addone', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('添加成功');
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      await this.reload();
      this.closeForm();
    },
    async handleDelete(form) {
      this.form = _.pick(form, [
        'deviceId',
        'deviceType',
      ]);
      try {
        await this.$confirm(`确认删除编号为${form.deviceId}的设备吗, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/device/delete', [this.form])).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('删除成功');
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
.edit-form >>> .el-form-item {
  height: 73px;
}
</style>
