export interface Admin {
    name: string;
    lastName: string;
    email: string;
    department: string;
    imageId: number;
    password: string;
    twoFaEnabled?: boolean;
    threeFaEnabled?: boolean;
}
