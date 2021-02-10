import { ProjectState } from 'data/entity';

const stateMap: Record<ProjectState, string> = {
  [ProjectState.idle]: 'Vorbereitung',
  [ProjectState.completed]: 'Abgeschlossen',
  [ProjectState.error]: 'Fehlerhaft',
  [ProjectState.processing]: 'In Bearbeitung',
  [ProjectState.ready]: 'Warteschlange',
};

export function mapProjectState(state: ProjectState): string {
  return stateMap[state] ?? 'Unbekannt';
}
