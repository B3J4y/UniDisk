import { User } from 'data';
import IAuthenticationService, { AuthToken } from 'data/services/Authentication';
import firebase from 'firebase';

type TokenCallback = (token: string) => void;

export class FirebaseAuthenticationService implements IAuthenticationService {
  private firebaseRestoreStatePromise: Promise<void>;

  tokenCallback?: TokenCallback;

  constructor() {
    this.firebaseRestoreStatePromise = new Promise((resolve, reject) => {
      const unsubscribe = firebase.auth().onAuthStateChanged((user) => {
        unsubscribe();
        resolve();
      }, reject);
    });

    firebase.auth().onIdTokenChanged(async (user) => {
      if (!user || !this.tokenCallback) return;

      const token = await user.getIdToken();
      const bearerToken = `Bearer ${token}`;

      this.tokenCallback(bearerToken);
    });
  }

  public async login(email: string, password: string): Promise<User | null> {
    const result = await firebase.auth().signInWithEmailAndPassword(email, password);
    const user = result.user;
    if (!user) return null;

    return { id: user.uid, email: user.email! };
  }

  public async restoreSession(): Promise<User | null> {
    await this.firebaseRestoreStatePromise;
    const user = await firebase.auth().currentUser;
    if (user) return { id: user.uid, email: user.email! };
    return null;
  }

  public async getAuthToken(): Promise<AuthToken> {
    const current = firebase.auth().currentUser;

    const token = await current!.getIdToken();

    return {
      token: `Bearer ${token}`,
      userId: current!!.uid,
    };
  }

  onTokenChanged(callback: (token: string) => void): void {
    this.tokenCallback = callback;
  }

  async logout(): Promise<void> {
    await firebase.auth().signOut();
  }
}
