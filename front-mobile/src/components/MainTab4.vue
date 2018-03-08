<template>
  <div>
    个人中心

    <x-button type="primary" class="btn-normal" @click.native="handleLogout">退出</x-button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      msg: 'Welcome to Your Vue.js App',
    };
  },
  methods: {
    async handleLogout() {
      this.$vux.confirm.show({
        title: '确认退出',
        content: '确认需要退出系统？',
        onConfirm: async () => {
          await this.$store.commit('logout');
          this.$router.replace('/login');
        },
      });
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
