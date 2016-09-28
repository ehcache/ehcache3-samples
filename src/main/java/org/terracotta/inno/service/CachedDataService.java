package org.terracotta.inno.service;

import io.rainfall.ObjectGenerator;
import org.ehcache.Cache;
import org.terracotta.inno.dao.SoRDao;

/**
 * @author Aurelien Broszniowski
 */
public class CachedDataService implements DataService<byte[]> {

  private final Cache<Long, byte[]> cache;
  private final SoRDao<byte[]> soRDao;

  public CachedDataService(final Cache<Long, byte[]> cache, final ObjectGenerator<byte[]> valueGenerator) {
    this.cache = cache;
    soRDao = new SoRDao<>(valueGenerator);
  }

  @Override
  public byte[] loadData(final Long key) {
    byte[] bytes = cache.get(key);
    if (bytes == null) {
      bytes = soRDao.loadData(key);
      cache.put(key, bytes);
    }
    return bytes;
  }
}
