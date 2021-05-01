import { Project, ProjectDetails, ProjectState, TopicResult } from 'data/entity';
import {
  CreateProjectArgs,
  FeedbackStatus,
  ProjectEvaluationResult,
  ProjectRelevanceScore,
  ProjectRepository,
  ProjectResult,
  ProjectType,
  RateResultArgs,
  UpdateProjectArgs,
} from 'data/repositories';
import { BaseApiRepository, RepositoryArgs } from './Base';
import {
  CreateProjectDto,
  ProjectModelDto,
  ProjectResultDto,
  ProjectStateDto,
  ProjectSubtypeDto,
  ResultRelevanceDto,
  UpdateProjectDto,
} from './dto/Project';

import { mapModelDtoToEntity as mapTopicDto } from './dto/Topic';

function mapDtoState(state: ProjectStateDto): ProjectState {
  switch (state) {
    case ProjectStateDto.idle:
      return ProjectState.idle;
    case ProjectStateDto.completed:
      return ProjectState.completed;
    case ProjectStateDto.error:
      return ProjectState.error;
    case ProjectStateDto.processing:
      return ProjectState.processing;
    case ProjectStateDto.ready:
      return ProjectState.ready;
    default:
      return ProjectState.idle;
  }
}

function mapModelDtoToEntity(dto: ProjectModelDto): Project {
  return {
    id: dto.id.toString(),
    name: dto.name,
    state: mapDtoState(dto.projectState),
  };
}

function mapFromRelevanceDto(state: ResultRelevanceDto): FeedbackStatus {
  switch (state) {
    case ResultRelevanceDto.relevant:
      return FeedbackStatus.Relevant;
    case ResultRelevanceDto.notRelevant:
      return FeedbackStatus.NotRelevant;
    default:
      return FeedbackStatus.None;
  }
}

function mapRelevanceToDto(state: FeedbackStatus): ResultRelevanceDto {
  switch (state) {
    case FeedbackStatus.Relevant:
      return ResultRelevanceDto.relevant;
    case FeedbackStatus.NotRelevant:
      return ResultRelevanceDto.notRelevant;
    default:
      return ResultRelevanceDto.none;
  }
}

function projectTypeFromDto(type: ProjectSubtypeDto): ProjectType {
  switch (type) {
    case ProjectSubtypeDto.ByTopics:
      return ProjectType.ByTopic;
    case ProjectSubtypeDto.CustomOnly:
      return ProjectType.Enhanced;
    default:
      return ProjectType.Default;
  }
}

export class ProjectApiRepository extends BaseApiRepository implements ProjectRepository {
  public constructor(args: RepositoryArgs) {
    super({ ...args, defaultPath: 'project' });
  }

  rateResult(args: RateResultArgs): Promise<void> {
    const { topicId, url, relevance } = args;
    return this.withDefaultClient<void>(async (client) => {
      const dto = {
        relevance: mapRelevanceToDto(relevance),
        topicId: topicId,
        url: url,
      };
      await client.post(`/result/topic`, dto);
    });
  }

  getResult(id: string): Promise<ProjectEvaluationResult | undefined> {
    return this.withClient<ProjectEvaluationResult>(async (client) => {
      const response = await client.get(`/${id}/results`);
      const resultDto = response.data as ProjectResultDto[];

      const scores: [ProjectType, TopicResult[], ProjectRelevanceScore[]][] = resultDto.map(
        (dto) => {
          const projectType = projectTypeFromDto(dto.projectSubtype);

          const results: TopicResult[] = dto.results.map((result) => {
            const { topic: topicName, topicId, university, id, keywords } = result;
            return {
              id: id.toString(),
              keywords: keywords.map((keyword) => {
                return {
                  ...keyword,
                  relevance: mapFromRelevanceDto(keyword.relevance),
                };
              }),
              topic: { id: topicId.toString(), name: topicName },
              score: result.score,
              entryCount: result.entryCount,
              university: {
                ...university,
                id: university.id.toString(),
              },
            };
          });

          const relevanceScores: ProjectRelevanceScore[] = dto.relevanceScores.map((score) => {
            return {
              url: score.searchMetaData.url,
              topicId: score.topicId.toString(),
              relevance: mapFromRelevanceDto(score.resultRelevance),
            };
          });
          return [projectType, results, relevanceScores];
        },
      );
      const projectResults = {
        results: scores.reduce((prev, [type, results, relevanceScores]) => {
          const projectResult: ProjectResult = {
            topicResults: results,
            relevanceScores,
          };

          return {
            ...prev,
            [type]: projectResult,
          };
        }, {} as Record<ProjectType, ProjectResult>),
      };
      console.log(projectResults);
      return projectResults;
    });
  }

  findAll(): Promise<Project[]> {
    return this.withClient<Project[]>(async (client) => {
      const response = await client.get('/all');
      const projectDtos = response.data as ProjectModelDto[];

      return projectDtos.map(mapModelDtoToEntity);
    });
  }

  get(id: string): Promise<ProjectDetails | undefined> {
    return this.withClient<ProjectDetails | undefined>(async (client) => {
      // Accept every status to prevent Axios from throwing error
      const response = await client.get(`/${id}`, { validateStatus: () => true });
      if (response.status === 404) return undefined;

      if (response.status !== 200) throw response.statusText;

      const { data } = response;
      return {
        ...mapModelDtoToEntity(data),
        topics: data.topics.map(mapTopicDto),
      };
    });
  }

  create(args: CreateProjectArgs): Promise<Project> {
    const dto: CreateProjectDto = {
      name: args.name,
    };

    return this.withClient<Project>(async (client) => {
      const response = await client.post('', dto);
      return mapModelDtoToEntity(response.data);
    });
  }

  update(args: UpdateProjectArgs): Promise<Project> {
    const dto: UpdateProjectDto = {
      name: args.name,
    };

    return this.withClient<Project>(async (client) => {
      const response = await client.put(`/${args.projectId}`, dto);
      return mapModelDtoToEntity(response.data);
    });
  }

  delete(id: string): Promise<void> {
    return this.withClient<void>(async (client) => {
      await client.delete(`/${id}`);
    });
  }

  enqueue(projectId: string): Promise<void> {
    return this.withClient<void>(async (client) => {
      await client.post(`/${projectId}/enqueue`);
    });
  }
  dequeue(projectId: string): Promise<void> {
    return this.withClient<void>(async (client) => {
      await client.post(`/${projectId}/dequeue`);
    });
  }
}
