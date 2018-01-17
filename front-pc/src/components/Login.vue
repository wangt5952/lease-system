<template>
  <div>
    <el-row type="flex" justify="center">
      <el-col style="border:1px solid red;border-radius:5px;padding:10px;max-width:400px;">
        <el-form ref="form" :model="form">
          <el-form-item label="账号">
            <el-input size="medium" v-model="form.username"></el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input size="medium" v-model="form.password"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button size="medium" type="primary" @click="handleSubmit" style="width:100%;margin-top:13px;">登录</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>

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
    handleSubmit() {
      this.$router.push('/');
    },
  },
};
</script>

<style scoped>
</style>
