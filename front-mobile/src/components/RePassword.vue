<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>修改密码</span></div>
    </div>

    <group>
      <x-input title="旧密码" placeholder="请输入原始密码" :min="6" :max="10" required v-model="oldVal"></x-input>
      <x-input title="新密码" placeholder="请输入新密码" :min="6" :max="10" required v-model="newVal"></x-input>
      <x-input title="确认新密码" placeholder="请再次输入新密码" :min="6" :max="10" required v-model="confirm"></x-input>
    </group>

    <x-button @click.native="handler" type="primary">提交</x-button>
  </div>
</template>

<script>
import { XInput, XButton, Group } from 'vux';
import { mapState } from 'vuex';
import moment from 'moment';
import md5 from 'js-md5';

export default {
  components: {
    Group,
    XInput,
    XButton,
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,

      relogin: state => state.relogin,
    }),
  },
  data() {
    return {
      oldVal: '',
      newVal: '',
      confirm: '',
    };
  },
  methods: {
    back() {
      window.history.go(-1);
    },
    async handler() {
      try {
        if (this.newVal !== this.confirm) {
          this.$vux.toast.show({ text: '新密码两次输入不一致', type: 'warn', width: '10em' });
        }
        if (!/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$/.test(this.newVal)) throw new Error('请输入6-10位字母数字混合的密码');
        const authTime = moment().unix() * 1000;
        const { loginName } = this.key_user_info;
        const form = {
          oldAuthStr: md5(loginName + md5(this.oldVal).toUpperCase() + authTime).toUpperCase(),
          authTime,
          newPassword: md5(this.newVal).toUpperCase(),
        };

        const { code, message } = (await this.$http.post('/api/mobile/v1/device/modifypassword', form)).body;
        if (code !== '200') throw new Error(message);
        this.$vux.toast.show({ text: '修改密码成功', type: 'success', width: '10em' });
        await this.$store.commit('logout');
        setTimeout(() => { this.$router.replace('/login'); }, 200);
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$vux.toast.show({ text: message, type: 'warn', width: '10em' });
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
    left:35%;
    top:25px;
  }
  .bg-parts {
    width:90%;
    margin:0 auto;
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
  .weui-btn{
    width:80%;
    margin-top:20px;
  }
</style>
