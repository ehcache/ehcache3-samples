import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class AsideVsPassthrough {

  public static void main(String[] args) throws InterruptedException {
    AsideVsPassthrough app = new AsideVsPassthrough();
    app.initSystemOfRecords();
    app.initCacheThrough();
//    app.initCacheAside();

    ExecutorService executor = Executors.newFixedThreadPool(10);
    IntStream.range(0, 9)
        .forEach(iteration -> executor.submit(() -> app.retrieveAndDisplayTheValueThrough()));
//        .forEach(iteration -> executor.submit(() -> app.retrieveAndDisplayTheValueAside()));

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
    app.closeCacheManager();

  }


  private static Logger LOG = LoggerFactory.getLogger(AsideVsPassthrough.class);
  private CacheManager cacheManager;
  private SystemOfRecord systemOfRecord;

  private void initCacheAside() {
    LOG.info("Init Cache");
    CacheConfiguration<String, String> cacheConfiguration = CacheConfigurationBuilder
        .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder
            .newResourcePoolsBuilder()
            .heap(100))
        .withExpiry(Expirations.timeToLiveExpiration(new Duration(60, TimeUnit.SECONDS)))
        .build();


    cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache("myCache", cacheConfiguration)
        .build();
    cacheManager.init();
  }

  private void initCacheThrough() {
    LOG.info("Init Cache");
    CacheConfiguration<String, String> cacheConfiguration = CacheConfigurationBuilder
        .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder
            .newResourcePoolsBuilder()
            .heap(100))

        .withExpiry(Expirations.timeToLiveExpiration(new Duration(60, TimeUnit.SECONDS)))
        .withLoaderWriter(new SorLoaderWriter(systemOfRecord))
        .build();


    cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache("myCache", cacheConfiguration)
        .build();
    cacheManager.init();
  }



  private void retrieveAndDisplayTheValueAside() {

    Cache<String, String> myCache = cacheManager.getCache("myCache", String.class, String.class);

    String value = myCache.get("key");
    if (value == null) {
      value = systemOfRecord.load("key");
      myCache.put("key", value);
    }

    LOG.info("We could retrieve the value : " + value);
  }

  private void retrieveAndDisplayTheValueThrough() {

    Cache<String, String> myCache = cacheManager.getCache("myCache", String.class, String.class);

    String value = myCache.get("key");

    LOG.info("We could retrieve the value : " + value);
  }

  private void initSystemOfRecords() {
    LOG.info("Init SystemOfRecords");
    systemOfRecord = SystemOfRecord.getSystemOfRecord();
  }


  private void closeCacheManager() {
    cacheManager.close();
  }
}
