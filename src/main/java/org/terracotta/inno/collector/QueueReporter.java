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

import io.rainfall.Reporter;
import io.rainfall.statistics.StatisticsHolder;
import io.rainfall.statistics.StatisticsPeek;
import io.rainfall.statistics.StatisticsPeekHolder;

import java.util.List;
import java.util.Queue;

import static org.terracotta.inno.collector.PerformanceMetricsCollector.DaoResult.LOAD;
import static org.terracotta.inno.collector.PerformanceMetricsCollector.OPERATION_NAME;

/**
 * @author Ludovic Orban
 */
public class QueueReporter extends Reporter<PerformanceMetricsCollector.DaoResult> {

  public static class Result {
    private final long timestamp;
    private final Long periodicTps;
    private final Double periodicAverageLatencyInMs;

    public Result(long timestamp, Long periodicTps, Double periodicAverageLatencyInMs) {
      this.timestamp = timestamp;
      this.periodicTps = periodicTps;
      this.periodicAverageLatencyInMs = periodicAverageLatencyInMs;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public Long getPeriodicTps() {
      return periodicTps;
    }

    public Double getPeriodicAverageLatencyInMs() {
      return periodicAverageLatencyInMs;
    }
  }

  private final Queue<Result> resultQueue;

  public QueueReporter(Queue<Result> resultQueue) {
    this.resultQueue = resultQueue;
  }

  @Override
  public void header(List<String> list) {
  }

  @Override
  public void report(StatisticsPeekHolder<PerformanceMetricsCollector.DaoResult> statisticsPeekHolder) {
    StatisticsPeek<PerformanceMetricsCollector.DaoResult> statisticsPeek = statisticsPeekHolder.getStatisticsPeeks(OPERATION_NAME);

    Double periodicAverageLatencyInMs = statisticsPeek.getPeriodicAverageLatencyInMs(LOAD);
    Long periodicTps = statisticsPeek.getPeriodicTps(LOAD);
    long timestamp = statisticsPeek.getTimestamp();

    if (periodicAverageLatencyInMs.isNaN()) {
      return;
    }
    resultQueue.offer(new Result(timestamp, periodicTps, periodicAverageLatencyInMs));
  }

  @Override
  public void summarize(StatisticsHolder<PerformanceMetricsCollector.DaoResult> statisticsHolder) {
  }

}
