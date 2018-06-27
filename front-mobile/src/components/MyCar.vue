<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>我的车辆</span></div>
    </div>

    <group v-if="this.list.length === 0">
     <x-textarea title="" v-model="value"></x-textarea>
     <x-button type="primary" @click.native="handler">实名认证</x-button>
    </group>

    <group v-for="(item,index) in list" :key="item.id">
      <cell :title="`我的车辆${index+1}`"  :value="item.id == localID?'默认车辆':''" :link="`/info/${item.id}`"></cell>
    </group>
  </div>
</template>

<script>
import { Cell, Group, XTextarea, XButton } from 'vux';
import { mapState } from 'vuex';

export default {
  components: {
    Group,
    Cell,
    XTextarea,
    XButton,
  },
  data() {
    return {
      list: [],
      localID: localStorage.getItem('vehicleId'),
      value: '很遗憾您的名下没有车辆，赶快实名认证，去企业申领车辆吧！',
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
      relogin: state => state.relogin,
    }),
  },
  methods: {
    back() {
      this.$router.replace('/');
    },
    handler() {
      this.$router.replace('/authentication_step1');
    },
  },
  async mounted() {
    const { code, message, respData } = (await this.$http.post('/api/mobile/v1/device/getVehicleByUserId', { id: this.key_user_info.id })).body;
    if (code !== '200') throw new Error(message || code);
    this.list = respData;
    console.log(this.list);
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
    left:40%;
    top:25px;
  }
  .weui-cell {
    height:50px;
  }
  >>>.weui-cells {
    margin:0;
  }
  >>>.vux-label {
      font-size: 15pt;
    }
  .weui-btn {
    width:80%;
  }
</style>
