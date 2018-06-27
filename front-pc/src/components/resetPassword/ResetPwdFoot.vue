<template>
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
</template>
<script>
export default {
  props: {
    // center: {
    //   type: String,
    // },
  },
  computed: {
    // normalizedSize() {.
    //   return this.center.trim().toLowerCase();
    // },
  },
  data() {
    return {

    };
  },
};
</script>
<style scoped>
/* 脚部 */
.resPw-foot{
  background-color:#D7D7D7;flex: 3;
}
.resPw-foot a {
  text-decoration:none; color: #454545;
}
</style>
