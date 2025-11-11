package bham.team.web.rest;

import static bham.team.domain.FindTimeAsserts.*;
import static bham.team.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bham.team.IntegrationTest;
import bham.team.domain.FindTime;
import bham.team.repository.FindTimeRepository;
import bham.team.service.dto.FindTimeDTO;
import bham.team.service.mapper.FindTimeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FindTimeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FindTimeResourceIT {

    private static final Instant DEFAULT_REQUEST_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUEST_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_REQUEST_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUEST_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_LENGTH = 1;
    private static final Integer UPDATED_LENGTH = 2;

    private static final String ENTITY_API_URL = "/api/find-times";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FindTimeRepository findTimeRepository;

    @Mock
    private FindTimeRepository findTimeRepositoryMock;

    @Autowired
    private FindTimeMapper findTimeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFindTimeMockMvc;

    private FindTime findTime;

    private FindTime insertedFindTime;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FindTime createEntity() {
        return new FindTime().requestStart(DEFAULT_REQUEST_START).requestEnd(DEFAULT_REQUEST_END).length(DEFAULT_LENGTH);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FindTime createUpdatedEntity() {
        return new FindTime().requestStart(UPDATED_REQUEST_START).requestEnd(UPDATED_REQUEST_END).length(UPDATED_LENGTH);
    }

    @BeforeEach
    public void initTest() {
        findTime = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFindTime != null) {
            findTimeRepository.delete(insertedFindTime);
            insertedFindTime = null;
        }
    }

    @Test
    @Transactional
    void createFindTime() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FindTime
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);
        var returnedFindTimeDTO = om.readValue(
            restFindTimeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(findTimeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FindTimeDTO.class
        );

        // Validate the FindTime in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFindTime = findTimeMapper.toEntity(returnedFindTimeDTO);
        assertFindTimeUpdatableFieldsEquals(returnedFindTime, getPersistedFindTime(returnedFindTime));

        insertedFindTime = returnedFindTime;
    }

    @Test
    @Transactional
    void createFindTimeWithExistingId() throws Exception {
        // Create the FindTime with an existing ID
        findTime.setId(1L);
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFindTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(findTimeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FindTime in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRequestStartIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        findTime.setRequestStart(null);

        // Create the FindTime, which fails.
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        restFindTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(findTimeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRequestEndIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        findTime.setRequestEnd(null);

        // Create the FindTime, which fails.
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        restFindTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(findTimeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLengthIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        findTime.setLength(null);

        // Create the FindTime, which fails.
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        restFindTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(findTimeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFindTimes() throws Exception {
        // Initialize the database
        insertedFindTime = findTimeRepository.saveAndFlush(findTime);

        // Get all the findTimeList
        restFindTimeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(findTime.getId().intValue())))
            .andExpect(jsonPath("$.[*].requestStart").value(hasItem(DEFAULT_REQUEST_START.toString())))
            .andExpect(jsonPath("$.[*].requestEnd").value(hasItem(DEFAULT_REQUEST_END.toString())))
            .andExpect(jsonPath("$.[*].length").value(hasItem(DEFAULT_LENGTH)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFindTimesWithEagerRelationshipsIsEnabled() throws Exception {
        when(findTimeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFindTimeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(findTimeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFindTimesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(findTimeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFindTimeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(findTimeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFindTime() throws Exception {
        // Initialize the database
        insertedFindTime = findTimeRepository.saveAndFlush(findTime);

        // Get the findTime
        restFindTimeMockMvc
            .perform(get(ENTITY_API_URL_ID, findTime.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(findTime.getId().intValue()))
            .andExpect(jsonPath("$.requestStart").value(DEFAULT_REQUEST_START.toString()))
            .andExpect(jsonPath("$.requestEnd").value(DEFAULT_REQUEST_END.toString()))
            .andExpect(jsonPath("$.length").value(DEFAULT_LENGTH));
    }

    @Test
    @Transactional
    void getNonExistingFindTime() throws Exception {
        // Get the findTime
        restFindTimeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFindTime() throws Exception {
        // Initialize the database
        insertedFindTime = findTimeRepository.saveAndFlush(findTime);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the findTime
        FindTime updatedFindTime = findTimeRepository.findById(findTime.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFindTime are not directly saved in db
        em.detach(updatedFindTime);
        updatedFindTime.requestStart(UPDATED_REQUEST_START).requestEnd(UPDATED_REQUEST_END).length(UPDATED_LENGTH);
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(updatedFindTime);

        restFindTimeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, findTimeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(findTimeDTO))
            )
            .andExpect(status().isOk());

        // Validate the FindTime in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFindTimeToMatchAllProperties(updatedFindTime);
    }

    @Test
    @Transactional
    void putNonExistingFindTime() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        findTime.setId(longCount.incrementAndGet());

        // Create the FindTime
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFindTimeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, findTimeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(findTimeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FindTime in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFindTime() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        findTime.setId(longCount.incrementAndGet());

        // Create the FindTime
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFindTimeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(findTimeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FindTime in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFindTime() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        findTime.setId(longCount.incrementAndGet());

        // Create the FindTime
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFindTimeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(findTimeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FindTime in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFindTimeWithPatch() throws Exception {
        // Initialize the database
        insertedFindTime = findTimeRepository.saveAndFlush(findTime);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the findTime using partial update
        FindTime partialUpdatedFindTime = new FindTime();
        partialUpdatedFindTime.setId(findTime.getId());

        restFindTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFindTime.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFindTime))
            )
            .andExpect(status().isOk());

        // Validate the FindTime in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFindTimeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFindTime, findTime), getPersistedFindTime(findTime));
    }

    @Test
    @Transactional
    void fullUpdateFindTimeWithPatch() throws Exception {
        // Initialize the database
        insertedFindTime = findTimeRepository.saveAndFlush(findTime);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the findTime using partial update
        FindTime partialUpdatedFindTime = new FindTime();
        partialUpdatedFindTime.setId(findTime.getId());

        partialUpdatedFindTime.requestStart(UPDATED_REQUEST_START).requestEnd(UPDATED_REQUEST_END).length(UPDATED_LENGTH);

        restFindTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFindTime.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFindTime))
            )
            .andExpect(status().isOk());

        // Validate the FindTime in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFindTimeUpdatableFieldsEquals(partialUpdatedFindTime, getPersistedFindTime(partialUpdatedFindTime));
    }

    @Test
    @Transactional
    void patchNonExistingFindTime() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        findTime.setId(longCount.incrementAndGet());

        // Create the FindTime
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFindTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, findTimeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(findTimeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FindTime in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFindTime() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        findTime.setId(longCount.incrementAndGet());

        // Create the FindTime
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFindTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(findTimeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FindTime in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFindTime() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        findTime.setId(longCount.incrementAndGet());

        // Create the FindTime
        FindTimeDTO findTimeDTO = findTimeMapper.toDto(findTime);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFindTimeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(findTimeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FindTime in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFindTime() throws Exception {
        // Initialize the database
        insertedFindTime = findTimeRepository.saveAndFlush(findTime);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the findTime
        restFindTimeMockMvc
            .perform(delete(ENTITY_API_URL_ID, findTime.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return findTimeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected FindTime getPersistedFindTime(FindTime findTime) {
        return findTimeRepository.findById(findTime.getId()).orElseThrow();
    }

    protected void assertPersistedFindTimeToMatchAllProperties(FindTime expectedFindTime) {
        assertFindTimeAllPropertiesEquals(expectedFindTime, getPersistedFindTime(expectedFindTime));
    }

    protected void assertPersistedFindTimeToMatchUpdatableProperties(FindTime expectedFindTime) {
        assertFindTimeAllUpdatablePropertiesEquals(expectedFindTime, getPersistedFindTime(expectedFindTime));
    }
}
