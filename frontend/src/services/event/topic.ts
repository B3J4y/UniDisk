import { Project, Topic } from 'data/entity';
import { DeleteEvent, EntityEvent } from './base';

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
