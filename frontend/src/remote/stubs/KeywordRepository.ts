import { Keyword } from 'data/entity';
import { CreateKeywordArgs, KeywordRepository, UpdateKeywordArgs } from 'data/repositories';

export class KeywordRepositoryStub implements KeywordRepository {
  keywords: Keyword[] = [];

  async create(args: CreateKeywordArgs): Promise<Keyword> {
    const topic = {
      name: args.name,
      id: new Date().getTime().toString(),
    };

    this.keywords.push(topic);
    return topic;
  }

  async update(args: UpdateKeywordArgs): Promise<Keyword> {
    const topic = this.keywords.find((topic) => topic.id === args.id);
    if (!topic) throw new Error('Keyword not found.');

    const newKeyword = { ...topic, name: args.name };
    this.keywords = this.keywords.map((t) => (t.id === newKeyword.id ? newKeyword : t));
    return newKeyword;
  }

  async delete(id: string): Promise<void> {
    this.keywords = this.keywords.filter((topic) => topic.id !== id);
  }
}
