<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>车辆信息</span></div>
    </div>
    <div class="battery_info">
      <group title="电池信息">
        <cell title="电池编号" value="dc00001" ></cell>
        <cell title="剩余电量" value="80%"></cell>
        <cell title="生产厂商" value="南京第一电池厂"></cell>
        <cell title="型号" value="锂电池"></cell>
        <cell title="参数" value="48V20A"></cell>
        <cell title="状态" value="正常"></cell>
        <cell title="" value="" class="kong"></cell>
      </group>
    </div>
    <div class="left1-huan"></div>
    <div class="left2-huan"></div>
    <div class="right1-huan"></div>
    <div class="right2-huan"></div>
    <div class="part_info">
      <group title="配件信息">
        <cell title="配件编号" value="pj00001" ></cell>
        <cell title="配件货品" value="锡柴"></cell>
        <cell title="品牌" value="南京第一配件"></cell>
        <cell title="型号" value="XXX"></cell>
        <cell title="参数" value="XXX"></cell>
        <cell title="状态" value="正常"></cell>
      </group>
    </div>
  </div>
</template>

<script>
import { Cell, CellBox, CellFormPreview, Group, Badge } from 'vux';

export default {
  components: {
    Group,
    Cell,
    CellFormPreview,
    CellBox,
    Badge,
  },
  methods: {
    back() {
      window.history.go(-1);
    },
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
  .battery_info {
    width:90%;
    background: #fff;
    position: absolute;
    top:70px;
    left:5%;
    box-shadow: 0 2px 5px;
    border-radius: 10px;
    z-index: 1;
  }
  .part_info {
    width:90%;
    background: #fff;
    margin-bottom: 20px;
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
</style>
