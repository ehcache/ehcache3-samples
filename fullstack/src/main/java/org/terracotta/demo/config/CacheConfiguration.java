package org.terracotta.demo.config;

import org.ehcache.clustered.client.config.ClusteredStoreConfiguration;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ServerSideConfigurationBuilder;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
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
import org.springframework.core.env.Environment;
import org.terracotta.demo.domain.Actor;
import org.terracotta.demo.domain.Authority;
import org.terracotta.demo.domain.PersistentAuditEvent;
import org.terracotta.demo.domain.PersistentToken;
import org.terracotta.demo.domain.User;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.inject.Inject;

@SuppressWarnings("unused")
@Configuration
@EnableCaching
@AutoConfigureBefore(value = { MetricsConfiguration.class, DatabaseConfiguration.class })
public class CacheConfiguration extends CachingConfigurerSupport {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @Inject
    private Environment env;

    @Inject
    private JHipsterProperties properties;

    private CacheManager cacheManager;

    @Bean
    @Override
    public org.springframework.cache.CacheManager cacheManager() {

        cacheManager = env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION) ? createClusteredCacheManager() : createInMemoryCacheManager();

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

    private CacheManager createInMemoryCacheManager() {
        long cacheSize = properties.getCache().getEhcache().getSize();
        long ttl = properties.getCache().getEhcache().getTimeToLiveSeconds();

        org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder
                .newResourcePoolsBuilder()
                .heap(cacheSize))
            .withExpiry(Expirations.timeToLiveExpiration(new org.ehcache.expiry.Duration(ttl, TimeUnit.SECONDS)))
            .build();

        Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = createCacheConfigurations(cacheConfiguration);

        EhcacheCachingProvider provider = getCachingProvider();
        DefaultConfiguration configuration = new DefaultConfiguration(caches, provider.getDefaultClassLoader());
        return provider.getCacheManager(provider.getDefaultURI(), configuration);
    }

    private CacheManager createClusteredCacheManager() {
        JHipsterProperties.Cache.Ehcache.Cluster clusterProperties = properties.getCache().getEhcache().getCluster();
        URI clusterUri = clusterProperties.getUri();
        boolean autoCreate = clusterProperties.isAutoCreate();
        long clusteredCacheSize = clusterProperties.getSizeInMb();
        Consistency consistency = clusterProperties.getConsistency();

        long heapCacheSize = properties.getCache().getEhcache().getSize();
        long ttl = properties.getCache().getEhcache().getTimeToLiveSeconds();

        ClusteringServiceConfigurationBuilder clusteringServiceConfigurationBuilder = ClusteringServiceConfigurationBuilder.cluster(clusterUri);
        ServerSideConfigurationBuilder serverSideConfigurationBuilder = (autoCreate ? clusteringServiceConfigurationBuilder.autoCreate() : clusteringServiceConfigurationBuilder.expecting())
            .defaultServerResource("primary-server-resource");

        org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder
                .newResourcePoolsBuilder()
                .heap(heapCacheSize)
                .with(ClusteredResourcePoolBuilder.clusteredDedicated(clusteredCacheSize, MemoryUnit.MB)))
            .withExpiry(Expirations.timeToLiveExpiration(new org.ehcache.expiry.Duration(ttl, TimeUnit.SECONDS)))
            .add(new ClusteredStoreConfiguration(consistency)).build();

        Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = createCacheConfigurations(cacheConfiguration);

        EhcacheCachingProvider provider = getCachingProvider();
        DefaultConfiguration configuration = new DefaultConfiguration(caches, provider.getDefaultClassLoader(), serverSideConfigurationBuilder.build());
        return provider.getCacheManager(provider.getDefaultURI(), configuration);
    }

    private Map<String, org.ehcache.config.CacheConfiguration<?, ?>> createCacheConfigurations(org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration) {
        Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = new HashMap<>();
        caches.put(User.class.getName(), cacheConfiguration);
        caches.put(Authority.class.getName(), cacheConfiguration);
        caches.put(User.class.getName() + ".authorities", cacheConfiguration);
        caches.put(PersistentToken.class.getName(), cacheConfiguration);
        caches.put(User.class.getName() + ".persistentTokens", cacheConfiguration);
        caches.put(PersistentAuditEvent.class.getName(), cacheConfiguration);
        caches.put(Actor.class.getName(), cacheConfiguration);
        caches.put("weatherReports", cacheConfiguration);
        return caches;
    }

    private EhcacheCachingProvider getCachingProvider() {
        return (EhcacheCachingProvider) Caching.getCachingProvider();
    }
}
