import { Box, Grid, IconButton } from '@material-ui/core';
import AppBar from '@material-ui/core/AppBar';
import Collapse from '@material-ui/core/Collapse';
import CssBaseline from '@material-ui/core/CssBaseline';
import Drawer from '@material-ui/core/Drawer';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import DescriptionIcon from '@material-ui/icons/Description';
import ExitToApp from '@material-ui/icons/ExitToApp';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import PersonIcon from '@material-ui/icons/Person';
import { THEME } from 'config';
import { UserContainer } from 'model/LoginState';
import React from 'react';
import { useHistory } from 'react-router-dom';
import { Subscribe } from 'unstated-typescript';
import GroupIcon from '@material-ui/icons/Group';
import LocationCityIcon from '@material-ui/icons/LocationCity';
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

type DrawerIcon = {
  name: string;
  link: string;
  icon: any;
  sublist?: DrawerIcon[];
};

const icons: DrawerIcon[] = [];

function MyAppBar() {
  const classes = useStyles();
  return (
    <AppBar position="fixed" className={classes.appBar}>
      <Toolbar>
        <Grid container xs={12} justify="space-between">
          <Grid item xs={9} container alignItems="center">
            <Typography variant="h6" noWrap style={{ color: THEME.colorOnPrimary }}>
              Unidisk
            </Typography>
          </Grid>

          <Box>
            <Subscribe to={[UserContainer]}>
              {(container) => {
                return (
                  <IconButton
                    aria-label="delete"
                    onClick={() => {
                      container.logout();
                    }}
                  >
                    <ExitToApp style={{ fill: 'white' }} />
                  </IconButton>
                );
              }}
            </Subscribe>
          </Box>
        </Grid>
      </Toolbar>
    </AppBar>
  );
}

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

export function NavItem(props: { item: DrawerIcon; nested?: boolean }) {
  const { item, nested } = props;
  const classes = navigationStyles();
  const history = useHistory();

  const getKey = (): string => 'NavBarItem' + item.name;

  const storageKey = getKey();
  const storageItem = localStorage.getItem(storageKey);
  var visibility = true;
  if (storageItem) {
    const visible = JSON.parse(storageItem).visible as boolean;
    visibility = visible;
  }

  const [open, setOpen] = React.useState(visibility);
  const handleClick = () => {
    const visible = open;
    setOpen(!visible);
    localStorage.setItem(storageKey, JSON.stringify({ visible: !visible }));
  };

  return (
    <>
      <ListItem
        button
        className={nested ? classes.nested : undefined}
        key={item.name}
        onClick={(e) => {
          if (!item.sublist) {
            history.push(`${item.link}`);
          } else {
            handleClick();
          }
        }}
      >
        <ListItemIcon>{item.icon}</ListItemIcon>
        <ListItemText primary={item.name} />
        {!item.sublist ? undefined : open ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      {item.sublist && (
        <Collapse in={open} timeout="auto" unmountOnExit>
          <List component="div" disablePadding>
            {item.sublist.map((item) => (
              <NavItem item={item} nested={true} />
            ))}
          </List>
        </Collapse>
      )}
    </>
  );
}

export default function DefaultDrawer({ children }: { children: any }) {
  const classes = useStyles();
  const navigationClasses = navigationStyles();
  const [opened, setOpen] = React.useState<string[]>([]);

  const isOpen = (name: string) => opened.includes(name);

  return (
    <div className={classes.root}>
      <CssBaseline />
      <MyAppBar />
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
            {icons.map((item: DrawerIcon) => {
              return <NavItem item={item} />;
            })}
          </List>
        </div>
      </Drawer>
      <main className={classes.content}>
        <Toolbar />
        {children}
      </main>
    </div>
  );
}
