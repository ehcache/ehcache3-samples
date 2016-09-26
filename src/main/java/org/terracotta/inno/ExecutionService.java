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
package org.terracotta.inno;

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
import org.terracotta.inno.dao.SoRDao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static io.rainfall.configuration.ReportingConfig.text;
import static io.rainfall.execution.Executions.during;

/**
 * @author Ludovic Orban
 */
public class ExecutionService {

  private static final String OPERATION_NAME = "load";
  private static final int OBJECT_SIZE = 1024;

  enum DaoResult {
    LOAD
  }

  static class Config {
    private final long datasetSizeInBytes;

    public Config(long datasetSizeInBytes) {
      this.datasetSizeInBytes = datasetSizeInBytes;
    }

  }


  public Future<StatisticsPeekHolder> spawn(Config config) throws Exception {
    ConcurrencyConfig concurrency = ConcurrencyConfig.concurrencyConfig().threads(4);

    long objectCount = config.datasetSizeInBytes / OBJECT_SIZE;

    ObjectGenerator<Long> keyGenerator = new LongGenerator();
    ObjectGenerator<byte[]> valueGenerator = ByteArrayGenerator.fixedLength(OBJECT_SIZE);
    SoRDao<byte[]> soRDao = new SoRDao<>(valueGenerator);
    RandomSequenceGenerator randomSequenceGenerator = new RandomSequenceGenerator(Distribution.SLOW_GAUSSIAN, 0, objectCount, objectCount / 10);


    CompletableFuture<StatisticsPeekHolder> completableFuture = new CompletableFuture<>();
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          StatisticsPeekHolder statisticsPeekHolder = Runner.setUp(
              Scenario.scenario("Scale showoff").exec(
                  new Operation() {
                    @Override
                    public void exec(StatisticsHolder statisticsHolder, Map<Class<? extends Configuration>, Configuration> map, List<AssertionEvaluator> list) throws TestException {
                      Long key = keyGenerator.generate(randomSequenceGenerator.next());
                      long before = getTimeInNs();
                      byte[] bytes = soRDao.loadData(key);
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
              .config(concurrency, ReportingConfig.report(DaoResult.class).log(text()))
              .start();

          completableFuture.complete(statisticsPeekHolder);
        } catch (Exception e) {
          completableFuture.complete(null);
        }
      }
    };

    thread.start();

    return completableFuture;
  }

}
