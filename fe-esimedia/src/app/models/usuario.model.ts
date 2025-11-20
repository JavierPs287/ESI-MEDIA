export interface Usuario {
    name: string;
    lastName: string;
    email: string;
    alias: string;
    birthDate: string;
    password: string;
    vip: boolean;
    enable2FA?: boolean;
    enable3FA?: boolean;
    twoFaEnabled?: boolean;
    threeFaEnabled?: boolean;
    imageId?: number | null;
}