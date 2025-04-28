interface ChatHttpRequest {
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
  url: string;
  params?: Record<string, string | number | boolean>;
  data?: unknown;
}

async function myFetch(obj: ChatHttpRequest): Promise<Response> {
  const http = 'http://localhost:8080';
  // 解构赋值（添加类型注解）
  const { method, url, params, data } = obj;

  // 处理URL参数
  let requestUrl = url;
  if (params) {
    // 使用URLSearchParams处理参数
    const searchParams = new URLSearchParams();

    // 处理参数中的各种类型
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        searchParams.append(key, String(value));
      }
    });

    const queryString = searchParams.toString();
    if (queryString) {
      requestUrl += `?${queryString}`;
    }
  }

  // 构造请求配置
  const requestOptions: RequestInit = {
    method,
    headers: {
      'Content-Type': 'application/json',
    },
  };

  // 处理请求体
  if (data) {
    requestOptions.body = JSON.stringify(data);
  }

  try {
    const response : Response = await fetch(`${http}${requestUrl}`, requestOptions);

    // 可以在这里添加响应处理逻辑
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error; // 重新抛出错误以便调用者处理
  }
}

export { myFetch };
export type { ChatHttpRequest };
