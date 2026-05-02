<template>
  <div class="admin-layout">
    <div class="admin-sidebar">
      <div class="admin-logo">
        <h2>后台管理</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="admin-menu"
      >
        <el-menu-item index="/admin/categories">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/banners">
          <el-icon><Picture /></el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/notices">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="admin-content">
      <div class="admin-header">
        <router-link to="/" class="back-home">
          <el-icon><ArrowLeft /></el-icon>
          返回首页
        </router-link>
      </div>
      <div class="admin-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Folder, Picture, Bell, ArrowLeft, User } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: var(--color-bg);
}

.admin-sidebar {
  width: 240px;
  background-color: var(--color-card);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.06);
}

.admin-logo {
  padding: 20px;
  border-bottom: 1px solid var(--color-border);
}

.admin-logo h2 {
  margin: 0;
  font-size: 20px;
  color: var(--color-primary);
}

.admin-menu {
  border: none;
}

.admin-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-header {
  padding: 16px 24px;
  background-color: var(--color-card);
  border-bottom: 1px solid var(--color-border);
}

.back-home {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 14px;
  transition: color var(--duration-fast);
}

.back-home:hover {
  color: var(--color-primary);
}

.admin-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 180px;
  }

  .admin-main {
    padding: 16px;
  }
}
</style>
