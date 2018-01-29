<template>
  <div style="display:flex;">
    <div style="display:flex;flex-direction:column;flex:1;">
      <div style="display:flex;height:100px;background:#070f3e;color:#fff;">
        <div style="flex:1;padding:10px;padding-top:25px;">
          <div style="font-size:12px;">车辆实时情况</div>
          <el-input style="margin-top:5px;" size="mini" suffix-icon="el-icon-search" />
        </div>
        <div style="display:flex;flex-direction:column;width:150px;text-align:center;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{selectedItem.code}}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆编码</div>
        </div>
        <div style="display:flex;flex-direction:column;width:150px;text-align:center;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:12px;margin-top:20px;">南京市雨花台区大数据产业园</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">车辆当前位置</div>
        </div>
        <div style="display:flex;flex-direction:column;width:150px;text-align:center;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">{{selectedItem.value}}</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">剩余电量</div>
        </div>
        <div style="display:flex;flex-direction:column;width:150px;text-align:center;">
          <div style="flex:1;display:flex;align-items:center;justify-content:center;font-size:16px;margin-top:20px;">张三</div>
          <div style="font-size:12px;height:40px;color:#5f7aa7;">使用人</div>
        </div>
      </div>
      <baidu-map style="width: 100%;flex:1;" center="北京" @moveend="syncCenterAndZoom" @zoomend="syncCenterAndZoom">
        <bm-marker v-for="o in points" :key="`${[o.lng,o.lat].join(',')}`" :position="{lng: o.lng, lat: o.lat}"></bm-marker>
      </baidu-map>
      <div style="display:flex;background:#eff5f8;height:180px;padding:10px 0;">
        <div style="flex:1;background:#fff;margin-left:10px;">可用车辆</div>
        <div style="flex:1;background:#fff;margin-left:10px;">待修车辆</div>
        <div style="flex:1;background:#fff;margin-left:10px;">已用车辆</div>
        <div style="flex:1;background:#fff;margin-left:10px;">平台车辆</div>
      </div>
    </div>
    <div style="display:flex;flex-direction:column;width:250px;background:#eff5f8;padding:10px 20px;">
      <div style="color:#9096ad;font-size:16px;">车辆列表（可用）</div>

      <div style="display:flex;align-items:center;font-size:14px;height:36px;margin-bottom:5px;text-align:center;">
        <div style="flex:1;">车辆编号</div>
        <div style="width:80px;">剩余电量</div>
      </div>
      <div style="flex:1;overflow:scroll;font-size:14px;">
        <div @click="selectedId = o.id" v-for="o in list" :key="o.id" :style="selectedId == o.id ? {backgroundColor:'#39a0f6', color:'#fff'} : {backgroundColor:'#e7eef5', color:'#9096ad'}" style="display:flex;align-items:center;height:36px;margin-bottom:5px;border-radius:3px;text-align:center;cursor:pointer;">
          <div style="flex:1;">{{o.code}}</div>
          <div style="width:80px;">{{o.value}}</div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import _ from 'lodash';

export default {
  data() {
    return {
      msg: 'Welcome to Your Vue.js App',
      points: [],
      selectedId: '1',
      list: [
        { id: '1', code: 'aima001', value: '60%' },
        { id: '2', code: 'aima002', value: '60%' },
        { id: '3', code: 'aima003', value: '60%' },
        { id: '4', code: 'aima004', value: '60%' },
        { id: '5', code: 'aima005', value: '60%' },
        { id: '6', code: 'aima006', value: '60%' },
        { id: '7', code: 'aima007', value: '60%' },
        { id: '8', code: 'aima008', value: '60%' },
        { id: '9', code: 'aima009', value: '60%' },
        { id: '10', code: 'aima010', value: '60%' },
        { id: '11', code: 'aima011', value: '60%' },
        { id: '12', code: 'aima012', value: '60%' },
        { id: '13', code: 'aima013', value: '60%' },
        { id: '14', code: 'aima014', value: '60%' },
        { id: '15', code: 'aima015', value: '60%' },
        { id: '16', code: 'aima016', value: '60%' },
        { id: '17', code: 'aima017', value: '60%' },
        { id: '18', code: 'aima018', value: '60%' },
        { id: '19', code: 'aima019', value: '60%' },
        { id: '20', code: 'aima020', value: '60%' },
      ],
    };
  },
  computed: {
    selectedItem() {
      return _.find(this.list, { id: this.selectedId }) || {};
    },
  },
  methods: {
    syncCenterAndZoom({ target }) {
      const { lng, lat } = target.getCenter();
      const zoom = target.getZoom();
      console.log(lng, lat, zoom);

      const sLng = (parseInt(lng * 4, 10) / 4) - 0.5;
      const eLng = (parseInt(lng * 4, 10) / 4) + 0.5;
      const sLat = (parseInt(lat * 4, 10) / 4) - 0.5;
      const eLat = (parseInt(lat * 4, 10) / 4) + 0.5;

      const points = [];
      for (let x = sLng; x <= eLng; x += 0.1) {
        for (let y = sLat; y <= eLat; y += 0.1) {
          points.push({ lng: x, lat: y });
        }
      }
      console.log(points);
      this.points = points;
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.map {
  width: 100%;
  height: 500px;
}
</style>
