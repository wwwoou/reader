import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import MainLayout from '@/layout/MainLayout.vue'
import { ElMessage } from 'element-plus'

// 白名单路由
const whiteList = ['/login', '/register']

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue')
  },
  {
    path: '/dashboard',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue')
      },
      {
        path: '/books',
        name: 'Books',
        component: () => import('@/views/books/index.vue')
      },
      {
        path: '/books/add',
        name: 'AddBook',
        component: () => import('@/views/books/AddBook.vue')
      },
      {
        path: '/books/:id',
        name: 'BookDetail',
        component: () => import('@/views/books/BookDetail.vue')
      },
      {
        path: '/borrows',
        name: 'Borrows',
        component: () => import('@/views/borrows/index.vue')
      },
      {
        path: '/borrows/add',
        name: 'AddBorrow',
        component: () => import('@/views/borrows/AddBorrow.vue')
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 修改路由守卫逻辑
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  // 如果已登录
  if (token) {
    if (to.path === '/login' || to.path === '/register') {
      // 如果已登录且要跳转的页面是登录或注册页，则重定向到首页
      next('/')
    } else {
      // 检查是否有访问权限
      const requiresAdmin = to.meta.requiresAdmin
      if (requiresAdmin && !userStore.isAdmin) {
        ElMessage.error('没有权限访问该页面')
        next('/dashboard')
      } else {
        next()
      }
    }
  } else {
    // 如果未登录
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

export default router 