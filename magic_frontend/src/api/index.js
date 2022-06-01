import http from "./axios";
import { BASE, SHOP, SHOPSET, cb, config } from "./base";

// 判断是否参加活动
export function login(params, callback) {
  let { user_name, password } = params;
  let query = `?user_name=${user_name}&password=${password}`;
  http(config(BASE + `user/login${query}`, {}, "POST")).then((res) =>
    cb(res, callback)
  );
}
// 用户列表
export function getUserList(params, callback) {
  http(config(BASE + "user/list", params, "GET", true, true)).then((res) =>
    cb(res, callback)
  );
}
// 添加用户
export function addUserList(params, callback) {
  http(config(BASE + "user/add", params, "POST", true, true)).then((res) =>
    cb(res, callback)
  );
}
// 删除用户
export function deleteUserList(params, callback) {
  http(config(BASE + "user/delete", params, "GET", true, true)).then((res) =>
    cb(res, callback)
  );
}
// 修改用户
export function updateUserList(params, callback) {
  http(config(BASE + "user/update", params, "POST", true, true)).then((res) =>
    cb(res, callback)
  );
}
// 获取写入状态
export function getWrite(params, callback) {
  http(config(BASE + "transform/get-wirtes", params, "GET", true, true)).then(
    (res) => cb(res, callback)
  );
}
// 执行写入
export function setWrite(params, callback) {
  let { user_id } = params;
  let query = `transform/write?user_id=${user_id}`;
  http(config(BASE + query, params, "POST", true, true)).then((res) =>
    cb(res, callback)
  );
}
// 提交爬虫
export function setShopUrl(params, callback) {
  http(config(SHOPSET, params, "POST", true, false)).then((res) =>
    cb(res, callback)
  );
}
// 爬虫状态
export function getShopStatue(params, callback) {
  http(config(SHOP, params, "GET", true, false)).then((res) =>
    cb(res, callback)
  );
}
