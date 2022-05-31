const { defineConfig } = require("@vue/cli-service");
console.log("process.env.NODE_ENV", process.env.NODE_ENV);
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      "/magic_transform": {
        target: "http://47.114.110.56:7777/",
        ws: true,
        changeOrigin: true,
      },
      "/spider-status": {
        target: "http://47.114.110.56:8082/",
        ws: true,
        changeOrigin: true,
      },
      "/run-spider": {
        target: "http://47.114.110.56:8082/",
        ws: true,
        changeOrigin: true,
      },
    },
  },
});
// pathRewrite: {
//   "^/api": "/",
// },
