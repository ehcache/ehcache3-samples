package org.ehcache.sample.service.dto;

/**
 * Created by Anthony Dahanne on 2016-09-25.
 */
public class ResourceCallReport {

    private final String resourceName;
    private final ResourceType resourceType;
    private final String param;
    private final long elapsed;

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
