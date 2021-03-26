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
};

export enum ResultRelevanceDto {
  relevant = 'RELEVANT',
  notRelevant = 'NOT_RELEVANT',
  none = 'NONE',
}

export type ResultDto = {
  id: number;
  topicId: number;
  topic: string;
  score: number;
  entryCount: number;
  relevance: ResultRelevanceDto;
  university: {
    id: number;
    name: string;
    lat: number;
    lng: number;
    seedUrl: string;
  };
};

export enum ProjectSubtypeDto {
  Default = 'DEFAULT',
  CustomOnly = 'CUSTOM_ONLY',
  ByTopics = 'BY_TOPICS',
}

export type ProjectResultDto = {
  projectSubtype: ProjectSubtypeDto;
  results: ResultDto[];
};
