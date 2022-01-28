package com.ensa.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensa.IntegrationTest;
import com.ensa.domain.ClientBlackList;
import com.ensa.repository.ClientBlackListRepository;
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
 * Integration tests for the {@link ClientBlackListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientBlackListResourceIT {

    private static final Long DEFAULT_IDKYC = 1L;
    private static final Long UPDATED_IDKYC = 2L;

    private static final String DEFAULT_RAISON = "AAAAAAAAAA";
    private static final String UPDATED_RAISON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-black-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientBlackListRepository clientBlackListRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientBlackListMockMvc;

    private ClientBlackList clientBlackList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientBlackList createEntity(EntityManager em) {
        ClientBlackList clientBlackList = new ClientBlackList().idkyc(DEFAULT_IDKYC).raison(DEFAULT_RAISON);
        return clientBlackList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientBlackList createUpdatedEntity(EntityManager em) {
        ClientBlackList clientBlackList = new ClientBlackList().idkyc(UPDATED_IDKYC).raison(UPDATED_RAISON);
        return clientBlackList;
    }

    @BeforeEach
    public void initTest() {
        clientBlackList = createEntity(em);
    }

    @Test
    @Transactional
    void createClientBlackList() throws Exception {
        int databaseSizeBeforeCreate = clientBlackListRepository.findAll().size();
        // Create the ClientBlackList
        restClientBlackListMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientBlackList))
            )
            .andExpect(status().isCreated());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeCreate + 1);
        ClientBlackList testClientBlackList = clientBlackListList.get(clientBlackListList.size() - 1);
        assertThat(testClientBlackList.getIdkyc()).isEqualTo(DEFAULT_IDKYC);
        assertThat(testClientBlackList.getRaison()).isEqualTo(DEFAULT_RAISON);
    }

    @Test
    @Transactional
    void createClientBlackListWithExistingId() throws Exception {
        // Create the ClientBlackList with an existing ID
        clientBlackList.setId(1L);

        int databaseSizeBeforeCreate = clientBlackListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientBlackListMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientBlackList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClientBlackLists() throws Exception {
        // Initialize the database
        clientBlackListRepository.saveAndFlush(clientBlackList);

        // Get all the clientBlackListList
        restClientBlackListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientBlackList.getId().intValue())))
            .andExpect(jsonPath("$.[*].idkyc").value(hasItem(DEFAULT_IDKYC.intValue())))
            .andExpect(jsonPath("$.[*].raison").value(hasItem(DEFAULT_RAISON)));
    }

    @Test
    @Transactional
    void getClientBlackList() throws Exception {
        // Initialize the database
        clientBlackListRepository.saveAndFlush(clientBlackList);

        // Get the clientBlackList
        restClientBlackListMockMvc
            .perform(get(ENTITY_API_URL_ID, clientBlackList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientBlackList.getId().intValue()))
            .andExpect(jsonPath("$.idkyc").value(DEFAULT_IDKYC.intValue()))
            .andExpect(jsonPath("$.raison").value(DEFAULT_RAISON));
    }

    @Test
    @Transactional
    void getNonExistingClientBlackList() throws Exception {
        // Get the clientBlackList
        restClientBlackListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewClientBlackList() throws Exception {
        // Initialize the database
        clientBlackListRepository.saveAndFlush(clientBlackList);

        int databaseSizeBeforeUpdate = clientBlackListRepository.findAll().size();

        // Update the clientBlackList
        ClientBlackList updatedClientBlackList = clientBlackListRepository.findById(clientBlackList.getId()).get();
        // Disconnect from session so that the updates on updatedClientBlackList are not directly saved in db
        em.detach(updatedClientBlackList);
        updatedClientBlackList.idkyc(UPDATED_IDKYC).raison(UPDATED_RAISON);

        restClientBlackListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientBlackList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClientBlackList))
            )
            .andExpect(status().isOk());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeUpdate);
        ClientBlackList testClientBlackList = clientBlackListList.get(clientBlackListList.size() - 1);
        assertThat(testClientBlackList.getIdkyc()).isEqualTo(UPDATED_IDKYC);
        assertThat(testClientBlackList.getRaison()).isEqualTo(UPDATED_RAISON);
    }

    @Test
    @Transactional
    void putNonExistingClientBlackList() throws Exception {
        int databaseSizeBeforeUpdate = clientBlackListRepository.findAll().size();
        clientBlackList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientBlackListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientBlackList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientBlackList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientBlackList() throws Exception {
        int databaseSizeBeforeUpdate = clientBlackListRepository.findAll().size();
        clientBlackList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientBlackListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientBlackList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientBlackList() throws Exception {
        int databaseSizeBeforeUpdate = clientBlackListRepository.findAll().size();
        clientBlackList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientBlackListMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientBlackList))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientBlackListWithPatch() throws Exception {
        // Initialize the database
        clientBlackListRepository.saveAndFlush(clientBlackList);

        int databaseSizeBeforeUpdate = clientBlackListRepository.findAll().size();

        // Update the clientBlackList using partial update
        ClientBlackList partialUpdatedClientBlackList = new ClientBlackList();
        partialUpdatedClientBlackList.setId(clientBlackList.getId());

        partialUpdatedClientBlackList.idkyc(UPDATED_IDKYC).raison(UPDATED_RAISON);

        restClientBlackListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientBlackList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientBlackList))
            )
            .andExpect(status().isOk());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeUpdate);
        ClientBlackList testClientBlackList = clientBlackListList.get(clientBlackListList.size() - 1);
        assertThat(testClientBlackList.getIdkyc()).isEqualTo(UPDATED_IDKYC);
        assertThat(testClientBlackList.getRaison()).isEqualTo(UPDATED_RAISON);
    }

    @Test
    @Transactional
    void fullUpdateClientBlackListWithPatch() throws Exception {
        // Initialize the database
        clientBlackListRepository.saveAndFlush(clientBlackList);

        int databaseSizeBeforeUpdate = clientBlackListRepository.findAll().size();

        // Update the clientBlackList using partial update
        ClientBlackList partialUpdatedClientBlackList = new ClientBlackList();
        partialUpdatedClientBlackList.setId(clientBlackList.getId());

        partialUpdatedClientBlackList.idkyc(UPDATED_IDKYC).raison(UPDATED_RAISON);

        restClientBlackListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientBlackList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientBlackList))
            )
            .andExpect(status().isOk());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeUpdate);
        ClientBlackList testClientBlackList = clientBlackListList.get(clientBlackListList.size() - 1);
        assertThat(testClientBlackList.getIdkyc()).isEqualTo(UPDATED_IDKYC);
        assertThat(testClientBlackList.getRaison()).isEqualTo(UPDATED_RAISON);
    }

    @Test
    @Transactional
    void patchNonExistingClientBlackList() throws Exception {
        int databaseSizeBeforeUpdate = clientBlackListRepository.findAll().size();
        clientBlackList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientBlackListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientBlackList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientBlackList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientBlackList() throws Exception {
        int databaseSizeBeforeUpdate = clientBlackListRepository.findAll().size();
        clientBlackList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientBlackListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientBlackList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientBlackList() throws Exception {
        int databaseSizeBeforeUpdate = clientBlackListRepository.findAll().size();
        clientBlackList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientBlackListMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientBlackList))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientBlackList in the database
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientBlackList() throws Exception {
        // Initialize the database
        clientBlackListRepository.saveAndFlush(clientBlackList);

        int databaseSizeBeforeDelete = clientBlackListRepository.findAll().size();

        // Delete the clientBlackList
        restClientBlackListMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientBlackList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClientBlackList> clientBlackListList = clientBlackListRepository.findAll();
        assertThat(clientBlackListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
