package de.unidisk.common.exceptions;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(Class c, int entityId) {
        super("Entity of type " + c.getSimpleName() + " with id " + String.valueOf(entityId) + " could not be found.");
    }
}
