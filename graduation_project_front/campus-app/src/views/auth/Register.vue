<template>
  <div class="register-page">
    <div class="register-header">
      <router-link to="/login" class="back-link">
        <el-icon><ArrowLeft /></el-icon>
      </router-link>
      <div class="logo">
        <div class="logo-icon">🎓</div>
        <h1 class="logo-text">校园二手</h1>
      </div>
      <div class="placeholder"></div>
    </div>
    
    <div class="register-form-container">
      <h2 class="page-title">创建账号</h2>
      
      <el-form :model="form" :rules="rules" ref="formRef" class="register-form">
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="请输入昵称"
            prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="realName">
          <el-input
            v-model="form.realName"
            placeholder="请输入真实姓名（选填）"
            prefix-icon="Document"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="school">
          <el-input
            v-model="form.school"
            placeholder="请输入学校（选填）"
            prefix-icon="OfficeBuilding"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="department">
          <el-input
            v-model="form.department"
            placeholder="请输入院系（选填）"
            prefix-icon="School"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-footer">
        <p class="hint">
          已有账号？
          <router-link to="/login" class="link">立即登录</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  realName: '',
  school: '',
  department: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称2-20个字符', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      await userStore.register({
        phone: form.phone,
        password: form.password,
        nickname: form.nickname,
        realName: form.realName,
        school: form.school,
        department: form.department
      })
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (e) {
      console.error('注册失败', e)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: var(--color-bg);
  padding: var(--spacing-lg);
}

.register-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xxl);
}

.back-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--color-text-primary);
  text-decoration: none;
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-primary);
  margin: 0;
}

.placeholder {
  width: 36px;
}

.register-form-container {
  max-width: 360px;
  margin: 0 auto;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-lg) 0;
  text-align: center;
}

.register-form {
  margin-bottom: var(--spacing-lg);
}

.register-btn {
  width: 100%;
  border-radius: var(--radius-button);
  font-weight: 500;
}

.register-footer {
  text-align: center;
}

.hint {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}
</style>
