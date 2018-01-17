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
      <x-input placeholder="请输入帐号" v-model="form.username" style="background:#fff;">
        <template slot="label">
          <i class="lt lt-my"/>
        </template>
      </x-input>
      <x-input placeholder="请输入密码" type="password" v-model="form.password" style="background:#fff;">
        <template slot="label">
          <i class="lt lt-lock"/>
        </template>
      </x-input>

      <x-button type="primary" style="background:#00c985;border-radius:25px;" @click.native="handleSubmit">登录</x-button>
    </div>

    <div style="display:flex; color:#00c985;margin:60px 30px;text-align:center;font-size:0.4rem;">
      <div style="flex:1">账号注册</div>
      <div style="flex:1;border-left:1px solid #00c985">找回密码</div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {},
      msg: 'Welcome to Your Vue.js App',
    };
  },
  methods: {
    async handleSubmit() {
      this.$router.push('/');
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
</style>
