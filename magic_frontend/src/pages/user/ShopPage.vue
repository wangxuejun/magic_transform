<template>
  <div class="shopPage">
    <el-form class="form-box" ref="ruleForm" :model="form" label-width="80px">
      <el-form-item label="爬取网址" prop="userName">
        <el-input
          v-model="form.spider_url"
          type="text"
          placeholder="请输入目标网址"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="爬取数量" prop="userName">
        <el-input
          v-model="form.good_count"
          type="number"
          placeholder="请输入爬取数量，默认30"
          clearable
        ></el-input>
      </el-form-item>
    </el-form>
    <div class="btns">
      <el-button style="width: 400px" type="primary" @click="submit"
        >爬取</el-button
      >
      <el-button style="width: 400px" type="success" @click="write"
        >写入</el-button
      >
    </div>
  </div>
</template>
<script>
// eslint-disable-next-line no-unused-vars
import { setShopUrl, getShopStatue, getWrite, setWrite } from "@/api";
import { MessageBox, Message } from "element-ui";
export default {
  data() {
    return {
      form: {
        spider_url: "",
        user_id: "",
        good_count: 30,
      },
    };
  },
  mounted() {
    let user_id = sessionStorage.getItem("user_id");
    if (!user_id) {
      MessageBox({
        title: "提示",
        message: "请重新登录",
        type: "warning",
      });
    } else {
      this.form.user_id = user_id;
    }
  },
  methods: {
    submit() {
      this.getStatue();
    },
    // 获取爬虫状态
    getStatue() {
      let { spider_url, user_id } = this.form;
      if (!spider_url) {
        Message({
          title: "提示",
          message: "请输入网址",
          type: "warning",
        });
        return;
      }
      getShopStatue({ q: user_id }, (res) => {
        console.log("res", res);
        let statue = res.data;
        if (statue === "not started" || statue === "end") {
          this.getWriteStatue()
            .then(() => {
              this.setShop();
            })
            .catch((num) => {
              MessageBox({
                title: "提示",
                message: `数据正在执行写入中，当前剩余${num}条，请稍后再提交`,
                type: "error",
              });
            });
        } else {
          MessageBox({
            title: "提示",
            message: "当前爬虫正在运行，请稍后再提交",
            type: "error",
          });
        }
      });
    },
    // 提交爬虫
    setShop() {
      let { form } = this;
      setShopUrl(form, (res) => {
        console.log(res);
        Message({
          title: "提示",
          message: "操作成功",
          type: "success",
        });
      });
    },
    // 获取写入状态
    getWriteStatue() {
      return new Promise((resolve, reject) => {
        getWrite({}, (res) => {
          console.log("res", res);
          if (res.data.data === 0) {
            resolve();
          } else {
            reject(res.data.data);
          }
        });
      });
    },
    write() {
      let { user_id } = this.form;
      setWrite({ user_id }, (res) => {
        console.log("res", res);
        Message({
          title: "提示",
          message: "操作成功",
          type: "success",
        });
      });
    },
  },
};
</script>
<style scoped lang="less">
.shopPage {
}
.btns {
  text-align: center;
}
.el-input {
  margin-bottom: 30px;
}
.el-button {
  margin-top: 20px;
}
</style>
