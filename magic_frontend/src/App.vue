<template>
  <div id="app">
    <router-view />
  </div>
</template>
<script>
import { login } from "@/api";
export default {
  methods: {
    logi() {
      login();
    },
  },
  mounted() {},
};
</script>

<style lang="less">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
* {
  padding: 0;
  margin: 0;
}
</style>
