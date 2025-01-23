<template>
  <div class="add-borrow-container">
    <div class="header">
      <h2>新增借阅</h2>
    </div>

    <el-card>
      <el-form
        ref="borrowForm"
        :model="borrowData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="图书" prop="bookId">
          <el-select
            v-model="borrowData.bookId"
            filterable
            remote
            reserve-keyword
            placeholder="请输入图书名称搜索"
            :remote-method="handleSearch"
            :loading="loading"
          >
            <el-option
              v-for="item in bookOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
              :disabled="!item.availableCopies"
            >
              <span>{{ item.title }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                可借: {{ item.availableCopies }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="借阅天数" prop="days">
          <el-input-number
            v-model="borrowData.days"
            :min="1"
            :max="30"
            controls-position="right"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remarks">
          <el-input
            v-model="borrowData.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
          >
            提交
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { searchBooks, getBook } from '@/api/book'
import { createBorrow } from '@/api/borrow'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const bookOptions = ref([])

const borrowData = reactive({
  bookId: undefined,
  days: 7,
  remarks: ''
})

const rules = {
  bookId: [
    { required: true, message: '请选择图书', trigger: 'change' }
  ],
  days: [
    { required: true, message: '请输入借阅天数', trigger: 'blur' },
    { type: 'number', min: 1, max: 30, message: '借阅天数在 1 到 30 天之间', trigger: 'blur' }
  ]
}

// 搜索图书
const handleSearch = async (query) => {
  if (query) {
    try {
      loading.value = true
      const { data } = await searchBooks(query)
      bookOptions.value = data
    } catch (error) {
      ElMessage.error('搜索图书失败')
    } finally {
      loading.value = false
    }
  } else {
    bookOptions.value = []
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!borrowForm.value) return
  
  await borrowForm.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        await createBorrow(borrowData)
        ElMessage.success('借阅成功')
        router.push('/borrows')
      } catch (error) {
        ElMessage.error('借阅失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 如果有预选的图书ID，获取图书信息
onMounted(async () => {
  const bookId = route.query.bookId
  if (bookId) {
    try {
      loading.value = true
      const { data } = await getBook(bookId)
      bookOptions.value = [data]
      borrowData.bookId = data.id
    } catch (error) {
      ElMessage.error('获取图书信息失败')
    } finally {
      loading.value = false
    }
  }
})
</script>

<style scoped lang="scss">
.add-borrow-container {
  .header {
    margin-bottom: 16px;

    h2 {
      margin: 0;
    }
  }
}
</style> 