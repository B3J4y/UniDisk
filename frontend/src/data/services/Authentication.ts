import { User } from '../user/User';
import { AuthProvider } from '../../remote/util';

export default interface IAuthenticationService extends AuthProvider {
  login(email: string, password: string): Promise<User | null>;
  logout(): Promise<void>;

  restoreSession(): Promise<User | null>;
}
