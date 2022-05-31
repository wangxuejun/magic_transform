<template>
  <div>
    <el-table :data="list" style="width: 100%">
      <el-table-column label="用户" width="180">
        <template slot-scope="scope">
          <p>{{ scope.row.userName }}</p>
        </template>
      </el-table-column>
      <el-table-column label="过期时间" width="180">
        <template slot-scope="scope">
          <p>{{ scope.row.exprTime }}</p>
        </template>
      </el-table-column>
      <el-table-column label="身份" width="180">
        <template slot-scope="scope">
          <p>{{ scope.row.userRole }}</p>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)"
            >编辑</el-button
          >
          <el-button
            v-if="scope.row.id !== 1"
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="page_size"
      :current-page="page_no"
      @current-change="changePage"
    >
    </el-pagination>
  </div>
</template>
<script>
import { MessageBox, Message } from "element-ui";
import { getUserList, deleteUserList } from "@/api";
export default {
  data() {
    return {
      list: [],
      page_no: 1,
      page_size: 5,
      total: 0,
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getList() {
      let { page_size, page_no } = this;
      getUserList({ page_no, page_size }, (res) => {
        console.log("res", res);
        let { users, total } = res.data.data;
        this.list = users;
        this.total = total;
      });
    },
    deltUser(user_id, index) {
      deleteUserList({ user_id }, (res) => {
        console.log("res", res);
        Message({
          title: "提示",
          message: "删除成功",
          type: "success",
          showCancelButton: true,
        });
        this.list.splice(index, 1);
      });
    },
    handleEdit(index, row) {
      console.log(index, row);
      sessionStorage.setItem("currentUser", JSON.stringify(row));
      this.$router.push({
        path: "/admin/userEditor",
        query: {
          id: row.id,
        },
      });
    },
    // 删除用户
    handleDelete(index, row) {
      console.log(index, row);
      MessageBox({
        title: "确认删除",
        message: "确认要删除该账户吗？",
        type: "warning",
        showCancelButton: true,
      })
        .then((res) => {
          console.log(res);
          this.deltUser(row.id, index);
        })
        .catch(() => {});
    },
    changePage(e) {
      this.page_no = e;
      this.getList();
    },
  },
};
</script>
