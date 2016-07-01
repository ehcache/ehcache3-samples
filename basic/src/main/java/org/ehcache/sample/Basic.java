package org.ehcache.sample;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Basic {
  private static final Logger LOGGER = LoggerFactory.getLogger(Basic.class);

  public static void main(String[] args) {
    {
      LOGGER.info("Creating cache manager programmatically");
      try (CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
              .withCache("basicCache",
                      CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class,
                              String.class,
                              ResourcePoolsBuilder.heap(100).offheap(1, MemoryUnit.MB)))
              .build(true)) {
        Cache<Long, String> basicCache = cacheManager.getCache("basicCache",
                Long.class, String.class);

        LOGGER.info("Putting to cache");
        basicCache.put(1L, "da one!");
        String value = basicCache.get(1L);
        LOGGER.info("Retrieved '{}'", value);

        LOGGER.info("Closing cache manager");
      }
    }

    LOGGER.info("---------------------------------------------------------------");
    
    {
      LOGGER.info("Creating cache manager via XML resource");
      Configuration xmlConfig = new XmlConfiguration(
          Basic.class.getResource("/ehcache.xml"));
      try (CacheManager cacheManager = CacheManagerBuilder
              .newCacheManager(xmlConfig)) {
        cacheManager.init();

        Cache<Long, String> basicCache = cacheManager.getCache("basicCache",
                Long.class, String.class);

        LOGGER.info("Putting to cache");
        basicCache.put(1L, "da one!");
        String value = basicCache.get(1L);
        LOGGER.info("Retrieved '{}'", value);

        LOGGER.info("Closing cache manager");
      }
    }

    LOGGER.info("Exiting");
  }
}
