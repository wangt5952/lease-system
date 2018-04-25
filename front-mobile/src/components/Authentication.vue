<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>实名认证</span></div>
    </div>

    <group>
      <cell
      title="请您上传身份证正面照片"
      is-link
      :border-intent="false"
      :arrow-direction="showContent001 ? 'up' : 'down'"
      @click.native="showContent001 = !showContent001"></cell>

      <template v-if="showContent001">
        <div class="card" @click="select">
          <img src="/static/images/front_card.jpg" style="width:100%;">
        </div>

        <div v-transfer-dom>
          <x-dialog v-model="show" hide-on-blur>
            <div class="img-box">
              <img src="/static/images/source_frontCard.jpg" style="max-width:100%">
            </div>
          </x-dialog>
        </div>

        <div class="up" type="button">
            <img src="/static/images/add.png" style="width:100%;">
            <input type="file" class="file" multiple>
        </div>
      </template>

      <cell
      title="请您上传身份证反面照片"
      is-link
      :border-intent="false"
      :arrow-direction="showContent002 ? 'up' : 'down'"
      @click.native="showContent002 = !showContent002"></cell>

      <template v-if="showContent002">
        <div class="card" @click="select1">
          <img src="/static/images/back_card.jpg"  style="width:100%;">
        </div>

        <div v-transfer-dom>
          <x-dialog v-model="show1" hide-on-blur>
            <div class="img-box">
              <img src="/static/images/source_backCard.jpg" style="max-width:100%">
            </div>
          </x-dialog>
        </div>

        <div class="up" type="button">
            <img src="/static/images/add.png" style="width:100%;">
            <input type="file" class="file" multiple>
        </div>
      </template>

      <cell
      title="请您上传手持身份证正面照片"
      is-link
      :border-intent="false"
      :arrow-direction="showContent003 ? 'up' : 'down'"
      @click.native="showContent003 = !showContent003"></cell>

      <template v-if="showContent003">
        <div class="sc_card" @click="select2">
          <img src="/static/images/sc_card.jpg" style="width:100%">
        </div>

        <div v-transfer-dom>
          <x-dialog v-model="show2" hide-on-blur>
            <div class="img-box">
              <img src="/static/images/sc_card.jpg" style="width:100%">
            </div>
          </x-dialog>
        </div>

        <div class="sc_up" type="button">
            <img src="/static/images/add1.png" style="width:100%;height:100%;">
            <input type="file" class="file" multiple>
        </div>
      </template>
    </group>

  </div>
</template>

<script>
import { Cell, Group, XDialog, TransferDom } from 'vux';
import { mapState } from 'vuex';

export default {
  directives: {
    TransferDom,
  },
  components: {
    Group,
    Cell,
    XDialog,
    TransferDom,
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,

      relogin: state => state.relogin,
    }),
  },
  data() {
    return {
      showContent001: false,
      showContent002: false,
      showContent003: false,
      show: false,
      show1: false,
      show2: false,
    };
  },
  methods: {
    back() {
      window.history.go(-1);
    },
    select() {
      this.show = true;
    },
    select1() {
      this.show1 = true;
    },
    select2() {
      this.show2 = true;
    },
  },
};
</script>

<style scoped>
  .head {
    width:100%;
    height:65px;
    background: -webkit-linear-gradient(#16D0A2,#20C987)!important;
    background: -o-linear-gradient(#16D0A2,#20C987)!important;
    background: -moz-linear-gradient(#16D0A2,#20C987)!important;
    background: linear-gradient(#16D0A2,#20C987)!important;
    display: flex;
    justify-content: space-between;
    margin: 0 auto;
    z-index: -1;
  }
  .left {
    margin-top:25px;
    margin-left: 5px;
    color: #fff;
  }
  .left .iconfont {
    font-size: 17pt;
  }
  .tlte span{
    font-size: 20px;
    font-weight: 400;
    color: #fff;
    position: absolute;
    left:40%;
    top:25px;
  }
  .weui-cell {
    height:50px;
  }
  >>>.weui-cells {
    margin:0;
  }
  >>>.vux-label {
      font-size: 15pt;
    }
  .card {
    width:40%;
    height:0;
    display: inline-block;
    padding-bottom: 40%;
    margin: 10px 10px;
    border: 1px dashed #666;
  }
  .up {
    width:40%;
    height:0;
    display: inline-block;
    padding-bottom: 40%;
    margin-left: 10px;
    border: 1px dashed #666;
    position: relative;
  }
  .up:hover {
    cursor: pointer;
  }
  .sc_up {
    width:90%;
    height:130px;
    display: inline-block;
    margin-left: 10px;
    border: 1px dashed #666;
    position: relative;
  }
  .sc_up:hover {
    cursor: pointer;
  }
  .img-box {
    height: 300px;
    overflow: hidden;
  }
  .file {
    width:100%;
    height:100%;
    opacity:0;
    font-size:100px;
    position:absolute;
    top:0;
    right:0;
  }
  .sc_card {
    width:90%;
    height:0;
    display: inline-block;
    padding-bottom: 70%;
    margin: 10px 10px;
    border: 1px dashed #666;
  }
</style>
