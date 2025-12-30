import api from "@/lib/axiosInstance";

interface MyParams {
    companyName: string;
    password: string;
}

export const SubmissionService = {
    get: (id: string) => api.get(`/api/submission/${id}`),

    adminGet: () => api.get(`/api/admin-submissions`),

    temporaryPost: (body: FormData) => api.post("/api/submission/temporaryStorage", body),

    post: (body: FormData) => api.post("/api/submission", body),

    getMyList: (params: MyParams) =>
        api.post(
            "/api/submission/my-list",
            null,
            { params }
        ),

    delete: (id: string) => api.delete(`/api/admin-submissions/${id}`),
};
