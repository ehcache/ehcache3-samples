package org.terracotta.demo;

/**
 * Created by Anthony Dahanne on 2016-09-25.
 */
public class ResourceCallReport {

  public enum ResourceType {WEB_SERVICE, DATABASE}

  private String parameterName;
  private long timeSpentMillis;
  private String methodName;
  private ResourceType resourceType;
  private String resourceName;

  public ResourceCallReport(String parameterName, long timeSpentMillis, String methodName, ResourceType resourceType, String resourceName) {
    this.parameterName = parameterName;
    this.timeSpentMillis = timeSpentMillis;
    this.methodName = methodName;
    this.resourceType = resourceType;
    this.resourceName = resourceName;
  }

  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public long getTimeSpentMillis() {
    return timeSpentMillis;
  }

  public void setTimeSpentMillis(long timeSpentMillis) {
    this.timeSpentMillis = timeSpentMillis;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public ResourceType getResourceType() {
    return resourceType;
  }

  public void setResourceType(ResourceType resourceType) {
    this.resourceType = resourceType;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResourceCallReport that = (ResourceCallReport) o;

    if (timeSpentMillis != that.timeSpentMillis) return false;
    if (parameterName != null ? !parameterName.equals(that.parameterName) : that.parameterName != null) return false;
    if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
    if (resourceType != that.resourceType) return false;
    return resourceName != null ? resourceName.equals(that.resourceName) : that.resourceName == null;

  }

  @Override
  public int hashCode() {
    int result = parameterName != null ? parameterName.hashCode() : 0;
    result = 31 * result + (int) (timeSpentMillis ^ (timeSpentMillis >>> 32));
    result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
    result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
    result = 31 * result + (resourceName != null ? resourceName.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ResourceCallReport{" +
        "parameterName='" + parameterName + '\'' +
        ", timeSpentMillis=" + timeSpentMillis +
        ", methodName='" + methodName + '\'' +
        ", resourceType=" + resourceType +
        ", resourceName='" + resourceName + '\'' +
        '}';
  }
}
