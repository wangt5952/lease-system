<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>实名认证</span></div>
    </div>

    <div>
     <step v-model="step" background-color='#fbf9fe'>
       <div class="line"></div>
       <step-item title="步骤1:" description="输入身份证号"></step-item>
     </step>
   </div>
   <x-hr></x-hr>

   <group>
    <x-input title="身份证号：" placeholder="请输入身份证号" :min="15" :max="18" required v-model="value"></x-input>
   </group>
   <x-button type="primary" @click.native="handler">下一步</x-button>
  </div>
</template>

<script>
import { Step, StepItem, XButton, XHr, XInput, Group } from 'vux';

export default {
  components: {
    Step,
    StepItem,
    XButton,
    XHr,
    XInput,
    Group,
  },
  data() {
    return {
      step: 0,
      value: '',
    };
  },
  methods: {
    back() {
      this.$router.replace('/');
    },
    handler() {
      if (this.value === '') {
        this.$vux.toast.show({ text: '身份证号不能为空', type: 'cancel', width: '10em' });
        return;
      }
      const p18 = /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(this.value);
      const p15 = /^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}$/.test(this.value);
      if (p18 || p15) {
        this.$router.replace(`/authentication_step2/${this.value}`);
      } else {
        this.$vux.toast.show({ text: '错误的身份证号', type: 'cancel', width: '10em' });
      }
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
  >>>.vux-step {
    position: relative;
  }
  >>>.vux-step-item {
    width:100%;
  }
  >>>.vux-step-item-head {
    position: relative;
    display: inline-block;
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
  >>>.vux-step-item-main-process {
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
    margin-top:20px;
  }
</style>
