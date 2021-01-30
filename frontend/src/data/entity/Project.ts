import { Topic } from './Topic';

export type Project = {
  id: string;
  name: string;
  topics?: Topic[];
  createdAt: Date;
};
