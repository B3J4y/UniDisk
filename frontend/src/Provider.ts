import { ProjectRepository } from 'data/repositories';
import { ProjectAllContainer, ProjectDetailContainer } from 'model';
import React, { useContext } from 'react';
import { ProjectRepositoryStub } from 'remote/stubs/ProjectRepository';
import { EventBus } from 'services/event/bus';
import IAuthenticationService from './data/services/Authentication';
import { AuthStub } from './remote/services/Authentication';

const eventBus = new EventBus();

const getProjectRepository = (): ProjectRepository => {
  return new ProjectRepositoryStub();
};

export const provider = {
  getAllProjectContainer: () => {
    return new ProjectAllContainer(getProjectRepository(), eventBus);
  },
  getProjectDetailContainer: () => {
    return new ProjectDetailContainer(getProjectRepository(), eventBus);
  },
};

export const ProviderContext = React.createContext(provider);

export function useProvider() {
  return useContext(ProviderContext);
}

export const AuthenticationService = (): IAuthenticationService => {
  return new AuthStub();
};
