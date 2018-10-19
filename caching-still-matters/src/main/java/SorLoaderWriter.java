import org.ehcache.spi.loaderwriter.CacheLoaderWriter;

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
  public void write(String key, String value) {
  }

  @Override
  public void delete(String key) {

  }
}
