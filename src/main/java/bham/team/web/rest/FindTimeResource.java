package bham.team.web.rest;

import bham.team.domain.FindTime;
import bham.team.repository.FindTimeRepository;
import bham.team.service.dto.FindTimeDTO;
import bham.team.service.mapper.FindTimeMapper;
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
 * REST controller for managing {@link bham.team.domain.FindTime}.
 */
@RestController
@RequestMapping("/api/find-times")
@Transactional
public class FindTimeResource {

    private static final Logger LOG = LoggerFactory.getLogger(FindTimeResource.class);

    private static final String ENTITY_NAME = "findTime";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FindTimeRepository findTimeRepository;

    private final FindTimeMapper findTimeMapper;

    public FindTimeResource(FindTimeRepository findTimeRepository, FindTimeMapper findTimeMapper) {
        this.findTimeRepository = findTimeRepository;
        this.findTimeMapper = findTimeMapper;
    }

    /**
     * {@code POST  /find-times} : Create a new findTime.
     *
     * @param findTimeDTO the findTimeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new findTimeDTO, or with status {@code 400 (Bad Request)} if the findTime has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FindTimeDTO> createFindTime(@Valid @RequestBody FindTimeDTO findTimeDTO) throws URISyntaxException {
        LOG.debug("REST request to save FindTime : {}", findTimeDTO);
        if (findTimeDTO.getId() != null) {
            throw new BadRequestAlertException("A new findTime cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FindTime findTime = findTimeMapper.toEntity(findTimeDTO);
        findTime = findTimeRepository.save(findTime);
        findTimeDTO = findTimeMapper.toDto(findTime);
        return ResponseEntity.created(new URI("/api/find-times/" + findTimeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, findTimeDTO.getId().toString()))
            .body(findTimeDTO);
    }

    /**
     * {@code PUT  /find-times/:id} : Updates an existing findTime.
     *
     * @param id the id of the findTimeDTO to save.
     * @param findTimeDTO the findTimeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated findTimeDTO,
     * or with status {@code 400 (Bad Request)} if the findTimeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the findTimeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FindTimeDTO> updateFindTime(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FindTimeDTO findTimeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FindTime : {}, {}", id, findTimeDTO);
        if (findTimeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, findTimeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!findTimeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FindTime findTime = findTimeMapper.toEntity(findTimeDTO);
        findTime = findTimeRepository.save(findTime);
        findTimeDTO = findTimeMapper.toDto(findTime);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, findTimeDTO.getId().toString()))
            .body(findTimeDTO);
    }

    /**
     * {@code PATCH  /find-times/:id} : Partial updates given fields of an existing findTime, field will ignore if it is null
     *
     * @param id the id of the findTimeDTO to save.
     * @param findTimeDTO the findTimeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated findTimeDTO,
     * or with status {@code 400 (Bad Request)} if the findTimeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the findTimeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the findTimeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FindTimeDTO> partialUpdateFindTime(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FindTimeDTO findTimeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FindTime partially : {}, {}", id, findTimeDTO);
        if (findTimeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, findTimeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!findTimeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FindTimeDTO> result = findTimeRepository
            .findById(findTimeDTO.getId())
            .map(existingFindTime -> {
                findTimeMapper.partialUpdate(existingFindTime, findTimeDTO);

                return existingFindTime;
            })
            .map(findTimeRepository::save)
            .map(findTimeMapper::toDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, findTimeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /find-times} : get all the findTimes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of findTimes in body.
     */
    @GetMapping("")
    public List<FindTimeDTO> getAllFindTimes(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all FindTimes");
        List<FindTime> findTimes = findTimeRepository.findAll();
        return findTimeMapper.toDto(findTimes);
    }

    /**
     * {@code GET  /find-times/:id} : get the "id" findTime.
     *
     * @param id the id of the findTimeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the findTimeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FindTimeDTO> getFindTime(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FindTime : {}", id);
        Optional<FindTimeDTO> findTimeDTO = findTimeRepository.findOneWithEagerRelationships(id).map(findTimeMapper::toDto);
        return ResponseUtil.wrapOrNotFound(findTimeDTO);
    }

    /**
     * {@code DELETE  /find-times/:id} : delete the "id" findTime.
     *
     * @param id the id of the findTimeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFindTime(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FindTime : {}", id);
        findTimeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
