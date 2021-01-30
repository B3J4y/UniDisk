import { Grid } from '@material-ui/core';
import { GridSpacing } from '@material-ui/core/Grid';
import React from 'react';

export type ColumnProps = {
  children: React.ReactNode[];
  spacing?: GridSpacing;
};
export function Column(props: ColumnProps) {
  const spacing = props.spacing ?? 2;
  return (
    <Grid container item xs={12} spacing={spacing}>
      {props.children.map((child) => {
        return (
          <Grid item xs={12}>
            {child}
          </Grid>
        );
      })}
    </Grid>
  );
}
