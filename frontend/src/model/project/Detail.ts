import { BaseTopic, ProjectDetails, ProjectState } from 'data/entity';
import {
  CreateProjectArgs,
  FeedbackStatus,
  ProjectEvaluationResult,
  ProjectRepository,
  UpdateProjectArgs,
} from 'data/repositories';
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
  TopicRelevanceChangeEvent,
  TopicUpdatedEvent,
} from 'services/event';
import { EventBus } from 'services/event/bus';

export type ProjectDetailState = EntityDetailState<ProjectDetails> & {
  enqueue: Operation;
  dequeue: Operation;
  result: Resource<ProjectEvaluationResult | undefined>;
  topicResults: Record<string, Record<string, FeedbackStatus>>;
};

export class ProjectDetailContainer extends EntityDetailStateContainer<
  ProjectDetails,
  CreateProjectArgs,
  UpdateProjectArgs,
  ProjectDetailState
> {
  private topicMap: Record<string, BaseTopic> = {};

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

    eventBus.subscribe(TopicCreatedEvent.Name, (event: TopicCreatedEvent) => {
      const project = this.state.entity.data;
      if (!project || project.id !== event.projectId) return;

      const newProject = {
        ...project,
        topics: [...project.topics, { ...event.entity, keywords: [] }],
      };

      this.updateProject(newProject);
    });

    eventBus.subscribe(TopicUpdatedEvent.Name, (event: TopicUpdatedEvent) => {
      const project = this.state.entity.data;
      if (!project) return;

      const newProject = {
        ...project,
        topics: project.topics.map((topic) =>
          topic.id !== event.entity.id ? topic : { ...topic, ...event.entity },
        ),
      };

      this.updateProject(newProject);
    });

    eventBus.subscribe(TopicDeletedEvent.Name, (event: TopicDeletedEvent) => {
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

      this.updateProject(newProject);
    });

    eventBus.subscribe(KeywordCreatedEvent.Name, (event: KeywordCreatedEvent) => {
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

      this.updateProject(updatedProject);
    });

    eventBus.subscribe(KeywordDeletedEvent.Name, (event: KeywordDeletedEvent) => {
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

      this.updateProject(updatedProject);
    });

    eventBus.subscribe(TopicRelevanceChangeEvent.Name, (event: TopicRelevanceChangeEvent) => {
      if (!this.getProject()) return;

      const { args } = event;

      const topic = this.topicMap[args.topicId];

      if (!topic) {
        return;
      }

      const resultData = this.state.result.data;
      if (!resultData) {
        return;
      }

      const otherTopics = Object.values(resultData.results).map((result) => {
        return result.topicResults.find((t) => t.topic.name === topic.name);
      });

      otherTopics.forEach((t) => {
        if (t) {
          const topicId = t.topic.id;

          const topicScores = this.state.topicResults[topicId];
          if (!topicScores) {
            this.state.topicResults[topicId] = {};
          }

          this.state.topicResults[topicId][event.args.url] = event.args.relevance;
        }
      });

      this.setState({ ...this.state });
    });
  }

  private updateProject(newProject: ProjectDetails, publish: boolean = true) {
    this.setState({
      ...this.state,
      entity: Resource.success(newProject),
    });
    if (publish) {
      this.eventBus.publish(new ProjectUpdatedEvent(newProject));
    }
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

  public async loadResults(): Promise<void> {
    const project = this.getProject();
    if (!project) throw new Error("Project hasn't been loaded yet.");

    if (project.state !== ProjectState.completed) {
      this.setState({
        ...this.state,
        result: Resource.success(undefined),
      });
      return;
    }

    for await (const event of Resource.execute(() => this.repository.getResult(project.id))) {
      if (event.hasData) {
        const topicResults: Record<string, Record<string, FeedbackStatus>> = {};

        Object.values(event.data!.results).forEach((result) => {
          result.topicResults.forEach(({ topic }) => {
            if (!this.topicMap[topic.id]) {
              this.topicMap[topic.id] = topic;
            }
          });

          result.relevanceScores.forEach((score) => {
            if (!topicResults[score.topicId]) {
              topicResults[score.topicId] = {};
            }

            topicResults[score.topicId][score.url] = score.relevance;
          });
        });
        this.setState({
          ...this.state,
          topicResults,
        });
      }
      this.setState({
        ...this.state,
        result: event,
      });
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
      result: Resource.idle(),
      topicResults: {},
    };
  }

  protected executeCreate(vars: CreateProjectArgs): Promise<ProjectDetails> {
    return this.repository.create(vars).then((project) => ({ ...project, topics: [] }));
  }
  protected executeUpdate(vars: UpdateProjectArgs): Promise<ProjectDetails> {
    return this.repository.update(vars).then((project) => ({ ...project, topics: [] }));
  }

  private getProject = () => this.state.entity.data;

  public getRelevance(args: { topicId: string; url: string }): FeedbackStatus {
    const topicRelevances = this.state.topicResults[args.topicId];
    if (!topicRelevances) return FeedbackStatus.None;

    return topicRelevances[args.url] ?? FeedbackStatus.None;
  }
}
