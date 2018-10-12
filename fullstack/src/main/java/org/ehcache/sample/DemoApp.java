package org.ehcache.sample;

import org.ehcache.sample.config.ApplicationProperties;
import org.ehcache.sample.config.DefaultProfileUtil;

import io.github.jhipster.config.JHipsterConstants;

import org.ehcache.sample.domain.Actor;
import org.ehcache.sample.repository.ActorRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.google.common.base.Strings;

import javax.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class DemoApp {

    private static final Logger log = LoggerFactory.getLogger(DemoApp.class);

    private final Environment env;

    public DemoApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes demo.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DemoApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }

    @Bean
    public CommandLineRunner demo(ActorRepository actorRepository) {
        return (args) -> {
            boolean stubWebServices = env.getProperty("application.stubWebServices", Boolean.class, false);

            if(stubWebServices) {
                log.warn("Fake web service calls will be made");
            }
            else {
                String googleApiKey = env.getProperty("application.googleApiKey");
                String darkSkyApiKey = env.getProperty("application.darkSkyApiKey");

                if (Strings.isNullOrEmpty(googleApiKey)) {
                    log.warn("The googleApiKey is not defined, CoordinatesService will NOT work !");
                }
                if (Strings.isNullOrEmpty(darkSkyApiKey)) {
                    log.warn("The darkSkyApiKey is not defined, WeatherService will NOT work !");
                }
            }

            // Assume that if we already have entries in the DB, that we already read the full file
            if (actorRepository.count() != 0) {
                return;
            }

            String biographiesLocation = env.getProperty("application.biographiesLocation");
            String biographiesRemoteLocation = env.getProperty("application.biographiesRemoteLocation");

            log.info("The Actor table is empty, let's fill it up!");

            Path localBiographiesPath = Paths.get(biographiesLocation);

            // If the file isn't present locally, download it
            if(!Files.exists(localBiographiesPath)) {
                log.info("We could NOT find a local copy of biographies.gz at {}, so let's download it from {}", biographiesLocation, biographiesRemoteLocation);
                try(InputStream inputStream = new URL(biographiesRemoteLocation).openStream()) {
                    Files.copy(inputStream, localBiographiesPath);
                }
                log.info("Download complete");
            } else {
                log.info("Using local copy of biographies.gz at {}", localBiographiesPath.toAbsolutePath().toString());

            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(localBiographiesPath)), StandardCharsets.ISO_8859_1))) {
                Stream<String> lines = br.lines()
                    .filter(s -> s.startsWith("NM:") && s.matches("^.*,.*$") || (s.startsWith("DB:") && s.matches("^.*\\s[0-9]{1,2}\\s.*\\s[0-9]{4}.*$")));

                Pattern birthDatePattern = Pattern.compile("^.*\\s([0-9]{1,2})\\s(.*)\\s([0-9]{4})(.*)$");

                AtomicReference<Actor> currentActor = new AtomicReference<>();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.US);

                lines.forEachOrdered(s -> {
                    try {
                        if (s.startsWith("NM:")) {
                            Actor actor = new Actor();
                            actor.setLastName(s.substring(4, s.indexOf(",")));
                            actor.setFirstName(s.split(",")[1].substring(1));
                            currentActor.set(actor);
                        } else {
                            Matcher birthDateMatcher = birthDatePattern.matcher(s);

                            if (birthDateMatcher.find()) {
                                String day = birthDateMatcher.group(1);
                                String month = birthDateMatcher.group(2);
                                String year = birthDateMatcher.group(3);

                                Actor actor = currentActor.get();
                                try {
                                    LocalDate birthDate = LocalDate.parse(day + " " + month + " " + year, formatter);
                                    actor.setBirthDate(birthDate);
                                    if (birthDateMatcher.group(4) != null && birthDateMatcher.group(4).length() > 2) {
                                        actor.setBirthLocation(birthDateMatcher.group(4).substring(2));
                                    }
                                    actorRepository.save(actor);
                                } catch (NumberFormatException e) {
                                    log.warn("Could not parse birthDate for " + currentActor.get().getFirstName() + " " + currentActor.get().getLastName(), e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Error loading : " + s, e);
                    }

                });

            }

            log.info("Number of actors in the database: {}", actorRepository.count());
        };
    }

}
