import { Box, Button, CircularProgress, Grid, IconButton, Paper, Toolbar } from '@material-ui/core';
import React from 'react';
import { FormActionError, SuccessAlert, useSuccessAlert } from './feedback';
import DeleteIcon from '@material-ui/icons/Delete';
import AlertDialog from './AlertDialog';

export type DeleteProps = {
  title: string;
  body: string;
  action: () => Promise<{ success: boolean; error?: string }>;
};

export type DetailFormProps = {
  formTitle: string;
  delete?: DeleteProps;
  actionName: string;
  canEdit: boolean;
  builder: (args: DetailFormBuildArgs) => JSX.Element;
  submit: (args: DetailFormSubmitArgs) => void;
};

export type DetailFormSubmitArgs = {
  setLoading: (loading: boolean) => void;
  setError: (error?: string) => void;
  showSuccess: (message: string) => void;
};

export type DetailFormBuildArgs = {};
export function DetailFormBuilder(props: DetailFormProps) {
  const { formTitle, delete: deleteProps, actionName, canEdit } = props;
  const [isLoading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<string | undefined>(undefined);
  const [successMessage, setSuccessMesage] = React.useState('');
  const successAlertProps = useSuccessAlert();

  const canDelete = deleteProps !== undefined;

  const canSubmit = canEdit && !isLoading;

  const onSubmit = (e) => {
    e.preventDefault();
    props.submit({
      setError,
      setLoading,
      showSuccess: (val) => {
        setSuccessMesage(val);
        successAlertProps.showSuccess();
      },
    });
  };

  const builderArgs: DetailFormBuildArgs = {
    showSuccess: (message) => {
      setSuccessMesage(message);
    },
  };
  return (
    <Grid spacing={3} item xs={12} sm={12} md={11} lg={7}>
      <Grid item xs={12}>
        <Paper>
          <Toolbar>
            <Grid item container xs={12} justify="space-between" alignItems="center">
              <h1>{formTitle}</h1>
              {canDelete && (
                <AlertDialog
                  title={deleteProps!.title}
                  positiveAction="Löschen"
                  contentText={deleteProps!.body}
                  action={async () => {
                    const { success, error } = await deleteProps!.action();

                    return { success };
                  }}
                  builder={(setOpen) => {
                    return (
                      <IconButton
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
              )}
            </Grid>
          </Toolbar>

          <form noValidate autoComplete="off" onSubmit={onSubmit}>
            <Box p={2}>
              <Grid container spacing={2}>
                {props.builder(builderArgs)}
              </Grid>

              {error && (
                <Grid item xs={12}>
                  <FormActionError message={error} />
                </Grid>
              )}

              <Box mt={2}>
                <Button variant="contained" color="primary" type="submit" disabled={!canSubmit}>
                  {isLoading ? (
                    <CircularProgress style={{ color: 'white' }} size={25} />
                  ) : (
                    actionName
                  )}
                </Button>
              </Box>
            </Box>
          </form>
        </Paper>
      </Grid>
      <SuccessAlert {...successAlertProps} message={successMessage} />
    </Grid>
  );
}
