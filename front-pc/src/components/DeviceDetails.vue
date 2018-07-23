<template>
  <div v-loading="loading" style="padding:10px">
    <div style="display: flex">
      <el-form :inline="true">
        <el-form-item label="企业名称:">
          <el-input style="width:150px;" v-model="search.orgName" placeholder="企业名称"></el-input>
        </el-form-item>
        <el-form-item label="设备名称:">
          <el-input style="width:150px;" v-model="search.deviceName" placeholder="设备名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="search.startTime" type="datetime" placeholder="起始时间" default-time="00:00:00"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="search.endTime" type="datetime" placeholder="终止时间" default-time="00:00:00"></el-date-picker>
        </el-form-item>
      </el-form>
      <div>
        <el-button type="primary" size="small" @click="searchDevice">统计闲置设备</el-button>
      </div>
      <div style="margin-left:10px;">
        <el-button type="success">导出</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import moment from "moment";

export default {
  data() {
    return {
      loading: false,
      search: {
        orgName: "",
        deviceName: "",
        startTime: "",
        endTime: ""
      }
    };
  },
  watch: {},
  methods: {
    async searchDevice() {
      const { search } = this;
      if (search.startTime) search.startTime = moment(search.startTime).format("X") * 1000;
      else search.startTime = "";
      if(search.endTime) search.endTime = moment(search.endTime).format("X") * 1000;
      else search.endTime = "";
      console.log(search);
    }
  },
  mounted() {}
};
</script>

<style scoped>
</style>

