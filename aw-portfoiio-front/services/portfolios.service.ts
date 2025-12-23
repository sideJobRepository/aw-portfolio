import api from "@/lib/axiosInstance";
import { Params } from "@/components/Pagination";

export const PortfolioService = {
  get: (params: Params) => api.get("/api/portfolio", { params }),

  getUser: (active: boolean, categoryId: string | null) => {
    let url = `/api/portfolios?active=${active}`;
    if (categoryId) {
      url += `&categoryId=${categoryId}`;
    }
    return api.get(url);
  },

  getCategorySelect: () => api.get("/api/categorys"),

  post: (body: FormData) => api.post("/api/portfolio", body),

  put: (body: FormData) => api.put("/api/portfolio", body),

  delete: (id: string) => api.delete(`/api/portfolio/${id}`),
};
