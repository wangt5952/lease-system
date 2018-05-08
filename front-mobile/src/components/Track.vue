<template>
    <div>
      <view-box ref="viewBox">
        <div class="head">
          <div class="back"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
          <div class="tlte"><span>行车轨迹</span></div>
        </div>

        <div class="time">
          <div class="dian"></div>
          <div class="start" @click="select(1)"><span>起始时间</span><span>{{val1}}</span></div>
          <div class="dx"></div>
          <div class="end" @click="select(2)"><span>终止时间</span><span>{{val2}}</span></div>
        </div>

        <div v-transfer-dom>
          <x-dialog v-model="show">
          <div class="butn">
            <div class="left" @click="close">取消</div>
            <div class="right" @click="confirm">确认</div>
          </div>
          <div class="bt">
            <span class="year">年</span>
            <span class="mouth">月</span>
            <span class="day">日</span>
            <span class="hour">时</span>
            <span class="minute">分</span>
          </div>
          <datetime-view v-model="val" ref="datetime" :format="format"></datetime-view>
          </x-dialog>
        </div>

        <mt-popup v-model="popShow" popup-transition="popup-fade">
          <x-button @click.native="handler" type="primary">确认</x-button>
        </mt-popup>

        <baidu-map @ready="ready" :center="center" :zoom="zoom" :dragging="true" :pinch-to-zoom="true" class="bm-view">
          <bm-polyline :path="polylinePath" stroke-color="blue" :stroke-opacity="0.5" :stroke-weight="2"></bm-polyline>
          <bm-marker :position="center" :dragging="true" animation="BMAP_ANIMATION_BOUNCE"></bm-marker>
        </baidu-map>
      </view-box>
  </div>
</template>

<script>
import { Cell, ViewBox, XDialog, XButton, DatetimeView, Popover, TransferDom } from 'vux';
import moment from 'moment';
import _ from 'lodash';

export default {
  directives: {
    TransferDom,
  },
  components: {
    Cell,
    ViewBox,
    XDialog,
    XButton,
    Popover,
    DatetimeView,
  },
  data() {
    return {
      center: { lng: 116.404, lat: 39.915 },
      zoom: 15,
      show: false,
      popShow: false,
      val: moment().format('YYYY-MM-DD h:mm'),
      val1: '',
      val2: '',
      index: 0,
      format: 'YYYY-MM-DD HH:mm',
      localID: '',
      polylinePath: [],
    };
  },
  methods: {
    back() {
      this.$router.replace('/');
    },
    select(index) {
      this.index = index;
      this.show = true;
    },
    close() {
      this.show = false;
    },
    confirm() {
      if (this.index === 1) {
        this.val1 = this.val;
        this.show = false;
      } else if (this.index === 2) {
        this.val2 = this.val;
        this.show = false;
      }
      if (this.val1 && this.val2) {
        this.popShow = true;
      }
    },
    async ready() {
      const r = await this.getCurrentPosition();
      this.center = r.point;
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
    async handler() {
      if (this.localID === '') {
        this.$vux.toast.show({ text: '实名认证并从企业申领车辆后才能使用本功能', type: 'warn', width: '10em' });
      } else {
        const start = `${this.val1}:00`;
        const end = `${this.val2}:00`;
        const { code, message, respData } = (await this.$http.post('/api/mobile/v1/device/gettrackbytime',
          { id: this.localID, startTime: start, endTime: end })).body;
        if (code !== '200') throw new Error(message || code);
        if (respData.length !== 0) {
          this.center.lng = respData[0].LON;
          this.center.lat = respData[0].LAT;
          this.polylinePath = _.map(respData, o => ({ lng: o.LON, lat: o.LAT }));
          this.zoom = 16;
        } else {
          this.$vux.toast.show({ text: '当前时间段没有车辆轨迹！', type: 'warn', width: '10em' });
        }
      }
    },
  },
  async mounted() {
    if (localStorage.getItem('vehicleId') !== '') this.localID = localStorage.getItem('vehicleId');
  },
};
</script>

<style scoped>
.head {
  width:100%;
  height:65px;
  background: -webkit-linear-gradient(#16D0A2,#20C987)!important;
  background: -o-linear-gradient(#16D0A2,#20C987)!important;
  background: -moz-linear-gradient(#16D0A2,#20C987)!important;
  background: linear-gradient(#16D0A2,#20C987)!important;
  display: flex;
  justify-content: space-between;
  margin: 0 auto;
}
.back {
  margin-top:25px;
  margin-left: 5px;
  color: #fff;
}
.back .iconfont {
  font-size: 17pt;
}
.tlte span{
  font-size: 20px;
  font-weight: 400;
  color: #fff;
  position: absolute;
  left:40%;
  top:25px;
}
.bm-view {
  width:100%;
  height:800px;
}
.time {
  width:90%;
  height:80px;
  background-color: #fff;
  position: absolute;
  left:12.5px;
  top:75px;
  border: 1px solid #D6E7E0;
  opacity: 100%;
  border-radius: 10px;
  z-index: 1;
}
.dian {
  width:4px;
  height:50px;
  position: absolute;
  left:14.5px;
  top:15px;
  background-image: url('/static/images/dian.jpg');
}
.start {
  width:90%;
  height:30px;
  margin-left:20px;
  margin-top: 7px;
}
.end {
  width:90%;
  height: 30px;
  margin-left: 20px;
  margin-top: 10px;
}
.start span,.end span {
  margin:8px 8px;
  font-size: 15px;
  color: #999;
}
.dx {
  width:85%;
  margin-left: 8%;
  border-bottom:1px solid #999;
}
.butn {
  width:100%;
  height:44px;
  display: flex;
  justify-content: space-between;
  background-color: #fbf9fe;
}
.left,.right {
  width:20%;
  height: 30px;
  margin-top: 7px;
  font-size: 15px;
}
.left {
  color: #828282;
}
.right {
  color: #FF9900;
}
.bt {
  width:100%;
  height:20px;
  display: flex;
  justify-content: space-between;
}
.bt span {
  font-size: 15px;
}
.year {
  margin-left: 25px;
}
.mouth {
  margin-left: -5px;
}
.minute {
  margin-right: 20px;
}
.mint-popup {
  width: 80%;
  height:42px;
  left:10%;
  top:18%;
  border-radius: 5px;
  background-color: #fff;
}
</style>
