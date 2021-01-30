import MaterialTable, { MaterialTableProps } from 'material-table';
import React from 'react';

export type CreateActionProps<T> = {
  tooltip?: string;
  createNew: () => void;
};
export type TableProps<T extends object> = {
  entityname: string;
  title: string;
  onRowClick: (value: T) => void;
  editRow?: (value: T) => void;
  create?: CreateActionProps<T>;
  deleteRow?: (value: T) => void;
  data: T[];
  columns: MaterialTableProps<T>['columns'];
};

export function LocalizedTable<T extends object>(props: TableProps<T>) {
  const { entityname, title, onRowClick, data, columns, editRow, create, deleteRow } = props;

  const actions: {
    icon: string;
    tooltip?: string;
    isFreeAction?: boolean;
    onClick: (e: any, data: T | T[]) => void;
  }[] = [];
  if (editRow) {
    actions.push({
      icon: 'create',
      tooltip: `${entityname} bearbeiten`,
      onClick: (event: any, data: T | T[]) => {
        if (isSingleEntity(data)) editRow(data);
      },
    });
  }

  const isSingleEntity = (data: T | T[]): data is T => {
    return (data as any).length === undefined;
  };

  if (deleteRow) {
    actions.push({
      icon: 'delete',
      tooltip: 'Gruppe löschen',
      onClick: (event: any, rowData: T | T[]) => {
        if (isSingleEntity(rowData)) deleteRow(rowData);
      },
    });
  }

  if (create) {
    actions.push({
      icon: 'add',
      tooltip: create.tooltip,
      isFreeAction: true,
      onClick: () => {
        create.createNew();
      },
    });
  }

  return (
    <MaterialTable
      localization={{
        body: {
          emptyDataSourceMessage: `Keine ${entityname} vorhanden`,
          editRow: {
            deleteText: 'Soll dieses Gruppe wirklich gelöscht werden?',
            cancelTooltip: 'Abbrechen',
            saveTooltip: 'Speichern',
          },
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
      }}
      options={{
        actionsColumnIndex: -1,
        pageSize: 10,
      }}
      onRowClick={(e, entity?: T) => {
        if (entity) onRowClick(entity);
      }}
      title={title}
      actions={actions}
      columns={columns}
      data={data}
    />
  );
}
