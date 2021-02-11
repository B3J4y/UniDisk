import { Topic } from 'data/entity';
import { KeywordDto, mapKeywordModelDtoToEntity } from './Keyword';

export type CreateTopicDto = {
  name: string;
  projectId: string;
};

export type UpdateTopicDto = {
  name: string;
};

export type TopicModelDto = {
  id: number;
  name: string;
  keywords: KeywordDto[];
};

export function mapModelDtoToEntity(dto: TopicModelDto): Topic {
  return {
    id: dto.id.toString(),
    name: dto.name,
    keywords: dto.keywords.map(mapKeywordModelDtoToEntity),
  };
}
