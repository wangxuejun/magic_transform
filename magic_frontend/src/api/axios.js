import axios from "axios";
import { showFullScreenLoading, tryHideFullScreenLoading } from "./loading";
import { Message } from "element-ui";

let http = axios.create({
  baseURL: "",
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
  transformRequest: [
    function (data) {
      return data;
    },
  ],
});
http.defaults.timeout = 25000;
http.interceptors.request.use(
  function (config) {
    let url = config.url;
    // get参数编码
    if (config.method === "get" && config.params) {
      url += "?";
      let keys = Object.keys(config.params);
      for (let key of keys) {
        if (config.params[key]) {
          url += `${key}=${encodeURIComponent(config.params[key])}&`;
        }
      }
      url = url.substring(0, url.length - 1);
      config.params = {};
    }
    config.url = url;
    if (config.setAuthor) {
      let Authorization = sessionStorage.getItem("Authorization");
      config.headers.Authorization = Authorization;
    }
    if (!config.showLoading && config.loading) {
      config.showLoading = true;
      showFullScreenLoading();
    }
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);
http.interceptors.response.use(
  function (response) {
    if (response.config.showLoading) {
      tryHideFullScreenLoading();
    }
    let code = response.data.code;
    if (!code) {
      // 没有状态直接返回
      return response;
    }
    let codes = [200];
    if (codes.indexOf(code) >= 0) {
      return response;
    } else {
      if (response.config.loading) {
        Message({
          message: response.data.message,
          type: "error",
        });
      }
      return response;
    }
  },
  function (response) {
    if (response.config.loading) {
      Message({
        message: "请求失败",
        type: "error",
      });
    }
    tryHideFullScreenLoading();
  }
);

export default http;
