<template>
  <div style="padding:10px;">
    <baidu-map class="map" center="北京" @moveend="syncCenterAndZoom" @zoomend="syncCenterAndZoom">
      <bm-marker v-for="o in points" :key="`${[o.lng,o.lat].join(',')}`" :position="{lng: o.lng, lat: o.lat}"></bm-marker>
    </baidu-map>
  </div>
</template>

<script>
export default {
  data() {
    return {
      msg: 'Welcome to Your Vue.js App',
      points: [],
    };
  },
  methods: {
    syncCenterAndZoom( { target }) {

      const { lng, lat } = target.getCenter();
      const zoom = target.getZoom();
      console.log(lng, lat, zoom);

      const sLng = parseInt(lng * 4)/4 - 0.5, eLng = parseInt(lng * 4)/4 + 0.5, sLat = parseInt(lat * 4) / 4 - 0.5, eLat = parseInt(lat * 4) / 4 + 0.5;

      const points = [];
      for(let x = sLng; x <= eLng; x += 0.1){
        for(let y = sLat; y <= eLat; y += 0.1){
          points.push({lng: x, lat: y});
        }
      }
      console.log(points);
      this.points = points;
    },

    fetch
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.map {
  width: 500px;
  height: 500px;
}
</style>
