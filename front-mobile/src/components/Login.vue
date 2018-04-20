<template>
  <div style="height:100%;display:flex;flex-direction:column;justify-content:center;">

    <div style="display:flex;margin:40px 30px;">
      <div style="width:1.8rem;height:1.8rem;border-radius:0.9rem;background-image:url(/static/logo.png);background-size:contain;"></div>
      <div>
        <div style="font-size:0.6rem;">小哥乐途</div>
        <div style="color:#999999;">电动车租赁平台</div>
      </div>
    </div>
    <div class="form" style="margin:40px 30px;font-size:0.4rem;">
      <x-input placeholder="请输入帐号" v-model="form.loginName" style="background:#fff;">
        <template slot="label">
          <i class="lt lt-my"/>
        </template>
      </x-input>
      <x-input placeholder="请输入密码" type="password" v-model="form.password" style="background:#fff;">
        <template slot="label">
          <i class="lt lt-lock"/>
        </template>
      </x-input>

      <x-button type="primary" class="btn-normal" @click.native="handleSubmit">登录</x-button>
    </div>

    <div style="display:flex; color:#00c985;margin:60px 30px;text-align:center;font-size:0.4rem;">
      <router-link to="/join" style="flex:1;color:#00c985;">账号注册</router-link>
      <router-link to="/reset" style="flex:1;border-left:1px solid #00c985;color:#00c985;">找回密码</router-link>
    </div>
  </div>
</template>

<script>
import moment from 'moment';
import md5 from 'js-md5';
import _ from 'lodash';

export default {
  data() {
    return {
      form: {},
    };
  },
  methods: {
    async handleSubmit() {
      const { password, ...form } = this.form;
      const loginTime = moment().unix() * 1000;
      form.loginAuthStr = md5(form.loginName + md5(password).toUpperCase() + loginTime).toUpperCase();
      form.loginTime = loginTime;

      try {
        const { code, message, respData } = (await this.$http.post('/api/mobile/v1/auth/login', form)).body;
        if (code !== '200') {
          this.$vux.toast.show({ text: message, type: 'warn', width: '10em', position: 'top' });
        }
        const { key_login_token, key_user_info, key_vehicle_info } = respData;
        const vehicleList = [];
        vehicleList.push(_.map(key_vehicle_info, 'id'));
        localStorage.setItem('vehicleId', vehicleList);
        await this.$store.commit('login', { key_login_token, key_user_info });
        this.$vux.toast.show({ text: '登录成功', type: 'success', width: '10em' });
        this.$router.push('/');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$vux.toast.show({ text: message, type: 'cancel', width: '10em' });
      }

      // this.$vux.loading.show({ text: '登录中' });
      // await this.$http.post('/mobile/v1/auth/login')
      // console.log(this.form);
      // this.$router.push('/');
    },
  },
};
</script>

<style scoped>
>>> .vux-x-input {
  border: 1px solid #D9D9D9;
  border-radius: 100px;
  margin-bottom: 22px;
  padding: 10px;
}

>>> .weui-cell:before {
  border-top: 0;
}

>>> .weui-cell__hd {
  text-align: center;
  padding-left: 10px;
  padding-right: 10px;
}

>>> .weui-cell__bd {
  padding-left: 10px;
}

>>> .btn-normal {
  background: #008E56;
  border-radius: 30px;
}
>>> .btn-normal:active {
  background: #009C75 !important;
}
</style>
