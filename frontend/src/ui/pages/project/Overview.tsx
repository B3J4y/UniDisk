import {
  AppBar,
  Box,
  Button,
  CircularProgress,
  Grid,
  IconButton,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Paper,
  Tab,
  Tabs,
  TextField,
  Toolbar,
} from '@material-ui/core';
import BlockIcon from '@material-ui/icons/Block';
import CheckIcon from '@material-ui/icons/Check';
import DeleteIcon from '@material-ui/icons/Delete';
import InfoIcon from '@material-ui/icons/Info';
import { Project, ProjectState, Topic } from 'data/entity';
import { ProjectDetailContainer } from 'model';
import { useProvider } from 'Provider';
import React, { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import AlertDialog from 'ui/components/form/AlertDialog';
import { Column } from 'ui/components/form/Column';
import { DequeueProjectDialog } from 'ui/components/project/DequeueDialog';
import { EnqueueProjectDialog } from 'ui/components/project/EnqueueDialog';
import { ProjectTopics } from 'ui/components/project/TopicTable';
import { Center } from 'ui/components/util/Center';
import { Subscribe } from 'unstated-typescript';
import { mapProjectState } from 'util/language';

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

  // Choose idle on as default and if it doesn't exist take first project
  const firstSelected =
    projects.find((project) => project.state === ProjectState.idle) ?? projects[0];

  const [selected, setSelected] = React.useState(firstSelected);
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

const selectionStates = [
  ProjectState.idle,
  ProjectState.ready,
  ProjectState.processing,
  ProjectState.completed,
  ProjectState.error,
];

function ProjectSelectionTable(props: ProjectSelectionTableProps) {
  const history = useHistory();
  const provider = useProvider();
  const { onSelect, projects, selected } = props;
  const [selectedState, setSelectedState] = React.useState(selected?.state ?? ProjectState.idle);

  const handleTabChange = (_event: React.ChangeEvent<{}>, state: ProjectState) => {
    setSelectedState(state);
  };

  const stateProjects = projects.filter((project) => project.state === selectedState);

  return (
    <Paper>
      <Toolbar>
        <Grid xs={12} container justify="space-between" alignItems="center">
          <Grid item>
            <h2 style={{ width: 'auto', float: 'left', margin: 0 }}>Projekte</h2>
          </Grid>

          <Grid item>
            <CreateProjectButton existingProjectNames={projects.map((project) => project.name)} />
          </Grid>
        </Grid>
      </Toolbar>
      <AppBar position="static">
        <Tabs
          value={selectedState}
          onChange={handleTabChange}
          scrollButtons="auto"
          variant="scrollable"
        >
          {selectionStates.map((state) => {
            const label = mapProjectState(state);
            return <Tab label={label} value={state} />;
          })}
        </Tabs>
      </AppBar>

      <Box p={0}>
        {stateProjects.length === 0 && (
          <Box p={1}>
            <p>Keine Projekte</p>
          </Box>
        )}
        {stateProjects.length > 0 && (
          <List component="nav" aria-label="secondary mailbox folder">
            {stateProjects.map((project) => {
              const isSelected = selected?.id === project.id;

              return (
                <ListItem
                  key={project.id}
                  button
                  selected={isSelected}
                  onClick={() => {
                    onSelect(project);
                  }}
                >
                  <ListItemText primary={project.name} />

                  {project.state === ProjectState.idle && (
                    <EnqueueProjectDialog project={project}>
                      {(show) => {
                        return (
                          <ListItemIcon>
                            <IconButton
                              aria-label="enqueue"
                              onClick={() => {
                                show();
                              }}
                            >
                              <CheckIcon />
                            </IconButton>
                          </ListItemIcon>
                        );
                      }}
                    </EnqueueProjectDialog>
                  )}
                  {project.state === ProjectState.ready && (
                    <DequeueProjectDialog project={project}>
                      {(show) => {
                        return (
                          <ListItemIcon>
                            <IconButton
                              aria-label="dequeue"
                              onClick={() => {
                                show();
                              }}
                            >
                              <BlockIcon />
                            </IconButton>
                          </ListItemIcon>
                        );
                      }}
                    </DequeueProjectDialog>
                  )}
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
        )}
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

type CreateProjectButtonProps = {
  existingProjectNames: string[];
};
function CreateProjectButton(props: CreateProjectButtonProps) {
  const provider = useProvider();
  const [name, setName] = React.useState('');

  const { existingProjectNames } = props;

  return (
    <AlertDialog
      title="Neues Projekt"
      content={<TextField placeholder="Projektname" onChange={(e) => setName(e.target.value)} />}
      action={async () => {
        const projectName = name.trim();
        const projectNameAvailable = !existingProjectNames.includes(projectName);
        if (!projectNameAvailable) {
          return {
            success: false,
            error: 'Projektname bereits vergeben.',
          };
        }
        const container = provider.getProjectDetailContainer();
        await container.create({ name: projectName });

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
