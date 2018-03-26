<template>
  <div style="display:flex;position: relative;">
    <div style="display:flex;flex-direction:column;flex:1;">
      <div style="display:flex;height:100px;background:#070f3e;color:#fff;">
        <div style="flex:1;padding:10px;padding-top:25px;">
          <div style="font-size:12px;">车辆实时情况</div>
          <el-input v-model="search.keyword" style="margin-top:5px;" size="mini" suffix-icon="el-icon-search" />
        </div>
        <div @click="showVehicleDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{selectedItem.code}}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆编码 {{ searchLocList }}</div>
        </div>
        <div @click="showVehiclePath" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:12px;margin-top:20px;">南京市雨花台区大数据产业园</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆当前位置</div>
        </div>
        <div @click="showBatteryDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{selectedItem.value}}%</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">剩余电量</div>
        </div>
        <div @click="showUserDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">张三</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">使用人</div>
        </div>
      </div>

      <baidu-map @click="handleMapClick" style="width: 100%;flex:1;" :center="mapCenter" :zoom="15" @moveend="syncCenterAndZoom" @zoomend="syncCenterAndZoom">
        <bm-navigation anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-navigation>
        <template v-if="vehiclePathVisible">
          <bm-polyline :path="selectedItem.path" stroke-color="blue" :stroke-opacity="0.5" :stroke-weight="2"></bm-polyline>
          <bm-marker :icon="{url: '/static/vehicle-cur.svg', size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: selectedItem.lng, lat: selectedItem.lat}"></bm-marker>
        </template>
        <bm-marker v-else v-for="o in vehicleList" :key="o.id" :icon="{url: selectedItem.id == o.id ? '/static/vehicle-cur.svg' : (`/static/${o.icon || 'vehicle-ok.svg'}`), size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: o.lng, lat: o.lat}"></bm-marker>
      </baidu-map>
      <el-date-picker v-if="vehiclePathVisible" v-model="searchLocList.time" value-format="yyyy-MM-dd HH:mm:ss" size="mini" type="datetimerange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="position:absolute;top:120px;right:400px;"></el-date-picker>

      <div v-if="false" style="display:flex;background:#eff5f8;height:180px;padding:10px 0;">
        <div style="flex:1;background:#fff;margin-left:10px;">
          可用车辆
          <div>
            <img src="/static/half-ring-1.png" style="background:#e9457d;"/>
          </div>
        </div>
        <div style="flex:1;background:#fff;margin-left:10px;">待修车辆
          <div>
            <img src="/static/half-ring.png" style="background:#7fd0b8;"/>
          </div>
        </div>
        <div style="flex:1;background:#fff;margin-left:10px;">已用车辆
          <img src="/static/half-ring.png" style="background:#f7af54;"/>
        </div>
        <div style="flex:1;background:#fff;margin-left:10px;">平台车辆
          <div>
            <img src="/static/half-ring.png" style="background:#4c85cf;"/>
          </div>
        </div>
      </div>
    </div>
    <div style="display:flex;flex-direction:column;width:250px;background:#eff5f8;padding:10px 20px;">
      <div style="color:#9096ad;font-size:16px;">车辆列表（可用）</div>
      <div style="display:flex;align-items:center;font-size:14px;height:36px;margin-bottom:5px;text-align:center;">
        <div style="flex:1;">车辆编号</div>
        <div style="width:80px;">剩余电量</div>
      </div>
      <div style="flex:1;overflow:scroll;font-size:14px;">
        <div @click="handleSelectItem(o)" v-for="o in vehicleList" :key="o.id" :style="selectedId == o.id ? {backgroundColor:'#39a0f6', color:'#fff'} : {backgroundColor:'#e7eef5', color:'#9096ad'}" style="display:flex;align-items:center;height:36px;margin-bottom:5px;border-radius:3px;text-align:center;cursor:pointer;">
          <div style="flex:1;">{{o.vehicleCode}}</div>
          <div style="width:80px;">{{o.value}}%</div>
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

// const path = [
//   { lng: 118.790852, lat: 32.057248 },
//   { lng: 118.803069, lat: 32.055044 },
//   { lng: 118.812267, lat: 32.063735 },
// ];

export default {
  data() {
    return {
      search: {},
      points: [],
      selectedId: '1',
      vehicleList: [],
      searchLocList: {},
      vehicleInfo: {},
      // vehicleList: [
      //   { id: '1', code: 'aima001', icon: 'vehicle-low.svg', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.822436, lat: 32.029365, path: [{ lng: 118.822436, lat: 32.029365 }, ...path] },
      //   { id: '2', code: 'aima002', icon: 'vehicle-low.svg', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.799044, lat: 32.040109, path: [{ lng: 118.799044, lat: 32.040109 }, ...path] },
      //   { id: '3', code: 'aima003', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.797499, lat: 32.029977, path: [{ lng: 118.797499, lat: 32.029977 }, ...path] },
      //   { id: '4', code: 'aima004', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.7906, lat: 32.037017, path: [{ lng: 118.7906, lat: 32.037017 }, ...path] },
      //   { id: '5', code: 'aima005', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.788229, lat: 32.028814, path: [{ lng: 118.788229, lat: 32.028814 }, ...path] },
      //   { id: '6', code: 'aima006', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.773712, lat: 32.048587, path: [{ lng: 118.773712, lat: 32.048587 }, ...path] },
      //   { id: '7', code: 'aima007', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.782839, lat: 32.048219, path: [{ lng: 118.782839, lat: 32.048219 }, ...path] },
      //   { id: '8', code: 'aima008', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.765448, lat: 32.043506, path: [{ lng: 118.765448, lat: 32.043506 }, ...path] },
      //   { id: '9', code: 'aima009', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.801164, lat: 32.019875, path: [{ lng: 118.801164, lat: 32.019875 }, ...path] },
      //   { id: '10', code: 'aima010', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.754165, lat: 32.040568, path: [{ lng: 118.754165, lat: 32.040568 }, ...path] },
      //   { id: '11', code: 'aima011', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.746044, lat: 32.034017, path: [{ lng: 118.746044, lat: 32.034017 }, ...path] },
      //   { id: '12', code: 'aima012', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.742595, lat: 32.021038, path: [{ lng: 118.742595, lat: 32.021038 }, ...path] },
      //   { id: '13', code: 'aima013', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.781366, lat: 32.01268, path: [{ lng: 118.781366, lat: 32.01268 }, ...path] },
      //   { id: '14', code: 'aima014', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.799332, lat: 32.009863, path: [{ lng: 118.799332, lat: 32.009863 }, ...path] },
      //   { id: '15', code: 'aima015', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.801919, lat: 32.001534, path: [{ lng: 118.801919, lat: 32.001534 }, ...path] },
      //   { id: '16', code: 'aima016', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.768574, lat: 31.997125, path: [] },
      //   { id: '17', code: 'aima017', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.789702, lat: 32.064714, path: [] },
      //   { id: '18', code: 'aima018', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.790852, lat: 32.057248, path: [] },
      //   { id: '19', code: 'aima019', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.803069, lat: 32.055044, path: [] },
      //   { id: '20', code: 'aima020', value: '60%', status: '正常', scsid: '艾玛电动车生产商am001', clcd: '江苏省南京市', clpp: '艾玛', clxh: '型号A', lng: 118.812267, lat: 32.063735, path: [] },
      // ],

      vehicleDialogVisible: false,
      batteryDialogVisible: false,
      userDialogVisible: false,
      vehiclePathVisible: false,
      mapCenter: '南京',
    };
  },
  computed: {
    selectedItem() {
      const vehicleListInfo = (_.find(this.vehicleList, { id: this.selectedId }) || {});
      return vehicleListInfo;
    },
  },
  watch: {
    'search.keyword'(v) {
      this.mapCenter = v;
    },
    searchLocList: {
      async handler() {
        this.reloadLocList();
      },
      deep: true,
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
      this.vehiclePathVisible = !this.vehiclePathVisible;
    },
    handleMapClick() {
    },

    async handleSelectItem(item) {
      console.log(item);
      this.mapCenter = {
        lng: item.lng, lat: item.lat,
      };
      this.selectedId = item.id;
      // const { vehicleList } = this;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getbypk', {
          id: item.id, flag: 'true',
        })).body;
        if (code !== '200') throw new Error(message);
        this.vehicleInfo = respData;
        // this.vehicleInfo = _.map(vehicleList, (o) => {
        //   const info = _.find(vehicleInfo, { VeicleID: o.id });
        //   if (!info) return o;
        //   return {
        //     ...o,
        //     info,
        //   }
        // });
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 获取所有车辆信息
    async reloadVehicleList() {
      const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/list', {
        needPaging: false, vehicleStatus: 'NORMAL',
      })).body;
      if (code !== '200') throw new Error(message);
      this.vehicleList = respData.rows;
      if (this.vehicleList && this.vehicleList.length && !_.find(this.vehicleList, { id: this.selectedId })) {
        this.selectedId = this.vehicleList[0].id;
      }
      await this.reloadVehicleLoc();
      await this.reloadVehiclePower();
    },
    // 获取车辆定位(经、纬度)
    async reloadVehicleLoc() {
      const { vehicleList } = this;
      const { respData } = (await this.$http.post('/api/manager/vehicle/getlocbyvehiclepk', _.map(vehicleList, 'id'))).body;
      const locList = respData;
      this.vehicleList = _.map(vehicleList, (o) => {
        const loc = _.find(locList, { VehicleID: o.id });
        if (!loc) return o;
        return {
          ...o,
          lat: loc.LAT,
          lng: loc.LON,
          LocTime: loc.LocTime,
        };
      });
    },
    // 获取车辆电池电量信息
    async reloadVehiclePower() {
      const { vehicleList } = this;
      try {
        const { respData } = (await this.$http.post('/api/manager/vehicle/getpowerbyvehiclepk', _.map(vehicleList, 'id'))).body;
        // if (code !== '200') throw new Error(message);
        const locList = respData;
        this.vehicleList = _.map(vehicleList, (o) => {
          const power = _.find(locList, { VehicleID: o.id });
          if (!power) return o;
          return {
            ...o,
            value: power.RSOC,
          };
        });
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 获取车辆在某一段时间内行驶的记录
    async reloadLocList() {
      const { time } = this.searchLocList;
      const id = this.selectedId;
      try {
        const { respData } = (await this.$http.post('/api/manager/vehicle/gettrackbytime', { id, startTime: time[0], endTime: time[1] })).body;
        // if (code !== '200') throw new Error(message);
        const locList = respData;
        this.mapCenter = {
          lng: locList[0].LON, lat: locList[0].LAT,
        };
        this.vehicleList = _.map(this.vehicleList, (o) => {
          if (o.id !== id) return o;
          return {
            ...o,
            path: _.map(locList, i => ({
              lng: i.LON,
              lat: i.LAT,
            })),
          };
        });
        // this.vehicleList = _.map(vehicleList, o => {
        //   const loc = _.find(locList, {VehicleID: o.id});
        //   if (!loc) return o;
        //   return {
        //     ...o,
        //     lat: loc.LAT,
        //     lng: loc.LON,
        //     LocTime: loc.LocTime,
        //   }
        // })
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
  },
  async mounted() {
    await this.reloadVehicleList();
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
