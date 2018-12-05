import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

/**
 * Show different caches. Some are correctly used, some requires attention. See the metrics to figure it out.
 */
public class CacheUsage {

  public static void main(String[] args) throws Exception {
    MutableConfiguration<Integer, String> configuration = new MutableConfiguration<>();
    configuration.setStatisticsEnabled(true);

    EhcacheCachingProvider provider = getCachingProvider();
    try(CacheManager cacheManager = provider.getCacheManager()) {
      // Just do not use it
      Cache<Integer, String> unused = cacheManager.createCache("unused", configuration);
      IntStream.range(0, 1000).forEach(i -> unused.put(i, "" + i));

      // Hit the empty cache
      Cache<Integer, String> alwaysmiss = cacheManager.createCache("alwaysmiss", configuration);
      IntStream.range(0, 1000).forEach(i -> alwaysmiss.get(i));

      // Put some stuff but misses most of the time
      Cache<Integer, String> mostlymiss = cacheManager.createCache("mostlymiss", configuration);
      IntStream.range(0, 250).forEach(i -> mostlymiss.put(i, "" + i));
      IntStream.range(0, 1000).forEach(i -> mostlymiss.get(i));

      // Cause evictions
      CacheConfiguration<Integer, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class,
          ResourcePoolsBuilder.heap(1000)).build();

      Cache<Integer, String> evicts = cacheManager.createCache("evicts", Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration));
      IntStream.range(0, 10_000).forEach(i -> {
        evicts.put(i, "" + i);
        if(i % 2 == 0) {
          evicts.get(i);
        }
      });

      // Nicely working cache
      Cache<Integer, String> works = cacheManager.createCache("works", configuration);
      IntStream.range(0, 950).forEach(i -> works.put(i, "" + i));
      IntStream.range(0, 1000).forEach(i -> works.get(i));

      Cache<Integer, String> notthatused = cacheManager.createCache("notthatused", configuration);
      IntStream.range(0, 1000).forEach(i -> notthatused.put(i, "" + i));
      notthatused.get(0);

      TimeUnit.MINUTES.sleep(10L);
    }

  }

  private static EhcacheCachingProvider getCachingProvider() {
    return (EhcacheCachingProvider) Caching.getCachingProvider();
  }
}
