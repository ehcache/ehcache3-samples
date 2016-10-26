package org.terracotta.demo.config.jcache;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.jcache.JCacheRegionFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Henri Tremblay
 */
public class SpringCacheRegionFactory extends JCacheRegionFactory {

    @Override
    public void start(SessionFactoryOptions options, Properties properties) throws CacheException {
        // Translate the Spring URI to a real URI
        String uri = properties.getProperty(CONFIG_URI);
        Resource resource = new DefaultResourceLoader().getResource(uri);
        try {
            properties.setProperty(CONFIG_URI, resource.getURI().toString());
        }
        catch(IOException e) {
            throw new CacheException(e);
        }
        super.start(options, properties);
    }
}
