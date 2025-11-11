package bham.team.web.rest;

import bham.team.domain.Suggestion;
import bham.team.repository.SuggestionRepository;
import bham.team.service.dto.SuggestionDTO;
import bham.team.service.mapper.SuggestionMapper;
import bham.team.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link bham.team.domain.Suggestion}.
 */
@RestController
@RequestMapping("/api/suggestions")
@Transactional
public class SuggestionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SuggestionResource.class);

    private static final String ENTITY_NAME = "suggestion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SuggestionRepository suggestionRepository;

    private final SuggestionMapper suggestionMapper;

    public SuggestionResource(SuggestionRepository suggestionRepository, SuggestionMapper suggestionMapper) {
        this.suggestionRepository = suggestionRepository;
        this.suggestionMapper = suggestionMapper;
    }

    /**
     * {@code POST  /suggestions} : Create a new suggestion.
     *
     * @param suggestionDTO the suggestionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new suggestionDTO, or with status {@code 400 (Bad Request)} if the suggestion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SuggestionDTO> createSuggestion(@Valid @RequestBody SuggestionDTO suggestionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Suggestion : {}", suggestionDTO);
        if (suggestionDTO.getId() != null) {
            throw new BadRequestAlertException("A new suggestion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Suggestion suggestion = suggestionMapper.toEntity(suggestionDTO);
        suggestion = suggestionRepository.save(suggestion);
        suggestionDTO = suggestionMapper.toDto(suggestion);
        return ResponseEntity.created(new URI("/api/suggestions/" + suggestionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, suggestionDTO.getId().toString()))
            .body(suggestionDTO);
    }

    /**
     * {@code PUT  /suggestions/:id} : Updates an existing suggestion.
     *
     * @param id the id of the suggestionDTO to save.
     * @param suggestionDTO the suggestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated suggestionDTO,
     * or with status {@code 400 (Bad Request)} if the suggestionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the suggestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SuggestionDTO> updateSuggestion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SuggestionDTO suggestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Suggestion : {}, {}", id, suggestionDTO);
        if (suggestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, suggestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!suggestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Suggestion suggestion = suggestionMapper.toEntity(suggestionDTO);
        suggestion = suggestionRepository.save(suggestion);
        suggestionDTO = suggestionMapper.toDto(suggestion);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, suggestionDTO.getId().toString()))
            .body(suggestionDTO);
    }

    /**
     * {@code PATCH  /suggestions/:id} : Partial updates given fields of an existing suggestion, field will ignore if it is null
     *
     * @param id the id of the suggestionDTO to save.
     * @param suggestionDTO the suggestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated suggestionDTO,
     * or with status {@code 400 (Bad Request)} if the suggestionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the suggestionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the suggestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SuggestionDTO> partialUpdateSuggestion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SuggestionDTO suggestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Suggestion partially : {}, {}", id, suggestionDTO);
        if (suggestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, suggestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!suggestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SuggestionDTO> result = suggestionRepository
            .findById(suggestionDTO.getId())
            .map(existingSuggestion -> {
                suggestionMapper.partialUpdate(existingSuggestion, suggestionDTO);

                return existingSuggestion;
            })
            .map(suggestionRepository::save)
            .map(suggestionMapper::toDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, suggestionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /suggestions} : get all the suggestions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of suggestions in body.
     */
    @GetMapping("")
    public List<SuggestionDTO> getAllSuggestions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Suggestions");
        List<Suggestion> suggestions = suggestionRepository.findAll();
        return suggestionMapper.toDto(suggestions);
    }

    /**
     * {@code GET  /suggestions/:id} : get the "id" suggestion.
     *
     * @param id the id of the suggestionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the suggestionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuggestionDTO> getSuggestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Suggestion : {}", id);
        Optional<SuggestionDTO> suggestionDTO = suggestionRepository.findOneWithEagerRelationships(id).map(suggestionMapper::toDto);
        return ResponseUtil.wrapOrNotFound(suggestionDTO);
    }

    /**
     * {@code DELETE  /suggestions/:id} : delete the "id" suggestion.
     *
     * @param id the id of the suggestionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Suggestion : {}", id);
        suggestionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
