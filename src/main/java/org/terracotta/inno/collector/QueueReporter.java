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
import org.ehcache.Cache;

import java.util.List;
import java.util.Queue;

import static org.terracotta.inno.collector.Ehcache3Stats.findValueStat;
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
    private final Number onHeapCount;
    private final Number offHeapSizeInBytes;

    public Result(long timestamp) {
      this(timestamp, null, null, null, null);
    }

    public Result(long timestamp, Long periodicTps, Double periodicAverageLatencyInMs, Number onHeapCount, Number offHeapSizeInBytes) {
      this.timestamp = timestamp;
      this.periodicTps = periodicTps;
      this.periodicAverageLatencyInMs = periodicAverageLatencyInMs;
      this.onHeapCount = onHeapCount;
      this.offHeapSizeInBytes = offHeapSizeInBytes;
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

    public Number getOnHeapCount() {
      return onHeapCount;
    }

    public Number getOffHeapSizeInBytes() {
      return offHeapSizeInBytes;
    }
  }

  private final Queue<Result> resultQueue;
  private final Cache<?, ?> cache;

  public QueueReporter(Queue<Result> resultQueue, Cache<?, ?> cache) {
    this.resultQueue = resultQueue;
    this.cache = cache;
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
    Number onHeapCount = null;
    Number offHeapSizeInBytes = null;
    if (cache != null) {
      onHeapCount = findValueStat(cache, "mappingsCount", "onheap-store").value();
      offHeapSizeInBytes = findValueStat(cache, "occupiedMemory", "local-offheap").value();
    }

    if (periodicAverageLatencyInMs.isNaN()) {
      return;
    }
    resultQueue.offer(new Result(timestamp, periodicTps, periodicAverageLatencyInMs, onHeapCount, offHeapSizeInBytes));
  }

  @Override
  public void summarize(StatisticsHolder<PerformanceMetricsCollector.DaoResult> statisticsHolder) {
  }

}
