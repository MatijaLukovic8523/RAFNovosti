package com.raf.novosti;

import com.raf.novosti.filter.CORSFilter;
import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages("com.raf.novosti.controller", "com.raf.novosti.filter");
        register(new ApplicationBinder());
        register(CORSFilter.class);
    }
}