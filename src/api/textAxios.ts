import { myAxios } from '@/utils/axios';

export function textApi(type: any) {
    return myAxios({
        method: "get",
        url: `/history/${type}`,
    })
}
