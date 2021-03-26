import { Project, ProjectDetails, TopicResult } from 'data/entity';

export type CreateProjectArgs = {
  name: string;
};

export type UpdateProjectArgs = {
  name: string;
  projectId: Project['id'];
};

export type ProjectEvaluationResult = {
  results: Record<ProjectType, TopicResult[]>;
};

export enum FeedbackStatus {
  Relevant,
  NotRelevant,
  None,
}

export enum ProjectType {
  Default,
  ByTopic,
  Enhanced,
}

export interface ProjectRepository {
  findAll(): Promise<Project[]>;

  get(id: Project['id']): Promise<ProjectDetails | undefined>;

  /**
   * Returns evalutation result of project.
   * Returns undefined if project didn't finish processing.
   * @param id project id
   */
  getResult(id: Project['id']): Promise<ProjectEvaluationResult | undefined>;

  create(args: CreateProjectArgs): Promise<Project>;
  update(args: UpdateProjectArgs): Promise<Project>;
  delete(id: Project['id']): Promise<void>;

  enqueue(projectId: Project['id']): Promise<void>;
  dequeue(projectId: Project['id']): Promise<void>;

  rateResult(id: string, status: FeedbackStatus): Promise<void>;
}
