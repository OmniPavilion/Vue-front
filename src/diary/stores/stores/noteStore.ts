import {defineStore} from 'pinia';
import {ref} from 'vue';
import type {NoteVO} from "@/diary/types/vo/NoteVO";
import {noteApi} from "@/diary/api/noteApi";
import type {PageDTO} from "@/common/types/dto/PageDTO";
import { Status} from "@/diary/types/vo/Status";

export const useNoteStore = defineStore('note', () => {
    const notes = ref<NoteVO[]>([]);
    const total = ref(0);
    const pageQuery = ref<PageDTO<Status | null>>({
        pageNum: 1,
        pageSize: 10,
        order: 'ASC',
        query: Status.PENDING,
    });
    const loading = ref(false);

    // 获取单个待办事项
    const fetchNote = async (id: number) => {
        console.log('通过id获取待办事项', id);
        loading.value = true;
        try {
            const res = await noteApi.getNote(id)
            console.log('获取待办事项成功', res.data);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 创建待办事项
    const createNote = async (note: NoteVO) => {
        console.log('创建待办事项', note);
        loading.value = true;
        try {
            const res = await noteApi.createNote(note);
            console.log('创建待办事项成功', res.data);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 更新待办事项
    const updateNote = async (note: NoteVO) => {
        console.log('更新待办事项', note);
        loading.value = true;
        try {
            const res = await noteApi.updateNote(note);
            console.log('更新待办事项成功', res.data);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 删除待办事项
    const deleteNote = async (id: number) => {
        console.log('删除待办事项', id);
        loading.value = true;
        try {
            const res = await noteApi.deleteNote(id);
            // 删除成功后可以刷新列表
            notes.value = notes.value.filter(note => note.id !== id);
            console.log('删除待办事项成功', res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 分页查询待办事项
    const fetchNotePage = async () => {
        console.log('分页查询待办事项', pageQuery.value)
        loading.value = true;
        try {
            const response = await noteApi.getNotePage(pageQuery.value);
            notes.value = response.data.data.rows;
            total.value = response.data.data.total;
            console.log('分页查询待办事项', response.data.data.rows)
            console.log('数量', response.data.data.total)
        } finally {
            loading.value = false;
        }
    };

    const theNoteToDo = async () => {
        const res = await noteApi.getNotePage({
            pageNum: 1,
            pageSize: 2147483647,
            order: 'ASC',
        });

        const notes = res.data.data.rows.filter(item => {
            if (!item.dueDate || !item.dueTime) return false;

            try {
                const dateTimeStr = `${item.dueDate} ${item.dueTime}`;
                const now = new Date();
                const dueDate = new Date(dateTimeStr);

                // 检查日期是否有效
                if (isNaN(dueDate.getTime())) return false;

                const timeDiff = dueDate.getTime() - now.getTime();
                // 返回24小时内的未过期项目
                return timeDiff > 0 && timeDiff <= 24 * 60 * 60 * 1000;
            } catch (e) {
                console.error('Error processing date:', e);
                return false;
            }
        });

        console.log("今天的待办事项数：" + notes.length)
        return notes
    }

    return {
        notes,
        loading,
        pageQuery,
        total,
        fetchNote,
        createNote,
        updateNote,
        deleteNote,
        fetchNotePage,
        theNoteToDo
    };
});
