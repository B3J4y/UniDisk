import { ProjectRepository } from 'data/repositories';

import { Resource } from 'data/Resource';
import { EntityAllState, EntityAllStateContainer } from 'model/base';
import { EventBus } from 'services/event/bus';
import { ProjectCreatedEvent, ProjectDeletedEvent, ProjectUpdatedEvent } from 'services/event';
import { Project } from 'data/entity';

export type ProjectAllState = EntityAllState<Project>;

export class ProjectAllContainer extends EntityAllStateContainer<Project, ProjectAllState> {
  protected initialState(): EntityAllState<Project> {
    return { entities: Resource.idle() };
  }

  public constructor(private repository: ProjectRepository, eventBus: EventBus) {
    super(eventBus);

    this.subscribeToEvents({
      create: ProjectCreatedEvent,
      update: ProjectUpdatedEvent,
      delete: ProjectDeletedEvent,
    });
  }

  protected executeFind(): Promise<Project[]> {
    return this.repository.findAll();
  }
}
