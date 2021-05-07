import { Keyword, Topic } from 'data/entity';
import { DeleteEvent, EntityEvent } from './base';

export class KeywordDeletedEvent extends DeleteEvent {
  eventName = 'KeywordDeletedEvent';
  public constructor(keywordId: Keyword['id']) {
    super(keywordId);
  }
}

export class KeywordCreatedEvent extends EntityEvent<Keyword> {
  eventName = 'KeywordCreatedEvent';
  public constructor(keyword: Keyword, public topicId: Topic['id']) {
    super(keyword);
  }
}

export class KeywordUpdatedEvent extends EntityEvent<Keyword> {
  eventName = 'KeywordUpdatedEvent';
  public constructor(keyword: Keyword) {
    super(keyword);
  }
}
