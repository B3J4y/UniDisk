import { Keyword } from 'data/entity';

export type KeywordDto = {
  id: number;
  name: string;
};

export type CreateKeywordDto = {
  name: string;
  topicId: string;
  suggestion: boolean;
};

export type UpdateKeywordDto = {
  name: string;
};

export function mapKeywordModelDtoToEntity(dto: KeywordDto): Keyword {
  return {
    id: dto.id.toString(),
    name: dto.name,
  };
}
