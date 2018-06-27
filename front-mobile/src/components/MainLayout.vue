<template>
  <div style="height:100%;">
    <drawer
    width="200px;"
    :show.sync="drawerVisibility"
    :show-mode="showModeValue"
    :placement="showPlacementValue"
    >
      <div slot="drawer">
        <div class="head">
          <span class="bg-dr_profile">
            <img :src="this.portrait" class="dr_profile">
          </span>
          <div class="info">
            <p class="name">{{key_user_info.nickName}}</p>
            <a :href="key_user_info.userRealNameAuthFlag=='AUTHORIZED'?'javascript:;':'/authentication_step1'" @click="getPath"><p class="realname">{{key_user_info.userRealNameAuthFlag=='AUTHORIZED'?'已实名':'未实名'}}</p></a>
          </div>
        </div>

        <div class="layout">
          <group>
            <cell title="我的车辆" link="/mycar">
              <i slot="icon" class="iconfont icon-chelun"></i>
            </cell>
            <cell title="个人资料" link="/profile">
              <i slot="icon" class="iconfont icon-weibiaoti1"></i>
            </cell>
            <cell title="修改密码" link="/repassword">
              <i slot="icon" class="iconfont icon-icon-"></i>
            </cell>
            <cell title="登出" @click.native="loginOut" is-link>
              <i slot="icon" class="iconfont icon-tuichu"></i>
            </cell>
          </group>
        </div>
      </div>

      <view-box ref="viewBox">
        <x-header slot="header"
        :left-options="leftOptions"
        title="小哥乐途">
          <span  class="bg-profile" slot="overwrite-left" @click="drawerVisibility = !drawerVisibility">
            <img :src="this.portrait" class="profile">
          </span>
          <a slot="right" href="/track"><i slot="icon" class="iconfont icon-guiji"></i></a>
        </x-header>

        <baidu-map @ready="handler" :center="mapCenter" :zoom="zoomNum" :dragging="true" :pinch-to-zoom="true" class="bm-view">
          <bm-navigation anchor="BMAP_ANCHOR_TOP_RIGHT" :showZoomInfo="true"></bm-navigation>
          <bm-marker :position="Center" :dragging="false" animation="BMAP_ANIMATION_BOUNCE" @click="vehicleBatterInfo(vehicleID)" :icon="this.icon"></bm-marker>
          <bm-info-window :position="PopCenter" :title="infoWindow.title" :show="infoWindow.show" @close="infoWindow.show = false" :width="this.width" :height="this.height">
            <p v-text="infoWindow.contents"></p>
          </bm-info-window>
        </baidu-map>
        <a class="btn" @click="location" href="javascript:;">
          <p><i slot="icon" class="iconfont icon-motuoche"></i></p>
          <span>车辆信息</span>
        </a>
        <span class="bg-btn"></span>
      </view-box>
    </drawer>

  </div>
</template>

<script>
import { Group, Cell, Drawer, ViewBox, XHeader, Loading } from 'vux';
import { mapState } from 'vuex';
import _ from 'lodash';

export default {
  components: {
    Group,
    Cell,
    Drawer,
    ViewBox,
    XHeader,
    Loading,
  },
  computed: {
    leftOptions() {
      return {
        showback: false,
      };
    },
    ...mapState({
      key_user_info: state => state.key_user_info,

      relogin: state => state.relogin,
      // 车辆ID
      vehicleID: state => state.vehicleID,
    }),
  },
  data() {
    return {
      infoWindow: {
        show: false,
        contents: '',
        title: '<span><i slot="icon" class="iconfont icon-electricquantity2dianchidianliang"></i></span>',
      },
      drawerVisibility: false,
      showMode: 'push',
      showModeValue: 'push',
      showPlacement: 'left',
      showPlacementValue: 'left',
      mapCenter: { lng: 116.404, lat: 39.915 },
      Center: { lng: 0, lat: 0 },
      PopCenter: { lng: 0, lat: 0 },
      zoomNum: 18,
      vehicleId: [],
      portrait: '',
      website: 'http://106.14.172.38:8990/leaseupload/usericon/',
      icon: { url: '/static/images/Red_Point.jpg', size: { width: 19, height: 25 }, opts: { imageSize: { width: 19, height: 25 } } },
      width: 0,
      height: 0,
    };
  },
  methods: {
    getPath() {
      if (this.key_user_info.userRealNameAuthFlag === 'AUTHORIZED') {
        this.$vux.toast.show({ text: '您已经实名认证，请勿重复提交', type: 'cancel', width: '10em' });
      }
    },
    async vehicleBatterInfo(vehicleID) {
      this.infoWindow.show = true;
      const { code, message, respData } = (await this.$http.post('/api/mobile/v1/device/getpowerbyvehiclepk', [vehicleID])).body;
      if (code !== '200') this.$vux.toast.show({ text: message, type: 'cancel', width: '10em' });
      this.infoWindow.contents = `剩余电量：${respData[0].RSOC}%`;
    },
    async handler() {
      const r = await this.getCurrentPosition();
      this.mapCenter.lng = r.point.lng;
      this.mapCenter.lat = r.point.lat;
      this.Center.lng = r.point.lng;
      this.Center.lat = r.point.lat;
    },
    getCurrentPosition() {
      this.$vux.loading.show({ text: 'Loading' });
      const thisOne = this;
      return new Promise((resolve, reject) => (new global.BMap.Geolocation()).getCurrentPosition(function get(r) {
        if (this.getStatus() === global.BMAP_STATUS_SUCCESS) {
          setTimeout(() => { thisOne.$vux.loading.hide(); }, 1000);
          resolve(r);
        } else {
          reject(this.getStatus());
        }
      }, { enableHighAccuracy: true }));
    },
    async location() {
      if (this.vehicleId.length === 0) {
        this.$vux.toast.show({ text: '实名认证并从企业申领车辆后才能使用本功能', type: 'warn', width: '10em' });
      } else {
        const { code, respData } = (await this.$http.post('/api/mobile/v1/device/getlocbyvehiclepk', this.vehicleId)).body;
        if (code !== '200') this.$vux.toast.show({ text: '未查询到本车辆的定位信息', type: 'cancel', width: '10em' });
        const v = _.find(respData, o => o.LON && o.LAT);
        this.Center = { lng: v.LON, lat: v.LAT };
        this.PopCenter = { lng: v.LON, lat: v.LAT };
        this.icon = { url: '/static/images/vehicle-cur.svg', size: { width: 48, height: 48 }, opts: { imageSize: { width: 48, height: 48 } } };
        this.mapCenter = { lng: this.Center.lng, lat: this.Center.lat };
        this.zoomNum = 18;
      }
    },
    async loginOut() {
      await this.$store.commit('logout');
      this.$router.replace('/login');
    },
  },
  async mounted() {
    // 静态文件
    if (!this.key_user_info.userIcon) this.portrait = '/static/images/users/1.jpg';
    else this.portrait = this.key_user_info.userIcon.includes(this.website) ? this.key_user_info.userIcon : `${this.website}${this.key_user_info.userIcon}`;
    if (localStorage.getItem('vehicleId') !== '') this.vehicleId.push(localStorage.getItem('vehicleId'));
  },
};
</script>

<style lang="less">

.batter_info {
  width:60%;
  background: #000000 red;
  position: absolute;
  top: 7rem;
  left:25%;
  box-shadow: 0 2px 5px;
  border-radius: 10px;
  z-index: 1;
}
.vux-header {
  width:100%;
  height: 85px;
  background: -webkit-linear-gradient(#16D0A2,#20C987)!important;
  background: -o-linear-gradient(#16D0A2,#20C987)!important;
  background: -moz-linear-gradient(#16D0A2,#20C987)!important;
  background: linear-gradient(#16D0A2,#20C987)!important;
  position:absolute;
  left:0;
  top:0;
  z-index:100;
}

.vux-header-right a{
  margin:28px 8px!important;
}

.vux-header-right a .iconfont{
  font-size: 20pt;
  color: black;
}

.bg-profile {
  width: 50px;
  height:50px;
  position: absolute;
  bottom:-65px;
  background-color: white;
  border-radius: 100%;
}

.profile {
  width:45px;
  height: 45px;
  margin:2.5px 2.5px;
  border-radius: 100%;
}

.vux-header-title {
  height:100%!important;
}

.vux-header-title span{
  margin-top:28px;
}

.head {
  width:100%;
  height:135px;
  background: -webkit-linear-gradient(#16D0A2,#20C987)!important;
  background: -o-linear-gradient(#16D0A2,#20C987)!important;
  background: -moz-linear-gradient(#16D0A2,#20C987)!important;
  background: linear-gradient(#16D0A2,#20C987)!important;
  display: flex;
  justify-content: space-between;
  margin: 0 auto;
  z-index: -1;
}

.bg-dr_profile {
  width:65px;
  height:65px;
  position: absolute;
  top: 55px;
  left: 10px;
  background-color: white;
  border-radius: 100%;
}

.dr_profile {
  width:60px;
  height:60px;
  margin: 2.5px 2.5px;
  border-radius: 100%;
}

.layout .weui-cells {
  margin:0!important;
}

.layout .weui-cells .weui-cell {
  height:50px;
}

.layout .vux-label {
  font-size: 15pt;
}

.layout .weui-cell__hd {
  margin-right: 10px;
}

.layout .weui-cell__hd .iconfont {
  font-size: 25px;
}

.vux-drawer {
  overflow: hidden;
}

.vux-drawer > .vux-drawer-active {
  width:200px;
  background: white!important;
}

.vux-drawer > .drawer-left {
  background: white!important;
}

.bg-btn {
  width:100%;
  height:0;
  padding-bottom: 50%;
  background: #fff;
  position: absolute;
  left:0;
  bottom: 0;
  z-index: 2;
  border-radius:100% 100% 0 0;
}

.btn {
  --AllWidht:100%;
  width:120px;
  height:120px;
  position: absolute;
  left:34%;
  bottom: 18%;
  background: #16D0A1;
  opacity: 100%;
  box-shadow: 0 2px 9px;
  border-radius: 100%;
  z-index: 3;
}

.btn span {
  font-size: 18px;
  font-weight: 400;
  color: #fff;
  position: absolute;
  left:24px;
  bottom: 30px;
}

.btn .iconfont {
  margin-left: 33.5px;
  color: #fff;
  font-size: 40pt;
}

.bm-view {
  width:100%;
  height:100%;
}

.info {
  position: absolute;
  left:90px;
  top:60px;
}

.info .name {
  margin-left: 5px;
  font-size: 15px;
  font-weight: bold;
  color: #fff;
}

.info .realname {
  width:80px;
  background-color: #666;
  font-size: 15px;
  font-weight: 400;
  color: #fff;
  border-radius: 10px;
  text-align: center;
}
</style>
