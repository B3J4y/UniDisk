import {
  Box,
  Button,
  CircularProgress,
  Grid,
  IconButton,
  List,
  ListItem,
  ListItemSecondaryAction,
  ListItemText,
  Paper,
  TextField,
  Toolbar,
} from '@material-ui/core';
import DeleteIcon from '@material-ui/icons/Delete';
import { Project } from 'data/entity';
import { useProvider } from 'Provider';
import React from 'react';
import AlertDialog from 'ui/components/form/AlertDialog';
import { Column } from 'ui/components/form/Column';
import { Center } from 'ui/components/util/Center';
import { Subscribe } from 'unstated-typescript';

export function ProjectOverviewPage() {
  const provider = useProvider();

  return (
    <Subscribe to={[provider.getAllProjectContainer()]}>
      {(container) => {
        const projectsResource = container.state.entities;

        const { isLoading, isIdle, data, hasError } = projectsResource;

        if (isIdle) container.load();

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
        return (
          <Grid container xs={12} spacing={2}>
            <Grid item xs={4}>
              <ProjectSelectionTable projects={projects} onSelect={(project) => {}} />
            </Grid>
          </Grid>
        );
      }}
    </Subscribe>
  );
}

type ProjectSelectionTableProps = {
  projects: Project[];
  selected?: Project;
  onSelect: (project: Project) => void;
};

function ProjectSelectionTable(props: ProjectSelectionTableProps) {
  const provider = useProvider();
  const { onSelect, projects } = props;
  const [selected, setSelected] = React.useState(props.selected);
  console.log({ projects });

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
                  setSelected(project);
                  onSelect(project);
                }}
              >
                <ListItemText primary={project.name} />
                <ListItemSecondaryAction>
                  <AlertDialog
                    title="Projekt löschen"
                    contentText={`Soll das Projekt ${project.name} wirklich gelöscht werden? Alle Themen, Stichworte und Auswertungen werden ebenfalls gelöscht.`}
                    action={async () => {
                      const container = provider.getProjectDetailContainer();
                      container.setEntity(project);
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
                          {' '}
                          <DeleteIcon />
                        </IconButton>
                      );
                    }}
                  />
                </ListItemSecondaryAction>
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
