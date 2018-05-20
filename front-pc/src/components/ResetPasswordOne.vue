<template>
  <div class="resPw">
    <reset-pwd-header></reset-pwd-header>
    <div class="resPw-body">
      <div class="resPw-body-left"></div>
      <div class="resPw-body-center">
        <div style="flex:1;border-bottom:solid 1.5px #EBEBEB;">
          <div style="background-image:url(/static/half-ring-15.png);width:45px;height:67px;position: absolute;left:42%;top:90px"></div>
          <div style="font-size:33px;width:180px;margin:3% 40% 0px 45% ">忘记密码</div>
        </div>
        <div style="flex:3;display:flex;flex-direction:column;">
          <div style="background-image:url(/static/half-ring-13.png);
              width:470px;height:40px;margin:20px 350px">
          </div>
          <div style="width:200px;height:20px;margin:0px 450px 10px 38%;font-weight:bold">请填写您需要找回的账号:</div>

          <el-form :model="sMs" ref="sMs" :rules="rules1">
            <div style="width:410px;height:50px;margin:10px 430px 10px 38%">
              <el-form-item prop="mobile">
                <el-input v-model.number="sMs.mobile" style="width: 300px; height: 35px;" placeholder="请输入您的手机号" clearable></el-input>
                <el-button style="width: 90px; height: 35px;" @click="validateMobileNumber" :type="buttontype" :disabled="state">
                  {{ buttontype === 'primary' ? '获取验证码': `(  ${time} s )`}}
                </el-button>
              </el-form-item>
            </div>
          </el-form>
          <el-form :model="token" ref="token" :rules="rules2">
            <div style="width:300px;height:50px;margin:10px 430px 10px 38%">
              <el-form-item prop="smsVCode">
                <el-input v-model="token.smsVCode" placeholder="请输入短信验证码" clearable></el-input>
              </el-form-item>
            </div>
          </el-form>
          <div class="nextButton" style="width: 100px; height: 35px;margin:10px 430px 10px 38%">
            <el-button style="width: 100px; height: 35px;"
                type="primary" @click="nextQuery">下一步</el-button>
          </div>
        </div>
      </div>
      <div class="resPw-body-right"></div>
    </div>
    <reset-pwd-foot></reset-pwd-foot>
  </div>
</template>

<script>
import * as validate from '@/util/validate';
import ResetPwdHeader from './resetPassword/ResetPwdHeader';
import ResetPwdFoot from './resetPassword/ResetPwdFoot';

// 验证码
const token = (rule, value, callback) => {
  if (!value) callback(new Error('短信验证码不能为空'));
  else callback();
};

// 验证手机格式
const checkPhone = (rule, value, callback) => {
  if (!value) callback(new Error('请输入手机号码'));
  else if (!validate.isvalidPhone(value)) callback(new Error('请输入正确的11位手机号码'));
  else callback();
};
export default {
  components: {
    ResetPwdHeader, ResetPwdFoot,
  },
  data() {
    return {
      sMs: {
        needCaptchaToken: false,
      },
      form: {
        smsToken: '',
      },
      token: {},
      // 验证码按钮状态
      state: false,
      // 验证码按钮样式
      buttontype: 'primary',
      time: 60,
      rules1: {
        mobile: [
          { required: true, validator: checkPhone, trigger: 'blur' },
        ],
      },
      rules2: {
        smsVCode: [
          { required: true, validator: token, trigger: 'blur' },
        ],
      },
    };
  },
  methods: {
    // 验证手机号码
    async validateMobileNumber() {
      const $sMs = this.$refs.sMs;
      await $sMs.validate();
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/auth/sendsms', {
          mobile: this.sMs.mobile,
        })).body;
        if (code !== '200') throw new Error(message || code);
        this.$message.success({ message: '验证码发送中,请稍等片刻...' });
        this.state = true;
        await this.$store.commit('setSmsToken', respData.key_sms_vcode_token);
        this.buttontype = 'info';

        let time = 60;
        const timeOut = setInterval(() => {
          this.buttontype = 'info';
          // this.time = time--;
          time -= 1;
          this.time = time;
          if (time === 0) {
            clearInterval(timeOut);
            this.buttontype = 'primary';
            this.state = false;
          }
        }, 1 * 1000);
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 下一步按钮
    async nextQuery() {
      const $sMs = this.$refs.sMs;
      const $token = this.$refs.token;
      await $sMs.validate();
      await $token.validate();
      await this.$store.commit('setSmsVCode', this.token.smsVCode);
      if ($sMs && $token) {
        this.$router.push('/resetPasswordTwo');
      }
    },
  },
};
</script>

<style scoped>
.resPw{
  display:flex; flex-direction:column; height:100%;
}
/* 主体 */
.resPw-body{
  height: 100%;
  display: flex; flex-direction: row;flex: 9
}
.resPw-body-left,.resPw-body-right{
  flex:1;background-color: #EBEBEB;
}
.resPw-body-center{
  flex:11;
  background-color: white;
  display: flex;
  flex-direction: column;
}

</style>
