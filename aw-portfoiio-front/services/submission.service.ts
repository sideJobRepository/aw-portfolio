import api from "@/lib/axiosInstance";

interface ExcelParams {
  portfolioId: string;
  submissionId: string;
}

export const SubmissionService = {
  get: (id: string) => api.get(`/api/submission/${id}`),

  adminGet: () => api.get(`/api/admin-submissions`),

  adminExcelGet: (params: ExcelParams) => api.post(`/api/excel`, { params }),

  temporaryPost: (body: FormData) =>
    api.post("/api/submission/temporaryStorage", body),

  post: (body: FormData) => api.post("/api/submission", body),

  getMyList: () => api.post("/api/submission/my-list"),

  delete: (id: string) => api.delete(`/api/admin-submissions/${id}`),
};
