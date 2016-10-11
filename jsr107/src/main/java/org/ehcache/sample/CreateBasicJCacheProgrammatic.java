package org.ehcache.sample;

import org.slf4j.Logger;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by fabien.sanglier on 10/6/16.
 */
public class CreateBasicJCacheProgrammatic extends BaseJCacheTester {
  private static final Logger LOGGER = getLogger(CreateBasicJCacheProgrammatic.class);

  private static String CACHE_NAME = "myJCache";

  public static void main(String[] args) throws Exception {
    // pass in the number of object you want to generate, default is 100
    int numberOfObjects = Integer.parseInt(args.length == 0 ? "5000" : args[0]);
    int numberOfIteration = Integer.parseInt(args.length == 0 ? "5" : args[1]);
    int sleepTimeMillisBetweenIterations = Integer.parseInt(args.length == 0 ? "1000" : args[2]);

    new CreateBasicJCacheProgrammatic().run(numberOfIteration, numberOfObjects, sleepTimeMillisBetweenIterations);

    LOGGER.info("Exiting");
  }

  public void run(int numberOfIteration, int numberOfObjectPerIteration, int sleepTimeMillisBetweenIterations) throws Exception {
    LOGGER.info("JCache testing BEGIN - Creating JCache Programmatically without any XML config");

    //finds ehcache provider automatically if it is in the classpath
    CachingProvider cachingProvider = Caching.getCachingProvider();

    // If there are multiple providers in your classpath, use the fully qualified name to retrieve the Ehcache caching provider.
    //CachingProvider cachingProvider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");

    try (CacheManager cacheManager = cachingProvider.getCacheManager()) {
      Cache<Long, String> myJCache = cacheManager.createCache(
        CACHE_NAME,
        new MutableConfiguration<Long, String>()
          .setTypes(Long.class, String.class)
          .setStoreByValue(false)
          .setStatisticsEnabled(true)
          .setExpiryPolicyFactory(FactoryBuilder.factoryOf(new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, 5)))));

      simpleGetsAndPutsCacheTest(myJCache, numberOfIteration, numberOfObjectPerIteration, sleepTimeMillisBetweenIterations, new KeyValueGenerator<Long, String>() {
        @Override
        public Long getKey(Number k) {
          return new Long(k.longValue());
        }

        @Override
        public String getValue(Number v) {
          return String.format("Da One %s!!", v.toString());
        }
      });
    }
    LOGGER.info("JCache testing DONE - Creating JCache Programmatically without any XML config");
  }
}
