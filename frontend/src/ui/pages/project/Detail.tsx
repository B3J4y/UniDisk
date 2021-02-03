import {
  CircularProgress,
  createStyles,
  Drawer,
  Grid,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  makeStyles,
  Theme,
  Toolbar,
} from '@material-ui/core';
import AssessmentIcon from '@material-ui/icons/Assessment';
import BuildIcon from '@material-ui/icons/Build';
import MapIcon from '@material-ui/icons/Map';
import { Project, ProjectDetails, ProjectState, Topic } from 'data/entity';
import { ProjectDetailContainer } from 'model';
import { useProvider } from 'Provider';
import React from 'react';
import { useParams } from 'react-router-dom';
import { ProjectTopics } from 'ui/components/project/TopicTable';
import { Provider, Subscribe } from 'unstated-typescript';

type Views = 'general' | 'map' | 'results';

export function ProjectDetailsPage() {
  const provider = useProvider();
  const params = useParams<{ projectId?: Project['id']; view?: Views }>();
  const { projectId } = params;

  const [activeView, setActiveView] = React.useState(params.view ?? 'general');

  if (!projectId) {
    return <p>Project ID muss angegeben werden.</p>;
  }

  const detailContainer = provider.getProjectDetailContainer();
  detailContainer.load(projectId);

  return (
    <Subscribe to={[detailContainer]}>
      {(container) => {
        const projectResource = container.state.entity;

        const { isLoading, hasError, error, data: project } = projectResource;

        if (isLoading) return <CircularProgress />;

        if (hasError) return <p>{`${error}`}</p>;

        if (!project) return <p>Projekt konnte nicht gefunden werden.</p>;

        return (
          <Provider inject={[detailContainer]}>
            <NavigationDrawer
              active={activeView}
              onSelected={(view) => {
                setActiveView(view);

                const newurl =
                  window.location.protocol +
                  '//' +
                  window.location.host +
                  window.location.pathname +
                  `?view=${view}`;
                window.history.pushState({ path: newurl }, '', newurl);
              }}
            >
              <h2 style={{ textAlign: 'left', marginTop: 0 }}>{project.name}</h2>
              {activeView === 'general' && <GeneralProjectDetails />}
              {activeView === 'results' && <ProjectResults project={project} />}
              {activeView === 'map' && <ProjectResultMap />}
            </NavigationDrawer>
          </Provider>
        );
      }}
    </Subscribe>
  );
}

const drawerWidth = 240;

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
    },
    appBar: {
      zIndex: theme.zIndex.drawer + 1,
    },
    drawer: {
      width: drawerWidth,
      flexShrink: 0,
    },
    drawerPaper: {
      width: drawerWidth,
    },
    drawerContainer: {
      overflow: 'auto',
    },
    content: {
      flexGrow: 1,
      padding: theme.spacing(3),
    },
  }),
);

type Item = {
  view: Views;
  name: string;
  icon: React.ReactNode;
};

const items: Item[] = [
  {
    view: 'general',
    name: 'Allgemein',
    icon: <BuildIcon />,
  },
  {
    view: 'results',
    name: 'Auswertung',
    icon: <AssessmentIcon />,
  },
  {
    view: 'map',
    name: 'Karte',
    icon: <MapIcon />,
  },
];

type NavigationDrawerProps = {
  active: Views;
  onSelected: (view: Views) => void;
  children: React.ReactNode[];
};

function NavigationDrawer(props: NavigationDrawerProps) {
  const { active, onSelected, children } = props;
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <Drawer
        className={classes.drawer}
        variant="permanent"
        classes={{
          paper: classes.drawerPaper,
        }}
      >
        <Toolbar />
        <div className={classes.drawerContainer}>
          <List>
            {items.map((item: Item) => {
              return (
                <ListItem
                  button
                  key={item.name}
                  selected={item.view === active}
                  onClick={(e) => {
                    onSelected(item.view);
                  }}
                >
                  <ListItemIcon>{item.icon}</ListItemIcon>
                  <ListItemText primary={item.name} />
                </ListItem>
              );
            })}
          </List>
        </div>
      </Drawer>
      <main className={classes.content}>{children}</main>
    </div>
  );
}

function GeneralProjectDetails() {
  const [selectedTopic, setSelectedTopic] = React.useState<Topic | undefined>(undefined);

  return (
    <Subscribe to={[ProjectDetailContainer]}>
      {(container) => {
        const project = container.state.entity.data!;
        const { topics } = project;
        if (!selectedTopic && topics.length > 0) {
          window.requestAnimationFrame(() => {
            setSelectedTopic(topics[0]);
          });
        }
        const disabled = project.state !== ProjectState.idle;
        return (
          <Grid container spacing={2}>
            <ProjectTopics
              disabled={disabled}
              selected={topics.find((t) => t.id === selectedTopic?.id)}
              topics={topics}
              projectId={project.id}
              onSelect={(topic) => {
                setSelectedTopic(topic);
              }}
            />
          </Grid>
        );
      }}
    </Subscribe>
  );
}

type ProjectResultsProps = {
  project: ProjectDetails;
};
function ProjectResults(props: ProjectResultsProps) {
  const { project } = props;

  if (project.state === ProjectState.idle)
    return (
      <p>
        Hier findest du die Projektergebnisse, nachdem die Bearbeitung dessen abgeschlossen wurde.
        Unter 'Allgemein' kannst du dein Projekt zur Bearbeitung freigeben.
      </p>
    );
  if (project.state === ProjectState.processing || project.state === ProjectState.ready)
    return (
      <p>
        Sobald die Bearbeitung deines Projekts abgeschlossen wurde, findest du hier deine
        Ergebnisse.
      </p>
    );

  return <p>Auswertung</p>;
}

function ProjectResultMap() {
  return <p>Karte</p>;
}
