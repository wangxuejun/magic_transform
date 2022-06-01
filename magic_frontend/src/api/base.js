let BASE, SHOP, SHOPSET;
switch (process.env.NODE_ENV) {
  case "production":
    BASE = "http://47.114.110.56:7777/magic_transform/magic/";
    SHOP = "http://47.114.110.56:8082/spider-status/";
    SHOPSET = "http://47.114.110.56:8082/run-spider/";
    break;
  default:
    BASE = "/magic_transform/magic/";
    SHOP = "/spider-status";
    SHOPSET = "/run-spider";
}
function config(
  url,
  params,
  method = "GET",
  loading = true,
  setAuthor = false
) {
  return {
    method: method,
    url: url,
    data: method === "POST" || method === "PUT" ? JSON.stringify(params) : null,
    params: method === "GET" || method === "DELETE" ? params : null,
    loading: loading,
    setAuthor: setAuthor,
  };
}

let cb = (res, callback) => {
  if (res && res.data) {
    if (typeof callback === "function") callback(res);
  }
};

export { BASE, SHOP, SHOPSET, config, cb };
