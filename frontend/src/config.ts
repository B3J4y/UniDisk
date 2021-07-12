export type Stage = 'dev' | 'prod';

export const stage: Stage = process.env.REACT_APP_STAGE === 'prod' ? 'prod' : 'dev';

export const THEME = { primary: '#2b669a', colorOnPrimary: 'white' };

// Determines whether stubs or API endpoints are used.
export const USE_STUBS = false;

const LOCAL_DEV_ENDPOINT = 'http://localhost:8080/unidisk_war/rest/';

export const API_ENDPOINT = LOCAL_DEV_ENDPOINT;
export const USE_FIREBASE_AUTH = true;
