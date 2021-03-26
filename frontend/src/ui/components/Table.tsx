import MaterialTable, { MaterialTableProps } from 'material-table';
import React from 'react';

export function LocalizedTable<T extends object>(props: MaterialTableProps<T>) {
  return (
    <MaterialTable
      {...props}
      localization={{
        body: {
          emptyDataSourceMessage: `Keine Ergebnisse vorhanden`,
        },
        toolbar: {
          searchPlaceholder: 'Suche',
          searchTooltip: 'Suche',
        },
        pagination: {
          labelRowsPerPage: 'Zeilen pro Seite:',
          labelRowsSelect: 'Zeilen',
          labelDisplayedRows: '{from}-{to} von {count}',
          nextAriaLabel: 'Nächste Seite',
        },
        header: {
          actions: 'Aktionen',
        },
        ...props.localization,
      }}
    />
  );
}
