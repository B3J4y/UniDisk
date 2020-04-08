package de.unidisk.common;


import de.unidisk.entities.hibernate.ProjectState;

public final class ProjectStateMapper {
    public static String mapToFriendlyName(ProjectState state){
        switch(state){
            case IDLE:
                return "Vorbereitung";
            case ERROR:
                return "Fehler";
            case WAITING:
                return "Bereit";
            case FINISHED:
                return "Abgeschlossen";

            default:
                return "In Bearbeitung";
        }
    }

    /**
     * Gibt Icon Url des Status zurück.
     * @param state
     * @return Pfad des Icons im resources/images Ordner
     */
    public static String getStateIconUrl(ProjectState state){
        switch(state){
            case IDLE:
                return "state_idle.png";
            case ERROR:
                return "state_error.png";
            case RUNNING:
                return "state_running.png";
            case WAITING:
                return "state_waiting.png";
            default:
                return "state_finished.png";
        }
    }

}
