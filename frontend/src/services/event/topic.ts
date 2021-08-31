import { Project, Topic } from 'data/entity';
import { RateResultArgs } from 'data/repositories';
import { DeleteEvent, EntityEvent } from './base';
import { Event } from './bus';

export class TopicDeletedEvent extends DeleteEvent {
  public static Name = 'TopicDeletedEvent';
  eventName = TopicDeletedEvent.Name;
  public constructor(topicId: Topic['id']) {
    super(topicId);
  }
}

export class TopicCreatedEvent extends EntityEvent<Topic> {
  public static Name = 'TopicCreatedEvent';
  eventName = TopicCreatedEvent.Name;
  public constructor(topic: Topic, public projectId: Project['id']) {
    super(topic);
  }
}

export class TopicUpdatedEvent extends EntityEvent<Topic> {
  public static Name = 'TopicUpdatedEvent';
  eventName = TopicUpdatedEvent.Name;
  public constructor(topic: Topic) {
    super(topic);
  }
}

export class TopicRelevanceChangeEvent extends Event {
  public static Name = 'TopicRelevanceChangeEvent';
  eventName = TopicRelevanceChangeEvent.Name;
  public constructor(public args: RateResultArgs) {
    super();
  }
}
