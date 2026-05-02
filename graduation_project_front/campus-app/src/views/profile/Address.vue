<template>
  <AppLayout>
    <div class="address-page">
      <!-- 添加地址按钮 -->
      <button class="add-address-btn" @click="handleAdd">
        <div class="add-icon">
          <el-icon :size="20"><Plus /></el-icon>
        </div>
        <span>添加新地址</span>
      </button>

      <!-- 地址列表 -->
      <div class="address-container">
        <template v-if="loading">
          <div class="skeleton-list">
            <div v-for="i in 3" :key="i" class="skeleton-item">
              <el-skeleton :rows="2" animated />
            </div>
          </div>
        </template>

        <template v-else-if="addresses.length === 0">
          <div class="empty-state">
            <div class="empty-icon">
              <el-icon :size="48"><Location /></el-icon>
            </div>
            <p class="empty-text">暂无收货地址</p>
            <p class="empty-hint">添加地址后可快速下单</p>
          </div>
        </template>

        <template v-else>
          <div class="address-list">
            <div 
              v-for="address in addresses" 
              :key="address.id" 
              class="address-card"
            >
              <div class="address-main">
                <div class="address-header">
                  <span class="receiver-name">{{ address.receiverName }}</span>
                  <span class="receiver-phone">{{ address.phone }}</span>
                  <span v-if="address.isDefault" class="default-tag">默认</span>
                </div>
                <p class="address-detail">
                  {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
                </p>
              </div>
              
              <div class="address-actions">
                <button 
                  v-if="!address.isDefault" 
                  class="action-btn default"
                  @click="handleSetDefault(address)"
                >
                  <el-icon><CircleCheck /></el-icon>
                  <span>设为默认</span>
                </button>
                <button class="action-btn edit" @click="handleEdit(address)">
                  <el-icon><Edit /></el-icon>
                  <span>编辑</span>
                </button>
                <button class="action-btn delete" @click="handleDelete(address)">
                  <el-icon><Delete /></el-icon>
                  <span>删除</span>
                </button>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 编辑弹窗 -->
      <el-dialog
        v-model="dialogVisible"
        :title="editingAddress ? '编辑地址' : '添加地址'"
        width="90%"
        :close-on-click-modal="false"
        class="address-dialog"
      >
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="收货人" prop="receiverName">
            <el-input 
              v-model="form.receiverName" 
              placeholder="请输入收货人姓名"
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="手机号" prop="phone">
            <el-input 
              v-model="form.phone" 
              placeholder="请输入手机号"
              size="large"
              maxlength="11"
            />
          </el-form-item>

          <div class="form-row">
            <el-form-item label="省份" prop="province" class="flex-1">
              <el-input 
                v-model="form.province" 
                placeholder="如：北京市"
                size="large"
              />
            </el-form-item>
            <el-form-item label="城市" prop="city" class="flex-1">
              <el-input 
                v-model="form.city" 
                placeholder="如：北京市"
                size="large"
              />
            </el-form-item>
          </div>

          <el-form-item label="区县" prop="district">
            <el-input 
              v-model="form.district" 
              placeholder="如：海淀区"
              size="large"
            />
          </el-form-item>

          <el-form-item label="详细地址" prop="detail">
            <el-input 
              v-model="form.detail" 
              placeholder="如：某某大学某某宿舍楼某某号"
              type="textarea"
              :rows="3"
              size="large"
            />
          </el-form-item>

          <el-form-item>
            <div class="default-switch">
              <span class="switch-label">设为默认地址</span>
              <el-switch v-model="form.isDefault" />
            </div>
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button size="large" @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">
              {{ editingAddress ? '保存修改' : '添加地址' }}
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Location, CircleCheck, Edit, Delete } from '@element-plus/icons-vue'
import AppLayout from '@/components/common/AppLayout.vue'
import { getUserAddresses, createAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/modules/order'

const loading = ref(true)
const submitting = ref(false)
const addresses = ref([])
const dialogVisible = ref(false)
const editingAddress = ref(null)
const formRef = ref(null)

const form = reactive({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const fetchAddresses = async () => {
  try {
    loading.value = true
    const res = await getUserAddresses()
    if (res && res.data) {
      addresses.value = res.data
    } else {
      addresses.value = []
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
    ElMessage.error('获取地址列表失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.receiverName = ''
  form.phone = ''
  form.province = ''
  form.city = ''
  form.district = ''
  form.detail = ''
  form.isDefault = false
  editingAddress.value = null
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (address) => {
  editingAddress.value = address
  form.receiverName = address.receiverName
  form.phone = address.phone
  form.province = address.province || ''
  form.city = address.city || ''
  form.district = address.district || ''
  form.detail = address.detail
  form.isDefault = address.isDefault === 1 || address.isDefault === true
  dialogVisible.value = true
}

const handleDelete = async (address) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '删除地址', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAddress(address.id)
    ElMessage.success('删除成功')
    fetchAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除地址失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSetDefault = async (address) => {
  try {
    await setDefaultAddress(address.id)
    ElMessage.success('设置成功')
    fetchAddresses()
  } catch (error) {
    console.error('设置默认地址失败:', error)
    ElMessage.error('设置失败')
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const addressData = {
      receiverName: form.receiverName,
      phone: form.phone,
      province: form.province,
      city: form.city,
      district: form.district,
      detail: form.detail,
      isDefault: form.isDefault ? 1 : 0
    }

    if (editingAddress.value) {
      await updateAddress(editingAddress.value.id, addressData)
      ElMessage.success('修改成功')
    } else {
      await createAddress(addressData)
      ElMessage.success('添加成功')
    }

    dialogVisible.value = false
    fetchAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchAddresses()
})
</script>

<style scoped>
.address-page {
  min-height: 100vh;
  background: var(--color-bg);
  padding: var(--spacing-lg);
}

.add-address-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  width: 100%;
  padding: var(--spacing-lg);
  background: var(--color-card);
  border: 2px dashed var(--color-border);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: var(--spacing-lg);
}

.add-address-btn:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.add-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  background: var(--color-primary-light);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-address-btn span {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-primary);
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.skeleton-item {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl) var(--spacing-lg);
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-full);
  background: var(--color-bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary);
  margin-bottom: var(--spacing-lg);
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-xs) 0;
}

.empty-hint {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.address-card {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.address-main {
  padding: var(--spacing-lg);
}

.address-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.receiver-phone {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.default-tag {
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: var(--radius-full);
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.address-detail {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  border-top: 1px solid var(--color-border);
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-md);
  border: none;
  background: transparent;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:not(:last-child) {
  border-right: 1px solid var(--color-border);
}

.action-btn.default {
  color: var(--color-success);
}

.action-btn.default:hover {
  background: var(--color-success-light);
}

.action-btn.edit {
  color: var(--color-primary);
}

.action-btn.edit:hover {
  background: var(--color-primary-light);
}

.action-btn.delete {
  color: var(--color-error);
}

.action-btn.delete:hover {
  background: var(--color-error-light);
}

/* Dialog Styles */
.address-dialog :deep(.el-dialog) {
  border-radius: var(--radius-xl);
}

.address-dialog :deep(.el-dialog__header) {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-border);
}

.address-dialog :deep(.el-dialog__body) {
  padding: var(--spacing-lg);
}

.address-dialog :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-primary);
}

.form-row {
  display: flex;
  gap: var(--spacing-md);
}

.flex-1 {
  flex: 1;
}

.default-switch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: var(--spacing-sm) 0;
}

.switch-label {
  font-size: 14px;
  color: var(--color-text-primary);
}

.dialog-footer {
  display: flex;
  gap: var(--spacing-md);
}

.dialog-footer .el-button {
  flex: 1;
  border-radius: var(--radius-lg);
}
</style>
