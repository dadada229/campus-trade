<template>
  <div class="profile-edit-page">
    <TopNavBar title="编辑资料" />
    
    <div class="content">
      <!-- 头像区域 -->
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :http-request="handleAvatarUpload"
          >
            <div class="avatar-container">
              <img v-if="form.avatar" :src="form.avatar" class="avatar" />
              <div v-else class="avatar-placeholder">
                <el-icon :size="32"><User /></el-icon>
              </div>
              <div class="avatar-overlay">
                <el-icon :size="20"><Camera /></el-icon>
              </div>
            </div>
          </el-upload>
        </div>
        <p class="avatar-hint">点击更换头像</p>
      </div>
      
      <!-- 表单区域 -->
      <div class="form-card">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="昵称" prop="nickname">
            <el-input 
              v-model="form.nickname" 
              placeholder="请输入昵称" 
              size="large"
              clearable 
            />
          </el-form-item>
          
          <el-form-item label="真实姓名" prop="realName">
            <el-input 
              v-model="form.realName" 
              placeholder="请输入真实姓名（选填）" 
              size="large"
              clearable 
            />
          </el-form-item>
          
          <el-form-item label="学校" prop="school">
            <el-input 
              v-model="form.school" 
              placeholder="请输入学校（选填）" 
              size="large"
              clearable 
            />
          </el-form-item>
          
          <el-form-item label="院系" prop="department">
            <el-input 
              v-model="form.department" 
              placeholder="请输入院系（选填）" 
              size="large"
              clearable 
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 保存按钮 -->
      <el-button
        type="primary"
        size="large"
        class="save-btn"
        :loading="loading"
        @click="handleSave"
      >
        {{ loading ? '保存中...' : '保存修改' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Camera } from '@element-plus/icons-vue'
import TopNavBar from '@/components/common/TopNavBar.vue'
import { useUserStore } from '@/stores/user'
import { updateProfile, uploadImage } from '@/api/modules/auth'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  avatar: '',
  nickname: '',
  realName: '',
  school: '',
  department: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称2-20个字符', trigger: 'blur' }
  ]
}

const beforeAvatarUpload = (file) => {
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

const handleAvatarUpload = async (options) => {
  try {
    const res = await uploadImage(options.file)
    form.avatar = res.data.url
    ElMessage.success('头像上传成功')
  } catch (e) {
    ElMessage.error('头像上传失败')
  }
}

const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const res = await updateProfile({
        nickname: form.nickname,
        avatar: form.avatar,
        realName: form.realName,
        school: form.school,
        department: form.department
      })
      userStore.setUserInfo(res.data)
      ElMessage.success('保存成功')
      router.back()
    } catch (e) {
      console.error('保存失败', e)
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  if (userStore.userInfo) {
    form.avatar = userStore.userInfo.avatar || ''
    form.nickname = userStore.userInfo.nickname || ''
    form.realName = userStore.userInfo.realName || ''
    form.school = userStore.userInfo.school || ''
    form.department = userStore.userInfo.department || ''
  }
})
</script>

<style scoped>
.profile-edit-page {
  min-height: 100vh;
  background: var(--color-bg);
}

.content {
  padding: var(--spacing-lg);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xl) 0;
  margin-bottom: var(--spacing-lg);
}

.avatar-wrapper {
  margin-bottom: var(--spacing-md);
}

.avatar-uploader {
  cursor: pointer;
}

.avatar-container {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: var(--radius-full);
  overflow: hidden;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-secondary-light));
  color: var(--color-primary);
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 32px;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.avatar-hint {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
}

.form-card {
  background: var(--color-card);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-sm);
  margin-bottom: var(--spacing-xl);
}

.form-card :deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  padding-bottom: var(--spacing-sm);
}

.form-card :deep(.el-input__wrapper) {
  border-radius: var(--radius-lg);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  box-shadow: none;
  transition: all 0.2s ease;
}

.form-card :deep(.el-input__wrapper:hover) {
  border-color: var(--color-primary-light);
}

.form-card :deep(.el-input__wrapper.is-focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(var(--color-primary-rgb), 0.1);
}

.save-btn {
  width: 100%;
  height: 48px;
  border-radius: var(--radius-lg);
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  border: none;
  transition: all 0.3s ease;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(var(--color-primary-rgb), 0.3);
}
</style>
