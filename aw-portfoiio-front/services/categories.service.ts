import api from "@/lib/axiosInstance";
import { Params } from "@/components/Pagination";

export const CategoriesService = {
  get: (params: Params) => api.get("/api/category", { params }),

  me: () => api.get("/auth/me"),
};
