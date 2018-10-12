import org.ehcache.spi.loaderwriter.BulkCacheLoadingException;
import org.ehcache.spi.loaderwriter.BulkCacheWritingException;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;

import java.util.Map;

/**
 * Created by adah on 2017-02-20.
 */
public class SorLoaderWriter implements CacheLoaderWriter<String, String> {
  private final SystemOfRecord systemOfRecord;

  public SorLoaderWriter(SystemOfRecord systemOfRecord) {
    this.systemOfRecord = systemOfRecord;
  }

  @Override
  public String load(String key) {
    return systemOfRecord.load(key);
  }

  @Override
  public Map<String, String> loadAll(Iterable<? extends String> keys) {
    return null;
  }

  @Override
  public void write(String key, String value) {
  }

  @Override
  public void writeAll(Iterable<? extends Map.Entry<? extends String, ? extends String>> entries) {

  }

  @Override
  public void delete(String key) {

  }

  @Override
  public void deleteAll(Iterable<? extends String> keys) {

  }
}
