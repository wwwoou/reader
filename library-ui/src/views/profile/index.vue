<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>个人信息</h3>
              <el-button type="primary" link @click="handleEdit">
                编辑
              </el-button>
            </div>
          </template>
          <div class="info-list">
            <div class="info-item">
              <span class="label">用户名：</span>
              <span class="value">{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ userInfo.fullName }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span class="value">{{ userInfo.email }}</span>
            </div>
            <div class="info-item">
              <span class="label">角色：</span>
              <span class="value">{{ userInfo.role === 'ROLE_ADMIN' ? '管理员' : '普通用户' }}</span>
            </div>
          </div>
        </el-card>

        <!-- 借阅统计卡片 -->
        <el-card class="mt-4">
          <template #header>
            <h3>借阅统计</h3>
          </template>
          <div class="stats-list">
            <div class="stat-item">
              <div class="stat-value">{{ stats.totalBorrows || 0 }}</div>
              <div class="stat-label">总借阅次数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value warning">{{ stats.currentBorrows || 0 }}</div>
              <div class="stat-label">当前借阅</div>
            </div>
            <div class="stat-item">
              <div class="stat-value danger">{{ stats.overdueBorrows || 0 }}</div>
              <div class="stat-label">逾期未还</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 最近借阅记录 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>最近借阅</h3>
              <el-button type="primary" link @click="$router.push('/borrows')">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table
            v-loading="loading"
            :data="recentBorrows"
            border
            style="width: 100%"
          >
            <el-table-column prop="bookTitle" label="图书" min-width="150" />
            <el-table-column prop="borrowDate" label="借阅日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.borrowDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="dueDate" label="应还日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.dueDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getBorrowStatusType(row.status)">
                  {{ getBorrowStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 修改密码卡片 -->
        <el-card class="mt-4">
          <template #header>
            <h3>修改密码</h3>
          </template>
          <el-form
            ref="passwordForm"
            :model="passwordData"
            :rules="passwordRules"
            label-width="100px"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordData.oldPassword"
                type="password"
                show-password
                placeholder="请输入原密码"
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordData.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码"
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordData.confirmPassword"
                type="password"
                show-password
                placeholder="请确认新密码"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="submitting"
                @click="handleChangePassword"
              >
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑个人信息对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人信息"
      width="500px"
    >
      <el-form
        ref="editForm"
        :model="editData"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="editData.fullName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editData.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSave">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getUserProfile, updateProfile, changePassword } from '@/api/user'
import { getUserStats, getRecentBorrows } from '@/api/user'
import { formatDate } from '@/utils/date'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const userInfo = ref({})
const stats = ref({})
const recentBorrows = ref([])

// 编辑个人信息相关
const editDialogVisible = ref(false)
const editForm = ref(null)
const editData = reactive({
  fullName: '',
  email: ''
})

const editRules = {
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 修改密码相关
const passwordForm = ref(null)
const passwordData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordData.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ]
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const { data } = await getUserProfile()
    userInfo.value = data
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 获取借阅统计
const fetchStats = async () => {
  try {
    const { data } = await getUserStats()
    stats.value = data
  } catch (error) {
    ElMessage.error('获取借阅统计失败')
  }
}

// 获取最近借阅
const fetchRecentBorrows = async () => {
  try {
    loading.value = true
    const { data } = await getRecentBorrows()
    recentBorrows.value = data
  } catch (error) {
    ElMessage.error('获取最近借阅记录失败')
  } finally {
    loading.value = false
  }
}

// 编辑个人信息
const handleEdit = () => {
  editData.fullName = userInfo.value.fullName
  editData.email = userInfo.value.email
  editDialogVisible.value = true
}

const handleSave = async () => {
  if (!editForm.value) return
  
  await editForm.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        await updateProfile(editData)
        ElMessage.success('保存成功')
        editDialogVisible.value = false
        fetchUserInfo()
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordForm.value) return
  
  await passwordForm.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        await changePassword(passwordData)
        ElMessage.success('密码修改成功，请重新登录')
        userStore.logoutUser()
      } catch (error) {
        ElMessage.error('密码修改失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 借阅状态
const getBorrowStatusType = (status) => {
  const map = {
    BORROWED: 'primary',
    RETURNED: 'success',
    OVERDUE: 'danger'
  }
  return map[status]
}

const getBorrowStatusText = (status) => {
  const map = {
    BORROWED: '借阅中',
    RETURNED: '已归还',
    OVERDUE: '已逾期'
  }
  return map[status]
}

onMounted(() => {
  fetchUserInfo()
  fetchStats()
  fetchRecentBorrows()
})
</script>

<style scoped lang="scss">
.profile-container {
  .mt-4 {
    margin-top: 16px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
    }
  }

  .info-list {
    .info-item {
      margin-bottom: 12px;
      
      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: #606266;
        margin-right: 8px;
      }

      .value {
        color: #303133;
      }
    }
  }

  .stats-list {
    display: flex;
    justify-content: space-around;
    text-align: center;

    .stat-item {
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #67c23a;

        &.warning {
          color: #e6a23c;
        }

        &.danger {
          color: #f56c6c;
        }
      }

      .stat-label {
        margin-top: 8px;
        color: #606266;
      }
    }
  }
}
</style> 