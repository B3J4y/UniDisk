import { Project, Topic, TopicDetails } from 'data/entity';

export type CreateTopicArgs = {
  projectId: Project['id'];
  name: string;
};

export type UpdateTopicArgs = {
  id: Topic['id'];
  name: string;
};

export interface TopicRepository {
  create(args: CreateTopicArgs): Promise<Topic>;
  update(args: UpdateTopicArgs): Promise<Topic>;
  delete(id: Topic['id']): Promise<void>;

  getProjectTopics(id: Project['id']): Promise<TopicDetails[]>;
}
