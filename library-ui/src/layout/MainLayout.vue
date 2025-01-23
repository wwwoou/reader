<template>
  <el-container class="layout-container">
    <!-- 侧边栏导航 -->
    <el-aside width="200px">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/books">
          <el-icon><Reading /></el-icon>
          <span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/borrows">
          <el-icon><List /></el-icon>
          <span>借阅管理</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header height="60px">
        <div class="header-left">
          <h2>图书管理系统</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ username }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Odometer, Reading, List, User, ArrowDown } from '@element-plus/icons-vue'

// 获取路由和用户状态
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 计算属性：当前用户名和激活的菜单项
const username = computed(() => userStore.username)
const activeMenu = computed(() => route.path)

// 处理下拉菜单命令
const handleCommand = async (command) => {
  if (command === 'profile') {
    // 跳转到个人中心
    router.push('/profile')
  } else if (command === 'logout') {
    // 退出登录
    await userStore.logoutUser()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  
  .el-aside {
    // 侧边栏样式
    .el-menu {
      height: 100%;
      border-right: none;
    }
  }
  
  .el-header {
    // 顶部导航栏样式
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #fff;
    box-shadow: 0 1px 4px rgba(0,21,41,.08);
    padding: 0 20px;
    
    .header-left {
      h2 {
        margin: 0;
        color: #303133;
      }
    }
    
    .header-right {
      .el-dropdown-link {
        display: flex;
        align-items: center;
        cursor: pointer;
        color: #303133;
        
        .el-icon--right {
          margin-left: 8px;
        }
        
        &:hover {
          color: #409eff;
        }
      }
    }
  }
  
  .el-main {
    // 主内容区域样式
    background-color: #f0f2f5;
    padding: 20px;
  }
}
</style> 