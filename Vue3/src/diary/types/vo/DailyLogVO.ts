import type {LogEntryVO} from './LogEntryVO'
import type {Weather} from "@/diary/types/vo/Weather";

export interface DailyLogVO {
    id: number;
    date: string; // 前端使用字符串格式更方便
    weather: Weather;
    logs: LogEntryVO[];
}
