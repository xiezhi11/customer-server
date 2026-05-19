import axios from 'axios'

const request = axios.create({
  baseURL: '/',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const role = localStorage.getItem('currentRole')
  const user = localStorage.getItem('currentUser')
  if (role) config.headers['X-Role'] = role
  if (user) config.headers['X-User'] = encodeURIComponent(user)
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    } else {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    return Promise.reject(error)
  }
)

export default request
