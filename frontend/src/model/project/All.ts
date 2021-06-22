import { Project, ProjectState } from 'data/entity';
import { ProjectRepository } from 'data/repositories';
import { Resource } from 'data/Resource';
import { EntityAllState, EntityAllStateContainer } from 'model/base';
import { ProjectCreatedEvent, ProjectDeletedEvent, ProjectUpdatedEvent } from 'services/event';
import { EventBus } from 'services/event/bus';

export type ProjectAllState = EntityAllState<Project>;

export class ProjectAllContainer extends EntityAllStateContainer<Project, ProjectAllState> {
  protected initialState(): EntityAllState<Project> {
    return { entities: Resource.idle() };
  }

  public constructor(private repository: ProjectRepository, eventBus: EventBus) {
    super(eventBus);

    this.subscribeToEvents({
      create: ProjectCreatedEvent.Name,
      update: ProjectUpdatedEvent.Name,
      delete: ProjectDeletedEvent.Name,
    });

    setInterval(() => this.refresh(), 1000 * 30);
  }

  protected executeFind(): Promise<Project[]> {
    return this.repository.findAll();
  }

  private async refresh(): Promise<void> {
    const projects = this.state.entities.data ?? [];
    // Check if any projects could be modified by backend
    if (
      !projects.some(
        (project) =>
          project.state === ProjectState.ready || project.state === ProjectState.processing,
      )
    ) {
      return;
    }

    try {
      const results = await this.repository.findAll();
      this.setState({
        ...this.state,
        entities: Resource.success(results),
      });
    } catch (e) {}
  }
}
