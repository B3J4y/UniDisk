import { Keyword } from 'data/entity';
import { CreateKeywordArgs, KeywordRepository, UpdateKeywordArgs } from 'data/repositories';
import { BaseApiRepository, RepositoryArgs } from './Base';
import { CreateKeywordDto, mapKeywordModelDtoToEntity, UpdateKeywordDto } from './dto/Keyword';

export class KeywordApiRepository extends BaseApiRepository implements KeywordRepository {
  public constructor(args: RepositoryArgs) {
    super({ ...args, defaultPath: 'keyword' });
  }
  create(args: CreateKeywordArgs): Promise<Keyword> {
    const dto: CreateKeywordDto = {
      name: args.name,
      topicId: args.topicId,
      suggestion: args.isSuggestion ?? false,
    };

    return this.withClient<Keyword>(async (client) => {
      const response = await client.post('', dto);
      return mapKeywordModelDtoToEntity(response.data);
    });
  }

  update(args: UpdateKeywordArgs): Promise<Keyword> {
    const dto: UpdateKeywordDto = {
      name: args.name,
    };
    return this.withClient<Keyword>(async (client) => {
      const response = await client.put('/' + args.id, dto);
      return mapKeywordModelDtoToEntity(response.data);
    });
  }

  delete(id: string): Promise<void> {
    return this.withClient<void>(async (client) => {
      await client.delete('/' + id);
    });
  }

  getProjectKeywords(id: string): Promise<Required<Keyword>[]> {
    throw new Error('Method not implemented.');
  }
}
