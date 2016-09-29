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
package org.terracotta.inno.dao;

import io.rainfall.ObjectGenerator;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ludovic Orban
 */
public class SoRDao<T> {

  private final static int INTERVAL_MS = 10;
  private final static int MAX_TPS = 80000;

  private volatile int tps = 1;
  private final AtomicInteger loads = new AtomicInteger();
  private final Timer timer = new Timer(true);

  private final ObjectGenerator<T> generator;
  private final Random random = new Random();

  public SoRDao(ObjectGenerator<T> generator) {
    this.generator = generator;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        tps = Math.max(MAX_TPS - (loads.getAndSet(0) * 1000 / INTERVAL_MS), 1);
      }
    }, INTERVAL_MS, INTERVAL_MS);
  }

  public T loadData(Long key) {
    int latency = 0;
    try {
      loads.incrementAndGet();
      latency = (Math.max(1000 / tps, 1));
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      int variance = latency / 10;   // variance = 0.1
      latency = latency - (variance);
      latency = latency + random.nextInt(2 * Math.max(variance, 1));

      Thread.sleep(latency);    // sleep between 0.9 x latency and 1.1 x latency
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return generator.generate(key);
  }

}
