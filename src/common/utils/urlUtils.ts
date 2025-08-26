const port = 8016;


const candidateUrls = [
    `http://192.168.117.83:`,
    `http://localhost:`,
];

// 存储可用的URL
let availableUrl: string | null = null;

// 检查URL是否可用
let checkPromise: Promise<boolean> | null = null;

const checkUrlAvailability = async (url: string): Promise<boolean> => {
    if (checkPromise) {
        return checkPromise;
    }

    checkPromise = (async () => {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 500); // 3秒超时

        try {
            await fetch(url, {
                method: 'HEAD',
                mode: 'no-cors',
                cache: 'no-cache',
                signal: controller.signal // 添加 abort signal
            });
            clearTimeout(timeoutId);
            return true;
        } catch (error) {
            clearTimeout(timeoutId);
            return false;
        } finally {
            checkPromise = null;
        }
    })();

    return checkPromise;
};


// 找出第一个可用的URL
const findAvailableUrl = async (): Promise<string> => {
    // 如果已经找到可用的URL，直接返回
    if (availableUrl) {
        return availableUrl;
    }

    // 按顺序检查每个候选地址
    for (let url of candidateUrls) {
        url = url + port;
        const isAvailable = await checkUrlAvailability(url);
        if (isAvailable) {
            availableUrl = url;
            console.log(`使用服务器地址: ${url}`);
            return url;
        }
        console.log(`地址不可用: ${url}`);
    }

    // 如果所有地址都不可用，使用最后一个（通常是本地地址）
    const fallbackUrl = candidateUrls[candidateUrls.length - 1];
    console.warn(`所有服务器地址都不可用，使用备用地址: ${fallbackUrl}`);
    return fallbackUrl;
};

// 导出的URL获取函数
export const getAvailableUrl = async (): Promise<string> => {
    return await findAvailableUrl();
};

// 兼容旧代码的导出
export const InternetConstant = {
    URL: getAvailableUrl
};