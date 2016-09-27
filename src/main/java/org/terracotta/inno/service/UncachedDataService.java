package org.terracotta.inno.service;

import io.rainfall.ObjectGenerator;
import org.terracotta.inno.dao.SoRDao;

/**
 * @author Aurelien Broszniowski
 */
public class UncachedDataService<T> implements DataService<T> {

  private final SoRDao<T> soRDao;

  public UncachedDataService(final ObjectGenerator<T> valueGenerator) {
    soRDao = new SoRDao<>(valueGenerator);
  }

  @Override
  public T loadData(Long key) {
    return soRDao.loadData(key);
  }
}
