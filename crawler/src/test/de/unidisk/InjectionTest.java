package de.unidisk;

import de.unidisk.config.InjectionBinder;
import de.unidisk.rest.LegacyRequestData;
import de.unidisk.rest.LegacyService;
import de.unidisk.rest.LoadPurpose;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;

public class InjectionTest {

    @Inject
    private LegacyService legacyService;

    @Before
    public void setUp() throws IOException, SQLException {


        final ServiceLocator locator = ServiceLocatorUtilities.bind(new InjectionBinder());
        //final ServiceLocator locator = ServiceLocatorUtilities.bind(new TestGFApplicationBinder());
        locator.inject(this);

    }

    @Test
    public void testLegacyRestInteface() {
        legacyService.getDBData(new LegacyRequestData(LoadPurpose.overview));
    }
}
