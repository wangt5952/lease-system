<template>
  <div v-loading="loading" style="padding:10px;">
    <div style="display:flex;">
      <template v-if="res['FUNCTION'].indexOf('manager-org-addone') >= 0">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加组织</el-button>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="search.keyStr" placeholder="组织Code/组织名称/组织介绍/组织地址/联系人/联系电话/营业执照号码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.orgStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.orgType" placeholder="请选择配件" style="width:100%;">
            <el-option v-for="o in searchTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
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
      <el-table-column prop="orgStatusText" label="状态">
        <template slot-scope="{row}">
          <template v-if="row.orgStatus === 'NORMAL'"><span style="color:#17BE45">正常</span></template>
          <template v-else><span style="color:red">作废</span></template>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="{row}">
          <el-button icon="el-icon-edit" size="mini" type="text" @click="allotVehicle(row)">分配车辆</el-button>
          <template v-if="res['FUNCTION'].indexOf('manager-org-modify') >= 0">
            <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
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

    <el-dialog title="企业信息" :visible.sync="formVisible" :close-on-click-modal="false" :before-close="closeForm">
      <el-form class="edit-form" :model="form" ref="form" :rules="rules1">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="orgCode" label="编码">
              <el-input v-model="form.orgCode" auto-complete="off" :disabled="disabledFormId"></el-input>
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
            <el-form-item prop="orgPhone" label="联系电话">
              <el-input v-model="form.orgPhone" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="orgStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.orgStatus" placeholder="请选择状态" style="width:100%;">
                <el-option v-for="o in statusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="orgBusinessLicences" label="营业执照号">
              <el-input v-model="form.orgBusinessLicences" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item label="营业执照">
              <!-- <input id="uploadFile" type="file" accept="image/*" @change="selectedFile"> -->
              <!-- <el-input v-model="form.orgBusinessLicences" auto-complete="off"></el-input> -->
              <el-upload
                class="avatar-uploader"
                action=""
                list-type="picture-card"
                :show-file-list="false"
                :auto-upload="false"
                :on-change="changeFile">
                <img id="giftImg" v-if="imageUrl" :src="imageUrl" class="avatar">
                <el-button v-else slot="trigger" size="small" type="primary">选取文件</el-button>
              </el-upload>
              <div>
                <el-button size="small" type="primary" @click="checkPhoto">点击查看</el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeForm">取消</el-button>
        <el-button type="primary" @click="saveForm">{{form.id ? '保存' : '添加'}}</el-button>
      </span>
    </el-dialog>

    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="imageUrl" alt="">
    </el-dialog>

    <el-dialog title="绑定企业" :visible.sync="bindFormVehicle" :close-on-click-modal="false">
      <el-form class="edit-form" :model="bindFormOrg" ref="bindFormOrg" :rules="rules2">
        <el-form-item prop="count" style="margin-top:10px;height:30px;" label="分配数量">
          <el-input-number v-model="bindFormOrg.count" :step="1"></el-input-number>
          <template>
            <span style="margin-left:20px">(当前平台闲置 {{ vehicleNumTotal }} 辆车)</span>
          </template>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeBindFormOrg">取消</el-button>
        <el-button type="primary" @click="saveBindFormOrg">{{form.id ? '保存' : '绑定'}}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import _ from 'lodash';
import { mapState } from 'vuex';

export default {
  data() {
    // 表单效验
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
      // callback();
    };
    const checkOrgId = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('编号不能为空'));
      }
      setTimeout(() => {
        if (/[\u4E00-\u9FA5]/g.test(value)) {
          callback(new Error('编号不能为汉字'));
        } else {
          callback();
        }
      }, 500);
      return false;
    };
    // 汉字验证
    const checkSinogram = (rule, value, callback) => {
      setTimeout(() => {
        if (/[\u4E00-\u9FA5]/g.test(value)) {
          callback(new Error('输入内容不能含有汉字'));
        } else {
          callback();
        }
      }, 500);
      return false;
    };
    return {
      disabledFormId: false,
      vehicleNumTotal: undefined,
      imageUrl: '',
      // 最大文件上传大小(单位:M)
      customImageMaxSize: 3,

      base64: '',

      loading: false,
      list: [],
      bindForm_orgList: [],
      search: {
        orgType: '',
        orgStatus: '',
      },

      pageSizes: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      formVisible: false,
      bindFormVehicle: false,
      dialogVisible: false,
      form: {},
      bindFormOrg: {
        count: 1,
      },

      typeList: [
        { id: 'PLATFORM', name: '平台' },
        { id: 'ENTERPRISE', name: '企业' },
      ],
      statusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'INVALID', name: '作废' },
      ],

      searchTypeList: [
        { id: '', name: '全部类型' },
        { id: 'PLATFORM', name: '平台' },
        { id: 'ENTERPRISE', name: '企业' },
      ],
      searchStatusList: [
        { id: '', name: '全部状态' },
        { id: 'NORMAL', name: '正常' },
        // { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],
      rules1: {
        orgCode: [
          { required: true, message: '请填写编码' },
          { validator: checkOrgId, trigger: 'blur' },
        ],
        orgPhone: [
          { validator: checkSinogram, trigger: 'blur' },
        ],
        orgBusinessLicences: [
          { validator: checkSinogram, trigger: 'blur' },
        ],
      },
      rules2: {
        count: [
          { validator: checkVehicleNum, trigger: 'blur' },
        ],
      },

    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
      res: state => _.mapValues(_.groupBy(state.key_res_info, 'resType'), o => _.map(o, 'resCode')),
      orgPhotoPath: state => state.orgPhotoPath,
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
    checkPhoto() {
      this.dialogVisible = true;
    },
    // changeFile(file, fileList) {
    changeFile(file) {
      console.log(file);
      const This = this;
      // this.imageUrl = URL.createObjectURL(file.raw);
      const reader = new FileReader();
      reader.readAsDataURL(file.raw);
      // reader.onload = function(e) {
      reader.onload = function () {
        This.imageUrl = this.result;
      };
    },
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },
    async reload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/org/list', {
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
    showForm(form = {}) {
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
        'orgBusinessLicenceFront',
        'orgStatus',
      ]);
      this.formVisible = true;
      if (form.id) {
        this.disabledFormId = true;
        // form.orgBusinessLicenceFront = this.orgPhotoPath + form.orgBusinessLicenceFront;
        // this.imageUrl = form.orgBusinessLicenceFront;
        // form.orgBusinessLicenceFront = this.orgPhotoPath + form.orgBusinessLicenceFront;
        this.imageUrl = this.orgPhotoPath + form.orgBusinessLicenceFront;
        console.log(this.imageUrl);
        console.log(form);
      } else {
        const $form = this.$refs.form;
        if ($form) $form.resetFields();
        this.disabledFormId = false;
      }
    },
    closeForm() {
      this.formVisible = false;
      const This = this;
      this.form = {};
      setTimeout(() => {
        This.imageUrl = '';
      }, 100);
    },
    // 跳转到给企业分配车辆页面
    async allotVehicle(row) {
      const $form = this.$refs.bindFormOrg;
      if ($form) $form.resetFields();
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/orgCountVehicle', {
           orgId: this.key_user_info.orgId,
        })).body;
        if (code !== '200') throw new Error(message);
        if(code === '200'){
          this.bindFormOrg = { orgId: row.id, count: respData };
          this.vehicleNumTotal = respData;
          this.bindFormVehicle = true;
        }else{
          this.bindFormVehicle = false;
        }
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 关闭
    closeBindFormOrg() {
      this.bindFormVehicle = false;
    },
    // 绑定企业
    async saveBindFormOrg() {
      const $form = this.$refs.bindFormOrg;
      await $form.validate();
      try {
        const { ...form } = this.bindFormOrg;
        form.count = String(this.bindFormOrg.count);
        console.log(form);
        const { code, message } = (await this.$http.post('/api/manager/user/batchvehiclebind', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('绑定企业成功');
        await this.reload();
        this.closeBindFormOrg();
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
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
          form.orgBusinessLicenceFront = this.imageUrl;
          const { code, message } = (await this.$http.post('/api/manager/org/modify', form)).body;
          if (code === '200') {
            this.$message.success('添加成功');
          } else {
            throw new Error(message);
          }
        } else {
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          form.orgBusinessLicenceFront = this.imageUrl;
          const { code, message } = (await this.$http.post('/api/manager/org/addone', form)).body;
          if (code === '200') {
            this.$message.success('添加成功');
          } else {
            throw new Error(message);
          }
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
.avatar-uploader >>> .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader >>> .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
>>> .el-table {
  position: relative;
  overflow-x: hidden;
  overflow-y: scroll;
  /* -webkit-box-sizing: border-box; */
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-flex: 1;
  /* -ms-flex: 1; */
  -ms-flex: 1;
  flex: 1;
  width: 100%;
  max-width: 100%;
  /* font-size: 14px; */
  color: #606266;
  height: 85%;
  max-height: 85%;
}  
</style>
