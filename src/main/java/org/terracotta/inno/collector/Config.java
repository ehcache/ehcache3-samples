package org.terracotta.inno.collector;

import org.ehcache.impl.internal.store.offheap.MemorySizeParser;

/**
 * @author Aurelien Broszniowski
 */
public class Config {
  private final long datasetSizeInBytes;
  private final boolean cacheEnabled;
  private final Long heapSizeInBytes;

  public Config(Entry[] entries) {
    this.datasetSizeInBytes = MemorySizeParser.parse(findEntry(entries, "datasetSize"));
    this.cacheEnabled = "on".equalsIgnoreCase(findEntry(entries, "cacheEnabled"));
    if ("on".equalsIgnoreCase(findEntry(entries, "heapEnabled"))) {
      this.heapSizeInBytes = MemorySizeParser.parse(findEntry(entries, "heapSize"));
    } else {
      this.heapSizeInBytes = null;
    }
  }

  public long getDatasetSizeInBytes() {
    return datasetSizeInBytes;
  }

  public boolean isCacheEnabled() {
    return cacheEnabled;
  }

  public Long getHeapSizeInBytes() {
    return heapSizeInBytes;
  }

  private String findEntry(Entry[] entries, String name) {
    for (Entry entry : entries) {
      if (entry.getName().equals(name)) {
        return entry.getValue();
      }
    }
    return null;
  }
}
