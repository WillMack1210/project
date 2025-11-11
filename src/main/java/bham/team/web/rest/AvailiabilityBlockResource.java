package bham.team.web.rest;

import bham.team.domain.AvailiabilityBlock;
import bham.team.repository.AvailiabilityBlockRepository;
import bham.team.service.dto.AvailiabilityBlockDTO;
import bham.team.service.mapper.AvailiabilityBlockMapper;
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
 * REST controller for managing {@link bham.team.domain.AvailiabilityBlock}.
 */
@RestController
@RequestMapping("/api/availiability-blocks")
@Transactional
public class AvailiabilityBlockResource {

    private static final Logger LOG = LoggerFactory.getLogger(AvailiabilityBlockResource.class);

    private static final String ENTITY_NAME = "availiabilityBlock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AvailiabilityBlockRepository availiabilityBlockRepository;

    private final AvailiabilityBlockMapper availiabilityBlockMapper;

    public AvailiabilityBlockResource(
        AvailiabilityBlockRepository availiabilityBlockRepository,
        AvailiabilityBlockMapper availiabilityBlockMapper
    ) {
        this.availiabilityBlockRepository = availiabilityBlockRepository;
        this.availiabilityBlockMapper = availiabilityBlockMapper;
    }

    /**
     * {@code POST  /availiability-blocks} : Create a new availiabilityBlock.
     *
     * @param availiabilityBlockDTO the availiabilityBlockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new availiabilityBlockDTO, or with status {@code 400 (Bad Request)} if the availiabilityBlock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AvailiabilityBlockDTO> createAvailiabilityBlock(@Valid @RequestBody AvailiabilityBlockDTO availiabilityBlockDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AvailiabilityBlock : {}", availiabilityBlockDTO);
        if (availiabilityBlockDTO.getId() != null) {
            throw new BadRequestAlertException("A new availiabilityBlock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AvailiabilityBlock availiabilityBlock = availiabilityBlockMapper.toEntity(availiabilityBlockDTO);
        availiabilityBlock = availiabilityBlockRepository.save(availiabilityBlock);
        availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);
        return ResponseEntity.created(new URI("/api/availiability-blocks/" + availiabilityBlockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, availiabilityBlockDTO.getId().toString()))
            .body(availiabilityBlockDTO);
    }

    /**
     * {@code PUT  /availiability-blocks/:id} : Updates an existing availiabilityBlock.
     *
     * @param id the id of the availiabilityBlockDTO to save.
     * @param availiabilityBlockDTO the availiabilityBlockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated availiabilityBlockDTO,
     * or with status {@code 400 (Bad Request)} if the availiabilityBlockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the availiabilityBlockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AvailiabilityBlockDTO> updateAvailiabilityBlock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AvailiabilityBlockDTO availiabilityBlockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AvailiabilityBlock : {}, {}", id, availiabilityBlockDTO);
        if (availiabilityBlockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, availiabilityBlockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!availiabilityBlockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AvailiabilityBlock availiabilityBlock = availiabilityBlockMapper.toEntity(availiabilityBlockDTO);
        availiabilityBlock = availiabilityBlockRepository.save(availiabilityBlock);
        availiabilityBlockDTO = availiabilityBlockMapper.toDto(availiabilityBlock);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, availiabilityBlockDTO.getId().toString()))
            .body(availiabilityBlockDTO);
    }

    /**
     * {@code PATCH  /availiability-blocks/:id} : Partial updates given fields of an existing availiabilityBlock, field will ignore if it is null
     *
     * @param id the id of the availiabilityBlockDTO to save.
     * @param availiabilityBlockDTO the availiabilityBlockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated availiabilityBlockDTO,
     * or with status {@code 400 (Bad Request)} if the availiabilityBlockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the availiabilityBlockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the availiabilityBlockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AvailiabilityBlockDTO> partialUpdateAvailiabilityBlock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AvailiabilityBlockDTO availiabilityBlockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AvailiabilityBlock partially : {}, {}", id, availiabilityBlockDTO);
        if (availiabilityBlockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, availiabilityBlockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!availiabilityBlockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AvailiabilityBlockDTO> result = availiabilityBlockRepository
            .findById(availiabilityBlockDTO.getId())
            .map(existingAvailiabilityBlock -> {
                availiabilityBlockMapper.partialUpdate(existingAvailiabilityBlock, availiabilityBlockDTO);

                return existingAvailiabilityBlock;
            })
            .map(availiabilityBlockRepository::save)
            .map(availiabilityBlockMapper::toDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, availiabilityBlockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /availiability-blocks} : get all the availiabilityBlocks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of availiabilityBlocks in body.
     */
    @GetMapping("")
    public List<AvailiabilityBlockDTO> getAllAvailiabilityBlocks() {
        LOG.debug("REST request to get all AvailiabilityBlocks");
        List<AvailiabilityBlock> availiabilityBlocks = availiabilityBlockRepository.findAll();
        return availiabilityBlockMapper.toDto(availiabilityBlocks);
    }

    /**
     * {@code GET  /availiability-blocks/:id} : get the "id" availiabilityBlock.
     *
     * @param id the id of the availiabilityBlockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the availiabilityBlockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AvailiabilityBlockDTO> getAvailiabilityBlock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AvailiabilityBlock : {}", id);
        Optional<AvailiabilityBlockDTO> availiabilityBlockDTO = availiabilityBlockRepository
            .findById(id)
            .map(availiabilityBlockMapper::toDto);
        return ResponseUtil.wrapOrNotFound(availiabilityBlockDTO);
    }

    /**
     * {@code DELETE  /availiability-blocks/:id} : delete the "id" availiabilityBlock.
     *
     * @param id the id of the availiabilityBlockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailiabilityBlock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AvailiabilityBlock : {}", id);
        availiabilityBlockRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
