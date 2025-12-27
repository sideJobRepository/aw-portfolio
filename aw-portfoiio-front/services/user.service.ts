import {Params} from "@/components/Pagination";
import api from "@/lib/axiosInstance";

interface user {
    email: string;
    password: string;
    name: string;
    role: string | null;

}

export const UserService = {
    get: (params: Params) => api.get("/api/user-list", { params }),

    post: (newUser : user) => api.post(`/api/user-list` , newUser),

};
