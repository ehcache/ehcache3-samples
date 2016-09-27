package org.terracotta.inno.service;

import io.rainfall.ObjectGenerator;
import io.rainfall.TestException;

/**
 * @author Aurelien Broszniowski
 */
public class CachedDataService<T> implements DataService<T> {

  public CachedDataService(final ObjectGenerator<T> valueGenerator) {}

  @Override
  public T loadData(final Long key) {
    return null;
  }
}
