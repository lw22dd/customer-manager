import type { Result } from "../models/Result";
import type { User } from "../models/UserModel";
import Axios from "../utils/axios";

export default class UserApi {
    public static async login(username: string, password: string): Promise<Result<string | null>> {
        console.log('login', username, password);

        return await Axios.post('/user/login', { username, password });
    }

    public static async register(user: User): Promise<Result<string | null>> {
        return await Axios.post('/user/register', user);
    }

    public static async logout(): Promise<Result<boolean>> {
        return await Axios.post('/user/logout');
    }
    public static async test(str?: string): Promise<Result<string | null>> {
        return await Axios.get('/user/test', { params: { str } });
    }
    public static async getCurrentUser(): Promise<Result<User | null>> {
        return await Axios.get('/user/info');
    }
    public static async updateUserInfo(user: User): Promise<Result<boolean>> {
        return await Axios.put('/user/info', user);
    }
    public static async changePassword(oldPassword: string, newPassword: string): Promise<Result<boolean>> {
        return await Axios.post('/user/change-password', { oldPassword, newPassword });
    }
    public static async getUserList(): Promise<Result<User[]>> {
        return await Axios.get('/user/list');
    }

}