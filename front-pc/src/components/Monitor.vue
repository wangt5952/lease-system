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
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{selectedItem.RSOC}}%</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">剩余电量</div>
        </div>
        <div @click="showUserDialog" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{ userInfo.userName }}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">使用人</div>
        </div>
      </div>

      <baidu-map class="allmap" @click="handleMapClick" style="width: 100%;flex:1;" :center="mapCenter" :zoom="zoomNum" @moveend="syncCenterAndZooms" @zoomend="syncCenterAndZoom" :scroll-wheel-zoom="true">
        <!-- 圆形图 -->
        <bm-circle :center="circlePath.center" :radius="circlePath.radius" stroke-color="blue" :stroke-opacity="0.5" :stroke-weight="1" @lineupdate="updateCirclePath" :fill-opacity="0.1"></bm-circle>
        <!-- 比列尺 -->
        <bm-scale anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-scale>
        <!-- 右上角控件 -->
        <bm-navigation anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-navigation>
        <!-- 定位控件 -->
        <bm-geolocation anchor="BMAP_ANCHOR_BOTTOM_RIGHT" :showAddressBar="true" :autoLocation="true" @locationSuccess="positionSuccess" @locationError="positionError"></bm-geolocation>
        <template v-if="vehiclePathVisible">
          <!-- 路线折线图 -->
          <bm-polyline v-if="selectedItem.path" :path="selectedItem.path" stroke-color="blue" :stroke-opacity="0.5" :stroke-weight="2"></bm-polyline>
          <!-- 点聚合(图片所在位置) -->
          <bm-marker :icon="{url: '/static/vehicle-cur.svg', size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: selectedItem.LON, lat: selectedItem.LAT}"></bm-marker>
        </template>
        <bm-marker v-else v-for="o in radiusVehicleList" :key="o.vehicleId" :icon="{url: selectedItem.vehicleId == o.vehicleId ? '/static/vehicle-cur.svg' : (`/static/${o.icon || 'vehicle-ok.svg'}`), size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: o.LON, lat: o.LAT}"></bm-marker>
      </baidu-map>

      <!-- Element <DateTimePicker>日期选择器控件 -->
      <el-date-picker v-if="vehiclePathVisible"
        v-model="searchLocList.time"
        type="datetimerange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd HH:mm:ss"
        @change="reloadLocList"
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

    <!-- 右侧车辆列表 -->
    <div style="display:flex;flex-direction:column;width:170px;background:#eff5f8;padding:10px 20px;overflow:auto;">
      <div style="color:#9096ad;font-size:16px;">车辆列表（可用）</div>
      <div style="display:flex;align-items:center;font-size:14px;height:36px;margin-bottom:5px;text-align:center;">
        <div style="flex:1;">车辆编号</div>
        <div style="width:80px;">剩余电量</div>
      </div>
      <div style="flex:1;overflow:scroll;font-size:14px;">
        <div @click="handleSelectItem(o)" v-for="o in radiusVehicleList" :key="o.vehicleId" :style="selectedId == o.vehicleId ? {backgroundColor:'#39a0f6', color:'#fff'} : {backgroundColor:'#e7eef5', color:'#9096ad'}"
          style="display:flex;align-items:center;height:36px;margin-bottom:5px;border-radius:3px;text-align:center;cursor:pointer;">
          <div style="flex:1;">{{o.vehicleCode}}</div>
          <div style="width:80px;">{{o.RSOC}}%</div>
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
        <template v-else-if="userInfo.userRealNameAuthFlag === 'UNAUTHORIZED'">
          <div class="item-value">未实名</div>
        </template>
        <template v-else>
          <div class="item-value"></div>
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
// import BMap from 'BMap';
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
          lng: 0,
          lat: 0,
        },
        radius: 1000,
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
      zoomNum: 15,

      // 指定范围内的车辆集合
      radiusVehicleList: [],
    };
  },
  computed: {
    selectedItem() {
      const vehicleListInfo = (_.find(this.radiusVehicleList, { vehicleId: this.selectedId }) || {});
      return vehicleListInfo;
    },
  },
  watch: {
    'search.keyword'(v) {
      this.mapCenter = v;
    },
    // searchLocList: {
    //   async handler() {
    //     this.reloadLocList();
    //   },
    //   deep: true,
    // },
  },
  methods: {
    // 单击右下角的定位
    positionSuccess() {
    },
    positionError() {
    },
    // 圆形区域
    updateCirclePath(e) {
      this.circlePath.center = e.target.getCenter();
      this.circlePath.radius = e.target.getRadius();
    },

    // 地图更改缩放级别结束时触发触发此事件
    async syncCenterAndZoom(e) {
      // 获取地图中心点
      const { lng, lat } = e.target.getCenter();
      // 或者缩放等级
      const zoomNum = e.target.getZoom();
      let num = 0;
      if (zoomNum > 10) {
        switch (zoomNum) {
          // 地图缩放等级 16 圆圈显示900M  车辆范围取900M
          case 16: this.circlePath.radius = 900; num = 900; break;
          // 地图缩放等级 15 圆圈显示2000M  车辆范围取2000M  <--以下同理-->
          case 15: this.circlePath.radius = 2000; num = 2000; break;
          case 14: this.circlePath.radius = 2000; num = 2000; break;
          case 13: this.circlePath.radius = 4000; num = 4000; break;
          case 12: this.circlePath.radius = 10000; num = 10000; break;
          case 11: this.circlePath.radius = 20000; num = 20000; break;
          default: this.circlePath.radius = 200000; num = 1000;
        }
        try {
          const { code, message } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
            lng: lng, lat: lat, radius: num,
          })).body;
          if (code !== '200') throw new Error(message);
        } catch (err) {
          const message = err.statusText || err.message;
          this.$message.error(message);
        }
      }
    },
    // 地图移动结束时触发此事件
    async syncCenterAndZooms(e) {
      const { lng, lat } = e.target.getCenter();
      this.circlePath.center = e.target.getCenter();
      this.circlePath.radius = 2000;
      try {
        const { code, message } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
          lng: lng, lat: lat, radius: this.circlePath.radius,
        })).body;
        if (code !== '200') throw new Error(message);
      } catch (err) {
        const message = err.statusText || err.message;
        this.$message.error(message);
      }
    },
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

    // 车辆单击事件
    async handleSelectItem(item) {
      this.mapCenter = {
        lng: item.LON, lat: item.LAT,
      };
      this.selectedId = item.vehicleId;

      // 车辆、电池、使用人、根据半径查看车辆 信息
      try {
        // 车辆、电池信息
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getbypk', {
          id: item.vehicleId, flag: 'true',
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

        // 使用人信息 (如果没有用户则不显示用户信息)
        const { code: userCode, message: userMessage, respData: userRespData } = (await this.$http.post('/api/manager/user/getUserByVehicle', [item.vehicleId])).body;
        if (userCode !== '200') throw new Error(userMessage);
        if (userRespData) {
          this.userInfo = userRespData;
        } else {
          this.userInfo = {};
        }

        // 车辆某段时间内行驶路径
        const { time } = this.searchLocList;
        const id = item.vehicleId;
        if (this.vehiclePathVisible) {
          const { respData: timeRespData } = (await this.$http.post('/api/manager/vehicle/gettrackbytime', { id, startTime: time[0], endTime: time[1] })).body;
          // if (code !== '200') throw new Error(message);
          const locList = timeRespData;
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
        }
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);

        const userMessage = e.statusText || e.userMessage;
        this.$message.error(userMessage);

        const radiusMessage = e.statusText || e.radiusMessage;
        this.$message.error(radiusMessage);
      }

      const loc = await this.getLocation(item.LON, item.LAT);
      // console.log(loc);

      this.address = loc.address;
    },

    // 获取所有车辆信息
    async reloadVehicleList() {
      const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/list', {
        needPaging: 'false', vehicleStatus: 'NORMAL', isBind: 'BIND',
      })).body;
      if (code !== '200') throw new Error(message);
      this.vehicleList = respData.rows;

      // 初始化区间范围内车辆、电池、使用人信息
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
        // else this.$message.error('该车辆未与用户绑定');
      } catch (e) {
        const veMessage = e.statusText || e.veMessage;
        this.$message.error(veMessage);

        const userMessage = e.statusText || e.userMessage;
        this.$message.error(userMessage);
      }
      await this.reloadVehicleLoc();

      // 以所有车辆中某辆车的定位信息 为条件 设置 区间范围
      // const { code: radiusCode, message: radiusMessage, respData: radiusRespData } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
      //   lng: this.vehicleList[0].lng, lat: this.vehicleList[0].lat, radius: 1000,
      // })).body;
      try {
        const { code: radiusCode, message: radiusMessage, respData: radiusRespData } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
          lng: 118.74594531868764, lat: 31.973434835225518, radius: 1000,
        })).body;
        if (radiusCode !== '200') throw new Error(radiusMessage);
        this.radiusVehicleList = radiusRespData;
      } catch (e) {
        const radiusMessage = e.statusText || e.radiusMessage;
        this.$message.error(radiusMessage);
      }

      if (this.radiusVehicleList && this.radiusVehicleList.length && !_.find(this.radiusVehicleList, { vehicleId: this.selectedId })) {
        this.selectedId = this.radiusVehicleList[0].vehicleId;
      }
      // 把区间范围内第一个车辆当前坐标设为地图中心点
      this.mapCenter = {
        lng: this.radiusVehicleList[0].LON, lat: this.radiusVehicleList[0].LAT,
      };
      // 给当前车辆设置 图形范围
      this.circlePath.center.lng = this.radiusVehicleList[0].LON;
      this.circlePath.center.lat = this.radiusVehicleList[0].LAT;
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
    // 获取车辆在某一段时间内行驶的轨迹
    async reloadLocList() {
      const { time } = this.searchLocList;
      const id = this.selectedId;
      const param = { id };
      if (time) {
        param.startTime = time[0];
        param.endTime = time[1];
      }
      try {
        const { respData } = (await this.$http.post('/api/manager/vehicle/gettrackbytime', param)).body;
        // if (code !== '200') throw new Error(message);
        const locList = respData;
        if (!locList.length) throw new Error('该时间段没有行驶轨迹');
        this.mapCenter = {
          lng: locList[0].LON, lat: locList[0].LAT,
        };
        this.radiusVehicleList = _.map(this.radiusVehicleList, (o) => {
          if (o.vehicleId !== id) return o;
          return {
            ...o,
            path: _.map(locList, i => ({
              lng: i.LON,
              lat: i.LAT,
            })),
          };
        });
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    // 逆地址解析
    getLocation(lng, lat) {
      return new Promise((resolve, reject) => (new BMap.Geocoder()).getLocation(new BMap.Point(lng, lat), res => resolve(res)));
    },
    // 百度地图定位
    getLocations(lng, lat) {
      return new Promise((resolve, reject) => (new BMap.Geolocation()).getCurrentPosition((r) => {
        if (this.getStatus() === BMAP_STATUS_SUCCESS) {
          var mk = new BMap.Marker(r.point);
          map.addOverlay(mk);
          map.panTo(r.point);
          alert(`您的位置：${r.point.lng} ,${r.point.lat}`);
        }
        else {
          alert('failed' + this.getStatus());
        }
      },{enableHighAccuracy: true}));
    },
  },
  async mounted() {
    const locs = await this.getLocations(118.75761510569, 31.977260789187);
    console.log(locs);
    const loc = await this.getLocation(118.75761510569, 31.977260789187);
    console.log(loc);
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
