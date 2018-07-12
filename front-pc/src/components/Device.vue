<template>
  <div v-loading="loading" style="padding:10px">
    <div style="display:flex;">
      <template v-if="res['FUNCTION'].indexOf('manager-device-addone') >= 0">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="addButton">添加设备</el-button>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:400px;" v-model="search.keyStr" placeholder="设备编码"></el-input>
        </el-form-item>
      </el-form>
    </div>
    <!-- 列表aa -->
    <el-table :data="list" class="deviceHeight">
      <el-table-column prop="deviceId" label="编号" width="100" header-align="left" align="left"></el-table-column>
      <el-table-column prop="deviceTypeListText" label="设备类别" width="80" align="center"></el-table-column>
      <el-table-column prop="perSet" label="请求间隔时间(单位:秒)" width="150" align="center"></el-table-column>
      <el-table-column prop="" label="电池电量" width="70" align="center">
        <template slot-scope="scope">
          {{ scope.row.perSet }} %
        </template>
      </el-table-column>
      <el-table-column prop="resetTypeText" label="硬件复位标志" width="100" align="center"></el-table-column>
      <el-table-column prop="requestTypeText" label="主动请求数据标志" width="200" align="center"></el-table-column>
      <el-table-column label="操作" width="400" align="left">
        <template slot-scope="{row}">
          <el-button icon="el-icon-search" size="mini" type="text" @click="showDeviceLocation(row)">查看设备位置</el-button>
          <template v-if="res['FUNCTION'].indexOf('manager-device-modify') >= 0">
            <el-button icon="el-icon-edit" size="mini" type="text" @click="editButton(row)">编辑</el-button>
          </template>
          <template v-if="res['FUNCTION'].indexOf('manager-device-delete') >= 0">
            <el-tooltip content="删除" placement="top">
              <el-button icon="el-icon-delete" size="mini" type="text" @click="handleDelete(row)"></el-button>
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination v-if="total" style="margin-top:10px;"
      @size-change="handleSizeChange"
      @current-change="reload"
      :current-page.sync="currentPage"
      :page-sizes="pageSizes"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
    <!-- 表单 -->
    <el-dialog title="设备信息" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form" :rules="rules2">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="deviceId" label="编号">
              <el-input v-model="form.deviceId" placeholder="请输入编号" auto-complete="off" :disabled="editForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="deviceType" label="设备类别">
              <el-select v-model="form.deviceType" placeholder="请选择设备类别" style="width:100%;" :disabled="editForm">
                <el-option v-for="o in deviceTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="perSet" label="请求间隔时间 (单位:秒)">
              <el-input v-model.number="form.perSet" placeholder="请输入请求间隔时间" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="reset" label="硬件复位标志">
              <el-select v-model="form.reset" placeholder="请选择硬件复位标志" style="width:100%;">
                <el-option v-for="o in resetTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="request" label="主动请求数据标志">
              <el-select v-model="form.request" placeholder="请选择主动请求数据标志" style="width:100%;">
                <el-option v-for="o in requestTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeForm">取消</el-button>
        <el-button type="primary" v-if="editButtonVisible" @click="saveForm(form)">保存</el-button>
        <el-button type="primary" v-if="addButtonVisible" @click="addForm">添加</el-button>
      </span>
    </el-dialog>

    <div class="deviceLocationClass" :style="styleDiv" @keyup.esc="closeAddresBut">
      <div style="height:50px; width:100%">
        <div style="display:flex; justify-content:center; line-height:50px;font-size:1em;">
          {{ address }}
          <div @click="closeAddresBut" style="position:absolute;top:10px;left:650px;color:#409eff;cursor:pointer">
            <img src="../assets/close.png" alt="" style="height:30px; width:30px;">
          </div>
        </div>
      </div>
      <div style="display:flex; flex:1" >
        <baidu-map @ready="handler" id="baiduMap" style="width: 100%;height:300px" :center="center" :zoom="zoom" :scroll-wheel-zoom="true">
          <bm-scale anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-scale>
          <bm-marker
            @click="clickBmInfoWindow"
            :icon="{url: '/static/device-cur.svg', size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }"
            :position="markerCenter"
            :dragging="false">
            <bm-info-window :position="PopCenter" :title="this.infoWindow.title" :show="this.infoWindow.show" :width="70" :height="60">
              <p v-text="this.infoWindow.contents"></p>
            </bm-info-window>
          </bm-marker>
        </baidu-map>
      </div>
      <div>
        <div @click="closeAddresBut" style="display:flex; justify-content:center;margin-top: 10px">
          <el-button type="info">关闭</el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import _ from 'lodash';
import {
  mapState,
} from 'vuex';
import * as validate from '@/util/validate';

const checkDeviceId = (rule, value, callback) => {
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
      center: {lng: 0, lat: 0},
      zoom: 3,
      deviceLocation:{},
      vehiclPowner:{},
      markerCenter: { lng: 0, lat: 0 },
      PopCenter: { lng: 0, lat: 0 },
      infoWindow: {
        title: '',
        show: true,
        contents: ''
      },
      // 地址解析
      address: '',
      styleDiv: 'display: block; opacity:0; z-index:-1',
      deviceFrom: {},
      batteryNum: '',

      loading: false,
      formVisible: false,
      editButtonVisible: false,
      addButtonVisible: false,
      editForm: false,
      search: {},
      form: {},

      pageSizes: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,

      list: [],
      deviceTypeList: [
        { id: 'VEHICLE', name: '车辆' },
        { id: 'BATTERY', name: '电池' },
        { id: 'PARTS', name: '配件' },
      ],
      resetTypeList: [
        { id: 0, name: '无处理' },
        { id: 1, name: '复位重启' },
      ],
      requestTypeList: [
        { id: 0, name: '无处理' },
        { id: 1, name: '主动请求' },
      ],
      // 表单效验
      rules2: {
        deviceId: [
          { required: true, validator: checkDeviceId },
        ],
        deviceType: [
          { required: true, message: '请输入设备类别' },
        ],
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
    };
  },
  computed: {
    // 获取当前登录用户信息.
    ...mapState({
      key_user_info: state => state.key_user_info,
      key_res_info: state => state.key_res_info,
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
    async clickBmInfoWindow () {
      this.infoWindow.show = !this.infoWindow.show;
      const { batteryNum } = this;
      try {
        // 获取电池剩余电量
        const { code, message, respData } = (await this.$http.post('/api/manager/device/getElectricByDevice',[batteryNum])).body;
        if (code !== '200') throw new Error(message);
        if (respData.Rsoc === "") throw new Error("该设备无电量");
        this.infoWindow.contents = `电池电量:  ${respData.Rsoc} %`;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handler ({BMap, map}) {
      this.center.lng = this.deviceLocation.LON;
      this.center.lat = this.deviceLocation.LAT;
      this.PopCenter = this.center;
      this.markerCenter = this.center;
      this.zoom = 16;

      const new_point = (lng, lat) => {
        this.center.lng = this.deviceLocation.LON;
        this.center.lat = this.deviceLocation.LAT;
        this.PopCenter = this.center;
        this.markerCenter = this.center;
        this.zoom = 16;
        const point = new BMap.Point(lng, lat);
        map.panTo(point);
      };
      // 
      window.new_point = new_point;
      // const myGeo = new BMap.Geocoder();
      // const thisOne = this;
      // myGeo.getLocation(new BMap.Point(thisOne.center.lng, thisOne.center.lat), function(result){
      //   if (result){
      //     thisOne.address = result.address
      //   }
      // });
      // 浏览器定位
      const getCurPosition = () => {
        return new Promise((resolve, reject) => (new BMap.Geolocation()).getCurrentPosition(function get(r) {
          if (this.getStatus() === BMAP_STATUS_SUCCESS) {
            resolve(r);
          } else {
            reject(this.getStatus());
          }
        }, { enableHighAccuracy: true }));
      };

      // 逆地址解析(根据经纬度获取详细地址).
      const getLocation = (lng, lat) => {
        return new Promise(resolve => (new BMap.Geocoder()).getLocation(new BMap.Point(lng, lat), res => resolve(res)));
      };
      window.getLocation = getLocation;
      window.getCurPosition = getCurPosition;
    },
    // 关闭车辆位置窗口按钮
    closeAddresBut() {
      this.styleDiv = 'display: block;opacity:0;z-index:-1';
    },
    // 设备位置
    async showDeviceLocation(row) {
      this.batteryNum = row.deviceId;
      try {
        // 获取坐标
        const { code, message, respData } = (await this.$http.post('/api/manager/device/getLocationByDevice',{
          deviceId: row.deviceId, deviceType: row.deviceType,
        })).body;
        if (code !== '200') {
          this.styleDiv = 'display: block;opacity:0;z-index:-1';
          throw new Error(message);
        }
        if(respData.LON === "" || respData.LAT === "") {
          this.styleDiv = 'display: block;opacity:0;z-index:-1';
          throw new Error("该设备无坐标");
        }
        this.deviceLocation = respData;
        await new_point(respData.LON, respData.LAT);
        this.styleDiv = 'display: block; opacity:1;z-index:1';

        const loc = await getLocation(respData.LON, respData.LAT);
        if(loc) this.address = `地址: ${loc.address}` ;
        else this.address = '地址: ';
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      try {
        // 获取电池剩余电量
        const { code, message, respData } = (await this.$http.post('/api/manager/device/getElectricByDevice',[row.deviceId])).body;
        if (code !== '200') throw new Error(message);
        if (respData.Rsoc === "") throw new Error("该设备无电量");
        this.infoWindow.contents = `电池电量:  ${respData.Rsoc} %`;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    // 分页下拉列表改变出发事件
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },
    // 加载
    async reload() {
      this.loading = true;
      if (this.key_user_info.userType === 'PLATFORM') {
        try {
          const { code, message, respData } = (await this.$http.post('/api/manager/device/lists', {
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
            resetTypeText: (_.find(this.resetTypeList, { id: o.reset }) || {}).name,
            requestTypeText: (_.find(this.requestTypeList, { id: o.request }) || {}).name,
            deviceTypeListText: (_.find(this.deviceTypeList, { id: o.deviceType }) || {}).name,
          }));
          this.loading = false;
        } catch (e) {
          this.loading = false;
          const message = e.statusText || e.message;
          this.$message.error(message);
        }
      }
    },
    // 添加按钮
    addButton(form = {}) {
      this.editForm = false;
      const $form = this.$refs.form;
      if ($form) $form.resetFields();
      this.form = _.pick(form, [
        'deviceId',
        'deviceType',
        'perSet',
        'request',
        'reset',
      ]);
      this.form.perSet = 30;
      this.formVisible = true;
      this.editButtonVisible = false;
      this.addButtonVisible = true;
    },
    // 编辑按钮
    async editButton(form) {
      this.editForm = true;
      const forms = _.pick(form, [
        'deviceId',
        'deviceType',
        'perSet',
        'request',
        'reset',
      ]);
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/device/getbypk', forms)).body;
        if (code !== '200') throw new Error(message);
        this.form = respData;
        this.formVisible = true;
        this.addButtonVisible = false;
        this.editButtonVisible = true;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 关闭
    closeForm() {
      this.formVisible = false;
    },
    // 保存功能
    async saveForm(form) {
      const $form = this.$refs.form;
      await $form.validate();
      try {
        const { code, message } = (await this.$http.post('/api/manager/device/modify', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('保存成功');
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      this.formVisible = false;
      await this.reload();
    },
    // 添加功能
    async addForm() {
      const $form = this.$refs.form;
      await $form.validate();
      const { ...form } = this.form;
      try {
        const { code, message } = (await this.$http.post('/api/manager/device/addone', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('添加成功');
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      await this.reload();
      this.closeForm();
    },
    // 删除功能
    async handleDelete(form) {
      this.form = _.pick(form, [
        'deviceId',
        'deviceType',
      ]);
      try {
        await this.$confirm(`确认删除编号为${form.deviceId}的设备吗, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/device/delete', [this.form])).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('删除成功');
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
>>> .deviceHeight {
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
.deviceLocationClass {
  border:1px solid #f2f2f2;
  display:flex;
  background-color:#f2f2f2;
  border-radius:5px;
  flex-direction:column;
  width:700px;
  height:400px;
  position:absolute;
  left:350px;
  top: 160px
}
</style>
