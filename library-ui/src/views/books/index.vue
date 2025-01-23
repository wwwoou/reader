<template>
  <div class="books-container">
    <div class="header">
      <h2>图书管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增图书
      </el-button>
    </div>

    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" @keyup.enter="handleSearch">
        <el-form-item label="书名">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入书名"
            clearable
          />
        </el-form-item>
        <el-form-item label="作者">
          <el-input
            v-model="queryParams.author"
            placeholder="请输入作者"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-container">
      <el-table
        v-loading="loading"
        :data="bookList"
        border
        style="width: 100%"
      >
        <el-table-column prop="title" label="书名" min-width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="isbn" label="ISBN" width="120" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="totalCopies" label="总数量" width="100" align="center" />
        <el-table-column prop="availableCopies" label="可借数量" width="100" align="center" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="success"
              link
              :disabled="!row.availableCopies"
              @click="handleBorrow(row)"
            >
              借阅
            </el-button>
            <el-button
              type="danger"
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { getBooks, deleteBook, getCategories } from '@/api/book'

const router = useRouter()
const loading = ref(false)
const bookList = ref([])
const total = ref(0)
const categories = ref([])

const queryParams = reactive({
  page: 1,
  size: 10,
  title: '',
  author: '',
  categoryId: undefined
})

const currentPage = computed({
  get: () => queryParams.page,
  set: (val) => queryParams.page = val
})

const pageSize = computed({
  get: () => queryParams.size,
  set: (val) => queryParams.size = val
})

// 获取图书列表
const fetchBooks = async () => {
  try {
    loading.value = true
    const { data } = await getBooks(queryParams)
    bookList.value = data.content
    total.value = data.totalElements
  } catch (error) {
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const { data } = await getCategories()
    categories.value = data
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  fetchBooks()
}

// 重置
const handleReset = () => {
  queryParams.title = ''
  queryParams.author = ''
  queryParams.categoryId = undefined
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  queryParams.size = val
  fetchBooks()
}

const handleCurrentChange = (val) => {
  queryParams.page = val
  fetchBooks()
}

// 添加图书
const handleAdd = () => {
  router.push('/books/add')
}

// 编辑图书
const handleEdit = (row) => {
  router.push(`/books/${row.id}`)
}

// 借阅图书
const handleBorrow = (row) => {
  router.push({
    path: '/borrows/add',
    query: { bookId: row.id }
  })
}

// 删除图书
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除这本图书吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteBook(row.id)
      ElMessage.success('删除成功')
      fetchBooks()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchBooks()
  fetchCategories()
})
</script>

<style scoped lang="scss">
.books-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h2 {
      margin: 0;
    }
  }

  .filter-container {
    margin-bottom: 16px;
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 16px;
  }
}
</style> 