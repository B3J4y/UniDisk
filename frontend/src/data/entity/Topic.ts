import { Keyword } from './Keyword';

export type Topic = {
  id: string;
  name: string;
  keywords?: Keyword[];
};

export type TopicDetails = Required<Topic>;
