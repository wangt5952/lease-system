<template>
<div class="resPw">
    <!-- 头部 -->
    <div class="resPw-header">
      <div class="header-font">
        <div style="font-size:35px;color:#4377B8;">logo</div>
        &nbsp;
        <div style="margin-top:4px;font-size:23px;color:#F2F2F2;width:150px;height:50px">小哥乐途</div>
        <div class="longinButton">
          <el-button type="info" size="medium">
            <router-link to="login">登录</router-link>
          </el-button>
        </div>
        <!-- <div class="registerButton"><a href="javascript:">注册</a></div> -->
      </div>
    </div>
    <!-- 主体 -->
    <!-- 步骤1 -->
    <template v-if="stepNo1">
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
            <div style="width:200px;height:20px;margin:0px 450px 10px 430px;font-weight:bold">请填写您需要找回的账号:</div>

            <el-form :model="sMs" ref="sMs" :rules="rules1">
              <div style="width:410px;height:50px;margin:10px 430px 10px 430px">
                <el-form-item prop="mobile">
                  <el-input v-model.number="sMs.mobile" style="width: 300px; height: 35px;" placeholder="请输入您的手机号" clearable></el-input>
                  <el-button style="width: 90px; height: 35px;" @click="validateMobileNumber" :type="buttontype" :disabled="state">
                    {{ buttontype === 'primary' ? '获取验证码': `(  ${time} s )`}}
                  </el-button>
                </el-form-item>
              </div>
            </el-form>
            <el-form :model="token" ref="token" :rules="rules2">
              <div style="width:300px;height:50px;margin:10px 430px 10px 430px">
                <el-form-item prop="smsVCode">
                  <el-input v-model="token.smsVCode" placeholder="请输入短信验证码" clearable></el-input>
                </el-form-item>
              </div>
            </el-form>
            <div class="nextButton" style="width: 100px; height: 35px;margin:10px 430px 10px 430px">
              <el-button style="width: 100px; height: 35px;"
                  type="primary" @click="nextQuery">下一步</el-button>
            </div>
          </div>
        </div>
        <div class="resPw-body-right"></div>
      </div>
    </template>
    <!-- 步骤2 -->
    <template v-if="stepNo2">
      <div class="resPw-body">
        <div class="resPw-body-left"></div>
        <div class="resPw-body-center">
          <div style="flex:1;border-bottom:solid 1.5px #EBEBEB;">
            <div style="background-image:url(/static/half-ring-15.png);width:45px;height:67px;position: absolute;left:42%;top:90px"></div>
            <div style="font-size:33px;width:180px;margin:3% 40% 0px 45% ">忘记密码</div>
          </div>
          <div style="flex:3;display:flex;flex-direction:column;">
            <div style="background-image:url(/static/half-ring-14.png);
                width:470px;height:40px;margin:20px 350px">
            </div>
            <div style="width:200px;height:20px;margin:0px 450px 10px 430px;font-weight:bold">请重置您的密码:</div>
            <el-form :model="form">
              <div style="width:300px;height:50px;margin:10px 430px 10px 430px">
                <el-form-item>
                  <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" clearable></el-input>
                </el-form-item>
              </div>
              <div style="width:300px;height:50px;margin:10px 430px 10px 430px">
                <el-form-item>
                  <el-input v-model="confirmNewPassword" type="password" placeholder="请再次输入新密码" clearable></el-input>
                </el-form-item>
              </div>
            </el-form>
            <div class="nextButton" style="width: 400px; height: 35px;margin:10px 430px 10px 430px;">
              <el-button style="width: 100px; height: 35px;" type="primary" @click="confirm">完成</el-button>
              <el-button style="width: 100px; height: 35px;margin-left:1px" type="info" @click="cancel">取消</el-button>
              <el-button style="width: 100px; height: 35px;margin-left:10px" @click="cancel">上一步</el-button>
            </div>
          </div>
        </div>
        <div class="resPw-body-right"></div>
      </div>
    </template>

    <!-- 脚部 -->
    <div class="resPw-foot">
      <div style="padding-left:300px; margin:20px 0px 40px 110px;width:900px;height:60px;border-bottom:solid 1.5px #BFBFBF;">
        <a href="javascript:">关于我们  &nbsp;|</a>
        <a href="javascript:">联系方式  &nbsp;|</a>
        <a href="javascript:">对外合作  &nbsp;|</a>
        <a href="javascript:">服务条款  &nbsp;|</a>
        <a href="javascript:">隐私政策  &nbsp;|</a>
        <a href="javascript:">版权声明  &nbsp;|</a>
        <a href="javascript:">招贤纳士  &nbsp;|</a>
        <a href="javascript:">问题建议  &nbsp;</a>
      </div>
      <div style="padding:0px 300px;width:300px;height:20px;position:absolute;left:270px;bottom:40px">
        Copyright© &nbsp;* &nbsp;* &nbsp;* &nbsp;* &nbsp;* &nbsp;* &nbsp;* &nbsp;* &nbsp;* &nbsp;
      </div>
    </div>
</div>
</template>
<script>
import md5 from 'js-md5';
import * as validate from '@/util/validate';

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
  data() {
    return {
      sMs: {
        needCaptchaToken: false,
      },
      form: {
        smsToken: '',
      },
      token: {},
      confirmNewPassword: '',
      stepNo1: true,
      stepNo2: false,
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
        const { key_sms_vcode_token } = respData;
        this.form.smsToken = key_sms_vcode_token;
        this.buttontype = 'info';

        let time = 60;
        const timeOut = setInterval(() => {
          this.buttontype = 'info';
          this.time = time--;
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
      if ($sMs && $token) {
        this.stepNo1 = false;
        this.stepNo2 = true;
      }
    },
    // 完成按钮
    async confirm() {
      const { smsToken, newPassword } = this.form;
      const { smsVCode } = this.token;
      if (newPassword === this.confirmNewPassword) {
        const form = { newPassword: md5(newPassword).toUpperCase(), smsVCode, smsToken };
        try {
          const { code, message } = (await this.$http.post('/api/manager/auth/resetpassword', form)).body;
          if (code !== '200') throw new Error(message || code);
          this.$message.success({
            message: '重置密码成功！',
          });
          this.$router.push('/login');
        } catch (e) {
          const message = e.statusText || e.message;
          this.$message.error(message);
        }
      } else {
        this.$message.error('两次密码输入不配备,请重新输入');
        this.cancel();
      }
    },
    // 取消按钮
    cancel() {
      this.form.newPassword = '';
      this.confirmNewPassword = '';
    },
  },
};
</script>
<style scoped>
.resPw{
  display:flex; flex-direction:column; height:100%;
}
/* 头部 */
.resPw-header{
  padding:15px 100px; background:#4D4D4D;
}
.header-font{
  display:flex;flex-direction:row
}
.header-font el-button{
  width: 60px; height: 13px;
}
.longinButton{
  margin:5px 0px 0px 70%
}
.registerButton{
  font-size:15px;color:white;margin: 12px 0px 0px 30px;
}

.resPw-header a{
  text-decoration:none; color:white;
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

/* 脚部 */
.resPw-foot{
  background-color:#D7D7D7;flex: 3;
}
.resPw-foot a {
  text-decoration:none; color: #454545;
}
</style>
