<template>
  <div v-loading="loading" style="padding:10px;">
    <!-- 平台 -->
    <template v-if="key_user_info.userType === 'PLATFORM'">
      <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
        <el-tab-pane label="审核列表" name="second">
          <div>
            <el-form :inline="true">
              <el-form-item>
                <el-input style="width:400px;" v-model="sysSearch.keyStr" placeholder="申请标题/内容/申请人/申请企业名称"></el-input>
              </el-form-item>
              <el-form-item>
                <el-select v-model="sysSearch.applyType" placeholder="请选择申请类别" style="width:100%;">
                  <el-option v-for="o in applyTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-select v-model="sysSearch.applyStatus" placeholder="请选择申请状态" style="width:100%;">
                  <el-option v-for="o in applyStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="sysApplyList">
            <el-table-column prop="applyTitle" label="申请标题"></el-table-column>
            <el-table-column prop="applyTypeText" label="申请类型"></el-table-column>
            <el-table-column prop="applyStatusText" label="申请状态"></el-table-column>
            <el-table-column prop="applyUserName" label="申请人名称"></el-table-column>
            <el-table-column prop="applyOrgName" label="申请企业名称"></el-table-column>
            <el-table-column label="操作">
              <template slot-scope="{row}">
                <el-button icon="el-icon-edit" size="mini" type="text" @click="searchInfo(row)">查看内容</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination v-if="sysTotal" style="margin-top:10px;"
            @size-change="sysHandleSizeChange"
            @current-change="sysReload"
            :current-page.sync="sysCurrentPage"
            :page-sizes="sysPageSizes"
            :page-size="sysPageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="sysTotal">
          </el-pagination>

          <el-dialog title="查看内容" :visible.sync="showFormVisible" style="margin-top:-0px" :close-on-click-modal="false" width="70%">
            <el-form ref="applyForm" :model="applyForm" label-width="80px">
              <el-form-item label="具体内容:">
                <el-input type="textarea" v-model="applyForm.applyContent" disabled></el-input>
              </el-form-item>
              <el-form-item label="审批意见:">
                <el-input type="textarea" v-model="applyForm.examineContent"></el-input>
              </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
              <el-button @click="closeUnitApplyForm">关闭</el-button>
              <template v-if="this.appleForms.applyStatus === 'TOBEAUDITED'">
                <el-button type="primary" size="mini" @click="agree()">同意</el-button>
                <el-button type="info" size="mini" @click="reject()">驳回</el-button>
              </template>
            </span>
          </el-dialog>

        </el-tab-pane>
      </el-tabs>
    </template>
    <!-- 企业 -->
    <template v-if="key_user_info.userType === 'ENTERPRISE'">
      <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
        <el-tab-pane label="审核列表" name="second">
          <div>
            <el-form :inline="true">
              <el-form-item>
                <el-input style="width:400px;" v-model="search.keyStr" placeholder="申请标题/内容/申请人/申请企业名称"></el-input>
              </el-form-item>
              <el-form-item>
                <el-select v-model="search.applyType" placeholder="请选择申请类别" style="width:100%;">
                  <el-option v-for="o in applyTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-select v-model="search.applyStatus" placeholder="请选择申请状态" style="width:100%;">
                  <el-option v-for="o in applyStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="applyList" >
            <el-table-column prop="applyTitle" label="申请标题"></el-table-column>
            <el-table-column prop="applyTypeText" label="申请类型"></el-table-column>
            <el-table-column prop="applyStatusText" label="申请状态"></el-table-column>
            <el-table-column prop="applyUserName" label="申请人名称"></el-table-column>
            <el-table-column prop="applyOrgName" label="申请企业名称"></el-table-column>
            <el-table-column label="操作">
              <template slot-scope="{row}">
                <el-button icon="el-icon-edit" size="mini" type="text" @click="searchInfo(row)">查看内容</el-button>
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

          <el-dialog title="查看内容" :visible.sync="showFormVisible" style="margin-top:-0px" :close-on-click-modal="false" width="70%">
            <el-form ref="applyForm" :model="applyForm" label-width="80px">
              <el-form-item label="具体内容:">
                <el-input type="textarea" v-model="applyForm.applyContent" disabled></el-input>
              </el-form-item>
              <el-form-item label="审批意见:">
                <el-input type="textarea" v-model="applyForm.examineContent"></el-input>
              </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
              <el-button @click="closeUnitApplyForm">关闭</el-button>
              <template v-if="this.appleForms.applyStatus === 'TOBEAUDITED'">
                <el-button type="primary" size="mini" @click="agree()">同意</el-button>
                <el-button type="info" size="mini" @click="reject()">驳回</el-button>
              </template>
            </span>
          </el-dialog>
        </el-tab-pane>

        <el-tab-pane label="我的申请" name="first">
          <div style="flex-direction:column;">
            <div style="display:flex;">
              <div style="margin-right:10px;">
                <el-button icon="el-icon-plus" type="primary" size="small" @click="addApply()">添加申请</el-button>
              </div>
              <el-form :inline="true">
                <el-form-item>
                  <el-input style="width:400px;" v-model="enterpriseMySearch.keyStr" placeholder="登录名/手机号码/昵称/姓名/身份证号/所属企业Code/所属企业名"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-select v-model="enterpriseMySearch.applyType" placeholder="请选择申请类别" style="width:100%;">
                    <el-option v-for="o in applyTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-select v-model="enterpriseMySearch.applyStatus" placeholder="请选择申请状态" style="width:100%;">
                    <el-option v-for="o in applyStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </div>

            <el-table :data="myApplyList">
              <el-table-column prop="applyTitle" label="申请标题"></el-table-column>
              <el-table-column prop="applyTypeText" label="申请类型"></el-table-column>
              <el-table-column prop="applyStatusText" label="申请状态"></el-table-column>
              <el-table-column prop="applyUserName" label="申请人名称"></el-table-column>
              <el-table-column prop="applyOrgName" label="申请企业名称"></el-table-column>
              <el-table-column label="操作">
                <template slot-scope="{row}">
                  <el-button icon="el-icon-edit" size="mini" type="text" @click="searchApplyInfo(row)">查看内容</el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination v-if="myApplyTotal" style="margin-top:10px;"
              @size-change="myApplyHandleSizeChange"
              @current-change="myApplyReload"
              :current-page.sync="myApplyCurrentPage"
              :page-sizes="myApplyPageSizes"
              :page-size="myApplyPageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="myApplyTotal">
            </el-pagination>

            <el-dialog title="申请内容" :visible.sync="applyFormVisible" style="margin-top:-0px" :close-on-click-modal="false" width="50%">
              <el-form ref="form" :model="form" label-width="80px">
                <el-form-item label="标题:" prop="applyTitle" :rules="[{required:true, message:'请输入标题'}]">
                  <el-input v-model="form.applyTitle" style="width: 70%;" placeholder="请输入标题"></el-input>
                </el-form-item>
                <el-form-item label="内容:" prop="applyContent" :rules="[{required:true, message:'请输入内容'}]">
                  <el-input v-model="form.applyContent" type="textarea" style="width: 70%;" placeholder="请输入内容"></el-input>
                </el-form-item>
                <el-form-item label="类型:" prop="applyType" :rules="[{required:true, message:'请选择类型'}]">
                  <el-select v-model="form.applyType" placeholder="请选择类型">
                    <el-option v-for="o in applyTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                  </el-select>
                </el-form-item>
              </el-form>
              <span slot="footer" class="dialog-footer">
                <el-button @click="applyFormVisible = false">取消</el-button>
                <el-button type="primary" @click="saveApplyForm">保存</el-button>
              </span>
            </el-dialog>

            <el-dialog title="查看内容" :visible.sync="showApplyFormVisible" style="margin-top:-0px" :close-on-click-modal="false" width="70%">
              <el-form ref="applyForm" :model="applyForm" label-width="80px">
                <el-form-item label="具体内容:">
                  <el-input type="textarea" v-model="applyForm.applyContent"></el-input>
                </el-form-item>
                <el-form-item label="审批意见:">
                  <el-input type="textarea" v-model="applyForm.examineContent"></el-input>
                </el-form-item>
              </el-form>
              <span slot="footer" class="dialog-footer">
                <el-button @click="closeEnApplyForm">关闭</el-button>
              </span>
            </el-dialog>
          </div>
        </el-tab-pane>
      </el-tabs>
    </template>
    <!-- 个人 -->
    <template v-if="key_user_info.userType === 'INDIVIDUAL'">
      <el-tabs v-model="individualName" type="card" @tab-click="handleClick">
        <el-tab-pane label="我的申请" name="first">
          <div style="flex-direction:column;">
            <div style="display:flex;">
              <div style="margin-right:10px;">
                <el-button icon="el-icon-plus" type="primary" size="small" @click="addApply()">添加申请</el-button>
              </div>
              <el-form :inline="true">
                <el-form-item>
                  <el-input style="width:400px;" v-model="individualSearch.keyStr" placeholder="申请标题/内容/申请人/申请企业名称"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-select v-model="individualSearch.applyType" placeholder="请选择申请类别" style="width:100%;">
                    <el-option v-for="o in applyTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-select v-model="individualSearch.applyStatus" placeholder="请选择申请状态" style="width:100%;">
                    <el-option v-for="o in applyStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </div>

            <el-table :data="individualApplyList">
              <el-table-column prop="applyTitle" label="申请标题"></el-table-column>
              <el-table-column prop="applyTypeText" label="申请类型"></el-table-column>
              <el-table-column prop="applyStatusText" label="申请状态"></el-table-column>
              <el-table-column prop="applyUserName" label="申请人名称"></el-table-column>
              <el-table-column prop="applyOrgName" label="申请企业名称"></el-table-column>
              <el-table-column label="操作">
                <template slot-scope="{row}">
                  <el-button icon="el-icon-edit" size="mini" type="text" @click="searchApplyInfo(row)">查看内容</el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination v-if="individualApplyTotal" style="margin-top:10px;"
              @size-change="individualApplyHandleSizeChange"
              @current-change="individualApplyReload"
              :current-page.sync="individualApplyCurrentPage"
              :page-sizes="individualApplyPageSizes"
              :page-size="individualApplyPageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="individualApplyTotal">
            </el-pagination>

            <el-dialog title="查看内容" :visible.sync="showApplyFormVisible" style="margin-top:-0px" :close-on-click-modal="false" width="70%">
              <el-form ref="applyForm" :model="applyForm" label-width="80px">
                <el-form-item label="具体内容:">
                  <el-input type="textarea" v-model="applyForm.applyContent"></el-input>
                </el-form-item>
                <el-form-item label="审批意见:">
                  <el-input type="textarea" v-model="applyForm.examineContent"></el-input>
                </el-form-item>
              </el-form>
              <span slot="footer" class="dialog-footer">
                <el-button @click="closeEnApplyForm">关闭</el-button>
              </span>
            </el-dialog>
            <!-- a -->
            <el-dialog title="申请内容" :visible.sync="applyFormVisible" style="margin-top:-0px" :close-on-click-modal="false" width="50%">
              <el-form ref="form" :model="form" label-width="80px">
                <el-form-item label="标题:" prop="applyTitle" :rules="[{required:true, message:'请输入标题'}]">
                  <el-input v-model="form.applyTitle" style="width: 70%;" placeholder="请输入标题"></el-input>
                </el-form-item>
                <el-form-item label="内容:" prop="applyContent" :rules="[{required:true, message:'请输入内容'}]">
                  <el-input v-model="form.applyContent" type="textarea" style="width: 70%;" placeholder="请输入内容"></el-input>
                </el-form-item>
                <el-form-item label="类型:" prop="applyType" :rules="[{required:true, message:'请选择类型'}]">
                  <el-select v-model="form.applyType" placeholder="请选择类型">
                    <el-option v-for="o in applyTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                  </el-select>
                </el-form-item>
              </el-form>
              <span slot="footer" class="dialog-footer">
                <el-button @click="applyFormVisible = false">取消</el-button>
                <el-button type="primary" @click="saveApplyForm">保存</el-button>
              </span>
            </el-dialog>
          </div>
        </el-tab-pane>
      </el-tabs>
    </template>
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
      activeName: 'second',
      applyList: [],
      applyForm: {},
      search: {
        keyStr: '',
        applyType: 'VEHICLEAPPLY',
        applyStatus: 'TOBEAUDITED',
      },
      enterpriseMySearch: {
        keyStr: '',
        applyType: 'VEHICLEAPPLY',
        applyStatus: '',
      },
      applyTypeList: [
        { id: 'VEHICLEAPPLY', name: '车辆' },
      ],
      applyStatusList: [
        { id: '', name: '全部' },
        { id: 'TOBEAUDITED', name: '待审批' },
        { id: 'AGREE', name: '同意' },
        { id: 'REJECT', name: '驳回' },
      ],
      pageSizes: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      showFormVisible: false,

      appleForms: {},
      orgApplyList: [],
      applyFormVisible: false,
      showApplyFormVisible: false,
      form: {
        applyType: 'VEHICLEAPPLY',
      },
      // 企业
      myApplyList: [],

      myApplyPageSizes: [10, 20, 50, 100],
      myApplyCurrentPage: 1,
      myApplyPageSize: 10,
      myApplyTotal: 0,

      // 个人
      individualName: 'first',
      individualSearch: {
        keyStr: '',
        applyType: 'VEHICLEAPPLY',
        applyStatus: '',
      },
      individualApplyList: [],

      individualApplyPageSizes: [10, 20, 50, 100],
      individualApplyCurrentPage: 1,
      individualApplyPageSize: 10,
      individualApplyTotal: 0,
      // 平台用户
      sysSearch: {
        keyStr: '',
        applyType: 'VEHICLEAPPLY',
        applyStatus: 'TOBEAUDITED',
      },
      sysApplyList: [],
      sysPageSizes: [10, 20, 50, 100],
      sysCurrentPage: 1,
      sysPageSize: 10,
      sysTotal: 0,
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
    enterpriseMySearch: {
      async handler() {
        await this.myApplyReload();
      },
      deep: true,
    },
    individualSearch: {
      async handler() {
        await this.individualApplyReload();
      },
      deep: true,
    },
    sysSearch: {
      async handler() {
        await this.sysReload();
      },
      deep: true,
    },
  },
  methods: {

    handleClick() {
    },
    // handleClick(tab, event) {
    //   console.log(tab, event);
    // },
    closeUnitApplyForm() {
      this.showFormVisible = false;
    },
    async addApply() {
      const $form = this.$refs.form;
      if ($form) $form.resetFields();
      this.applyFormVisible = true;
    },
    // 添加申请
    async saveApplyForm() {
      const $form = this.$refs.form;
      await $form.validate();
      const { form } = this;
      if (this.key_user_info.userType === 'ENTERPRISE') {
        // 企业
        try {
          const { code, message } = (await this.$http.post('/api/manager/apply/addone', form)).body;
          if (code !== '200') throw new Error(message);
          await this.myApplyReload();
          this.$message.success('添加成功');
        } catch (e) {
          const message = e.statusText || e.message;
          this.$message.error(message);
        }
      } else if (this.key_user_info.userType === 'INDIVIDUAL') {
        // 个人
        try {
          const { code, message } = (await this.$http.post('/api/manager/apply/addone', form)).body;
          if (code !== '200') throw new Error(message);
          await this.individualApplyReload();
          this.$message.success('添加成功');
        } catch (e) {
          const message = e.statusText || e.message;
          this.$message.error(message);
        }
      }
      this.applyFormVisible = false;
    },
    // 查看申请者的信息
    async searchApplyInfo(row) {
      this.loading = true;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/apply/getbypk', [row.id])).body;
        if (code !== '200') throw new Error(message);
        this.applyForm = respData;
        this.showApplyFormVisible = true;
        this.loading = false;
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 查看
    async searchInfo(row) {
      // const { appleForms } = this;
      this.appleForms = row;
      // appleForms.id = row.id;
      // appleForms.applyStatus = row.applyStatus;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/apply/getbypk', [row.id])).body;
        if (code !== '200') throw new Error(message);
        this.applyForm = respData;
        this.showFormVisible = true;
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 同意
    async agree() {
      const { appleForms } = this;
      try {
        const { code, message } = (await this.$http.post('/api/manager/apply/applyapproval', {
          applyId: appleForms.id, flag: 'AGREE', examineContent: this.applyForm.examineContent,
        })).body;
        if (code !== '200') throw new Error(message);
        this.showFormVisible = false;
        this.$message.success('审批通过');
        // 平台
        if (this.key_user_info.userType === 'PLATFORM') await this.sysReload();
        // 企业
        if (this.key_user_info.userType === 'ENTERPRISE') await this.reload();
        // await this.searchInfo({ id: appleForms.id });
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 驳回
    async reject() {
      const { appleForms } = this;
      // console.log(appleForms);
      try {
        const { code, message } = (await this.$http.post('/api/manager/apply/applyapproval', {
          applyId: appleForms.id, flag: 'REJECT', examineContent: this.applyForm.examineContent,
        })).body;
        if (code !== '200') throw new Error(message);
        this.showFormVisible = false;
        this.$message.success('驳回通过');
        // 平台
        if (this.key_user_info.userType === 'PLATFORM') await this.sysReload();
        // 企业
        if (this.key_user_info.userType === 'ENTERPRISE') await this.reload();
        // await this.searchInfo({ id: appleForms.id });
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 删除
    async handleDelete(row) {
      try {
        const { code, message } = (await this.$http.post('/api/manager/apply/delete', [row.id])).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('删除成功');
        await this.reload();
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 企业 审核列表
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },
    async reload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/apply/list', {
          currPage: this.currentPage, pageSize: this.pageSize, ...this.search, flag: '0',
        })).body;
        if (code === '40106') {
          this.$store.commit('relogin');
          throw new Error('认证超时，请重新登录');
        }
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.total = total;
        this.applyList = _.map(rows, o => ({
          ...o,
          applyTypeText: (_.find(this.applyTypeList, { id: o.applyType }) || { name: o.applyType }).name,
          applyStatusText: (_.find(this.applyStatusList, { id: o.applyStatus }) || { name: o.applyStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async myApplyHandleSizeChange(myApplyPageSize) {
      this.myApplyPageSize = myApplyPageSize;
      await this.myApplyReload();
    },
    closeEnApplyForm() {
      this.showApplyFormVisible = false;
    },
    // 企业 我的申请
    async myApplyReload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/apply/list', {
          currPage: this.myApplyCurrentPage, pageSize: this.myApplyPageSize, ...this.enterpriseMySearch, flag: '1',
        })).body;
        if (code === '40106') {
          this.$store.commit('relogin');
          throw new Error('认证超时，请重新登录');
        }
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.myApplyTotal = total;
        this.myApplyList = _.map(rows, o => ({
          ...o,
          applyTypeText: (_.find(this.applyTypeList, { id: o.applyType }) || { name: o.applyType }).name,
          applyStatusText: (_.find(this.applyStatusList, { id: o.applyStatus }) || { name: o.applyStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    // 个人 我的申请
    async individualApplyHandleSizeChange(individualApplyPageSize) {
      this.individualApplyPageSize = individualApplyPageSize;
      await this.individualApplyReload();
    },
    async individualApplyReload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/apply/list', {
          currPage: this.individualApplyCurrentPage, pageSize: this.individualApplyPageSize, ...this.individualSearch, flag: '',
        })).body;
        if (code === '40106') {
          this.$store.commit('relogin');
          throw new Error('认证超时，请重新登录');
        }
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.individualApplyTotal = total;
        this.individualApplyList = _.map(rows, o => ({
          ...o,
          applyTypeText: (_.find(this.applyTypeList, { id: o.applyType }) || { name: o.applyType }).name,
          applyStatusText: (_.find(this.applyStatusList, { id: o.applyStatus }) || { name: o.applyStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    // 平台 审核列表
    async sysHandleSizeChange(sysPageSize) {
      this.sysPageSize = sysPageSize;
      await this.sysReload();
    },
    async sysReload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/apply/list', {
          currPage: this.sysCurrentPage, pageSize: this.sysPageSize, ...this.sysSearch, flag: '',
        })).body;
        if (code === '40106') {
          this.$store.commit('relogin');
          throw new Error('认证超时，请重新登录');
        }
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.sysTotal = total;
        this.sysApplyList = _.map(rows, o => ({
          ...o,
          applyTypeText: (_.find(this.applyTypeList, { id: o.applyType }) || { name: o.applyType }).name,
          applyStatusText: (_.find(this.applyStatusList, { id: o.applyStatus }) || { name: o.applyStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
  },
  async mounted() {
    // 平台
    if (this.key_user_info.userType === 'PLATFORM') await this.sysReload();
    // 企业 审核列表 我的申请
    if (this.key_user_info.userType === 'ENTERPRISE') await this.reload();
    if (this.key_user_info.userType === 'ENTERPRISE') await this.myApplyReload();
    // 个人 我的申请
    if (this.key_user_info.userType === 'INDIVIDUAL') await this.individualApplyReload();
  },
};
</script>

<style>

</style>
