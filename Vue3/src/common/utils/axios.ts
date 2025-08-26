import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import axios from 'axios';
import type {Result} from '@/common/types/vo/Result'
import {InternetConstant} from "@/common/utils/urlUtils";

// 创建基础实例
const myAxios: AxiosInstance = axios.create({
    baseURL: '', // 先设为空
    timeout: 500000,
    headers: {
        'Content-Type': 'application/json',
    },
});

let baseURLCache: string | null = null;

// 请求拦截器 - 动态设置 baseURL
myAxios.interceptors.request.use(
    async (config: InternalAxiosRequestConfig): Promise<InternalAxiosRequestConfig> => {
        // 动态设置 baseURL
        if (!config.baseURL) {
            if (!baseURLCache) {
                baseURLCache = await InternetConstant.URL();
            }
            config.baseURL = baseURLCache;
        }

        // 添加认证 token
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

// 响应拦截器（保持不变）
myAxios.interceptors.response.use(
    <T>(response: AxiosResponse<Result<T>>): AxiosResponse<Result<T>> => {
        if (response.data.code != 1) {
            console.error("异常：", response.data.message || 'Request failed');
        }
        return response;
    },
    (error: AxiosError): Promise<AxiosError> => {
        if (error.response?.status === 401) {
            console.error('Unauthorized, redirect to login');
        }
        return Promise.reject(error);
    }
);

export { myAxios };