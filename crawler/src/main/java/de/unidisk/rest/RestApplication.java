package de.unidisk.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {
    public RestApplication() {
        register(new InjectionBinder());
        packages(true, "de.unidisk.rest");
    }
}