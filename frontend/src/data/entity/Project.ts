import { Topic, TopicDetails } from './Topic';

export enum ProjectState {
  idle,
  ready,
  processing,
  error,
  completed,
}

export type Project = {
  id: string;
  name: string;
  topics?: Topic[];
  createdAt: Date;
  state: ProjectState;
};

export type ProjectDetails = Omit<Project, 'topics'> & { topics: TopicDetails[] };
