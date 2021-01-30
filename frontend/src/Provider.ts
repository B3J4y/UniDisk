import { EventBus } from 'services/event/bus';
import IAuthenticationService from './data/services/Authentication';
import { AuthStub } from './remote/services/Authentication';

const eventBus = new EventBus();

export const AuthenticationService = (): IAuthenticationService => {
  // return new FirebaseAuthService(UserRepository());
  return new AuthStub();
};

function getUserId(): Promise<string> {
  return AuthenticationService()
    .getAuthToken()
    .then((token) => token.userId);
}
