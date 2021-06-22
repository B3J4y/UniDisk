import { Keyword, Topic } from 'data/entity';
import { DeleteEvent, EntityEvent } from './base';

export class KeywordDeletedEvent extends DeleteEvent {
  public static Name = 'KeywordDeletedEvent';
  eventName = KeywordDeletedEvent.Name;

  public constructor(keywordId: Keyword['id']) {
    super(keywordId);
  }
}

export class KeywordCreatedEvent extends EntityEvent<Keyword> {
  public static Name = 'KeywordCreatedEvent';
  eventName = KeywordCreatedEvent.Name;
  public constructor(keyword: Keyword, public topicId: Topic['id']) {
    super(keyword);
  }
}

export class KeywordUpdatedEvent extends EntityEvent<Keyword> {
  public static Name = 'KeywordUpdatedEvent';
  eventName = KeywordUpdatedEvent.Name;
  public constructor(keyword: Keyword) {
    super(keyword);
  }
}
