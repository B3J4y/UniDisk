package de.unidisk.entities.templates;

import org.junit.Test;

public interface CRUDTest {

    @Test
    void canCreateEntity();
    @Test
    void creatingDuplicateEntityThrowsError();
    @Test
    void canUpdateEntity();

    @Test
    void canDeleteEntity();


    @Test
    void findEntityReturnsData();

    @Test
    void findEntityReturnsNullIfMissing();

}
