package org.ehcache.sample.service;

import org.ehcache.sample.service.dto.ResourceCallReport;
import org.ehcache.sample.service.dto.ResourceType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henri Tremblay
 */
@Service
@RequestScope
public class ResourceCallService {

    private List<ResourceCallReport> reports = new ArrayList<>();

    public void addCall(String resourceName, ResourceType type, String param, long elapsed) {
        reports.add(new ResourceCallReport(resourceName, type, param, elapsed));
    }

    public long currentElapsed() {
        return reports.stream().mapToLong(ResourceCallReport::getElapsed).sum();
    }

    public List<ResourceCallReport> getReports() {
        return reports;
    }
}
