import { User } from 'data';
import IAuthenticationService from 'data/services/Authentication';
import { AuthToken } from 'remote/util';

export class AuthenticationService implements IAuthenticationService {
  onTokenChanged(callback: (token: string) => void): void {}

  login(email: string, password: string): Promise<User | null> {
    throw new Error('Method not implemented.');
  }
  logout(): Promise<void> {
    throw new Error('Method not implemented.');
  }
  restoreSession(): Promise<User | null> {
    throw new Error('Method not implemented.');
  }

  async getAuthToken(): Promise<AuthToken> {
    return {
      userId: '0',
      token: 'Bearer we424',
    };
  }
}
