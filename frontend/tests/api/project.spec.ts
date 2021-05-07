import { ProjectApiRepository } from 'remote/repository';
import { ProjectRepository } from 'data/repositories';
import { getTestAPIToken, TEST_API_ENDPOINT } from './util';

describe('Project API', () => {
  let api: ProjectRepository;

  beforeAll(async () => {
    const token = await getTestAPIToken();
    api = new ProjectApiRepository({
      endpoint: TEST_API_ENDPOINT,
      tokenProvider: {
        getToken: async () => token,
        onTokenChange: () => {},
      },
    });
  });

  it('findAll', async () => {
    await api.findAll();
  });

  it('create', async () => {
    await api.create({ name: 'test' });
  });

  it('update', async () => {
    const project = await api.create({ name: 'test' });
    await api.update({ name: 'test3', projectId: project.id });
  });

  it('delete', async () => {
    const project = await api.create({ name: 'test3' });
    await api.delete(project.id);
  });

  it('get', async () => {
    const project = await api.create({ name: 'test5' });
    const result = await api.get(project.id);
    expect(result).toBeDefined();
    expect(result!.id).toBe(project.id);
  });
});
