<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>修改昵称</span></div>
      <div class="right" @click="handler">确定</div>
    </div>

    <group>
      <x-input v-model="val" title="请输入昵称" required>{{this.val}}</x-input>
    </group>

  </div>
</template>

<script>
import { XInput, Group } from 'vux';
import { mapState } from 'vuex';

export default {
  components: {
    Group,
    XInput,
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,

      relogin: state => state.relogin,
    }),
  },
  data() {
    return {
      val: '',
    };
  },
  methods: {
    back() {
      window.history.go(-1);
    },
    async handler() {
      const { code, message } = (await this.$http.post('/api/mobile/v1/auth/updateuserinfo',
        { id: this.key_user_info.id,
          loginName: this.key_user_info.loginName,
          nickName: this.val,
          userName: this.key_user_info.loginName,
          userPid: this.key_user_info.userPid,
          updateUser: this.key_user_info.loginName })).body;
      if (code !== '200') {
        throw new Error(message || code);
      } else {
        this.key_user_info.nickName = this.val;
        localStorage.setItem('key_user_info', JSON.stringify(this.key_user_info));
        this.$vux.toast.show({ text: '修改成功', type: 'success', width: '10em' });
        setTimeout(() => { this.$router.push('/profile'); }, 100);
      }
    },
  },
  async mounted() {
    if (this.key_user_info.nickName) this.val = this.key_user_info.nickName;
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
  .right {
    margin-top: 30px;
    margin-right: 10px;
    font-size: 15px;
    color: #fff;
  }
  .tlte span{
    font-size: 20px;
    font-weight: 400;
    color: #fff;
    position: absolute;
    left:35%;
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
</style>
