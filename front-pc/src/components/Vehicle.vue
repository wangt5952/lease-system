<template>
  <div v-loading="loading" style="padding:10px;">
    <div style="display:flex;">
      <div style="margin-right:10px;">
        <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加车辆</el-button>
      </div>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="search.keyStr" placeholder="车辆编号/车辆型号/车辆品牌/车辆产地/生产商ID/生产商名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.vehicleStatus" placeholder="请选择状态" style="width:100%;">
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

    <!-- {{ res }} -->
    <!-- {{ key_res_info }} -->
    <el-table :data="list" style="width: 100%">
      <el-table-column prop="vehicleCode" label="编号"></el-table-column>
      <el-table-column prop="vehiclePn" label="型号"></el-table-column>
      <el-table-column prop="vehicleBrand" label="品牌"></el-table-column>
      <el-table-column prop="vehicleMadeIn" label="车辆产地"></el-table-column>
      <el-table-column prop="mfrsName" label="生产商"></el-table-column>
      <el-table-column prop="vehicleStatusText" label="状态"></el-table-column>
      <el-table-column label="电池">
        <template slot-scope="{row}">
          <el-button v-if="!row.batteryId" type="text" @click="showBindForm(row)">绑定</el-button>
          <el-button v-else type="text" @click="handleUnbind(row)">解绑</el-button>
        </template>
      </el-table-column>
      <!-- PLATFORM:平台, ENTERPRISE:企业 -->
      <template v-if="key_user_info.userType === 'PLATFORM'">
        <el-table-column label="配件">
          <template slot-scope="{row}">
            <el-button v-if="!row.batteryId" type="text" @click="showBindForm(row)">绑定</el-button>
            <el-button v-else type="text" @click="handleUnbind(row)">解绑</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="">
          <template slot-scope="{row}">
            <template v-if="res['FUNCTION'].indexOf('manager-vehicle-modify') >= 0">
              <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
            </template>
            <template v-if="res['FUNCTION'].indexOf('manager-vehicle-delete') >= 0">
              <el-tooltip content="删除" placement="top">
                <el-button icon="el-icon-delete" size="mini" type="text" @click="handleDelete(row)"></el-button>
              </el-tooltip>
            </template>
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

    <el-dialog title="车辆信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="vehicleCode" :rules="[{required:true, message:'请填写编号'}]" label="编号">
              <el-input v-model="form.vehicleCode" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehiclePn" :rules="[{required:true, message:'请填写型号'}]" label="型号">
              <el-input v-model="form.vehiclePn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleBrand" :rules="[{required:true, message:'请填写品牌'}]" label="品牌">
              <el-input v-model="form.vehicleBrand" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleMadeIn" :rules="[{required:true, message:'请填写产地'}]" label="产地">
              <el-input v-model="form.vehicleMadeIn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="mfrsId" :rules="[{required:true, message:'请选择生产商'}]" label="生产商">
              <el-select v-model="form.mfrsId" placeholder="请选择生产商" style="width:100%;">
                <el-option v-for="o in mfrsList" :key="o.id" :label="o.mfrsName" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.vehicleStatus" placeholder="请选择状态" style="width:100%;">
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

    <el-dialog title="绑定电池" :visible.sync="bindFormVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="bindForm" ref="bindForm">
        <el-form-item prop="batteryId" :rules="[{required:true, message:'请选择状态'}]" label="电池">
          <el-select style="width:100%;" v-model="bindForm.batteryId" filterable remote placeholder="请输入电池 电池编号、电池货名、电池品牌、电池型号、电池参数、生产商ID、生产商名" @focus="remoteBattery('')" :remote-method="remoteBattery" :loading="bindForm_batteryLoading">
            <el-option v-for="o in bindForm_batteryList" :key="o.id" :label="`${o.batteryBrand}-${o.batteryCode}`" :value="o.id">
              <span style="float: left">{{ o.batteryBrand }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ o.batteryCode }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeBindForm">取消</el-button>
        <el-button type="primary" @click="saveBindForm">{{form.id ? '保存' : '绑定'}}</el-button>
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
      search: {
        vehicleStatus: '',
        isBind: '',
      },

      pageSizes: [10, 50, 100, 200],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      formVisible: false,
      form: {},

      bindFormVisible: false,
      bindForm: {},

      bindForm_batteryList: [],
      bindForm_batteryLoading: false,

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
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
      key_res_info: state => state.key_res_info,
      res: state => _.mapValues(_.groupBy(state.key_res_info, 'resType'), o => _.map(o, 'resCode')),
      // res: state => _.groupBy(state.key_res_info, 'resType'),
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
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/list', {
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
          vehicleStatusText: (_.find(this.statusList, { id: o.vehicleStatus }) || { name: o.vehicleStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, resName }) {
      try {
        await this.$confirm(`确认删除${resName}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/vehicle/delete', [id])).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('删除成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    showForm(form = { }) {
      const $form = this.$refs.form;
      if ($form) $form.resetFields();
      this.form = _.pick(form, [
        'id',
        'vehicleCode',
        'vehiclePn',
        'vehicleBrand',
        'vehicleMadeIn',
        'mfrsId',
        'vehicleStatus',
      ]);
      this.formVisible = true;
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
          const { code, message } = (await this.$http.post('/api/manager/vehicle/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        } else {
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/vehicle/addone', {
            bizVehicleInfo: form, flag: '2',
          })).body;
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
    showBindForm({ id }) {
      const $form = this.$refs.bindForm;
      if ($form) $form.resetFields();
      this.bindForm = { vehicleId: id };
      this.bindFormVisible = true;
    },
    closeBindForm() {
      this.bindFormVisible = false;
    },
    async saveBindForm() {
      try {
        const $form = this.$refs.bindForm;
        await $form.validate();

        const { ...form } = this.bindForm;
        const { code, message } = (await this.$http.post('/api/manager/vehicle/batterybind', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('绑定成功');
        await this.reload();
        this.closeBindForm();
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleUnbind({ id, vehicleCode, batteryId }) {
      try {
        await this.$confirm(`确认解绑${vehicleCode}的电池, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/vehicle/batteryunbind', { vehicleId: id, batteryId })).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('解绑成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    async remoteBattery(keyStr) {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/battery/list', {
          currPage: 1, pageSize: 10, keyStr, isBind: 'UNBIND',
        })).body;
        if (code !== '200') throw new Error(message);
        const { rows } = respData;
        this.bindForm_batteryList = _.map(rows, o => ({
          ...o,
          batteryStatusText: (_.find(this.statusList, { id: o.batteryStatus }) || { name: o.batteryStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
  },
  async mounted() {
    if (this.key_user_info.userType === 'PLATFORM') {
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
    }
    await this.reload();
  },
};
</script>

<style scoped>
.edit-form >>> .el-form-item {
  height: 73px;
}
</style>
