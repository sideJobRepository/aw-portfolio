import api from "@/lib/axiosInstance";
import {Params} from "@/components/Pagination";

interface MyParams {
    companyName: string;
    password: string;
}

export const SubmissionService = {
    get: (id: string) => api.get(`/api/submission/${id}`),

    adminGet: (params: Params) => api.get(`/api/admin-submissions`, { params }),

    temporaryPost: (body: FormData) => api.post("/api/submission/temporaryStorage", body),

    post: (body: FormData) => api.post("/api/submission", body),

    getMyList: (params: MyParams) =>
        api.post(
            "/api/submission/my-list",
            null,
            { params }
        ),

    delete: (id: string) => api.delete(`/api/question/${id}`),
};
