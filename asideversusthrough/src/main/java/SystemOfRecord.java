import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SystemOfRecord {

  private Logger LOG = LoggerFactory.getLogger(AsideVsPassthrough.class);
  private static SystemOfRecord systemOfRecord = new SystemOfRecord();
  private Map<String, String> records = new HashMap() {{
    put("key", "value");
  }};

  private SystemOfRecord() {
  }

  public String load(String key) {
    LOG.warn("Someone is accessing the slow SoR to load : " + key);
    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return records.get(key);
  }

  public void save(String key, String value) {
    try {
      Thread.sleep(10_000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    records.put(key, value);
  }

  public static SystemOfRecord getSystemOfRecord() {
    return systemOfRecord;
  }

}
