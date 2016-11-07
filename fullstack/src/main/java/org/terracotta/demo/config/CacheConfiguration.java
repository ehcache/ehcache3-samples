package org.terracotta.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;

import javax.annotation.PreDestroy;
import javax.cache.CacheManager;
import javax.inject.Inject;

@SuppressWarnings("unused")
@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class, DatabaseConfiguration.class })
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @Inject
    private MetricRegistry metricRegistry;

    @Inject
    private CacheManager cacheManager;

    @PreDestroy
    public void destroy() {
        log.info("Close Cache Manager");
        cacheManager.close();
    }

}
