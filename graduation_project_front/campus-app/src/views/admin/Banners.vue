<template>
  <div class="banners-page">
    <div class="page-header">
      <h2>轮播图管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加轮播图
      </el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="banners" border style="width: 100%">
        <el-table-column label="图片" width="200">
          <template #default="{ row }">
            <el-image
              :src="row.image"
              style="width: 160px; height: 60px"
              fit="cover"
              :preview-src-list="[row.image]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="link" label="跳转链接" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.enabled" @change="handleToggle(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑轮播图' : '添加轮播图'"
      width="520px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="图片" prop="image">
          <el-upload
            class="banner-uploader"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
          >
            <img v-if="formData.image" :src="formData.image" class="banner-preview" />
            <el-icon v-else class="banner-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="跳转链接" prop="link">
          <el-input v-model="formData.link" placeholder="请输入跳转链接（可选）" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="1" />
        </el-form-item>
        <el-form-item label="启用" prop="enabled">
          <el-switch v-model="formData.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getBanners, addBanner, updateBanner, deleteBanner, toggleBanner, uploadImage } from '@/api/modules/admin'

const banners = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const formData = ref({
  id: null,
  image: '',
  link: '',
  sort: 1,
  enabled: true
})

const rules = {
  image: [{ required: true, message: '请上传图片', trigger: 'change' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const res = await getBanners()
    banners.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  isEdit.value = false
  formData.value = {
    id: null,
    image: '',
    link: '',
    sort: banners.value.length + 1,
    enabled: true
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  formData.value = {
    id: row.id,
    image: row.image,
    link: row.link,
    sort: row.sort,
    enabled: row.enabled
  }
  dialogVisible.value = true
}

const handleToggle = async (row) => {
  try {
    await toggleBanner(row.id)
    ElMessage.success('切换成功')
  } catch (error) {
    row.enabled = !row.enabled
    console.error(error)
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  return true
}

const handleUpload = async (options) => {
  try {
    const res = await uploadImage(options.file)
    formData.value.image = res.data
    ElMessage.success('上传成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('上传失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateBanner(formData.value.id, formData.value)
        ElMessage.success('更新成功')
      } else {
        await addBanner(formData.value)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteBanner(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.banners-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: var(--color-secondary);
}

.table-card {
  border-radius: var(--radius-card);
}

.banner-uploader {
  width: 240px;
  height: 80px;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-input);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color var(--duration-fast);
}

.banner-uploader:hover {
  border-color: var(--color-primary);
}

.banner-uploader-icon {
  font-size: 28px;
  color: var(--color-text-tertiary);
}

.banner-preview {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}
</style>
