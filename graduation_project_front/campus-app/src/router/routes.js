export const routes = [
  {
    path: '/',
    redirect: '/home',
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/Home.vue'),
    meta: { title: '首页' },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' },
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/product/ProductDetail.vue'),
    meta: { title: '商品详情' },
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('@/views/product/Publish.vue'),
    meta: { title: '发布商品', requiresAuth: true },
  },
  {
    path: '/publish/:id',
    name: 'PublishEdit',
    component: () => import('@/views/product/Publish.vue'),
    meta: { title: '编辑商品', requiresAuth: true },
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/product/Search.vue'),
    meta: { title: '搜索' },
  },
  {
      path: '/category',
      name: 'CategoryRoot',
      component: () => import('@/views/product/Category.vue'),
      meta: { title: '分类' },
    },
    {
      path: '/category/:id',
      name: 'Category',
      component: () => import('@/views/product/Category.vue'),
      meta: { title: '分类' },
    },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true },
  },
  {
    path: '/profile/edit',
    name: 'ProfileEdit',
    component: () => import('@/views/profile/ProfileEdit.vue'),
    meta: { title: '编辑资料', requiresAuth: true },
  },
  {
    path: '/my-products',
    name: 'MyProducts',
    component: () => import('@/views/profile/MyProducts.vue'),
    meta: { title: '我的发布', requiresAuth: true },
  },
  {
    path: '/my-orders',
    name: 'MyOrders',
    component: () => import('@/views/profile/MyOrders.vue'),
    meta: { title: '我的订单', requiresAuth: true },
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('@/views/profile/OrderDetail.vue'),
    meta: { title: '订单详情', requiresAuth: true },
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/profile/Favorites.vue'),
    meta: { title: '我的收藏', requiresAuth: true },
  },
  {
    path: '/address',
    name: 'Address',
    component: () => import('@/views/profile/Address.vue'),
    meta: { title: '地址管理', requiresAuth: true },
  },
  {
    path: '/chat',
    name: 'ChatList',
    component: () => import('@/views/chat/ChatList.vue'),
    meta: { title: '消息', requiresAuth: true },
  },
  {
    path: '/chat/:id',
    name: 'ChatDetail',
    component: () => import('@/views/chat/ChatDetail.vue'),
    meta: { title: '聊天', requiresAuth: true },
  },

  {
    path: '/admin',
    component: () => import('@/components/common/AdminLayout.vue'),
    meta: { requiresAuth: true, adminOnly: true },
    redirect: '/admin/categories',
    children: [
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/Categories.vue'),
        meta: { title: '分类管理', requiresAuth: true, adminOnly: true }
      },
      {
        path: 'banners',
        name: 'AdminBanners',
        component: () => import('@/views/admin/Banners.vue'),
        meta: { title: '轮播图管理', requiresAuth: true, adminOnly: true }
      },
      {
        path: 'notices',
        name: 'AdminNotices',
        component: () => import('@/views/admin/Notices.vue'),
        meta: { title: '公告管理', requiresAuth: true, adminOnly: true }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理', requiresAuth: true, adminOnly: true }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/other/NotFound.vue'),
    meta: { title: '404' },
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
  },
]
