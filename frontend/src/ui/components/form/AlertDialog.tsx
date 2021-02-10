import React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import { CircularProgress } from '@material-ui/core';

export type AlertDialogProps = {
  title: string;
  builder: (setOpen: (open: boolean) => void) => JSX.Element;
  positiveAction?: string;
  content?: JSX.Element;
  contentText?: string;
  action: () => Promise<{ success: boolean; error?: string }>;
};
export default function AlertDialog(props: AlertDialogProps) {
  const { builder, positiveAction, content, contentText, title, action } = props;
  const [open, setOpen] = React.useState(false);
  const [isLoading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<string | undefined>(undefined);
  const handleClose = () => {
    setOpen(false);
  };

  return (
    <div>
      {builder(setOpen)}
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{title}</DialogTitle>
        <DialogContent>
          {contentText && (
            <DialogContentText id="alert-dialog-description">{contentText}</DialogContentText>
          )}
          {content && content}
          {error && <p style={{ color: 'red' }}>{error}</p>}
        </DialogContent>
        <DialogActions>
          {!isLoading && (
            <Button onClick={handleClose} color="primary">
              Abbrechen
            </Button>
          )}
          {!isLoading && (
            <Button
              onClick={async () => {
                setLoading(true);
                setError(undefined);
                try {
                  const result = await action();
                  if (result.success) handleClose();
                  else setError(result.error);
                } catch (e) {
                  console.error(e);
                } finally {
                  setLoading(false);
                }
              }}
              color="primary"
              autoFocus
            >
              {positiveAction ?? 'Bestätigen'}
            </Button>
          )}
          {isLoading && <CircularProgress />}
        </DialogActions>
      </Dialog>
    </div>
  );
}
