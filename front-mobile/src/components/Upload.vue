<template>
  <div id="demo">

    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>修改头像</span></div>
    </div>

    <div class="container" v-show="panel">
      <div>
        <img id="image" :src="url" alt="Picture">
      </div>

      <button type="button" id="button" @click="crop">确定</button>
    </div>

    <div style="padding:20px;">
        <div class="show">
          <div class="picture" :style="'backgroundImage:url('+headerImage+')'">
          </div>
        </div>
        <div style="margin-top:20px;">
          <a href="javascript:;" class="upload"><input type="file" accept="image" @change="change">上传头像</a>
        </div>
    </div>

    <x-button type="primary" @click.native="handler" class="tj">提交</x-button>
  </div>
</template>

<script>
import Cropper from 'cropperjs';
import { XButton } from 'vux';
import { mapState } from 'vuex';
import _ from 'lodash';

export default {
  components: {
    XButton,
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
      relogin: state => state.relogin,
    }),
  },
  data() {
    return {
      headerImage: '',
      picValue: '',
      cropper: {},
      croppable: false,
      panel: false,
      url: '',
      path: '',
      website: 'http://106.14.172.38:8990/leaseupload/usericon/',
    };
  },
  created() {
    if (this.key_user_info.userIcon) {
      this.headerImage = this.key_user_info.userIcon.includes(this.website) ? this.$route.query.userIcon : this.website + this.$route.query.userIcon;
    } else {
      this.headerImage = '/static/images/users/1.jpg';
    }
  },
  mounted() {
    const self = this;
    const image = document.getElementById('image');
    this.cropper = new Cropper(image, {
      aspectRatio: 1,
      viewMode: 1,
      background: false,
      zoomable: false,
      ready: () => { self.croppable = true; },
    });
  },
  methods: {
    back() {
      window.history.go(-1);
    },
    getObjectURL(file) {
      let url = null;
      if (window.createObjectURL !== undefined) {
        url = window.createObjectURL(file);
        url = window.URL.createObjectURL(file);
      } else if (window.webkitURL !== undefined) {
        url = window.webkitURL.createObjectURL(file);
      }
      return url;
    },
    change(e) {
      // 图片大小不能超过5M (1024字节=1kb,1024KB=1MB,1024MB=1GB,1024GB=1TB)
      if (e.target.files[0].size > 5 * 1024 * 1024) {
        this.$vux.toast.show({ text: '图片不能超过5M', type: 'warn', width: '10em' });
        return;
      }
      const files = e.target.files || e.dataTransfer.files;
      if (!files.length) return;
      this.panel = true;
      this.picValue = files[0];
      this.url = this.getObjectURL(this.picValue);
      if (this.cropper) {
        this.cropper.replace(this.url);
      }
      this.panel = true;
    },
    crop() {
      this.panel = false;
      if (!this.croppable) return;
      // Crop
      const croppedCanvas = this.cropper.getCroppedCanvas();

      // Round
      const roundedCanvas = this.getRoundedCanvas(croppedCanvas);

      this.headerImage = roundedCanvas.toDataURL();
    },
    // 画布
    getRoundedCanvas(sourceCanvas) {
      const canvas = document.createElement('canvas');
      const context = canvas.getContext('2d');
      // 把画布原来的 宽高 缩小6倍(宽 高大小根据实际情况而定)
      const width = 200;
      const height = 200;

      canvas.width = width;
      canvas.height = height;

      context.imageSmoothingEnabled = true;
      context.drawImage(sourceCanvas, 0, 0, width, height);
      context.globalCompositeOperation = 'destination-in';
      context.beginPath();
      context.arc(width / 2, height / 2, Math.min(width, height) / 2, 0, 2 * Math.PI, true);
      context.fill();
      return canvas;
    },

    async handler() {
      this.path = _.split(this.headerImage, ',')[1];
      const { code, message, respData } = (await this.$http.post('/api/mobile/v1/auth/uplodeusericon',
        { id: this.key_user_info.id, userIcon: this.path, updateUser: this.key_user_info.loginName })).body;
      if (code !== '200') throw new Error(message || code);
      if (respData) {
        this.key_user_info.userIcon = respData;
        localStorage.setItem('key_user_info', JSON.stringify(this.key_user_info));
        this.$vux.toast.show({ text: '提交成功', type: 'success', width: '10em', time: '100' });
        setTimeout(() => { this.$router.replace('/'); }, 200);
      }
    },
  },
};
</script>

<style>
*{
  margin: 0;
  padding: 0;
}
#demo .head {
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
#demo #button {
  position: absolute;
  right: 10px;
  top: 10px;
  width: 80px;
  height: 40px;
  border:none;
  border-radius: 5px;
  background:white;
}
#demo .show {
  width: 100px;
  height: 100px;
  overflow: hidden;
  position: relative;
  border-radius: 50%;
  border: 1px solid #d5d5d5;
}
#demo .picture {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-position: center center;
  background-repeat: no-repeat;
  background-size: cover;
}
#demo .container {
    z-index: 99;
    position: fixed;
    padding-top: 60px;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    background:rgba(0,0,0,1);
}

#demo #image {
  max-width: 100%;
}

.cropper-view-box,.cropper-face {
  border-radius: 50%;
}
/*!
 * Cropper.js v1.0.0-rc
 * https://github.com/fengyuanchen/cropperjs
 *
 * Copyright (c) 2017 Fengyuan Chen
 * Released under the MIT license
 *
 * Date: 2017-03-25T12:02:21.062Z
 */

.cropper-container {
  font-size: 0;
  line-height: 0;

  position: relative;

  -webkit-user-select: none;

     -moz-user-select: none;

      -ms-user-select: none;

          user-select: none;

  direction: ltr;
  -ms-touch-action: none;
      touch-action: none
}

.cropper-container img {
  /* Avoid margin top issue (Occur only when margin-top <= -height) */
  display: block;
  min-width: 0 !important;
  max-width: none !important;
  min-height: 0 !important;
  max-height: none !important;
  width: 100%;
  height: 100%;
  image-orientation: 0deg
}

.cropper-wrap-box,
.cropper-canvas,
.cropper-drag-box,
.cropper-crop-box,
.cropper-modal {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}

.cropper-wrap-box {
  overflow: hidden;
}

.cropper-drag-box {
  opacity: 0;
  background-color: #fff;
}

.cropper-modal {
  opacity: .5;
  background-color: #000;
}

.cropper-view-box {
  display: block;
  overflow: hidden;

  width: 100%;
  height: 100%;

  outline: 1px solid #39f;
  outline-color: rgba(51, 153, 255, 0.75);
}

.cropper-dashed {
  position: absolute;

  display: block;

  opacity: .5;
  border: 0 dashed #eee
}

.cropper-dashed.dashed-h {
  top: 33.33333%;
  left: 0;
  width: 100%;
  height: 33.33333%;
  border-top-width: 1px;
  border-bottom-width: 1px
}

.cropper-dashed.dashed-v {
  top: 0;
  left: 33.33333%;
  width: 33.33333%;
  height: 100%;
  border-right-width: 1px;
  border-left-width: 1px
}

.cropper-center {
  position: absolute;
  top: 50%;
  left: 50%;

  display: block;

  width: 0;
  height: 0;

  opacity: .75
}

.cropper-center:before,
  .cropper-center:after {
  position: absolute;
  display: block;
  content: ' ';
  background-color: #eee
}

.cropper-center:before {
  top: 0;
  left: -3px;
  width: 7px;
  height: 1px
}

.cropper-center:after {
  top: -3px;
  left: 0;
  width: 1px;
  height: 7px
}

.cropper-face,
.cropper-line,
.cropper-point {
  position: absolute;

  display: block;

  width: 100%;
  height: 100%;

  opacity: .1;
}

.cropper-face {
  top: 0;
  left: 0;

  background-color: #fff;
}

.cropper-line {
  background-color: #39f
}

.cropper-line.line-e {
  top: 0;
  right: -3px;
  width: 5px;
  cursor: e-resize
}

.cropper-line.line-n {
  top: -3px;
  left: 0;
  height: 5px;
  cursor: n-resize
}

.cropper-line.line-w {
  top: 0;
  left: -3px;
  width: 5px;
  cursor: w-resize
}

.cropper-line.line-s {
  bottom: -3px;
  left: 0;
  height: 5px;
  cursor: s-resize
}

.cropper-point {
  width: 5px;
  height: 5px;

  opacity: .75;
  background-color: #39f
}

.cropper-point.point-e {
  top: 50%;
  right: -3px;
  margin-top: -3px;
  cursor: e-resize
}

.cropper-point.point-n {
  top: -3px;
  left: 50%;
  margin-left: -3px;
  cursor: n-resize
}

.cropper-point.point-w {
  top: 50%;
  left: -3px;
  margin-top: -3px;
  cursor: w-resize
}

.cropper-point.point-s {
  bottom: -3px;
  left: 50%;
  margin-left: -3px;
  cursor: s-resize
}

.cropper-point.point-ne {
  top: -3px;
  right: -3px;
  cursor: ne-resize
}

.cropper-point.point-nw {
  top: -3px;
  left: -3px;
  cursor: nw-resize
}

.cropper-point.point-sw {
  bottom: -3px;
  left: -3px;
  cursor: sw-resize
}

.cropper-point.point-se {
  right: -3px;
  bottom: -3px;
  width: 20px;
  height: 20px;
  cursor: se-resize;
  opacity: 1
}

@media (min-width: 768px) {

  .cropper-point.point-se {
    width: 15px;
    height: 15px
  }
}

@media (min-width: 992px) {

  .cropper-point.point-se {
    width: 10px;
    height: 10px
  }
}

@media (min-width: 1200px) {

  .cropper-point.point-se {
    width: 5px;
    height: 5px;
    opacity: .75
  }
}

.cropper-point.point-se:before {
  position: absolute;
  right: -50%;
  bottom: -50%;
  display: block;
  width: 200%;
  height: 200%;
  content: ' ';
  opacity: 0;
  background-color: #39f
}

.cropper-invisible {
  opacity: 0;
}

.cropper-bg {
  background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQAQMAAAAlPW0iAAAAA3NCSVQICAjb4U/gAAAABlBMVEXMzMz////TjRV2AAAACXBIWXMAAArrAAAK6wGCiw1aAAAAHHRFWHRTb2Z0d2FyZQBBZG9iZSBGaXJld29ya3MgQ1M26LyyjAAAABFJREFUCJlj+M/AgBVhF/0PAH6/D/HkDxOGAAAAAElFTkSuQmCC');
}

.cropper-hide {
  position: absolute;

  display: block;

  width: 0;
  height: 0;
}

.cropper-hidden {
  display: none !important;
}

.cropper-move {
  cursor: move;
}

.cropper-crop {
  cursor: crosshair;
}

.cropper-disabled .cropper-drag-box,
.cropper-disabled .cropper-face,
.cropper-disabled .cropper-line,
.cropper-disabled .cropper-point {
  cursor: not-allowed;
}

.upload {
  position: relative;
  display: block;
  cursor: pointer;
  margin-left: auto;
  margin-right: auto;
  padding-left: 14px;
  padding-right: 14px;
  box-sizing: border-box;
  font-size: 18px;
  text-align: center;
  text-decoration: none;
  color: #fff;
  line-height: 2.33333333;
  border-radius: 5px;
  overflow: hidden;
  background-color: #1AAD19;
}
.upload input {
  width:100%;
  height:100%;
  position: absolute;
  font-size: 100px;
  left:0;
  top:0;
  opacity: 0;
  cursor: pointer;
}
.tj {
  width:89%!important;
}
</style>
