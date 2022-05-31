import { Loading } from "_element-ui@2.15.8@element-ui";

let loadingDom = null;
let loadingNum = 0;
export function showFullScreenLoading() {
  loadingNum++;
  loadingDom = Loading.service({
    fullscreen: true,
    spinner: "el-icon-loading",
    text: "加载中...",
    background: "rgba(0,0,0,0.6)",
    customClass: "loading-box",
  });
}
export function tryHideFullScreenLoading() {
  loadingNum--;
  if (loadingNum === 0 && Loading) {
    loadingDom.close();
  }
}
