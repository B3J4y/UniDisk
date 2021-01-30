import { Operation, Resource } from 'data/Resource';
import { EventBus } from 'services/event/bus';
import { DeleteEvent, EntityEvent } from 'services/event/events';
import { Container } from 'unstated-typescript';

export type EntityDetailState<T> = {
  entity: Resource<T | null>;
  save: Operation;
  delete: Operation;
};

abstract class BaseContainer<TState extends object> extends Container<TState> {
  public constructor() {
    super();
    this.state = this.initialState();
  }

  protected abstract initialState(): TState;
}

export abstract class EntityDetailStateContainer<
  TEntity,
  TCreateVars,
  TUpdateVars,
  TState extends EntityDetailState<TEntity>
> extends BaseContainer<TState> {
  protected abstract executeCreate(vars: TCreateVars): Promise<TEntity>;

  protected abstract executeUpdate(vars: TUpdateVars): Promise<TEntity>;

  protected abstract executeDelete(id: string): Promise<void>;

  protected onCreate(entity: TEntity): void {}
  protected onUpdate(entity: TEntity): void {}
  protected onDelete(entity: TEntity): void {}

  protected abstract getEntityId(): string;

  protected abstract find(id: string): Promise<TEntity | null>;

  public async load(id: string): Promise<void> {
    this.setState({
      ...this.state,
      entity: Resource.loading(),
    });

    try {
      const entity = await this.find(id);

      this.setState({
        ...this.state,
        entity: Resource.success(entity),
      });
    } catch (e) {
      console.error(e);
      this.setState({
        ...this.state,
        entity: Resource.success('Daten konnten nicht geladen werden.'),
      });
    }
  }

  public async create(vars: TCreateVars): Promise<void> {
    this.setState({
      ...this.state,
      save: Operation.loading(),
    });

    try {
      const entity = await this.executeCreate(vars);
      this.setState({
        ...this.state,
        save: Operation.success(),
        entity: Resource.success(entity),
      });
      this.onCreate(entity);
    } catch (e) {
      console.error(e);
      this.setState({
        ...this.state,
        save: Operation.failure('Daten konnten nicht gespeichert werden.'),
      });
    }
  }

  public async update(vars: TUpdateVars): Promise<void> {
    this.setState({
      ...this.state,
      save: Operation.loading(),
    });

    try {
      const entity = await this.executeUpdate(vars);
      this.setState({
        ...this.state,
        save: Operation.success(),
        entity: Resource.success(entity),
      });
      this.onUpdate(entity);
    } catch (e) {
      console.error(e);
      this.setState({
        ...this.state,
        save: Operation.failure('Daten konnten nicht aktualisiert werden.'),
      });
    }
  }

  public async delete(): Promise<void> {
    this.setState({
      ...this.state,
      delete: Operation.loading(),
    });

    try {
      const id = this.getEntityId();
      await this.executeDelete(id);
      this.setState({
        ...this.state,
        delete: Operation.success(),
      });
      this.onDelete(this.state.entity.data!);
    } catch (e) {
      console.error(e);
      this.setState({
        ...this.state,
        delete: Operation.failure('Daten konnten nicht aktualisiert werden.'),
      });
    }
  }
}

export type EntityAllState<T> = {
  entities: Resource<T[]>;
};

export abstract class EntityAllStateContainer<
  TEntity extends { id: string },
  TState extends EntityAllState<TEntity>
> extends BaseContainer<TState> {
  protected constructor(protected eventBus: EventBus) {
    super();
  }

  protected abstract executeFind(): Promise<TEntity[]>;

  protected subscribeToEvents<
    TCreate extends EntityEvent<TEntity>,
    TUpdate extends EntityEvent<TEntity>,
    TDelete extends DeleteEvent
  >(events: { create: any; update: any; delete: any }): void {
    const { create, update, delete: deleteName } = events;

    this.eventBus.subscribe<TCreate>(create, (event) => {
      if (!this.state.entities.data) return;

      const newItems = [...this.state.entities.data, event.entity];
      this.setState({
        ...this.state,
        entities: Resource.success(newItems),
      });
    });

    this.eventBus.subscribe<TUpdate>(update, (event) => {
      if (!this.state.entities.data) return;
      const newItems = this.state.entities.data.map((article) =>
        article.id === event.entity.id ? event.entity : article,
      );
      this.setState({
        ...this.state,
        entities: Resource.success(newItems),
      });
    });

    this.eventBus.subscribe<TDelete>(deleteName, (event) => {
      if (!this.state.entities.data) return;
      const newItems = this.state.entities.data.filter((article) => article.id !== event.id);
      this.setState({
        ...this.state,
        entities: Resource.success(newItems),
      });
    });
  }

  public async load(): Promise<void> {
    this.setState({
      ...this.state,
      entities: Resource.loading(),
    });

    try {
      const entities = await this.executeFind();
      this.setState({
        ...this.state,

        entities: Resource.success(entities),
      });
    } catch (e) {
      console.error(e);
      this.setState({
        ...this.state,
        entities: Resource.failure('Daten konnten nicht geladen werden.'),
      });
    }
  }
}
