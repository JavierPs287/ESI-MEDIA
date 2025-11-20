export interface Creator {
    name: string;
    lastName: string;
    email: string;
    alias: string;
    imageId: number;
    description?: string;
    field: string;
    type: string;
    password: string;
    twoFaEnabled?: boolean;
    threeFaEnabled?: boolean;
}