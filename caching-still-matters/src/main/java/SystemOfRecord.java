import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class SystemOfRecord {

  private static final Logger LOG = LoggerFactory.getLogger(AsideVsPassthrough.class);
  private static final int MAX_CONNECTIONS = 5;
  private static final SystemOfRecord systemOfRecord = new SystemOfRecord();

  private volatile int activeConnections = 0;

  private final Map<String, String> records = new HashMap<>();

  private SystemOfRecord() {
    records.put("key", "value");
  }

  public String load(String key) {
    LOG.warn("Someone is accessing the slow SoR to load : " + key);
    acquireConnection();
    try {
      TimeUnit.SECONDS.sleep(30);
    } catch (InterruptedException e) {
      // Get out
    }
    releaseConnection();
    return records.get(key);
  }

  private synchronized void acquireConnection() {
    if(activeConnections++ >= MAX_CONNECTIONS) {
      LOG.error("No connections available");
    };
  }

  private void releaseConnection() {
    activeConnections--;
  }

  public void save(String key, String value) {
    acquireConnection();
    try {
      TimeUnit.SECONDS.sleep(10);
    } catch (InterruptedException e) {
      // Get out
    }
    releaseConnection();
    records.put(key, value);
  }

  public static SystemOfRecord getSystemOfRecord() {
    return systemOfRecord;
  }

}
