<template>
  <div v-loading="loading" style="padding:10px;">
    <div style="display:flex;">
      <template v-if="res['FUNCTION'].indexOf('manager-device-addone') >= 0">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="addButton">添加设备</el-button>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="search.keyStr" placeholder="设备编码"></el-input>
        </el-form-item>
      </el-form>
    </div>
    <!-- 列表a -->
    <el-table :data="list" style="width:100%;margin-top:10px">
      <el-table-column prop="deviceId" label="编号"></el-table-column>
      <el-table-column prop="deviceTypeListText" label="设备类别"></el-table-column>
      <el-table-column prop="perSet" label="请求间隔时间(单位:秒)"></el-table-column>
      <el-table-column prop="resetTypeText" label="硬件复位标志"></el-table-column>
      <el-table-column prop="requestTypeText" label="主动请求数据标志"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="{row}">
          <template v-if="res['FUNCTION'].indexOf('manager-device-modify') >= 0">
            <el-button icon="el-icon-edit" size="mini" type="text" @click="editButton(row)">编辑</el-button>
          </template>
          <template v-if="res['FUNCTION'].indexOf('manager-device-delete') >= 0">
            <el-tooltip content="删除" placement="top">
              <el-button icon="el-icon-delete" size="mini" type="text" @click="handleDelete(row)"></el-button>
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination v-if="total" style="margin-top:10px;"
      @size-change="handleSizeChange"
      @current-change="reload"
      :current-page.sync="currentPage"
      :page-sizes="pageSizes"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
    <!-- 表单 -->
    <el-dialog title="设备信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form" :rules="rules2">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="deviceId" label="编号">
              <el-input v-model="form.deviceId" placeholder="请输入编号" auto-complete="off" :disabled="editForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="deviceType" label="设备类别">
              <el-select v-model="form.deviceType" placeholder="请选择设备类别" style="width:100%;" :disabled="editForm">
                <el-option v-for="o in deviceTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="perSet" label="请求间隔时间 (单位:秒)">
              <el-input v-model.number="form.perSet" placeholder="请输入请求间隔时间" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="request" label="硬件复位标志">
              <el-select v-model="form.reset" placeholder="请选择硬件复位标志" style="width:100%;">
                <el-option v-for="o in resetTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="reset" label="主动请求数据标志">
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
    const checkTime = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请求间隔时间不能为空'));
      }
      setTimeout(() => {
        if (!/^\d+$/.test(value)) {
          callback(new Error('请输入非负正整数'));
        } else {
          callback();
        }
      }, 500);
      return false;
    };
    return {
      loading: false,
      formVisible: false,
      editButtonVisible: false,
      addButtonVisible: false,
      editForm: false,
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
      // 表单效验
      rules2: {
        deviceId: [
          { required: true, message: '请输入编号' },
        ],
        deviceType: [
          { required: true, message: '请输入设备类别' },
        ],
        perSet: [
          { validator: checkTime, trigger: 'blur' },
        ],
        request: [
          { required: true, message: '请选择硬件复位标志' },
        ],
        reset: [
          { required: true, message: '请选择主动请求数据标志' },
        ],
      },
    };
  },
  computed: {
    // 获取当前登录用户信息.
    ...mapState({
      key_user_info: state => state.key_user_info,
      key_res_info: state => state.key_res_info,
      res: state => _.mapValues(_.groupBy(state.key_res_info, 'resType'), o => _.map(o, 'resCode')),
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
    // 分页下拉列表改变出发事件
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },
    // 加载
    async reload() {
      this.loading = false;
      if (this.key_user_info.userType === 'PLATFORM') {
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
      }
    },
    // 添加按钮
    addButton(form = {}) {
      this.editForm = false;
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
    // 编辑按钮
    async editButton(form) {
      this.editForm = true;
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
    // 关闭
    closeForm() {
      this.formVisible = false;
    },
    // 保存功能
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
    // 添加功能
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
    // 删除功能
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
    this.loading = true;
    await this.reload();
    this.loading = false;
  },
};
</script>

<style scoped>
.edit-form >>> .el-form-item {
  height: 73px;
}
</style>
