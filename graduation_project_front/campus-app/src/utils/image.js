export const formatImageUrl = (url) => {
  if (!url) {
    return ''
  }
  
  if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('data:')) {
    return url
  }
  
  if (url.startsWith('/uploads/')) {
    return url
  }
  
  if (url.startsWith('uploads/')) {
    return '/' + url
  }
  
  return '/uploads/' + url
}

export const getAvatarUrl = (avatar, defaultAvatar = '') => {
  if (!avatar) {
    return defaultAvatar || 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop'
  }
  return formatImageUrl(avatar)
}
