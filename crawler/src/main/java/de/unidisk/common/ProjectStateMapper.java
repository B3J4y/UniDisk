package de.unidisk.common;


import de.unidisk.entities.hibernate.ProjectState;

public final class ProjectStateMapper {
    public static String mapToFriendlyName(ProjectState state){
        switch(state){
            case ERROR:
                return "Fehler aufgetreten";
            case WAITING:
                return "Nicht gestartet";
            case FINISHED:
                return "Erfolgreich beendet";

            default:
                return "Im Prozess";
        }
    }

    /**
     * Gibt Icon Url des Status zurück.
     * @param state
     * @return Pfad des Icons im resources/images Ordner
     */
    public static String getStateIconUrl(ProjectState state){
        switch(state){
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
