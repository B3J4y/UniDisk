import {
  Box,
  Button,
  CircularProgress,
  Grid,
  IconButton,
  List,
  ListItem,
  ListItemIcon,
  ListItemSecondaryAction,
  ListItemText,
  Paper,
  TextField,
  Toolbar,
} from '@material-ui/core';
import DeleteIcon from '@material-ui/icons/Delete';
import { Keyword, Project, ProjectState, Topic } from 'data/entity';
import { useProvider } from 'Provider';
import React, { useEffect } from 'react';
import AlertDialog from 'ui/components/form/AlertDialog';
import { Column } from 'ui/components/form/Column';
import { Center } from 'ui/components/util/Center';
import { Subscribe } from 'unstated-typescript';
import InfoIcon from '@material-ui/icons/Info';
import { useHistory } from 'react-router-dom';
import { KeywordRecommedation } from 'remote/services/KeywordRecommendation';
import { ProjectDetailContainer } from 'model';

export function ProjectOverviewPage() {
  const provider = useProvider();

  return (
    <Subscribe to={[provider.getAllProjectContainer()]}>
      {(container) => {
        const projectsResource = container.state.entities;

        const { isLoading, isIdle, data, hasError } = projectsResource;

        if (isIdle) {
          window.requestAnimationFrame(() => container.load());
        }

        if (isLoading || isIdle) {
          return <CircularProgress />;
        }

        if (hasError) {
          return <p>Etwas lief schief...</p>;
        }

        const projects = data ?? [];

        if (projects.length === 0) {
          return (
            <Grid container xs={12} style={{ height: '100%' }}>
              <Center child={<NoProjectsView />}></Center>
            </Grid>
          );
        }
        return <ProjectsSelection projects={projects} />;
      }}
    </Subscribe>
  );
}

type ProjectsSelectionProps = {
  projects: Project[];
};

const projectContainers: Record<string, ProjectDetailContainer> = {};

function ProjectsSelection(props: ProjectsSelectionProps) {
  const { projects } = props;

  const provider = useProvider();
  const [selected, setSelected] = React.useState(projects[0]);
  const [selectedTopic, setSelectedTopic] = React.useState<Topic | undefined>(undefined);

  const getProjectContainer = (projectId: string): ProjectDetailContainer => {
    const existingContainer = projectContainers[projectId];
    if (existingContainer) return existingContainer;

    const container = provider.getProjectDetailContainer();
    container.load(projectId);
    projectContainers[projectId] = container;

    return container;
  };

  const [detailContainer, setDetailContainer] = React.useState<ProjectDetailContainer>(
    getProjectContainer(selected.id),
  );

  useEffect(() => {
    const selectedProject = projects.find((p) => p.id === selected.id) ?? projects[0];
    setSelected(selectedProject);
    const newSelectedTopic = selectedProject?.topics?.find(
      (topic) => topic.id === selectedTopic?.id,
    );
    setSelectedTopic(newSelectedTopic);
  }, [props.projects, selected]);

  return (
    <Grid container xs={12} spacing={2}>
      <Grid item xs={4}>
        <ProjectSelectionTable
          projects={projects}
          selected={selected}
          onSelect={(project) => {
            if (project.id === selected.id) return;

            setSelected(project);
            setSelectedTopic(undefined);
            const container = getProjectContainer(project.id);
            setDetailContainer(container);
          }}
        />
      </Grid>

      <Grid item xs={8}>
        <Subscribe to={[detailContainer]}>
          {(detailContainer) => {
            const projectResource = detailContainer.state.entity;
            if (projectResource.isLoading) return <CircularProgress />;
            const project = detailContainer.state.entity?.data;

            if (projectResource.hasData && !project)
              return <p>Project konnte nicht gefunden werden.</p>;

            const topics = project?.topics ?? [];
            return (
              <ProjectTopics
                disabled={selected.state !== ProjectState.idle}
                selected={topics.find((t) => t.id === selectedTopic?.id)}
                topics={topics}
                projectId={selected.id}
                onSelect={(topic) => {
                  setSelectedTopic(topic);
                }}
              />
            );
          }}
        </Subscribe>
      </Grid>
    </Grid>
  );
}

type ProjectSelectionTableProps = {
  projects: Project[];
  selected?: Project;
  onSelect: (project: Project) => void;
};

function ProjectSelectionTable(props: ProjectSelectionTableProps) {
  const history = useHistory();
  const provider = useProvider();
  const { onSelect, projects, selected } = props;

  return (
    <Paper>
      <Toolbar>
        <Grid xs={12} justify="space-between" alignItems="center">
          <Grid item>
            <h2 style={{ width: 'auto', float: 'left', margin: 0 }}>Projekte</h2>
          </Grid>

          <Grid item>
            <CreateProjectButton />
          </Grid>
        </Grid>
      </Toolbar>
      <Box p={2}>
        <List component="nav" aria-label="secondary mailbox folder">
          {projects.map((project) => {
            const isSelected = selected?.id === project.id;

            return (
              <ListItem
                key={project.id}
                button
                selected={isSelected}
                onClick={(event) => {
                  // setSelected(project);
                  onSelect(project);
                }}
              >
                <ListItemText primary={project.name} />
                <ListItemIcon>
                  <IconButton
                    aria-label="info"
                    onClick={() => {
                      history.push(`/project/${project.id}`);
                    }}
                  >
                    <InfoIcon />
                  </IconButton>
                </ListItemIcon>
                <ListItemIcon>
                  <AlertDialog
                    title="Projekt löschen"
                    contentText={`Soll das Projekt ${project.name} wirklich gelöscht werden? Alle Themen, Stichworte und Auswertungen werden ebenfalls gelöscht.`}
                    action={async () => {
                      const container = provider.getProjectDetailContainer();
                      container.setEntity({ ...project, topics: [] });
                      await container.delete();
                      const { state } = container;

                      return {
                        success: state.delete.isFinished,
                        error: state.delete.hasError
                          ? 'Projekt konnte nicht gelöscht werden.'
                          : undefined,
                      };
                    }}
                    builder={(setOpen) => {
                      return (
                        <IconButton
                          edge="end"
                          aria-label="delete"
                          onClick={() => {
                            setOpen(true);
                          }}
                        >
                          <DeleteIcon />
                        </IconButton>
                      );
                    }}
                  />
                </ListItemIcon>
              </ListItem>
            );
          })}
        </List>
      </Box>
    </Paper>
  );
}

function NoProjectsView() {
  const container = useProvider().getProjectDetailContainer();
  const [name, setName] = React.useState('');

  return (
    <Subscribe to={[container]}>
      {(container) => {
        return (
          <form
            onSubmit={(e) => {
              e.preventDefault();
            }}
          >
            <Column>
              <h2>Erstes Projekt</h2>
              <p>Erstelle dein erstes Projekt und beginne mit der Suche</p>

              <TextField placeholder="Projektname" onChange={(e) => setName(e.target.value)} />

              <Button
                type="button"
                color="primary"
                onClick={() => {
                  container.create({
                    name,
                  });
                }}
              >
                {container.state.save.isLoading ? <CircularProgress /> : 'Projekt erstellen'}
              </Button>
            </Column>
          </form>
        );
      }}
    </Subscribe>
  );
}

function CreateProjectButton() {
  const provider = useProvider();
  const [name, setName] = React.useState('');

  return (
    <AlertDialog
      title="Neues Projekt"
      content={<TextField placeholder="Projektname" onChange={(e) => setName(e.target.value)} />}
      action={async () => {
        const container = provider.getProjectDetailContainer();
        await container.create({ name });

        const result = {
          success: container.state.save.isFinished,
          error: container.state.save.hasError ? 'Fehler' : undefined,
        };
        return result;
      }}
      builder={(setOpen) => {
        return (
          <Button
            onClick={() => {
              setOpen(true);
            }}
          >
            Neues Projekt
          </Button>
        );
      }}
    />
  );
}

type ProjectTopicsProps = {
  projectId: Project['id'];
  selected?: Topic;
  onSelect: (topic: Topic) => void;
  topics: Topic[];
  disabled?: boolean;
};
function ProjectTopics(props: ProjectTopicsProps) {
  const disabled = props.disabled ?? false;
  const [selected, setSelected] = React.useState(props.selected);
  const { onSelect, topics } = props;

  useEffect(() => {
    setSelected(props.selected);
  }, [props.selected]);

  return (
    <Grid container xs={12} spacing={2}>
      <Grid item xs={6}>
        <Paper>
          <Toolbar>
            <Grid container justify="space-between" alignItems="center">
              <h2>Themen</h2>
            </Grid>
          </Toolbar>
          <CreateTopicForm
            projectId={props.projectId}
            onCreated={(topic) => {
              // setTopics([...topics, topic]);
            }}
          />
          <List style={{ paddingBottom: 0 }}>
            {topics.map((topic) => {
              return (
                <TopicItem
                  key={topic.id}
                  topic={topic}
                  disabled={disabled}
                  selected={selected?.id === topic.id}
                  onSelect={() => onSelect(topic)}
                />
              );
            })}
          </List>
        </Paper>
      </Grid>
      <Grid item xs={6}>
        {selected && <TopicKeywords topicId={selected?.id} keywords={selected?.keywords ?? []} />}
      </Grid>
    </Grid>
  );
}

type TopicKeywordsProps = {
  topicId: Topic['id'];
  keywords: Keyword[];
  disabled?: boolean;
};

function TopicKeywords(props: TopicKeywordsProps) {
  const disabled = props.disabled ?? false;
  const provider = useProvider();
  const { keywords } = props;
  return (
    <Paper>
      <Toolbar>
        <Grid container justify="space-between" alignItems="center">
          <h2>Stichworte</h2>
        </Grid>
      </Toolbar>
      <CreateKeywordForm topicId={props.topicId} topicKeywords={keywords.map((k) => k.name)} />
      <List style={{ paddingBottom: 0 }}>
        {keywords.map((keyword) => {
          return (
            <ListItem key={keyword.id}>
              <ListItemText primary={keyword.name} />
              {!disabled && (
                <ListItemSecondaryAction>
                  <Button
                    onClick={async () => {
                      const container = provider.getKeywordDetailContainer();
                      container.setEntity(keyword);
                      await container.delete();
                    }}
                  >
                    X
                  </Button>
                </ListItemSecondaryAction>
              )}
            </ListItem>
          );
        })}
      </List>
    </Paper>
  );
}

type CreateTopicFormProps = {
  projectId: Project['id'];
  onCreated: (topic: Topic) => void;
};

function CreateTopicForm(props: CreateTopicFormProps) {
  const { projectId, onCreated } = props;
  const provider = useProvider();
  const [name, setName] = React.useState('');
  const [container, setContainer] = React.useState(provider.getTopicDetailContainer());

  return (
    <Subscribe to={[container]}>
      {(container) => {
        const createTopic = async () => {
          await container.create({
            name,
            projectId,
          });

          if (container.state.save.isFinished) {
            setName('');
            setContainer(provider.getTopicDetailContainer());
            onCreated(container.state.entity.data!);
          }
        };

        return (
          <form
            onSubmit={(e) => {
              e.preventDefault();
              createTopic();
            }}
          >
            <Box p={2}>
              <Grid container justify="space-between" alignItems="center" spacing={2}>
                <Grid item style={{ flexGrow: 1, display: 'flex' }}>
                  <TextField
                    onChange={(e) => {
                      setName(e.target.value);
                    }}
                    fullWidth
                    value={name}
                    variant="outlined"
                    placeholder="Thema..."
                  />
                </Grid>
                <Grid item>
                  <Button type="submit">+</Button>
                </Grid>
              </Grid>
            </Box>
          </form>
        );
      }}
    </Subscribe>
  );
}

type CreateKeywordFormProps = {
  topicId: Topic['id'];
  topicKeywords: string[];
  disabled?: boolean;
};

function CreateKeywordForm(props: CreateKeywordFormProps) {
  const { topicId, topicKeywords } = props;
  const provider = useProvider();
  const disabled = props.disabled ?? false;
  const [name, setName] = React.useState('');
  const [container, setContainer] = React.useState(provider.getKeywordDetailContainer());

  const [loading, setLoading] = React.useState(false);
  const [recommendations, setRecommendations] = React.useState<KeywordRecommedation[] | undefined>(
    undefined,
  );
  const [showRecommendations, setShowRecommendations] = React.useState(false);

  const [inputError, setInputError] = React.useState<string | undefined>(undefined);

  const x = async () => {
    try {
      const results = await provider.getKeywordRecommendationService().search(name);
      const filtered = results.filter((result) => !topicKeywords.includes(result.keyword));
      setRecommendations(filtered);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setLoading(true);
    x();
  }, [name]);

  const keywordRecommendationClass = 'keyword-rec-item';

  return (
    <Subscribe to={[container]}>
      {(container) => {
        const createKeyword = async (keyword: string, reset: boolean = false) => {
          await container.create({
            name: keyword,
            topicId,
          });

          if (container.state.save.isFinished) {
            if (reset) {
              setName('');
              setRecommendations(undefined);
              setShowRecommendations(false);
            }
            setContainer(provider.getKeywordDetailContainer());
          }
        };

        return (
          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (topicKeywords.includes(name)) {
                setInputError('Stichwort kann Thema nur einmal zugewiesen werden.');
                return;
              }
              createKeyword(name, true);
            }}
          >
            <Box p={2}>
              <Grid container alignItems="center" spacing={2}>
                <Grid item style={{ position: 'relative', display: 'flex', flexGrow: 1 }}>
                  <TextField
                    onBlur={(e) => {
                      if (e.relatedTarget) {
                        const className = (e.relatedTarget as { className?: string }).className;
                        // Ignore keyword selection otherwise the selected keyword is not used
                        if (className?.includes(keywordRecommendationClass) ?? false) return;
                      }
                      setShowRecommendations(false);
                    }}
                    onFocus={() => {
                      setShowRecommendations(true);
                    }}
                    onChange={(e) => {
                      setInputError(undefined);
                      setShowRecommendations(true);
                      setName(e.target.value);
                    }}
                    fullWidth
                    value={name}
                    variant="outlined"
                    placeholder="Stichwort..."
                  />
                  <div style={{ position: 'absolute', bottom: '-60px', zIndex: 2, width: '100%' }}>
                    {recommendations && showRecommendations && recommendations.length > 0 && (
                      <List style={{ background: 'white' }}>
                        {recommendations.map((r) => {
                          return (
                            <ListItem
                              className={keywordRecommendationClass}
                              button
                              onClick={() => {
                                createKeyword(r.keyword, false);

                                setRecommendations(
                                  recommendations.filter((k) => r.keyword !== k.keyword),
                                );
                              }}
                            >
                              <ListItemText primary={r.keyword} />
                            </ListItem>
                          );
                        })}
                      </List>
                    )}
                  </div>
                </Grid>
                <Grid item>
                  <Button type="submit">+</Button>
                </Grid>
              </Grid>
              <Grid>
                {inputError && <p style={{ color: 'red', marginBottom: 0 }}>{inputError}</p>}
              </Grid>
            </Box>
          </form>
        );
      }}
    </Subscribe>
  );
}

type TopicItemProps = {
  topic: Topic;
  disabled?: boolean;
  selected: boolean;
  onSelect: () => void;
};

function TopicItem(props: TopicItemProps) {
  const provider = useProvider();
  const disabled = props.disabled ?? false;
  const { topic, selected, onSelect } = props;
  const [name, setName] = React.useState(topic.name);

  return (
    <ListItem
      key={topic.id}
      button
      disabled={disabled}
      selected={selected}
      onClick={(event) => {
        onSelect();
      }}
    >
      <ListItemText
        primary={
          <TextField
            value={name}
            onChange={(e) => setName(e.target.value)}
            onBlur={async (e) => {
              if (name !== topic.name) {
                const container = provider.getTopicDetailContainer();
                container.setEntity(topic);
                await container.update({ id: topic.id, name });
              }
            }}
          />
        }
      />
      <ListItemSecondaryAction>
        <AlertDialog
          title="Thema löschen"
          contentText={`Soll das Thema ${topic.name} wirklich gelöscht werden? Alle Stichworte werden ebenfalls gelöscht.`}
          action={async () => {
            const container = provider.getTopicDetailContainer();
            container.setEntity(topic);
            await container.delete();
            const { state } = container;

            return {
              success: state.delete.isFinished,
              error: state.delete.hasError ? 'Thema konnte nicht gelöscht werden.' : undefined,
            };
          }}
          builder={(setOpen) => {
            return (
              <IconButton
                edge="end"
                aria-label="delete"
                onClick={() => {
                  setOpen(true);
                }}
              >
                {' '}
                <DeleteIcon />
              </IconButton>
            );
          }}
        />
      </ListItemSecondaryAction>
    </ListItem>
  );
}
