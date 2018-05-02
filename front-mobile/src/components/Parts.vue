<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>车辆配件信息</span></div>
    </div>

    <div class="bg-parts" v-for="(o,index) in list" :key="o.id">
      <group :title="p_type[index]">
        <cell title="配件编号" :value="o.partsCode"></cell>
        <cell title="配件货名" :value="o.partsName"></cell>
        <cell title="配件品牌" :value="o.partsBrand"></cell>
        <cell title="配件型号" :value="o.partsPn"></cell>
        <cell title="配件参数" :value="o.partsParameters"></cell>
        <cell title="生产商家" :value="o.mfrsName"></cell>
        <cell title="配件状态" :value="p_status.value"></cell>
      </group>
    </div>

  </div>
</template>

<script>
import { Cell, Group } from 'vux';
import _ from 'lodash';

const parts_type = [
  {
    key: '',
    value: '全部配件',
  }, {
    key: 'SEATS',
    value: '车座',
  }, {
    key: 'FRAME',
    value: '车架',
  }, {
    key: 'HANDLEBAR',
    value: '车把',
  }, {
    key: 'BELL',
    value: '车铃',
  }, {
    key: 'TYRE',
    value: '轮胎',
  }, {
    key: 'PEDAL',
    value: '脚蹬',
  }, {
    key: 'DASHBOARD',
    value: '仪表盘',
  },
];
const parts_status = [
  {
    key: 'NORMAL',
    value: '正常',
  }, {
    key: 'FREEZE',
    value: '冻结/维保',
  }, {
    key: 'INVALID',
    value: '作废',
  },
];
export default {
  components: {
    Group,
    Cell,
  },
  data() {
    return {
      list: [],
      p_type: [],
      p_status: '',
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
    for (let i = 0; i < respData.length; i += 1) {
      const item = respData[i];
      const obj = _.find(parts_type, { key: item.partsType });
      if (obj) {
        this.p_type.push(obj.value);
      }
    }
    this.p_status = _.find(parts_status, { key: this.list[0].partsStatus });
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
