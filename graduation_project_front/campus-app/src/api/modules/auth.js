import request from '@/api/request'

export const login = (data) => {
  return request.post('/auth/login', data)
}

export const register = (data) => {
  return request.post('/auth/register', data)
}

export const getUserInfo = () => {
  return request.get('/user/profile')
}

export const updateProfile = (data) => {
  return request.put('/user/profile', data)
}

export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload', formData).then(res => {
    if (res.code === 200 && typeof res.data === 'string') {
      return { ...res, data: { url: res.data } }
    }
    return res
  })
}
