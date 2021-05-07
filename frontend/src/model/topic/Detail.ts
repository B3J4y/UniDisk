import { Topic } from 'data/entity';
import { CreateTopicArgs, TopicRepository, UpdateTopicArgs } from 'data/repositories';
import { Operation, Resource } from 'data/Resource';
import { EntityDetailState, EntityDetailStateContainer } from 'model/base';
import { EventBus } from 'services/event/bus';
import { TopicCreatedEvent, TopicDeletedEvent, TopicUpdatedEvent } from 'services/event/topic';

export type TopicDetailState = EntityDetailState<Topic>;

export class TopicDetailContainer extends EntityDetailStateContainer<
  Topic,
  CreateTopicArgs,
  UpdateTopicArgs,
  TopicDetailState
> {
  protected onCreate(entity: Topic, args: CreateTopicArgs): void {
    this.eventBus.publish(new TopicCreatedEvent(entity, args.projectId));
  }
  protected onUpdate(entity: Topic): void {
    this.eventBus.publish(new TopicUpdatedEvent(entity));
  }
  protected onDelete(entity: Topic): void {
    this.eventBus.publish(new TopicDeletedEvent(entity.id));
  }

  protected getEntityId(): string {
    return this.state.entity.data!.id;
  }
  public constructor(private repository: TopicRepository, private eventBus: EventBus) {
    super();
  }

  protected executeDelete(id: string): Promise<void> {
    return this.repository.delete(id);
  }

  protected async find(id: string): Promise<Topic | null> {
    return null;
  }

  protected initialState(): TopicDetailState {
    return { entity: Resource.idle(), save: Operation.idle(), delete: Operation.idle() };
  }

  protected executeCreate(vars: CreateTopicArgs): Promise<Topic> {
    return this.repository.create(vars);
  }
  protected executeUpdate(vars: UpdateTopicArgs): Promise<Topic> {
    return this.repository.update(vars);
  }
}
