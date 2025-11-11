package bham.team.web.rest;

import static bham.team.domain.AvailiabilityBlockAsserts.*;
import static bham.team.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bham.team.IntegrationTest;
import bham.team.domain.AvailiabilityBlock;
import bham.team.repository.AvailiabilityBlockRepository;
import bham.team.service.dto.AvailiabilityBlockDTO;
import bham.team.service.mapper.AvailiabilityBlockMapper;
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
 * Integration tests for the {@link AvailiabilityBlockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AvailiabilityBlockResourceIT {

    private static final Instant DEFAULT_START_DATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/availiability-blocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AvailiabilityBlockRepository availiabilityBlockRepository;

    @Autowired
    private AvailiabilityBlockMapper availiabilityBlockMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAvailiabilityBlockMockMvc;

    private AvailiabilityBlock availiabilityBlock;

    private AvailiabilityBlock insertedAvailiabilityBlock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AvailiabilityBlock createEntity() {
        return new AvailiabilityBlock().startDateTime(DEFAULT_START_DATE_TIME).endDateTime(DEFAULT_END_DATE_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AvailiabilityBlock createUpdatedEntity() {
        return new AvailiabilityBlock().startDateTime(UPDATED_START_DATE_TIME).endDateTime(UPDATED_END_DATE_TIME);
    }

    @BeforeEach
    public void initTest() {
        availiabilityBlock = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAvailiabilityBlock != null) {
            availiabilityBlockRepository.delete(insertedAvailiabilityBlock);
            insertedAvailiabilityBlock = null;
        }
    }

    @Test
    @Transactional
    void createAvailiabilityBlock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AvailiabilityBlock
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);
        var returnedAvailiabilityBlockDTO = om.readValue(
            restAvailiabilityBlockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(availiabilityBlockDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AvailiabilityBlockDTO.class
        );

        // Validate the AvailiabilityBlock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAvailiabilityBlock = availiabilityBlockMapper.toEntity(returnedAvailiabilityBlockDTO);
        assertAvailiabilityBlockUpdatableFieldsEquals(
            returnedAvailiabilityBlock,
            getPersistedAvailiabilityBlock(returnedAvailiabilityBlock)
        );

        insertedAvailiabilityBlock = returnedAvailiabilityBlock;
    }

    @Test
    @Transactional
    void createAvailiabilityBlockWithExistingId() throws Exception {
        // Create the AvailiabilityBlock with an existing ID
        availiabilityBlock.setId(1L);
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAvailiabilityBlockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(availiabilityBlockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AvailiabilityBlock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        availiabilityBlock.setStartDateTime(null);

        // Create the AvailiabilityBlock, which fails.
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);

        restAvailiabilityBlockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(availiabilityBlockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        availiabilityBlock.setEndDateTime(null);

        // Create the AvailiabilityBlock, which fails.
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);

        restAvailiabilityBlockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(availiabilityBlockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAvailiabilityBlocks() throws Exception {
        // Initialize the database
        insertedAvailiabilityBlock = availiabilityBlockRepository.saveAndFlush(availiabilityBlock);

        // Get all the availiabilityBlockList
        restAvailiabilityBlockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(availiabilityBlock.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDateTime").value(hasItem(DEFAULT_START_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].endDateTime").value(hasItem(DEFAULT_END_DATE_TIME.toString())));
    }

    @Test
    @Transactional
    void getAvailiabilityBlock() throws Exception {
        // Initialize the database
        insertedAvailiabilityBlock = availiabilityBlockRepository.saveAndFlush(availiabilityBlock);

        // Get the availiabilityBlock
        restAvailiabilityBlockMockMvc
            .perform(get(ENTITY_API_URL_ID, availiabilityBlock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(availiabilityBlock.getId().intValue()))
            .andExpect(jsonPath("$.startDateTime").value(DEFAULT_START_DATE_TIME.toString()))
            .andExpect(jsonPath("$.endDateTime").value(DEFAULT_END_DATE_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAvailiabilityBlock() throws Exception {
        // Get the availiabilityBlock
        restAvailiabilityBlockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAvailiabilityBlock() throws Exception {
        // Initialize the database
        insertedAvailiabilityBlock = availiabilityBlockRepository.saveAndFlush(availiabilityBlock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the availiabilityBlock
        AvailiabilityBlock updatedAvailiabilityBlock = availiabilityBlockRepository.findById(availiabilityBlock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAvailiabilityBlock are not directly saved in db
        em.detach(updatedAvailiabilityBlock);
        updatedAvailiabilityBlock.startDateTime(UPDATED_START_DATE_TIME).endDateTime(UPDATED_END_DATE_TIME);
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(updatedAvailiabilityBlock);

        restAvailiabilityBlockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, availiabilityBlockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(availiabilityBlockDTO))
            )
            .andExpect(status().isOk());

        // Validate the AvailiabilityBlock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAvailiabilityBlockToMatchAllProperties(updatedAvailiabilityBlock);
    }

    @Test
    @Transactional
    void putNonExistingAvailiabilityBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        availiabilityBlock.setId(longCount.incrementAndGet());

        // Create the AvailiabilityBlock
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAvailiabilityBlockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, availiabilityBlockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(availiabilityBlockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AvailiabilityBlock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAvailiabilityBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        availiabilityBlock.setId(longCount.incrementAndGet());

        // Create the AvailiabilityBlock
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvailiabilityBlockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(availiabilityBlockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AvailiabilityBlock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAvailiabilityBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        availiabilityBlock.setId(longCount.incrementAndGet());

        // Create the AvailiabilityBlock
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvailiabilityBlockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(availiabilityBlockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AvailiabilityBlock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAvailiabilityBlockWithPatch() throws Exception {
        // Initialize the database
        insertedAvailiabilityBlock = availiabilityBlockRepository.saveAndFlush(availiabilityBlock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the availiabilityBlock using partial update
        AvailiabilityBlock partialUpdatedAvailiabilityBlock = new AvailiabilityBlock();
        partialUpdatedAvailiabilityBlock.setId(availiabilityBlock.getId());

        restAvailiabilityBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAvailiabilityBlock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAvailiabilityBlock))
            )
            .andExpect(status().isOk());

        // Validate the AvailiabilityBlock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAvailiabilityBlockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAvailiabilityBlock, availiabilityBlock),
            getPersistedAvailiabilityBlock(availiabilityBlock)
        );
    }

    @Test
    @Transactional
    void fullUpdateAvailiabilityBlockWithPatch() throws Exception {
        // Initialize the database
        insertedAvailiabilityBlock = availiabilityBlockRepository.saveAndFlush(availiabilityBlock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the availiabilityBlock using partial update
        AvailiabilityBlock partialUpdatedAvailiabilityBlock = new AvailiabilityBlock();
        partialUpdatedAvailiabilityBlock.setId(availiabilityBlock.getId());

        partialUpdatedAvailiabilityBlock.startDateTime(UPDATED_START_DATE_TIME).endDateTime(UPDATED_END_DATE_TIME);

        restAvailiabilityBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAvailiabilityBlock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAvailiabilityBlock))
            )
            .andExpect(status().isOk());

        // Validate the AvailiabilityBlock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAvailiabilityBlockUpdatableFieldsEquals(
            partialUpdatedAvailiabilityBlock,
            getPersistedAvailiabilityBlock(partialUpdatedAvailiabilityBlock)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAvailiabilityBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        availiabilityBlock.setId(longCount.incrementAndGet());

        // Create the AvailiabilityBlock
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAvailiabilityBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, availiabilityBlockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(availiabilityBlockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AvailiabilityBlock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAvailiabilityBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        availiabilityBlock.setId(longCount.incrementAndGet());

        // Create the AvailiabilityBlock
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvailiabilityBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(availiabilityBlockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AvailiabilityBlock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAvailiabilityBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        availiabilityBlock.setId(longCount.incrementAndGet());

        // Create the AvailiabilityBlock
        AvailiabilityBlockDTO availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvailiabilityBlockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(availiabilityBlockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AvailiabilityBlock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAvailiabilityBlock() throws Exception {
        // Initialize the database
        insertedAvailiabilityBlock = availiabilityBlockRepository.saveAndFlush(availiabilityBlock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the availiabilityBlock
        restAvailiabilityBlockMockMvc
            .perform(delete(ENTITY_API_URL_ID, availiabilityBlock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return availiabilityBlockRepository.count();
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

    protected AvailiabilityBlock getPersistedAvailiabilityBlock(AvailiabilityBlock availiabilityBlock) {
        return availiabilityBlockRepository.findById(availiabilityBlock.getId()).orElseThrow();
    }

    protected void assertPersistedAvailiabilityBlockToMatchAllProperties(AvailiabilityBlock expectedAvailiabilityBlock) {
        assertAvailiabilityBlockAllPropertiesEquals(expectedAvailiabilityBlock, getPersistedAvailiabilityBlock(expectedAvailiabilityBlock));
    }

    protected void assertPersistedAvailiabilityBlockToMatchUpdatableProperties(AvailiabilityBlock expectedAvailiabilityBlock) {
        assertAvailiabilityBlockAllUpdatablePropertiesEquals(
            expectedAvailiabilityBlock,
            getPersistedAvailiabilityBlock(expectedAvailiabilityBlock)
        );
    }
}
