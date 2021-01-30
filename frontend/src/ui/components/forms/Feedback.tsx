import { Box } from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import {
  createStyles,
  makeStyles,
  Theme,
  useTheme,
} from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import React from "react";

export interface ConfirmationDialogRawProps {
  classes: Record<"paper", string>;
  id: string;
  keepMounted: boolean;
  value: string;
  open: boolean;
  onClose: (value?: string) => void;
  title?: string;
  content?: React.ReactNode;
  actions?: React.ReactNode[];
}

function ConfirmationDialogRaw(props: ConfirmationDialogRawProps) {
  const { onClose, value: valueProp, open, ...other } = props;
  const [value, setValue] = React.useState(valueProp);

  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down("sm"));
  React.useEffect(() => {
    if (!open) {
      setValue(valueProp);
    }
  }, [valueProp, open]);

  const handleCancel = () => {
    onClose();
  };

  const handleOk = () => {
    onClose(value);
  };

  const handleClose = () => {
    onClose();
  };

  return (
    <Dialog
      fullScreen={fullScreen}
      open={open}
      transitionDuration={400}
      onClose={handleClose}
      maxWidth="xs"
      aria-labelledby="confirmation-dialog-title"
      {...other}
    >
      {props.title && (
        <DialogContent dividers>
          <DialogTitle id="confirmation-dialog-title">
            {props.title}
          </DialogTitle>
        </DialogContent>
      )}
      {props.content && (
        <DialogContent dividers>
          <Box p={2}>{props.content}</Box>
        </DialogContent>
      )}

      <DialogActions>{props.actions}</DialogActions>
    </Dialog>
  );
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: "100%",
      maxWidth: 360,
      backgroundColor: theme.palette.background.paper,
    },
    paper: {
      width: "80%",
      maxHeight: 435,
    },
  })
);

export type DialogProps = {
  builder: (setOpen: (value: boolean) => void) => React.ReactNode;
  actions?: (close: () => void) => React.ReactNode[];
  content?: React.ReactNode;
  title?: string;
};

export default function ConfirmationDialog(props: DialogProps) {
  const classes = useStyles();
  const [open, setOpen] = React.useState(false);
  const [value, setValue] = React.useState("Dione");

  const handleClose = (newValue?: string) => {
    setOpen(false);

    if (newValue) {
      setValue(newValue);
    }
  };

  return (
    <div>
      {props.builder(setOpen)}
      <div className={classes.root}>
        <ConfirmationDialogRaw
          classes={{
            paper: classes.paper,
          }}
          id="ringtone-menu"
          keepMounted
          title={props.title}
          open={open}
          content={props.content}
          actions={
            props.actions ? props.actions(() => setOpen(false)) : undefined
          }
          onClose={handleClose}
          value={value}
        />
      </div>
    </div>
  );
}
