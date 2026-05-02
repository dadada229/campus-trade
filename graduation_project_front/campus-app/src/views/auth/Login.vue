<template>
  <div class="login-page">
    <!-- 顶部装饰 -->
    <div class="top-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
    </div>

    <!-- Logo区域 -->
    <div class="login-header">
      <div class="logo">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M5 13.18v4L12 21l7-3.82v-4L12 17l-7-3.82zM12 3L1 9l11 6 9-4.91V17h2V9L12 3z"/>
          </svg>
        </div>
        <h1 class="logo-text">校园好物</h1>
      </div>
      <p class="slogan">让闲置流动起来，让校园更美好</p>
    </div>
    
    <!-- 登录表单 -->
    <div class="login-form-container">
      <div class="form-card">
        <h2 class="form-title">欢迎回来</h2>
        <p class="form-subtitle">登录以继续使用校园二手交易平台</p>
        
        <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
          <el-form-item prop="phone">
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07 19.5 19.5 0 01-6-6 19.79 19.79 0 01-3.07-8.67A2 2 0 014.11 2h3a2 2 0 012 1.72 12.84 12.84 0 00.7 2.81 2 2 0 01-.45 2.11L8.09 9.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45 12.84 12.84 0 002.81.7A2 2 0 0122 16.92z"/>
                </svg>
              </div>
              <el-input 
                v-model="form.phone" 
                placeholder="请输入手机号/用户名" 
                size="large" 
                clearable 
              />
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <div class="input-wrapper">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0110 0v4"/>
                </svg>
              </div>
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码" 
                size="large" 
                show-password 
                @keyup.enter="handleLogin" 
              />
            </div>
          </el-form-item>
          
          <el-form-item>
            <button 
              type="button" 
              class="login-btn" 
              :class="{ loading: loading }"
              :disabled="loading" 
              @click="handleLogin"
            >
              <span v-if="!loading">登录</span>
              <span v-else class="loading-text">
                <span class="loading-spinner"></span>
                登录中...
              </span>
            </button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="login-footer">
        <p class="hint">还没有账号？<router-link to="/register" class="link">立即注册</router-link></p>
      </div>
    </div>

    <!-- 底部装饰 -->
    <div class="bottom-info">
      <p>安全 · 便捷 · 校园专属</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  phone: '',
  password: ''
})

const validatePhone = (rule, value, callback) => {
  const phonePattern = /^1[3-9]\d{9}$/
  if (value === 'admin') {
    callback()
  } else if (phonePattern.test(value)) {
    callback()
  } else {
    callback(new Error('请输入正确的手机号'))
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号/用户名', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      await userStore.login(form.phone, form.password)
      ElMessage.success('登录成功')
      
      const redirect = route.query.redirect || (userStore.isAdmin ? '/admin/categories' : '/home')
      router.push(redirect)
    } catch (e) {
      console.error('登录失败', e)
      const errorMsg = (e && e.message) ? e.message : '登录失败，请检查账号密码'
      ElMessage.error(errorMsg)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, var(--color-bg) 0%, var(--color-card) 100%);
  padding: var(--spacing-xxl) var(--padding-page);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

/* 顶部装饰 */
.top-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 200px;
  overflow: hidden;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: var(--radius-circle);
  background: linear-gradient(135deg, var(--color-primary-bg) 0%, rgba(61, 154, 139, 0.05) 100%);
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: -100px;
  right: -50px;
}

.circle-2 {
  width: 120px;
  height: 120px;
  top: 50px;
  left: -40px;
}

/* Logo区域 */
.login-header {
  text-align: center;
  margin-bottom: var(--spacing-xxxl);
  position: relative;
  z-index: 1;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.logo-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  border-radius: var(--radius-lg);
  box-shadow: 0 8px 24px rgba(61, 154, 139, 0.3);
}

.logo-icon svg {
  width: 28px;
  height: 28px;
  color: #fff;
}

.logo-text {
  font-size: 28px;
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -0.02em;
}

.slogan {
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  margin: 0;
}

/* 表单卡片 */
.login-form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 400px;
  width: 100%;
  margin: 0 auto;
}

.form-card {
  background: var(--color-card);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-card);
  margin-bottom: var(--spacing-xl);
}

.form-title {
  font-size: var(--font-size-h2);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-xs) 0;
}

.form-subtitle {
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
  margin: 0 0 var(--spacing-xl) 0;
}

.login-form {
  margin-bottom: 0;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: var(--spacing-lg);
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  color: var(--color-text-tertiary);
  z-index: 1;
  pointer-events: none;
}

.input-icon svg {
  width: 100%;
  height: 100%;
}

.input-wrapper :deep(.el-input__wrapper) {
  padding-left: 48px !important;
  height: 52px !important;
  border-radius: var(--radius-card) !important;
}

.input-wrapper :deep(.el-input__inner) {
  font-size: var(--font-size-body) !important;
}

.login-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  color: #fff;
  border: none;
  border-radius: var(--radius-card);
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-standard);
  box-shadow: 0 4px 16px rgba(61, 154, 139, 0.3);
  margin-top: var(--spacing-sm);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(61, 154, 139, 0.35);
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn.loading {
  opacity: 0.8;
  cursor: not-allowed;
}

.loading-text {
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

/* 底部链接 */
.login-footer {
  text-align: center;
}

.hint {
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  margin: 0;
}

.link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: var(--font-weight-semibold);
  transition: all var(--duration-fast);
}

.link:hover {
  color: var(--color-primary-light);
}

/* 底部信息 */
.bottom-info {
  text-align: center;
  margin-top: auto;
  padding-top: var(--spacing-xl);
}

.bottom-info p {
  font-size: var(--font-size-small);
  color: var(--color-text-quaternary);
  margin: 0;
}
</style>
