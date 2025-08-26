import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { NoteVO } from '@/diary/types/vo/NoteVO';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type {PageVO} from "@/common/types/vo/PageVO";
import type {Status} from "@/diary/types/vo/Status";
import type {AxiosResponse} from "axios";

export const noteApi = {
    // 创建待办事项
    createNote(note: NoteVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'post',
            url: '/diary/notes',
            data: note,
        });
    },

    // 获取单个待办事项
    getNote(id: number): Promise<AxiosResponse<Result<NoteVO>>> {
        return myAxios({
            method: 'get',
            url: `/diary/notes/${id}`,
        });
    },

    // 更新待办事项
    updateNote(note: NoteVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: '/diary/notes',
            data: note,
        });
    },

    // 删除待办事项
    deleteNote(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/diary/notes/${id}`,
        });
    },

    // 根据状态查询待办事项
    getNotesByStatus(status: Status): Promise<AxiosResponse<Result<NoteVO[]>>> {
        return myAxios({
            method: 'get',
            url: `/diary/notes/status/${status}`,
        });
    },

    // 分页查询待办事项
    getNotePage(pageDTO: PageDTO<Status | null>): Promise<AxiosResponse<Result<PageVO<NoteVO>>>> {
        return myAxios({
            method: 'post',
            url: '/diary/notes/page',
            data: pageDTO,
        });
    },
};
