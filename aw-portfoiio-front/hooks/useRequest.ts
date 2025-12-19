'use client';

import { useRouter } from 'next/navigation';
import axios, { AxiosError } from 'axios';

interface RequestOptions {
    ignoreErrorRedirect?: boolean;
}

export function useRequest() {
    const router = useRouter();

    const request = async <T>(
        requestFn: () => Promise<T>,
        onSuccess?: (data: T) => void,
        options?: RequestOptions
    ): Promise<T | undefined> => {
        try {
            const data = await requestFn();
            onSuccess?.(data);
            return data;
        } catch (error) {
            const err = error as AxiosError<any>;

            if (options?.ignoreErrorRedirect) {

                const errData = err.response?.data;
                
                //벨리데이터 형식 에러 검증
                if(errData?.validation.length > 0){
                    const message = errData.validation.map((v: any) => v.message).join('\n');
                    alert(message);
                }else {
                    alert(err.response?.data?.message ?? '오류가 발생했습니다.')
                }


            }
            throw error;
        }
    };

    return { request };
}
