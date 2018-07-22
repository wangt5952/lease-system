<template>
<div style="display:flex;">
  <!-- 监控(左边) -->
  <div class="deviceMonStyle">
    <!-- 头部 -->
    <div class="deviceMonStyle-head">
      <!-- 搜索 -->
      <div class="deviceMonStyle-head-search">
        <el-input v-model="content" style="margin-top:5px;width:75%" size="mini" suffix-icon="el-icon-edit" :placeholder="searchContext" clearable></el-input>
        <el-button size="mini" @click="searchStreet(content, view)">搜索</el-button>
        <div style="margin-top:10px">
          <el-radio-group v-model="view" @change="radioEvent(view)">
            <!-- <el-radio class="radio"
              v-model="report"
              :key="i"
              :label="o"
              v-for="(o, i) in radioList">{{o.label}}
            </el-radio> -->
            <el-radio :label="1">按设备查找</el-radio>
            <el-radio :label="2">按范围查找</el-radio>
          </el-radio-group>
        </div>
      </div>
      <!-- 1 -->
      <div @click="deviceDialogVisible = true" class="deviceMonStyle-head-info">
        <div class="info-head">{{ getDeviceItem.deviceId }}</div>
        <div class="info-foot">设备编码</div> 
      </div>
      <!-- 2 -->
      <div @click="devicePathVisible = !devicePathVisible" class="deviceMonStyle-head-info">
        <div class="info-head2">{{ address }}</div>
        <div class="info-foot">设备当前位置</div>
      </div>
      <!-- 3 -->
      <div @click="batteryDialogVisible = true" class="deviceMonStyle-head-info">
        <div class="info-head">{{ getDeviceItem.Rsoc ? `${getDeviceItem.Rsoc}%` : getDeviceItem.Rsoc}} </div>
        <div class="info-foot">设备剩余电量</div>
      </div>
      <!-- 4 -->
      <div @click="vehiclDialogVisible = true" class="deviceMonStyle-head-info">
        <div class="info-head">{{ vehicleInfo.vehicleCode }}</div>
        <div class="info-foot">车辆信息</div>
      </div>
    </div>

    <!-- 设备监控地图 -->
    <baidu-map @ready="handler" class="bMapStyle" :center="mapCenter" :zoom="zoom" @dragend="syncCenterAndZooms" @zoomend="syncCenterAndZoom" :scroll-wheel-zoom="true">
      <!--1 比列尺 -->
      <bm-scale anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-scale>
      <!--2 右上角控件 -->
      <bm-navigation anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-navigation>
      <!--3 定位控件 -->
      <bm-geolocation anchor="BMAP_ANCHOR_BOTTOM_RIGHT" :showAddressBar="true" :autoLocation="true" @locationSuccess="positionSuccess" @locationError="positionError"></bm-geolocation>
      <template v-if="devicePathVisible">
        <!-- 单个点聚合 -->
        <bm-marker @click="" :icon="{url: '/static/vehicle-cur.svg', size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: devicePosition.LON, lat: devicePosition.LAT}"></bm-marker>
        <!-- 路线折线图 -->
        <bm-polyline v-if="devicePaths" :path="devicePaths" stroke-color="blue" :stroke-opacity="0.5" :stroke-weight="2"></bm-polyline>
      </template>
      <!-- 多个点聚合 -->
      <bm-marker @click="" v-else v-for="o in getDeviceList" :key="o.deviceId" :icon="{url: getDeviceItem.deviceId === o.deviceId ? '/static/vehicle-cur.svg' : '/static/vehicle-ok.svg', size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: o.LON, lat: o.LAT}"></bm-marker>
    </baidu-map>

    <!-- Element <DateTimePicker>日期选择器控件 -->
    <el-date-picker v-if="devicePathVisible"
      v-model="searchLocList.time"
      type="datetimerange"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      value-format="yyyy-MM-dd HH:mm:ss"
      @change="reloadLocList"
      size="mini"
      style="position:absolute;top:180px;right:420px;">
    </el-date-picker>
  </div>

  <!-- 设备列表(右边) -->
  <div class="deviceListStyle">
    <div style="color:#9096ad;font-size:16px;">设备列表</div>
    <div class="deviceListInfo">
      <div style="flex:1;">设备编号</div>
      <div style="width:80px;">剩余电量</div>
    </div>
    <div style="flex:1;overflow:scroll;font-size:14px;">
      <div @click="handleSelectItem(o)" v-for="o in getDeviceList" class="clickDeviceEvent"
        :style="deviceId == o.deviceId ? {backgroundColor:'#39a0f6', color:'#fff'} : {backgroundColor:'#e7eef5', color:'#9096ad'}">
        <div style="flex:1;">{{ o.deviceId }}</div>
        <div style="width:80px;">{{ o.Rsoc ? `${o.Rsoc}%`:o.Rsoc }}</div>
      </div>
      {{ getDeviceItem.deviceId }}
      <div v-if="view === 1">
        <el-pagination v-if="total"
          small
          @current-change="loadDevice"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="prev, pager, next"
          :total="total">
        </el-pagination>
      </div>
    </div>
  </div>

  <!-- 电池窗口样式 -->
  <div class="batteryStyle">
    <el-dialog title="电池信息" :visible.sync="batteryDialogVisible" width="30%">
      <div class="item"><div class="item-name">电池编号</div><div class="item-value">{{ powerInfo.batteryCode }}</div></div>
      <div class="item"><div class="item-name">电池货名</div><div class="item-value">{{ powerInfo.batteryName }}</div></div>
      <div class="item"><div class="item-name">电池品牌</div><div class="item-value">{{ powerInfo.batteryBrand }}</div></div>
      <div class="item"><div class="item-name">电池型号</div><div class="item-value">{{ powerInfo.batteryPn }}</div></div>
      <div class="item"><div class="item-name">电池参数</div><div class="item-value">{{ powerInfo.batteryParameters }}</div></div>
      <!-- <div class="item"><div class="item-name">生产商名称</div><div class="item-value">{{ powerInfo.mfrsName }}</div></div> -->
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
  </div>

  <!-- 车辆窗口样式 -->
  <div class="vehicleStyle">
    <el-dialog title="车俩信息" :visible.sync="vehiclDialogVisible" width="30%">
      <div class="item"><div class="item-name">车辆编号</div><div class="item-value">{{ vehicleInfo.vehicleCode }}</div></div>
      <div class="item"><div class="item-name">车辆品牌</div><div class="item-value">{{ vehicleInfo.vehicleBrand }}</div></div>
      <div class="item"><div class="item-name">车辆型号</div><div class="item-value">{{ vehicleInfo.vehiclePn }}</div></div>
      <div class="item"><div class="item-name">车辆产地</div><div class="item-value">{{ vehicleInfo.vehicleMadeIn }}</div></div>
      <div class="item"><div class="item-name">车辆状态</div>
        <template v-if=" vehicleInfo.vehicleStatus === 'NORMAL'">
          <div class="item-value">正常</div>
        </template>
        <template v-if=" vehicleInfo.vehicleStatus === 'FREEZE'">
          <div class="item-value">冻结</div>
        </template>
        <template v-if=" vehicleInfo.vehicleStatus === 'INVALID'">
          <div class="item-value">作废</div>
        </template>
      </div>
    </el-dialog>
  </div>
</div>
</template>

<script>
import moment from "moment";
import _ from "lodash";
export default {
  data() {
    return {
      batteryDialogVisible: false,
      devicePathVisible: false,
      deviceDialogVisible: false,
      vehiclDialogVisible: false,
      // 设备列表
      getDeviceList: [],
      // 设备运行轨迹时间
      searchLocList: {},
      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,
      content: "",
      // 设备ID
      deviceId: "1",
      radioDeviceType: "1",
      // radioList: [
      //   { label: "按设备查找", value: 1 },
      //   { label: "按范围查找", value: 2 }
      // ],
      view: 1,
      report: 1,
      zoom: 15,
      mapCenter: "南京",
      // 地址
      address: "",
      searchAddress: "",
      // 搜索框内容
      searchContext: "请输入设备编号",
      // 电池
      powerInfo: {},
      // 车辆
      vehicleInfo: {},
      // 设备路径
      devicePath: [{}],
      // 设备路径2
      devicePaths: [{}],
      // 获取单独设备的定位信息
      devicePosition: {}
    };
  },
  mounted() {
    this.loadDevice();
  },
  computed: {
    // 根据设备id获取设备对象信息
    getDeviceItem() {
      return _.find(this.getDeviceList, { deviceId: this.deviceId }) || {};
    }
  },
  methods: {
    async handler({ BMap, map }) {
      // 根据经纬度 获取中心点
      const new_point = (lng, lat) => {
        this.zoom = 17;
        const point = new BMap.Point(lng, lat);
        map.panTo(point);
      };

      // const myGeo = new BMap.Geocoder();
      // const thisOne = this;
      // myGeo.getLocation(new BMap.Point(thisOne.center.lng, thisOne.center.lat), function(result){
      //   if (result){
      //     thisOne.address = result.address
      //   }
      // });
      // 浏览器定位
      const getCurPosition = () => {
        return new Promise((resolve, reject) =>
          new BMap.Geolocation().getCurrentPosition(
            function get(r) {
              if (this.getStatus() === BMAP_STATUS_SUCCESS) {
                resolve(r);
              } else {
                reject(this.getStatus());
              }
            },
            { enableHighAccuracy: true }
          )
        );
      };
      const r = await getCurPosition();
      this.mapCenter = {
        lng: r.point.lng,
        lat: r.point.lat
      };

      // 逆地址解析(根据经纬度获取详细地址).
      const getLocation = (lng, lat) => {
        return new Promise(resolve =>
          new BMap.Geocoder().getLocation(new BMap.Point(lng, lat), res =>
            resolve(res)
          )
        );
      };
      // 根据地址获取经纬度
      const getAddressByLocation = address => {
        return new Promise(resolve =>
          new global.BMap.Geocoder().getPoint(
            address,
            res => {
              // 给一个初始化坐标
              new BMap.Point(118.805297, 32.052656);
              resolve(res);
            },
            address
          )
        );
      };
      // 设置全局变量______________
      // 设置地图中心点
      window.new_point = new_point;
      // 根据地址获取经纬度
      window.getAddressByLocation = getAddressByLocation;
      // 逆地址解析(根据经纬度获取详细地址).
      window.getLocation = getLocation;
      // 浏览器定位
      window.getCurPosition = getCurPosition;
    },
    // 地图更改缩放级别结束时触发触发此事件
    async syncCenterAndZoom(e) {
      if (this.devicePathVisible === false) {
        if (this.view === 2) {
          const { lng, lat } = e.target.getCenter();
          // 获取缩放等级
          const zoomNum = e.target.getZoom();
          let num = 0;
          if (zoomNum > 10) {
            switch (zoomNum) {
              // 地图缩放等级 16 圆圈显示900M  车辆范围取900M
              case 16:
                num = 900;
                break;
              // 地图缩放等级 15 圆圈显示2000M  车辆范围取2000M  <--以下同理-->
              case 15:
                num = 2000;
                break;
              case 14:
                num = 2000;
                break;
              case 13:
                num = 4000;
                break;
              case 12:
                num = 10000;
                break;
              case 11:
                num = 20000;
                break;
              default:
                num = 1000;
            }
            try {
              const { code, message, respData } = (await this.$http.post(
                "/api/manager/deviceTrak/getDeviceByLocationAndRadius",
                {
                  lng,
                  lat,
                  radius: num
                }
              )).body;
              if (code === "200") {
                this.getDeviceList = respData;
              } else {
                this.getDeviceList = [];
                this.powerInfo = {};
                this.vehicleInfo = {};
                this.address = "";
                throw new Error(message);
              }
            } catch (err) {
              const message = err.statusText || err.message;
              this.$message.error(message);
            }
          }
        }
      }
    },
    // 停止拖拽地图时触发此事件
    async syncCenterAndZooms(e) {
      if (this.devicePathVisible === false) {
        if (this.view === 2) {
          const { lng, lat } = e.target.getCenter();
          // 逆地址解析(根据经纬度获取详细地址).
          const loc = await getLocation(lng, lat);
          // 赋值给搜索框
          this.content = loc.address;
          // this.circlePath.center = e.target.getCenter();
          // 获取缩放等级
          const zoomNum = e.target.getZoom();
          let num = 0;
          if (zoomNum > 10) {
            switch (zoomNum) {
              // 地图缩放等级 16 圆圈显示900M  车辆范围取900M
              case 16:
                num = 900;
                break;
              // 地图缩放等级 15 圆圈显示2000M  车辆范围取2000M  <--以下同理-->
              case 15:
                num = 2000;
                break;
              case 14:
                num = 2000;
                break;
              case 13:
                num = 4000;
                break;
              case 12:
                num = 10000;
                break;
              case 11:
                num = 20000;
                break;
              default:
                num = 1000;
            }
            try {
              const { code, message, respData } = (await this.$http.post(
                "/api/manager/deviceTrak/getDeviceByLocationAndRadius",
                {
                  lng,
                  lat,
                  radius: num
                }
              )).body;
              if (code === "200") {
                // 获取数据并且赋值
                this.getDeviceList = respData;
              } else {
                // 失败
                this.getDeviceList = [];
                this.powerInfo = {};
                this.vehicleInfo = {};
                this.address = "";
                throw new Error(message);
              }
            } catch (err) {
              const message = err.statusText || err.message;
              this.$message.error(message);
            }
          }
        }
      }
    },
    async openUserInfoVis() {},
    // 右下角定位按钮 (成功)
    async positionSuccess(e) {
      try {
        // 把当前地址的经纬度设为中心点 查询2000米范围之内的设备
        const { code, message, respData } = (await this.$http.post(
          "/api/manager/deviceTrak/getDeviceByLocationAndRadius",
          {
            lng: e.point.lng,
            lat: e.point.lat,
            radius: 2000
          }
        )).body;
        if (code !== "200") {
          // 失败
          this.getDeviceList = [];
          this.powerInfo = {};
          this.vehicleInfo = {};
          this.address = "";
          throw new Error(message);
        } else {
          this.getDeviceList = respData;
        }
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 右下角定位按钮 (失败)
    async positionError() {
      alert("定位失败");
    },
    // 搜索按钮
    async searchStreet(val, view) {
      // 按设备查找
      if (view === 1) this.loadDevice();
      // 按范围查找
      if (view === 2) {
        try {
          // 获取地址的坐标
          const point = await getAddressByLocation(val);
          if (!point) throw new Error("请输入正确地址");
          // 将坐标设备地图中心点
          await new_point(point.lng, point.lat);
          // 逆地址解析
          const loc = await getLocation(point.lng, point.lat);
          // 把值赋给搜索框
          this.content = loc.address;
          // 把当前地址的经纬度设为中心点 查询2000米范围之内的设备
          const { code, message, respData } = (await this.$http.post(
            "/api/manager/deviceTrak/getDeviceByLocationAndRadius",
            {
              lng: point.lng,
              lat: point.lat,
              radius: 2000
            }
          )).body;
          if (code !== "200") {
            // 失败
            this.getDeviceList = [];
            this.powerInfo = {};
            this.vehicleInfo = {};
            this.address = "";
            throw new Error(message);
          } else {
            this.getDeviceList = respData;
          }
        } catch (e) {
          const message = e.statusText || e.message;
          this.$message.error(message);
        }
      }
    },
    // 获取所有设备信息
    async loadDevice() {
      try {
        const { code, message, respData } = (await this.$http.post(
          "/api/manager/device/lists",
          {
            keyStr: this.content,
            needPaging: "true",
            currPage: this.currentPage,
            pageSize: this.pageSize
          }
        )).body;
        if (code === "40106") {
          this.$store.commit("relogin");
          throw new Error("认证超时，请重新登录");
        }
        if (code !== "200") throw new Error(message);
        const { total, rows } = respData;
        this.total = total;
        const getDeviceList = (this.getDeviceList = rows);

        // 设置地图中心点
        // await new_point(getDeviceList[0].LON, getDeviceList[0].LAT);
        // // 获取地理位置
        // const loc = await getLocation(getDeviceList[0].LON, getDeviceList[0].LAT);
        // // 赋值
        // this.address = loc.address;

        // 复用下面或第一个设备的信息 方法
        await this.getBatteryAndVehicleInfo();
        if (
          getDeviceList &&
          getDeviceList.length &&
          !_.find(getDeviceList, { deviceId: this.deviceId })
        ) {
          this.deviceId = getDeviceList[0].deviceId;
        }
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 初始化...  获取 指定范围内的设备
    async reloadVehicleList() {
      // 浏览器定位
      const { lng, lat } = this.mapCenter;
      // 设置地图中心点
      await new_point(lng, lat);
      try {
        const { code, message, respData } = (await this.$http.post(
          "/api/manager/deviceTrak/getDeviceByLocationAndRadius",
          {
            lng,
            lat,
            radius: 2000
          }
        )).body;
        // 成功
        if (code === "200") {
          this.getDeviceList = respData;
        } else {
          // 失败
          this.getDeviceList = [];
          this.powerInfo = {};
          this.vehicleInfo = {};
          this.address = "";
          throw new Error(message);
        }
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 获取第一个设备的信息
    async getBatteryAndVehicleInfo() {
      const { getDeviceList } = this;
      try {
        const { code, message, respData } = (await this.$http.post(
          "/api/manager/device/getRelationInformationByDevice",
          [getDeviceList[0].deviceId]
        )).body;
        if (code !== "200") throw new Error(message);
        const { key_battery_info, key_vehicle_info } = respData;
        this.powerInfo = key_battery_info;
        if (key_vehicle_info) {
          this.vehicleInfo = key_vehicle_info;
        } else {
          this.vehicleInfo = {};
        }
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 范围设备
    async searchScopeDevice() {},
    // 单击设备按钮
    async handleSelectItem(item) {
      this.deviceId = item.deviceId;
      // 如果 页面有时间控件 显示路径
      if (this.devicePathVisible === true) {
        await this.reloadLocList();
      } else {
        // 设备位置(地址)
        try {
          const { code, message, respData } = (await this.$http.post(
            "/api/manager/device/getLocationByDevice",
            {
              deviceId: item.deviceId,
              deviceType: "BATTERY"
            }
          )).body;
          // 先判断 200 状态码
          if (code === "200") {
            // 因为接口的原因 设置一个单独的设备定位信息对象
            const { LON, LAT } = (this.devicePosition = respData);
            if (LON === "" || LAT === "") {
              this.address = "";
              throw new Error("该设备无坐标");
            }
            await new_point(LON, LAT);
            const loc = await getLocation(LON, LAT);
            this.address = loc.address;
          } else {
            throw new Error(message);
          }
        } catch (e) {
          const message = e.statusText || e.message;
          this.$message.error(message);
        }

        // 电池信息 和 车辆信息
        try {
          const { code, message, respData } = (await this.$http.post(
            "/api/manager/device/getRelationInformationByDevice",
            [item.deviceId]
          )).body;
          if (code !== "200") throw new Error(message);
          const { key_battery_info, key_vehicle_info } = respData;
          this.powerInfo = key_battery_info;
          if (key_vehicle_info) {
            this.vehicleInfo = key_vehicle_info;
          } else {
            this.vehicleInfo = {};
          }
        } catch (e) {
          const message = e.statusText || e.message;
          this.$message.error(message);
        }
      }
    },

    // 按照时间区间显示设备运行轨迹
    async reloadLocList() {
      const { time } = this.searchLocList;
      const deviceId = this.deviceId;
      const param = { deviceId };
      try {
        if (time) {
          // 时间转换成毫秒
          param.startTime = moment(time[0]).format("X") * 1000;
          param.endTime = moment(time[1]).format("X") * 1000;
          const { code, message, respData } = (await this.$http.post(
            "/api/manager/deviceTrak/getBypk",
            param
          )).body;
          if (code !== "200") {
            this.devicePaths = [];
            throw new Error(message);
          }
          // let devicePath = this.devicePath = respData.location;
          if (!respData.location.length) {
            // 无轨迹 数组设为空数组
            this.devicePaths = [];
            throw new Error("该时间段没有行驶轨迹");
          }
          let devicePath = (this.devicePath = _.map(this.devicePath, o => {
            return {
              ...o,
              path: _.map(respData.location, i => ({
                lng: i.LON,
                lat: i.LAT
              }))
            };
          }));
          let devicePaths = (this.devicePaths = devicePath[0].path);
          // 设置地图中心点(默认轨迹的起始坐标)
          await new_point(this.devicePaths[0].lng, this.devicePaths[0].lat);
        } else {
          // 没有时间区间 轨迹数组设为空
          this.devicePaths = [];
          throw new Error("请选择时间段");
        }
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 切换radio
    async radioEvent(value) {
      // 获取全部设备
      if (value == 1) {
        // 初始化搜索内容 为空字符串(查询全部)
        this.content = "";
        // 提示输入内容 (placeholder)
        this.searchContext = "请输入设备编号";
        await this.loadDevice();
      }
      // 按范围查找
      if (value == 2) {
        // 提示输入内容 (placeholder)
        this.searchContext = "请输入要查看的地址";
        await this.reloadVehicleList();
        // this.getDeviceList = [];
      }
    }
  }
};
</script>

<style lang="scss">
.deviceMonStyle {
  display: flex;
  flex-direction: column;
  flex: 1;
  &-head {
    display: flex;
    height: 100px;
    background: #070f3e;
    color: #fff;
    &-search {
      flex: 1;
      padding: 10px;
      padding-top: 25px;
    }
    &-info {
      display: flex;
      flex-direction: column;
      width: 150px;
      text-align: center;
      cursor: pointer;
      .info-head {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        margin-top: 20px;
      }
      .info-foot {
        font-size: 12px;
        height: 40px;
        color: #5f7aa7;
      }
      .info-head2 {
        flex: 1;
        display: flex;
        justify-content: center;
        font-size: 12px;
        margin-top: 20px;
      }
    }
  }
  .bMapStyle {
    width: 100%;
    flex: 1;
  }
}
.deviceListStyle {
  display: flex;
  flex-direction: column;
  width: 190px;
  background: #eff5f8;
  padding: 10px 20px;
  overflow: auto;
  .deviceListInfo {
    display: flex;
    align-items: center;
    font-size: 14px;
    height: 36px;
    margin-bottom: 5px;
    text-align: center;
  }
  .clickDeviceEvent {
    display: flex;
    align-items: center;
    height: 36px;
    margin-bottom: 5px;
    border-radius: 3px;
    text-align: center;
    cursor: pointer;
  }
}
.el-radio {
  color: #fff;
}

.batteryStyle,
.vehicleStyle {
  .el-dialog {
    background-color: #eff5f8;
    .el-dialog__body {
      padding-top: 0;
    }
    .el-dialog__title {
      color: #9298ae;
    }
    .el-dialog__close {
      font-family: "lt" !important;
      color: #fa142d;
      &::before {
        content: "\e6b2" !important;
      }
    }
    .item {
      background: #fff;
      height: 32px;
      margin-top: 5px;
      border-radius: 3px;
      display: flex;
      &-name {
        width: 120px;
        text-align: center;
        line-height: 32px;
        color: #858c9c;
      }
      &-value {
        flex: 1;
        line-height: 32px;
      }
    }
  }
}
</style>
