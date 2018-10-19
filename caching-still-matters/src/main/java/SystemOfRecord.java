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
    if(!acquireConnection()) {
      throw new IllegalStateException("No connection available");
    }
    try {
      TimeUnit.SECONDS.sleep(10);
      return records.get(key);
    } catch (InterruptedException e) {
      // Get out
      return null;
    } finally {
      releaseConnection();
    }
  }

  synchronized boolean acquireConnection() {
    if(activeConnections >= MAX_CONNECTIONS) {
      LOG.error("No connections available");
      return false;
    };
    activeConnections++;
    return true;
  }

  synchronized void releaseConnection() {
    activeConnections--;
  }

  public static SystemOfRecord getSystemOfRecord() {
    return systemOfRecord;
  }

}
