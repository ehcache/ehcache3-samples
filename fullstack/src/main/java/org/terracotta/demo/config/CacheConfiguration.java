package org.terracotta.demo.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.terracotta.demo.domain.Actor;
import org.terracotta.demo.domain.Authority;
import org.terracotta.demo.domain.PersistentAuditEvent;
import org.terracotta.demo.domain.PersistentToken;
import org.terracotta.demo.domain.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.inject.Inject;

@SuppressWarnings("unused")
@Configuration
@EnableCaching
@AutoConfigureBefore(value = { MetricsConfiguration.class, DatabaseConfiguration.class })
public class CacheConfiguration extends CachingConfigurerSupport {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @Inject
    private JHipsterProperties properties;

    private CacheManager cacheManager;

    @Bean
    @Override
    public org.springframework.cache.CacheManager cacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        EhcacheCachingProvider ehcacheProvider = (EhcacheCachingProvider) cachingProvider;

        Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = new HashMap<>();
        caches.put(User.class.getName(), createCacheConfiguration());
        caches.put(Authority.class.getName(), createCacheConfiguration());
        caches.put(User.class.getName() + ".authorities", createCacheConfiguration());
        caches.put(PersistentToken.class.getName(), createCacheConfiguration());
        caches.put(User.class.getName() + ".persistentTokens", createCacheConfiguration());
        caches.put(PersistentAuditEvent.class.getName(), createCacheConfiguration());
        caches.put(Actor.class.getName(), createCacheConfiguration());
        caches.put("weatherReports", createCacheConfiguration());

        DefaultConfiguration configuration = new DefaultConfiguration(caches, ehcacheProvider.getDefaultClassLoader());

        cacheManager = ehcacheProvider.getCacheManager(ehcacheProvider.getDefaultURI(), configuration);

        return new JCacheCacheManager(cacheManager) {
            @Override
            protected org.springframework.cache.Cache getMissingCache(String name) {
                org.springframework.cache.Cache cache = super.getMissingCache(name);
                // All caches should be configured. So I throw an exception when one is not found
                if(cache == null) {
                    throw new IllegalArgumentException("Unknown cache: " + name);
                }
                return cache;
            }
        };
    }

    @PreDestroy
    public void destroy() {
        log.info("Close Cache Manager");
        cacheManager.close();
    }

    private CacheManager createCacheManager() {
        EhcacheCachingProvider provider = getCachingProvider();

        DefaultConfiguration configuration = new DefaultConfiguration(
            Collections.emptyMap(),
            provider.getDefaultClassLoader());

        return provider.getCacheManager(provider.getDefaultURI(), configuration);
    }

    private org.ehcache.config.CacheConfiguration<Object, Object> createCacheConfiguration() {
        long cacheSize = properties.getCache().getEhcache().getSize();
        long ttl = properties.getCache().getEhcache().getTimeToLiveSeconds();
        return CacheConfigurationBuilder
            .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(cacheSize))
            .withExpiry(Expirations.timeToLiveExpiration(new org.ehcache.expiry.Duration(ttl, TimeUnit.SECONDS)))
            .build();
    }

    private EhcacheCachingProvider getCachingProvider() {
        return (EhcacheCachingProvider) Caching.getCachingProvider();
    }
}
