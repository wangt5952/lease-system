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
        <div style="background-image:url(/static/half-ring-14.png);
            width:470px;height:40px;margin:20px 350px">
        </div>
        <div style="width:200px;height:20px;margin:0px 450px 10px 38%;font-weight:bold">请重置您的密码:</div>
        <el-form :model="form">
          <!-- <div style="width:300px;height:50px;margin:10px 430px 10px 430px"> -->
          <div style="width:300px;height:50px;margin:10px 430px 10px 38%">
            <el-form-item>
              <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" clearable></el-input>
            </el-form-item>
          </div>
          <div style="width:300px;height:50px;margin:10px 430px 10px 38%">
            <el-form-item>
              <el-input v-model="confirmNewPassword" type="password" placeholder="请再次输入新密码" clearable></el-input>
            </el-form-item>
          </div>
        </el-form>
        <div class="nextButton" style="width: 400px; height: 35px;margin:10px 430px 10px 38%;">
          <el-button style="width: 100px; height: 35px;" type="primary" @click="confirm">完成</el-button>
          <el-button style="width: 100px; height: 35px;margin-left:10px" type="info" @click="cancel">取消</el-button>
          <el-button style="width: 100px; height: 35px;margin-left:10px" @click="queryBack">返回上一步</el-button>
        </div>
      </div>
    </div>
    <div class="resPw-body-right"></div>
  </div>
  <reset-pwd-foot></reset-pwd-foot>
</div>
</template>

<script>
import md5 from 'js-md5';
import {
  mapState,
} from 'vuex';
import ResetPwdHeader from './resetPassword/ResetPwdHeader';
import ResetPwdFoot from './resetPassword/ResetPwdFoot';

export default {
  components: {
    ResetPwdHeader, ResetPwdFoot,
  },
  computed: {
    ...mapState({
      smsToken: state => state.smsToken,
      smsVCode: state => state.smsVCode,
    }),
  },
  data() {
    return {
      form: {},
      confirmNewPassword: '',
    };
  },
  methods: {
    // 完成按钮
    async confirm() {
      const { newPassword } = this.form;
      if (newPassword === this.confirmNewPassword) {
        const form = { newPassword: md5(newPassword).toUpperCase(), smsVCode: this.smsVCode, smsToken: this.smsToken };
        try {
          const { code, message } = (await this.$http.post('/api/manager/auth/resetpassword', form)).body;
          if (code !== '200') throw new Error(message || code);
          this.$message.success({
            message: '重置密码成功！2秒后跳转登录页面...',
          });
          setTimeout(() => {
            this.$router.push('/login');
          }, 2 * 1000);
        } catch (e) {
          const message = e.statusText || e.message;
          this.$message.error(message);
        }
      } else {
        this.$message.error('两次密码输入不匹配,请重新输入');
        this.cancel();
      }
    },
    // 取消按钮
    cancel() {
      this.form.newPassword = '';
      this.confirmNewPassword = '';
    },
    queryBack() {
      this.$router.push('/resetPasswordOne');
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
