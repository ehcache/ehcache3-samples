package org.ehcache.sample;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;

import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManager;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;
import static org.ehcache.config.units.MemoryUnit.MB;
import static org.slf4j.LoggerFactory.getLogger;

public class Basic {
  private static final Logger LOGGER = getLogger(Basic.class);

  public static void main(String[] args) {
    {
      LOGGER.info("Creating cache manager programmatically");
      try (CacheManager cacheManager = newCacheManagerBuilder()
              .withCache("basicCache",
                      newCacheConfigurationBuilder(Long.class,
                              String.class,
                              heap(100).offheap(1, MB)))
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
      try (CacheManager cacheManager = newCacheManager(xmlConfig)) {
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
