import Mock from 'mockjs'

const users = [
  {
    id: 1,
    phone: '13800138001',
    password: '123456',
    nickname: '李同学',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop',
    realName: '李明',
    school: '北京大学',
    department: '计算机科学与技术学院',
    role: 0,
    createdAt: '2024-01-15T08:00:00Z'
  },
  {
    id: 2,
    phone: '13800138002',
    password: '123456',
    nickname: '王同学',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&h=200&fit=crop',
    realName: '王芳',
    school: '清华大学',
    department: '经济管理学院',
    role: 0,
    createdAt: '2024-02-10T10:30:00Z'
  },
  {
    id: 999,
    phone: 'admin',
    password: 'admin123',
    nickname: '管理员',
    avatar: '',
    realName: '管理员',
    school: '',
    department: '',
    role: 1,
    createdAt: '2024-01-01T00:00:00Z'
  }
]

let nextUserId = 1000

export default [
  {
    url: '/api/auth/login',
    method: 'post',
    response: ({ body }) => {
      const { phone, password } = body
      const user = users.find(u => u.phone === phone && u.password === password)
      
      if (!user) {
        return {
          code: 401,
          data: null,
          message: '手机号或密码错误'
        }
      }
      
      const token = Mock.Random.string('lower', 32)
      
      return {
        code: 200,
        data: {
          token,
          userInfo: {
            id: user.id,
            phone: user.phone,
            nickname: user.nickname,
            avatar: user.avatar,
            realName: user.realName,
            school: user.school,
            department: user.department,
            role: user.role
          }
        },
        message: '登录成功'
      }
    }
  },
  {
    url: '/api/auth/register',
    method: 'post',
    response: ({ body }) => {
      const { phone, password, nickname, realName, school, department } = body
      
      if (!phone || !password || !nickname) {
        return {
          code: 400,
          data: null,
          message: '请填写必填信息'
        }
      }
      
      if (users.find(u => u.phone === phone)) {
        return {
          code: 400,
          data: null,
          message: '该手机号已注册'
        }
      }
      
      const newUser = {
        id: nextUserId++,
        phone,
        password,
        nickname,
        avatar: '',
        realName: realName || '',
        school: school || '',
        department: department || '',
        role: 0,
        createdAt: new Date().toISOString()
      }
      
      users.push(newUser)
      const token = Mock.Random.string('lower', 32)
      
      return {
        code: 200,
        data: {
          token,
          userInfo: {
            id: newUser.id,
            phone: newUser.phone,
            nickname: newUser.nickname,
            avatar: newUser.avatar,
            realName: newUser.realName,
            school: newUser.school,
            department: newUser.department,
            role: newUser.role
          }
        },
        message: '注册成功'
      }
    }
  },
  {
    url: '/api/user/info',
    method: 'get',
    response: ({ headers }) => {
      const authHeader = headers.Authorization || headers.authorization
      if (!authHeader) {
        return {
          code: 401,
          data: null,
          message: '未登录'
        }
      }
      
      const user = users[0]
      
      return {
        code: 200,
        data: {
          id: user.id,
          phone: user.phone,
          nickname: user.nickname,
          avatar: user.avatar,
          realName: user.realName,
          school: user.school,
          department: user.department,
          role: user.role
        },
        message: 'success'
      }
    }
  },
  {
    url: '/api/user/profile',
    method: 'put',
    response: ({ body }) => {
      const user = users[0]
      if (body.nickname) user.nickname = body.nickname
      if (body.avatar) user.avatar = body.avatar
      if (body.realName !== undefined) user.realName = body.realName
      if (body.school !== undefined) user.school = body.school
      if (body.department !== undefined) user.department = body.department
      
      return {
        code: 200,
        data: {
          id: user.id,
          phone: user.phone,
          nickname: user.nickname,
          avatar: user.avatar,
          realName: user.realName,
          school: user.school,
          department: user.department,
          role: user.role
        },
        message: '更新成功'
      }
    }
  },
  {
    url: '/api/upload',
    method: 'post',
    response: () => {
      const randomImages = [
        'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop',
        'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400&h=400&fit=crop',
        'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop',
        'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=400&h=400&fit=crop',
        'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=400&fit=crop',
        'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',
        'https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=400&h=400&fit=crop',
        'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&h=400&fit=crop',
        'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400&h=400&fit=crop'
      ]
      const randomIndex = Math.floor(Math.random() * randomImages.length)
      return {
        code: 200,
        data: {
          url: randomImages[randomIndex]
        },
        message: '上传成功'
      }
    }
  }
]
