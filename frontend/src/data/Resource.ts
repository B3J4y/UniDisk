export enum ResourceState {
  Idle,
  Loading,
  Error,
  Success,
}

export class Resource<T> {
  public data: T | null;
  public state: ResourceState;
  public error?: unknown;

  constructor(data: T | null, state: ResourceState, error?: unknown) {
    this.data = data;
    this.state = state;
    this.error = error;
  }

  static success<T>(data: T): Resource<T> {
    return new Resource(data, ResourceState.Success);
  }
  static idle<T>(): Resource<T> {
    return new Resource<T>(null, ResourceState.Idle);
  }

  static loading<T>(): Resource<T> {
    return new Resource<T>(null, ResourceState.Loading);
  }

  static failure<T>(error: unknown): Resource<T> {
    return new Resource<T>(null, ResourceState.Error, error);
  }

  get isLoading(): boolean {
    return this.state === ResourceState.Loading;
  }

  get hasError(): boolean {
    return this.state === ResourceState.Error;
  }

  get hasData(): boolean {
    return this.state === ResourceState.Success;
  }
  get isIdle(): boolean {
    return this.state === ResourceState.Idle;
  }

  public toOperation(): Operation {
    return new Operation(this.state, this.error);
  }

  public static async *execute<T>(
    task: () => Promise<T> | T,
    errorHandler?: (e: unknown) => unknown,
  ) {
    yield Resource.loading<T>();
    try {
      const result = await task();
      yield Resource.success<T>(result);
    } catch (e) {
      const error = errorHandler ? errorHandler(e) : e;
      yield Resource.failure<T>(error);
    }
  }
}

export class Operation {
  public state: ResourceState;
  public error?: unknown;

  constructor(state: ResourceState, error?: unknown) {
    this.state = state;
    this.error = error;
  }

  static success(): Operation {
    return new Operation(ResourceState.Success);
  }
  static idle(): Operation {
    return new Operation(ResourceState.Idle);
  }

  static loading(): Operation {
    return new Operation(ResourceState.Loading);
  }

  static failure(error: unknown): Operation {
    return new Operation(ResourceState.Error, error);
  }

  get isIdle(): boolean {
    return this.state === ResourceState.Idle;
  }

  get isLoading(): boolean {
    return this.state === ResourceState.Loading;
  }

  get hasError(): boolean {
    return this.state === ResourceState.Error;
  }

  get isFinished(): boolean {
    return this.state === ResourceState.Success;
  }

  public static async *execute(
    task: () => Promise<void> | void,
    errorHandler?: (e: unknown) => unknown,
  ) {
    yield Operation.loading();
    try {
      await task();
      yield Operation.success();
    } catch (e) {
      const error = errorHandler ? errorHandler(e) : e;
      yield Operation.failure(error);
    }
  }
}
