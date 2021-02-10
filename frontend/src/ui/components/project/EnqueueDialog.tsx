import { Project } from 'data/entity';
import { useProvider } from 'Provider';
import React from 'react';
import AlertDialog from '../form/AlertDialog';

export type EnqueueProjectDialogProps = {
  children: (show: () => void) => React.ReactNode[] | React.ReactNode;
  project: Project;
};
export function EnqueueProjectDialog(props: EnqueueProjectDialogProps) {
  const provider = useProvider();
  const { project, children } = props;

  return (
    <AlertDialog
      title="Projekt freigeben"
      contentText={`Nach der Projektfreigabe beginnt die Projektauswertung und Themen/Stichworte können nicht mehr bearbeiten werden.`}
      positiveAction="Freigeben"
      action={async () => {
        const container = provider.getProjectDetailContainer();
        container.setEntity({ ...project, topics: [] });
        await container.enqueue();
        const { state } = container;

        return {
          success: state.enqueue.isFinished,
          error: state.enqueue.hasError ? 'Projekt konnte nicht freigegeben werden.' : undefined,
        };
      }}
      builder={(setOpen) => {
        return <>{children(() => setOpen(true))}</>;
      }}
    />
  );
}
