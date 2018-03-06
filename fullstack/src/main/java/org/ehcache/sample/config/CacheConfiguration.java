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
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.cache.CacheManager;
import javax.cache.Caching;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class })
@AutoConfigureBefore(value = { WebConfigurer.class, DatabaseConfiguration.class })
public class CacheConfiguration extends CachingConfigurerSupport {

    private final Environment environment;
    private final JHipsterProperties jHipsterProperties;
    private final ApplicationProperties applicationProperties;

    public CacheConfiguration(Environment environment, JHipsterProperties jHipsterProperties, ApplicationProperties applicationProperties) {
        this.environment = environment;
        this.jHipsterProperties = jHipsterProperties;
        this.applicationProperties = applicationProperties;
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
                .newResourcePoolsBuilder()
                .heap(cacheSize))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ttl)))
            .build();

        Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = createCacheConfigurations(cacheConfiguration);

        EhcacheCachingProvider provider = getCachingProvider();
        DefaultConfiguration configuration = new DefaultConfiguration(caches, provider.getDefaultClassLoader());
        return provider.getCacheManager(provider.getDefaultURI(), configuration);
    }

    private CacheManager createClusteredCacheManager() {
        ApplicationProperties.Cluster clusterProperties = applicationProperties.getCluster();
        URI clusterUri = clusterProperties.getUri();
        boolean autoCreate = clusterProperties.isAutoCreate();
        long clusteredCacheSize = clusterProperties.getSizeInMb();
        Consistency consistency = clusterProperties.getConsistency();

        long heapCacheSize = jHipsterProperties.getCache().getEhcache().getMaxEntries();
        long ttl = jHipsterProperties.getCache().getEhcache().getTimeToLiveSeconds();

        ClusteringServiceConfigurationBuilder clusteringServiceConfigurationBuilder = ClusteringServiceConfigurationBuilder.cluster(clusterUri);
        ServerSideConfigurationBuilder serverSideConfigurationBuilder = (autoCreate ? clusteringServiceConfigurationBuilder.autoCreate() : clusteringServiceConfigurationBuilder.expecting())
            .defaultServerResource("primary-server-resource");

        org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder
                .newResourcePoolsBuilder()
                .heap(heapCacheSize)
                .with(ClusteredResourcePoolBuilder.clusteredDedicated(clusteredCacheSize, MemoryUnit.MB)))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ttl)))
            .add(new ClusteredStoreConfiguration(consistency)).build();

        Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = createCacheConfigurations(cacheConfiguration);

        EhcacheCachingProvider provider = getCachingProvider();
        DefaultConfiguration configuration = new DefaultConfiguration(caches, provider.getDefaultClassLoader(), serverSideConfigurationBuilder.build());
        return provider.getCacheManager(provider.getDefaultURI(), configuration);
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
