import { Project } from 'data/entity';

export type CreateProjectArgs = {
  name: string;
};

export type UpdateProjectArgs = {
  name: string;
  projectId: Project['id'];
};

export type DeleteProjectArgs = {
  projectId: Project['id'];
};

export interface ProjectRepository {
  findAll(): Promise<Project[]>;

  create(args: CreateProjectArgs): Promise<Project>;
  update(args: UpdateProjectArgs): Promise<Project>;
  delete(args: DeleteProjectArgs): Promise<void>;
}
