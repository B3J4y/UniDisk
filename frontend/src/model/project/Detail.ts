import { Project } from 'data/entity';
import { CreateProjectArgs, ProjectRepository, UpdateProjectArgs } from 'data/repositories';
import { Operation, Resource } from 'data/Resource';
import { EntityDetailState, EntityDetailStateContainer } from 'model/base';
import { ProjectCreatedEvent, ProjectDeletedEvent, ProjectUpdatedEvent } from 'services/event';
import { EventBus } from 'services/event/bus';

export type ProjectDetailState = EntityDetailState<Project>;

export class ProjectDetailContainer extends EntityDetailStateContainer<
  Project,
  CreateProjectArgs,
  UpdateProjectArgs,
  ProjectDetailState
> {
  protected onCreate(entity: Project): void {
    this.eventBus.publish(new ProjectCreatedEvent(entity));
  }
  protected onUpdate(entity: Project): void {
    this.eventBus.publish(new ProjectUpdatedEvent(entity));
  }
  protected onDelete(entity: Project): void {
    this.eventBus.publish(new ProjectDeletedEvent(entity.id));
  }

  protected getEntityId(): string {
    return this.state.entity.data!.id;
  }
  public constructor(private repository: ProjectRepository, private eventBus: EventBus) {
    super();
  }

  protected executeDelete(id: string): Promise<void> {
    return this.repository.delete(id);
  }

  protected find(id: string): Promise<Project | null> {
    return this.repository.get(id).then((val) => val ?? null);
  }

  protected initialState(): ProjectDetailState {
    return { entity: Resource.idle(), save: Operation.idle(), delete: Operation.idle() };
  }

  protected executeCreate(vars: CreateProjectArgs): Promise<Project> {
    return this.repository.create(vars);
  }
  protected executeUpdate(vars: UpdateProjectArgs): Promise<Project> {
    return this.repository.update(vars);
  }
}
