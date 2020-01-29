package de.unidisk.config;


import org.glassfish.jersey.server.ResourceConfig;

public class UnidiskResourceConfig extends ResourceConfig {

    public UnidiskResourceConfig() {
        register(new InjectionBinder());
        packages("de.unidisk");
    }
}
