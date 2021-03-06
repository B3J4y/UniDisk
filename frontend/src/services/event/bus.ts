export abstract class Event {
  public abstract get eventName(): string;
}

export class EventSubscription {
  public constructor(private cancelAction: () => void) {}

  public cancel() {
    this.cancelAction();
  }
}

export class EventBus {
  private subscriptions: Record<string, Record<string, (event: Event) => void>> = {};

  public subscribe<T extends Event>(
    event: string,
    callback: (event: T) => void,
  ): EventSubscription {
    const id = (new Date().getMilliseconds() + Math.random() * 1000).toString();
    if (!this.subscriptions[event]) {
      this.subscriptions[event] = {};
    }
    // @ts-ignore
    this.subscriptions[event][id] = callback;

    return new EventSubscription(() => {
      const eventSubscriptions = this.subscriptions[event];
      delete eventSubscriptions.id;
    });
  }

  public publish(event: Event): void {
    const { eventName } = event;

    const eventSubscriber = this.subscriptions[eventName];
    if (!eventSubscriber) return;

    Object.keys(eventSubscriber).forEach((subscriberId) => {
      const subscriberCallback = eventSubscriber[subscriberId];
      subscriberCallback(event);
    });
  }
}
