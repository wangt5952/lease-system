<template>
  <div style="height:100%;display:flex;flex-direction:column;justify-content:center;">

    <div style="display:flex;margin:40px 30px;">
      <div style="width:1.8rem;height:1.8rem;border-radius:0.9rem;background-image:url(/static/logo.png);background-size:contain;"></div>
      <div>
        <div style="font-size:0.6rem;">小哥乐途</div>
        <div style="color:#999999;">电动车租赁平台</div>
      </div>
    </div>
    <div class="form" style="margin:10px 30px;font-size:0.4rem;">
      <x-input placeholder="请输入手机号" v-model="form.mobile" style="background:#fff;">
        <template slot="label">
          <i slot="icon" class="iconfont icon-shoujihao"></i>
        </template>
      </x-input>
      <x-input placeholder="图形验证码" v-model="form.captcha" style="background:#fff;">
        <template slot="label">
          <i slot="icon" class="iconfont icon-yanzhengma2"></i>
        </template>
        <div @click="reloadCaptcha" style="width:2.5rem;height:0.8rem;margin:0 5px;background-size:contain;" :style="{backgroundImage:`url(data:image/png;base64,${key_captcha_base64})`}" slot="right">
          <!-- <img   style="height:0.8rem;margin:auto 0;"  :src="``" /> -->
        </div>
      </x-input>
      <x-input placeholder="短信验证码" v-model="form.smsVCode" style="background:#fff;">
        <template slot="label">
          <i slot="icon" class="iconfont icon-yanzhengma2"></i>
        </template>
        <x-button :disabled="codeBtnDisabled>0" slot="right" class="btn-small" type="primary" @click.native="handleCode">{{ codeBtnDisabled ? `${codeBtnDisabled} 秒后重新获取` : '获取验证码'}}</x-button>
      </x-input>
      <x-input placeholder="请输入6-10位字母数字混合密码" type="password" v-model="form.password" :min="6" :max="10" style="background:#fff;">
        <template slot="label">
          <i slot="icon" class="iconfont icon-icon-"></i>
        </template>
      </x-input>
      <group>
        <popup-picker :data="list" v-model="form.orgName">
          <template slot="title">
            <i slot="icon" class="iconfont icon-qiyetupu"></i>
            <span style="margin-left:10px;line-height:1.41176471;color:inherit;font-size:14px;">关联企业</span>
          </template>
        </popup-picker>
      </group>

      <x-button type="primary" class="btn-normal" @click.native="handleSubmit">注册</x-button>
    </div>
  </div>
</template>

<script>
import { Group, PopupPicker } from 'vux';
import md5 from 'js-md5';
import _ from 'lodash';

export default {
  components: {
    PopupPicker,
    Group,
  },
  data() {
    return {
      form: {},
      key_captcha_base64: '',
      codeBtnDisabled: 0,
      secondTickHandle: null,
      list: [],
      dataList: [],
    };
  },
  methods: {
    secondTick() {
      if (this.codeBtnDisabled) {
        this.codeBtnDisabled = this.codeBtnDisabled - 1;
      }
      if (!this.codeBtnDisabled) this.stopSecondTick();
    },
    startSecondTick() {
      if (!this.secondTickHandle) this.secondTickHandle = setInterval(() => this.secondTick(), 1000);
    },
    stopSecondTick() {
      if (this.secondTickHandle) {
        clearInterval(this.secondTickHandle);
        this.secondTickHandle = null;
      }
    },

    async handleSubmit() {
      const { mobile, password, smsToken, smsVCode, orgName } = this.form;
      const form = {
        loginName: '',
        userMobile: mobile,
        password,
        createUser: '',
        updateUser: '',
        smsToken,
        smsVCode,
        orgId: '',
      };

      try {
        if (!form.userMobile) throw new Error('请输入手机号');
        if (!form.smsToken) throw new Error('请先获取短信码');
        if (!form.smsVCode) throw new Error('请输入短信码');
        if (!form.password) throw new Error('请输入密码');
        if (!orgName) throw new Error('请选择关联企业');
        if (!/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$/.test(form.password)) throw new Error('请输入6-10位字母数字混合的密码');
        form.password = md5(form.password).toUpperCase();
        form.orgId = _.find(this.dataList, { orgName: orgName[0] }).id;
        const { code, message } = (await this.$http.post('/api/mobile/v1/auth/register', form)).body;
        if (code !== '200') throw new Error(message || code);

        this.$vux.toast.show({ text: '注册成功', type: 'success', width: '10em' });
        this.$router.push('/login');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$vux.toast.text(message);
      }
    },
    async handleCode() {
      const { mobile, captchaToken, captcha } = this.form;
      const form = {
        mobile, captchaToken, captcha, needCaptchaToken: 'true',
      };
      try {
        if (!form.mobile) throw new Error('请输入手机号');
        if (!/^\d{11}$/.test(form.mobile)) throw new Error('手机格式有误');
        if (!form.captcha) throw new Error('请输入图形验证码');
        const { code, message, respData } = (await this.$http.post('/api/mobile/v1/auth/sendsms', form)).body;
        if (code !== '200') throw new Error(message || code);
        this.codeBtnDisabled = 30;
        this.startSecondTick();
        const { key_sms_vcode_token } = respData;
        this.form.smsToken = key_sms_vcode_token;
        this.$vux.toast.text('短信验证码已发送');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$vux.toast.text(message);
      }
    },

    async reloadCaptcha() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/mobile/v1/auth/getcaptcha')).body;
        if (code !== '200') throw new Error(message || code);
        const { key_captcha_base64, key_captcha_token } = respData;
        this.form.captchaToken = key_captcha_token;
        this.key_captcha_base64 = key_captcha_base64;
      } catch (e) {
        const message = e.statusText || e.message;
        this.$vux.toast.text(message);
      }
    },
    async getOrg() {
      try {
        const { code, message, respData } = (await this.$http.get('/api/mobile/v1/auth/userBindOrg')).body;
        if (code !== '200') throw new Error(message || code);
        this.dataList = respData;
        this.list.push(_.map(respData, 'orgName'));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$vux.toast.text(message);
      }
    },
  },
  async mounted() {
    await this.reloadCaptcha();
    await this.getOrg();
  },
  destroyed() {
    this.stopSecondTick();
  },
};
</script>

<style scoped>
>>> .weui-cell__ft {
  display: flex;
  align-items: center;
}
>>> .vux-x-input {
  border: 1px solid #D9D9D9;
  border-radius: 100px;
  margin-bottom: 22px;
  padding: 10px;
}
>>>.vux-cell-box {
  border: 1px solid #D9D9D9;
  border-radius: 100px;
  margin-bottom: 22px;
  padding: 10px;
}
>>> .weui-cell {
  padding: 0px 5px;
}
>>>.weui-cells {
  font-size: inherit;
  position: inherit;
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
  margin:10px;
}

>>> .btn-small {
  background: #008E56;
  font-size: 0.4rem;
  border-radius: 30px;
  margin-left: 5px;
}
>>> .btn-small:active {
  background: #009C75 !important;
}

>>> .btn-normal {
  background: #008E56;
  border-radius: 30px;
}
>>> .btn-normal:active {
  background: #009C75 !important;
}
</style>
