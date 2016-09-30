package org.terracotta.demo.web.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.terracotta.demo.DemoApp;
import org.terracotta.demo.domain.Actor;
import org.terracotta.demo.repository.ActorRepository;
import org.terracotta.demo.service.ResourceCallService;
import org.terracotta.demo.service.WeatherService;
import org.terracotta.demo.service.dto.WeatherReport;

import java.time.LocalDate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Henri Tremblay
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApp.class)
public class StarResourceTest {

    private static final String DEFAULT_FIRST_NAME = "Alain";
    private static final String DEFAULT_LAST_NAME = "Delon";
    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.of(1935, 11, 8);
    private static final String DEFAULT_BIRTH_LOCATION = "Sceaux, Seine, France";

    @Inject
    private ActorRepository actorRepository;

    @Mock
    private WeatherService weatherService;

    @Inject
    private ResourceCallService resourceCallService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restActorMockMvc;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public Actor createEntity() {
        Actor actor = new Actor()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .birthDate(DEFAULT_BIRTH_DATE)
            .birthLocation(DEFAULT_BIRTH_LOCATION);
        return actor;
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StarResource starResource = new StarResource();
        ReflectionTestUtils.setField(starResource, "actorRepository", actorRepository);
        ReflectionTestUtils.setField(starResource, "weatherService", weatherService);
        ReflectionTestUtils.setField(starResource, "resourceCallService", resourceCallService);
        this.restActorMockMvc = MockMvcBuilders.standaloneSetup(starResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Test
    @Transactional
    public void startDetails() throws Exception {
        Actor actor = createEntity();
        actorRepository.saveAndFlush(actor);

        // FIXME: This mock doesn't. For a really strange reason, the mock injected in setup() isn't the same as this one
        WeatherReport report = new WeatherReport(DEFAULT_BIRTH_DATE, DEFAULT_BIRTH_LOCATION, "cloud", "cloud", 20, 30);
        when(weatherService.retrieveWeatherReport(DEFAULT_BIRTH_LOCATION, DEFAULT_BIRTH_DATE)).thenReturn(report);

        // Get all the actors
        restActorMockMvc.perform(get("/api/stars/{id}", actor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actor.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].birthLocation").value(hasItem(DEFAULT_BIRTH_LOCATION.toString())));
//            .andExpect(jsonPath("$.[*].temperatureMin").value(hasItem("20")))
//            .andExpect(jsonPath("$.[*].resourceName").value(hasItem("ActorRepository.findOne")));

    }

}
