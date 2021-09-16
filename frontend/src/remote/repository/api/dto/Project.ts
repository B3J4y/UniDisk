import { KeywordResult } from 'data/entity';
import { TopicModelDto } from './Topic';

export type CreateProjectDto = {
  name: string;
};

export type UpdateProjectDto = {
  name: string;
};

export enum ProjectStateDto {
  idle = 'IDLE',
  ready = 'WAITING',
  processing = 'RUNNING',
  error = 'ERROR',
  completed = 'FINISHED',
}

export type ProjectModelDto = {
  id: number;
  name: string;
  userId: string;
  projectState: ProjectStateDto;
  processingError: string;
  topics: TopicModelDto[];
};

export enum ResultRelevanceDto {
  relevant = 'RELEVANT',
  notRelevant = 'NOT_RELEVANT',
  none = 'NONE',
}

export type KeywordResultDto = Omit<KeywordResult, 'relevance'> & {
  relevance: ResultRelevanceDto;
};

export type ResultDto = {
  id: number;
  topicId: number;
  topic: string;
  score: number;
  entryCount: number;
  university: {
    id: number;
    name: string;
    lat: number;
    lng: number;
    seedUrl: string;
  };
  keywords: KeywordResultDto[];
};

export enum ProjectSubtypeDto {
  Default = 'DEFAULT',
  CustomOnly = 'CUSTOM_ONLY',
  ByTopics = 'BY_TOPICS',
}

export type RelevanceScoreDto = {
  topicId: number;
  resultRelevance: ResultRelevanceDto;
  searchMetaData: {
    url: string;
  };
};

export type ProjectResultDto = {
  projectSubtype: ProjectSubtypeDto;
  results: ResultDto[];
  relevanceScores: RelevanceScoreDto[];
};
