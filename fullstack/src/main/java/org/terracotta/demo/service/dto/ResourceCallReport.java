package org.terracotta.demo.service.dto;

/**
 * Created by Anthony Dahanne on 2016-09-25.
 */
public class ResourceCallReport {

    public enum ResourceType {WEB_SERVICE, DATABASE}

    private String resourceName;
    private ResourceType resourceType;
    private String param;
    private long elapsed;

    public ResourceCallReport(String resourceName, ResourceType resourceType, String param, long elapsed) {
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.param = param;
        this.elapsed = elapsed;
    }

    public String getResourceName() {
        return resourceName;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getParam() {
        return param;
    }

    public long getElapsed() {
        return elapsed;
    }
}
