<template>
  <div style="display:flex;position: relative;">
    <div style="display:flex;flex-direction:column;flex:1;">
      <div style="display:flex;height:100px;background:#070f3e;color:#fff;">
        <div style="flex:1;padding:10px;padding-top:25px;">
          <!-- <div style="font-size:12px;">车辆实时情况</div> -->
          <el-input v-model="searchAddress" style="margin-top:5px;width:75%" size="mini" suffix-icon="el-icon-edit" placeholder="请输入要查看的地址" clearable></el-input>
          <el-button size="mini" @click="searchStreet(searchAddress)">搜索</el-button>
          <div style="font-size: 0.8em;margin-top:5px">
          </div>
        </div>
        <div @click="vehicleDialogVisible = true" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{vehicleInfo.vehicleCode}}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆编码</div>
        </div>
        <div @click="vehiclePathVisible = !vehiclePathVisible" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:12px;margin-top:20px;">{{ address }}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆当前位置</div>
        </div>
          <div @click="batteryDialogVisible = true" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{ selectedItem.Rsoc ? selectedItem.Rsoc+"%" : selectedItem.Rsoc }}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">剩余电量</div>
        </div>
        <div @click="userDialogVisible = true;" style="display:flex;flex-direction:column;width:150px;text-align:center;cursor:pointer;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{ userInfo.nickName }}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">使用人</div>
        </div>
      </div>

      <baidu-map @ready="handler" @click="handleMapClick" style="width: 100%;flex:1;" :center="mapCenter" :zoom="zoomNum" @dragend="syncCenterAndZooms" @zoomend="syncCenterAndZoom" :scroll-wheel-zoom="true">
        <!--1 比列尺 -->
        <bm-scale anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-scale>
        <!--2 右上角控件 -->
        <bm-navigation anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-navigation>
        <!--3 定位控件 -->
        <bm-geolocation anchor="BMAP_ANCHOR_BOTTOM_RIGHT" :showAddressBar="true" :autoLocation="true" @locationSuccess="positionSuccess" @locationError="positionError"></bm-geolocation>
        <template v-if="vehiclePathVisible">
          <!-- 路线折线图 -->
          <bm-polyline v-if="selectedItem.path" :path="selectedItem.path" stroke-color="blue" :stroke-opacity="0.5" :stroke-weight="2"></bm-polyline>
          <!-- 点聚合(图片所在位置) -->
          <bm-marker :icon="{url: '/static/vehicle-cur.svg', size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: selectedItem.LON, lat: selectedItem.LAT}"></bm-marker>
        </template>
        <bm-marker @click="clickVehicle(o)" v-else v-for="o in radiusVehicleList" :key="o.vehicleId" :icon="{url: selectedItem.vehicleId == o.vehicleId ? '/static/vehicle-cur.svg' : (`/static/${o.icon || 'vehicle-ok.svg'}`), size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }" :position="{lng: o.LON, lat: o.LAT}"></bm-marker>
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
          <div style="width:80px;">{{ o.Rsoc ? `${o.Rsoc}%`:o.Rsoc }}</div>
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
// import moment from 'moment';
import _ from 'lodash';

export default {
  data() {
    return {
      address: '',
      search: {},
      points: [],
      selectedId: '1',
      searchLocList: {},
      vehicleInfo: {},
      userInfo: {},
      powerInfo: {},
      vehicleDialogVisible: false,
      batteryDialogVisible: false,
      userDialogVisible: false,
      vehiclePathVisible: false,
      mapCenter: '南京',
      zoomNum: 10,
      searchAddress: '',

      // 指定范围内的车辆集合
      radiusVehicleList: [],

    };
  },
  async mounted() {
    // 先让地图加载完，然后调用 methods 里的方法 (异步操作)
    await setTimeout(()=> {
      this.reloadVehicleList();
    }, 300);
  },
  computed: {
    selectedItem() {
      return _.find(this.radiusVehicleList, { vehicleId: this.selectedId }) || {};
    },
  },
  methods: {
    // 地图车辆图标单击事件1
    async clickVehicle(item){
      await this.handleSelectItem(item);
    },
    async handler({BMap, map}) {
      const new_point = (lng, lat) => {
        const point = new BMap.Point(lng, lat);
        map.panTo(point);
      };
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
      // 浏览器定位
      window.getCurPosition = getCurPosition;
      const r = await getCurPosition();
      this.mapCenter = {
        lng: r.point.lng, lat: r.point.lat,
      };

      // 逆地址解析(根据经纬度获取详细地址).
      const getLocation = (lng, lat) => {
        return new Promise(resolve => (new BMap.Geocoder()).getLocation(new BMap.Point(lng, lat), res => resolve(res)));
      };

      // 根据地址获取经纬度
      const getAddressByLocation = (address) => {
        return new Promise(resolve => (new global.BMap.Geocoder()).getPoint(address, (res) => {
          // 给一个初始化坐标
          new BMap.Point(118.805297, 32.052656);
          resolve(res);
        }, address));
      };

      window.new_point = new_point;
      window.getLocation = getLocation;
      // 浏览器定位
      window.getAddressByLocation = getAddressByLocation;
    },
    // 搜索
    async searchStreet(value) {
      try {
        const point = await getAddressByLocation(value);
        if (!point) throw new Error('请输入正确地址');
        this.mapCenter = {
          lng: point.lng, lat: point.lat,
        };
        const loc = await getLocation(point.lng, point.lat);
        this.searchAddress = loc.address;
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
          lng: point.lng, lat: point.lat, radius: 2000,
        })).body;
        if (code === '200') {
          this.radiusVehicleList = respData;
        } else {
          this.radiusVehicleList = [];
          throw new Error(message);
        }
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 单击右下角的定位成功
    async positionSuccess(e) {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
          lng: e.point.lng, lat: e.point.lat, radius: 2 * 1000,
        })).body;
        if (code === '200') {
          this.radiusVehicleList = respData;
        } else {
          this.radiusVehicleList = [];
          this.vehicleInfo = {};
          this.powerInfo = {};
          this.userInfo = {};
          this.address = '';
          throw new Error(message);
        }
      } catch (err) {
        if (!err) return;
        const message = err.statusText || err.message;
        this.$message.error(message);
      }
    },
    async positionError(e) {
      alert('定位失败');
    },
    // 地图更改缩放级别结束时触发触发此事件
    async syncCenterAndZoom(e) {
      // 获取地图中心点
      if (this.vehiclePathVisible === false) {
        const { lng, lat } = e.target.getCenter();
        // this.circlePath.center = e.target.getCenter();
        // 获取缩放等级
        const zoomNum = e.target.getZoom();
        let num = 0;
        if (zoomNum) {
          switch (zoomNum) {
            // 地图缩放等级 18 圆圈显示900M  车辆范围取150M
            case 19: num = 0.15 * 1000; break;
            case 18: num = 0.2 * 1000; break;
            case 17: num = 0.5 * 1000; break;
            case 16: num = 0.9 * 1000; break;
            // 地图缩放等级 15 圆圈显示2000M  车辆范围取2000M  <--以下同理-->
            case 15: num = 2 * 1000; break;
            case 14: num = 3 * 1000; break;
            case 13: num = 6 * 1000; break;
            case 12: num = 15 * 1000; break;
            case 11: num = 30 * 1000; break;
            case 10: num = 50 * 1000; break;
            case 9: num = 50 * 1000; break;
            case 8: num = 60 * 1000; break;
            case 7: num = 100 * 1000; break;
            default: num = 100 * 1000;
          }
          try {
            const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
              lng: lng, lat: lat, radius: num,
            })).body;
            if (code === '200') {
              this.radiusVehicleList = respData;
            } else {
              this.radiusVehicleList = [];
              this.vehicleInfo = {};
              this.powerInfo = {};
              this.address = '';
              throw new Error(message);
            }
          } catch (err) {
            const message = err.statusText || err.message;
            this.$message.error(message);
          }
        }
      } else {
        await this.reloadLocList();
      }
    },
    // 停止拖拽地图时触发此事件
    async syncCenterAndZooms(e) {
      if (this.vehiclePathVisible === false) {
        const { lng, lat } = e.target.getCenter();
        const loc = await getLocation(lng, lat);
        this.searchAddress = loc.address;
        // this.circlePath.center = e.target.getCenter();
        // 获取缩放等级
        const zoomNum = e.target.getZoom();
        let num = 0;
        if (zoomNum) {
          switch (zoomNum) {
            // 地图缩放等级 18 圆圈显示900M  车辆范围取150M
            case 19: num = 0.15 * 1000; break;
            case 18: num = 0.2 * 1000; break;
            case 17: num = 0.5 * 1000; break;
            case 16: num = 0.9 * 1000; break;
            // 地图缩放等级 15 圆圈显示2000M  车辆范围取2000M  <--以下同理-->
            case 15: num = 2 * 1000; break;
            case 14: num = 3 * 1000; break;
            case 13: num = 6 * 1000; break;
            case 12: num = 15 * 1000; break;
            case 11: num = 30 * 1000; break;
            case 10: num = 50 * 1000; break;
            case 9: num = 50 * 1000; break;
            case 8: num = 60 * 1000; break;
            case 7: num = 100 * 1000; break;
            default: num = 100 * 1000;
          }
          try {
            const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
              lng: lng, lat: lat, radius: num,
            })).body;
            if (code === '200') {
              this.radiusVehicleList = respData;
            } else {
              this.radiusVehicleList = [];
              this.vehicleInfo = {};
              this.powerInfo = {};
              this.address = '';
              throw new Error(message);
            }
          } catch (err) {
            const message = err.statusText || err.message;
            this.$message.error(message);
          }
        }
      } else {
        await this.reloadLocList();
      }
    },
    showVehiclePath() {
      this.vehiclePathVisible = !this.vehiclePathVisible;
    },
    handleMapClick() {
    },
    // 右边车辆列表单击事件1
    async handleSelectItem(item) {
      await new_point(item.LON,item.LAT);
      this.selectedId = item.vehicleId;
      const loc = await getLocation(item.LON, item.LAT);
      this.address = loc.address;
      // 车辆、电池、使用人、根据半径查看车辆 信息
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
          lng: item.LON, lat: item.LAT, radius: 50 * 1000,
        })).body;
        //
        const loc = await getLocation(item.LON, item.LAT);
        this.searchAddress = loc.address;
        if (code === '200') {
          this.radiusVehicleList = respData;
        } else {
          this.radiusVehicleList = [];
          this.vehicleInfo = {};
          this.powerInfo = {};
          this.address = '';
          throw new Error(message);
        }
      } catch(e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }

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
        if (userCode !== '200') {
          throw new Error(userMessage);
        }
        this.userInfo = userRespData;
        // 车辆某段时间内行驶路径
        const { time } = this.searchLocList;
        const id = item.vehicleId;
        const param = { id };
        if (time) {
          param.startTime = time[0];
          param.endTime = time[1];
        }
        // 日期显示控件 true false
        if (this.vehiclePathVisible) {
          const { respData: timeRespData } = (await this.$http.post('/api/manager/vehicle/gettrackbytime', param)).body;
          // if (code !== '200') throw new Error(message);
          const locList = timeRespData;
          if (!locList.length) {
            throw new Error('该时间段没有行驶轨迹');
          }
          await new_point(locList[0].LON, locList[0].LAT);
          // this.mapCenter = {
          //   lng: locList[0].LON, lat: locList[0].LAT,
          // };
          this.radiusVehicleList = _.map(this.radiusVehicleList, (o) => {
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
      }
    },
    // 获取所有车辆信息
    async reloadVehicleList() {
      const curPoint = await getCurPosition();
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/listvehiclesbylocandradius', {
          lng: curPoint.point.lng, lat: curPoint.point.lat, radius: 50 * 1000,
        })).body;
        if (code === '200') {
          this.radiusVehicleList = respData;
          // 获取当前范围内第一辆车的信息
          await this.getVehicleInfo();
          // 获取当前范围内第一辆车的用户信息
          await this.getUserInfo();
          if (this.radiusVehicleList && this.radiusVehicleList.length && !_.find(this.radiusVehicleList, { vehicleId: this.selectedId })) {
            this.selectedId = this.radiusVehicleList[0].vehicleId;
          }
          this.mapCenter = {
            lng: this.radiusVehicleList[0].LON, lat: this.radiusVehicleList[0].LAT,
          };
          const loc = await getLocation(this.radiusVehicleList[0].LON, this.radiusVehicleList[0].LAT);
          this.address = loc.address;
        } else {
          this.radiusVehicleList = [];
          this.vehicleInfo = {};
          this.powerInfo = {};
          this.address = '';
          throw new Error(message);
        }
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 车辆电池信息
    async getVehicleInfo() {
      const { radiusVehicleList } = this;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getbypk', {
          id: radiusVehicleList[0].vehicleId, flag: 'true',
        })).body;
        if (code !== '200') throw new Error(message);
        if (respData) {
          this.vehicleInfo = respData;
          this.powerInfo = respData.bizBatteries[0];
        } else {
          this.$message.error('该车辆未绑定电池');
        }
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 用户信息
    async getUserInfo() {
      const { radiusVehicleList } = this;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/user/getUserByVehicle', [radiusVehicleList[0].vehicleId])).body;
        if (code !== '200') {
          this.userInfo = {nickName:'',loginName:'',userMobile:'',userType:'',userRealNameAuthFlag:'',userPid:'',orgName:'',userStatus:''};
          throw new Error(message);
        }
        this.userInfo = respData;
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
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
      // 日期格式 转换成时间戳 moment('').format('X')
      try {
        const { respData } = (await this.$http.post('/api/manager/vehicle/gettrackbytime', param)).body;
        // if (code !== '200') throw new Error(message);
        const locList = respData;
        if (!locList.length) {
          throw new Error('该时间段没有行驶轨迹');
        }
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
  },
};
</script>
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
  font-family: "lt" !important;
  color: #fa142d;
}
>>> .el-dialog .el-dialog__close::before {
  content: "\e6b2" !important;
}
</style>
