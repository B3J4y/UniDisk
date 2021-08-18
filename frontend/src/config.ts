export type Stage = 'dev' | 'prod';

export const stage: Stage = process.env.REACT_APP_STAGE === 'prod' ? 'prod' : 'dev';

export const THEME = { primary: '#2b669a', colorOnPrimary: 'white' };

// Determines whether stubs or API endpoints are used.
export const USE_STUBS = false;

const LOCAL_DOCKER_ENDPOINT = 'http://localhost:80/api/unidisk/rest/';
// Local endpoint when using npm run start
const LOCAL_DOCKER_DEV_ENDPOINT = 'http://localhost:8081/unidisk/rest/';
//IntelliJ
const LOCAL_DEV_ENDPOINT = 'http://localhost:8080/unidisk_war/rest/';

export const API_ENDPOINT = process.env.API_ENDPOINT ?? LOCAL_DOCKER_ENDPOINT;

export const KEYWORD_SERVICE_ENDPOINT =
  process.env.RECOMMENDATION_ENDPOINT ?? 'http://localhost:80/recommendation';

export const USE_FIREBASE_AUTH = true;
