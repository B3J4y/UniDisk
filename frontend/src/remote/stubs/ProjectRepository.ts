import { Project } from 'data/entity';
import { CreateProjectArgs, ProjectRepository, UpdateProjectArgs } from 'data/repositories';

export class ProjectRepositoryStub implements ProjectRepository {
  projects: Project[] = [];

  async findAll(): Promise<Project[]> {
    return this.projects;
  }

  async get(id: string): Promise<Project | undefined> {
    return this.projects.find((p) => p.id === id);
  }

  async create(args: CreateProjectArgs): Promise<Project> {
    const project: Project = {
      id: new Date().getTime().toString(),
      name: args.name,
      createdAt: new Date(),
    };

    this.projects.push(project);
    return project;
  }

  async update(args: UpdateProjectArgs): Promise<Project> {
    const project = await this.get(args.projectId);
    if (!project) throw new Error('Project existiert nicht.');
    const updated = { ...project, name: args.name };
    this.projects = this.projects.map((p) => (p.id === args.projectId ? updated : p));
    return updated;
  }

  async delete(id: string): Promise<void> {
    this.projects = this.projects.filter((p) => p.id !== id);
  }
}
