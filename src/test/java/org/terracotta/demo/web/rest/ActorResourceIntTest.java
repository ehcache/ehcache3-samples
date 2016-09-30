package org.terracotta.demo.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ActorResource REST controller.
 *
 * @see ActorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApp.class)
public class ActorResourceIntTest {

    private static final String DEFAULT_FIRST_NAME = "AAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBB";
    private static final String DEFAULT_LAST_NAME = "AAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final String DEFAULT_BIRTH_LOCATION = "AAAAA";
    private static final String UPDATED_BIRTH_LOCATION = "BBBBB";

    @Inject
    private ActorRepository actorRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restActorMockMvc;

    private Actor actor;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ActorResource actorResource = new ActorResource();
        ReflectionTestUtils.setField(actorResource, "actorRepository", actorRepository);
        this.restActorMockMvc = MockMvcBuilders.standaloneSetup(actorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createEntity(EntityManager em) {
        Actor actor = new Actor()
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .birthDate(DEFAULT_BIRTH_DATE)
                .birthLocation(DEFAULT_BIRTH_LOCATION);
        return actor;
    }

    @Before
    public void initTest() {
        actor = createEntity(em);
    }

    @Test
    @Transactional
    public void createActor() throws Exception {
        int databaseSizeBeforeCreate = actorRepository.findAll().size();

        // Create the Actor

        restActorMockMvc.perform(post("/api/actors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(actor)))
                .andExpect(status().isCreated());

        // Validate the Actor in the database
        List<Actor> actors = actorRepository.findAll();
        assertThat(actors).hasSize(databaseSizeBeforeCreate + 1);
        Actor testActor = actors.get(actors.size() - 1);
        assertThat(testActor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testActor.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
        assertThat(testActor.getBirthLocation()).isEqualTo(DEFAULT_BIRTH_LOCATION);
    }

    @Test
    @Transactional
    public void getAllActors() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actors
        restActorMockMvc.perform(get("/api/actors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(actor.getId().intValue())))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
                .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
                .andExpect(jsonPath("$.[*].birthLocation").value(hasItem(DEFAULT_BIRTH_LOCATION.toString())));
    }

    @Test
    @Transactional
    public void getActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get the actor
        restActorMockMvc.perform(get("/api/actors/{id}", actor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(actor.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.birthLocation").value(DEFAULT_BIRTH_LOCATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingActor() throws Exception {
        // Get the actor
        restActorMockMvc.perform(get("/api/actors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActor() throws Exception {
        // Initialize the database
        actorRepository.save(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor
        Actor updatedActor = actorRepository.findOne(actor.getId());
        updatedActor
                .firstName(UPDATED_FIRST_NAME)
                .lastName(UPDATED_LAST_NAME)
                .birthDate(UPDATED_BIRTH_DATE)
                .birthLocation(UPDATED_BIRTH_LOCATION);

        restActorMockMvc.perform(put("/api/actors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedActor)))
                .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actors = actorRepository.findAll();
        assertThat(actors).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actors.get(actors.size() - 1);
        assertThat(testActor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testActor.getBirthLocation()).isEqualTo(UPDATED_BIRTH_LOCATION);
    }

    @Test
    @Transactional
    public void deleteActor() throws Exception {
        // Initialize the database
        actorRepository.save(actor);

        int databaseSizeBeforeDelete = actorRepository.findAll().size();

        // Get the actor
        restActorMockMvc.perform(delete("/api/actors/{id}", actor.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Actor> actors = actorRepository.findAll();
        assertThat(actors).hasSize(databaseSizeBeforeDelete - 1);
    }
}
