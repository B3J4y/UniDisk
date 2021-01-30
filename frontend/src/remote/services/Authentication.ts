import IAuthenticationService from '../../data/services/Authentication';
import { User } from '../../data/user/User';
import { AuthToken } from '../util';

export class AuthStub implements IAuthenticationService {
  private user: User = {
    email: 'test@web.de',
    id: '0',
  };

  login(email: string, password: string): Promise<User | null> {
    return Promise.resolve(this.user);
  }

  logout(): Promise<void> {
    return Promise.resolve();
  }

  restoreSession(): Promise<User | null> {
    return Promise.resolve(this.user);
  }

  getAuthToken(): Promise<AuthToken> {
    return Promise.resolve({ token: '15', userId: '15' });
  }
}
