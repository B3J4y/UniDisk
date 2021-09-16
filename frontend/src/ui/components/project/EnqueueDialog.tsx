import { Project } from 'data/entity';
import { useProvider } from 'Provider';
import React from 'react';
import AlertDialog from '../form/AlertDialog';

export type EnqueueProjectDialogProps = {
  children: (show: () => void) => React.ReactNode[] | React.ReactNode;
  project: Project;
};

function getProjectError(project: Project): string | undefined {
  const topics = project.topics ?? [];

  if (topics.length < 1) {
    return 'Das Projekt muss aus mindestens einem Thema bestehen.';
  }

  const everyTopicHasKeyword = topics.every((topic) => (topic.keywords ?? []).length > 0);

  if (!everyTopicHasKeyword) {
    return 'Jedes Thema muss mindestens ein Stichwort enthalten.';
  }

  return undefined;
}

export function EnqueueProjectDialog(props: EnqueueProjectDialogProps) {
  const provider = useProvider();
  const { project, children } = props;

  const error = getProjectError(project);

  return (
    <AlertDialog
      title="Projekt freigeben"
      contentText={
        error ??
        `Nach der Projektfreigabe beginnt die Projektauswertung und Themen/Stichworte können nicht mehr bearbeiten werden.`
      }
      positiveAction="Freigeben"
      hideAction={error !== undefined}
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
