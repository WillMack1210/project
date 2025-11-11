package bham.team.web.rest;

import bham.team.domain.ScheduleRequest;
import bham.team.repository.ScheduleRequestRepository;
import bham.team.service.dto.ScheduleRequestDTO;
import bham.team.service.mapper.ScheduleRequestMapper;
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
 * REST controller for managing {@link bham.team.domain.ScheduleRequest}.
 */
@RestController
@RequestMapping("/api/schedule-requests")
@Transactional
public class ScheduleRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleRequestResource.class);

    private static final String ENTITY_NAME = "scheduleRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScheduleRequestRepository scheduleRequestRepository;

    private final ScheduleRequestMapper scheduleRequestMapper;

    public ScheduleRequestResource(ScheduleRequestRepository scheduleRequestRepository, ScheduleRequestMapper scheduleRequestMapper) {
        this.scheduleRequestRepository = scheduleRequestRepository;
        this.scheduleRequestMapper = scheduleRequestMapper;
    }

    /**
     * {@code POST  /schedule-requests} : Create a new scheduleRequest.
     *
     * @param scheduleRequestDTO the scheduleRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduleRequestDTO, or with status {@code 400 (Bad Request)} if the scheduleRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ScheduleRequestDTO> createScheduleRequest(@Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ScheduleRequest : {}", scheduleRequestDTO);
        if (scheduleRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new scheduleRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ScheduleRequest scheduleRequest = scheduleRequestMapper.toEntity(scheduleRequestDTO);
        scheduleRequest = scheduleRequestRepository.save(scheduleRequest);
        scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);
        return ResponseEntity.created(new URI("/api/schedule-requests/" + scheduleRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, scheduleRequestDTO.getId().toString()))
            .body(scheduleRequestDTO);
    }

    /**
     * {@code PUT  /schedule-requests/:id} : Updates an existing scheduleRequest.
     *
     * @param id the id of the scheduleRequestDTO to save.
     * @param scheduleRequestDTO the scheduleRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleRequestDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scheduleRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleRequestDTO> updateScheduleRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ScheduleRequest : {}, {}", id, scheduleRequestDTO);
        if (scheduleRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduleRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ScheduleRequest scheduleRequest = scheduleRequestMapper.toEntity(scheduleRequestDTO);
        scheduleRequest = scheduleRequestRepository.save(scheduleRequest);
        scheduleRequestDTO = scheduleRequestMapper.toDto(scheduleRequest);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, scheduleRequestDTO.getId().toString()))
            .body(scheduleRequestDTO);
    }

    /**
     * {@code PATCH  /schedule-requests/:id} : Partial updates given fields of an existing scheduleRequest, field will ignore if it is null
     *
     * @param id the id of the scheduleRequestDTO to save.
     * @param scheduleRequestDTO the scheduleRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleRequestDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scheduleRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scheduleRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ScheduleRequestDTO> partialUpdateScheduleRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScheduleRequestDTO scheduleRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ScheduleRequest partially : {}, {}", id, scheduleRequestDTO);
        if (scheduleRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduleRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ScheduleRequestDTO> result = scheduleRequestRepository
            .findById(scheduleRequestDTO.getId())
            .map(existingScheduleRequest -> {
                scheduleRequestMapper.partialUpdate(existingScheduleRequest, scheduleRequestDTO);

                return existingScheduleRequest;
            })
            .map(scheduleRequestRepository::save)
            .map(scheduleRequestMapper::toDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, scheduleRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /schedule-requests} : get all the scheduleRequests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scheduleRequests in body.
     */
    @GetMapping("")
    public List<ScheduleRequestDTO> getAllScheduleRequests() {
        LOG.debug("REST request to get all ScheduleRequests");
        List<ScheduleRequest> scheduleRequests = scheduleRequestRepository.findAll();
        return scheduleRequestMapper.toDto(scheduleRequests);
    }

    /**
     * {@code GET  /schedule-requests/:id} : get the "id" scheduleRequest.
     *
     * @param id the id of the scheduleRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduleRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleRequestDTO> getScheduleRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ScheduleRequest : {}", id);
        Optional<ScheduleRequestDTO> scheduleRequestDTO = scheduleRequestRepository.findById(id).map(scheduleRequestMapper::toDto);
        return ResponseUtil.wrapOrNotFound(scheduleRequestDTO);
    }

    /**
     * {@code DELETE  /schedule-requests/:id} : delete the "id" scheduleRequest.
     *
     * @param id the id of the scheduleRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ScheduleRequest : {}", id);
        scheduleRequestRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
