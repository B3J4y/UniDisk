package de.unidisk;

import de.unidisk.entities.hibernate.Project;

public interface IKeyword {
    void deleteKeyword(String keyword, Project project);
}
