<template>
    <div>
      <div class="head">
        <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
        <div class="tlte"><span>行车轨迹</span></div>
      </div>
      <baidu-map :center="center" :zoom="zoom" @ready="handler" :dragging="true" :pinch-to-zoom="true" class="bm-view">

      </baidu-map>
  </div>
</template>

<script>
export default {
  data() {
    return {
      center: { lng: 0, lat: 0 },
      zoom: 3,
    };
  },
  methods: {
    back() {
      window.history.go(-1);
    },
    handler() {
      this.center.lng = 116.404;
      this.center.lat = 39.915;
      this.zoom = 15;
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
.left {
  margin-top:25px;
  margin-left: 5px;
  color: #fff;
}
.left .iconfont {
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
  height:100%;
}
</style>
