package de.unidisk.entities;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.MockData;
import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import de.unidisk.entities.util.TestFactory;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HibernateSetupTest implements HibernateLifecycle{

    @Test
    void canCreateSetupDataState(){
        //crashes if hibernate uses in memory database
        final ApplicationState state = MockData.getMockState();
        HibernateTestSetup.Setup(state);
    }

}
