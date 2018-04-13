<template>
  <div id="app">
    <router-view/>
  </div>
</template>
<script src='http://api.map.baidu.com/api?v=2.0&ak=NmRvD46XSX0n2jOYGNZhK2jA9Bw6yGT0&callback=init'></script>
<script>
export default {
  name: 'App',
};
</script>

<style lang="less">
@import url(//at.alicdn.com/t/font_622714_fg1iqo8wx5wdygb9.css);
@import '~vux/src/styles/reset.less';
@import '~vux/src/styles/1px.less';
@import '~vux/src/styles/tap.less';
#app {
  min-width:320px;
  max-width:640px;
  margin:0 auto;
  height: 100%;
}
html, body {
  height: 100%;
  margin: 0;
  -webkit-overflow-scrolling: touch;
  user-select: none;
}
</style>
