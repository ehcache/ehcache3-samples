package org.ehcache.sample.config;

import org.ehcache.clustered.common.Consistency;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/**
 * Properties specific to Demo.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String biographiesRemoteLocation;
    private String biographiesLocation;
    private String googleApiKey;
    private String darkSkyApiKey;
    private boolean stubWebServices;

    private Cluster cluster;

    public String getBiographiesRemoteLocation() {
        return biographiesRemoteLocation;
    }

    public void setBiographiesRemoteLocation(String biographiesRemoteLocation) {
        this.biographiesRemoteLocation = biographiesRemoteLocation;
    }

    public String getBiographiesLocation() {
        return biographiesLocation;
    }

    public void setBiographiesLocation(String biographiesLocation) {
        this.biographiesLocation = biographiesLocation;
    }

    public String getGoogleApiKey() {
        return googleApiKey;
    }

    public void setGoogleApiKey(String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    public String getDarkSkyApiKey() {
        return darkSkyApiKey;
    }

    public void setDarkSkyApiKey(String darkSkyApiKey) {
        this.darkSkyApiKey = darkSkyApiKey;
    }

    public boolean isStubWebServices() {
        return stubWebServices;
    }

    public void setStubWebServices(boolean stubWebServices) {
        this.stubWebServices = stubWebServices;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public static class Cluster {

        private URI uri;

        private boolean autoCreate = true;

        private Consistency consistency = Consistency.STRONG;

        private long sizeInMb = 10;

        private String offheapResourceName = "offheap-1";

        public URI getUri() {
            return uri;
        }

        public void setUri(URI uri) {
            this.uri = uri;
        }

        public boolean isAutoCreate() {
            return autoCreate;
        }

        public void setAutoCreate(boolean autoCreate) {
            this.autoCreate = autoCreate;
        }

        public Consistency getConsistency() {
            return consistency;
        }

        public void setConsistency(Consistency consistency) {
            this.consistency = consistency;
        }

        public long getSizeInMb() {
            return sizeInMb;
        }

        public void setSizeInMb(long sizeInMb) {
            this.sizeInMb = sizeInMb;
        }

        public String getOffheapResourceName() {
            return offheapResourceName;
        }

        public void setOffheapResourceName(String offheapResourceName) {
            this.offheapResourceName = offheapResourceName;
        }
    }
}
