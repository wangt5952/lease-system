<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>我的车辆</span></div>
    </div>
    <div class="vehicle_info">
      <group title="车辆信息">
        <cell title="车辆编号" :value="list.vehicleCode" ></cell>
        <cell title="车辆型号" :value="list.vehiclePn"></cell>
        <cell title="车辆品牌" :value="list.vehicleBrand"></cell>
        <cell title="车辆产地" :value="list.vehicleMadeIn"></cell>
        <cell title="车辆生产商" :value="list.mfrsName"></cell>
        <cell title="车辆状态" :value="v_status.value"></cell>
        <cell title="" value="" class="kong"></cell>
      </group>
    </div>
    <div class="left1-huan"></div>
    <div class="left2-huan"></div>
    <div class="right1-huan"></div>
    <div class="right2-huan"></div>
    <div class="battery_info">
      <group title="电池信息" v-for="o in batteryList" :key="o.id">
        <cell title="电池编号" :value="o.batteryCode" ></cell>
        <cell title="电池类型" :value="o.batteryName"></cell>
        <cell title="电池品牌" :value="o.batteryBrand"></cell>
        <cell title="电池型号" :value="o.batteryPn"></cell>
        <cell title="参数" :value="o.batteryParameters"></cell>
        <cell title="生产厂商" :value="o.mfrsName"></cell>
        <cell title="电池状态" :value="b_status.value"></cell>
        <cell title="" value="" class="kong"></cell>
      </group>
    </div>
    <box class="parts" gap="10px 10px">
        <x-button class="parts" @click.native="skip('/parts')">查看车辆配件信息</x-button>
        <x-button type="primary" @click.native="deft">设为默认车辆</x-button>
    </box>
  </div>
</template>

<script>
import { Cell, Group, XButton, Box } from 'vux';
import _ from 'lodash';

const vehicle_status = [
  {
    key: 'INVALID',
    value: '作废',
  }, {
    key: 'FREEZE',
    value: '冻结/维保',
  }, {
    key: 'NORMAL',
    value: '正常',
  },
];
const battery_status = [
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
    XButton,
    Box,
  },
  data() {
    return {
      list: {},
      batteryList: [],
      v_status: '',
      b_status: '',
    };
  },
  methods: {
    back() {
      window.history.go(-1);
    },
    skip(a) {
      this.$router.push(`${a}/${this.$route.params.id}`);
    },
    deft() {
      const localID = localStorage.getItem('vehicleId');
      if (localID === this.$route.params.id) {
        this.$vux.toast.show({ text: '该车辆已经为默认车辆', type: 'cancel', width: '10em' });
      } else {
        localStorage.setItem('vehicleId', this.$route.params.id);
        this.$vux.toast.show({ text: '设置成功', type: 'success', width: '10em' });
      }
    },
  },
  async mounted() {
    const { code, message, respData } = (await this.$http.post('/api/mobile/v1/device/getbypk', { id: this.$route.params.id, flag: 'true' })).body;
    if (code !== '200') throw new Error(message || code);
    this.list = respData;
    this.batteryList = respData.bizBatteries;
    this.v_status = _.find(vehicle_status, { key: this.list.vehicleStatus });
    this.b_status = _.find(battery_status, { key: this.batteryList[0].batteryStatus });
  },
};
</script>

<style scoped>
  .head {
    width:100%;
    height:89px;
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
    left:40%;
    top:25px;
  }
  .vehicle_info {
    width:90%;
    background: #fff;
    position: absolute;
    top:70px;
    left:5%;
    box-shadow: 0 2px 5px;
    border-radius: 10px;
    z-index: 1;
  }
  .battery_info {
    width:90%;
    background: #fff;
    position: absolute;
    top:610px;
    left:5%;
    box-shadow: 0 2px 5px;
    border-radius: 10px;
    z-index: 1;
  }
  .weui-cell {
    height:50px;
  }
 >>>.weui-cells__title {
    height:40px;
    font-size: 18px;
    color: #000;
    background-color: #fff;
    text-align: center;
  }
>>>.vux-label {
    font-size: 15pt;
  }
  .kong {
    height:20px;
  }
  .left1-huan {
    width: 8px;
    height:45px;
    position: absolute;
    top:580px;
    left:50px;
    background-image: url(/static/images/bg-huan.jpg);
    z-index: 2;
  }
  .left2-huan {
    width: 8px;
    height:45px;
    position: absolute;
    top:580px;
    left:70px;
    background-image: url(/static/images/bg-huan.jpg);
    z-index: 2;
  }
  .right1-huan {
    width: 8px;
    height:45px;
    position: absolute;
    top:580px;
    right:50px;
    background-image: url(/static/images/bg-huan.jpg);
    z-index: 2;
  }
  .right2-huan {
    width: 8px;
    height:45px;
    position: absolute;
    top:580px;
    right:70px;
    background-image: url(/static/images/bg-huan.jpg);
    z-index: 2;
  }
  .parts {
    margin-top:1150px;
  }
  .weui-btn {
    width:95%;
  }
</style>
