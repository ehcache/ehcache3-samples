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
package org.terracotta.inno.collector;

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
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.terracotta.inno.dao.SoRDao;
import org.terracotta.inno.dao.SorLoaderWriter;
import org.terracotta.inno.service.CachedDataService;
import org.terracotta.inno.service.DataService;
import org.terracotta.inno.service.UncachedDataService;

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
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.newResourcePoolsBuilder;

/**
 * @author Ludovic Orban
 */
public class PerformanceMetricsCollector {

  public static final String OPERATION_NAME = "load";
  private static final int OBJECT_SIZE = 1024;

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
    ConcurrencyConfig concurrency = ConcurrencyConfig.concurrencyConfig().threads(4);

    long objectCount = config.getDatasetSizeInBytes() / OBJECT_SIZE;

    ObjectGenerator<Long> keyGenerator = new LongGenerator();
    ObjectGenerator<byte[]> valueGenerator = ByteArrayGenerator.fixedLength(OBJECT_SIZE);
    DataService<byte[]> dataService;
    if (config.isCacheEnabled()) {
      CacheConfigurationBuilder<Long, byte[]> builder = newCacheConfigurationBuilder(Long.class, byte[].class,
          newResourcePoolsBuilder().heap(config.getHeapSizeInBytes(), MemoryUnit.B))
          .withLoaderWriter(new SorLoaderWriter(new SoRDao<byte[]>(valueGenerator)));

      cacheManager = newCacheManagerBuilder()
          .withCache("cache", builder.build())
          .build(true);

      dataService = new CachedDataService(cacheManager.getCache("cache", Long.class, byte[].class));
    } else {
      dataService = new UncachedDataService<>(valueGenerator);
    }
    RandomSequenceGenerator randomSequenceGenerator = new RandomSequenceGenerator(Distribution.SLOW_GAUSSIAN, 0, objectCount, objectCount / 10);

    resultQueue = new LinkedBlockingQueue<>();
    Callable<StatisticsPeekHolder> callable = () -> Runner.setUp(
        Scenario.scenario("Scaling demo").exec(
            new Operation() {
              @Override
              public void exec(StatisticsHolder statisticsHolder, Map<Class<? extends Configuration>, Configuration> map, List<AssertionEvaluator> list) throws TestException {
                Long key = keyGenerator.generate(randomSequenceGenerator.next());
                long before = getTimeInNs();
                byte[] bytes = dataService.loadData(key);
                long after = getTimeInNs();
                statisticsHolder.record(OPERATION_NAME, (after - before), DaoResult.LOAD);
              }

              @Override
              public List<String> getDescription() {
                return Arrays.asList("Service get");
              }
            }
        ))
        .executed(during(10, TimeDivision.minutes))
        .config(concurrency, ReportingConfig.report(DaoResult.class).log(new QueueReporter(resultQueue)))
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
