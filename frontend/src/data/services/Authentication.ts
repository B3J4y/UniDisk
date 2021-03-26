import { User } from '../user/User';

export type AuthToken = {
  token: string;
  userId: string;
};

export interface AuthProvider {
  getAuthToken(): Promise<AuthToken>;
  onTokenChanged(callback: (token: string) => void): void;
}

export default interface IAuthenticationService extends AuthProvider {
  login(email: string, password: string): Promise<User | null>;
  logout(): Promise<void>;

  restoreSession(): Promise<User | null>;
}
