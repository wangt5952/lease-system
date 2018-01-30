<template>
  <div style="display:flex;">
    <div style="display:flex;flex-direction:column;flex:1;">
      <div style="display:flex;height:100px;background:#070f3e;color:#fff;">
        <div style="flex:1;padding:10px;padding-top:25px;">
          <div style="font-size:12px;">车辆实时情况</div>
          <el-input v-model="search.keyword" style="margin-top:5px;" size="mini" suffix-icon="el-icon-search" />
        </div>
        <div @click="showVehicleDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{selectedItem.code}}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆编码</div>
        </div>
        <div @click="showVehiclePath" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:12px;margin-top:20px;">南京市雨花台区大数据产业园</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆当前位置</div>
        </div>
        <div @click="showBatteryDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{selectedItem.value}}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">剩余电量</div>
        </div>
        <div @click="showUserDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">张三</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">使用人</div>
        </div>
      </div>
      <baidu-map @click="handleMapClick" style="width: 100%;flex:1;" :center="mapCenter" :zoom="15" @moveend="syncCenterAndZoom" @zoomend="syncCenterAndZoom">
        <bm-navigation anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-navigation>
        <bm-marker v-for="o in list" :key="`${[o.lng,o.lat].join(',')}`" :icon="{url: '/static/blue-vehicle.svg', size: {width: 64, height: 64}, opts:{ imageSize: {width: 64, height: 64} } }" :position="{lng: o.lng, lat: o.lat}"></bm-marker>
      </baidu-map>
      <!-- <div style="display:flex;background:#eff5f8;height:180px;padding:10px 0;">
        <div style="flex:1;background:#fff;margin-left:10px;">可用车辆</div>
        <div style="flex:1;background:#fff;margin-left:10px;">待修车辆</div>
        <div style="flex:1;background:#fff;margin-left:10px;">已用车辆</div>
        <div style="flex:1;background:#fff;margin-left:10px;">平台车辆</div>
      </div> -->
    </div>
    <div style="display:flex;flex-direction:column;width:250px;background:#eff5f8;padding:10px 20px;">
      <div style="color:#9096ad;font-size:16px;">车辆列表（可用）</div>

      <div style="display:flex;align-items:center;font-size:14px;height:36px;margin-bottom:5px;text-align:center;">
        <div style="flex:1;">车辆编号</div>
        <div style="width:80px;">剩余电量</div>
      </div>
      <div style="flex:1;overflow:scroll;font-size:14px;">
        <div @click="handleSelectItem(o)" v-for="o in list" :key="o.id" :style="selectedId == o.id ? {backgroundColor:'#39a0f6', color:'#fff'} : {backgroundColor:'#e7eef5', color:'#9096ad'}" style="display:flex;align-items:center;height:36px;margin-bottom:5px;border-radius:3px;text-align:center;cursor:pointer;">
          <div style="flex:1;">{{o.code}}</div>
          <div style="width:80px;">{{o.value}}</div>
        </div>
      </div>

    </div>


    <el-dialog title="车辆信息" :visible.sync="vehicleDialogVisible" width="30%">
      <div class="item"><div class="item-name">车辆编号</div><div class="item-value">{{selectedItem.code}}</div></div>
      <div class="item"><div class="item-name">车辆型号</div><div class="item-value">{{selectedItem.clxh}}</div></div>
      <div class="item"><div class="item-name">车辆品牌</div><div class="item-value">{{selectedItem.clpp}}</div></div>
      <div class="item"><div class="item-name">车辆产地</div><div class="item-value">{{selectedItem.clcd}}</div></div>
      <div class="item"><div class="item-name">生产商ID</div><div class="item-value">{{selectedItem.scsid}}</div></div>
      <div class="item"><div class="item-name">车辆状态</div><div class="item-value">{{selectedItem.status}}</div></div>
    </el-dialog>

    <el-dialog title="电池信息" :visible.sync="batteryDialogVisible" width="30%">
      <div class="item"><div class="item-name">电池编号</div><div class="item-value">dc001</div></div>
      <div class="item"><div class="item-name">电池货名</div><div class="item-value">电池A</div></div>
      <div class="item"><div class="item-name">电池品牌</div><div class="item-value">某电池品牌</div></div>
      <div class="item"><div class="item-name">电池型号</div><div class="item-value">xh-001</div></div>
      <div class="item"><div class="item-name">电池参数</div><div class="item-value">参数详情</div></div>
      <div class="item"><div class="item-name">生产商ID</div><div class="item-value">某电池生产商dc001</div></div>
      <div class="item"><div class="item-name">电池状态</div><div class="item-value">正常</div></div>
    </el-dialog>

    <el-dialog title="用户信息" :visible.sync="userDialogVisible" width="30%">
      <div class="item"><div class="item-name">用户名</div><div class="item-value">张三</div></div>
      <div class="item"><div class="item-name">手机号码</div><div class="item-value">18225520167</div></div>
      <div class="item"><div class="item-name">用户类别</div><div class="item-value">个人</div></div>
      <div class="item"><div class="item-name">昵称</div><div class="item-value"></div></div>
      <div class="item"><div class="item-name">姓名</div><div class="item-value">张三</div></div>
      <div class="item"><div class="item-name">实名认证标示</div><div class="item-value">已认证</div></div>
      <div class="item"><div class="item-name">身份证号</div><div class="item-value">340222199409291234</div></div>
      <div class="item"><div class="item-name">所属组织ID</div><div class="item-value">正常</div></div>
      <div class="item"><div class="item-name">用户状态</div><div class="item-value">正常</div></div>
    </el-dialog>
  </div>
</template>

<script>
import _ from 'lodash';

export default {
  data() {
    return {
      search: {},
      points: [],
      selectedId: '1',
      list: [
        { id: '1', code: 'aima001', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.822436, lat: 32.029365 },
        { id: '2', code: 'aima002', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.799044, lat: 32.040109 },
        { id: '3', code: 'aima003', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.797499, lat: 32.029977 },
        { id: '4', code: 'aima004', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.7906, lat: 32.037017 },
        { id: '5', code: 'aima005', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.788229, lat: 32.028814 },
        { id: '6', code: 'aima006', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.773712, lat: 32.048587 },
        { id: '7', code: 'aima007', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.782839, lat: 32.048219 },
        { id: '8', code: 'aima008', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.765448, lat: 32.043506 },
        { id: '9', code: 'aima009', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.801164, lat: 32.019875 },
        { id: '10', code: 'aima010', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.754165, lat: 32.040568 },
        { id: '11', code: 'aima011', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.746044, lat: 32.034017 },
        { id: '12', code: 'aima012', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.742595, lat: 32.021038 },
        { id: '13', code: 'aima013', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.781366, lat: 32.01268 },
        { id: '14', code: 'aima014', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.799332, lat: 32.009863 },
        { id: '15', code: 'aima015', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.801919, lat: 32.001534 },
        { id: '16', code: 'aima016', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.768574, lat: 31.997125 },
        { id: '17', code: 'aima017', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.789702, lat: 32.064714 },
        { id: '18', code: 'aima018', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.790852, lat: 32.057248 },
        { id: '19', code: 'aima019', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.803069, lat: 32.055044 },
        { id: '20', code: 'aima020', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.812267, lat: 32.063735 },
      ],

      vehicleDialogVisible: false,
      batteryDialogVisible: false,
      userDialogVisible: false,
      vehiclePathVisible: false,

      mapCenter: '南京',
    };
  },
  computed: {
    selectedItem() {
      return _.find(this.list, { id: this.selectedId }) || {};
    },
  },
  watch: {
    'search.keyword'(v) {
      this.mapCenter = v;
    },
  },
  methods: {
    syncCenterAndZoom() {},
    showVehicleDialog() {
      this.vehicleDialogVisible = true;
    },
    showBatteryDialog() {
      this.batteryDialogVisible = true;
    },
    showUserDialog() {
      this.userDialogVisible = true;
    },
    showVehiclePath() {
      this.vehiclePathVisible = true;
    },
    handleMapClick({ point }) {
      console.log(point);
    },
    handleSelectItem(item) {
      this.mapCenter = {
        lng: item.lng, lat: item.lat,
      };
      this.selectedId = item.id;
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.map {
  width: 100%;
  height: 500px;
}

>>> .el-dialog {
  background: #eff5f8;
}

>>> .el-dialog .item {
  background: #fff;
  height: 32px;
  margin-top: 5px;
  border-radius: 3px;
  display: flex;
}

>>> .el-dialog .item .item-name {
  width: 120px;
  text-align: center;
  line-height: 32px;
  color: #858c9c;
}

>>> .el-dialog .item .item-value {
  flex: 1;
  line-height: 32px;
}
>>> .el-dialog .el-dialog__body {
  padding-top: 0;
}
>>> .el-dialog .el-dialog__title {
  color: #9298ae;
}
>>> .el-dialog .el-dialog__close {
  font-family:"lt" !important;
  color: #fa142d;
}
>>> .el-dialog .el-dialog__close::before {
  content: "\e6b2" !important;
}
</style>
