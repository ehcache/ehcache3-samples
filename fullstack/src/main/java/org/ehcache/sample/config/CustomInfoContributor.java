package org.ehcache.sample.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Henri Tremblay
 */
@Component
public class CustomInfoContributor implements InfoContributor {

    private final Logger log = LoggerFactory.getLogger(CustomInfoContributor.class);

    @Override
    public void contribute(Info.Builder builder) {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.warn("Problem trying to resolve the hostname", e);
            hostname = "UNKNOWN";
        }
        builder.withDetail("hostname", hostname);
    }
}
