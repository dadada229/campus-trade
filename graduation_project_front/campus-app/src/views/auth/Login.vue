<template>
  <div class="login-page">
    <div class="login-header">
      <div class="logo">
        <div class="logo-icon">🎓</div>
        <h1 class="logo-text">校园二手</h1>
      </div>
      <p class="slogan">让闲置流动起来</p>
    </div>
    
    <div class="login-form-container">
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号/用户名" prefix-icon="Phone" size="large" clearable />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <p class="hint">还没有账号？<router-link to="/register" class="link">立即注册</router-link></p>
      </div>
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
  background: var(--color-bg);
  padding: var(--spacing-xxl) var(--spacing-lg);
}

.login-header {
  text-align: center;
  margin-bottom: var(--spacing-xxl);
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.logo-icon {
  font-size: 36px;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
  margin: 0;
}

.slogan {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.login-form-container {
  max-width: 360px;
  margin: 0 auto;
}

.login-form {
  margin-bottom: var(--spacing-lg);
}

.login-btn {
  width: 100%;
  border-radius: var(--radius-button);
  font-weight: 500;
}

.login-footer {
  text-align: center;
}

.hint {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0 0 var(--spacing-lg) 0;
}

.link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

.test-accounts {
  padding: var(--spacing-md);
  background: var(--color-card);
  border-radius: var(--radius-card);
}

.test-title {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 var(--spacing-xs) 0;
}

.test-item {
  font-size: 14px;
  color: var(--color-text-primary);
  margin: 0;
  font-family: monospace;
}
</style>