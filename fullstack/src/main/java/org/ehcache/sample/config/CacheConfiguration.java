package org.ehcache.sample.config;

import io.github.jhipster.config.JHipsterConstants;
import io.github.jhipster.config.JHipsterProperties;
import org.ehcache.clustered.client.config.ClusteredStoreConfiguration;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ServerSideConfigurationBuilder;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import io.github.jhipster.config.jcache.BeanClassLoaderAwareJCacheRegionFactory;

import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.cache.CacheManager;
import javax.cache.Caching;

@Configuration
@EnableCaching
public class CacheConfiguration extends CachingConfigurerSupport {

    private final Environment environment;
    private final JHipsterProperties jHipsterProperties;
    private final ApplicationProperties applicationProperties;

    public CacheConfiguration(Environment environment, JHipsterProperties jHipsterProperties, ApplicationProperties applicationProperties) {
        BeanClassLoaderAwareJCacheRegionFactory.setBeanClassLoader(getClassLoader());
        this.environment = environment;
        this.jHipsterProperties = jHipsterProperties;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Return the class loader to use to retrieve the CacheManager from the Caching Provider
     *
     * @return class loader to use
     */
    private ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    private CacheManager getCacheManager(EhcacheCachingProvider provider, DefaultConfiguration configuration) {
        return provider.getCacheManager(provider.getDefaultURI(), configuration);
    }

    @Bean
    @Override
    public org.springframework.cache.CacheManager cacheManager() {
        return new JCacheCacheManager(environment.acceptsProfiles(JHipsterConstants.SPRING_PROFILE_PRODUCTION) ?
            createClusteredCacheManager() : createInMemoryCacheManager());
    }

    private CacheManager createInMemoryCacheManager() {
        long cacheSize = jHipsterProperties.getCache().getEhcache().getMaxEntries();
        long ttl = jHipsterProperties.getCache().getEhcache().getTimeToLiveSeconds();

        org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder
                .heap(cacheSize))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ttl)))
            .build();

        Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = createCacheConfigurations(cacheConfiguration);

        EhcacheCachingProvider provider = getCachingProvider();
        DefaultConfiguration configuration = new DefaultConfiguration(caches, getClassLoader());
        return getCacheManager(provider, configuration);
    }

    private CacheManager createClusteredCacheManager() {
        ApplicationProperties.Cluster clusterProperties = applicationProperties.getCluster();
        URI clusterUri = clusterProperties.getUri();
        boolean autoCreate = clusterProperties.isAutoCreate();
        long clusteredCacheSize = clusterProperties.getSizeInMb();
        String offheapResourceName = clusterProperties.getOffheapResourceName();
        Consistency consistency = clusterProperties.getConsistency();

        long heapCacheSize = jHipsterProperties.getCache().getEhcache().getMaxEntries();
        long ttl = jHipsterProperties.getCache().getEhcache().getTimeToLiveSeconds();

        ClusteringServiceConfigurationBuilder clusteringServiceConfigurationBuilder = ClusteringServiceConfigurationBuilder.cluster(clusterUri);
        ServerSideConfigurationBuilder serverSideConfigurationBuilder = (autoCreate ? clusteringServiceConfigurationBuilder.autoCreate() : clusteringServiceConfigurationBuilder.expecting())
            .defaultServerResource(offheapResourceName);

        org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder
                .heap(heapCacheSize)
                .with(ClusteredResourcePoolBuilder.clusteredDedicated(clusteredCacheSize, MemoryUnit.MB)))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ttl)))
            .add(new ClusteredStoreConfiguration(consistency)).build();

        Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = createCacheConfigurations(cacheConfiguration);

        EhcacheCachingProvider provider = getCachingProvider();
        DefaultConfiguration configuration = new DefaultConfiguration(caches, getClassLoader(), serverSideConfigurationBuilder.build());
        return getCacheManager(provider, configuration);
    }

    private Map<String, org.ehcache.config.CacheConfiguration<?, ?>> createCacheConfigurations(org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration) {
        Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = new HashMap<>();
        caches.put(org.ehcache.sample.repository.UserRepository.USERS_BY_LOGIN_CACHE, cacheConfiguration);
        caches.put(org.ehcache.sample.repository.UserRepository.USERS_BY_EMAIL_CACHE, cacheConfiguration);
        caches.put(org.ehcache.sample.domain.User.class.getName(), cacheConfiguration);
        caches.put(org.ehcache.sample.domain.Authority.class.getName(), cacheConfiguration);
        caches.put(org.ehcache.sample.domain.User.class.getName() + ".authorities", cacheConfiguration);
        caches.put(org.ehcache.sample.domain.Actor.class.getName(), cacheConfiguration);
        caches.put("weatherReports", cacheConfiguration);
        // jhipster-needle-ehcache-add-entry
        return caches;
    }

    private EhcacheCachingProvider getCachingProvider() {
        return (EhcacheCachingProvider) Caching.getCachingProvider();
    }
}
