export interface BaseRepository<T> {
  create(entity: T): Promise<T>;
  update(entity: T): Promise<T>;
  delete(id: string): Promise<void>;

  get(id: string): Promise<T | null>;
}
