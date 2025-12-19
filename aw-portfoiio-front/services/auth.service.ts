import axios from "axios";
import api from "@/lib/axiosInstance";

const baseURL = process.env.NEXT_PUBLIC_API_BASE_URL;

export const AuthService = {
  login: (loginId: string, password: string) =>
    axios.post(
      "/api/admin-login",
      { loginId, password },
      {
        baseURL,
        withCredentials: true,
      },
    ),

  me: () => api.get("/auth/me"),
};
