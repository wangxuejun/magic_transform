<template>
  <div class="page">
    <div class="login-box">
      <div class="left">
        <span>
          <i class="el-icon-user-solid"></i>
        </span>
        <h1>后台管理系统</h1>
      </div>
      <div class="right">
        <el-input
          prefix-icon="el-icon-user"
          v-model="user_name"
          placeholder="请输入用户名"
        ></el-input>
        <el-input
          prefix-icon="el-icon-lock"
          type="password"
          v-model="password"
          placeholder="请输入密码"
        ></el-input>
        <el-button type="primary" @click="login">登录</el-button>
      </div>
    </div>
  </div>
</template>
<script>
import { login } from "@/api";
import { Message, MessageBox } from "element-ui";
export default {
  data() {
    return {
      user_name: "",
      password: "",
    };
  },
  methods: {
    login() {
      let { user_name, password } = this;
      if (!user_name) {
        Message({
          type: "warning",
          message: "请输入用户名",
        });
        return;
      }
      if (!password) {
        Message({
          type: "warning",
          message: "请输入密码",
        });
        return;
      }
      login({ user_name, password }, (res) => {
        console.log("res", res);
        let { jwt, user_id, role } = res.data.data;
        if (role === "common") {
          MessageBox({
            type: "error",
            title: "提示",
            message: "对不起，您无权进入系统！",
            duration: 700,
          });
          return;
        }
        sessionStorage.setItem("user_name", user_name);
        sessionStorage.setItem("user_id", user_id);
        sessionStorage.setItem("role", role);
        sessionStorage.setItem("Authorization", "Bearer" + jwt);
        Message({
          type: "success",
          message: `您好,${user_name},欢迎进入！`,
          duration: 700,
        });
        setTimeout(() => {
          this.$router.push({
            path: "/admin/userList",
          });
        }, 700);
      });
    },
  },
};
</script>
<style scoped lang="less">
.page {
  width: 100vw;
  height: 100vh;
}
.login-box {
  width: 600px;
  height: 350px;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  box-shadow: rgba(100, 100, 111, 0.2) 0 7px 29px 0;
  border-radius: 6px;
  display: flex;
  background-image: linear-gradient(to left, #accbee 0%, #e7f0fd 100%);
  .left {
    height: 100%;
    width: 300px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    span {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      background-color: #409eff;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      i {
        font-size: 24px;
        color: white;
      }
    }
    h1 {
      margin-top: 30px;
    }
  }
  .right {
    background-color: white;
    width: 300px;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 0 20px;
    .el-input {
      margin-bottom: 30px;
    }
    .el-button {
      width: 100%;
    }
  }
}
</style>
