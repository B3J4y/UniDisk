import { Topic } from 'data/entity';
import { CreateTopicArgs, TopicRepository, UpdateTopicArgs } from 'data/repositories';
import { BaseApiRepository, RepositoryArgs } from './Base';
import { CreateTopicDto, mapModelDtoToEntity, UpdateTopicDto } from './dto/Topic';

export class TopicApiRepository extends BaseApiRepository implements TopicRepository {
  public constructor(args: RepositoryArgs) {
    super({ ...args, defaultPath: 'topic' });
  }
  create(args: CreateTopicArgs): Promise<Topic> {
    const dto: CreateTopicDto = {
      name: args.name,
      projectId: args.projectId,
    };

    return this.withClient<Topic>(async (client) => {
      const response = await client.post('', dto);
      return mapModelDtoToEntity(response.data);
    });
  }

  update(args: UpdateTopicArgs): Promise<Topic> {
    const dto: UpdateTopicDto = {
      name: args.name,
    };
    return this.withClient<Topic>(async (client) => {
      const response = await client.put('/' + args.id, dto);
      return mapModelDtoToEntity(response.data);
    });
  }

  delete(id: string): Promise<void> {
    return this.withClient<void>(async (client) => {
      await client.delete('/' + id);
    });
  }
}
