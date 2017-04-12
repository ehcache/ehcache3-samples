package org.terracotta.demo.config.jcache;

import org.hibernate.cache.spi.CacheDataDescription;

import java.util.Properties;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;

/**
 * Extends the default {@code JCacheRegionFactory} but makes sure all caches are already existing to prevent
 * spontaneous creation of badly configured caches.
 */
public class JCacheRegionFactory extends org.hibernate.cache.jcache.JCacheRegionFactory {

    @Override
    protected Cache<Object, Object> createCache(String regionName, Properties properties, CacheDataDescription metadata) {
        throw new IllegalArgumentException("Unknown hibernate cache: " + regionName);
    }

}
