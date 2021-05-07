import { Box } from '@material-ui/core';
import React from 'react';

export function Center(props: { child: JSX.Element }): JSX.Element {
  return (
    <Box display="flex" alignItems="center" justifyContent="center">
      {props.child}
    </Box>
  );
}
