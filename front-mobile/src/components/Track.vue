<template>
    <div>
      <view-box ref="viewBox">
        <div class="head">
          <div class="back"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
          <div class="tlte"><span>行车轨迹</span></div>
        </div>

        <div class="time">
          <div class="dian"></div>
          <div class="start" @click="select"><span>起始时间</span><span id="val1"></span></div>
          <div class="dx"></div>
          <div class="end" @click="select"><span>终止时间</span><span id="val2"></span></div>
        </div>

        <div v-transfer-dom>
          <x-dialog v-model="show">
          <div class="btn">
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

        <baidu-map :center="center" :zoom="zoom" :dragging="true" :pinch-to-zoom="true" class="bm-view">
        </baidu-map>
      </view-box>
  </div>
</template>

<script>
import { Cell, ViewBox, XDialog, DatetimeView, TransferDom } from 'vux';

export default {
  directives: {
    TransferDom,
  },
  components: {
    Cell,
    ViewBox,
    XDialog,
    DatetimeView,
  },
  data() {
    return {
      center: { lng: 116.404, lat: 39.915 },
      zoom: 15,
      show: false,
      val: '2017-01-01',
      format: 'YYYY-MM-DD HH:mm',
    };
  },
  methods: {
    back() {
      window.history.go(-1);
    },
    select() {
      this.show = true;
    },
    close() {
      this.show = false;
    },
    confirm() {

    },
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
  width:350px;
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
.btn {
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
</style>
