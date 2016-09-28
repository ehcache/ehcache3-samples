package org.terracotta.inno.dao;

import io.rainfall.ObjectGenerator;
import org.ehcache.spi.loaderwriter.BulkCacheLoadingException;
import org.ehcache.spi.loaderwriter.BulkCacheWritingException;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aurelien Broszniowski
 */
public class SorLoaderWriter implements CacheLoaderWriter<Long, byte[]> {

  private final SoRDao<byte[]> soRDao;

  public SorLoaderWriter(final SoRDao<byte[]> soRDao) {
    this.soRDao = soRDao;
  }

  @Override
  public byte[] load(final Long key) throws Exception {
    return soRDao.loadData(key);
  }

  @Override
  public Map<Long, byte[]> loadAll(final Iterable<? extends Long> iterable) throws BulkCacheLoadingException, Exception {
    Map<Long, byte[]> results = new HashMap<>();
    for (Long key : iterable) {
      results.put(key, soRDao.loadData(key));
    }
    return results;
  }

  @Override
  public void write(final Long aLong, final byte[] bytes) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeAll(final Iterable<? extends Map.Entry<? extends Long, ? extends byte[]>> iterable) throws BulkCacheWritingException, Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(final Long aLong) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteAll(final Iterable<? extends Long> iterable) throws BulkCacheWritingException, Exception {
    throw new UnsupportedOperationException();
  }
}
