<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>实名认证</span></div>
    </div>

    <div>
     <step v-model="step" background-color='#fbf9fe'>
       <div class="line"></div>
       <step-item title="" description="" style="display:none"></step-item>
       <step-item title="" description="" style="display:none"></step-item>
       <step-item title="步骤3:" description="等待审核"></step-item>
     </step>
   </div>

   <group style="margin-top:10px;">
    <div class="bg"><span>{{this.value}}</span></div>
   </group>
   <div class="botmline"></div>
   <x-button type="primary" @click.native="handler">完成</x-button>
  </div>
</template>

<script>
import { Step, StepItem, XButton, Group } from 'vux';

export default {
  components: {
    Step,
    StepItem,
    XButton,
    Group,
  },
  data() {
    return {
      step: 0,
      value: '        请耐心等待企业审核',
    };
  },
  methods: {
    back() {
      window.history.go(-1);
    },
    async handler() {
      const { code, message, respData } = (await this.$http.get('/api/mobile/v1/auth/userState')).body;
      if (code !== '200') throw new Error(message || code);
      const { key_user_info } = respData;
      await this.$store.commit('update', { key_user_info });
      this.$router.push('/');
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
  >>>.vux-step-item {
    width:100%;
  }
  >>>.vux-step-item-head-inner {
    border: 1px solid #09bb07!important;
    color: #FFF!important;
    background: #09bb07 none repeat scroll 0 0!important;
  }
  >>>.vux-step-item-head {
    margin: 2px 10px;
  }
  .line {
    width: 30%;
    height:1px;
    clear:both;
    border-top:1px solid #888;
    position: absolute;
    top:14%;
  }
  >>>.vux-step-item-main {
    font-weight: bold;
    color: #666;
    margin:0 10%;
    width:80%;
    height:80px;
  }
  >>>.vux-step-item-title {
    font-size: 18px;
  }
  >>>.vux-step-item-description {
    margin:10px 50px;
    font-size:15px;
  }
  .weui-btn {
    width:80%;
    margin:20px auto;
  }
  >>>.vux-x-hr {
    margin:0;
  }
  .bg {
    width:100%;
    height:50px;
    margin: 10px auto;
    text-align: center;
  }
  .bg span {
    margin:auto 20px;
    padding:10px 0;
    font-size: 18px;
  }
</style>
