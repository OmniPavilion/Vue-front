// 定义日志级别类型
type LogLevel = 'debug' | 'info' | 'warn' | 'error' | 'log';

// 日志级别权重
const logLevelPriority: Record<LogLevel, number> = {
    debug: 0,
    log: 1,
    info: 2,
    warn: 3,
    error: 4,
};

let currentLogLevel: LogLevel = 'debug';

function setLogLevel(level: LogLevel): void {
    currentLogLevel = level;
}

/**
 * 增强的日志方法
 * @param level 日志级别
 */
function createLogMethod(level: LogLevel) {
    return function(...args: any[]) {
        if (logLevelPriority[level] >= logLevelPriority[currentLogLevel]) {
            console.groupCollapsed(`→ ${args[0]}`);
            console.trace('调用栈位置'); // 这会自动创建可点击的源码链接
            console.groupEnd();
        }
    };
}

const logger = {
    debug: createLogMethod('debug'),
    log: createLogMethod('log'),
    info: createLogMethod('info'),
    warn: createLogMethod('warn'),
    error: createLogMethod('error'),
    setLevel: setLogLevel,
};

export default logger;