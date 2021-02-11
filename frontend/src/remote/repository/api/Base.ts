import axios, { AxiosInstance } from 'axios';

export type TokenProvider = {
  getToken: () => Promise<string>;
  onTokenChange: (callback: (token: string) => void) => void;
};

export type RepositoryArgs = {
  endpoint: string;
  tokenProvider: TokenProvider;
  defaultPath?: string;
};

export abstract class BaseApiRepository {
  private client: AxiosInstance | undefined;

  private setup: Promise<void>;

  protected constructor(args: RepositoryArgs) {
    const { endpoint, tokenProvider } = args;
    this.setup = new Promise(async (resolve, reject) => {
      try {
        const token = await tokenProvider.getToken();
        this.client = axios.create({
          baseURL: endpoint + (args.defaultPath ?? ''),
          headers: {
            Authorization: token,
          },
        });
        resolve();
      } catch (e) {
        reject(e);
      }
    });

    tokenProvider.onTokenChange((token) => {
      axios.create({
        baseURL: endpoint + (args.defaultPath ?? ''),
        headers: {
          Authorization: token,
        },
      });
    });
  }

  protected async getClient(): Promise<AxiosInstance> {
    await this.setup;
    return this.client!;
  }

  protected async withClient<T>(callback: (client: AxiosInstance) => Promise<T>): Promise<T> {
    const client = await this.getClient();
    return await callback(client);
  }
}
