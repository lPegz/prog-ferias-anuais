package br.pegz.ciandt.web.rest;

import br.pegz.ciandt.VacationplannerApp;

import br.pegz.ciandt.domain.Teammate;
import br.pegz.ciandt.repository.TeammateRepository;

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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TeammateResource REST controller.
 *
 * @see TeammateResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = VacationplannerApp.class)
public class TeammateResourceIntTest {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ADMIN = false;
    private static final Boolean UPDATED_IS_ADMIN = true;

    @Inject
    private TeammateRepository teammateRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTeammateMockMvc;

    private Teammate teammate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TeammateResource teammateResource = new TeammateResource();
        ReflectionTestUtils.setField(teammateResource, "teammateRepository", teammateRepository);
        this.restTeammateMockMvc = MockMvcBuilders.standaloneSetup(teammateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teammate createEntity(EntityManager em) {
        Teammate teammate = new Teammate()
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .login(DEFAULT_LOGIN)
                .isAdmin(DEFAULT_IS_ADMIN);
        return teammate;
    }

    @Before
    public void initTest() {
        teammate = createEntity(em);
    }

    @Test
    @Transactional
    public void createTeammate() throws Exception {
        int databaseSizeBeforeCreate = teammateRepository.findAll().size();

        // Create the Teammate

        restTeammateMockMvc.perform(post("/api/teammates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teammate)))
            .andExpect(status().isCreated());

        // Validate the Teammate in the database
        List<Teammate> teammateList = teammateRepository.findAll();
        assertThat(teammateList).hasSize(databaseSizeBeforeCreate + 1);
        Teammate testTeammate = teammateList.get(teammateList.size() - 1);
        assertThat(testTeammate.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testTeammate.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testTeammate.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testTeammate.isIsAdmin()).isEqualTo(DEFAULT_IS_ADMIN);
    }

    @Test
    @Transactional
    public void createTeammateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = teammateRepository.findAll().size();

        // Create the Teammate with an existing ID
        Teammate existingTeammate = new Teammate();
        existingTeammate.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeammateMockMvc.perform(post("/api/teammates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingTeammate)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Teammate> teammateList = teammateRepository.findAll();
        assertThat(teammateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTeammates() throws Exception {
        // Initialize the database
        teammateRepository.saveAndFlush(teammate);

        // Get all the teammateList
        restTeammateMockMvc.perform(get("/api/teammates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teammate.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].isAdmin").value(hasItem(DEFAULT_IS_ADMIN.booleanValue())));
    }

    @Test
    @Transactional
    public void getTeammate() throws Exception {
        // Initialize the database
        teammateRepository.saveAndFlush(teammate);

        // Get the teammate
        restTeammateMockMvc.perform(get("/api/teammates/{id}", teammate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(teammate.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.login").value(DEFAULT_LOGIN.toString()))
            .andExpect(jsonPath("$.isAdmin").value(DEFAULT_IS_ADMIN.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTeammate() throws Exception {
        // Get the teammate
        restTeammateMockMvc.perform(get("/api/teammates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTeammate() throws Exception {
        // Initialize the database
        teammateRepository.saveAndFlush(teammate);
        int databaseSizeBeforeUpdate = teammateRepository.findAll().size();

        // Update the teammate
        Teammate updatedTeammate = teammateRepository.findOne(teammate.getId());
        updatedTeammate
                .firstName(UPDATED_FIRST_NAME)
                .lastName(UPDATED_LAST_NAME)
                .login(UPDATED_LOGIN)
                .isAdmin(UPDATED_IS_ADMIN);

        restTeammateMockMvc.perform(put("/api/teammates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTeammate)))
            .andExpect(status().isOk());

        // Validate the Teammate in the database
        List<Teammate> teammateList = teammateRepository.findAll();
        assertThat(teammateList).hasSize(databaseSizeBeforeUpdate);
        Teammate testTeammate = teammateList.get(teammateList.size() - 1);
        assertThat(testTeammate.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testTeammate.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testTeammate.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testTeammate.isIsAdmin()).isEqualTo(UPDATED_IS_ADMIN);
    }

    @Test
    @Transactional
    public void updateNonExistingTeammate() throws Exception {
        int databaseSizeBeforeUpdate = teammateRepository.findAll().size();

        // Create the Teammate

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTeammateMockMvc.perform(put("/api/teammates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teammate)))
            .andExpect(status().isCreated());

        // Validate the Teammate in the database
        List<Teammate> teammateList = teammateRepository.findAll();
        assertThat(teammateList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTeammate() throws Exception {
        // Initialize the database
        teammateRepository.saveAndFlush(teammate);
        int databaseSizeBeforeDelete = teammateRepository.findAll().size();

        // Get the teammate
        restTeammateMockMvc.perform(delete("/api/teammates/{id}", teammate.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Teammate> teammateList = teammateRepository.findAll();
        assertThat(teammateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
