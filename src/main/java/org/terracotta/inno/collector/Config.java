package org.terracotta.inno.collector;

import org.ehcache.impl.internal.store.offheap.MemorySizeParser;

/**
 * @author Aurelien Broszniowski
 */
public class Config {
  private final long datasetCount;
  private final boolean cacheEnabled;
  private final Long heapSizeCount;
  private final Long offheapSizeCount;
  private final String terracottaUrl;
  private final int clientCount;
  private final long valueSizeInBytes;

  public Config(Entry[] entries) {
    this.datasetCount = Long.parseLong(findEntry(entries, "datasetCount"));
    this.valueSizeInBytes = MemorySizeParser.parse(findEntry(entries, "valueSize"));
    this.clientCount = Integer.parseInt(findEntry(entries, "clientCount", "16"));
    this.cacheEnabled = "on".equalsIgnoreCase(findEntry(entries, "cacheEnabled"));
    if ("on".equalsIgnoreCase(findEntry(entries, "heapEnabled"))) {
      this.heapSizeCount = Long.parseLong(findEntry(entries, "heapSize"));
    } else {
      this.heapSizeCount = null;
    }
    if ("on".equalsIgnoreCase(findEntry(entries, "offheapEnabled"))) {
      this.offheapSizeCount = Long.parseLong(findEntry(entries, "offheapSize"));
    } else {
      this.offheapSizeCount = null;
    }
    if ("on".equalsIgnoreCase(findEntry(entries, "TerracottaEnabled"))) {
      this.terracottaUrl = findEntry(entries, "terracottaUrl");
    } else {
      this.terracottaUrl = null;
    }
  }

  public long getDatasetCount() {
    return datasetCount;
  }

  public boolean isCacheEnabled() {
    return cacheEnabled;
  }

  public Long getHeapSizeCount() {
    return heapSizeCount;
  }

  public Long getOffheapSizeCount() {
    return offheapSizeCount;
  }

  public String getTerracottaUrl() {
    return terracottaUrl;
  }

  public int getClientCount() {
    return clientCount;
  }

  public long getValueSizeInBytes() {
    return valueSizeInBytes;
  }

  private String findEntry(Entry[] entries, String name) {
    return findEntry(entries, name, null);
  }

  private String findEntry(Entry[] entries, String name, String defaultValue) {
    for (Entry entry : entries) {
      if (entry.getName().equals(name)) {
        return entry.getValue();
      }
    }
    return defaultValue;
  }
}
