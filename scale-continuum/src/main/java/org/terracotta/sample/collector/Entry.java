package org.terracotta.sample.collector;

/**
 * @author Aurelien Broszniowski
 */
public class Entry {

  private String name;
  private String value;

  public Entry() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return name + "->" + value;
  }
}
