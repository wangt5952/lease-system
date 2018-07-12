<template>
<div style="display:flex;">
  <!-- 监控 -->
  <div class="deviceMonStyle">
    <!-- 头部 -->
    <div class="deviceMonStyle-head">
      <!-- 搜索 -->
      <div class="deviceMonStyle-head-search">
        <el-input v-model="searchDevice" style="margin-top:5px;width:75%" size="mini" suffix-icon="el-icon-edit" placeholder="请输入要查看的地址" clearable></el-input>
        <el-button size="mini" @click="searchStreet(searchDevice)">搜索</el-button>
        <div style="margin-top:10px">
          <el-radio-group v-model="view" @change="radioEvent(view)">
            <el-radio class="radio"
              v-model="report"
              :key="i"
              :label="o"
              v-for="(o, i) in radioList">{{o.label}}
            </el-radio>
          </el-radio-group>
        </div>
      </div>
      <!-- 1 -->
      <div @click="deviceDialogVisible = true" class="deviceMonStyle-head-info">
        <div class="info-head"></div>
        <div class="info-foot">设备编码</div> 
      </div>
      <!-- 2 -->
      <div @click="devicePathVisible = !devicePathVisible" class="deviceMonStyle-head-info">
        <div class="info-head2"></div>
        <div class="info-foot">设备当前位置</div>
      </div>
      <!-- 3 -->
      <div @click="batteryDialogVisible = true" class="deviceMonStyle-head-info">
        <div class="info-head"></div>
        <div class="info-foot">设备剩余电量</div>
      </div>
      <!-- 4 -->
      <div @click="openUserInfoVis" class="deviceMonStyle-head-info">
        <div class="info-head"></div>
        <div class="info-foot">设备使用人</div>
      </div>
    </div>
    <!-- 设备监控地图 -->
    <baidu-map @ready="handler" class="bMapStyle" center="南京" :zoom="15" @dragend="syncCenterAndZooms" @zoomend="syncCenterAndZoom" :scroll-wheel-zoom="true">
    </baidu-map>
  </div>

  <!-- 设备列表 -->
  <div class="deviceListStyle">
    <div style="color:#9096ad;font-size:16px;">设备列表（可用）</div>
    <div class="deviceListInfo">
      <div style="flex:1;">设备编号</div>
      <div style="width:80px;">剩余电量</div>
    </div>
    <div style="flex:1;overflow:scroll;font-size:14px;">
      <div @click="handleSelectItem(o)" v-for="o in getDeviceList" class="clickDeviceEvent"
        :style="selectedId == o.deviceId ? {backgroundColor:'#39a0f6', color:'#fff'} : {backgroundColor:'#e7eef5', color:'#9096ad'}">
        <div style="flex:1;">{{ o.deviceId }}</div>
        <div style="width:80px;">{{ o.perSet }}</div>
      </div>
      <div v-if="true">
        <el-pagination v-if="total"
          small
          @current-change="loadDevice"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="prev, pager, next"
          :total="total">
        </el-pagination>
      </div>
    </div>
  </div>

</div>
</template>

<script>
export default {
  data() {
    return {
      batteryDialogVisible: false,
      devicePathVisible: false,
      deviceDialogVisible: false,
      // 设备列表
      getDeviceList: [],
      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,
      searchDevice: "",
      selectedId: "1",
      radioDeviceType: "1",
      radioList: [
        { label: "按设备查找", value: 1 },
        { label: "按范围查找", value: 2 }
      ],
      view: 1,
      report: 1,
      radioVal: 1
    };
  },
  created() {
    this.loadDevice();
  },
  mounted() {},
  computed: {},
  methods: {
    async handler() {},
    async syncCenterAndZooms() {},
    async syncCenterAndZoom() {},
    async openUserInfoVis() {},
    async searchStreet(v) {},

    async loadDevice() {
      try {
        const { code, message, respData } = (await this.$http.post(
          "/api/manager/device/list",
          {
            keyStr: "",
            needPaging: "true",
            currPage: this.currentPage,
            pageSize: this.pageSize
          }
        )).body;
        if (code === "40106") {
          this.$store.commit("relogin");
          throw new Error("认证超时，请重新登录");
        }
        if (code !== "200") throw new Error(message);
        const { total, rows } = respData;
        this.total = total;
        this.getDeviceList = rows;
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleSelectItem(item) {
      this.selectedId = item.deviceId;
    },
    radioEvent(view) {
      this.radioVal = view.value;
    }
  }
};
</script>

<style lang="scss" scoped>
.deviceMonStyle {
  display: flex;
  flex-direction: column;
  flex: 1;
  .deviceMonStyle-head {
    display: flex;
    height: 100px;
    background: #070f3e;
    color: #fff;
    .deviceMonStyle-head-search {
      flex: 1;
      padding: 10px;
      padding-top: 25px;
    }
    .deviceMonStyle-head-info {
      display: flex;
      flex-direction: column;
      width: 150px;
      text-align: center;
      cursor: pointer;
      .info-head {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        margin-top: 20px;
      }
      .info-foot {
        font-size: 12px;
        height: 40px;
        color: #5f7aa7;
      }
      .info-head2 {
        flex: 1;
        display: flex;
        justify-content: center;
        font-size: 12px;
        margin-top: 20px;
      }
    }
  }
  .bMapStyle {
    width: 100%;
    flex: 1;
  }
}

.deviceListStyle {
  display: flex;
  flex-direction: column;
  width: 190px;
  background: #eff5f8;
  padding: 10px 20px;
  overflow: auto;
  .deviceListInfo {
    display: flex;
    align-items: center;
    font-size: 14px;
    height: 36px;
    margin-bottom: 5px;
    text-align: center;
  }
  .clickDeviceEvent {
    display: flex;
    align-items: center;
    height: 36px;
    margin-bottom: 5px;
    border-radius: 3px;
    text-align: center;
    cursor: pointer;
  }
}
.el-radio {
  color: #fff;
}
</style>

