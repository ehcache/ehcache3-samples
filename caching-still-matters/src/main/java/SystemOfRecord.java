import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class SystemOfRecord {

  private static final Logger LOG = LoggerFactory.getLogger(AsideVsPassthrough.class);
  private static final SystemOfRecord systemOfRecord = new SystemOfRecord();

  private final Map<String, String> records = new HashMap();

  private SystemOfRecord() {
    records.put("key", "value");
  }

  public String load(String key) {
    LOG.warn("Someone is accessing the slow SoR to load : " + key);
    try {
      TimeUnit.SECONDS.sleep(30);
    } catch (InterruptedException e) {
      // Get out
    }
    return records.get(key);
  }

  public void save(String key, String value) {
    try {
      TimeUnit.SECONDS.sleep(10);
    } catch (InterruptedException e) {
      // Get out
    }
    records.put(key, value);
  }

  public static SystemOfRecord getSystemOfRecord() {
    return systemOfRecord;
  }

}
