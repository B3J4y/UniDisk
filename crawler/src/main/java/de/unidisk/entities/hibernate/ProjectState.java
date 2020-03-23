package de.unidisk.entities.hibernate;

public enum ProjectState {
    IDLE,
    WAITING,
    RUNNING,
    FINISHED,
    ERROR;

    public boolean isIdle(){
        return IDLE.equals(this);
    }

    public boolean isWaiting(){
        return WAITING.equals(this);
    }
}

