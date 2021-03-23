package de.unidisk.entities.hibernate;

public enum ProjectSubtype {
    DEFAULT,
    // Only contains keywords that were created by user
    CUSTOM_ONLY,
    // Project which was automatically generated from topic names
    BY_TOPICS
}