<template>
  <div class="add-page">
    <el-form
      class="form-box"
      ref="ruleForm"
      :model="user"
      label-width="80px"
      :rules="rules"
    >
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="user.userName" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="user.password" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="repetPassword">
        <el-input
          v-model="user.repetPassword"
          placeholder="请确认密码"
        ></el-input>
      </el-form-item>
      <el-form-item label="用户类型" prop="userRole">
        <el-select v-model="user.userRole" placeholder="请选择活动区域">
          <el-option label="admin" value="admin"></el-option>
          <el-option label="common" value="common"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="爬虫账号" prop="shopUsername">
        <el-input
          v-model="user.shopUsername"
          placeholder="请输入爬虫账号"
        ></el-input>
      </el-form-item>
      <el-form-item label="爬虫密码" prop="shopPassword">
        <el-input
          v-model="user.shopPassword"
          placeholder="请输入爬虫密码"
        ></el-input>
      </el-form-item>
      <el-form-item label="过期时间" prop="exprTime">
        <el-date-picker
          v-model="user.exprTime"
          type="datetime"
          placeholder="选择日期时间"
          value-format="yyyy-MM-dd hh:mm:ss"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{
          mode === "add" ? "立即创建" : "修改"
        }}</el-button>
        <el-button @click="cancle">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
// eslint-disable-next-line no-unused-vars
import { addUserList, updateUserList } from "@/api";
import { Message } from "element-ui";
export default {
  data() {
    let validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入密码"));
      } else {
        if (this.user.repetPassword !== "") {
          this.$refs.ruleForm.validateField("repetPassword");
        }
        callback();
      }
    };
    let validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.user.password) {
        callback(new Error("两次输入密码不一致!"));
      } else {
        callback();
      }
    };
    return {
      mode: "",
      user: {
        userName: "",
        password: "",
        repetPassword: "",
        userRole: "",
        shopUsername: "",
        shopPassword: "",
        exprTime: "",
      },
      rules: {
        userName: [
          { required: true, message: "请输入用户名", trigger: "blur" },
        ],
        password: [
          { required: true, validator: validatePass, trigger: "blur" },
        ],
        repetPassword: {
          required: true,
          validator: validatePass2,
          trigger: "blur",
        },
        userRole: [
          {
            required: true,
            message: "请选择账户类型",
            trigger: "change",
          },
        ],
        shopUsername: [
          { required: true, message: "请输入爬虫账号", trigger: "blur" },
        ],
        shopPassword: [
          { required: true, message: "请输入爬虫密码", trigger: "blur" },
        ],
        // exprTime: [
        //   {
        //     type: "time",
        //     required: true,
        //     message: "请选择过期时间",
        //     trigger: "change",
        //   },
        // ],
      },
    };
  },
  mounted() {
    let id = this.$route.query.id;
    let user = JSON.parse(sessionStorage.getItem("currentUser"));
    if (id) {
      this.mode = "update";
      if (+user.id !== +id) {
        // url中的id和缓存中的id不一致，则返回列表
        this.$router.push({
          path: "/admin/userList",
        });
      } else {
        user.repetPassword = user.password;
        this.user = user;
      }
    } else {
      this.mode = "add";
    }
  },
  methods: {
    add() {
      let { user } = this;
      console.log("user", user);
      addUserList(user, (res) => {
        console.log("res", res);
        Message({
          message: "添加成功",
          type: "success",
        });
        Object.keys(this.user).forEach((key) => {
          this.user[key] = "";
        });
        this.$router.push({
          path: "/admin/userList",
        });
      });
    },
    update() {
      let { user } = this;
      updateUserList(user, (res) => {
        console.log(res);
        Message({
          message: "修改成功",
          type: "success",
        });
      });
    },
    onSubmit() {
      let { mode } = this;
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          if (mode === "add") {
            this.add();
          } else {
            this.update();
          }
        } else {
          return false;
        }
      });
    },
    cancle() {
      this.$router.push({
        path: "/admin/userList",
      });
    },
  },
};
</script>
<style scoped lang="less">
.add-page {
  width: 100%;
}
.form-box {
  width: 700px;
}
</style>
