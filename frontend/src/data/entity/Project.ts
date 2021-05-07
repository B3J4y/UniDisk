import { Topic, TopicDetails } from './Topic';

export enum ProjectState {
  idle,
  ready,
  processing,
  error,
  completed,
}

export const ProjectStates: ProjectState[] = Object.values(ProjectState).filter(
  (v) => typeof v !== 'string',
) as ProjectState[];

export type Project = {
  id: string;
  name: string;
  topics?: Topic[];
  state: ProjectState;
};

export type ProjectDetails = Omit<Project, 'topics'> & { topics: TopicDetails[] };
