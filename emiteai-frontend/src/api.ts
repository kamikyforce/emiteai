import axios from 'axios'
import { toast } from 'react-toastify'
import { translateError } from 'utils/translateError'


const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL,
  headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
  timeout: 10000
})

api.interceptors.response.use(
  response => response,
  error => {
    toast.error(translateError(error))
    return Promise.reject(error)
  }
)

export default api
