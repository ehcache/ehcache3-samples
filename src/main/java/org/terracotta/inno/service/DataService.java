package org.terracotta.inno.service;

/**
 * @author Aurelien Broszniowski
 */
public interface DataService<T> {
  T loadData(Long key);
}
