<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalBooks || 0 }}</div>
            <div class="stat-label">图书总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon">
            <el-icon><List /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalBorrows || 0 }}</div>
            <div class="stat-label">借阅总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning">
          <div class="stat-icon">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.currentBorrows || 0 }}</div>
            <div class="stat-label">当前借阅</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card danger">
          <div class="stat-icon">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.overdueBorrows || 0 }}</div>
            <div class="stat-label">逾期未还</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近借阅和热门图书 -->
    <el-row :gutter="20" class="mt-4">
      <el-col :span="16">
        <el-card v-loading="loading">
          <template #header>
            <div class="card-header">
              <h3>最近借阅</h3>
              <el-button type="primary" link @click="$router.push('/borrows')">
                查看全部
              </el-button>
            </div>
          </template>
          
          <el-empty
            v-if="!recentBorrows.length"
            description="暂无借阅记录"
          />
          
          <el-table
            v-else
            :data="recentBorrows"
            border
            style="width: 100%"
          >
            <el-table-column prop="bookTitle" label="图书" min-width="150" />
            <el-table-column prop="username" label="借阅人" width="120" />
            <el-table-column prop="borrowDate" label="借阅日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.borrowDate) }}
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
      </el-col>

      <el-col :span="8">
        <el-card v-loading="loading">
          <template #header>
            <div class="card-header">
              <h3>热门图书</h3>
              <el-button type="primary" link @click="$router.push('/books')">
                查看全部
              </el-button>
            </div>
          </template>
          
          <el-empty
            v-if="!popularBooks.length"
            description="暂无热门图书"
          />
          
          <div v-else class="popular-books">
            <div
              v-for="(book, index) in popularBooks"
              :key="book.id"
              class="book-item"
            >
              <span class="rank">{{ index + 1 }}</span>
              <div class="book-info">
                <div class="book-title">{{ book.title }}</div>
                <div class="book-author">{{ book.author }}</div>
              </div>
              <div class="borrow-count">
                借阅 {{ book.borrowCount }} 次
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStats, getRecentBorrows, getPopularBooks } from '@/api/dashboard'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const stats = ref({
  totalBooks: 0,
  totalBorrows: 0,
  currentBorrows: 0,
  overdueBorrows: 0
})
const recentBorrows = ref([])
const popularBooks = ref([])

// 获取统计数据
const fetchStats = async () => {
  try {
    const { data } = await getStats()
    stats.value = {
      totalBooks: data.totalBooks || 0,
      totalBorrows: data.totalBorrows || 0,
      currentBorrows: data.currentBorrows || 0,
      overdueBorrows: data.overdueBorrows || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 获取最近借阅记录
const fetchRecentBorrows = async () => {
  try {
    const { data } = await getRecentBorrows()
    recentBorrows.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('获取最近借阅记录失败:', error)
    ElMessage.error('获取最近借阅记录失败')
  }
}

// 获取热门图书
const fetchPopularBooks = async () => {
  try {
    const { data } = await getPopularBooks()
    popularBooks.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('获取热门图书失败:', error)
    ElMessage.error('获取热门图书失败')
  }
}

// 借阅状态样式
const getBorrowStatusType = (status) => {
  const map = {
    BORROWED: 'primary',
    RETURNED: 'success',
    OVERDUE: 'danger'
  }
  return map[status] || 'info'
}

// 借阅状态文本
const getBorrowStatusText = (status) => {
  const map = {
    BORROWED: '借阅中',
    RETURNED: '已归还',
    OVERDUE: '已逾期'
  }
  return map[status] || status
}

// 加载所有数据
const loadDashboardData = async () => {
  loading.value = true
  try {
    await Promise.all([
      fetchStats(),
      fetchRecentBorrows(),
      fetchPopularBooks()
    ])
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;

  .mt-4 {
    margin-top: 16px;
  }

  .stat-card {
    display: flex;
    align-items: center;
    padding: 20px;

    .stat-icon {
      font-size: 48px;
      margin-right: 20px;
      color: var(--el-color-primary);
    }

    &.warning .stat-icon {
      color: var(--el-color-warning);
    }

    &.danger .stat-icon {
      color: var(--el-color-danger);
    }

    .stat-content {
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
    }
  }

  .popular-books {
    .book-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .rank {
        width: 24px;
        height: 24px;
        line-height: 24px;
        text-align: center;
        background-color: #f4f4f5;
        border-radius: 4px;
        margin-right: 12px;
        font-size: 14px;
        color: #909399;
      }

      .book-info {
        flex: 1;
        min-width: 0;

        .book-title {
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
          @include ellipsis;
        }

        .book-author {
          font-size: 12px;
          color: #909399;
          @include ellipsis;
        }
      }

      .borrow-count {
        font-size: 12px;
        color: #909399;
        margin-left: 12px;
      }
    }
  }
}
</style> 