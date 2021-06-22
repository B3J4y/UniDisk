import { Project } from 'data/entity';
import { DeleteEvent, EntityEvent } from './base';

export class ProjectDeletedEvent extends DeleteEvent {
  public static Name = 'ProjectDeletedEvent';
  eventName = ProjectDeletedEvent.Name;
  public constructor(projectId: Project['id']) {
    super(projectId);
  }
}

export class ProjectCreatedEvent extends EntityEvent<Project> {
  public static Name = 'ProjectCreatedEvent';
  eventName = ProjectCreatedEvent.Name;
  public constructor(project: Project) {
    super(project);
  }
}

export class ProjectUpdatedEvent extends EntityEvent<Project> {
  public static Name = 'ProjectUpdatedEvent';
  eventName = ProjectUpdatedEvent.Name;
  public constructor(project: Project) {
    super(project);
  }
}
