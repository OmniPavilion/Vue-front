import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import axios from 'axios';

// 定义基础响应数据类型
interface BaseResponse<T = any> {
    code: number;
    message: string;
    data: T;
}

// 创建 axios 实例
const myAxios: AxiosInstance = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// 请求拦截器
myAxios.interceptors.request.use(
    (config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
        // 添加认证 token 示例
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error: AxiosError): Promise<AxiosError> => {
        return Promise.reject(error);
    }
);

// 响应拦截器
myAxios.interceptors.response.use(
    <T>(response: AxiosResponse<BaseResponse<T>>): T => {
        if (response.status !== 200) {
            throw new Error(response.data.message || 'Request failed');
        }
        if (response.data.code != 1) {
            throw new Error(response.data.message || 'Request failed');
        }
        return response.data.data;
    },
    (error: AxiosError): Promise<AxiosError> => {
        // 统一错误处理
        if (error.response?.status === 401) {
            // 处理未授权
            console.error('Unauthorized, redirect to login');
        }
        return Promise.reject(error);
    }
);

export { myAxios };
