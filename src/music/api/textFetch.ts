import { myFetch } from '@/common/utils/fetch';

export async function textApi(
    prompt: string,
    chatId: string
): Promise<Response> {
    return  myFetch({
        method: "GET",
        url: '/chat',
        params: {
            prompt,
            chatId,
        },
    });
}
