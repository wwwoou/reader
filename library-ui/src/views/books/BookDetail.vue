<template>
  <div class="book-detail-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <h3>图书详情</h3>
          <div class="header-actions">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button 
              type="success" 
              :disabled="!book.availableCopies"
              @click="handleBorrow"
            >
              借阅
            </el-button>
          </div>
        </div>
      </template>

      <div class="book-info" v-if="book">
        <div class="book-cover">
          <img :src="book.coverImage || '/placeholder.png'" :alt="book.title">
        </div>
        
        <div class="book-content">
          <h2 class="book-title">{{ book.title }}</h2>
          
          <el-descriptions :column="2" border>
            <el-descriptions-item label="作者">
              {{ book.author }}
            </el-descriptions-item>
            <el-descriptions-item label="ISBN">
              {{ book.isbn }}
            </el-descriptions-item>
            <el-descriptions-item label="分类">
              {{ book.categoryName }}
            </el-descriptions-item>
            <el-descriptions-item label="出版社">
              {{ book.publisher }}
            </el-descriptions-item>
            <el-descriptions-item label="出版年份">
              {{ book.publicationYear }}
            </el-descriptions-item>
            <el-descriptions-item label="总数量/可借">
              {{ book.totalCopies }}/{{ book.availableCopies }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="book-description">
            <h4>图书简介</h4>
            <p>{{ book.description || '暂无简介' }}</p>
          </div>
        </div>
      </div>

      <!-- 借阅记录 -->
      <div class="borrow-records" v-if="book">
        <h4>借阅记录</h4>
        <el-table :data="borrowRecords" border style="width: 100%">
          <el-table-column prop="username" label="借阅人" width="120" />
          <el-table-column prop="borrowDate" label="借阅时间" width="180" />
          <el-table-column prop="dueDate" label="应还时间" width="180" />
          <el-table-column prop="returnDate" label="实际归还时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getBorrowStatusType(row.status)">
                {{ getBorrowStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            :page-sizes="[5, 10, 20]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getBook } from '@/api/book'
import { getBorrowRecords } from '@/api/borrow'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const book = ref(null)
const borrowRecords = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 10,
  bookId: route.params.id
})

const currentPage = computed({
  get: () => queryParams.page,
  set: (val) => queryParams.page = val
})

const pageSize = computed({
  get: () => queryParams.size,
  set: (val) => queryParams.size = val
})

// 获取图书详情
const fetchBookDetail = async () => {
  try {
    loading.value = true
    const { data } = await getBook(route.params.id)
    book.value = data
  } catch (error) {
    ElMessage.error('获取图书详情失败')
  } finally {
    loading.value = false
  }
}

// 获取借阅记录
const fetchBorrowRecords = async () => {
  try {
    const { data, total: totalCount } = await getBorrowRecords(queryParams)
    borrowRecords.value = data
    total.value = totalCount
  } catch (error) {
    ElMessage.error('获取借阅记录失败')
  }
}

// 借阅状态
const getBorrowStatusType = (status) => {
  const map = {
    BORROWED: 'warning',
    RETURNED: 'success',
    OVERDUE: 'danger'
  }
  return map[status] || 'info'
}

const getBorrowStatusText = (status) => {
  const map = {
    BORROWED: '借阅中',
    RETURNED: '已归还',
    OVERDUE: '已逾期'
  }
  return map[status] || status
}

// 分页
const handleSizeChange = (val) => {
  queryParams.size = val
  fetchBorrowRecords()
}

const handleCurrentChange = (val) => {
  queryParams.page = val
  fetchBorrowRecords()
}

// 编辑
const handleEdit = () => {
  router.push({ name: 'EditBook', params: { id: book.value.id }})
}

// 借阅
const handleBorrow = () => {
  router.push({ 
    name: 'AddBorrow',
    query: { bookId: book.value.id }
  })
}

onMounted(() => {
  fetchBookDetail()
  fetchBorrowRecords()
})
</script>

<style scoped lang="scss">
.book-detail-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
    }

    .header-actions {
      .el-button {
        margin-left: 12px;
      }
    }
  }

  .book-info {
    display: flex;
    gap: 24px;
    margin-bottom: 24px;

    .book-cover {
      flex-shrink: 0;
      width: 200px;
      height: 280px;
      overflow: hidden;
      border-radius: 4px;
      box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .book-content {
      flex: 1;

      .book-title {
        margin: 0 0 20px;
        color: #303133;
      }

      .book-description {
        margin-top: 20px;

        h4 {
          margin: 0 0 12px;
          color: #303133;
        }

        p {
          margin: 0;
          line-height: 1.6;
          color: #606266;
        }
      }
    }
  }

  .borrow-records {
    margin-top: 24px;

    h4 {
      margin: 0 0 16px;
      color: #303133;
    }

    .pagination-container {
      margin-top: 16px;
      display: flex;
      justify-content: center;
    }
  }
}
</style> 