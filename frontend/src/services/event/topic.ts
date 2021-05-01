import { Project, Topic } from 'data/entity';
import { RateResultArgs } from 'data/repositories';
import { DeleteEvent, EntityEvent } from './base';
import { Event } from './bus';

export class TopicDeletedEvent extends DeleteEvent {
  eventName = 'TopicDeletedEvent';
  public constructor(topicId: Topic['id']) {
    super(topicId);
  }
}

export class TopicCreatedEvent extends EntityEvent<Topic> {
  eventName = 'TopicCreatedEvent';
  public constructor(topic: Topic, public projectId: Project['id']) {
    super(topic);
  }
}

export class TopicUpdatedEvent extends EntityEvent<Topic> {
  eventName = 'TopicUpdatedEvent';
  public constructor(topic: Topic) {
    super(topic);
  }
}

export class TopicRelevanceChangeEvent extends Event {
  eventName = 'TopicRelevanceChangeEvent';
  public constructor(public args: RateResultArgs) {
    super();
  }
}
