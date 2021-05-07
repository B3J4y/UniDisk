import { Keyword } from 'data/entity';
import { CreateKeywordArgs, KeywordRepository, UpdateKeywordArgs } from 'data/repositories';
import { Operation, Resource } from 'data/Resource';
import { EntityDetailState, EntityDetailStateContainer } from 'model/base';
import { EventBus } from 'services/event/bus';
import { KeywordCreatedEvent, KeywordDeletedEvent, KeywordUpdatedEvent } from 'services/event';

export type KeywordDetailState = EntityDetailState<Keyword>;

export class KeywordDetailContainer extends EntityDetailStateContainer<
  Keyword,
  CreateKeywordArgs,
  UpdateKeywordArgs,
  KeywordDetailState
> {
  protected onCreate(entity: Keyword, args: CreateKeywordArgs): void {
    this.eventBus.publish(new KeywordCreatedEvent(entity, args.topicId));
  }
  protected onUpdate(entity: Keyword): void {
    this.eventBus.publish(new KeywordUpdatedEvent(entity));
  }
  protected onDelete(entity: Keyword): void {
    this.eventBus.publish(new KeywordDeletedEvent(entity.id));
  }

  protected getEntityId(): string {
    return this.state.entity.data!.id;
  }
  public constructor(private repository: KeywordRepository, private eventBus: EventBus) {
    super();
  }

  protected executeDelete(id: string): Promise<void> {
    return this.repository.delete(id);
  }

  protected async find(id: string): Promise<Keyword | null> {
    return null;
  }

  protected initialState(): KeywordDetailState {
    return { entity: Resource.idle(), save: Operation.idle(), delete: Operation.idle() };
  }

  protected executeCreate(vars: CreateKeywordArgs): Promise<Keyword> {
    return this.repository.create(vars);
  }
  protected executeUpdate(vars: UpdateKeywordArgs): Promise<Keyword> {
    return this.repository.update(vars);
  }
}
