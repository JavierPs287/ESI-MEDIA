export interface Creator {
    nombre: string;
    apellidos: string;
    email: string;
    contrasena: string;
    foto: number;
    alias: string;
    descripcion?: string;
    campo: Campo;
    tipo: Tipo;
}

export enum Campo {
    PELICULA = 'PELICULA',
    SERIE = 'SERIE',
    LIBRO = 'LIBRO',
    VIDEOJUEGO = 'VIDEOJUEGO',
    MUSICA = 'MUSICA'
}

export enum Tipo {
    AUDIO = 'AUDIO',
    VIDEO = 'VIDEO'
}

export interface CreatorRegisterResponse {
    message: string;
    error?: string;
}
