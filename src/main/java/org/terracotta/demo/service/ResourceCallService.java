package org.terracotta.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.terracotta.demo.service.dto.ResourceCallReport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henri Tremblay
 */
@Service
@RequestScope
public class ResourceCallService {

    private List<ResourceCallReport> reports = new ArrayList<>();

    public void addCall(String resourceName, ResourceCallReport.ResourceType type, long elapsed) {
        reports.add(new ResourceCallReport(resourceName, type, elapsed));
    }

    public long currentElapsed() {
        return reports.stream().mapToLong(ResourceCallReport::getElapsed).sum();
    }

    public List<ResourceCallReport> getReports() {
        return reports;
    }
}
