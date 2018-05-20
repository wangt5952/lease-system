<template>
  <div style="display:flex;flex-direction:column;height:100%;">
    <div style="padding:10px 30px;background:#000;color:#fff;font-size:22px;font-weight:bold;">
      小哥乐途后台登录系统
    </div>
    <el-row type="flex" justify="end" style="background:url(/static/pc-login-bg-1.jpg);height:100%;">
      <el-col style="display:flex;background:#ffffff80;padding:10px;max-width:500px;">
        <div style="margin:80px 40px; background:#fff;flex:1;">
          <div style="padding:60px;padding-bottom:0;border-bottom:1px solid #eee;">
            <div style="padding:10px 20px;border-bottom:4px solid #000;display:inline-block;font-size:20px;font-weight:bold;">登录</div>
          </div>
          <el-form ref="form" :model="form" style="margin-top:40px;" >
            <el-form-item>
              <el-input prefix-icon="lt lt-my" v-model="form.loginName" placeholder="用户名" @keyup.enter.native="$refs.pwd.focus()"></el-input>
            </el-form-item>
            <el-form-item>
              <el-input ref="pwd" prefix-icon="lt lt-lock" v-model="form.password" type="password" placeholder="密码" @keyup.enter.native="handleSubmit"></el-input>
            </el-form-item>
            <div style="cursor:pointer;background:#fff;border:2px solid #000;margin:40px 20px;text-align:center;padding:10px;border-radius:3px;" @click="handleSubmit" >登录</div>
            <div style="margin-left:300px;margin-top:-25px">
              <router-link to="resetPassword1" style="text-decoration:none; color: #454545;">忘记密码</router-link>
            </div>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import moment from 'moment';
import md5 from 'js-md5';

export default {
  data() {
    return {
      form: {},
    };
  },
  methods: {
    async handleSubmit() {
      if (this.form.loginName === '' || this.form.password === '') {
        this.$message.error('用户名或者密码不能为空');
        return;
      }
      const { password, ...form } = this.form;
      const loginTime = moment().unix() * 1000;
      form.loginAuthStr = md5(form.loginName + md5(password).toUpperCase() + loginTime).toUpperCase();
      form.loginTime = loginTime;

      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/auth/login', form)).body;
        if (code !== '200') throw new Error(message || code);
        const { key_login_token, key_res_info, key_user_info } = respData;
        await this.$store.commit('login', { key_login_token, key_res_info, key_user_info });
        this.$message.success({
          message: `欢迎回来，${key_user_info.userName}`,
        });
        this.$router.push('/monitor');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
      // this.$router.push('/');
    },
    // resetPassword(){
    //   this.$router.push('/resetPassword');
    // }
  },
};
</script>

<style scoped>
>>> .el-form .el-form-item {
  margin-bottom: 2px;
}
>>> .el-form .el-input {
  background: #eee;
  padding: 10px;
  box-sizing: border-box;
}
>>> .el-form .el-input .el-input__prefix {
  color: #000;
  margin-left: 30px;
}
>>> .el-form .el-input .el-input__icon {
  font-size: 20px;
}

>>> .el-form input.el-input__inner {
  border: 0;
  background: #eee;
  padding-left: 60px;
  font-size: 20px;
  height: 44px;
  -webkit-box-shadow: 0 0 0px 1000px #eee inset !important;
}
</style>
