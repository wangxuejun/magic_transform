<template>
  <div class="layputPage">
    <el-container class="container">
      <el-header class="header">
        <span>后台管理系统</span>
        <span></span>
        <i class="el-icon-user-solid"></i>
        <span class="user">{{ user_name }}</span>
        <span class="loginout" @click="loginOut">退出</span>
      </el-header>
      <el-container>
        <el-aside class="aside" width="200px">
          <el-menu
            :router="true"
            :default-active="current"
            class="el-menu-vertical-demo"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <el-submenu index="user">
              <template slot="title">
                <i class="el-icon-menu"></i>
                <span>用户管理</span>
              </template>
              <el-menu-item index="userList">
                <template slot="title">
                  <span>用户列表</span>
                </template>
              </el-menu-item>
              <el-menu-item index="userEditor">
                <span>添加用户</span>
              </el-menu-item>
              <el-menu-item index="userShop">
                <span>爬虫</span>
              </el-menu-item>
            </el-submenu>
          </el-menu>
        </el-aside>
        <el-container>
          <el-main class="main"><router-view /></el-main>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>
<script>
import { Message } from "element-ui";
export default {
  data() {
    return {
      user_name: "",
      current: "userList",
    };
  },
  methods: {
    loginOut() {
      sessionStorage.removeItem("user_name");
      sessionStorage.removeItem("user_id");
      sessionStorage.removeItem("role");
      sessionStorage.removeItem("Authorization");
      this.$router.push({
        path: "/login",
      });
    },
  },
  mounted() {
    let user_name = sessionStorage.getItem("user_name");
    this.user_name = user_name;
  },
  watch: {
    $route: {
      handler(n) {
        console.log("更新路由");
        let user_id = sessionStorage.getItem("user_id");
        if (!user_id) {
          Message({
            type: "warning",
            message: "请重新登录！",
          });
          this.$router.push({
            path: "/login",
          });
          return;
        }
        let { fullPath } = n;
        let path = fullPath.split("/");
        this.current = path[path.length - 1];
      },
      immediate: true,
    },
  },
};
</script>
<style scoped lang="less">
.layputPage {
  width: 100vw;
  height: 100vh;
}
.header {
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  box-shadow: rgba(0, 0, 0, 0.1) 0px 10px 15px -3px,
    rgba(0, 0, 0, 0.05) 0px 4px 6px -2px;
  span:nth-child(2) {
    flex: 1;
  }
  i {
    margin-right: 10px;
  }
  .user {
    margin-right: 30px;
    font-weight: bold;
  }
  .loginout {
    cursor: pointer;
  }
}
.container {
  height: 100vh;
}
.aside {
  box-shadow: rgba(0, 0, 0, 0.15) 2.4px 2.4px 3.2px;
  background-color: #545c64;
  .el-menu {
    height: 100%;
  }
}
.main {
}
</style>
