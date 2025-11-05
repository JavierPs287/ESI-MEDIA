export interface User {
    nombre: string;
    apellidos: string;
    email: string;
    alias: string;
    fecha_nacimiento: string | Date;
    contrasena: string;
    vip: boolean;
    foto_perfil?: string | null;
}

export interface RegisterResponse {
    message: string;
    error?: string;
}