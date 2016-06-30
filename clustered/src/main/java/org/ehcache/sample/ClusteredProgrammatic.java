package org.ehcache.sample;

import java.net.URI;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusteredProgrammatic {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClusteredProgrammatic.class);

  public static void main(String[] args) {
    LOGGER.info("Creating clustered cache manager");
    final URI uri = URI
        .create("terracotta://localhost:9510/clustered");
    CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(ClusteringServiceConfigurationBuilder.cluster(uri).autoCreate()
            .defaultServerResource("default-resource"))
        .withCache("basicCache",
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class,
                String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(100, EntryUnit.ENTRIES)
                    .offheap(1, MemoryUnit.MB)
                    .with(ClusteredResourcePoolBuilder
                        .clusteredDedicated("default-resource", 5, MemoryUnit.MB))))
        .build(true);

    Cache<Long, String> basicCache = cacheManager.getCache("basicCache", Long.class,
        String.class);

    LOGGER.info("Putting to cache");
    basicCache.put(1L, "da one!");

    LOGGER.info("Closing cache manager");
    cacheManager.close();

    LOGGER.info("Exiting");
  }
}
