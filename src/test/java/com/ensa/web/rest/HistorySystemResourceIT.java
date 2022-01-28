package com.ensa.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensa.IntegrationTest;
import com.ensa.domain.HistorySystem;
import com.ensa.repository.HistorySystemRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link HistorySystemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HistorySystemResourceIT {

    private static final Long DEFAULT_ID_AGENT = 1L;
    private static final Long UPDATED_ID_AGENT = 2L;

    private static final String DEFAULT_ACTION = "AAAAAAAAAA";
    private static final String UPDATED_ACTION = "BBBBBBBBBB";

    private static final String DEFAULT_MODULE = "AAAAAAAAAA";
    private static final String UPDATED_MODULE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_EXTRA = "AAAAAAAAAA";
    private static final String UPDATED_EXTRA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/history-systems";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HistorySystemRepository historySystemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHistorySystemMockMvc;

    private HistorySystem historySystem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistorySystem createEntity(EntityManager em) {
        HistorySystem historySystem = new HistorySystem()
            .idAgent(DEFAULT_ID_AGENT)
            .action(DEFAULT_ACTION)
            .module(DEFAULT_MODULE)
            .date(DEFAULT_DATE)
            .extra(DEFAULT_EXTRA);
        return historySystem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistorySystem createUpdatedEntity(EntityManager em) {
        HistorySystem historySystem = new HistorySystem()
            .idAgent(UPDATED_ID_AGENT)
            .action(UPDATED_ACTION)
            .module(UPDATED_MODULE)
            .date(UPDATED_DATE)
            .extra(UPDATED_EXTRA);
        return historySystem;
    }

    @BeforeEach
    public void initTest() {
        historySystem = createEntity(em);
    }

    @Test
    @Transactional
    void createHistorySystem() throws Exception {
        int databaseSizeBeforeCreate = historySystemRepository.findAll().size();
        // Create the HistorySystem
        restHistorySystemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(historySystem)))
            .andExpect(status().isCreated());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeCreate + 1);
        HistorySystem testHistorySystem = historySystemList.get(historySystemList.size() - 1);
        assertThat(testHistorySystem.getIdAgent()).isEqualTo(DEFAULT_ID_AGENT);
        assertThat(testHistorySystem.getAction()).isEqualTo(DEFAULT_ACTION);
        assertThat(testHistorySystem.getModule()).isEqualTo(DEFAULT_MODULE);
        assertThat(testHistorySystem.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testHistorySystem.getExtra()).isEqualTo(DEFAULT_EXTRA);
    }

    @Test
    @Transactional
    void createHistorySystemWithExistingId() throws Exception {
        // Create the HistorySystem with an existing ID
        historySystem.setId(1L);

        int databaseSizeBeforeCreate = historySystemRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHistorySystemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(historySystem)))
            .andExpect(status().isBadRequest());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHistorySystems() throws Exception {
        // Initialize the database
        historySystemRepository.saveAndFlush(historySystem);

        // Get all the historySystemList
        restHistorySystemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historySystem.getId().intValue())))
            .andExpect(jsonPath("$.[*].idAgent").value(hasItem(DEFAULT_ID_AGENT.intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].module").value(hasItem(DEFAULT_MODULE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].extra").value(hasItem(DEFAULT_EXTRA)));
    }

    @Test
    @Transactional
    void getHistorySystem() throws Exception {
        // Initialize the database
        historySystemRepository.saveAndFlush(historySystem);

        // Get the historySystem
        restHistorySystemMockMvc
            .perform(get(ENTITY_API_URL_ID, historySystem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(historySystem.getId().intValue()))
            .andExpect(jsonPath("$.idAgent").value(DEFAULT_ID_AGENT.intValue()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION))
            .andExpect(jsonPath("$.module").value(DEFAULT_MODULE))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.extra").value(DEFAULT_EXTRA));
    }

    @Test
    @Transactional
    void getNonExistingHistorySystem() throws Exception {
        // Get the historySystem
        restHistorySystemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHistorySystem() throws Exception {
        // Initialize the database
        historySystemRepository.saveAndFlush(historySystem);

        int databaseSizeBeforeUpdate = historySystemRepository.findAll().size();

        // Update the historySystem
        HistorySystem updatedHistorySystem = historySystemRepository.findById(historySystem.getId()).get();
        // Disconnect from session so that the updates on updatedHistorySystem are not directly saved in db
        em.detach(updatedHistorySystem);
        updatedHistorySystem
            .idAgent(UPDATED_ID_AGENT)
            .action(UPDATED_ACTION)
            .module(UPDATED_MODULE)
            .date(UPDATED_DATE)
            .extra(UPDATED_EXTRA);

        restHistorySystemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHistorySystem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHistorySystem))
            )
            .andExpect(status().isOk());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeUpdate);
        HistorySystem testHistorySystem = historySystemList.get(historySystemList.size() - 1);
        assertThat(testHistorySystem.getIdAgent()).isEqualTo(UPDATED_ID_AGENT);
        assertThat(testHistorySystem.getAction()).isEqualTo(UPDATED_ACTION);
        assertThat(testHistorySystem.getModule()).isEqualTo(UPDATED_MODULE);
        assertThat(testHistorySystem.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testHistorySystem.getExtra()).isEqualTo(UPDATED_EXTRA);
    }

    @Test
    @Transactional
    void putNonExistingHistorySystem() throws Exception {
        int databaseSizeBeforeUpdate = historySystemRepository.findAll().size();
        historySystem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistorySystemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, historySystem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historySystem))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHistorySystem() throws Exception {
        int databaseSizeBeforeUpdate = historySystemRepository.findAll().size();
        historySystem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistorySystemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historySystem))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHistorySystem() throws Exception {
        int databaseSizeBeforeUpdate = historySystemRepository.findAll().size();
        historySystem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistorySystemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(historySystem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHistorySystemWithPatch() throws Exception {
        // Initialize the database
        historySystemRepository.saveAndFlush(historySystem);

        int databaseSizeBeforeUpdate = historySystemRepository.findAll().size();

        // Update the historySystem using partial update
        HistorySystem partialUpdatedHistorySystem = new HistorySystem();
        partialUpdatedHistorySystem.setId(historySystem.getId());

        partialUpdatedHistorySystem.date(UPDATED_DATE).extra(UPDATED_EXTRA);

        restHistorySystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistorySystem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistorySystem))
            )
            .andExpect(status().isOk());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeUpdate);
        HistorySystem testHistorySystem = historySystemList.get(historySystemList.size() - 1);
        assertThat(testHistorySystem.getIdAgent()).isEqualTo(DEFAULT_ID_AGENT);
        assertThat(testHistorySystem.getAction()).isEqualTo(DEFAULT_ACTION);
        assertThat(testHistorySystem.getModule()).isEqualTo(DEFAULT_MODULE);
        assertThat(testHistorySystem.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testHistorySystem.getExtra()).isEqualTo(UPDATED_EXTRA);
    }

    @Test
    @Transactional
    void fullUpdateHistorySystemWithPatch() throws Exception {
        // Initialize the database
        historySystemRepository.saveAndFlush(historySystem);

        int databaseSizeBeforeUpdate = historySystemRepository.findAll().size();

        // Update the historySystem using partial update
        HistorySystem partialUpdatedHistorySystem = new HistorySystem();
        partialUpdatedHistorySystem.setId(historySystem.getId());

        partialUpdatedHistorySystem
            .idAgent(UPDATED_ID_AGENT)
            .action(UPDATED_ACTION)
            .module(UPDATED_MODULE)
            .date(UPDATED_DATE)
            .extra(UPDATED_EXTRA);

        restHistorySystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistorySystem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistorySystem))
            )
            .andExpect(status().isOk());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeUpdate);
        HistorySystem testHistorySystem = historySystemList.get(historySystemList.size() - 1);
        assertThat(testHistorySystem.getIdAgent()).isEqualTo(UPDATED_ID_AGENT);
        assertThat(testHistorySystem.getAction()).isEqualTo(UPDATED_ACTION);
        assertThat(testHistorySystem.getModule()).isEqualTo(UPDATED_MODULE);
        assertThat(testHistorySystem.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testHistorySystem.getExtra()).isEqualTo(UPDATED_EXTRA);
    }

    @Test
    @Transactional
    void patchNonExistingHistorySystem() throws Exception {
        int databaseSizeBeforeUpdate = historySystemRepository.findAll().size();
        historySystem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistorySystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, historySystem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(historySystem))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHistorySystem() throws Exception {
        int databaseSizeBeforeUpdate = historySystemRepository.findAll().size();
        historySystem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistorySystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(historySystem))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHistorySystem() throws Exception {
        int databaseSizeBeforeUpdate = historySystemRepository.findAll().size();
        historySystem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistorySystemMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(historySystem))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistorySystem in the database
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHistorySystem() throws Exception {
        // Initialize the database
        historySystemRepository.saveAndFlush(historySystem);

        int databaseSizeBeforeDelete = historySystemRepository.findAll().size();

        // Delete the historySystem
        restHistorySystemMockMvc
            .perform(delete(ENTITY_API_URL_ID, historySystem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HistorySystem> historySystemList = historySystemRepository.findAll();
        assertThat(historySystemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
