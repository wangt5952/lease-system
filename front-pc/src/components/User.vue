<template>
  <div v-loading="loading" style="padding:10px;">
    <div style="display:flex;">
      <template v-if="key_user_info.userType === 'PLATFORM'">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加用户</el-button>
        </div>
      </template>
      <template v-if="key_user_info.userType !== 'INDIVIDUAL'">
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
      </template>
    </div>
    <el-table :data="list" class="userHeight">
      <el-table-column prop="loginName" label="用户名"></el-table-column>
      <el-table-column prop="userMobile" label="手机号"></el-table-column>
      <el-table-column prop="userTypeText" label="用户类型"></el-table-column>
      <el-table-column prop="orgName" label="所属企业名称">
        <template v-if="scope.row.userType !== 'PLATFORM'" slot-scope="scope">
          {{ scope.row.orgName }}
        </template>
      </el-table-column>
      <el-table-column prop="userIcon" label="用户LOGO"></el-table-column>
      <el-table-column prop="nickName" label="昵称"></el-table-column>
      <el-table-column prop="userName" label="姓名"></el-table-column>
      <el-table-column prop="userRealNameAuthFlagText" label="实名认证">
        <template slot-scope="{row}">
          <template v-if="row.userRealNameAuthFlag === 'AUTHORIZED'"><span style="color:#17BE45">已实名</span></template>
          <template v-else><span style="color:red">未实名</span></template>
        </template>
      </el-table-column>
      <!-- PLATFORM:平台, ENTERPRISE:企业 -->
      <el-table-column label="操作" width="270">
        <template slot-scope="{row}">
          <template v-if="key_user_info.userType !== 'INDIVIDUAL'">
            <template v-if="row.userType === 'INDIVIDUAL'">
              <el-button icon="el-icon-plus" size="mini" type="text" @click="vehicleReload(row)">绑定车辆</el-button>
              <el-button icon="el-icon-search" size="mini" type="text" @click="bindVehicleForm(row)">查看车辆</el-button>
              <el-button icon="el-icon-search" size="mini" type="text" @click="showPhoto(row)">查看身份证</el-button>
            </template>
          </template>
          <template v-if="key_user_info.userType === 'PLATFORM'">
            <template v-if="row.userType === 'ENTERPRISE'">
              <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
              <el-button icon="el-icon-edit" size="mini" type="text" @click="retrieveVehicle(row)">批量收回车辆</el-button>
            </template>
            <!-- <template v-if="row.userType !== 'INDIVIDUAL'">
            </template> -->
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

    <el-dialog title="人员信息" :visible.sync="formVisible" :close-on-click-modal="false" :close-on-press-escape="true" :before-close="closeForm">
      <el-form class="edit-form" :model="form" ref="form" :rules="rules2">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="loginName" :rules="[{required:true, message:'请填写用户名'}]" label="用户名">
              <el-input v-model="form.loginName" placeholder="请填写用户名" auto-complete="off" :disabled="disabledForm"></el-input>
            </el-form-item>
          </el-col>
          <template>
            <el-col :span="8">
              <el-form-item prop="userMobile" label="手机号码">
                <el-input v-model="form.userMobile" placeholder="请填写手机号码" auto-complete="off" :disabled="disabledForm"></el-input>
              </el-form-item>
            </el-col>
          </template>
          <el-col :span="8">
            <el-form-item prop="userType" :rules="[{required:true, message:'请选择用户类型'}]" label="用户类型">
              <el-select v-model="form.userType" placeholder="请选择用户类型" style="width:100%;" :disabled="disabledForm">
                <el-option v-for="o in typeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="企业图标">
              <!-- <el-input v-model="form.userIcon" placeholder="请输入LOGO路径" auto-complete="off" :disabled="disabledForm"></el-input> -->
              <el-select v-model="form.userIcon" placeholder="请选择企业Logo" style="width:100%;">
                <el-option v-for="(o, i) in userIconPhoto" style="backgroundColor: #F1F1F1" :key="`${i}`" :label="o.iconName" :value="o.iconName">
                  <img class="companyLogo" :src="userIconPath + o.iconName" alt="">
                </el-option>
              </el-select>
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
            <el-form-item prop="userStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.userStatus" placeholder="请选择状态" style="width:100%;">
                <el-option v-for="o in statusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="orgId" :rules="[{required:true, message:'请选择企业'}]" label="所属企业">
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
        <el-button @click="assignRoleFormVisible = false">取消</el-button>
        <el-button :disabled="loading" type="primary" @click="saveAssignRoleForm">{{assignRoleForm.id ? '保存' : '添加'}}</el-button>
      </span>
    </el-dialog>

    <!-- 车辆列表 -->
    <el-dialog title="车辆列表" :visible.sync="vehicleFormVisible" style="margin-top:-50px" :close-on-click-modal="false" width="80%">
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="vehicleSearch.keyStr" placeholder="车辆编号/车辆型号/车辆品牌/车辆产地/生产商ID/生产商名"></el-input>
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
        @current-change="vehiclesReload"
        :current-page.sync="vehicleCurrentPage"
        :page-sizes="vehiclePageSizes"
        :page-size="vehiclePageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="vehicleTotal">
      </el-pagination>
      <span slot="footer" class="dialog-footer">
        <el-button @click="vehicleFormVisible = false">关闭</el-button>
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
      <span slot="footer" class="dialog-footer">
        <el-button @click="userVehicleFormVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <el-dialog title="批量收回车辆" :visible.sync="returnVehicleFormVehicle" :close-on-click-modal="false">
      <el-form class="edit-form" :model="returnVehicleForm" ref="returnVehicleForm" :rules="rules3">
        <el-form-item prop="count" style="margin-top:10px;height:30px;" label="回收数量">
          <el-input-number v-model="returnVehicleForm.count" :step="1"></el-input-number>
          <template>
            <span style="margin-left:20px">(当前企业共 {{ vehicleNumTotal }} 辆车)</span>
          </template>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="returnVehicleFormVehicle = false">取消</el-button>
        <el-button type="primary" @click="saveReturnVehicleForm">确定</el-button>
      </span>
    </el-dialog>

    <!-- 照片表单 -->
    <el-dialog title="用户认证信息" :visible.sync="photoFormVisible" style="margin-top:-50px" :close-on-click-modal="false" width="80%">
      <div class="pidPhoto">
        <!-- 身份证正面 -->
        <div class="imgClass">
          <img :src="userPidPath + cardPhotoFront" alt="">
          <!-- <div class="imgButton">
            <el-button type="primary" @click="searchPid(pid,'cardPhotoFront')">放大观看</el-button>
          </div> -->
        </div>
        <!-- 身份证反面 -->
        <div class="imgClass">
          <img :src="userPidPath + cardPhotoBack" alt="">
          <!-- <div class="imgButton">
            <el-button type="primary" @click="searchPid(pid,'cardPhotoBack')">放大观看</el-button>
          </div> -->
        </div>
        <!-- 双手举起身份证 -->
        <div class="imgClass">
          <img :src="userPidPath + cardPhotoGroup" alt="">
          <!-- <div class="imgButton">
            <el-button type="primary" @click="searchPid(pid,'cardPhotoGroup')">放大观看</el-button>
          </div> -->
        </div>
      </div>
      <span slot="footer" class="dialog-footer" >
        <el-button @click="photoFormVisible = false">关闭</el-button>
        <el-button type="info" @click="overrule">驳回</el-button>
        <el-button :disabled="loading" type="primary" @click="identification">通过</el-button>
      </span>
    </el-dialog>
    <!-- 放大后的表单 -->
    <el-dialog :title="pidInfo" :visible.sync="photoInfoVisible" style="margin-top:-50px" :close-on-click-modal="false" width="80%" center>
      <img :src="userPidPath + cardPhoto" alt="">
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
    // 验证手机格式
    const checkPhone = (rule, value, callback) => {
      setTimeout(() => {
        if (/^$|^\d+$/.test(value)) {
          callback();
        } else {
          callback(new Error('请输入正确手机格式'));
        }
      }, 500);
      return false;
    };
    // 车辆表单效验
    const checkVehicleNum = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('数量不能为空'));
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

      vehicleForms: {},
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
      pageSizes: [10, 20, 50, 100],
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
      ],
      typeList2: [
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
        { id: 'INVALID', name: '作废' },
      ],
      assignRoleFormVisible: false,
      assignRoleForm: {
        list: [],
      },
      roleList: [],
      orgList: [],
      userIconPhoto: [
        { iconName: '20180514171821.png' },
        { iconName: '20180514172108.png' },
        { iconName: '20180514172120.png' },
      ],
      // 照片表单
      photoFormVisible: false,
      pid: '',
      photoInfoVisible: false,
      cardPhoto: '',
      pidInfo: '',

      // 表单效验
      rules2: {
        userMobile: [
          { required: true, message: '请填写手机号码' },
          { validator: checkPhone, trigger: 'blur' },
        ],
      },

      // 批量回收车辆
      returnVehicleFormVehicle: false,
      returnVehicleForm: {
        count: 1,
      },
      vehicleNumTotal: undefined,
      rules3: {
        count: [
          { validator: checkVehicleNum, trigger: 'blur' },
        ],
      },
      // 用户验证表单
      cardForm: {},
      // 身份证正面照片
      cardPhotoFront: undefined,
      // 身份证反面照片
      cardPhotoBack: undefined,
      // 双手举起身份证照片
      cardPhotoGroup: undefined,
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
      res: state => _.mapValues(_.groupBy(state.key_res_info, 'resType'), o => _.map(o, 'resCode')),
      userIconPath: state => state.userIconPath,
      userPidPath: state => state.userPidPath,
    }),
  },
  watch: {
    search: {
      async handler() {
        await this.reload();
      },
      deep: true,
    },
    async 'vehicleSearch.keyStr'() {
      await this.vehiclesReload();
    },
  },
  methods: {
    // 查看营业执照或者身份证照片
    async showPhoto(row) {
      this.cardForm = row;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/user/getbypk', [row.id])).body;
        if (code !== '200') throw new Error(message);
        const data = respData.key_user_info;
        // hasOwnProperty(暂时不可用) 查看某个元素中是否包含 某元素  返回 boolean
        // typeof 判断该对象里 有没有该字段
        // 判断 用户是否上传的身份证
        if (!(typeof (data.userIcFront)) ||
           !(typeof (data.userIcBack)) ||
           !(typeof (data.userIcGroup)) ||
           data.userIcFront === '' || data.userIcBack === '' || data.userIcGroup === '') {
          throw new Error('该用户身份照不齐全');
        } else {
          this.pid = row.id;
          this.cardPhotoFront = data.userIcFront;
          this.cardPhotoBack = data.userIcBack;
          this.cardPhotoGroup = data.userIcGroup;
          // console.log(this.userPidPath+this.cardPhotoFronta);
          // console.log(this.userPidPath+this.cardPhotoGroup);
          this.photoFormVisible = true;
        }
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 查看放大的身份证
    async searchPid(pid, value) {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/user/getbypk', [pid])).body;
        if (code !== '200') throw new Error(message);
        const data = respData.key_user_info;
        if (value === 'cardPhotoFront') {
          this.pidInfo = '正面照';
          this.cardPhoto = data.userIcFront;
        } else if (value === 'cardPhotoBack') {
          this.pidInfo = '背面照';
          this.cardPhoto = data.userIcBack;
        } else {
          this.pidInfo = '举起双手合照';
          this.cardPhoto = data.userIcGroup;
        }
        this.photoInfoVisible = true;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 通过
    async identification() {
      const { cardForm } = this;
      try {
        const { code, message } = (await this.$http.post('/api/manager/user/userapproval', {
          userId: cardForm.id, flag: 'AUTHORIZED',
        })).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('实名认证成功');
        await this.reload();
        this.photoFormVisible = false;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 驳回
    async overrule() {
      const { cardForm } = this;
      try {
        const { code, message } = (await this.$http.post('/api/manager/user/userapproval', {
          userId: cardForm.id, flag: 'REJECTAUTHORIZED',
        })).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('驳回成功');
        await this.reload();
        this.photoFormVisible = false;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 批量回收车辆页面
    async retrieveVehicle(row) {
      const $form = this.$refs.returnVehicleForm;
      if ($form) $form.resetFields();
      this.returnVehicleForm = { orgId: row.orgId, count: this.returnVehicleForm.count };
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/orgCountVehicle', { orgId: row.orgId })).body;
        if (code !== '200') throw new Error(message);
        if (code === '200') {
          this.vehicleNumTotal = respData;
          this.returnVehicleForm.count = respData;
          this.returnVehicleFormVehicle = true;
        }
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 确认回收车辆
    async saveReturnVehicleForm() {
      const $form = this.$refs.returnVehicleForm;
      await $form.validate();
      try {
        const { ...form } = this.returnVehicleForm;
        form.count = String(this.returnVehicleForm.count);
        const { code, message } = (await this.$http.post('/api/manager/user/batchvehicleunbind', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('归还车辆成功');
        await this.reload();
        this.returnVehicleFormVehicle = false;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 车辆
    async vehicleHandleSizeChange(vehiclePageSize) {
      this.vehiclePageSize = vehiclePageSize;
      await this.vehiclesReload();
    },
    // 第一次加载车辆页面
    async vehicleReload(row) {
      this.vehicleForms = row;
      const { vehicleForm } = this;
      vehicleForm.userId = row.id;
      vehicleForm.orgId = row.orgId;
      if (row.orgId) {
        try {
          const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/selectExtUnbindExtByParams', {
            orgId: row.orgId, currPage: this.vehicleCurrentPage, pageSize: this.vehiclePageSize,
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
      } else {
        this.vehicleTotal = 0;
        this.vehicleList = [];
        this.$message.error('该用户未绑定企业');
      }
    },
    // 带参分页  加载车辆页面
    async vehiclesReload() {
      const { vehicleForms } = this;
      if (vehicleForms.orgId) {
        try {
          const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/selectExtUnbindExtByParams', {
            orgId: vehicleForms.orgId, currPage: this.vehicleCurrentPage, pageSize: this.vehiclePageSize, ...this.vehicleSearch,
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
      } else {
        this.vehicleTotal = 0;
        this.vehicleList = [];
        this.$message.error('该用户未绑定企业');
      }
    },
    async bindVehicle(row) {
      const { ...form } = this.vehicleForm;
      form.vehicleId = row.id;
      try {
        const { code, message } = (await this.$http.post('/api/manager/user/vehiclebind', form)).body;
        if (code !== '200') throw new Error(message);
        await this.vehiclesReload();
        this.$message.success('绑定成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
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
        // row.id = form.userId;
        await this.bindVehicleForm({ ...row, id: form.userId });
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
    // 加载
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
          userTypeText: (_.find(this.typeList2, { id: o.userType }) || {}).name,
          userRealNameAuthFlagText: (_.find(this.authList, { id: o.userRealNameAuthFlag }) || {}).name,
        }));
        // await this.getOrgList();
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
    async showForm(form = { }) {
      await this.getOrgList();
      this.form = _.pick(form, [
        'id',
        'loginName',
        'userMobile',
        'userType',
        'userIcon',
        'nickName',
        'userName',
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
          this.$message.success('修改成功');
        } else {
          const { ...form } = this.form;
          form.create_user = loginName;
          form.update_user = loginName;
          // form.password = '123';
          const { code, message } = (await this.$http.post('/api/manager/user/addone', form)).body;
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
      await this.getRoleList();
      this.assignRoleFormVisible = true;
      this.assignRoleForm = { id, name: loginName, list: [] };
      const { code, respData } = (await this.$http.post('/api/manager/user/getbypk', [id])).body;
      if (code === '200') {
        const { key_role_info } = respData;
        this.assignRoleForm.list = _.map(key_role_info, 'id');
      }
    },

    async saveAssignRoleForm() {
      try {
        const { id, list } = this.assignRoleForm;
        const { code, message } = (await this.$http.post('/api/manager/user/refuserandroles', { userId: id, roleIds: list.join(',') })).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('分配成功');
        this.assignRoleFormVisible = false;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async getOrgList() {
      try {
        const { code, respData } = (await this.$http.post('/api/manager/org/list', {
          currPage: 1, pageSize: 999, orgStatus: 'NORMAL',
        })).body;
        if (code === '200') this.orgList = respData.rows;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async getRoleList() {
      const { code, respData } = (await this.$http.post('/api/manager/role/list', {
        currPage: 1, pageSize: 999,
      })).body;
      if (code === '200') this.roleList = respData.rows;
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
.pidPhoto {
  display: flex;
  flex-direction: row;
}
.pidPhoto > .imgClass {
  margin: 10px;
  height: 339px;
  width: 350px;
}
.imgButton {
  margin-left: 30%;
}
/* 企业图片1 */
.companyLogo {
  width: 30px;
  height: 30px;
}
.edit-form >>> .el-form-item {
  height: 65px;
}
/* >>> .el-table {
  height: 100%;
}
>>> .el-table__body-wrapper {
  height: calc(100% - 42px);
} */
>>> .userHeight {
  position: relative;
  overflow-x: hidden;
  overflow-y: scroll;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  width: calc(100% - 2px);
  /* width: 100%; */
  max-width: 100%;
  color: #606266;
  height: 85%;
  max-height: 85%;
}
</style>
