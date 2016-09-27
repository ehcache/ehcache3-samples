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

/**
 * @author Ludovic Orban
 */
public class SoRDao<T> {

  private final ObjectGenerator<T> generator;
  private final Random random = new Random();

  public SoRDao(ObjectGenerator<T> generator) {
    this.generator = generator;
  }

  public T loadData(Long key) {
    try {
      Thread.sleep(random.nextInt(50));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return generator.generate(key);
  }

}
