import { Keyword } from './Keyword';
import { University } from './University';

type BaseTopic = {
  id: string;
  name: string;
};

export type Topic = {
  keywords?: Keyword[];
} & BaseTopic;

export type TopicDetails = Required<Topic>;

export type TopicResult = {
  topic: BaseTopic;
  score: number;
  entryCount: number;
  university: University;
};
