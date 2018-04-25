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
            <img src="/static/images/users/1.jpg" class="dr_profile">
          </span>
          <div class="info">
            <p class="name">{{key_user_info.nickName}}</p>
            <a href="/authentication"><p class="realname">{{key_user_info.userRealNameAuthFlag=='AUTHORIZED'?'已实名':'未实名'}}</p></a>
          </div>
        </div>

        <group>
          <cell title="我的车辆" link="/mycar">
            <i slot="icon" class="iconfont icon-chelun"></i>
          </cell>
          <cell title="个人资料" link="/profile">
            <i slot="icon" class="iconfont icon-weibiaoti1"></i>
          </cell>
          <cell title="修改密码" link="/tab3">
            <i slot="icon" class="iconfont icon-icon-"></i>
          </cell>
          <cell title="关联企业" link="/tab4">
            <i slot="icon" class="iconfont icon-qiyetupu"></i>
          </cell>
        </group>
      </div>

      <view-box ref="viewBox">

        <x-header slot="header"
        :left-options="leftOptions"
        title="小哥乐途">
          <span  class="bg-profile" slot="overwrite-left" @click="drawerVisibility = !drawerVisibility">
            <img src="/static/images/users/1.jpg" class="profile">
          </span>
          <a slot="right" href="/track"><i slot="icon" class="iconfont icon-guiji"></i></a>
        </x-header>

        <baidu-map @ready="handler" :center="mapCenter" :zoom="zoomNum" :dragging="true" :pinch-to-zoom="true" class="bm-view">
          <bm-navigation anchor="BMAP_ANCHOR_TOP_RIGHT" :showZoomInfo="true"></bm-navigation>
          <bm-marker :position="Center" :dragging="true" animation="BMAP_ANIMATION_BOUNCE" :icon="{url: '/static/images/vehicle-cur.svg', size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} }}"></bm-marker>
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
import { Group, Cell, Drawer, ViewBox, XHeader } from 'vux';
import { mapState } from 'vuex';

export default {
  components: {
    Group,
    Cell,
    Drawer,
    ViewBox,
    XHeader,
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
    }),
  },
  data() {
    return {
      showMenu: false,
      drawerVisibility: false,
      showMode: 'push',
      showModeValue: 'push',
      showPlacement: 'left',
      showPlacementValue: 'left',
      mapCenter: '北京',
      Center: { lng: 0, lat: 0 },
      zoomNum: 10,
      vehicleId: [],
    };
  },
  methods: {
    handler() {
      return new Promise(() => (new BMap.Geolocation()).getCurrentPosition((r) => {
        if (r.point) {
          this.mapCenter = r.point;
        }
      }, { enableHighAccuracy: true }));
    },
    location() {
      this.mapCenter = { lng: this.Center.lng, lat: this.Center.lat };
      this.zoomNum = 15;
    },
  },
  async mounted() {
    this.vehicleId.push(localStorage.getItem('vehicleId'));
    const { code, message, respData } = (await this.$http.post('/api/mobile/v1/device/getlocbyvehiclepk', this.vehicleId)).body;
    if (code !== '200') throw new Error(message || code);
    this.Center.lng = respData[0].LON;
    this.Center.lat = respData[0].LAT;
  },
};
</script>

<style lang="less">

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

.weui-cells {
  margin:0!important;
}

.weui-cell {
  height:50px;
}

.vux-label {
  font-size: 15pt;
}

.weui-cell__hd {
  margin-right: 10px;
}

.weui-cell__hd .iconfont {
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
