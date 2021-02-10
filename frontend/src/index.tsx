import { red } from '@material-ui/core/colors';
import { deDE } from '@material-ui/core/locale';
import { createMuiTheme, MuiThemeProvider } from '@material-ui/core/styles';
import { THEME } from 'config';
import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import './index.css';
import * as serviceWorker from './serviceWorker';

const theme = createMuiTheme(
  {
    palette: {
      primary: { main: THEME.primary },
      error: red,
    },
  },
  deDE,
);

ReactDOM.render(
  <React.StrictMode>
    <MuiThemeProvider theme={theme}>
      <App />
    </MuiThemeProvider>
  </React.StrictMode>,
  document.getElementById('root'),
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
