import React from 'react';
import { RouteWrapper as AuthRoute } from './../../Router';

import { Dashboard } from './Dashboard';

export default [<AuthRoute exact path="/" component={Dashboard} isPrivate />];
