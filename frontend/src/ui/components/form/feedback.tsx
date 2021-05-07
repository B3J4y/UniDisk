import { Snackbar } from '@material-ui/core';
import MuiAlert from '@material-ui/lab/Alert';
import React from 'react';
import './feedback.css';

export function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

export function useSuccessAlert() {
  const [open, setOpen] = React.useState(false);

  const showSuccess = () => {
    setOpen(true);
  };

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }

    setOpen(false);
  };

  return {
    open,
    setOpen,
    showSuccess,
    handleClose,
  };
}

export type SuccessAlertProps = {
  open: boolean;
  message: string;
  handleClose: (e: unknown, reason: any) => void;
};

export function SuccessAlert(props: SuccessAlertProps) {
  const { message, open, handleClose } = props;
  return (
    <Snackbar open={open} autoHideDuration={3000} onClose={handleClose}>
      <Alert onClose={handleClose} severity="success">
        {message}
      </Alert>
    </Snackbar>
  );
}

export function FormInputError(props: { message: string }) {
  return (
    <div className="NoElevation">
      <Alert variant="outlined" severity="error">
        {props.message}
      </Alert>
    </div>
  );
}

export function FormActionError(props: { message: string }) {
  return (
    <div className="NoElevation">
      <Alert variant="filled" severity="error">
        {props.message}
      </Alert>
    </div>
  );
}
