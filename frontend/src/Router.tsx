import { Box, CircularProgress } from '@material-ui/core';
import { THEME } from 'config';

import React from 'react';
import { Redirect, Route } from 'react-router-dom';
import DefaultLayout from 'ui/pages/layouts/Default';
import { Subscribe } from 'unstated-typescript';
import { UserContainer } from './model/LoginState';

export const PrivateRoute = ({ component, ...rest }: any) => {
  return (
    <Subscribe to={[UserContainer]}>
      {(userContainer) => {
        const isAuthenticated = userContainer.isAuthorized;

        if (!isAuthenticated)
          return <Route {...rest} render={(_) => <Redirect to={{ pathname: '/login' }} />} />;

        const routeComponent = (props: any) => React.createElement(component, props);
        return <Route {...rest} render={routeComponent} />;
      }}
    </Subscribe>
  );
};

function LoadingScreen() {
  return (
    <Box
      bgcolor={THEME.primary}
      height="100%"
      width="100%"
      display="flex"
      alignItems="center"
      justifyContent="center"
    >
      <CircularProgress style={{ color: THEME.colorOnPrimary }}></CircularProgress>
    </Box>
  );
}

export const RouteWrapper = ({
  component: Component,

  isPrivate = false,
  ...rest
}: any) => {
  return (
    <Subscribe to={[UserContainer]}>
      {(c) => {
        const { isAuthorized } = c;

        if (c.state.setup.isIdle || c.state.setup.isLoading) {
          return <LoadingScreen />;
        }

        if (!isAuthorized) {
          /**
           * Redirect user to SignIn page if he tries to access a private route
           * without authentication.
           */
          if (isPrivate) {
            return (
              <Route
                {...rest}
                render={({ location }) => {
                  return (
                    <Redirect
                      to={{
                        pathname: '/login',
                        state: { from: location, originalLocation: location.pathname },
                      }}
                    />
                  );
                }}
              />
            );
          }
        }

        return (
          <Route
            {...rest}
            render={(props) => {
              return (
                <DefaultLayout>
                  <Component {...props} />
                </DefaultLayout>
              );
            }}
          />
        );
      }}
    </Subscribe>
  );
};
