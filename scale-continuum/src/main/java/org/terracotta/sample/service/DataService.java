package org.terracotta.sample.service;

/**
 * @author Aurelien Broszniowski
 */
public interface DataService<T> {
  T loadData(Long key);
}
