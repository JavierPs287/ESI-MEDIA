export interface Admin {
    nombre: string;
    apellidos: string;
    email: string;
    contrasena: string;
    foto: number;
    departamento: Departamento;
}

export enum Departamento {
    RRHH = 'RRHH',
    IT = 'IT',
    MARKETING = 'MARKETING',
    VENTAS = 'VENTAS'
}

export interface AdminRegisterResponse {
    message: string;
    error?: string;
}
