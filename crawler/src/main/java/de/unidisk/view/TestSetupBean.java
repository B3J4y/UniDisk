package de.unidisk.view;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.MockData;
import de.unidisk.entities.hibernate.HibernateTestSetup;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;

/**
 * Klasse initialisiert die Datenbank mit Testdaten.
 * Dies wird beim Start der Anwendung ausgeführt.
 */

@ManagedBean(eager=true)
@ApplicationScoped
public class TestSetupBean {

    @PostConstruct
    public void init() {
        //Folgenden Teil auskommentieren, falls keine Testdaten angelegt werden sollen.
        final ApplicationState state = MockData.getMockState();
        HibernateTestSetup.Setup(state);
    }

}