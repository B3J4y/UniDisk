import { Project, ProjectDetails, ProjectState } from 'data/entity';
import { CreateProjectArgs, ProjectRepository, UpdateProjectArgs } from 'data/repositories';

export class ProjectRepositoryStub implements ProjectRepository {
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
      createdAt: new Date(),
      state: ProjectState.idle,
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
      createdAt: new Date(),
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
