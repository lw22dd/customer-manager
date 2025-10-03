
import axios from "axios";
import { ElMessage } from "element-plus";

const serverConfig = {
  baseURL: import.meta.env.VITE_APP_API_BASE_URL || "http://localhost:8080", // 使用环境变量中的API地址，如果没有则使用默认地址
  useTokenAuthorization: true, // 开启 token 认证
};

// 创建 axios 请求实例
const serviceAxios = axios.create({
  baseURL: serverConfig.baseURL, // 基础请求地址
  timeout: 10000, // 请求超时设置
  withCredentials: true, // 跨域请求是否需要携带 cookie
  headers: {
    'Content-Type': ' application/json'
  }
});

// 请求拦截器
serviceAxios.interceptors.request.use(
  (config) => {
    // 如果开启 token 认证
    if (serverConfig.useTokenAuthorization) {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
serviceAxios.interceptors.response.use(
  (res) => {
    let data = res.data;
    // 处理自己的业务逻辑，比如判断 token 是否过期等等
    return data;
  },
  (err) => {
    // 处理请求错误
    // 错误消息变量，根据需要使用
    let errorMessage = '请求失败，请稍后重试';
    
    if (err.response) {
      // 服务器返回了错误状态码
      switch (err.response.status) {
        case 401:
          errorMessage = '未授权，请重新登录';
          // 在生产环境中，可考虑跳转到登录页
          if (import.meta.env.VITE_APP_ENV === 'production') {
            // 跳转到登录页的逻辑
            window.location.href = '/login';
          }
          break;
        case 403:
          errorMessage = '权限不足，无法访问';
          break;
        case 404:
          errorMessage = '请求的资源不存在';
          break;
        case 500:
          errorMessage = '服务器内部错误';
          break;
        default:
          errorMessage = err.response.data?.message || '请求失败';
      }
    } else if (err.request) {
      // 请求已发送但没有收到响应
      errorMessage = '网络错误，请检查网络连接';
    }
    
    // 在开发环境下打印详细错误信息，生产环境下只显示用户友好的错误信息
    if (import.meta.env.VITE_APP_ENV !== 'production') {
      console.error('请求错误:', err);
    }
    
    // 这里可以使用 Element Plus 的 Message 组件显示错误消息
    ElMessage.error(errorMessage);
    
    return Promise.reject(err);
  }
);

export default serviceAxios;