export interface User {
    name: string;
    lastName: string;
    email: string;
    alias: string;
    birthDate: string;
    password: string;
    vip: boolean;
    enable2FA?: boolean;
    enable3FA?: boolean;
    imageId?: number | null;
    twoFaEnabled?: boolean;
    threeFaEnabled?: boolean;
    role: string;
    blocked: boolean;
    active: boolean;
}

export interface Usuario extends User {
  alias: string;
  birthDate: string;
  vip: boolean;
}

export interface Admin extends User {
  department: string;
}

export interface Creator extends User {
  alias: string;
  description?: string;
  field: string;
  type: string;
}
