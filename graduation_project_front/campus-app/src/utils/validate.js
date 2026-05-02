export const validatePhone = (phone) => {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(phone)
}

export const validatePassword = (password) => {
  return password && password.length >= 6
}

export const validateRequired = (value, message = '此项为必填') => {
  if (!value || (Array.isArray(value) && value.length === 0)) {
    return message
  }
  return true
}
