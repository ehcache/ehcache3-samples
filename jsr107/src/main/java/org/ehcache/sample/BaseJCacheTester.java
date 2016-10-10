package org.ehcache.sample;

import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.config.ResourceType;
import org.ehcache.jsr107.Eh107Configuration;
import org.slf4j.Logger;

import javax.cache.Cache;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by fabien.sanglier on 10/8/16.
 */
public abstract class BaseJCacheTester {
  private static final Logger LOGGER = getLogger(BaseJCacheTester.class);

  protected <K, V> boolean simpleGetsAndPutsCacheTest(Cache<K, V> myJCache, int numberOfIteration, int numberOfObjectPerIteration, int sleepTimeMillisBetweenIterations, KeyValueGenerator<K, V> keyValueGenerator) throws InterruptedException {
    try {
      LOGGER.info("simpleGetsAndPutsCacheTest BEGIN");
      if (null != myJCache) {
        //config check to log
        inspectCacheConfig(myJCache);

        int iterationCount = 0;
        while (iterationCount < numberOfIteration) {
          LOGGER.info("simpleGetsAndPutsCacheTest -------------------------------------------------------------");
          LOGGER.info("simpleGetsAndPutsCacheTest - Iteration #{} of {}", iterationCount + 1, numberOfIteration);

          int hitCounts = 0, missCount = 0;
          long startTime = System.nanoTime();
          //iterate through numberOfObjects and use the iterator as the key, value does not matter at this time
          int opsCount = 0;
          for (opsCount = 0; opsCount < numberOfObjectPerIteration; opsCount++) {
            K key = keyValueGenerator.getKey(opsCount);
            V value;
            if (null == (value = myJCache.get(key))) {
              missCount++;
              LOGGER.debug("Key {} NOT in cache. Putting it...", key);
              myJCache.put(key, keyValueGenerator.getValue(opsCount));
            } else {
              hitCounts++;
              LOGGER.debug("Key {} IS in cache. Value = {}", key, value);
            }
          }
          long duration = System.nanoTime() - startTime;
          LOGGER.info("simpleGetsAndPutsCacheTest - Done Iteration #{} of {} in {} micros - total cache ops: {} hits: {} / misses: {}", iterationCount + 1, numberOfIteration, (duration / 1000), opsCount, hitCounts, missCount);

          iterationCount++;
          if (sleepTimeMillisBetweenIterations > 0) {
            LOGGER.info("Sleeping for {} millis...", sleepTimeMillisBetweenIterations);
            Thread.sleep(sleepTimeMillisBetweenIterations);
          }
        }
        LOGGER.info("simpleGetsAndPutsCacheTest - Successfully executed {} iteration.", iterationCount);
        return true;
      } else {
        LOGGER.error("simpleGetsAndPutsCacheTest - Cache object is null...doing nothing...");
        return false;
      }
    } finally {
      LOGGER.info("simpleGetsAndPutsCacheTest DONE");
    }
  }

  private <K, V> void inspectCacheConfig(Cache<K, V> myJCache) {
    //get the configuration to print the size on heap
    CacheRuntimeConfiguration<K, V> ehcacheConfig = (CacheRuntimeConfiguration<K, V>) myJCache
          .getConfiguration(Eh107Configuration.class)
          .unwrap(CacheRuntimeConfiguration.class);
    long heapSize = ehcacheConfig.getResourcePools().getPoolForResource(ResourceType.Core.HEAP).getSize();
    LOGGER.info(ehcacheConfig.toString());
    LOGGER.info("Cache testing - Cache {} with heap capacity = {}", myJCache.getName(), heapSize);
  }
}
