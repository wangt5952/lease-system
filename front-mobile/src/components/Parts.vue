<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>车辆配件信息</span></div>
    </div>

    <div class="bg-parts" v-for="o in list">
      <group :title="o.partsType">
        <cell title="配件编号" :value="o.partsCode"></cell>
        <cell title="配件货名" :value="o.partsName"></cell>
        <cell title="配件品牌" :value="o.partsBrand"></cell>
        <cell title="配件型号" :value="o.partsPn"></cell>
        <cell title="配件参数" :value="o.partsParameters"></cell>
        <cell title="生产商家" :value="o.mfrsName"></cell>
        <cell title="配件状态" :value="o.partsStatus"></cell>
      </group>
    </div>

  </div>
</template>

<script>
import { Cell, Group, CellFormPreview, } from 'vux';

export default {
  components: {
    Group,
    Cell,
    CellFormPreview,
  },
  data() {
    return {
      list: [],
    };
  },
  methods: {
    back() {
      window.history.go(-1);
    },
  },
  async mounted() {
    const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getbypr', [this.$route.params.id])).body;
    if (code !== '200') throw new Error(message || code);
    this.list = respData;
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
    z-index: -1;
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
    left:35%;
    top:25px;
  }
  .bg-parts {
    width:90%;
    margin:0 auto;
  }
</style>
