import { Topic, TopicDetails } from 'data/entity';
import { CreateTopicArgs, TopicRepository, UpdateTopicArgs } from 'data/repositories';

export class TopicRepositoryStub implements TopicRepository {
  static topics: Topic[] = [];

  async create(args: CreateTopicArgs): Promise<Topic> {
    const topic = {
      name: args.name,
      id: new Date().getTime().toString(),
    };

    TopicRepositoryStub.topics.push(topic);
    return topic;
  }

  async update(args: UpdateTopicArgs): Promise<Topic> {
    const topic = TopicRepositoryStub.topics.find((topic) => topic.id === args.id);
    if (!topic) throw new Error('Topic not found.');

    const newTopic = { ...topic, name: args.name };
    TopicRepositoryStub.topics = TopicRepositoryStub.topics.map((t) =>
      t.id === newTopic.id ? newTopic : t,
    );
    return newTopic;
  }

  async delete(id: string): Promise<void> {
    TopicRepositoryStub.topics = TopicRepositoryStub.topics.filter((topic) => topic.id !== id);
  }

  getProjectTopics(id: string): Promise<TopicDetails[]> {
    return TopicRepositoryStub.topics[id] ?? [];
  }
}
