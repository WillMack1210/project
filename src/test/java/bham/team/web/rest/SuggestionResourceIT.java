package bham.team.web.rest;

import static bham.team.domain.SuggestionAsserts.*;
import static bham.team.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bham.team.IntegrationTest;
import bham.team.domain.Suggestion;
import bham.team.repository.SuggestionRepository;
import bham.team.service.dto.SuggestionDTO;
import bham.team.service.mapper.SuggestionMapper;
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
 * Integration tests for the {@link SuggestionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SuggestionResourceIT {

    private static final Instant DEFAULT_SUGGESTED_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUGGESTED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SUGGESTED_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUGGESTED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/suggestions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Mock
    private SuggestionRepository suggestionRepositoryMock;

    @Autowired
    private SuggestionMapper suggestionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSuggestionMockMvc;

    private Suggestion suggestion;

    private Suggestion insertedSuggestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Suggestion createEntity() {
        return new Suggestion().suggestedStart(DEFAULT_SUGGESTED_START).suggestedEnd(DEFAULT_SUGGESTED_END);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Suggestion createUpdatedEntity() {
        return new Suggestion().suggestedStart(UPDATED_SUGGESTED_START).suggestedEnd(UPDATED_SUGGESTED_END);
    }

    @BeforeEach
    public void initTest() {
        suggestion = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSuggestion != null) {
            suggestionRepository.delete(insertedSuggestion);
            insertedSuggestion = null;
        }
    }

    @Test
    @Transactional
    void createSuggestion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Suggestion
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);
        var returnedSuggestionDTO = om.readValue(
            restSuggestionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(suggestionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SuggestionDTO.class
        );

        // Validate the Suggestion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSuggestion = suggestionMapper.toEntity(returnedSuggestionDTO);
        assertSuggestionUpdatableFieldsEquals(returnedSuggestion, getPersistedSuggestion(returnedSuggestion));

        insertedSuggestion = returnedSuggestion;
    }

    @Test
    @Transactional
    void createSuggestionWithExistingId() throws Exception {
        // Create the Suggestion with an existing ID
        suggestion.setId(1L);
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSuggestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(suggestionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Suggestion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSuggestedStartIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        suggestion.setSuggestedStart(null);

        // Create the Suggestion, which fails.
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);

        restSuggestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(suggestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSuggestedEndIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        suggestion.setSuggestedEnd(null);

        // Create the Suggestion, which fails.
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);

        restSuggestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(suggestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSuggestions() throws Exception {
        // Initialize the database
        insertedSuggestion = suggestionRepository.saveAndFlush(suggestion);

        // Get all the suggestionList
        restSuggestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(suggestion.getId().intValue())))
            .andExpect(jsonPath("$.[*].suggestedStart").value(hasItem(DEFAULT_SUGGESTED_START.toString())))
            .andExpect(jsonPath("$.[*].suggestedEnd").value(hasItem(DEFAULT_SUGGESTED_END.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSuggestionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(suggestionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSuggestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(suggestionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSuggestionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(suggestionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSuggestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(suggestionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSuggestion() throws Exception {
        // Initialize the database
        insertedSuggestion = suggestionRepository.saveAndFlush(suggestion);

        // Get the suggestion
        restSuggestionMockMvc
            .perform(get(ENTITY_API_URL_ID, suggestion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(suggestion.getId().intValue()))
            .andExpect(jsonPath("$.suggestedStart").value(DEFAULT_SUGGESTED_START.toString()))
            .andExpect(jsonPath("$.suggestedEnd").value(DEFAULT_SUGGESTED_END.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSuggestion() throws Exception {
        // Get the suggestion
        restSuggestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSuggestion() throws Exception {
        // Initialize the database
        insertedSuggestion = suggestionRepository.saveAndFlush(suggestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the suggestion
        Suggestion updatedSuggestion = suggestionRepository.findById(suggestion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSuggestion are not directly saved in db
        em.detach(updatedSuggestion);
        updatedSuggestion.suggestedStart(UPDATED_SUGGESTED_START).suggestedEnd(UPDATED_SUGGESTED_END);
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(updatedSuggestion);

        restSuggestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, suggestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(suggestionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Suggestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSuggestionToMatchAllProperties(updatedSuggestion);
    }

    @Test
    @Transactional
    void putNonExistingSuggestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        suggestion.setId(longCount.incrementAndGet());

        // Create the Suggestion
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSuggestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, suggestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(suggestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Suggestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSuggestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        suggestion.setId(longCount.incrementAndGet());

        // Create the Suggestion
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuggestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(suggestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Suggestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSuggestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        suggestion.setId(longCount.incrementAndGet());

        // Create the Suggestion
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuggestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(suggestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Suggestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSuggestionWithPatch() throws Exception {
        // Initialize the database
        insertedSuggestion = suggestionRepository.saveAndFlush(suggestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the suggestion using partial update
        Suggestion partialUpdatedSuggestion = new Suggestion();
        partialUpdatedSuggestion.setId(suggestion.getId());

        partialUpdatedSuggestion.suggestedEnd(UPDATED_SUGGESTED_END);

        restSuggestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSuggestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSuggestion))
            )
            .andExpect(status().isOk());

        // Validate the Suggestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSuggestionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSuggestion, suggestion),
            getPersistedSuggestion(suggestion)
        );
    }

    @Test
    @Transactional
    void fullUpdateSuggestionWithPatch() throws Exception {
        // Initialize the database
        insertedSuggestion = suggestionRepository.saveAndFlush(suggestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the suggestion using partial update
        Suggestion partialUpdatedSuggestion = new Suggestion();
        partialUpdatedSuggestion.setId(suggestion.getId());

        partialUpdatedSuggestion.suggestedStart(UPDATED_SUGGESTED_START).suggestedEnd(UPDATED_SUGGESTED_END);

        restSuggestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSuggestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSuggestion))
            )
            .andExpect(status().isOk());

        // Validate the Suggestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSuggestionUpdatableFieldsEquals(partialUpdatedSuggestion, getPersistedSuggestion(partialUpdatedSuggestion));
    }

    @Test
    @Transactional
    void patchNonExistingSuggestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        suggestion.setId(longCount.incrementAndGet());

        // Create the Suggestion
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSuggestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, suggestionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(suggestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Suggestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSuggestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        suggestion.setId(longCount.incrementAndGet());

        // Create the Suggestion
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuggestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(suggestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Suggestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSuggestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        suggestion.setId(longCount.incrementAndGet());

        // Create the Suggestion
        SuggestionDTO suggestionDTO = suggestionMapper.toDto(suggestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuggestionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(suggestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Suggestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSuggestion() throws Exception {
        // Initialize the database
        insertedSuggestion = suggestionRepository.saveAndFlush(suggestion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the suggestion
        restSuggestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, suggestion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return suggestionRepository.count();
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

    protected Suggestion getPersistedSuggestion(Suggestion suggestion) {
        return suggestionRepository.findById(suggestion.getId()).orElseThrow();
    }

    protected void assertPersistedSuggestionToMatchAllProperties(Suggestion expectedSuggestion) {
        assertSuggestionAllPropertiesEquals(expectedSuggestion, getPersistedSuggestion(expectedSuggestion));
    }

    protected void assertPersistedSuggestionToMatchUpdatableProperties(Suggestion expectedSuggestion) {
        assertSuggestionAllUpdatablePropertiesEquals(expectedSuggestion, getPersistedSuggestion(expectedSuggestion));
    }
}
