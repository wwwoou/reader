<template>
  <div class="borrows-container">
    <div class="header">
      <h2>借阅管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增借阅
      </el-button>
    </div>

    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" @keyup.enter="handleSearch">
        <el-form-item label="图书">
          <el-input
            v-model="queryParams.bookTitle"
            placeholder="请输入图书名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="借阅人">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入借阅人"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="借阅中" value="BORROWED" />
            <el-option label="已归还" value="RETURNED" />
            <el-option label="已逾期" value="OVERDUE" />
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
        :data="borrowList"
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
        <el-table-column prop="dueDate" label="应还日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="归还日期" width="120">
          <template #default="{ row }">
            {{ row.returnDate ? formatDate(row.returnDate) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getBorrowStatusType(row.status)">
              {{ getBorrowStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'BORROWED'"
              type="success"
              link
              @click="handleReturn(row)"
            >
              归还
            </el-button>
            <el-button
              v-if="row.status === 'BORROWED'"
              type="primary"
              link
              @click="handleRenew(row)"
            >
              续借
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

    <!-- 归还对话框 -->
    <el-dialog
      v-model="returnDialogVisible"
      title="归还图书"
      width="500px"
    >
      <el-form
        ref="returnForm"
        :model="returnForm"
        label-width="100px"
      >
        <el-form-item label="备注">
          <el-input
            v-model="returnForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="returnDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="submitReturn">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 续借对话框 -->
    <el-dialog
      v-model="renewDialogVisible"
      title="续借图书"
      width="500px"
    >
      <el-form
        ref="renewForm"
        :model="renewForm"
        :rules="renewRules"
        label-width="100px"
      >
        <el-form-item label="续借天数" prop="days">
          <el-input-number
            v-model="renewForm.days"
            :min="1"
            :max="30"
            placeholder="请输入续借天数"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="renewForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="renewDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="submitRenew">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { getBorrows, returnBook, renewBook } from '@/api/borrow'
import { formatDate } from '@/utils/date'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const borrowList = ref([])
const total = ref(0)

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  bookTitle: '',
  username: '',
  status: undefined
})

const currentPage = computed({
  get: () => queryParams.page,
  set: (val) => queryParams.page = val
})

const pageSize = computed({
  get: () => queryParams.size,
  set: (val) => queryParams.size = val
})

// 归还相关
const returnDialogVisible = ref(false)
const returnForm = reactive({
  id: null,
  remarks: ''
})

// 续借相关
const renewDialogVisible = ref(false)
const renewForm = reactive({
  id: null,
  days: 7,
  remarks: ''
})

const renewRules = {
  days: [
    { required: true, message: '请输入续借天数', trigger: 'blur' },
    { type: 'number', min: 1, max: 30, message: '续借天数在 1 到 30 天之间', trigger: 'blur' }
  ]
}

// 获取借阅列表
const fetchBorrows = async () => {
  try {
    loading.value = true
    const { data } = await getBorrows(queryParams)
    borrowList.value = data.content
    total.value = data.totalElements
  } catch (error) {
    ElMessage.error('获取借阅列表失败')
  } finally {
    loading.value = false
  }
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

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  fetchBorrows()
}

// 重置
const handleReset = () => {
  queryParams.bookTitle = ''
  queryParams.username = ''
  queryParams.status = undefined
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  queryParams.size = val
  fetchBorrows()
}

const handleCurrentChange = (val) => {
  queryParams.page = val
  fetchBorrows()
}

// 新增借阅
const handleAdd = () => {
  router.push('/borrows/add')
}

// 归还图书
const handleReturn = (row) => {
  returnForm.id = row.id
  returnForm.remarks = ''
  returnDialogVisible.value = true
}

const submitReturn = async () => {
  try {
    submitting.value = true
    await returnBook(returnForm.id, { remarks: returnForm.remarks })
    ElMessage.success('归还成功')
    returnDialogVisible.value = false
    fetchBorrows()
  } catch (error) {
    ElMessage.error('归还失败')
  } finally {
    submitting.value = false
  }
}

// 续借图书
const handleRenew = (row) => {
  renewForm.id = row.id
  renewForm.days = 7
  renewForm.remarks = ''
  renewDialogVisible.value = true
}

const submitRenew = async () => {
  try {
    submitting.value = true
    await renewBook(renewForm.id, {
      days: renewForm.days,
      remarks: renewForm.remarks
    })
    ElMessage.success('续借成功')
    renewDialogVisible.value = false
    fetchBorrows()
  } catch (error) {
    ElMessage.error('续借失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchBorrows()
})
</script>

<style scoped lang="scss">
.borrows-container {
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

  .table-container {
    .link-type {
      color: #409eff;
      text-decoration: none;

      &:hover {
        color: #66b1ff;
      }
    }
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 16px;
  }
}
</style> 