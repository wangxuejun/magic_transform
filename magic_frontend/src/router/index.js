import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/login",
  },
  {
    path: "/login",
    name: "LoginPage",
    component: () =>
      import(/* webpackChunkName: "LoginPage" */ "../pages/LoginPage.vue"),
  },
  {
    path: "/admin",
    name: "LayoutPage",
    redirect: "/admin/userList",
    component: () =>
      import(/* webpackChunkName: "LayoutPage" */ "../pages/LayoutPage.vue"),
    children: [
      {
        path: "userList",
        name: "UserListPage",
        component: () =>
          import(
            /* webpackChunkName: "UserListPage" */ "../pages/user/ListPage.vue"
          ),
      },
      {
        path: "userEditor",
        name: "UserEditorPage",
        component: () =>
          import(
            /* webpackChunkName: "UserEditorPage" */ "../pages/user/EditorPage.vue"
          ),
      },
      {
        path: "userShop",
        name: "UserShopPage",
        component: () =>
          import(
            /* webpackChunkName: "UserShopPage" */ "../pages/user/ShopPage.vue"
          ),
      },
    ],
  },
];

const router = new VueRouter({
  routes,
});

export default router;
