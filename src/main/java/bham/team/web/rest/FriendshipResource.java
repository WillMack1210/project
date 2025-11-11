package bham.team.web.rest;

import bham.team.domain.Friendship;
import bham.team.repository.FriendshipRepository;
import bham.team.service.dto.FriendshipDTO;
import bham.team.service.mapper.FriendshipMapper;
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
 * REST controller for managing {@link bham.team.domain.Friendship}.
 */
@RestController
@RequestMapping("/api/friendships")
@Transactional
public class FriendshipResource {

    private static final Logger LOG = LoggerFactory.getLogger(FriendshipResource.class);

    private static final String ENTITY_NAME = "friendship";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FriendshipRepository friendshipRepository;

    private final FriendshipMapper friendshipMapper;

    public FriendshipResource(FriendshipRepository friendshipRepository, FriendshipMapper friendshipMapper) {
        this.friendshipRepository = friendshipRepository;
        this.friendshipMapper = friendshipMapper;
    }

    /**
     * {@code POST  /friendships} : Create a new friendship.
     *
     * @param friendshipDTO the friendshipDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new friendshipDTO, or with status {@code 400 (Bad Request)} if the friendship has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FriendshipDTO> createFriendship(@Valid @RequestBody FriendshipDTO friendshipDTO) throws URISyntaxException {
        LOG.debug("REST request to save Friendship : {}", friendshipDTO);
        if (friendshipDTO.getId() != null) {
            throw new BadRequestAlertException("A new friendship cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Friendship friendship = friendshipMapper.toEntity(friendshipDTO);
        friendship = friendshipRepository.save(friendship);
        friendshipDTO = friendshipMapper.toDto(friendship);
        return ResponseEntity.created(new URI("/api/friendships/" + friendshipDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, friendshipDTO.getId().toString()))
            .body(friendshipDTO);
    }

    /**
     * {@code PUT  /friendships/:id} : Updates an existing friendship.
     *
     * @param id the id of the friendshipDTO to save.
     * @param friendshipDTO the friendshipDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated friendshipDTO,
     * or with status {@code 400 (Bad Request)} if the friendshipDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the friendshipDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FriendshipDTO> updateFriendship(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FriendshipDTO friendshipDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Friendship : {}, {}", id, friendshipDTO);
        if (friendshipDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, friendshipDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!friendshipRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Friendship friendship = friendshipMapper.toEntity(friendshipDTO);
        friendship = friendshipRepository.save(friendship);
        friendshipDTO = friendshipMapper.toDto(friendship);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, friendshipDTO.getId().toString()))
            .body(friendshipDTO);
    }

    /**
     * {@code PATCH  /friendships/:id} : Partial updates given fields of an existing friendship, field will ignore if it is null
     *
     * @param id the id of the friendshipDTO to save.
     * @param friendshipDTO the friendshipDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated friendshipDTO,
     * or with status {@code 400 (Bad Request)} if the friendshipDTO is not valid,
     * or with status {@code 404 (Not Found)} if the friendshipDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the friendshipDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FriendshipDTO> partialUpdateFriendship(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FriendshipDTO friendshipDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Friendship partially : {}, {}", id, friendshipDTO);
        if (friendshipDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, friendshipDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!friendshipRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FriendshipDTO> result = friendshipRepository
            .findById(friendshipDTO.getId())
            .map(existingFriendship -> {
                friendshipMapper.partialUpdate(existingFriendship, friendshipDTO);

                return existingFriendship;
            })
            .map(friendshipRepository::save)
            .map(friendshipMapper::toDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, friendshipDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /friendships} : get all the friendships.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of friendships in body.
     */
    @GetMapping("")
    public List<FriendshipDTO> getAllFriendships() {
        LOG.debug("REST request to get all Friendships");
        List<Friendship> friendships = friendshipRepository.findAll();
        return friendshipMapper.toDto(friendships);
    }

    /**
     * {@code GET  /friendships/:id} : get the "id" friendship.
     *
     * @param id the id of the friendshipDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the friendshipDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FriendshipDTO> getFriendship(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Friendship : {}", id);
        Optional<FriendshipDTO> friendshipDTO = friendshipRepository.findById(id).map(friendshipMapper::toDto);
        return ResponseUtil.wrapOrNotFound(friendshipDTO);
    }

    /**
     * {@code DELETE  /friendships/:id} : delete the "id" friendship.
     *
     * @param id the id of the friendshipDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Friendship : {}", id);
        friendshipRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
