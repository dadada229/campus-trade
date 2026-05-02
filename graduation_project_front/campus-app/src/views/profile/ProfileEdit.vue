<template>
  <div class="profile-edit-page">
    <TopNavBar title="编辑资料" />
    
    <div class="content">
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :http-request="handleAvatarUpload"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar" />
            <el-icon v-else class="avatar-placeholder-icon"><Plus /></el-icon>
          </el-upload>
        </div>
        <p class="avatar-hint">点击更换头像</p>
      </div>
      
      <el-form :model="form" :rules="rules" ref="formRef" class="edit-form">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        
        <el-form-item label="学校" prop="school">
          <el-input v-model="form.school" placeholder="请输入学校" clearable />
        </el-form-item>
        
        <el-form-item label="院系" prop="department">
          <el-input v-model="form.department" placeholder="请输入院系" clearable />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            class="save-btn"
            :loading="loading"
            @click="handleSave"
          >
            保存
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
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
  padding-bottom: var(--spacing-lg);
}

.content {
  padding: var(--spacing-lg);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.avatar-wrapper {
  margin-bottom: var(--spacing-sm);
}

.avatar-uploader {
  cursor: pointer;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.avatar-placeholder-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-border);
  border-radius: 50%;
  font-size: 24px;
  color: var(--color-text-secondary);
}

.avatar-hint {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
}

.edit-form {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-md) var(--spacing-lg);
}

.save-btn {
  width: 100%;
  border-radius: var(--radius-button);
  font-weight: 500;
}
</style>
