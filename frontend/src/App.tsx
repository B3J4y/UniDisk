import React from 'react';
import { Route, Router, Switch } from 'react-router-dom';
import { Provider } from 'unstated-typescript';
import './App.css';
import { UserContainer } from './model/LoginState';
import { getAuthenticationService, provider, ProviderContext } from './Provider';
import { RouteWrapper as AuthRoute } from './Router';
import history from './services/History';
import { StatedLoginForm } from './ui/pages/auth/Login';
import { Dashboard } from './ui/pages/Dashboard';
import Routes from './ui/pages/Routes';

export const userContainer = new UserContainer(getAuthenticationService());

function App(): JSX.Element {
  return (
    <ProviderContext.Provider value={provider}>
      <Provider inject={[userContainer]}>
        <Router history={history}>
          <div className="App" style={{ height: '100%' }}>
            <Switch>
              <AuthRoute exact path="/" component={Dashboard} isPrivate />
              {Routes.map((r) => r)}
              <Route exact path="/login" component={StatedLoginForm} />
            </Switch>
          </div>
        </Router>
      </Provider>
    </ProviderContext.Provider>
  );
}

export default App;
