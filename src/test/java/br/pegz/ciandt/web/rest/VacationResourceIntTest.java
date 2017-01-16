package br.pegz.ciandt.web.rest;

import br.pegz.ciandt.VacationplannerApp;

import br.pegz.ciandt.domain.Vacation;
import br.pegz.ciandt.repository.VacationRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static br.pegz.ciandt.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.pegz.ciandt.domain.enumeration.Status;
/**
 * Test class for the VacationResource REST controller.
 *
 * @see VacationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = VacationplannerApp.class)
public class VacationResourceIntTest {

    private static final Integer DEFAULT_PERIOD = 1;
    private static final Integer UPDATED_PERIOD = 2;

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_SOLD_DAYS = 1;
    private static final Integer UPDATED_SOLD_DAYS = 2;

    private static final Integer DEFAULT_TOOK_DAYS = 1;
    private static final Integer UPDATED_TOOK_DAYS = 2;

    private static final Status DEFAULT_TOOK = Status.UNTAKEN;
    private static final Status UPDATED_TOOK = Status.SOLD_PARTIALLY;

    @Inject
    private VacationRepository vacationRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restVacationMockMvc;

    private Vacation vacation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        VacationResource vacationResource = new VacationResource();
        ReflectionTestUtils.setField(vacationResource, "vacationRepository", vacationRepository);
        this.restVacationMockMvc = MockMvcBuilders.standaloneSetup(vacationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vacation createEntity(EntityManager em) {
        Vacation vacation = new Vacation()
                .period(DEFAULT_PERIOD)
                .startDate(DEFAULT_START_DATE)
                .endDate(DEFAULT_END_DATE)
                .soldDays(DEFAULT_SOLD_DAYS)
                .tookDays(DEFAULT_TOOK_DAYS)
                .took(DEFAULT_TOOK);
        return vacation;
    }

    @Before
    public void initTest() {
        vacation = createEntity(em);
    }

    @Test
    @Transactional
    public void createVacation() throws Exception {
        int databaseSizeBeforeCreate = vacationRepository.findAll().size();

        // Create the Vacation

        restVacationMockMvc.perform(post("/api/vacations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vacation)))
            .andExpect(status().isCreated());

        // Validate the Vacation in the database
        List<Vacation> vacationList = vacationRepository.findAll();
        assertThat(vacationList).hasSize(databaseSizeBeforeCreate + 1);
        Vacation testVacation = vacationList.get(vacationList.size() - 1);
        assertThat(testVacation.getPeriod()).isEqualTo(DEFAULT_PERIOD);
        assertThat(testVacation.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testVacation.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testVacation.getSoldDays()).isEqualTo(DEFAULT_SOLD_DAYS);
        assertThat(testVacation.getTookDays()).isEqualTo(DEFAULT_TOOK_DAYS);
        assertThat(testVacation.getTook()).isEqualTo(DEFAULT_TOOK);
    }

    @Test
    @Transactional
    public void createVacationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = vacationRepository.findAll().size();

        // Create the Vacation with an existing ID
        Vacation existingVacation = new Vacation();
        existingVacation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVacationMockMvc.perform(post("/api/vacations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingVacation)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Vacation> vacationList = vacationRepository.findAll();
        assertThat(vacationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllVacations() throws Exception {
        // Initialize the database
        vacationRepository.saveAndFlush(vacation);

        // Get all the vacationList
        restVacationMockMvc.perform(get("/api/vacations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vacation.getId().intValue())))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))))
            .andExpect(jsonPath("$.[*].soldDays").value(hasItem(DEFAULT_SOLD_DAYS)))
            .andExpect(jsonPath("$.[*].tookDays").value(hasItem(DEFAULT_TOOK_DAYS)))
            .andExpect(jsonPath("$.[*].took").value(hasItem(DEFAULT_TOOK.toString())));
    }

    @Test
    @Transactional
    public void getVacation() throws Exception {
        // Initialize the database
        vacationRepository.saveAndFlush(vacation);

        // Get the vacation
        restVacationMockMvc.perform(get("/api/vacations/{id}", vacation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(vacation.getId().intValue()))
            .andExpect(jsonPath("$.period").value(DEFAULT_PERIOD))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)))
            .andExpect(jsonPath("$.soldDays").value(DEFAULT_SOLD_DAYS))
            .andExpect(jsonPath("$.tookDays").value(DEFAULT_TOOK_DAYS))
            .andExpect(jsonPath("$.took").value(DEFAULT_TOOK.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingVacation() throws Exception {
        // Get the vacation
        restVacationMockMvc.perform(get("/api/vacations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVacation() throws Exception {
        // Initialize the database
        vacationRepository.saveAndFlush(vacation);
        int databaseSizeBeforeUpdate = vacationRepository.findAll().size();

        // Update the vacation
        Vacation updatedVacation = vacationRepository.findOne(vacation.getId());
        updatedVacation
                .period(UPDATED_PERIOD)
                .startDate(UPDATED_START_DATE)
                .endDate(UPDATED_END_DATE)
                .soldDays(UPDATED_SOLD_DAYS)
                .tookDays(UPDATED_TOOK_DAYS)
                .took(UPDATED_TOOK);

        restVacationMockMvc.perform(put("/api/vacations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVacation)))
            .andExpect(status().isOk());

        // Validate the Vacation in the database
        List<Vacation> vacationList = vacationRepository.findAll();
        assertThat(vacationList).hasSize(databaseSizeBeforeUpdate);
        Vacation testVacation = vacationList.get(vacationList.size() - 1);
        assertThat(testVacation.getPeriod()).isEqualTo(UPDATED_PERIOD);
        assertThat(testVacation.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testVacation.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testVacation.getSoldDays()).isEqualTo(UPDATED_SOLD_DAYS);
        assertThat(testVacation.getTookDays()).isEqualTo(UPDATED_TOOK_DAYS);
        assertThat(testVacation.getTook()).isEqualTo(UPDATED_TOOK);
    }

    @Test
    @Transactional
    public void updateNonExistingVacation() throws Exception {
        int databaseSizeBeforeUpdate = vacationRepository.findAll().size();

        // Create the Vacation

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restVacationMockMvc.perform(put("/api/vacations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vacation)))
            .andExpect(status().isCreated());

        // Validate the Vacation in the database
        List<Vacation> vacationList = vacationRepository.findAll();
        assertThat(vacationList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteVacation() throws Exception {
        // Initialize the database
        vacationRepository.saveAndFlush(vacation);
        int databaseSizeBeforeDelete = vacationRepository.findAll().size();

        // Get the vacation
        restVacationMockMvc.perform(delete("/api/vacations/{id}", vacation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Vacation> vacationList = vacationRepository.findAll();
        assertThat(vacationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
