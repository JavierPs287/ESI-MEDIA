export interface User {
    nombre: string;
    apellidos: string;
    email: string;
    alias: string;
    fechaNacimiento: string;
    contrasena: string;
    esVIP: boolean;
    fotoPerfil?: string | null;
}