<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>实名认证</span></div>
    </div>

    <div>
     <step v-model="step" background-color='#fbf9fe'>
       <div class="line"></div>
       <step-item title="" description="" style="display:none"></step-item>
       <step-item title="步骤2:" description="上传身份证件图片"></step-item>
     </step>
   </div>


    <group style="margin-top:10px">
      <cell
      title="请您上传身份证正面照片"
      is-link
      :border-intent="false"
      :arrow-direction="showContent001 ? 'up' : 'down'"
      @click.native="showContent001 = !showContent001"></cell>

      <template v-if="showContent001">
        <div class="card" @click="select(1)">
          <img src="/static/images/front_card.jpg" style="width:100%;">
        </div>

        <div v-transfer-dom>
          <x-dialog v-model="show" hide-on-blur>
            <div class="img-box">
              <img src="/static/images/source_frontCard.jpg" style="max-width:100%;height:250px;margin:25px auto">
            </div>
          </x-dialog>
        </div>

        <div class="up" type="button">
            <img :src="this.data" :width="this.width" :height="this.height">
            <input type="file" class="file" accept="image/*" multiple @change="change(1,$event)">
        </div>
      </template>

      <cell
      title="请您上传身份证反面照片"
      is-link
      :border-intent="false"
      :arrow-direction="showContent002 ? 'up' : 'down'"
      @click.native="showContent002 = !showContent002"></cell>

      <template v-if="showContent002">
        <div class="card" @click="select(2)">
          <img src="/static/images/back_card.jpg"  style="width:100%;">
        </div>

        <div v-transfer-dom>
          <x-dialog v-model="show1" hide-on-blur>
            <div class="img-box">
              <img src="/static/images/source_backCard.jpg" style="max-width:100%;height:250px;margin:25px auto">
            </div>
          </x-dialog>
        </div>

        <div class="up" type="button">
            <img :src="this.data1" :width="this.width" :height="this.height">
            <input type="file" class="file" accept="image/*" @change="change(2,$event)">
        </div>
      </template>

      <cell
      title="请您上传手持身份证正面照片"
      is-link
      :border-intent="false"
      :arrow-direction="showContent003 ? 'up' : 'down'"
      @click.native="showContent003 = !showContent003"></cell>

      <template v-if="showContent003">
        <div class="sc_card" @click="select(3)">
          <img src="/static/images/sc_card.jpg" style="width:100%">
        </div>

        <div v-transfer-dom>
          <x-dialog v-model="show2" hide-on-blur>
            <div class="img-box">
              <img src="/static/images/sc_card.jpg" style="max-width:100%;height:250px;margin:25px auto">
            </div>
          </x-dialog>
        </div>

        <div class="sc_up" type="button">
            <img :src="this.data2" :width="this.width" :height="this.height">
            <input type="file" class="file" accept="image/*" @change="change(3,$event)">
        </div>

      </template>
    </group>
    <x-button type="primary" @click.native="handler">提交</x-button>
  </div>
</template>

<script>
import { Cell, Group, XDialog, Step, StepItem, XButton, TransferDom } from 'vux';
import { mapState } from 'vuex';
import _ from 'lodash';

export default {
  directives: {
    TransferDom,
  },
  components: {
    Group,
    Cell,
    XDialog,
    XButton,
    Step,
    StepItem,
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
      step: 0,
      showContent001: false,
      showContent002: false,
      showContent003: false,
      show: false,
      show1: false,
      show2: false,
      data: '/static/images/add.png',
      data1: '/static/images/add.png',
      data2: '/static/images/add1.png',
      path: '',
      path1: '',
      path2: '',
      width: '100%',
      height: '100%',
      headerImage: '',
    };
  },
  methods: {
    back() {
      this.$router.push('/authentication_step1');
    },
    select(index) {
      if (index === 1) {
        this.show = true;
      } else if (index === 2) {
        this.show1 = true;
      } else if (index === 3) {
        this.show2 = true;
      }
    },
    change(index, e) {
      const files = e.target.files || e.dataTransfer.files;
      if (!files.length) return;
      const thisOne = this;
      const reader = new FileReader();
      reader.onload = function get() {
        const image = new Image();
        image.src = this.result;
        image.onload = function getImg() {
          thisOne.headerImage = thisOne.getCanvas(image).toDataURL();

          if (index === 1) {
            thisOne.data = image.src;
            thisOne.path = _.split(thisOne.headerImage, ',')[1];
          } else if (index === 2) {
            thisOne.data1 = image.src;
            thisOne.path1 = _.split(thisOne.headerImage, ',')[1];
          } else if (index === 3) {
            thisOne.data2 = image.src;
            thisOne.path2 = _.split(thisOne.headerImage, ',')[1];
          }
          thisOne.$vux.toast.show({ text: '上传成功', type: 'success', width: '10em' });
        };
      };
      reader.readAsDataURL(files[0]);
    },
    getCanvas(sourceCanvas) {
      const canvas = document.createElement('canvas');
      const context = canvas.getContext('2d');
      const width = 300;
      const height = 300;

      canvas.width = width;
      canvas.height = height;

      context.drawImage(sourceCanvas, 0, 0, width, height);
      return canvas;
    },
    async handler() {
      const { code, message } = (await this.$http.post('/api/mobile/v1/auth/userrealnameauth',
        { id: this.key_user_info.id, userPid: this.$route.params.id, userIcFront: this.path, userIcBack: this.path1, userIcGroup: this.path2, updateUser: this.key_user_info.loginName })).body;
      if (code !== '200') {
        this.$vux.toast.show({ text: message, type: 'cancel', width: '10em' });
      } else {
        this.$vux.toast.show({ text: '资料上传成功', type: 'success', width: '10em' });
        this.$router.push({
          name:'/',
          params:{ state:'TOAUTHORIZED' }
        });
      }
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
    margin: 0px auto;
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
    width:40vw;
    height:40vw;
    display: inline-block;
    margin: 10px 10px;
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
  .weui-btn {
    width:80%;
    margin:20px auto;
  }
  >>>.vux-step-item {
    width:100%;
  }
  >>>.vux-step-item-head-inner {
    border: 1px solid #09bb07!important;
    color: #FFF!important;
    background: #09bb07 none repeat scroll 0 0!important;
  }
  >>>.vux-step-item-head {
    margin: 2px 10px;
  }
  .line {
    width: 30%;
    height:1px;
    clear:both;
    border-top:1px solid #888;
    position: absolute;
    top:14%;
  }
  >>>.vux-step-item-main {
    font-weight: bold;
    color: #666;
    margin:0 10%;
    width:80%;
    height:80px;
  }
  >>>.vux-step-item-title {
    font-size: 18px;
  }
  >>>.vux-step-item-description {
    margin:10px 50px;
    font-size:15px;
  }
</style>
