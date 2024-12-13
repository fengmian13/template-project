import { createRouter, createWebHistory,createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: '登录',
      component: () => import('../views/Login.vue')
    },{
      path: '/',
      name: 'layout',
      component: () => import('../views/Layout.vue'),
      children: [{
        path: '/home',
        name: '首页',
        component: () => import('../views/home/Home.vue')
      }, {
        path: "/content/category",
        name: "分类管理",
        component: () => import('../views/content/CategoryList.vue'),
      }, {
        path: "/setting/menu",
        name: "菜单管理",
        component: () => import('../views/setting/MenuList.vue'),
      }, {
        path: "/setting/role",
        name: "角色管理",
        component: () => import('../views/setting/RoleList.vue'),
      }, {
        path: "/setting/user",
        name: "系统管理",
        component: () => import('../views/setting/UserList.vue'),
      }]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userInfo = sessionStorage.getItem('userInfo')
  if(!userInfo && to.path !== '/login'){
    router.push('/login')
  }
  next();
})

export default router
