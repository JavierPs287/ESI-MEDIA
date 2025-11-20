export interface superUser {
    role: string;
    name: string;
    lastName: string;
    email: string;
    imageId?: number | null;

    alias?: string;
    blocked?: boolean;
    active?: boolean;
    
    // Usuario
    birthDate?: string;
    vip?: boolean;
    
    // Creador
    description?: string;
    field?: string;
    type?: string;

    // Admin
    department?: string;


}