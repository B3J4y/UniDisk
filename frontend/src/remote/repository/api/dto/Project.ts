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
