import React from 'react';
import { RouteWrapper as AuthRoute } from './../../Router';
import { Dashboard } from './Dashboard';
import { ProjectDetailsPage } from './project/Detail';

export default [
  <AuthRoute exact path="/" layout="header" component={Dashboard} isPrivate />,
  <AuthRoute exact path="/project/:projectId" component={ProjectDetailsPage} isPrivate />,
];
