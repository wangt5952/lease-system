<template>
  <div v-loading="loading" style="padding:10px">
    <div style="display:flex;">
      <!-- PLATFORM:平台, ENTERPRISE:企业 -->
      <template v-if="res['FUNCTION'].indexOf('manager-battery-addone') >= 0">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加电池</el-button>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:450px;" v-model="search.keyStr" placeholder="电池编号/电池货名/电池品牌/电池型号/电池参数/生产商ID/生产商名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.batteryStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.isBind" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchIsBindList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <!-- 电池集合 -->
    <el-table :data="list" class="batteryHeight">
      <el-table-column prop="batteryCode" label="编号" width="150"></el-table-column>
      <el-table-column prop="batteryName" label="电池货名" width="100"></el-table-column>
      <el-table-column prop="batteryBrand" label="品牌" width="100"></el-table-column>
      <el-table-column prop="batteryPn" label="型号" width="100"></el-table-column>
      <el-table-column prop="batteryParameters" label="参数" width="100"></el-table-column>
      <el-table-column prop="mfrsName" label="生产商" width="120"></el-table-column>
      <el-table-column prop="batteryStatusText" label="状态" width="100">
        <template slot-scope="{row}">
          <template v-if="row.batteryStatus === 'NORMAL'"><span style="color:#17BE45">正常</span></template>
          <template v-else-if="row.batteryStatus === 'FREEZE'"><span style="color:red">冻结/维保</span></template>
          <template v-else><span style="color:red">作废</span></template>
        </template>
      </el-table-column>
      <el-table-column label="绑定车辆" width="100">
        <template slot-scope="{row}">
          <template v-if="!row.vehicleId"><span style="color:red">未绑定</span></template>
          <template v-else><span style="color:#17BE45">已绑定</span></template>
        </template>
      </el-table-column>
      <!-- PLATFORM:平台, ENTERPRISE:企业 -->
      <template v-if="key_user_info.userType === 'PLATFORM'">
        <el-table-column label="操作" width="300">
            <template slot-scope="{row}">
              <template v-if="res['FUNCTION'].indexOf('manager-battery-modify') >= 0">
                <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
              </template>
              <el-button v-if="!row.deviceId" icon="el-icon-plus" size="mini" type="text" @click="addDeviceButton(row)">添加设备</el-button>
            </template>
        </el-table-column>
      </template>
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

    <el-dialog title="设备信息" :visible.sync="deviceFormVisible" :before-close="colseDeviceForm">
      <el-form class="edit-form" :model="deviceForm" ref="deviceForm" :rules="rules2">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="deviceId" label="编号">
              <el-input v-model="batteryForm.batteryCode" placeholder="请输入编号" auto-complete="off" :disabled="editForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="deviceType" label="设备类别">
              <el-select v-model="deviceForm.deviceType" placeholder="请选择设备类别" style="width:100%;" :disabled="editForm">
                <el-option v-for="o in deviceTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="perSet" label="请求间隔时间 (单位:秒)">
              <el-input v-model.number="deviceForm.perSet" placeholder="请输入请求间隔时间" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="reset" label="硬件复位标志">
              <el-select v-model="deviceForm.reset" placeholder="请选择硬件复位标志" style="width:100%;">
                <el-option v-for="o in resetTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="request" label="主动请求数据标志">
              <el-select v-model="deviceForm.request" placeholder="请选择主动请求数据标志" style="width:100%;">
                <el-option v-for="o in requestTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="colseDeviceForm">取消</el-button>
        <el-button type="primary" @click="addDeviceForm">添加</el-button>
      </span>
    </el-dialog>

    <el-dialog title="电池信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form" :rules="rules1">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="batteryCode" label="编号">
              <el-input v-model="form.batteryCode" auto-complete="off" :disabled="disabledFormId"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="batteryName" label="电池货名">
              <el-input v-model="form.batteryName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="batteryBrand" label="品牌">
              <el-input v-model="form.batteryBrand" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="batteryPn" label="型号">
              <el-input v-model="form.batteryPn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="batteryParameters" label="参数">
              <el-input v-model="form.batteryParameters" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="mfrsId" label="生产商">
              <el-select v-model="form.mfrsId" placeholder="请选择生产商" style="width:100%;">
                <el-option v-for="o in mfrsList" :key="o.id" :label="o.mfrsName" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="batteryStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.batteryStatus" placeholder="请选择状态" style="width:100%;">
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
import * as validate from '@/util/validate';

const checkBattreryId = (rule, value, callback) => {
  if (!value) callback(new Error('编号不能为空'));
  else if (!validate.isvalidSinogram(value)) callback(new Error('编号不能包含汉字'));
  else callback();
};

const checkTime = (rule, value, callback) => {
  if (!value) callback(new Error('请求间隔时间不能为空'));
  else if (!validate.isvalidSignlessInteger(value)) callback(new Error('请输入非负正整数'));
  else callback();
};

export default {
  data() {
    return {
      // 设备
      deviceFormVisible: false,
      editForm: false,
      editButtonVisible: false,
      addButtonVisible: false,
      deviceForm: {},
      batteryForm: {},
      deviceTypeList: [
        { id: 'BATTERY', name: '电池' },
      ],
      resetTypeList: [
        { id: 0, name: '无处理' },
        { id: 1, name: '复位重启' },
      ],
      requestTypeList: [
        { id: 0, name: '无处理' },
        { id: 1, name: '主动请求' },
      ],
      // 设备表单效验
      rules2: {
        perSet: [
          { required: true, validator: checkTime },
        ],
        reset: [
          { required: true, message: '请选择硬件复位标志' },
        ],
        request: [
          { required: true, message: '请选择主动请求数据标志' },
        ],
      },


      loading: false,
      list: [],
      search: {
        batteryStatus: '',
        isBind: '',
      },

      pageSizes: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      formVisible: false,
      disabledFormId: false,
      form: {},

      typeList: [
        { id: 'VEHICLE', name: '车辆' },
        { id: 'BATTERY', name: '电池' },
        { id: 'PARTS', name: '配件' },
      ],
      statusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],
      searchStatusList: [
        { id: '', name: '全部状态' },
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],
      searchIsBindList: [
        { id: '', name: '全部' },
        { id: 'UNBIND', name: '未绑定' },
        { id: 'BIND', name: '已绑定' },
      ],
      mfrsList: [],
      rules1: {
        batteryCode: [
          { required: true, validator: checkBattreryId },
        ],
      },
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
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
    // 添加设备按钮
    addDeviceButton(row = {}) {
      this.editForm = true;
      this.deviceFormVisible = true;
      this.batteryForm = _.pick(row, [
        'batteryCode',
      ]);
      this.deviceForm.perSet = 30;
      this.deviceForm.request = 0;
      this.deviceForm.reset = 0;
      this.deviceForm.deviceType = '电池';
    },
    // 保存设备
    async addDeviceForm() {
      const $deviceForm = this.$refs.deviceForm;
      await $deviceForm.validate();

      const { ...deviceForm } = this.deviceForm;
      deviceForm.deviceId = this.batteryForm.batteryCode;
      deviceForm.deviceType = 'BATTERY';
      try {
        const { code, message } = (await this.$http.post('/api/manager/device/addone', deviceForm)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('添加成功');
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      await this.reload();
      this.deviceFormVisible = false;
    },
    // 关闭设备窗口
    colseDeviceForm() {
      const $deviceForm = this.$refs.deviceForm;
      if ($deviceForm) $deviceForm.resetFields();
      this.deviceFormVisible = false;
      this.deviceForm = {};
    },

    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },
    async reload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/battery/list', {
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
          batteryStatusText: (_.find(this.statusList, { id: o.batteryStatus }) || { name: o.batteryStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, resName }) {
      try {
        await this.$confirm(`确认删除${resName}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/battery/delete', [id])).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('删除成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async showForm(form = {}) {
      await this.getMfrsList();
      this.form = _.pick(form, [
        'id',
        'batteryCode',
        'batteryName',
        'batteryBrand',
        'batteryPn',
        'batteryParameters',
        'mfrsId',
        'batteryStatus',
      ]);
      this.formVisible = true;
      if (form.id) {
        this.disabledFormId = true;
      } else {
        const $form = this.$refs.form;
        if ($form) $form.resetFields();
        this.disabledFormId = false;
      }
    },
    closeForm() {
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
          const { code, message } = (await this.$http.post('/api/manager/battery/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        } else {
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/battery/addone', form)).body;
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
    async getMfrsList() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/mfrs/list', {
          currPage: 1, pageSize: 999,
        })).body;
        if (code !== '200') throw new Error(message);
        this.mfrsList = respData.rows;
      } catch (e) {
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
>>> .batteryHeight {
  position: relative;
  overflow-x: hidden;
  overflow-y: scroll;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  /* width: 100%; */
  max-width: 100%;
  color: #606266;
  height: 85%;
  max-height: 85%;
}
</style>
