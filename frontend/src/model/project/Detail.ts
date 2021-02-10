import { Project, ProjectDetails, ProjectState } from 'data/entity';
import { CreateProjectArgs, ProjectRepository, UpdateProjectArgs } from 'data/repositories';
import { Operation, Resource } from 'data/Resource';
import { EntityDetailState, EntityDetailStateContainer } from 'model/base';
import {
  KeywordCreatedEvent,
  KeywordDeletedEvent,
  ProjectCreatedEvent,
  ProjectDeletedEvent,
  ProjectUpdatedEvent,
  TopicCreatedEvent,
  TopicDeletedEvent,
  TopicUpdatedEvent,
} from 'services/event';
import { EventBus } from 'services/event/bus';

export type ProjectDetailState = EntityDetailState<ProjectDetails> & {
  enqueue: Operation;
  dequeue: Operation;
};

export class ProjectDetailContainer extends EntityDetailStateContainer<
  ProjectDetails,
  CreateProjectArgs,
  UpdateProjectArgs,
  ProjectDetailState
> {
  protected onCreate(entity: ProjectDetails): void {
    this.eventBus.publish(new ProjectCreatedEvent(entity));
  }
  protected onUpdate(entity: ProjectDetails): void {
    this.eventBus.publish(new ProjectUpdatedEvent(entity));
  }
  protected onDelete(entity: ProjectDetails): void {
    this.eventBus.publish(new ProjectDeletedEvent(entity.id));
  }

  protected getEntityId(): string {
    return this.state.entity.data!.id;
  }
  public constructor(private repository: ProjectRepository, private eventBus: EventBus) {
    super();

    eventBus.subscribe(TopicCreatedEvent, (event: TopicCreatedEvent) => {
      const project = this.state.entity.data;
      if (!project || project.id !== event.projectId) return;

      const newProject = {
        ...project,
        topics: [...project.topics, { ...event.entity, keywords: [] }],
      };

      this.setState({
        entity: Resource.success(newProject),
      });
    });

    eventBus.subscribe(TopicUpdatedEvent, (event: TopicUpdatedEvent) => {
      const project = this.state.entity.data;
      if (!project) return;

      const newProject = {
        ...project,
        topics: project.topics.map((topic) =>
          topic.id !== event.entity.id ? topic : { ...topic, ...event.entity },
        ),
      };

      this.setState({
        entity: Resource.success(newProject),
      });
    });

    eventBus.subscribe(TopicDeletedEvent, (event: TopicDeletedEvent) => {
      const project = this.state.entity.data;
      if (!project) return;

      const topics = project.topics.filter((topic) => topic.id !== event.id);
      if (topics.length === project.topics.length) {
        return;
      }

      const newProject = {
        ...project,
        topics,
      };

      this.setState({
        entity: Resource.success(newProject),
      });
    });

    eventBus.subscribe(KeywordCreatedEvent, (event: KeywordCreatedEvent) => {
      const project = this.state.entity.data;
      if (!project) return;

      const { topics } = project;

      const topic = topics.find((topic) => topic.id === event.topicId);
      if (!topic) return;

      const newTopic = {
        ...topic,
        keywords: [...topic.keywords, event.entity],
      };

      const updatedProject = {
        ...project,
        topics: topics.map((t) => (t.id === newTopic.id ? newTopic : t)),
      };
      console.log({ newTopic, updatedProject });

      this.setState({ ...this.state, entity: Resource.success(updatedProject) });
    });

    eventBus.subscribe(KeywordDeletedEvent, (event: KeywordDeletedEvent) => {
      const project = this.state.entity.data;
      if (!project) return;

      const { topics } = project;

      const topic = topics.find((topic) =>
        topic.keywords.some((keyword) => keyword.id === event.id),
      );
      if (!topic) return;

      const newTopic = {
        ...topic,
        keywords: topic.keywords.filter((keyword) => keyword.id !== event.id),
      };

      const updatedProject = {
        ...project,
        topics: topics.map((t) => (t.id === newTopic.id ? newTopic : t)),
      };

      this.setState({ ...this.state, entity: Resource.success(updatedProject) });
    });
  }

  public async enqueue(): Promise<void> {
    const projectId = this.state.entity.data!.id;
    const task = () => this.repository.enqueue(projectId);

    for await (const event of Operation.execute(task)) {
      this.setState({
        ...this.state,
        enqueue: event,
      });
    }

    if (this.state.enqueue.isFinished) {
      const newProject: ProjectDetails = {
        ...this.getProject()!,
        state: ProjectState.ready,
      };
      this.setState({
        ...this.state,
        entity: Resource.success(newProject),
      });
      this.eventBus.publish(new ProjectUpdatedEvent(newProject));
    }
  }

  public async dequeue(): Promise<void> {
    const projectId = this.state.entity.data!.id;
    const task = () => this.repository.dequeue(projectId);

    for await (const event of Operation.execute(task)) {
      this.setState({
        ...this.state,
        dequeue: event,
      });
    }

    if (this.state.dequeue.isFinished) {
      const newProject: ProjectDetails = {
        ...this.getProject()!,
        state: ProjectState.idle,
      };
      this.setState({
        ...this.state,
        entity: Resource.success(newProject),
      });
      this.eventBus.publish(new ProjectUpdatedEvent(newProject));
    }
  }

  protected executeDelete(id: string): Promise<void> {
    return this.repository.delete(id);
  }

  protected find(id: string): Promise<ProjectDetails | null> {
    return this.repository.get(id).then((val) => val ?? null);
  }

  protected initialState(): ProjectDetailState {
    return {
      entity: Resource.idle(),
      save: Operation.idle(),
      delete: Operation.idle(),
      enqueue: Operation.idle(),
      dequeue: Operation.idle(),
    };
  }

  protected executeCreate(vars: CreateProjectArgs): Promise<ProjectDetails> {
    return this.repository.create(vars).then((project) => ({ ...project, topics: [] }));
  }
  protected executeUpdate(vars: UpdateProjectArgs): Promise<ProjectDetails> {
    return this.repository.update(vars).then((project) => ({ ...project, topics: [] }));
  }

  private getProject = () => this.state.entity.data;
}