<template>
  <div class="add-book-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>{{ isEdit ? '编辑图书' : '添加图书' }}</h3>
        </div>
      </template>

      <el-form
        ref="bookForm"
        :model="bookData"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookData.title" placeholder="请输入书名" />
        </el-form-item>

        <el-form-item label="作者" prop="author">
          <el-input v-model="bookData.author" placeholder="请输入作者" />
        </el-form-item>

        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookData.isbn" placeholder="请输入ISBN" />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="bookData.categoryId" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="bookData.publisher" placeholder="请输入出版社" />
        </el-form-item>

        <el-form-item label="出版年份" prop="publicationYear">
          <el-date-picker
            v-model="bookData.publicationYear"
            type="year"
            placeholder="请选择出版年份"
            value-format="YYYY"
          />
        </el-form-item>

        <el-form-item label="总数量" prop="totalCopies">
          <el-input-number
            v-model="bookData.totalCopies"
            :min="1"
            :max="999"
            placeholder="请输入总数量"
          />
        </el-form-item>

        <el-form-item label="封面图片" prop="coverImage">
          <el-upload
            class="cover-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="bookData.coverImage" :src="bookData.coverImage" class="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="bookData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入图书描述"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            {{ isEdit ? '保存' : '添加' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createBook, updateBook, getBook, getCategories } from '@/api/book'

const route = useRoute()
const router = useRouter()
const bookForm = ref(null)
const loading = ref(false)
const categories = ref([])

const isEdit = computed(() => !!route.params.id)

const bookData = reactive({
  title: '',
  author: '',
  isbn: '',
  categoryId: '',
  publisher: '',
  publicationYear: '',
  totalCopies: 1,
  description: '',
  coverImage: ''
})

const rules = {
  title: [
    { required: true, message: '请输入书名', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' }
  ],
  isbn: [
    { required: true, message: '请输入ISBN', trigger: 'blur' },
    { pattern: /^[\d-]{10,17}$/, message: 'ISBN格式不正确', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  totalCopies: [
    { required: true, message: '请输入总数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '总数量必须大于0', trigger: 'blur' }
  ]
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

// 获取图书详情
const fetchBookDetail = async (id) => {
  try {
    loading.value = true
    const { data } = await getBook(id)
    Object.assign(bookData, data)
  } catch (error) {
    ElMessage.error('获取图书详情失败')
  } finally {
    loading.value = false
  }
}

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 上传成功回调
const handleUploadSuccess = (response) => {
  bookData.coverImage = response.url
}

// 提交表单
const handleSubmit = async () => {
  try {
    await bookForm.value.validate()
    const response = isEdit.value
      ? await updateBook(route.params.id, bookData)
      : await createBook(bookData)
    ElMessage.success(`${isEdit.value ? '更新' : '添加'}成功`)
    router.push('/books')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

// 取消
const handleCancel = () => {
  router.back()
}

onMounted(() => {
  fetchCategories()
  if (isEdit.value) {
    fetchBookDetail(route.params.id)
  }
})
</script>

<style scoped lang="scss">
.add-book-container {
  .card-header {
    h3 {
      margin: 0;
      color: #303133;
    }
  }

  .cover-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
      }
    }

    .cover-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      text-align: center;
      line-height: 178px;
    }

    .cover {
      width: 178px;
      height: 178px;
      display: block;
      object-fit: cover;
    }
  }
}
</style> 