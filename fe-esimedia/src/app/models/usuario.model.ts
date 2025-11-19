export interface Usuario {
    name: string;
    lastName: string;
    email: string;
    alias: string;
    birthDate: string;
    password: string;
    vip: boolean;
    enable2FA?: boolean;
    imageId?: number | null;
}