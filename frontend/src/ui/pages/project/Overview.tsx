import {
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
  TextField,
  Toolbar,
} from '@material-ui/core';
import DeleteIcon from '@material-ui/icons/Delete';
import InfoIcon from '@material-ui/icons/Info';
import { Project, ProjectState, Topic } from 'data/entity';
import { ProjectDetailContainer } from 'model';
import { useProvider } from 'Provider';
import React, { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import AlertDialog from 'ui/components/form/AlertDialog';
import { Column } from 'ui/components/form/Column';
import { ProjectTopics } from 'ui/components/project/TopicTable';
import { Center } from 'ui/components/util/Center';
import { Subscribe } from 'unstated-typescript';

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
            <CreateProjectButton existingProjectNames={projects.map((project) => project.name)} />
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
                onClick={() => {
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
