export interface User {
    name: string;
    lastName: string;
    email: string;
    alias: string;
    birthDate: string;
    password: string;
    vip: boolean;
    imageId?: number | null;
}