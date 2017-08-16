package com.bob.cock.job.data.jdbc.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bob.cock.job.utils.IOUtils;

public final class ClasspathResource {
    
    private final Logger LOG = LoggerFactory.getLogger(ClasspathResource.class);

    private Properties p = new Properties();
    private final String resourcePath;

    public ClasspathResource(String resourcePath) {
        this.resourcePath = resourcePath;
        InputStream stream = null;
        try {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
            p.load(stream);
        } catch (IOException ioe) {
            LOG.error("Can not load resource, path = {}" , resourcePath);
        } finally {
            IOUtils.close(stream);
        }
    }

    public String getProperty(String name) {
        return p.getProperty(name);
    }

    public String resourcePath() {
        return resourcePath;
    }
}
