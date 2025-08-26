export enum Status {
    DRAFT = "DRAFT",
    PENDING = "PENDING",
    IN_PROGRESS = "IN_PROGRESS",
    COMPLETED = "COMPLETED",
    CANCELLED = "CANCELLED",
    EXPIRED = "EXPIRED",
}

// 状态对应的中文名称和图标类名
export const StatusInfo = {
    [Status.DRAFT]: {
        name: '草稿',
        icon: "bi bi-file-earmark-text",
        color: "#8D8D8D",  // 中性灰色，表示未激活状态
    },
    [Status.PENDING]: {
        name: '待处理',
        icon: "bi bi-clock",
        color: "#FFA726",  // 温暖的橙色，表示需要注意
    },
    [Status.IN_PROGRESS]: {
        name: '进行中',
        icon: "bi bi-arrow-repeat",
        color: "#4285F4",  // 专业的蓝色，表示正在进行
    },
    [Status.COMPLETED]: {
        name: '已完成',
        icon: "bi bi-check-circle",
        color: "#34A853",  // 积极的绿色，表示成功完成
    },
    [Status.CANCELLED]: {
        name: '已取消',
        icon: "bi bi-x-circle",
        color: "#EA4335",  // 警示的红色，表示已终止
    },
    [Status.EXPIRED]: {
        name: '已过期',
        icon: "bi bi-trash",
        color: "#9E9E9E",  // 浅灰色，表示失效状态
    },
};

// 分类选项
export const StatusOptions = (Object.keys(Status) as Array<keyof typeof Status>)
    .filter(key => isNaN(Number(key)))
    .map(key => ({
        value: key,
        label: StatusInfo[Status[key]].name,
    }));
