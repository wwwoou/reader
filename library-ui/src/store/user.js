import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, logout } from '@/api/auth'
import { getUser } from '@/api/user' // 假设您已有对应的 API 方法


export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ROLE_ADMIN')
  const username = computed(() => userInfo.value?.username || '')

  async function getUserInfo() {
    try {
      console.log('Getting user info with token:', token.value); // 添加日志
      const response = await getUser('me') // 假设 'me' 是获取当前用户信息的路径
      console.log('User info response:', response); // 添加日志
      userInfo.value = response.data
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      return response
    } catch (error) {
      console.error('Error getting user info:', error); // 添加详细错误日志
      throw error
    }
  }
  async function loginUser(loginData) {
    try {
      const response = await login(loginData)
      setUserData(response.data) // 替换 setToken 为 setUserData
      await getUserInfo() // 如果有获取用户信息的需求，可以保留
      return response
    } catch (error) {
      clearUserData()
      throw error
    }
  }
  

  async function registerUser(userData) {
    try {
      const response = await register(userData)
      if (response.status === 'SUCCESS') {
        setUserData(response)  // 如果需要自动登录的话
      }
      return response
    } catch (error) {
      console.error('Registration error:', error)
      throw error
    }
  }

  async function logoutUser() {
    try {
      await logout()
      clearUserData()
    } catch (error) {
      clearUserData()
      throw error
    }
  }

  function setUserData(data) {
    token.value = data.token
    userInfo.value = {
      username: data.username,
      role: data.role
    }
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  function clearUserData() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isAuthenticated,
    isAdmin,
    username,
    loginUser,
    registerUser,
    logoutUser,
    clearUserData
  }
}) 