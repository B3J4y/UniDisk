import { Project, ProjectDetails } from 'data/entity';

export type CreateProjectArgs = {
  name: string;
};

export type UpdateProjectArgs = {
  name: string;
  projectId: Project['id'];
};

export interface ProjectRepository {
  findAll(): Promise<Project[]>;

  get(id: Project['id']): Promise<ProjectDetails | undefined>;

  create(args: CreateProjectArgs): Promise<Project>;
  update(args: UpdateProjectArgs): Promise<Project>;
  delete(id: Project['id']): Promise<void>;

  enqueue(projectId: Project['id']): Promise<void>;
  dequeue(projectId: Project['id']): Promise<void>;
}
