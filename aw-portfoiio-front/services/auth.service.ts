// services/auth.service.ts
import api from "@/lib/axiosInstance";

export const AuthService = {
    login: (email : string, password : string) =>
        api.post('/auth/login', { email, password }),

    me: () =>
         api.get('/auth/me'),
};

// 호출단 예시
// const handleLogin = async () => {
//     await request(
//         () => AuthService.login(email, password),
//         (res) => {
//             const { token, user } = res.data;
//             localStorage.setItem('token', token);
//             localStorage.setItem('user', JSON.stringify(user));
//             router.push('/admin');
//         },
//         { ignoreErrorRedirect: true }
//     );
// };