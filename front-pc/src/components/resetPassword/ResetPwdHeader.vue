<template>
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
    </div>
  </div>
</template>
<script>

export default {
  data() {
    return {

    };
  },
};
</script>
<style scoped>
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
.resPw-header a{
  text-decoration:none; color:white;
}
</style>
