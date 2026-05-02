<template>
  <AppLayout>
    <div class="address-page">
      <div class="add-button" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        <span>添加地址</span>
      </div>

      <template v-if="loading">
        <el-skeleton :rows="5" animated />
      </template>
      <template v-else-if="addresses.length === 0">
        <el-empty description="暂无收货地址" />
      </template>
      <template v-else>
        <div class="address-list">
          <div v-for="address in addresses" :key="address.id" class="address-item">
            <div class="address-info">
              <div class="address-top">
                <span class="name">{{ address.receiverName }}</span>
                <span class="phone">{{ address.phone }}</span>
                <el-tag v-if="address.isDefault" type="success" size="small">默认</el-tag>
              </div>
              <div class="address-detail">
                {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
              </div>
            </div>
            <div class="address-actions">
              <el-button type="primary" size="small" link @click="handleEdit(address)">编辑</el-button>
              <el-button type="danger" size="small" link @click="handleDelete(address)">删除</el-button>
              <el-button v-if="!address.isDefault" type="success" size="small" link @click="handleSetDefault(address)">设为默认</el-button>
            </div>
          </div>
        </div>
      </template>

      <el-dialog
        v-model="dialogVisible"
        :title="editingAddress ? '编辑地址' : '添加地址'"
        width="90%"
      >
        <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="省份" prop="province">
            <el-input v-model="form.province" placeholder="请输入省份，如：北京市" />
          </el-form-item>
          <el-form-item label="城市" prop="city">
            <el-input v-model="form.city" placeholder="请输入城市，如：北京市" />
          </el-form-item>
          <el-form-item label="区县" prop="district">
            <el-input v-model="form.district" placeholder="请输入区县，如：海淀区" />
          </el-form-item>
          <el-form-item label="详细地址" prop="detail">
            <el-input v-model="form.detail" placeholder="请输入详细地址，如：某某大学某某宿舍" type="textarea" />
          </el-form-item>
          <el-form-item label="设为默认">
            <el-switch v-model="form.isDefault" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
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
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
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
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
      confirmButtonText: '确定',
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
      ElMessage.success('编辑成功')
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
  padding: var(--spacing-lg);
}

.add-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg);
  background: var(--color-card);
  border-radius: var(--radius-card);
  margin-bottom: var(--spacing-md);
  cursor: pointer;
  color: var(--color-primary);
  font-size: var(--font-size-body);
  box-shadow: var(--shadow-card);
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.address-item {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-md);
  box-shadow: var(--shadow-card);
}

.address-info {
  flex: 1;
  margin-bottom: var(--spacing-md);
}

.address-top {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.name {
  font-weight: bold;
  color: var(--color-text-primary);
}

.phone {
  color: var(--color-text-secondary);
}

.address-detail {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
}

.address-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border);
}
</style>
