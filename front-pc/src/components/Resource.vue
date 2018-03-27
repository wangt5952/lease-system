<template>
  <div v-loading="loading" style="padding:10px;display:flex:1;display:flex;flex-direction:column;">

    <div>
      <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm({resType:'CATALOG'})">添加目录</el-button>
    </div>

    <el-table :data="tableTree" row-key="id" style="width: 100%;margin-top:10px;">
      <el-table-column width="80">
        <template slot-scope="{row}">
          <template v-if="row.children && row.children.length">
            <i v-if="expandItem.indexOf(row.id) === -1" @click="showChildren(row.id, true)" class="el-icon-arrow-right" style="cursor:pointer;" :style="{marginLeft:`${(row.tabs - 1) * 10}px`}"></i>
            <i v-else @click="showChildren(row.id, false)" class="el-icon-arrow-down" style="cursor:pointer;" :style="{marginLeft:`${(row.tabs - 1) * 10}px`}"></i>
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="resCode" label="编码" width="100"></el-table-column>
      <el-table-column prop="resName" label="资源名"></el-table-column>
      <el-table-column prop="resTypeText" label="类型"></el-table-column>
      <el-table-column prop="resUrl" label="请求URL"></el-table-column>
      <el-table-column prop="groupSort" label="分组排序"></el-table-column>
      <el-table-column prop="resSort" label="组内排序"></el-table-column>
      <el-table-column prop="showFlagText" label="显示标志"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="{row}">
          <el-button icon="el-icon-edit" size="mini" type="text" @click="showForm(row)">编辑</el-button>
          <el-button v-if="row.resType == 'CATALOG'" icon="el-icon-edit" size="mini" type="text" @click="showForm({resType:'MENU', parent:row.id})">添加菜单</el-button>
          <el-button v-if="row.resType == 'MENU'" icon="el-icon-edit" size="mini" type="text" @click="showForm({resType:'FUNCTION', parent:row.id})">添加功能</el-button>
          <el-tooltip content="删除" placement="top">
            <el-button icon="el-icon-delete" size="mini" type="text" @click="handleDelete(row)"></el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="资源信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form :model="form" ref="form">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="resCode" :rules="[{required:true, message:'请填写编码'}]" label="编码">
              <el-input v-model="form.resCode" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="resName" :rules="[{required:true, message:'请填写资源名'}]" label="资源名">
              <el-input v-model="form.resName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="resType" :rules="[{required:true, message:'请选择类型'}]" label="类型">
              <el-select disabled v-model="form.resType" placeholder="请选择类型" style="width:100%;">
                <el-option v-for="o in typeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="请求URL">
              <el-input v-model="form.resUrl" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="groupSort" label="分组排序" :rules="[{required:true, message:'请填写分组排序'}]">
              <el-input v-model="form.groupSort" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="组内排序">
              <el-input v-model="form.resSort" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="resType" :rules="[{required:true, message:'请选择显示标志'}]" label="显示标志">
              <el-select v-model="form.showFlag" placeholder="请选择显示标志" style="width:100%;">
                <el-option v-for="o in showFlagList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="level" :rules="[{required:true, message:'请填写级别'}]" label="级别">
              <el-input v-model="form.level" auto-complete="off"></el-input>
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
    return {
      loading: false,
      list: [],

      formVisible: false,
      form: {},

      typeList: [
        { id: 'CATALOG', name: '目录' },
        { id: 'MENU', name: '菜单' },
        { id: 'PAGE', name: '页面' },
        { id: 'FUNCTION', name: '功能' },
      ],
      showFlagList: [
        { id: 'SHOW', name: '显示' },
        { id: 'HIDDEN', name: '隐藏' },
      ],

      expandItem: [],
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
    }),

    tableList() {
      return this.list;
    },

    tree() {
      const { list } = this;

      const buildChildren = (parent, tabs) => {
        const childrenList = parent ? _.filter(list, { parent }) : _.filter(list, o => !o.parent);
        if (!childrenList.length) return null;
        return _.map(childrenList, (o) => {
          const children = buildChildren(o.id, tabs + 1);
          if (!children) return { ...o, tabs };
          return { ...o, children, tabs };
        });
      };

      return buildChildren(null, 0);
    },

    tableTree() {
      const { tree } = this;
      if (!tree || !tree.length) return [];
      const flattenChildren = arr => _.map(arr, o => (this.expandItem.indexOf(o.id) !== -1 ? [o, ...flattenChildren(o.children)] : o));

      if (tree.length > 1) return _.flattenDeep(flattenChildren(tree));
      return _.flattenDeep(flattenChildren(tree[0].children));
    },
  },
  watch: {
    formVisible(v) {
      if (!v) {
        this.form = {};
        const $form = this.$refs.form;
        $form.resetFields();
      }
    },
  },
  methods: {
    showChildren(id, value) {
      if (value) {
        if (this.expandItem.indexOf(id) === -1) this.expandItem.push(id);
      } else if (this.expandItem.indexOf(id) !== -1) this.expandItem = _.without(this.expandItem, id);
    },

    async reload() {
      this.loading = true;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/res/list', {
          currPage: 1, pageSize: 999,
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
          resTypeText: (_.find(this.typeList, { id: o.resType }) || { name: o.resType }).name,
          showFlagText: (_.find(this.showFlagList, { id: o.showFlag }) || {}).name,
        }));
        this.loading = false;
      } catch (e) {
        this.loading = false;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, resName }) {
      try {
        await this.$confirm(`确认删除${resName}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/res/delete', [id])).body;
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
        'resCode',
        'resName',
        'resType',
        'resUrl',
        'groupSort',
        'resSort',
        'showFlag',
        'parent',
        'level',
      ]);
      if (!this.form.parent) {
        this.form.parent = this.tree[0].id;
      }
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
          const { code, message } = (await this.$http.post('/api/manager/res/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        } else {
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/res/add', [form])).body;
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
>>> .el-form-item {
  height: 73px;
}
</style>
