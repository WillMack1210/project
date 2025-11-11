package bham.team.web.rest;

import static bham.team.domain.ScheduleRequestAsserts.*;
import static bham.team.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bham.team.IntegrationTest;
import bham.team.domain.ScheduleRequest;
import bham.team.domain.enumeration.ScheduleIntensity;
import bham.team.repository.ScheduleRequestRepository;
import bham.team.service.dto.ScheduleRequestDTO;
import bham.team.service.mapper.ScheduleRequestMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ScheduleRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScheduleRequestResourceIT {

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SCHEDULE_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SCHEDULE_DESCRIPTION = "BBBBBBBBBB";

    private static final ScheduleIntensity DEFAULT_INTENSITY = ScheduleIntensity.EASY;
    private static final ScheduleIntensity UPDATED_INTENSITY = ScheduleIntensity.INTERMEDIATE;

    private static final String ENTITY_API_URL = "/api/schedule-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScheduleRequestRepository scheduleRequestRepository;

    @Autowired
    private ScheduleRequestMapper scheduleRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScheduleRequestMockMvc;

    private ScheduleRequest scheduleRequest;

    private ScheduleRequest insertedScheduleRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleRequest createEntity() {
        return new ScheduleRequest()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .scheduleDescription(DEFAULT_SCHEDULE_DESCRIPTION)
            .intensity(DEFAULT_INTENSITY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleRequest createUpdatedEntity() {
        return new ScheduleRequest()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .scheduleDescription(UPDATED_SCHEDULE_DESCRIPTION)
            .intensity(UPDATED_INTENSITY);
    }

    @BeforeEach
    public void initTest() {
        scheduleRequest = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedScheduleRequest != null) {
            scheduleRequestRepository.delete(insertedScheduleRequest);
            insertedScheduleRequest = null;
        }
    }

    @Test
    @Transactional
    void createScheduleRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ScheduleRequest
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);
        var returnedScheduleRequestDTO = om.readValue(
            restScheduleRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScheduleRequestDTO.class
        );

        // Validate the ScheduleRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedScheduleRequest = scheduleRequestMapper.toEntity(returnedScheduleRequestDTO);
        assertScheduleRequestUpdatableFieldsEquals(returnedScheduleRequest, getPersistedScheduleRequest(returnedScheduleRequest));

        insertedScheduleRequest = returnedScheduleRequest;
    }

    @Test
    @Transactional
    void createScheduleRequestWithExistingId() throws Exception {
        // Create the ScheduleRequest with an existing ID
        scheduleRequest.setId(1L);
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ScheduleRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleRequest.setStartDate(null);

        // Create the ScheduleRequest, which fails.
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        restScheduleRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleRequest.setEndDate(null);

        // Create the ScheduleRequest, which fails.
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        restScheduleRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIntensityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleRequest.setIntensity(null);

        // Create the ScheduleRequest, which fails.
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        restScheduleRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScheduleRequests() throws Exception {
        // Initialize the database
        insertedScheduleRequest = scheduleRequestRepository.saveAndFlush(scheduleRequest);

        // Get all the scheduleRequestList
        restScheduleRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].scheduleDescription").value(hasItem(DEFAULT_SCHEDULE_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].intensity").value(hasItem(DEFAULT_INTENSITY.toString())));
    }

    @Test
    @Transactional
    void getScheduleRequest() throws Exception {
        // Initialize the database
        insertedScheduleRequest = scheduleRequestRepository.saveAndFlush(scheduleRequest);

        // Get the scheduleRequest
        restScheduleRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, scheduleRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scheduleRequest.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.scheduleDescription").value(DEFAULT_SCHEDULE_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.intensity").value(DEFAULT_INTENSITY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingScheduleRequest() throws Exception {
        // Get the scheduleRequest
        restScheduleRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScheduleRequest() throws Exception {
        // Initialize the database
        insertedScheduleRequest = scheduleRequestRepository.saveAndFlush(scheduleRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleRequest
        ScheduleRequest updatedScheduleRequest = scheduleRequestRepository.findById(scheduleRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScheduleRequest are not directly saved in db
        em.detach(updatedScheduleRequest);
        updatedScheduleRequest
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .scheduleDescription(UPDATED_SCHEDULE_DESCRIPTION)
            .intensity(UPDATED_INTENSITY);
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(updatedScheduleRequest);

        restScheduleRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScheduleRequestToMatchAllProperties(updatedScheduleRequest);
    }

    @Test
    @Transactional
    void putNonExistingScheduleRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleRequest.setId(longCount.incrementAndGet());

        // Create the ScheduleRequest
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScheduleRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleRequest.setId(longCount.incrementAndGet());

        // Create the ScheduleRequest
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScheduleRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleRequest.setId(longCount.incrementAndGet());

        // Create the ScheduleRequest
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduleRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScheduleRequestWithPatch() throws Exception {
        // Initialize the database
        insertedScheduleRequest = scheduleRequestRepository.saveAndFlush(scheduleRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleRequest using partial update
        ScheduleRequest partialUpdatedScheduleRequest = new ScheduleRequest();
        partialUpdatedScheduleRequest.setId(scheduleRequest.getId());

        restScheduleRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduleRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduleRequest))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedScheduleRequest, scheduleRequest),
            getPersistedScheduleRequest(scheduleRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateScheduleRequestWithPatch() throws Exception {
        // Initialize the database
        insertedScheduleRequest = scheduleRequestRepository.saveAndFlush(scheduleRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleRequest using partial update
        ScheduleRequest partialUpdatedScheduleRequest = new ScheduleRequest();
        partialUpdatedScheduleRequest.setId(scheduleRequest.getId());

        partialUpdatedScheduleRequest
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .scheduleDescription(UPDATED_SCHEDULE_DESCRIPTION)
            .intensity(UPDATED_INTENSITY);

        restScheduleRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduleRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduleRequest))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleRequestUpdatableFieldsEquals(
            partialUpdatedScheduleRequest,
            getPersistedScheduleRequest(partialUpdatedScheduleRequest)
        );
    }

    @Test
    @Transactional
    void patchNonExistingScheduleRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleRequest.setId(longCount.incrementAndGet());

        // Create the ScheduleRequest
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scheduleRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScheduleRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleRequest.setId(longCount.incrementAndGet());

        // Create the ScheduleRequest
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScheduleRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleRequest.setId(longCount.incrementAndGet());

        // Create the ScheduleRequest
        ScheduleRequestDTO scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(scheduleRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduleRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScheduleRequest() throws Exception {
        // Initialize the database
        insertedScheduleRequest = scheduleRequestRepository.saveAndFlush(scheduleRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the scheduleRequest
        restScheduleRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, scheduleRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scheduleRequestRepository.count();
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

    protected ScheduleRequest getPersistedScheduleRequest(ScheduleRequest scheduleRequest) {
        return scheduleRequestRepository.findById(scheduleRequest.getId()).orElseThrow();
    }

    protected void assertPersistedScheduleRequestToMatchAllProperties(ScheduleRequest expectedScheduleRequest) {
        assertScheduleRequestAllPropertiesEquals(expectedScheduleRequest, getPersistedScheduleRequest(expectedScheduleRequest));
    }

    protected void assertPersistedScheduleRequestToMatchUpdatableProperties(ScheduleRequest expectedScheduleRequest) {
        assertScheduleRequestAllUpdatablePropertiesEquals(expectedScheduleRequest, getPersistedScheduleRequest(expectedScheduleRequest));
    }
}
