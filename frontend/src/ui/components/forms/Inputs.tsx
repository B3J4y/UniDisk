import { Box, Paper, TextField, TextFieldProps } from '@material-ui/core';
import Image from 'material-ui-image';
import React from 'react';
import Dropzone, { DropzoneRef, DropzoneProps } from 'react-dropzone';
import moment from 'moment';
import Button, { ButtonProps } from '@material-ui/core/Button';
import { CircularProgress } from '@material-ui/core';

export type DateFieldProps = TextFieldProps & {
  value: Date;
  onValueChange: (val: Date) => void;
};

export function DateField(props: DateFieldProps) {
  return (
    <TextField
      id={props.id}
      label={props.label}
      error={props.error ?? false}
      required={props.required ?? false}
      variant="outlined"
      helperText={props.helperText}
      type="date"
      onChange={(e) => {
        const val = e.target.value;
        const parts = val.split('-').map((v) => parseInt(v.trim()));
        const year = parts[0];
        const month = parts[1] - 1;
        const day = parts[2];
        if (val === '') {
          props.onValueChange(props.value);
          return;
        }
        const date =
          props.value === null
            ? moment(val)
            : moment(props.value).set({
                year: year,
                month: month,
                date: day,
              });

        props.onValueChange(date.toDate());
      }}
      value={moment(props.value).format('YYYY-MM-DD')}
      InputLabelProps={{
        shrink: true,
      }}
    />
  );
}

export type TimeFieldProps = {
  id: string;
  value: Date;
  onChange: (val: Date) => void;
  label: string;
  required?: boolean;
  error?: boolean;
  helperText?: string;
};

export function TimeField(props: TimeFieldProps) {
  return (
    <TextField
      id={props.id}
      error={props.error ?? false}
      required={props.required ?? false}
      label={props.label}
      helperText={props.helperText}
      variant="outlined"
      type="time"
      onChange={(e) => {
        const val = e.target.value;

        if (val === '') {
          props.onChange(props.value);
          return;
        }

        const parts = val.split(':').map((v) => parseInt(v.trim()));
        const date = moment(props.value).set({
          hour: parts[0],
          minute: parts[1],
        });

        props.onChange(date.toDate());
      }}
      value={moment(props.value).format('HH:mm')}
      InputLabelProps={{
        shrink: true,
      }}
    />
  );
}

type Props = {
  onChange?: (image: any) => void;
  imageUrl?: string | null;
  onRef?: (ref: DropzoneRef) => void;
};

export function PaperDropzone(props: Props & DropzoneProps & React.RefAttributes<DropzoneRef>) {
  const [imageSrc, setImage] = React.useState<string | null>(props.imageUrl ?? null);
  React.useEffect(() => {
    setImage(props.imageUrl ?? null);
  }, [props.imageUrl]);

  return (
    <Paper>
      {imageSrc !== null && <Image width="100%" aspectRatio={16 / 9} src={imageSrc} />}
      {imageSrc === null && (
        <Box p={2}>
          <Dropzone
            {...props}
            ref={(node) => {
              if (props.onRef && node !== null) props.onRef(node);
            }}
            multiple={false}
            accept=".jpg,.png,.jpeg"
            onDrop={(acceptedFiles) => {
              if (props.onChange) {
                props.onChange(acceptedFiles[0]);
              }
              if (acceptedFiles.length > 0) {
                const file = URL.createObjectURL(acceptedFiles[0]);
                setImage(file);
              }
            }}
          >
            {({ getRootProps, getInputProps }) => (
              <section style={{ cursor: 'default' }}>
                <div {...getRootProps()}>
                  <input {...getInputProps()} />
                  <p>Datei ablegen oder anklicken um Bild hochzuladen</p>
                </div>
              </section>
            )}
          </Dropzone>
        </Box>
      )}
    </Paper>
  );
}

export function ActionButton(
  props: {
    action: () => () => Promise<void>;
    value: string;
    loadingBuilder?: () => React.ReactNode;
  } & ButtonProps,
) {
  const [loading, setLoading] = React.useState<boolean>(false);
  const { loadingBuilder, action, value } = props;

  // if (loadingBuilder) return loadingBuilder();

  return (
    <Button
      onClick={async () => {
        if (loading) return;

        setLoading(true);
        const task = action();
        await task();
        setLoading(false);
      }}
      {...props}
    >
      {loading && <CircularProgress />}
      {!loading && value}
    </Button>
  );
}
