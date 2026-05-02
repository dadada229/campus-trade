<template>
  <AppLayout>
    <template #header>
      <TopNavBar :title="isEdit ? '编辑商品' : '发布商品'" :show-back="true" />
    </template>
    <template #default>
      <div class="publish-page">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-position="top"
          class="publish-form"
        >
          <el-form-item label="商品图片" prop="images">
            <div class="upload-container">
              <draggable
                v-model="imageList"
                item-key="url"
                class="image-list"
                ghost-class="ghost"
                @start="drag = true"
                @end="drag = false"
              >
                <template #item="{ element }">
                  <div class="image-item">
                    <el-image :src="element.url" fit="cover" class="image-preview" />
                    <div class="image-actions">
                      <el-button
                        type="danger"
                        size="small"
                        circle
                        @click="removeImage(element)"
                        class="delete-btn"
                      >
                        <el-icon><Close /></el-icon>
                      </el-button>
                    </div>
                  </div>
                </template>
              </draggable>
              <el-upload
                v-if="imageList.length < 9"
                class="image-uploader"
                :show-file-list="false"
                :http-request="handleCustomUpload"
                :before-upload="beforeUpload"
                action="#"
              >
                <div class="upload-placeholder">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <span>添加图片</span>
                </div>
              </el-upload>
            </div>
            <div class="upload-tip">最多上传9张图片，拖拽可排序</div>
          </el-form-item>

          <el-form-item label="商品标题" prop="title">
            <el-input
              v-model="formData.title"
              placeholder="请输入商品标题（2-30字符）"
              maxlength="30"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="商品描述" prop="description">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请输入商品描述（最多1000字符）"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="价格" prop="price">
                <el-input-number
                  v-model="formData.price"
                  :min="0.01"
                  :precision="2"
                  :step="1"
                  placeholder="请输入价格"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="原价" prop="originalPrice">
                <el-input-number
                  v-model="formData.originalPrice"
                  :min="0"
                  :precision="2"
                  :step="1"
                  placeholder="请输入原价（可选）"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="成色" prop="condition">
            <el-select v-model="formData.condition" placeholder="请选择成色" style="width: 100%">
              <el-option :label="item.label" :value="item.value" v-for="item in conditionOptions" :key="item.value" />
            </el-select>
          </el-form-item>

          <el-form-item label="分类" prop="categoryId">
            <el-cascader
              v-model="categoryValue"
              :options="categories"
              :props="cascaderProps"
              placeholder="请选择分类"
              style="width: 100%"
              clearable
              @change="handleCategoryChange"
            />
          </el-form-item>

          <el-form-item label="交易地点" prop="tradePlace">
            <el-input
              v-model="formData.tradePlace"
              placeholder="如：图书馆门口"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="submitting"
              @click="handleSubmit"
            >
              {{ isEdit ? '保存修改' : '发布商品' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Close, Plus } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import AppLayout from '@/components/common/AppLayout.vue'
import TopNavBar from '@/components/common/TopNavBar.vue'
import { getProductDetail, getCategories, createProduct, updateProduct, uploadImage } from '@/api/modules/product'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const submitting = ref(false)
const drag = ref(false)
const product = ref(null)
const categories = ref([])
const categoryValue = ref([])
const imageList = ref([])

const conditionOptions = [
  { label: '全新', value: 1 },
  { label: '几乎全新', value: 2 },
  { label: '轻微使用', value: 3 },
  { label: '明显痕迹', value: 4 }
]

const cascaderProps = {
  label: 'name',
  value: 'id',
  children: 'children',
  checkStrictly: true,
  emitPath: false
}

const formData = ref({
  title: '',
  description: '',
  price: null,
  originalPrice: null,
  condition: null,
  categoryId: null,
  images: [],
  tradePlace: ''
})

const rules = {
  title: [
    { required: true, message: '请输入商品标题', trigger: 'blur' },
    { min: 2, max: 30, message: '标题长度为2-30字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { max: 1000, message: '描述最多1000字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
  ],
  condition: [
    { required: true, message: '请选择成色', trigger: 'change' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  tradePlace: [
    { required: true, message: '请输入交易地点', trigger: 'blur' }
  ],
  images: [
    { required: true, message: '请至少上传一张图片', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (imageList.value.length === 0) {
          callback(new Error('请至少上传一张图片'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const isEdit = computed(() => !!route.params.id)

const loadCategories = async () => {
  try {
    const res = await getCategories({ tree: true })
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const loadProduct = async () => {
  try {
    const res = await getProductDetail(route.params.id)
    if (res.code === 200) {
      product.value = res.data
      if (res.data.sellerId !== userStore.userInfo?.id && res.data.sellerId !== 1) {
        ElMessage.error('您无权编辑此商品')
        router.back()
        return
      }
      if (res.data.status === 2) {
        ElMessage.warning('商品已售出，无法编辑')
        router.back()
        return
      }
      formData.value = {
        title: res.data.title,
        description: res.data.description,
        price: res.data.price,
        originalPrice: res.data.originalPrice,
        condition: res.data.condition,
        categoryId: res.data.categoryId,
        images: res.data.images,
        tradePlace: res.data.tradePlace
      }
      imageList.value = res.data.images.map(url => ({ url }))
      categoryValue.value = [res.data.categoryId]
    } else {
      ElMessage.error(res.message || '商品不存在')
      router.back()
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('加载商品失败')
    router.back()
  }
}

const handleCategoryChange = (value) => {
  formData.value.categoryId = value
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

const handleCustomUpload = async ({ file }) => {
  try {
    const res = await uploadImage(file)
    if (res.code === 200 && res.data) {
      imageList.value.push({ url: res.data.url })
      formData.value.images = imageList.value.map(item => item.url)
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('上传失败')
  }
}

const removeImage = (item) => {
  const index = imageList.value.findIndex(i => i.url === item.url)
  if (index > -1) {
    imageList.value.splice(index, 1)
    formData.value.images = imageList.value.map(i => i.url)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  formData.value.images = imageList.value.map(item => item.url)

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        let res
        if (isEdit.value) {
          res = await updateProduct(route.params.id, formData.value)
        } else {
          res = await createProduct(formData.value)
        }
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '保存成功' : '发布成功')
          if (isEdit.value) {
            // 编辑模式，返回上一页
            router.back()
          } else {
            // 发布模式，跳转到商品详情页
            router.push(`/product/${res.data.id}`)
          }
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (e) {
        console.error(e)
        ElMessage.error('操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  loadCategories()
  if (isEdit.value) {
    loadProduct()
  }
})
</script>

<style scoped>
.publish-page {
  padding: var(--spacing-lg);
  background: var(--color-bg);
  min-height: calc(100vh - var(--navbar-height) - var(--tabbar-height));
}

.publish-form {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-card);
}

.upload-container {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.image-item {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-input);
  overflow: hidden;
  position: relative;
  cursor: move;
}

.image-item .image-preview {
  width: 100%;
  height: 100%;
}

.image-actions {
  position: absolute;
  top: 0;
  right: 0;
  padding: var(--spacing-xs);
}

.delete-btn {
  width: 24px;
  height: 24px;
  padding: 0;
}

.image-uploader {
  width: 100px;
  height: 100px;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-input);
  color: var(--color-text-tertiary);
  transition: all var(--duration-normal);
}

.upload-placeholder:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.upload-icon {
  font-size: 28px;
  margin-bottom: var(--spacing-xs);
}

.upload-tip {
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
  margin-top: var(--spacing-sm);
}

.ghost {
  opacity: 0.5;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: var(--radius-button);
}
</style>
