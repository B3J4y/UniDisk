import React from 'react';
import { Project } from 'data/entity';
import { useParams } from 'react-router-dom';
import {
  createStyles,
  Drawer,
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

type Views = 'general' | 'map' | 'results';

export function ProjectDetailsPage() {
  const params = useParams<{ projectId?: Project['id']; view?: Views }>();
  const { projectId } = params;

  const [activeView, setActiveView] = React.useState(params.view ?? 'general');

  if (!projectId) {
    return <p>Project ID muss angegeben werden.</p>;
  }

  return (
    <>
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
      />
    </>
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

const navigationStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
      maxWidth: 360,
      backgroundColor: theme.palette.background.paper,
    },
    nested: {
      paddingLeft: theme.spacing(4),
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
};

function NavigationDrawer(props: NavigationDrawerProps) {
  const { active, onSelected } = props;
  const classes = useStyles();
  const navigationClasses = navigationStyles();

  return (
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
  );
}
