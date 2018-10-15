import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class AsideVsPassthrough implements AutoCloseable {

  public static void main(String[] args) throws InterruptedException {
    try(AsideVsPassthrough app = new AsideVsPassthrough()) {
      app.initSystemOfRecords();
//    app.initCacheAside();
      app.initCacheThrough();

      ExecutorService executor = Executors.newFixedThreadPool(10);
      IntStream.range(0, 9)
//        .forEach(iteration -> executor.submit(() -> app.retrieveAndDisplayTheValueAside()));
          .forEach(iteration -> executor.submit(() -> app.retrieveAndDisplayTheValueThrough()));

      executor.shutdown();
      executor.awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(AsideVsPassthrough.class);

  private CacheManager cacheManager;
  private SystemOfRecord systemOfRecord;

  private void initCacheAside() {
    LOG.info("Init Cache");
    CacheConfiguration<String, String> cacheConfiguration = CacheConfigurationBuilder
        .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder
            .heap(100))
        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(60)))
        .build();


    cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache("myCache", cacheConfiguration)
        .build();
    cacheManager.init();
  }

  private void initCacheThrough() {
    LOG.info("Init Cache");
    CacheConfigurationBuilder<String, String> cacheConfiguration = CacheConfigurationBuilder
        .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder
            .heap(100))
        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(60)))
        .withLoaderWriter(new SorLoaderWriter(systemOfRecord));

    cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache("myCache", cacheConfiguration)
        .build(true);
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

  @Override
  public void close() {
    cacheManager.close();
  }
}
