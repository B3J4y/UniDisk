import { Project } from 'data/entity';
import { useProvider } from 'Provider';
import React from 'react';
import AlertDialog from '../form/AlertDialog';

export type DequeueProjectDialogProps = {
  children: (show: () => void) => React.ReactNode[] | React.ReactNode;
  project: Project;
};
export function DequeueProjectDialog(props: DequeueProjectDialogProps) {
  const provider = useProvider();
  const { project, children } = props;

  return (
    <AlertDialog
      title="Freigabe aufheben"
      contentText={`Soll die Projektfreigabe aufgehoben werden? Themen und Stichworte können anschließend wieder bearbeitet werden.`}
      positiveAction="Aufheben"
      action={async () => {
        const container = provider.getProjectDetailContainer();
        container.setEntity({ ...project, topics: [] });
        await container.dequeue();
        const { state } = container;

        return {
          success: state.dequeue.isFinished,
          error: state.dequeue.hasError ? 'Freigabe konnte nicht aufgehoben werden.' : undefined,
        };
      }}
      builder={(setOpen) => {
        return <>{children(() => setOpen(true))}</>;
      }}
    />
  );
}
