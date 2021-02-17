import {
  Button,
  Checkbox,
  CircularProgress,
  createStyles,
  Drawer,
  Grid,
  List,
  ListItem,
  ListItemIcon,
  ListItemSecondaryAction,
  ListItemText,
  makeStyles,
  Paper,
  Theme,
  Toolbar,
} from '@material-ui/core';
import AssessmentIcon from '@material-ui/icons/Assessment';
import BuildIcon from '@material-ui/icons/Build';
import MapIcon from '@material-ui/icons/Map';
import { Project, ProjectDetails, ProjectState, Topic } from 'data/entity';
import MaterialTable from 'material-table';
import { ProjectDetailContainer } from 'model';
import { useProvider } from 'Provider';
import React from 'react';
import { useLocation, useParams } from 'react-router-dom';
import { OLMap } from 'ui/components/forms/Map';
import { ProjectTopics } from 'ui/components/project/TopicTable';
import { Provider, Subscribe } from 'unstated-typescript';
import * as htmlToImage from 'html-to-image';
import { ProjectEvaluationResult } from 'data/repositories';
type Views = 'general' | 'map' | 'results';

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

export function ProjectDetailsPage() {
  const provider = useProvider();
  const params = useParams<{ projectId?: Project['id'] }>();
  const { projectId } = params;

  const queryParameter = useQuery();
  const view = queryParameter.get('view') ?? 'general';

  const [activeView, setActiveView] = React.useState(view as Views);

  let mapController: MapController | undefined = undefined;

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

        const isMapView = activeView === 'map';
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
              <Grid container justify="space-between" xs={8}>
                <Grid>
                  <h2 style={{ textAlign: 'left', marginTop: 0 }}>{project.name}</h2>
                </Grid>
                <Grid>
                  {isMapView && (
                    <Button
                      onClick={() => {
                        if (!mapController) return;
                        mapController.export();
                      }}
                    >
                      Export
                    </Button>
                  )}
                </Grid>
              </Grid>
              {activeView === 'general' && <GeneralProjectDetails />}
              {activeView === 'results' && (
                <ProjectResultsGuard project={project}>
                  {(result) => {
                    return <ProjectResults project={project} result={result} />;
                  }}
                </ProjectResultsGuard>
              )}
              {isMapView && (
                <ProjectResultsGuard project={project}>
                  {(result) => {
                    return (
                      <ProjectResultMap
                        result={result}
                        onCreate={(controller) => {
                          mapController = controller;
                        }}
                      />
                    );
                  }}
                </ProjectResultsGuard>
              )}
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

type ProjectResultsGuardProps = {
  project: ProjectDetails;
  children: (results: ProjectEvaluationResult) => React.ReactNode | React.ReactNode[];
};
function ProjectResultsGuard(props: ProjectResultsGuardProps) {
  const { project, children } = props;

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

  return (
    <Subscribe to={[ProjectDetailContainer]}>
      {(container) => {
        const result = container.state.result;

        const { data: results, isLoading, isIdle, hasError } = result;
        if (isIdle) {
          window.requestAnimationFrame(() => container.loadResults());
        }

        if (isLoading || isIdle) {
          return <CircularProgress />;
        }

        if (hasError) {
          return <p>Ergebnisse konnten nicht geladen werden.</p>;
        }

        if (result.hasData && !results)
          return <p>Projektergenisse stehen noch nicht zur Verfügung</p>;

        return children(results!);
      }}
    </Subscribe>
  );
}

type ProjectResultsProps = {
  project: ProjectDetails;
  result: ProjectEvaluationResult;
};
function ProjectResults(props: ProjectResultsProps) {
  const { project, result } = props;

  const { topicScores } = result;
  return (
    <MaterialTable
      localization={{
        body: {
          emptyDataSourceMessage: `Keine Ergebnisse vorhanden`,
        },
        toolbar: {
          searchPlaceholder: 'Suche',
          searchTooltip: 'Suche',
        },
        pagination: {
          labelRowsPerPage: 'Zeilen pro Seite:',
          labelRowsSelect: 'Zeilen',
          labelDisplayedRows: '{from}-{to} von {count}',
          nextAriaLabel: 'Nächste Seite',
        },
        header: {
          actions: 'Aktionen',
        },
      }}
      columns={[
        { title: 'Universität', field: 'university.name' },
        { title: 'Thema', field: 'topic.name' },
        { title: 'Score', field: 'score', type: 'numeric' },
        {
          title: 'Anzahl Einträge',
          field: 'entryCount',
          type: 'numeric',
        },
      ]}
      data={topicScores}
      title="Auswertung"
    />
  );
}

type MapController = {
  export: () => void;
};
type ProjectResultMapProps = {
  onCreate?: (controller: MapController) => void;
  result: ProjectEvaluationResult;
};
function ProjectResultMap(props: ProjectResultMapProps) {
  const { onCreate, result } = props;
  const { topicScores } = result;

  const topics = Array.from(new Set(topicScores.map((topic) => topic.topic.name)));
  const [visibleTopics, setVisibleTopics] = React.useState(new Set(topics));

  return (
    <Grid item xs={12} container spacing={2}>
      <Grid item xs={8}>
        <a id="image-download" download="map.png" style={{ display: 'none' }}></a>
        <OLMap
          height={700}
          onCreate={(map) => {
            if (!onCreate) return;

            const exportCallback = () => {
              var exportOptions = {
                filter: function (element) {
                  return element.className ? element.className.indexOf('ol-control') === -1 : true;
                },
              };

              map.once('rendercomplete', function () {
                htmlToImage.toPng(map.getTargetElement(), exportOptions).then(function (dataURL) {
                  const link = document.getElementById('image-download');
                  if (!link) {
                    return;
                  }
                  // @ts-ignore
                  link.href = dataURL;
                  link.click();
                });
              });
              map.renderSync();
            };
            const controller: MapController = {
              export: exportCallback,
            };

            onCreate(controller);
          }}
          initialPosition={{
            lat: 51.295471273122644,
            lng: 9.568830237027088,
            zoom: 6.553662572584849,
          }}
        />
      </Grid>
      <Grid item xs={4}>
        <Paper>
          <Toolbar>
            <h1>Legende</h1>
          </Toolbar>
          <List>
            {topics.map((topic) => {
              const checked = visibleTopics.has(topic);
              return (
                <ListItem key={topic}>
                  <ListItemText primary={topic} />
                  <ListItemSecondaryAction>
                    <Checkbox
                      edge="end"
                      onChange={() => {
                        if (checked) {
                          visibleTopics.delete(topic);
                        } else {
                          visibleTopics.add(topic);
                        }
                        setVisibleTopics(new Set(visibleTopics));
                      }}
                      checked={checked}
                      inputProps={{ 'aria-labelledby': topic }}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
              );
            })}
          </List>
        </Paper>
      </Grid>
    </Grid>
  );
}
