<template>
  <div class="register-page">
    <!-- 顶部导航 -->
    <div class="register-header">
      <router-link to="/login" class="back-btn">
        <el-icon :size="20"><ArrowLeft /></el-icon>
      </router-link>
      <div class="brand">
        <div class="brand-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
          </svg>
        </div>
        <span class="brand-name">校园集市</span>
      </div>
      <div class="placeholder"></div>
    </div>

    <!-- 注册表单 -->
    <div class="register-container">
      <div class="register-card">
        <div class="card-header">
          <h1 class="title">创建账号</h1>
          <p class="subtitle">填写信息，开始你的校园交易之旅</p>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" class="register-form">
          <!-- 基本信息 -->
          <div class="form-section">
            <div class="section-title">账号信息</div>
            
            <el-form-item prop="phone">
              <div class="input-wrapper">
                <el-icon class="input-icon"><Phone /></el-icon>
                <el-input
                  v-model="form.phone"
                  placeholder="请输入手机号"
                  size="large"
                  clearable
                  maxlength="11"
                />
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="input-wrapper">
                <el-icon class="input-icon"><Lock /></el-icon>
                <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="请输入密码（至少6位）"
                  size="large"
                  show-password
                />
              </div>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <div class="input-wrapper">
                <el-icon class="input-icon"><Lock /></el-icon>
                <el-input
                  v-model="form.confirmPassword"
                  type="password"
                  placeholder="请确认密码"
                  size="large"
                  show-password
                />
              </div>
            </el-form-item>
          </div>

          <!-- 个人信息 -->
          <div class="form-section">
            <div class="section-title">个人信息</div>
            
            <el-form-item prop="nickname">
              <div class="input-wrapper">
                <el-icon class="input-icon"><User /></el-icon>
                <el-input
                  v-model="form.nickname"
                  placeholder="请输入昵称"
                  size="large"
                  clearable
                />
              </div>
            </el-form-item>

            <el-form-item prop="realName">
              <div class="input-wrapper">
                <el-icon class="input-icon"><Postcard /></el-icon>
                <el-input
                  v-model="form.realName"
                  placeholder="请输入真实姓名（选填）"
                  size="large"
                  clearable
                />
              </div>
            </el-form-item>
          </div>

          <!-- 学校信息 -->
          <div class="form-section">
            <div class="section-title">学校信息</div>
            
            <el-form-item prop="school">
              <div class="input-wrapper">
                <el-icon class="input-icon"><School /></el-icon>
                <el-input
                  v-model="form.school"
                  placeholder="请输入学校（选填）"
                  size="large"
                  clearable
                />
              </div>
            </el-form-item>

            <el-form-item prop="department">
              <div class="input-wrapper">
                <el-icon class="input-icon"><OfficeBuilding /></el-icon>
                <el-input
                  v-model="form.department"
                  placeholder="请输入院系（选填）"
                  size="large"
                  clearable
                />
              </div>
            </el-form-item>
          </div>

          <!-- 注册按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="register-btn"
              :loading="loading"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 底部链接 -->
        <div class="register-footer">
          <span class="hint">已有账号？</span>
          <router-link to="/login" class="link">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Phone, Lock, User, Postcard, School, OfficeBuilding } from '@element-plus/icons-vue'
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
  background: linear-gradient(180deg, var(--color-bg-secondary) 0%, var(--color-bg) 100%);
}

.register-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg);
  position: sticky;
  top: 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  z-index: 10;
}

.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  background: var(--color-card);
  color: var(--color-text-primary);
  text-decoration: none;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-sm);
}

.back-btn:hover {
  background: var(--color-bg-secondary);
  transform: translateX(-2px);
}

.brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.brand-icon {
  width: 32px;
  height: 32px;
  color: var(--color-primary);
}

.brand-icon svg {
  width: 100%;
  height: 100%;
}

.brand-name {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.placeholder {
  width: 40px;
}

.register-container {
  padding: 0 var(--spacing-lg) var(--spacing-xxl);
}

.register-card {
  background: var(--color-card);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-lg);
}

.card-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.subtitle {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.form-section {
  margin-bottom: var(--spacing-lg);
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: var(--spacing-md);
  padding-left: var(--spacing-xs);
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 14px;
  color: var(--color-text-tertiary);
  z-index: 1;
  pointer-events: none;
}

.input-wrapper :deep(.el-input__wrapper) {
  padding-left: 44px;
  border-radius: var(--radius-lg);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  box-shadow: none;
  transition: all 0.2s ease;
}

.input-wrapper :deep(.el-input__wrapper:hover) {
  border-color: var(--color-primary-light);
}

.input-wrapper :deep(.el-input__wrapper.is-focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(var(--color-primary-rgb), 0.1);
}

.register-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-md);
}

.register-form :deep(.el-form-item__error) {
  padding-left: var(--spacing-xs);
}

.register-btn {
  width: 100%;
  height: 48px;
  border-radius: var(--radius-lg);
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  border: none;
  margin-top: var(--spacing-md);
  transition: all 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(var(--color-primary-rgb), 0.3);
}

.register-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--color-border);
}

.hint {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.link {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary);
  text-decoration: none;
  transition: color 0.2s ease;
}

.link:hover {
  color: var(--color-secondary);
}
</style>
