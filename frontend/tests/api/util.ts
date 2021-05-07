export const getTestAPIToken = async (): Promise<string> => {
  return 'Bearer testat';
};

export const TEST_API_ENDPOINT = process.env.ENDPOINT ?? 'http://localhost:8080/unidisk_war/rest/';
