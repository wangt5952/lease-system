<template>
  <div style="display:flex;position: relative;">
    <div style="display:flex;flex-direction:column;flex:1;">
      <div style="display:flex;height:100px;background:#070f3e;color:#fff;">
        <div style="flex:1;padding:10px;padding-top:25px;">
          <!-- <div style="font-size:12px;">车辆实时情况</div> -->
          <el-input v-model="search.keyword" style="margin-top:5px;" size="mini" suffix-icon="el-icon-search" placeholder="请输入要查看的地址"></el-input>
        </div>
        <div @click="showVehicleDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{vehicleInfo.vehicleCode}}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆编码</div>
        </div>
        <div @click="showVehiclePath" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:12px;margin-top:20px;">{{ address }}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆当前位置</div>
        </div>
        <div @click="showBatteryDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{selectedItem.value}}%</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">剩余电量</div>
        </div>
        <div @click="showUserDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{ userInfo.userName }}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">使用人</div>
        </div>
      </div>

      <baidu-map class="allmap" @click="handleMapClick" style="width: 100%;flex:1;" :center="mapCenter" :zoom="15" @moveend="syncCenterAndZoom" @zoomend="syncCenterAndZoom" :scroll-wheel-zoom="true">
        <!-- 右上角控件 -->
        <bm-navigation anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-navigation>
        <template v-if="vehiclePathVisible">
          <!-- 路线折线图 -->
          <bm-polyline :path="selectedItem.path" stroke-color="blue" :stroke-opacity="0.5" :stroke-weight="4"></bm-polyline>
          <!-- 点聚合(图片所在位置) -->
          <bm-marker :icon="{url: '/static/vehicle-cur.svg', size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: selectedItem.lng, lat: selectedItem.lat}"></bm-marker>
          <!-- 圆形图 -->
          <bm-circle :center="circlePath.center" :radius="circlePath.radius" stroke-color="blue" :stroke-opacity="0.5" :stroke-weight="2" @lineupdate="updateCirclePath" :editing="true"></bm-circle>
        </template>
        <bm-marker v-else v-for="o in vehicleList" :key="o.id" :icon="{url: selectedItem.id == o.id ? '/static/vehicle-cur.svg' : (`/static/${o.icon || 'vehicle-ok.svg'}`), size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: o.lng, lat: o.lat}"></bm-marker>
      </baidu-map>

      <!-- Element <DatePicker>日期选择器控件 -->
      <el-date-picker v-if="vehiclePathVisible"
        v-model="searchLocList.time"
        type="datetimerange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd HH:mm:ss"
        size="mini"
        style="position:absolute;top:120px;right:400px;">
      </el-date-picker>

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

    <div style="display:flex;flex-direction:column;width:170px;background:#eff5f8;padding:10px 20px;overflow:auto;">
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
      <div class="item"><div class="item-name">车辆编号</div><div class="item-value">{{vehicleInfo.vehicleCode}}</div></div>
      <div class="item"><div class="item-name">车辆型号</div><div class="item-value">{{vehicleInfo.vehiclePn}}</div></div>
      <div class="item"><div class="item-name">车辆品牌</div><div class="item-value">{{vehicleInfo.vehicleBrand}}</div></div>
      <div class="item"><div class="item-name">车辆产地</div><div class="item-value">{{vehicleInfo.vehicleMadeIn}}</div></div>
      <div class="item"><div class="item-name">生产商名称</div><div class="item-value">{{vehicleInfo.mfrsName}}</div></div>
      <div class="item"><div class="item-name">车辆状态</div>
        <template v-if="vehicleInfo.vehicleStatus === 'NORMAL'">
          <div class="item-value">正常</div>
        </template>
        <template v-if="vehicleInfo.vehicleStatus === 'FREEZE'">
          <div class="item-value">冻结/维保</div>
        </template>
        <template v-if="vehicleInfo.vehicleStatus === 'INVALID'">
          <div class="item-value">作废</div>
        </template>
      </div>
    </el-dialog>

    <el-dialog title="电池信息" :visible.sync="batteryDialogVisible" width="30%">
      <div class="item"><div class="item-name">电池编号</div><div class="item-value">{{ powerInfo.batteryCode }}</div></div>
      <div class="item"><div class="item-name">电池货名</div><div class="item-value">{{ powerInfo.batteryName }}</div></div>
      <div class="item"><div class="item-name">电池品牌</div><div class="item-value">{{ powerInfo.batteryBrand }}</div></div>
      <div class="item"><div class="item-name">电池型号</div><div class="item-value">{{ powerInfo.batteryPn }}</div></div>
      <div class="item"><div class="item-name">电池参数</div><div class="item-value">{{ powerInfo.batteryParameters }}</div></div>
      <div class="item"><div class="item-name">生产商名称</div><div class="item-value">{{ powerInfo.mfrsName }}</div></div>
      <div class="item"><div class="item-name">电池状态</div>
        <template v-if=" powerInfo.batteryStatus === 'NORMAL'">
          <div class="item-value">正常</div>
        </template>
        <template v-if=" powerInfo.batteryStatus === 'FREEZE'">
          <div class="item-value">冻结</div>
        </template>
        <template v-if=" powerInfo.batteryStatus === 'INVALID'">
          <div class="item-value">作废</div>
        </template>
      </div>
    </el-dialog>

    <el-dialog title="用户信息" :visible.sync="userDialogVisible" width="30%">
      <div class="item"><div class="item-name">用户名</div><div class="item-value">{{ userInfo.loginName }}</div></div>
      <div class="item"><div class="item-name">手机号码</div><div class="item-value">{{ userInfo.userMobile }}</div></div>

      <div class="item"><div class="item-name">用户类别</div>
        <template v-if="userInfo.userType === 'INDIVIDUAL'">
          <div class="item-value">个人</div>
        </template>
      </div>
      <div class="item"><div class="item-name">昵称</div><div class="item-value">{{ userInfo.nickName }}</div></div>
      <div class="item"><div class="item-name">姓名</div><div class="item-value">{{ userInfo.userName }}</div></div>
      <div class="item"><div class="item-name">实名认证标示</div>
        <template v-if="userInfo.userRealNameAuthFlag === 'AUTHORIZED'">
          <div class="item-value">已实名</div>
        </template>
        <template v-else>
          <div class="item-value">未实名</div>
        </template>
      </div>
      <div class="item"><div class="item-name">身份证号</div><div class="item-value">{{ userInfo.userPid }}</div></div>
      <div class="item"><div class="item-name">所属企业</div><div class="item-value">{{ userInfo.orgName }}</div></div>
      <div class="item"><div class="item-name">用户状态</div>
        <template v-if="userInfo.userStatus === 'NORMAL'">
          <div class="item-value">正常</div>
        </template>
        <template v-if="userInfo.userStatus === 'FREEZE'">
          <div class="item-value">冻结</div>
        </template>
        <template v-if="userInfo.userStatus === 'INVALID'">
          <div class="item-value">作废</div>
        </template>
      </div>
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
      // 绘制圆形图
      circlePath: {
        center: {
          lng: 116.404,
          lat: 39.915,
        },
        radius: 500,
      },

      address: '',
      search: {},
      points: [],
      selectedId: '1',
      vehicleList: [],
      searchLocList: {},
      vehicleInfo: {},
      userInfo: {},
      powerInfo: {},
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
    // 圆形区域
    updateCirclePath(e) {
      this.circlePath.center = e.target.getCenter();
      this.circlePath.radius = e.target.getRadius();
    },
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
      this.mapCenter = {
        lng: item.lng, lat: item.lat,
      };
      this.selectedId = item.id;

      // 车辆、电池、使用人、根据半径查看车辆 信息
      try {
        // 车辆、电池信息
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getbypk', {
          id: item.id, flag: 'true',
        })).body;
        if (code !== '200') throw new Error(message);
        if (respData) {
          this.vehicleInfo = respData;
          this.powerInfo = respData.bizBatteries[0];
        } else {
          this.$message.error('该车辆未绑定电池');
          this.vehicleInfo = {};
          this.powerInfo = {};
        }

        // 使用人信息
        const { code: userCode, message: userMessage, respData: userRespData } = (await this.$http.post('/api/manager/user/getUserByVehicle', [item.id])).body;
        if (userCode !== '200') throw new Error(userMessage);
        if (userRespData) {
          this.userInfo = userRespData;
        } else {
          this.userInfo = {};
        }

        // 根据经度、纬度、半径范围查询信息
        const { code: radiusCode, message: radiusMessage } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
          lng: item.lng, lat: item.lat, radius: 1000,
        })).body;
        if (radiusCode !== '200') throw new Error(radiusMessage);
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);

        const userMessage = e.statusText || e.userMessage;
        this.$message.error(userMessage);

        const radiusMessage = e.statusText || e.radiusMessage;
        this.$message.error(radiusMessage);
      }

      // 根据坐标反解析地址
      // const BMap = new BMap().Map("allmap");
      // const geocoder = BMap.Geocoder();
      // const point = BMap.Point(item.lng, item.lat);
      // geocoder.getLocation(point, (geocoderResult) => {
      //   this.address = geocoderResult.address;
      // });
    },
    // 获取所有车辆信息
    async reloadVehicleList() {
      const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/list', {
        needPaging: 'false', vehicleStatus: 'NORMAL', isBind: 'BIND',
      })).body;
      if (code !== '200') throw new Error(message);
      this.vehicleList = respData.rows;
      if (this.vehicleList && this.vehicleList.length && !_.find(this.vehicleList, { id: this.selectedId })) {
        this.selectedId = this.vehicleList[0].id;
      }
      // 初始化车辆、电池、使用人信息
      try {
        // 车辆电池信息
        const { code: veCode, message: veMessage, respData: veRespData } = (await this.$http.post('/api/manager/vehicle/getbypk', {
          id: this.vehicleList[0].id, flag: 'true',
        })).body;
        if (veCode !== '200') throw new Error(veMessage);
        if (veRespData) {
          this.vehicleInfo = veRespData;
          this.powerInfo = veRespData.bizBatteries[0];
        } else {
          this.$message.error('该车辆未绑定电池');
        }
        // 用户信息
        const { code: userCode, message: userMessage, respData: userRespData } = (await this.$http.post('/api/manager/user/getUserByVehicle', [this.vehicleList[0].id])).body;
        if (userCode !== '200') throw new Error(userMessage);
        if (userRespData) this.userInfo = userRespData;
        else this.$message.error('该车辆未与用户绑定');
      } catch (e) {
        const veMessage = e.statusText || e.veMessage;
        this.$message.error(veMessage);

        const userMessage = e.statusText || e.userMessage;
        this.$message.error(userMessage);
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
    // 获取车辆在某一段时间内行驶的轨迹
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
