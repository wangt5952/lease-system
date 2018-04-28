<template>
  <div v-loading="loading" style="padding:10px;">
    <div style="display:flex;">
      <!-- PLATFORM:平台, ENTERPRISE:企业1 -->
      <template v-if="res['FUNCTION'].indexOf('manager-parts-addone') >= 0">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加配件</el-button>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="search.keyStr" placeholder="配件编码/配件货名/配件品牌/配件型号/配件参数/生产商ID/生产商名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.partsStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.partsType" placeholder="请选择配件" style="width:100%;">
            <el-option v-for="o in searchTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <!-- a -->
    <el-table :data="list" class="partsHeight">
      <el-table-column prop="partsCode" label="编码"></el-table-column>
      <el-table-column prop="partsName" label="配件货名"></el-table-column>
      <el-table-column prop="partsBrand" label="品牌"></el-table-column>
      <el-table-column prop="partsPn" label="型号"></el-table-column>
      <el-table-column prop="partsTypeText" label="类别"></el-table-column>
      <el-table-column prop="partsParameters" label="参数"></el-table-column>
      <el-table-column prop="mfrsName" label="生产商"></el-table-column>
      <el-table-column prop="partsStatusText" label="状态"></el-table-column>
      <el-table-column label="绑定车辆">
        <template slot-scope="{row}">
          <template v-if="!row.vehicleId"><span style="color:red">未绑定</span></template>
          <template v-else><span style="color:#17BE45">已绑定</span></template>
        </template>
      </el-table-column>
      <!-- PLATFORM:平台, ENTERPRISE:企业 -->
      <template v-if="res['FUNCTION'].indexOf('manager-parts-modify') >= 0">
        <el-table-column label="操作" width="100">
          <template slot-scope="{row}">
            <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
            <!-- <el-button icon="el-icon-edit" size="mini" type="text" @click="next(row)">下一步</el-button> -->
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

    <el-dialog title="配件信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form" :rules="rules1">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="partsCode" label="编码">
              <el-input v-model="form.partsCode" auto-complete="off" :disabled="disabledFormId"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="partsName" :rules="[{required:true, message:'请填写配件货名'}]" label="配件货名">
              <el-input v-model="form.partsName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="partsBrand" :rules="[{required:true, message:'请填写品牌'}]" label="品牌">
              <el-input v-model="form.partsBrand" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="partsPn" :rules="[{required:true, message:'请填写型号'}]" label="型号">
              <el-input v-model="form.partsPn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="partsType" :rules="[{required:true, message:'请选择类别'}]" label="类别">
              <el-select v-model="form.partsType" placeholder="请选择类型" style="width:100%;">
                <el-option v-for="o in typeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="partsParameters" :rules="[{required:true, message:'请填写参数'}]" label="参数">
              <el-input v-model="form.partsParameters" auto-complete="off"></el-input>
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
            <el-form-item prop="partsStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.partsStatus" placeholder="请选择状态" style="width:100%;">
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
    const checkPartsId = (rule, value, callback) => {
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
    return {
      loading: false,
      list: [],

      search: {
        partsStatus: '',
        partsType: '',
      },

      pageSizes: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      formVisible: false,
      disabledFormId: false,
      form: {},

      typeList: [
        { id: 'SEATS', name: '车座' },
        { id: 'FRAME', name: '车架' },
        { id: 'HANDLEBAR', name: '车把' },
        { id: 'BELL', name: '车铃' },
        { id: 'TYRE', name: '轮胎' },
        { id: 'PEDAL', name: '脚蹬' },
        { id: 'DASHBOARD', name: '仪表盘' },
      ],
      statusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],

      searchTypeList: [
        { id: '', name: '全部配件' },
        { id: 'SEATS', name: '车座' },
        { id: 'FRAME', name: '车架' },
        { id: 'HANDLEBAR', name: '车把' },
        { id: 'BELL', name: '车铃' },
        { id: 'TYRE', name: '轮胎' },
        { id: 'PEDAL', name: '脚蹬' },
        { id: 'DASHBOARD', name: '仪表盘' },
      ],

      searchStatusList: [
        { id: '', name: '全部状态' },
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],
      mfrsList: [],
      rules1: {
        partsCode: [
          { required: true, message: '请填写编码' },
          { validator: checkPartsId, trigger: 'blur' },
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
    // next(row) {
    //   this.$router.push({ path: 'battery', query: { aaaa: '12312' }})
    // },
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },
    async reload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/parts/list', {
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
          partsTypeText: (_.find(this.typeList, { id: o.partsType }) || { name: o.partsType }).name,
          partsStatusText: (_.find(this.statusList, { id: o.partsStatus }) || { name: o.partsStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, resName }) {
      try {
        await this.$confirm(`确认删除${resName}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/parts/delete', [id])).body;
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
        'partsCode',
        'partsName',
        'partsBrand',
        'partsPn',
        'partsType',
        'partsParameters',
        'mfrsId',
        'partsStatus',
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
          const { code, message } = (await this.$http.post('/api/manager/parts/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        } else {
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/parts/add', [form])).body;
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
    const { code, respData } = (await this.$http.post('/api/manager/mfrs/list', {
      currPage: 1, pageSize: 999,
    })).body;
    if (code === '200') this.mfrsList = respData.rows;
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
>>> .partsHeight {
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
