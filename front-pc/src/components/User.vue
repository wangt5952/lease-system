<template>
  <div v-loading="loading" style="padding:10px;display:flex:1;display:flex;flex-direction:column;">
    <div style="display:flex;">
      <template v-if="res['FUNCTION'].indexOf('manager-user-addone') >= 0">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加人员</el-button>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="search.keyStr" placeholder="登录名/手机号码/昵称/姓名/身份证号/所属企业Code/所属企业名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.userStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.userType" placeholder="请选择类型" style="width:100%;">
            <el-option v-for="o in searchTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="list" height="100%"  style="width: 100%;margin-top:10px;">
      <el-table-column prop="loginName" label="用户名"></el-table-column>
      <el-table-column prop="userMobile" label="手机号"></el-table-column>
      <el-table-column prop="userTypeText" label="用户类型"></el-table-column>
      <el-table-column prop="userIcon" label="用户LOGO"></el-table-column>
      <el-table-column prop="nickName" label="昵称"></el-table-column>
      <el-table-column prop="userName" label="姓名"></el-table-column>
      <el-table-column prop="userRealNameAuthFlagText" label="实名认证"></el-table-column>
      <!-- PLATFORM:平台, ENTERPRISE:企业 -->
      <el-table-column label="操作" width="250">
        <template slot-scope="{row}">
          <template v-if="key_user_info.userType !== 'INDIVIDUAL'">
            <template v-if="row.userType === 'INDIVIDUAL'">
              <el-button icon="el-icon-plus" size="mini" type="text" @click="vehicleReload(row)">绑定车辆</el-button>
              <el-button icon="el-icon-search" size="mini" type="text" @click="bindVehicleForm(row)">查看车辆</el-button>
            </template>
          </template>
          <template v-if="key_user_info.userType === 'PLATFORM'">
            <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
            <el-button icon="el-icon-edit" size="mini" type="text" @click="showAssignRoleForm(row)">分配角色</el-button>
          </template>
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
      <el-form class="edit-form" :model="form" ref="form">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="loginName" :rules="[{required:true, message:'请填写用户名'}]" label="用户名">
              <el-input v-model="form.loginName" placeholder="请填写用户名" auto-complete="off" :disabled="disabledForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userMobile" :rules="[{required:true, message:'请填写手机号码'}]" label="手机号码">
              <el-input v-model="form.userMobile" placeholder="请填写手机号码" auto-complete="off" :disabled="disabledForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userType" :rules="[{required:true, message:'请选择用户类型'}]" label="用户类型">
              <el-select v-model="form.userType" placeholder="请选择用户类型" style="width:100%;" :disabled="disabledForm">
                <el-option v-for="o in typeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="LOGO路径">
              <el-input v-model="form.userIcon" placeholder="请输入LOGO路径" auto-complete="off" :disabled="disabledForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="昵称">
              <el-input v-model="form.nickName" placeholder="请输入昵称" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="姓名" prop="userName" :rules="[{required:true, message:'请填写用户姓名'}]">
              <el-input v-model="form.userName" placeholder="请输入姓名" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userRealNameAuthFlag" :rules="[{required:true, message:'请选择实名认证类型'}]" label="实名认证">
              <el-select v-model="form.userRealNameAuthFlag" placeholder="请选择实名认证类型" style="width:100%;">
                <el-option v-for="o in authList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userPid" label="身份证号">
              <el-input v-model="form.userPid" placeholder="请选择企业" auto-complete="off" :disabled="disabledForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身份证正面">
              <el-input v-model="form.userIcFront" auto-complete="off" :disabled="disabledForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身份证背面">
              <el-input v-model="form.userIcBack" auto-complete="off" :disabled="disabledForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="手举身份证合照">
              <el-input v-model="form.userIcGroup" auto-complete="off" :disabled="disabledForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="userStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.userStatus" placeholder="请选择状态" style="width:100%;">
                <el-option v-for="o in statusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="orgId" :rules="[{required:true, message:'请选择企业'}]" label="企业">
              <el-select v-model="form.orgId" placeholder="请选择企业" style="width:100%;" >
                <el-option v-for="o in orgList" :key="o.id" :label="o.orgName" :value="o.id"></el-option>
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

    <el-dialog :title="`分配角色 ( ${assignRoleForm.name} ) `" :visible.sync="assignRoleFormVisible" :close-on-click-modal="false">
      <el-form :model="form" ref="form" v-loading="loading && assignRoleFormVisible">
        <el-row :gutter="10">
          <el-col :span="24">
            <el-form-item label="角色">
              <el-select v-model="assignRoleForm.list" :placeholder="`请选择角色`" style="width:100%;" multiple>
                <el-option v-for="o in roleList" :key="o.id" :label="o.roleName" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer" >
        <el-button @click="closeAssignRoleForm">取消</el-button>
        <el-button :disabled="loading" type="primary" @click="saveAssignRoleForm">{{form.id ? '保存' : '添加'}}</el-button>
      </span>
    </el-dialog>

    <!-- 车辆列表 -->
    <el-dialog title="车辆列表" :visible.sync="vehicleFormVisible" style="margin-top:-50px" :close-on-click-modal="false" width="80%">
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="vehicleSearch.keyStr" placeholder="车辆编号/车辆型号/车辆品牌/车辆产地/生产商ID/生产商名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="vehicleSearch.vehicleStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchVehicleStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="vehicleSearch.isBind" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchVehicleIsBindList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-table :data="vehicleList" style="width: 100%">
        <el-table-column prop="vehicleCode" label="编号"></el-table-column>
        <el-table-column prop="vehiclePn" label="型号"></el-table-column>
        <el-table-column prop="vehicleBrand" label="品牌"></el-table-column>
        <el-table-column prop="vehicleMadeIn" label="车辆产地"></el-table-column>
        <el-table-column prop="mfrsName" label="生产商"></el-table-column>
        <el-table-column prop="vehicleStatusText" label="状态"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="{row}">
            <el-button type="primary" @click="bindVehicle(row)">绑定</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="vehicleTotal" style="margin-top:10px;"
        @size-change="vehicleHandleSizeChange"
        @current-change="vehicleReload"
        :current-page.sync="vehicleCurrentPage"
        :page-sizes="vehiclePageSizes"
        :page-size="vehiclePageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="vehicleTotal">
      </el-pagination>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeVehicleForm">关闭</el-button>
        <el-button type="primary" @click="saveVehicleForm">保存</el-button>
      </span>
    </el-dialog>

    <el-dialog title="用户车辆列表" :visible.sync="userVehicleFormVisible" style="margin-top:-50px" :close-on-click-modal="false" width="80%">
      <el-table :data="userVehicleList" style="width: 100%">
        <el-table-column prop="vehicleCode" label="编号"></el-table-column>
        <el-table-column prop="vehiclePn" label="型号"></el-table-column>
        <el-table-column prop="vehicleBrand" label="品牌"></el-table-column>
        <el-table-column prop="vehicleMadeIn" label="车辆产地"></el-table-column>
        <el-table-column prop="mfrsName" label="生产商"></el-table-column>
        <el-table-column prop="vehicleStatusText" label="状态"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="{row}">
            <el-button type="info" @click="unBindVehicle(row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>
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
      // 车辆
      vehicleList: [],
      vehicleSearch: {
        vehicleStatus: 'NORMAL',
        isBind: 'UNBIND',
      },
      vehiclePageSizes: [1, 10, 50, 100],
      vehicleCurrentPage: 1,
      vehiclePageSize: 10,
      vehicleTotal: 0,
      vehicleForm: {},
      searchVehicleStatusList: [
        { id: 'NORMAL', name: '正常' },
      ],
      searchVehicleIsBindList: [
        { id: 'UNBIND', name: '未绑定' },
      ],
      // 用户车辆列表
      userVehicleFormVisible: false,
      userVehicleList: [],
      userVehicleForm: {},

      // 用户
      list: [],
      search: {
        userType: '',
        userStatus: '',
      },
      pageSizes: [10, 50, 100, 200],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      formVisible: false,
      disabledForm: false,
      // 车辆列表窗口
      vehicleFormVisible: false,
      form: {},
      typeList: [
        { id: 'PLATFORM', name: '平台' },
        { id: 'ENTERPRISE', name: '企业' },
        { id: 'INDIVIDUAL', name: '个人' },
      ],
      authList: [
        { id: 'AUTHORIZED', name: '已实名' },
        { id: 'UNAUTHORIZED', name: '未实名' },
      ],
      statusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结' },
        { id: 'INVALID', name: '作废' },
      ],
      searchTypeList: [
        { id: '', name: '全部类型' },
        { id: 'PLATFORM', name: '平台' },
        { id: 'ENTERPRISE', name: '企业' },
        { id: 'INDIVIDUAL', name: '个人' },
      ],
      searchStatusList: [
        { id: '', name: '全部状态' },
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结' },
        { id: 'INVALID', name: '作废' },
      ],
      assignRoleFormVisible: false,
      assignRoleForm: {
        list: [],
      },
      roleList: [],
      orgList: [],
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
    vehicleSearch: {
      async handler() {
        await this.vehicleReload();
      },
      deep: true,
    },
  },
  methods: {
    // 车辆
    async vehicleHandleSizeChange(vehiclePageSize) {
      this.vehiclePageSize = vehiclePageSize;
      await this.vehicleReload();
    },
    async vehicleReload(row) {
      this.vehicleForm = { userId: row.id };
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/selectExtUnbindExtByParams', {
          orgId: row.orgId, ...this.vehicleSearch, currPage: this.vehicleCurrentPage, pageSize: this.vehiclePageSize,
        })).body;
        if (code === '40106') {
          this.$store.commit('relogin');
          throw new Error('认证超时，请重新登录');
        }
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.vehicleTotal = total;
        this.vehicleList = _.map(rows, o => ({
          ...o,
          vehicleStatusText: (_.find(this.statusList, { id: o.vehicleStatus }) || { name: o.vehicleStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      this.vehicleFormVisible = true;
    },
    async bindVehicle(row) {
      const { ...form } = this.vehicleForm;
      form.vehicleId = row.id;
      try {
        const { code, message } = (await this.$http.post('/api/manager/user/vehiclebind', form)).body;
        if (code !== '200') throw new Error(message);
        row.id = form.userId;
        await this.vehicleReload(row);
        this.$message.success('绑定成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    closeVehicleForm() {
      this.vehicleFormVisible = false;
    },
    saveVehicleForm() {
      this.$message.success('保存成功');
      this.vehicleFormVisible = false;
    },

    // 用户车辆
    async bindVehicleForm(row) {
      this.userVehicleFormVisible = true;
      this.userVehicleForm = { userId: row.id };
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getVehicleByUserId', {
          id: row.id,
        })).body;
        if (code !== '200') throw new Error(message);
        this.userVehicleList = _.map(respData, o => ({
          ...o,
          vehicleStatusText: (_.find(this.statusList, { id: o.vehicleStatus }) || { name: o.vehicleStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 用户与车辆解绑
    async unBindVehicle(row) {
      const { ...form } = this.userVehicleForm;
      form.vehicleId = row.id;
      try {
        const { code, message } = (await this.$http.post('/api/manager/user/vehicleunbind', form)).body;
        if (code !== '200') throw new Error(message);
        row.id = form.userId;
        await this.bindVehicleForm(row);
        this.$message.success('解绑成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    // 用户
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },

    async reload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/user/list', {
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
          userTypeText: (_.find(this.typeList, { id: o.userType }) || {}).name,
          userRealNameAuthFlagText: (_.find(this.authList, { id: o.userRealNameAuthFlag }) || {}).name,
        }));
        await this.getOrgList();
      } catch (e) {
        this.loading = false;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, name }) {
      try {
        await this.$confirm(`确认删除${name}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/user/delete', [id])).body;
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
        'userMobile',
        'userType',
        'userIcon',
        'nickName',
        'userName',
        'userRealNameAuthFlag',
        'userPid',
        'userIcFront',
        'userIcBack',
        'userIcGroup',
        'userStatus',
        'orgId',
      ]);
      this.formVisible = true;
      if (form.id) {
        this.disabledForm = true;
      } else {
        const $form = this.$refs.form;
        if ($form) $form.resetFields();
        this.disabledForm = false;
      }
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
          const { code, message } = (await this.$http.post('/api/manager/user/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        } else {
          const { ...form } = this.form;
          form.create_user = loginName;
          form.update_user = loginName;
          // form.password = '123';
          const { code, message } = (await this.$http.post('/api/manager/user/add', [form])).body;
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

    async showAssignRoleForm({ id, loginName }) {
      this.assignRoleFormVisible = true;

      this.assignRoleForm = { id, name: loginName, list: [] };

      this.loading = true;
      const { code, respData } = (await this.$http.post('/api/manager/user/getbypk', [id])).body;
      this.loading = false;
      if (code === '200') {
        const { key_role_info } = respData;
        this.assignRoleForm.list = _.map(key_role_info, 'id');
      }
    },
    closeAssignRoleForm() {
      this.assignRoleFormVisible = false;
    },

    async saveAssignRoleForm() {
      try {
        const { id, list } = this.assignRoleForm;
        const { code, message } = (await this.$http.post('/api/manager/user/refuserandroles', { userId: id, roleIds: list.join(',') })).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('分配成功');
        this.closeAssignRoleForm();
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async getOrgList() {
      const { code, respData } = (await this.$http.post('/api/manager/org/list', {
        currPage: 1, pageSize: 999,
      })).body;
      if (code === '200') this.orgList = respData.rows;
    },
  },
  async mounted() {
    const { code, respData } = (await this.$http.post('/api/manager/role/list', {
      currPage: 1, pageSize: 999,
    })).body;
    if (code === '200') this.roleList = respData.rows;
    this.loading = true;
    await this.reload();
    this.loading = false;
  },
};
</script>

<style scoped>
.edit-form >>> .el-form-item {
  height: 65px;
}
</style>
