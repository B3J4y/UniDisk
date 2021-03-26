import { API_ENDPOINT, USE_STUBS } from 'config';
import { KeywordRepository, ProjectRepository, TopicRepository } from 'data/repositories';
import { ProjectAllContainer, ProjectDetailContainer } from 'model';
import { KeywordDetailContainer } from 'model/keyword/Detail';
import { FeedbackResultContainer } from 'model/project/Feedback';
import { TopicDetailContainer } from 'model/topic/Detail';
import React, { useContext } from 'react';
import { TopicApiRepository } from 'remote/repository';
import { KeywordApiRepository } from 'remote/repository/api/Keyword';
import { ProjectApiRepository } from 'remote/repository/api/Project';
import { AuthStub } from 'remote/services/Authentication';
import { KeywordRecommendationService } from 'remote/services/KeywordRecommendation';
import { ProjectRepositoryStub, KeywordRepositoryStub, TopicRepositoryStub } from 'remote/stubs';
import { EventBus } from 'services/event/bus';
import IAuthenticationService from './data/services/Authentication';

const eventBus = new EventBus();

const getTokenProvider = () => {
  const authService = getAuthenticationService();
  return {
    getToken: () => authService.getAuthToken().then((v) => v!.token),
    onTokenChange: authService.onTokenChanged,
  };
};

const getRepositoryArgs = () => {
  return {
    endpoint: API_ENDPOINT,
    tokenProvider: getTokenProvider(),
  };
};

const getProjectRepository = (): ProjectRepository => {
  if (USE_STUBS) return new ProjectRepositoryStub();
  return new ProjectApiRepository(getRepositoryArgs());
};

const getTopicRepository = (): TopicRepository => {
  if (USE_STUBS) return new TopicRepositoryStub();
  return new TopicApiRepository(getRepositoryArgs());
};

const getKeywordRepository = (): KeywordRepository => {
  if (USE_STUBS) return new KeywordRepositoryStub();
  return new KeywordApiRepository(getRepositoryArgs());
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
  getFeedbackResultContainer: () => {
    return new FeedbackResultContainer(getProjectRepository(), eventBus);
  },
};

export const ProviderContext = React.createContext(provider);

export function useProvider() {
  return useContext(ProviderContext);
}

export const getAuthenticationService = (): IAuthenticationService => {
  return new AuthStub();
};
