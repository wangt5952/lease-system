<template>
  <div v-loading="loading" style="padding:10px;">
    <div style="display:flex;">
      <template v-if="res['FUNCTION'].indexOf('manager-mfrs-addone') >= 0">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加制造商</el-button>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="search.keyStr" placeholder="制造商名称/制造商介绍/制造商地址/联系人/联系电话"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.mfrsStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.mfrsType" placeholder="请选择配件" style="width:100%;">
            <el-option v-for="o in searchTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="list" class="mrfsHeight">
      <el-table-column prop="mfrsName" label="制造商名称"></el-table-column>
      <el-table-column prop="mfrsTypeText" label="类型" width="80"></el-table-column>
      <el-table-column label="介绍">
        <template class="qqqq" slot-scope="scope">
          {{ scope.row.mfrsIntroduce }}
        </template>
      </el-table-column>
      <el-table-column prop="mfrsAddress" label="地址"></el-table-column>
      <el-table-column prop="mfrsContacts" label="联系人" width="100"></el-table-column>
      <el-table-column prop="mfrsPhone" label="联系电话" width="150"></el-table-column>
      <el-table-column prop="mfrsStatusText" label="状态" width="100">
        <template slot-scope="{row}">
          <template v-if="row.mfrsStatus === 'NORMAL'"><span style="color:#17BE45">正常</span></template>
          <template v-else><span style="color:red">作废</span></template>
        </template>
      </el-table-column>
      <template v-if="res['FUNCTION'].indexOf('manager-mfrs-modify') >= 0">
        <el-table-column label="操作" width="100">
          <template slot-scope="{row}">
            <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
          </template>
        </el-table-column>
      </template>
    </el-table>
    <!-- 分页1 -->
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
            <el-form-item prop="mfrsName" :rules="[{required:true, message:'请填写制造商名称'}]" label="制造商名称">
              <el-input v-model="form.mfrsName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="mfrsType" :rules="[{required:true, message:'请选择类型'}]" label="类型">
              <el-select v-model="form.mfrsType" placeholder="请选择类型" style="width:100%;">
                <el-option v-for="o in typeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="地址">
              <el-input v-model="form.mfrsAddress" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系人">
              <el-input v-model="form.mfrsContacts" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="mfrsPhone" label="联系电话">
              <el-input v-model.number="form.mfrsPhone"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="mfrsStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.mfrsStatus" placeholder="请选择状态" style="width:100%;">
                <el-option v-for="o in statusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="mfrsIntroduce" label="介绍">
              <el-input type="textarea" v-model="form.mfrsIntroduce" auto-complete="off"></el-input>
              <span :style="residueStrNumber > 0 ? {'color': 'green'}:{'color': 'red'}">
                剩余字数: {{ residueStrNumber }}
              </span>
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
import { isvalidPhone } from '../util/validate';

// 手机验证全局变量
const checkPhone = (rule, value, callback) => {
  if (!value) callback(new Error('请输入手机号码'));
  else if (!isvalidPhone(value)) callback(new Error('请输入正确的11位手机号码'));
  else callback();
};
const checkStrLen = (rule, value, callback) => {
  console.log(value);
  // if (value.length > 80) callback(new Error('字符不能超过80'));
  // else callback();
};
export default {
  data() {
    return {
      loading: false,
      list: [],

      search: {
        mfrsStatus: '',
        mfrsType: '',
      },

      pageSizes: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      formVisible: false,
      form: {},

      typeList: [
        { id: 'VEHICLE', name: '车辆' },
        { id: 'BATTERY', name: '电池' },
        { id: 'PARTS', name: '配件' },
      ],
      statusList: [
        { id: 'NORMAL', name: '正常' },
        // { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],

      searchTypeList: [
        { id: '', name: '全部类型' },
        { id: 'VEHICLE', name: '车辆' },
        { id: 'BATTERY', name: '电池' },
        { id: 'PARTS', name: '配件' },
      ],
      searchStatusList: [
        { id: '', name: '全部状态' },
        { id: 'NORMAL', name: '正常' },
        // { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],
      // 介绍剩余字数
      residueStrNumber: 80,
      rules1: {
        // 验证手机号码
        mfrsPhone: [
          { required: true, validator: checkPhone, trigger: 'blur' },
        ],
        // 验证字符长度(非必填 如果填写不能超过80个字符)
        mfrsIntroduce : [
          { min: 0, max: 80, message: '长度不能超过 80 个字符' },
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
    'form.mfrsIntroduce'(v) {
      let len = 0;
      if (v) len = v.length;
      else len = 0;
      this.residueStrNumber = 80 - len >= 0 ? 80 - len : 0;
    },
  },
  methods: {
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },

    async reload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/mfrs/list', {
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
          mfrsTypeText: (_.find(this.typeList, { id: o.mfrsType }) || { name: o.mfrsType }).name,
          mfrsStatusText: (_.find(this.statusList, { id: o.mfrsStatus }) || { name: o.mfrsStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, resName }) {
      try {
        await this.$confirm(`确认删除${resName}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/mfrs/delete', [id])).body;
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
        'mfrsName',
        'mfrsType',
        'mfrsIntroduce',
        'mfrsAddress',
        'mfrsContacts',
        'mfrsPhone',
        'mfrsStatus',
      ]);
      this.formVisible = true;
      if (!form.id) {
        const $form = this.$refs.form;
        if ($form) $form.resetFields();
      } else {
        // 判断剩余字节长度
        this.residueStrNumber = 80 - form.mfrsIntroduce.length >= 0 ? 80 - form.mfrsIntroduce.length : 0;
      }
    },
    closeForm() {
      this.formVisible = false;
      setTimeout(() => {
        this.residueStrNumber = 80;
      }, 0.2 * 1000);
    },
    async saveForm() {
      const { loginName } = this.key_user_info;
      const $form = this.$refs.form;
      await $form.validate();
      try {
        if (this.form.id) {
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.update_user = loginName;

          const { code, message } = (await this.$http.post('/api/manager/mfrs/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        } else {
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/mfrs/add', [form])).body;
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
>>> .el-textarea__inner {
  height: 60px;
}
/* .mrfsHeight >>> td.el-table_1_column_3 .cell {
  width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis !important;
} */
>>> .el-table .cell {
  box-sizing: border-box;
  white-space: normal;
  word-break: break-all;
  line-height: 23px;
  width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis !important;
}
>>> .mrfsHeight {
  position: relative;
  overflow-x: hidden;
  overflow-y: scroll;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  width: 100%;
  max-width: 100%;
  color: #606266;
  height: 85%;
  max-height: 85%;
}
</style>
