<template>
  <div style="padding:10px;">
    <template>
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="选项卡1" name="first">内容1</el-tab-pane>
        <el-tab-pane label="选项卡2" name="second">内容2</el-tab-pane>
        <el-tab-pane label="选项卡3" name="third">内容3</el-tab-pane>
        <el-tab-pane label="选项卡4" name="fourth">内容4</el-tab-pane>
      </el-tabs>
    </template>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeName: 'second',
    };
  },
  methods: {
    handleClick() {
    },
    // handleClick(tab, event) {
    //   console.log(tab, event);
    // },
  },
};
</script>

<style>

</style>
