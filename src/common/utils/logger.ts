// 定义日志级别类型
type LogLevel = 'debug' | 'info' | 'warn' | 'error' | 'log';

// 日志级别权重，用于控制日志输出
const logLevelPriority: Record<LogLevel, number> = {
    debug: 0,
    log: 1,
    info: 2,
    warn: 3,
    error: 4,
};

// 默认日志级别
let currentLogLevel: LogLevel = 'debug';

/**
 * 设置日志级别
 * @param level 要设置的日志级别
 */
function setLogLevel(level: LogLevel): void {
    currentLogLevel = level;
}

/**
 * 创建日志方法
 * @param level 日志级别
 * @returns 日志函数
 */
function createLogMethod(level: LogLevel) {
    return function(...args: Parameters<typeof console[LogLevel]>) {
        // 只有当前日志级别权重高于或等于设置的级别时才输出
        if (logLevelPriority[level] >= logLevelPriority[currentLogLevel]) {
            console[level](...args);
        }
    };
}

// 日志对象，包含各种级别日志方法
const logger = {
    debug: createLogMethod('debug'),
    log: createLogMethod('log'),
    info: createLogMethod('info'),
    warn: createLogMethod('warn'),
    error: createLogMethod('error'),
    setLevel: setLogLevel,
};

export default logger;
