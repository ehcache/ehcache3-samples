package org.ehcache.sample.web.rest;

import org.ehcache.sample.config.DefaultProfileUtil;

import io.github.jhipster.config.JHipsterProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.net.InetAddress.getLocalHost;

/**
 * Resource to return information about the currently running Spring profiles.
 */
@RestController
@RequestMapping("/api")
public class ProfileInfoResource {

    private final Logger log = LoggerFactory.getLogger(ProfileInfoResource.class);

    private final Environment env;

    private final JHipsterProperties jHipsterProperties;

    public ProfileInfoResource(Environment env, JHipsterProperties jHipsterProperties) {
        this.env = env;
        this.jHipsterProperties = jHipsterProperties;
    }

    @GetMapping("/profile-info")
    public ProfileInfoVM getActiveProfiles() {
        String[] activeProfiles = DefaultProfileUtil.getActiveProfiles(env);
        String hostname = null;
        try {
            hostname = getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            log.warn("Problem trying to resolve the hostname", e);
            hostname = "UNKNOWN";
        }
        return new ProfileInfoVM(activeProfiles, getRibbonEnv(activeProfiles), hostname);
    }

    private String getRibbonEnv(String[] activeProfiles) {
        String[] displayOnActiveProfiles = jHipsterProperties.getRibbon().getDisplayOnActiveProfiles();
        if (displayOnActiveProfiles == null) {
            return null;
        }
        List<String> ribbonProfiles = new ArrayList<>(Arrays.asList(displayOnActiveProfiles));
        List<String> springBootProfiles = Arrays.asList(activeProfiles);
        ribbonProfiles.retainAll(springBootProfiles);
        if (!ribbonProfiles.isEmpty()) {
            return ribbonProfiles.get(0);
        }
        return null;
    }

    class ProfileInfoVM {

        private String[] activeProfiles;

        private String ribbonEnv;

        private String hostname;

        ProfileInfoVM(String[] activeProfiles, String ribbonEnv, String hostname) {
            this.activeProfiles = activeProfiles;
            this.ribbonEnv = ribbonEnv;
            this.hostname = hostname;
        }

        public String[] getActiveProfiles() {
            return activeProfiles;
        }

        public String getRibbonEnv() {
            return ribbonEnv;
        }

        public String getHostname() {
            return hostname;
        }

    }
}
