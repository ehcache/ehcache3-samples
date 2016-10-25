package org.terracotta.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.terracotta.demo.config.Constants;
import org.terracotta.demo.config.DefaultProfileUtil;
import org.terracotta.demo.config.JHipsterProperties;
import org.terracotta.demo.domain.Actor;
import org.terracotta.demo.repository.ActorRepository;

import com.google.common.base.Strings;

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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@ComponentScan
@EnableAutoConfiguration(exclude = { MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class })
@EnableConfigurationProperties({ JHipsterProperties.class, LiquibaseProperties.class })
public class DemoApp {

    private static final Logger log = LoggerFactory.getLogger(DemoApp.class);

    @Inject
    private Environment env;

    /**
     * Initializes demo.
     * <p>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="http://jhipster.github.io/profiles/">http://jhipster.github.io/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not" +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     * @throws UnknownHostException if the local host name could not be resolved into an address
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(DemoApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\thttp://127.0.0.1:{}\n\t" +
                "External: \thttp://{}:{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            env.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"));

    }

    @Bean
    public CommandLineRunner demo(ActorRepository actorRepository) {
        return (args) -> {
            String googleApiKey = env.getProperty("demo.googleApiKey");
            String darkSkyApiKey = env.getProperty("demo.darkSkyApiKey");

            if(Strings.isNullOrEmpty(googleApiKey)) {
                log.warn("The googleApiKey is not defined, CoordinatesService will NOT work !");
            }
            if(Strings.isNullOrEmpty(darkSkyApiKey)) {
                log.warn("The darkSkyApiKey is not defined, WeatherService will NOT work !");
            }

            // Assume that if we already have entries in the DB, that we already read the full file
            if (actorRepository.count() != 0) {
                return;
            }

            String biographiesLocation = env.getProperty("demo.biographiesLocation");
            String biographiesRemoteLocation = env.getProperty("demo.biographiesRemoteLocation");

            log.info("The Actor table is empty, let's fill it up!");

            Path localBiographiesPath = Paths.get(biographiesLocation);

            // If the file isn't present locally, download it
            if(!Files.exists(localBiographiesPath)) {
                log.info("We could NOT find a local copy of biographies.gz at {}, so let's download it from {}", biographiesLocation, biographiesRemoteLocation);
                try(InputStream inputStream = new URL(biographiesRemoteLocation).openStream()) {
                    Files.copy(inputStream, localBiographiesPath);
                }
                log.info("Download complete");
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(localBiographiesPath)), StandardCharsets.ISO_8859_1))) {
                Stream<String> lines = br.lines()
                        .filter(s -> s.startsWith("NM:") && s.matches("^.*,.*$") || (s.startsWith("DB:") && s.matches("^.*\\s[0-9]{1,2}\\s.*\\s[0-9]{4}.*$")));

                Pattern birthDatePattern = Pattern.compile("^.*\\s([0-9]{1,2})\\s(.*)\\s([0-9]{4})(.*)$");

                AtomicReference<Actor> currentActor = new AtomicReference<>();

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
                                    LocalDate birthDate = LocalDate.parse(day + " " + month + " " + year, DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.US));
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
