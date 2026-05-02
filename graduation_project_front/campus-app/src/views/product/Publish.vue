<template>
  <div class="publish-page">
    <!-- 顶部导航 -->
    <header class="publish-header">
      <div class="header-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
          <path d="M15 18l-6-6 6-6"/>
        </svg>
      </div>
      <h1 class="header-title">{{ isEdit ? '编辑商品' : '发布宝贝' }}</h1>
      <div class="header-placeholder"></div>
    </header>

    <div class="publish-content">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-position="top"
        class="publish-form"
      >
        <!-- 图片上传 -->
        <div class="form-section">
          <div class="section-label">
            <span class="label-text">商品图片</span>
            <span class="label-hint">最多9张，拖拽可排序</span>
          </div>
          <el-form-item prop="images" class="no-margin">
            <div class="upload-area">
              <draggable
                v-model="imageList"
                item-key="url"
                class="image-grid"
                ghost-class="ghost"
                @start="drag = true"
                @end="drag = false"
              >
                <template #item="{ element }">
                  <div class="image-item">
                    <el-image :src="element.url" fit="cover" class="image-preview" />
                    <div class="image-overlay">
                      <button type="button" class="delete-btn" @click="removeImage(element)">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M18 6L6 18M6 6l12 12"/>
                        </svg>
                      </button>
                    </div>
                    <div v-if="imageList.indexOf(element) === 0" class="cover-tag">封面</div>
                  </div>
                </template>
              </draggable>
              <el-upload
                v-if="imageList.length < 9"
                class="upload-btn"
                :show-file-list="false"
                :http-request="handleCustomUpload"
                :before-upload="beforeUpload"
                action="#"
              >
                <div class="upload-placeholder">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M12 5v14M5 12h14"/>
                  </svg>
                  <span>添加图片</span>
                </div>
              </el-upload>
            </div>
          </el-form-item>
        </div>

        <!-- 基本信息 -->
        <div class="form-section">
          <div class="section-label">
            <span class="label-text">基本信息</span>
          </div>

          <el-form-item prop="title" class="form-item">
            <div class="input-label">商品标题</div>
            <el-input
              v-model="formData.title"
              placeholder="请输入商品标题，吸引买家注意"
              maxlength="30"
              show-word-limit
            />
          </el-form-item>

          <el-form-item prop="description" class="form-item">
            <div class="input-label">商品描述</div>
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="描述一下宝贝的成色、购买渠道、使用情况等"
              maxlength="1000"
              show-word-limit
              resize="none"
            />
          </el-form-item>
        </div>

        <!-- 价格信息 -->
        <div class="form-section">
          <div class="section-label">
            <span class="label-text">价格信息</span>
          </div>
          
          <div class="price-row">
            <el-form-item prop="price" class="form-item price-item">
              <div class="input-label">出售价格</div>
              <div class="price-input">
                <span class="price-symbol">¥</span>
                <el-input-number
                  v-model="formData.price"
                  :min="0.01"
                  :precision="2"
                  :step="1"
                  :controls="false"
                  placeholder="0.00"
                />
              </div>
            </el-form-item>
            <el-form-item prop="originalPrice" class="form-item price-item">
              <div class="input-label">原价（选填）</div>
              <div class="price-input">
                <span class="price-symbol muted">¥</span>
                <el-input-number
                  v-model="formData.originalPrice"
                  :min="0"
                  :precision="2"
                  :step="1"
                  :controls="false"
                  placeholder="0.00"
                />
              </div>
            </el-form-item>
          </div>
        </div>

        <!-- 详细信息 -->
        <div class="form-section">
          <div class="section-label">
            <span class="label-text">详细信息</span>
          </div>

          <el-form-item prop="condition" class="form-item">
            <div class="input-label">成色</div>
            <div class="condition-grid">
              <div 
                v-for="item in conditionOptions" 
                :key="item.value"
                class="condition-item"
                :class="{ active: formData.condition === item.value }"
                @click="formData.condition = item.value"
              >
                {{ item.label }}
              </div>
            </div>
          </el-form-item>

          <el-form-item prop="categoryId" class="form-item">
            <div class="input-label">分类</div>
            <el-cascader
              v-model="categoryValue"
              :options="categories"
              :props="cascaderProps"
              placeholder="请选择分类"
              clearable
              @change="handleCategoryChange"
            />
          </el-form-item>

          <el-form-item prop="tradePlace" class="form-item">
            <div class="input-label">交易地点</div>
            <el-input
              v-model="formData.tradePlace"
              placeholder="如：图书馆门口、宿舍楼下"
            />
          </el-form-item>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-section">
          <button
            type="button"
            class="submit-btn"
            :class="{ loading: submitting }"
            :disabled="submitting"
            @click="handleSubmit"
          >
            <span v-if="!submitting">{{ isEdit ? '保存修改' : '立即发布' }}</span>
            <span v-else class="loading-content">
              <span class="loading-spinner"></span>
              {{ isEdit ? '保存中...' : '发布中...' }}
            </span>
          </button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import draggable from 'vuedraggable'
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
            router.back()
          } else {
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
  min-height: 100vh;
  background-color: var(--color-bg);
}

/* 顶部导航 */
.publish-header {
  height: var(--navbar-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: var(--shadow-xs);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-circle);
  cursor: pointer;
  transition: all var(--duration-fast);
  margin-left: -8px;
}

.header-btn:hover {
  background: var(--color-bg);
}

.header-btn svg {
  width: 22px;
  height: 22px;
  color: var(--color-text-primary);
}

.header-title {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin: 0;
}

.header-placeholder {
  width: 36px;
}

/* 内容区域 */
.publish-content {
  padding: var(--spacing-md);
  padding-bottom: 100px;
}

.publish-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

/* 表单区块 */
.form-section {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-card);
}

.section-label {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-lg);
}

.label-text {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.label-hint {
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
}

/* 图片上传 */
.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.image-grid {
  display: contents;
}

.image-item {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  position: relative;
  cursor: move;
}

.image-preview {
  width: 100%;
  height: 100%;
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0);
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: var(--spacing-xs);
  transition: all var(--duration-fast);
}

.image-item:hover .image-overlay {
  background: rgba(0, 0, 0, 0.3);
}

.delete-btn {
  width: 24px;
  height: 24px;
  border-radius: var(--radius-circle);
  background: rgba(0, 0, 0, 0.5);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: all var(--duration-fast);
}

.image-item:hover .delete-btn {
  opacity: 1;
}

.delete-btn svg {
  width: 14px;
  height: 14px;
  color: #fff;
}

.cover-tag {
  position: absolute;
  left: 0;
  bottom: 0;
  padding: 2px 8px;
  background: var(--color-primary);
  color: #fff;
  font-size: var(--font-size-mini);
  font-weight: var(--font-weight-medium);
}

.upload-btn {
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
  gap: var(--spacing-xs);
  border: 1.5px dashed var(--color-border);
  border-radius: var(--radius-sm);
  color: var(--color-text-tertiary);
  font-size: var(--font-size-small);
  transition: all var(--duration-fast);
  cursor: pointer;
}

.upload-placeholder:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-primary-bg);
}

.upload-placeholder svg {
  width: 24px;
  height: 24px;
}

.ghost {
  opacity: 0.5;
}

/* 表单项 */
.form-item {
  margin-bottom: var(--spacing-lg);
}

.form-item:last-child {
  margin-bottom: 0;
}

.no-margin {
  margin-bottom: 0;
}

.input-label {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-sm);
}

/* 价格行 */
.price-row {
  display: flex;
  gap: var(--spacing-lg);
}

.price-item {
  flex: 1;
}

.price-input {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.price-symbol {
  font-size: var(--font-size-h3);
  font-weight: var(--font-weight-bold);
  color: var(--color-price);
}

.price-symbol.muted {
  color: var(--color-text-tertiary);
}

.price-input :deep(.el-input-number) {
  width: 100%;
}

.price-input :deep(.el-input__wrapper) {
  padding-left: 0 !important;
}

.price-input :deep(.el-input__inner) {
  font-size: var(--font-size-h3) !important;
  font-weight: var(--font-weight-bold) !important;
}

/* 成色选择 */
.condition-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-sm);
}

.condition-item {
  padding: var(--spacing-md);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-sm);
  text-align: center;
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.condition-item:hover {
  border-color: var(--color-primary-light);
}

.condition-item.active {
  border-color: var(--color-primary);
  background: var(--color-primary-bg);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
}

/* 级联选择器 */
.form-item :deep(.el-cascader) {
  width: 100%;
}

/* 提交按钮 */
.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: var(--spacing-md) var(--spacing-lg);
  padding-bottom: calc(var(--spacing-md) + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: var(--shadow-top);
}

.submit-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  color: #fff;
  border: none;
  border-radius: var(--radius-card);
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--duration-fast);
  box-shadow: 0 4px 16px rgba(61, 154, 139, 0.3);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(61, 154, 139, 0.35);
}

.submit-btn:active {
  transform: translateY(0);
}

.submit-btn.loading {
  opacity: 0.8;
  cursor: not-allowed;
}

.loading-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
