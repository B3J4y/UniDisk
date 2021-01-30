import axios, { AxiosInstance } from "axios";

export type AuthToken = {
  token: string;
  userId: string;
};

export interface AuthProvider {
  getAuthToken(): Promise<AuthToken>;
}

export type ApiConfig = {
  baseUrl: string;
};

export class ApiClient {
  private authProvider: AuthProvider;
  private apiConfig: ApiConfig;

  constructor($authProvider: AuthProvider, $apiConfig: ApiConfig) {
    this.authProvider = $authProvider;
    this.apiConfig = $apiConfig;
  }

  public async createClient(): Promise<AxiosInstance> {
    const token = await this.authProvider.getAuthToken();

    const client = axios.create({
      baseURL: this.apiConfig.baseUrl,
      responseType: "json",
      headers: {
        "Content-Type": "application/json",
        Authorization: token.token,
      },
    });
    return client;
  }
}
