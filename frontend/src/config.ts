export type Stage = 'dev' | 'prod';

export const stage: Stage = process.env.REACT_APP_STAGE === 'prod' ? 'prod' : 'dev';

const isProd = stage == 'prod';

export const THEME = { primary: '#2b669a', colorOnPrimary: 'white' };

// Determines whether stubs or API endpoints are used.
export const USE_STUBS = false;

//IntelliJ
const LOCAL_DEV_ENDPOINT = 'http://localhost:8080/unidisk_war/rest/';
const LOCAL_RECOMMENDATION_ENDPOINT = 'http://localhost:8083';

var _API_ENDPOINT = LOCAL_DEV_ENDPOINT;
var _KEYWORD_SERVICE_ENDPOINT = LOCAL_RECOMMENDATION_ENDPOINT;

if (isProd) {
  const uniUrl = 'http://stud-01.cs.uni-potsdam.de:80';
  _API_ENDPOINT = `${uniUrl}/api/unidisk/rest/`;
  _KEYWORD_SERVICE_ENDPOINT = `${uniUrl}/recommendation`;
}

export const API_ENDPOINT = _API_ENDPOINT;
export const KEYWORD_SERVICE_ENDPOINT = _KEYWORD_SERVICE_ENDPOINT;
export const USE_FIREBASE_AUTH = true;
