import { Project, ProjectDetails, ProjectState } from 'data/entity';
import {
  CreateProjectArgs,
  ProjectEvaluationResult,
  ProjectRepository,
  UpdateProjectArgs,
} from 'data/repositories';
import { BaseApiRepository, RepositoryArgs } from './Base';
import {
  CreateProjectDto,
  ProjectModelDto,
  ProjectStateDto,
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

export class ProjectApiRepository extends BaseApiRepository implements ProjectRepository {
  public constructor(args: RepositoryArgs) {
    super({ ...args, defaultPath: 'project' });
  }
  getResult(id: string): Promise<ProjectEvaluationResult | undefined> {
    throw new Error('Method not implemented.');
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

      console.log(response);

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
