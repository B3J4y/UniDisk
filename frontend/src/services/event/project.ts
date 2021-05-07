import { Project } from 'data/entity';
import { DeleteEvent, EntityEvent } from './base';

export class ProjectDeletedEvent extends DeleteEvent {
  eventName = 'ProjectDeletedEvent';
  public constructor(projectId: Project['id']) {
    super(projectId);
  }
}

export class ProjectCreatedEvent extends EntityEvent<Project> {
  eventName = 'ProjectCreatedEvent';
  public constructor(project: Project) {
    super(project);
  }
}

export class ProjectUpdatedEvent extends EntityEvent<Project> {
  eventName = 'ProjectUpdatedEvent';
  public constructor(project: Project) {
    super(project);
  }
}
