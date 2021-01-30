import { TextField, TextFieldProps } from '@material-ui/core';
import moment from 'moment';
import React from 'react';

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
