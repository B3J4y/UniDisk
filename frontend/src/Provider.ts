import { API_ENDPOINT } from 'config';
import { KeywordRepository, ProjectRepository, TopicRepository } from 'data/repositories';
import { ProjectAllContainer, ProjectDetailContainer } from 'model';
import { KeywordDetailContainer } from 'model/keyword/Detail';
import { TopicDetailContainer } from 'model/topic/Detail';
import React, { useContext } from 'react';
import { ProjectApiRepository } from 'remote/repository/api/Project';
import { AuthStub } from 'remote/services/Authentication';
import { KeywordRecommendationService } from 'remote/services/KeywordRecommendation';
import { KeywordRepositoryStub } from 'remote/stubs/KeywordRepository';
import { TopicRepositoryStub } from 'remote/stubs/TopicRepository';
import { EventBus } from 'services/event/bus';
import IAuthenticationService from './data/services/Authentication';

const eventBus = new EventBus();

const getProjectRepository = (): ProjectRepository => {
  const authService = getAuthenticationService();

  return new ProjectApiRepository({
    endpoint: API_ENDPOINT,
    tokenProvider: {
      getToken: () => authService.getAuthToken().then((v) => v!.token),
      onTokenChange: authService.onTokenChanged,
    },
  });
};

const getTopicRepository = (): TopicRepository => {
  return new TopicRepositoryStub();
};

const getKeywordRepository = (): KeywordRepository => {
  return new KeywordRepositoryStub();
};

export const provider = {
  getAllProjectContainer: () => {
    return new ProjectAllContainer(getProjectRepository(), eventBus);
  },
  getProjectDetailContainer: () => {
    return new ProjectDetailContainer(getProjectRepository(), eventBus);
  },
  getTopicDetailContainer: () => {
    return new TopicDetailContainer(getTopicRepository(), eventBus);
  },
  getKeywordDetailContainer: () => {
    return new KeywordDetailContainer(getKeywordRepository(), eventBus);
  },
  getKeywordRecommendationService: () => {
    return new KeywordRecommendationService();
  },
};

export const ProviderContext = React.createContext(provider);

export function useProvider() {
  return useContext(ProviderContext);
}

export const getAuthenticationService = (): IAuthenticationService => {
  return new AuthStub();
};
