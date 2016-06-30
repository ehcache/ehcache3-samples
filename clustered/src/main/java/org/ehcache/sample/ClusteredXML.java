package org.ehcache.sample;

import java.net.URL;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusteredXML {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClusteredXML.class);

  public static void main(String[] args) {
    LOGGER.info("Creating clustered cache manager from XML");
    final URL myUrl = ClusteredXML.class.getResource("/ehcache.xml");
    Configuration xmlConfig = new XmlConfiguration(myUrl);
    CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
    
    cacheManager.init();

    Cache<Long, String> basicCache = cacheManager.getCache("basicCache", Long.class,
        String.class);

    LOGGER.info("Getting from cache");
    String value = basicCache.get(1L);
    LOGGER.info("Retrieved '{}'", value);

    LOGGER.info("Closing cache manager");
    cacheManager.close();

    LOGGER.info("Exiting");
  }
}
