import { Event } from './bus';

export abstract class DeleteEvent extends Event {
  public constructor(public id: string) {
    super();
  }
}

export abstract class EntityEvent<T = { id: string }> extends Event {
  public constructor(public entity: T) {
    super();
  }
}
