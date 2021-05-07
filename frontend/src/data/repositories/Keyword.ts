import { Keyword, Topic } from 'data/entity';

export type CreateKeywordArgs = {
  topicId: Topic['id'];
  name: string;
  isSuggestion?: boolean;
};

export type UpdateKeywordArgs = {
  id: Keyword['id'];
  name: string;
};

export interface KeywordRepository {
  create(args: CreateKeywordArgs): Promise<Keyword>;
  update(args: UpdateKeywordArgs): Promise<Keyword>;
  delete(id: Keyword['id']): Promise<void>;
}
