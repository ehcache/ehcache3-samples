package org.terracotta.inno.service;

import org.ehcache.Cache;

/**
 * @author Aurelien Broszniowski
 */
public class CachedDataService implements DataService<byte[]> {

  private final Cache<Long, byte[]> cache;

  public CachedDataService(final Cache<Long, byte[]> cache) {
    this.cache = cache;
  }

  @Override
  public byte[] loadData(final Long key) {
    return cache.get(key);
  }
}
