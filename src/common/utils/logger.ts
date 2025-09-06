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
            // 打印标题
            console.groupCollapsed(`→ ${args[0]}`);

            // 打印调用栈
            console.trace('调用位置');

            // 打印额外参数（从第二个参数开始）
            if (args.length > 1) {
                console.log('参数:');
                // 循环输出所有额外参数
                for (let i = 1; i < args.length; i++) {
                    console.log(args[i]);
                }
            }

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
