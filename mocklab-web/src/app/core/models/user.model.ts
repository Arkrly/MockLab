export interface User {
  id: number;
  name: string;
  email: string;
  plan: 'FREE' | 'PRO';
  createdAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  name: string;
  plan: string;
}
