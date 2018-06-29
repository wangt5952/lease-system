<template>
  <div>
    <div class="head">
      <div class="left"><a @click="back"><i slot="icon" class="iconfont icon-fanhui"></i></a></div>
      <div class="tlte"><span>个人资料</span></div>
    </div>

    <group>
      <cell title="用户头像" :link="`/upload?userIcon=${key_user_info.userIcon}`">
        <div class="icon_val">
          <i slot="icon" class="iconfont icon-shenfenzheng"></i>
        </div>
      </cell>
      <cell title="用户类型" :value="this.u_type.value"></cell>
      <cell title="用户" :value="this.key_user_info.loginName"></cell>
      <cell title="昵称" :value="this.key_user_info.nickName" @click="goNickName" :link="'/nickName/'+ key_user_info.nickName"></cell>
      <cell title="身份证号" :value="this.key_user_info.userPid"></cell>
      <cell title="手机号" :value="this.key_user_info.userMobile"></cell>
      <cell title="所属企业" :value="this.key_user_info.orgName"></cell>
      <cell title="用户状态" :value="this.u_status.value"></cell>
    </group>

  </div>
</template>

<script>
import { Cell, Group } from 'vux';
import { mapState } from 'vuex';
import _ from 'lodash';

const user_type = [
  {
    key: 'PLATFORM',
    value: '平台',
  }, {
    key: 'ENTERPRISE',
    value: '企业',
  }, {
    key: 'INDIVIDUAL',
    value: '个人',
  },
];
const user_status = [
  {
    key: 'NORMAL',
    value: '正常',
  }, {
    key: 'FREEZE',
    value: '冻结/维保',
  }, {
    key: 'INVALID',
    value: '作废',
  },
];
export default {
  components: {
    Group,
    Cell,
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,

      relogin: state => state.relogin,
    }),
  },
  data() {
    return {
      u_type: '',
      u_status: '',
    };
  },
  methods: {
    back() {
      // window.history.go(-1);
       this.$router.push('/');
    },
    goNickName() {
      this.$router.push(`'/nickName/'${this.key_user_info.nickName}`);
    }
  },
  async mounted() {
    this.u_type = _.find(user_type, { key: this.key_user_info.userType });
    this.u_status = _.find(user_status, { key: this.key_user_info.userStatus });
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
  .icon_val {
    display: inline-block!important;
  }
  .icon_val .iconfont {
    font-size: 17pt;
  }
</style>
