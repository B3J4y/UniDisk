import { Container } from 'unstated-typescript';
import { Operation, Resource } from '../data/Resource';
import IAuthenticationService from '../data/services/Authentication';
import { User } from '../data/user/User';

export type UserState = {
  user: Resource<User>;
  setup: Operation;
  logout: Operation;
};

export class UserContainer extends Container<UserState> {
  private authService: IAuthenticationService;

  constructor(authService: IAuthenticationService) {
    super();
    this.authService = authService;
    this.restoreSession();
  }

  state = {
    user: Resource.idle<User>(),
    setup: Operation.idle(),
    logout: Operation.idle(),
  };

  private async restoreSession() {
    this.setState({ setup: Operation.loading() });
    try {
      const user = await this.authService.restoreSession();
      if (user == null) {
        this.setState({ user: Resource.idle(), setup: Operation.success() });
        return;
      }

      this.setState({
        user: Resource.success(user),
        setup: Operation.success(),
      });
    } catch (e) {
      console.log(e);
      this.setState({
        setup: Operation.failure('Unable to restore user session.'),
      });
    }
  }

  async logout(): Promise<void> {
    this.setState({ ...this.state, logout: Operation.idle() });
    try {
      await this.authService.logout();
      this.setState({ ...this.state, logout: Operation.success(), user: Resource.idle() });
    } catch (e) {
      console.error(e);
      this.setState({ ...this.state, logout: Operation.failure('Fehler beim Logout...') });
    }
  }

  async login(email: string, password: string): Promise<boolean> {
    this.setState({ user: Resource.loading() });
    try {
      const user = await this.authService.login(email, password);
      if (!user) {
        this.setState({ user: Resource.failure('Nicht authorisiert.') });
        return false;
      }

      this.setState({
        ...this.state,
      });

      this.setState({
        ...this.state,
        user: Resource.success(user),
      });

      return true;
    } catch (e) {
      console.error(e);
      let error = 'Unbekannter Fehler aufgetreten.';
      const { code } = e;
      if (code) {
        if (code === 'auth/wrong-password') {
          error = 'Falsche E-Mail oder falsches Passwort.';
        }
      }

      this.setState({ user: Resource.failure(error) });
    }
    return false;
  }

  get isAuthenticated(): boolean {
    return this.state.user.hasData;
  }

  get isAuthorized(): boolean {
    return this.isAuthenticated && this.state.user.data != null;
  }
}
