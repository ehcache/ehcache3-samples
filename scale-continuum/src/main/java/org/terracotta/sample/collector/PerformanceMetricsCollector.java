/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.sample.collector;

import io.rainfall.AssertionEvaluator;
import io.rainfall.Configuration;
import io.rainfall.ObjectGenerator;
import io.rainfall.Operation;
import io.rainfall.Runner;
import io.rainfall.Scenario;
import io.rainfall.TestException;
import io.rainfall.configuration.ConcurrencyConfig;
import io.rainfall.configuration.ReportingConfig;
import io.rainfall.generator.ByteArrayGenerator;
import io.rainfall.generator.LongGenerator;
import io.rainfall.generator.RandomSequenceGenerator;
import io.rainfall.generator.sequence.Distribution;
import io.rainfall.statistics.StatisticsHolder;
import io.rainfall.statistics.StatisticsPeekHolder;
import io.rainfall.unit.TimeDivision;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.terracotta.sample.dao.SoRDao;
import org.terracotta.sample.dao.SorLoaderWriter;
import org.terracotta.sample.service.CachedDataService;
import org.terracotta.sample.service.DataService;
import org.terracotta.sample.service.UncachedDataService;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.rainfall.execution.Executions.during;
import static org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder.clusteredDedicated;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.newResourcePoolsBuilder;
import static org.ehcache.config.units.MemoryUnit.GB;

/**
 * @author Ludovic Orban
 */
public class PerformanceMetricsCollector {

  public static final String OPERATION_NAME = "load";

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  public enum DaoResult {
    LOAD
  }

  private volatile Queue<QueueReporter.Result> resultQueue;
  private CacheManager cacheManager;

  public Future<StatisticsPeekHolder> start(Config config) throws Exception {
    if (resultQueue != null) {
      throw new RuntimeException("Execution is in progress");
    }
    ConcurrencyConfig concurrency = ConcurrencyConfig.concurrencyConfig().threads(config.getClientCount());

    long objectCount = config.getDatasetCount();

    ObjectGenerator<Long> keyGenerator = new LongGenerator();
    ObjectGenerator<byte[]> valueGenerator = ByteArrayGenerator.fixedLengthByteArray((int)config.getValueSizeInBytes());
    DataService<byte[]> dataService;
    final Cache<Long, byte[]> cache;
    if (config.isCacheEnabled()) {
      ResourcePoolsBuilder resourcePoolsBuilder = newResourcePoolsBuilder();
      if (config.getHeapSizeCount() != null) {
        resourcePoolsBuilder = resourcePoolsBuilder.heap(config.getHeapSizeCount(), EntryUnit.ENTRIES);
      }
      if (config.getOffheapSizeCount() != null) {
        resourcePoolsBuilder = resourcePoolsBuilder.offheap(config.getOffheapSizeCount(), MemoryUnit.MB);
      }
      if (config.getTerracottaUrl() != null) {
        resourcePoolsBuilder = resourcePoolsBuilder.with(clusteredDedicated("primary-server-resource", 1, GB));
      }
      CacheConfigurationBuilder<Long, byte[]> cacheConfigurationBuilder = newCacheConfigurationBuilder(Long.class, byte[].class,
          resourcePoolsBuilder)
          .withLoaderWriter(new SorLoaderWriter(new SoRDao<>(valueGenerator)));

      CacheManagerBuilder cacheManagerBuilder = newCacheManagerBuilder();
      if (config.getTerracottaUrl() != null) {
        cacheManagerBuilder = cacheManagerBuilder.with(ClusteringServiceConfigurationBuilder.cluster(
            URI.create("terracotta://" + config.getTerracottaUrl() + "/clusterExample"))
            .autoCreate().build());
      }
      cacheManager = cacheManagerBuilder
          .withCache("cache", cacheConfigurationBuilder.build())
          .build(true);

      cache = cacheManager.getCache("cache", Long.class, byte[].class);
      dataService = new CachedDataService(cache);
    } else {
      dataService = new UncachedDataService<>(valueGenerator);
      cache = null;
    }
    RandomSequenceGenerator randomSequenceGenerator = new RandomSequenceGenerator(Distribution.SLOW_GAUSSIAN, 0, objectCount, objectCount / 10);

    resultQueue = new LinkedBlockingQueue<>();
    Callable<StatisticsPeekHolder> callable = () -> Runner.setUp(
        Scenario.scenario("Scaling demo").exec(
            new Operation() {
              @Override
              public void exec(StatisticsHolder statisticsHolder, Map<Class<? extends Configuration>, Configuration> map, List<AssertionEvaluator> list) throws TestException {
                Long key = keyGenerator.generate(randomSequenceGenerator.next());
                long before = System.nanoTime();
                byte[] bytes = dataService.loadData(key);
                long after = System.nanoTime();
                statisticsHolder.record(OPERATION_NAME, (after - before), DaoResult.LOAD);
              }

              @Override
              public List<String> getDescription() {
                return Arrays.asList("Service get");
              }
            }
        ))
        .executed(during(24 * 60, TimeDivision.minutes))
        .config(concurrency, ReportingConfig.report(DaoResult.class).log(new QueueReporter(resultQueue, cache)))
        .start();

    Future<StatisticsPeekHolder> future = executorService.submit(callable);
    return new Future<StatisticsPeekHolder>() {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancel = future.cancel(true);
        if (cacheManager != null) {
          cacheManager.close();
        }
        resultQueue = null;
        return cancel;
      }

      @Override
      public boolean isCancelled() {
        return future.isCancelled();
      }

      @Override
      public boolean isDone() {
        return future.isDone();
      }

      @Override
      public StatisticsPeekHolder get() throws InterruptedException, ExecutionException {
        return future.get();
      }

      @Override
      public StatisticsPeekHolder get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
      }
    };
  }

  public QueueReporter.Result pollStats() {
    if (resultQueue == null) {
      return null;
    }
    return resultQueue.poll();
  }

  public boolean isRunning() {
    return resultQueue != null;
  }

}
