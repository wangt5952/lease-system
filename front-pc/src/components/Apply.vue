<template>
  <div style="padding:10px;">
    <template>
      <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
        <el-tab-pane label="审核列表" name="second">
          <div>
            <el-form :inline="true">
              <el-form-item>
                <el-input style="width:500px;" v-model="search.keyStr" placeholder="登录名/手机号码/昵称/姓名/身份证号/所属企业Code/所属企业名"></el-input>
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
          <el-table :data="applyList" style="width: 100%">
            <el-table-column prop="applyTitle" label="申请标题"></el-table-column>
            <el-table-column prop="applyTypeText" label="申请类型"></el-table-column>
            <el-table-column prop="applyStatusText" label="申请状态"></el-table-column>
            <el-table-column prop="applyContent" label="内容" width="250"></el-table-column>
            <el-table-column prop="applyUserName" label="申请人名称" width="100"></el-table-column>
            <el-table-column prop="applyOrgName" label="申请企业名称" width="100"></el-table-column>
            <el-table-column label="操作">
              <template slot-scope="{row}">
                <el-button icon="el-icon-edit" style="color:#20B648" size="mini" type="text" @click="agree(row)">同意</el-button>
                <el-button icon="el-icon-edit" style="color:red" size="mini" type="text" @click="reject(row)">驳回</el-button>
                <!-- <el-tooltip content="删除" placement="top">
                  <el-button icon="el-icon-delete" size="mini" type="text" @click="handleDelete(row)"></el-button>
                </el-tooltip> -->
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

          <!-- <el-dialog title="查看内容" :visible.sync="showApplyFormVisible" style="margin-top:-0px" :close-on-click-modal="false" width="70%">
            <el-table :data="unitApplyList" style="width: 100%">
              <el-table-column prop="applyTitle" label="申请标题"></el-table-column>
              <el-table-column prop="applyTypeText" label="申请类型"></el-table-column>
              <el-table-column prop="applyStatusText" label="申请状态"></el-table-column>
              <el-table-column prop="applyContent" label="内容" width="120"></el-table-column>
              <el-table-column prop="applyUserName" label="申请人名称"></el-table-column>
              <el-table-column prop="applyOrgName" label="申请企业名称"></el-table-column>
              <el-table-column label="操作">
                <template slot-scope="{row}">
                  <el-button icon="el-icon-edit" size="mini" type="text"  @click="agree(row)">同意</el-button>
                  <el-button icon="el-icon-edit" size="mini" type="text"  @click="peject(row)">驳回</el-button>
                </template>
              </el-table-column>
              <span slot="footer" class="dialog-footer">
                <el-button @click="closeUnitApplyForm">关闭</el-button>
              </span>
            </el-table>
          </el-dialog> -->

        </el-tab-pane>
        <el-tab-pane label="我的申请" name="first">
          <div>
            <div style="margin-right:10px;">
              <el-button icon="el-icon-plus" type="primary" size="small" @click="addApply()">添加申请</el-button>
            </div>
            <el-table :data="applyList" style="width: 100%">
              <el-table-column prop="applyTitle" label="申请标题"></el-table-column>
              <el-table-column prop="applyTypeText" label="申请类型"></el-table-column>
              <el-table-column prop="applyStatusText" label="申请状态"></el-table-column>
              <el-table-column prop="applyContent" label="内容" width="250"></el-table-column>
              <el-table-column prop="applyUserName" label="申请人名称" width="100"></el-table-column>
              <el-table-column prop="applyOrgName" label="申请企业名称" width="100"></el-table-column>
              <el-table-column label="操作">
                <template slot-scope="{row}">
                  <el-button icon="el-icon-edit" style="color:#20B648" size="mini" type="text" @click="agree(row)">同意</el-button>
                  <el-button icon="el-icon-edit" style="color:red" size="mini" type="text" @click="reject(row)">驳回</el-button>
                  <!-- <el-tooltip content="删除" placement="top">
                    <el-button icon="el-icon-delete" size="mini" type="text" @click="handleDelete(row)"></el-button>
                  </el-tooltip> -->
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

            <!-- <el-dialog title="申请内容" :visible.sync="applyFormVisible" style="margin-top:-0px" :close-on-click-modal="false" width="50%">
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
                <el-button @click="closeApplyForm">取消</el-button>
                <el-button type="primary" @click="saveApplyForm">{{form.id ? '保存' : '添加'}}</el-button>
              </span>
            </el-dialog> -->

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
      activeName: 'second',
      applyList: [],
      unitApplyList: [],
      search: {
        keyStr: '',
        applyType: 'VEHICLEAPPLY',
        applyStatus: 'TOBEAUDITED',
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
      pageSizes: [5, 10, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      showApplyFormVisible: false,

      orgApplyList: [],
      applyFormVisible: false,
      form: {
        applyType: 'VEHICLEAPPLY',
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

    handleClick() {
    },
    // handleClick(tab, event) {
    //   console.log(tab, event);
    // },
    closeUnitApplyForm() {
      this.showApplyFormVisible = false;
    },
    async addApply() {
      const $form = this.$refs.form;
      if ($form) $form.resetFields();
      this.applyFormVisible = true;
    },

    async saveApplyForm() {
      const $form = this.$refs.form;
      await $form.validate();
      const { form } = this;
      try {
        const { code, message } = (await this.$http.post('/api/manager/apply/addone', form)).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('添加成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      this.applyFormVisible = false;
    },
    closeApplyForm() {
      this.applyFormVisible = false;
    },

    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },
    // 同意
    async agree(row) {
      try {
        const { code, message } = (await this.$http.post('/api/manager/apply/applyapproval', {
          applyId: row.id, flag: 'AGREE',
        })).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('审批通过');
        await this.reload();
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async reject(row) {
      try {
        const { code, message } = (await this.$http.post('/api/manager/apply/applyapproval', {
          applyId: row.id, flag: 'REJECT',
        })).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('驳回通过');
        await this.reload();
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    // 查看审批列表(管理员权限)
    // async unintReload() {
    //   try {
    //     const { code, message, respData } = (await this.$http.post('/api/manager/apply/getbypk', [this.key_user_info.id])).body;
    //     if (code !== '200') throw new Error(message);
    //     this.unitApplyList.push(respData);
    //     this.unitApplyList = _.map(this.unitApplyList, o => ({
    //       ...o,
    //       applyTypeText: (_.find(this.applyTypeList, { id: o.applyType }) || { name: o.applyType }).name,
    //       applyStatusText: (_.find(this.applyStatusList, { id: o.applyStatus }) || { name: o.applyStatus }).name,
    //     }));
    //   } catch (e) {
    //     const message = e.statusText || e.message;
    //     this.$message.error(message);
    //   }
    // },
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
  },
  async mounted() {
    await this.reload();
  },
};
</script>

<style>

</style>
