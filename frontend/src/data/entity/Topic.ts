import { FeedbackStatus } from 'data/repositories';
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

export type KeywordResult = {
  id: string;
  keywordId: number;
  keyword: string;
  searchMetaData: {
    url: string;
  };
  score: number;
  relevance: FeedbackStatus;
};
export type TopicResult = {
  id: string;
  topic: BaseTopic;
  score: number;
  entryCount: number;
  university: University;
  keywords: KeywordResult[];
};
