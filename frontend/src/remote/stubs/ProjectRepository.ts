import { Project, ProjectDetails, ProjectState } from 'data/entity';
import {
  CreateProjectArgs,
  ProjectEvaluationResult,
  ProjectRepository,
  UpdateProjectArgs,
} from 'data/repositories';

export class ProjectRepositoryStub implements ProjectRepository {
  public async getResult(id: string): Promise<ProjectEvaluationResult | undefined> {
    const project = ProjectRepositoryStub.projects.find((p) => p.id === id);
    if (!id) return undefined;

    return {
      topicScores:
        project?.topics.map((topic) => {
          return {
            topic,
            university: {
              id: '15',
              name: 'Uni Potsdam',
              lat: 10,
              lng: 5,
            },
            score: 0.5,
            entryCount: 20,
          };
        }) ?? [],
    };
  }

  public async enqueue(projectId: string): Promise<void> {
    ProjectRepositoryStub.projects = ProjectRepositoryStub.projects.map((project) => {
      if (project.id === projectId) {
        return { ...project, state: ProjectState.ready };
      }
      return project;
    });
  }

  public async dequeue(projectId: string): Promise<void> {
    ProjectRepositoryStub.projects = ProjectRepositoryStub.projects.map((project) => {
      if (project.id === projectId) {
        return { ...project, state: ProjectState.idle };
      }
      return project;
    });
  }

  static projects: ProjectDetails[] = [
    {
      id: '15',
      name: 'test',
      state: ProjectState.idle,
      topics: [
        {
          id: '0',
          name: 'Software',
          keywords: [],
        },
      ],
    },
    {
      id: '14',
      name: 'completed',
      state: ProjectState.completed,
      topics: [
        {
          id: '0',
          name: 'Software',
          keywords: [],
        },
      ],
    },
    {
      id: '17',
      name: 'error',
      state: ProjectState.error,
      topics: [
        {
          id: '0',
          name: 'Software',
          keywords: [],
        },
      ],
    },
  ];

  async findAll(): Promise<Project[]> {
    return [...ProjectRepositoryStub.projects];
  }

  async get(id: string): Promise<ProjectDetails | undefined> {
    const project = ProjectRepositoryStub.projects.find((p) => p.id === id);
    if (!project) return undefined;
    return { ...project };
  }

  async create(args: CreateProjectArgs): Promise<Project> {
    const project: Project = {
      id: new Date().getTime().toString(),
      name: args.name,
      state: ProjectState.idle,
    };

    ProjectRepositoryStub.projects.push({ ...project, topics: [] });

    return { ...project };
  }

  async update(args: UpdateProjectArgs): Promise<Project> {
    const project = await this.get(args.projectId);
    if (!project) throw new Error('Project existiert nicht.');
    const updated = { ...project, name: args.name };
    ProjectRepositoryStub.projects = ProjectRepositoryStub.projects.map((p) =>
      p.id === args.projectId ? updated : p,
    );
    return { ...updated };
  }

  async delete(id: string): Promise<void> {
    ProjectRepositoryStub.projects = ProjectRepositoryStub.projects.filter((p) => p.id !== id);
  }
}
