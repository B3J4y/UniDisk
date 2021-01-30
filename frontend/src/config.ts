export type Stage = 'dev' | 'prod';

export const stage: Stage = process.env.REACT_APP_STAGE === 'prod' ? 'prod' : 'dev';

export const THEME = { primary: '#A0C55F', colorOnPrimary: 'white' };
