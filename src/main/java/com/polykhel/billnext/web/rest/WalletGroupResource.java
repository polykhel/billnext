package com.polykhel.billnext.web.rest;

import com.polykhel.billnext.repository.WalletGroupRepository;
import com.polykhel.billnext.service.WalletGroupService;
import com.polykhel.billnext.service.criteria.WalletGroupCriteria;
import com.polykhel.billnext.service.dto.WalletGroupDTO;
import com.polykhel.billnext.service.query.WalletGroupQueryService;
import com.polykhel.billnext.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.polykhel.billnext.domain.WalletGroup}.
 */
@RestController
@RequestMapping("/api")
public class WalletGroupResource {

    private final Logger log = LoggerFactory.getLogger(WalletGroupResource.class);

    private static final String ENTITY_NAME = "walletGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WalletGroupService walletGroupService;

    private final WalletGroupRepository walletGroupRepository;

    private final WalletGroupQueryService walletGroupQueryService;

    public WalletGroupResource(
        WalletGroupService walletGroupService,
        WalletGroupRepository walletGroupRepository,
        WalletGroupQueryService walletGroupQueryService
    ) {
        this.walletGroupService = walletGroupService;
        this.walletGroupRepository = walletGroupRepository;
        this.walletGroupQueryService = walletGroupQueryService;
    }

    /**
     * {@code POST  /wallet-groups} : Create a new walletGroup.
     *
     * @param walletGroupDTO the walletGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new walletGroupDTO, or with status {@code 400 (Bad Request)} if the walletGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wallet-groups")
    public ResponseEntity<WalletGroupDTO> createWalletGroup(@Valid @RequestBody WalletGroupDTO walletGroupDTO) throws URISyntaxException {
        log.debug("REST request to save WalletGroup : {}", walletGroupDTO);
        if (walletGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new walletGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WalletGroupDTO result = walletGroupService.save(walletGroupDTO);
        return ResponseEntity
            .created(new URI("/api/wallet-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /wallet-groups/:id} : Updates an existing walletGroup.
     *
     * @param id the id of the walletGroupDTO to save.
     * @param walletGroupDTO the walletGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated walletGroupDTO,
     * or with status {@code 400 (Bad Request)} if the walletGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the walletGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wallet-groups/{id}")
    public ResponseEntity<WalletGroupDTO> updateWalletGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WalletGroupDTO walletGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to update WalletGroup : {}, {}", id, walletGroupDTO);
        if (walletGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, walletGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!walletGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WalletGroupDTO result = walletGroupService.save(walletGroupDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, walletGroupDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /wallet-groups/:id} : Partial updates given fields of an existing walletGroup, field will ignore if it is null
     *
     * @param id the id of the walletGroupDTO to save.
     * @param walletGroupDTO the walletGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated walletGroupDTO,
     * or with status {@code 400 (Bad Request)} if the walletGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the walletGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the walletGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/wallet-groups/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<WalletGroupDTO> partialUpdateWalletGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WalletGroupDTO walletGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update WalletGroup partially : {}, {}", id, walletGroupDTO);
        if (walletGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, walletGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!walletGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WalletGroupDTO> result = walletGroupService.partialUpdate(walletGroupDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, walletGroupDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /wallet-groups} : get all the walletGroups.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of walletGroups in body.
     */
    @GetMapping("/wallet-groups")
    public ResponseEntity<List<WalletGroupDTO>> getAllWalletGroups(WalletGroupCriteria criteria, Pageable pageable) {
        log.debug("REST request to get WalletGroups by criteria: {}", criteria);
        Page<WalletGroupDTO> page = walletGroupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /wallet-groups/count} : count all the walletGroups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/wallet-groups/count")
    public ResponseEntity<Long> countWalletGroups(WalletGroupCriteria criteria) {
        log.debug("REST request to count WalletGroups by criteria: {}", criteria);
        return ResponseEntity.ok().body(walletGroupQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /wallet-groups/:id} : get the "id" walletGroup.
     *
     * @param id the id of the walletGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the walletGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/wallet-groups/{id}")
    public ResponseEntity<WalletGroupDTO> getWalletGroup(@PathVariable Long id) {
        log.debug("REST request to get WalletGroup : {}", id);
        Optional<WalletGroupDTO> walletGroupDTO = walletGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(walletGroupDTO);
    }

    /**
     * {@code DELETE  /wallet-groups/:id} : delete the "id" walletGroup.
     *
     * @param id the id of the walletGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/wallet-groups/{id}")
    public ResponseEntity<Void> deleteWalletGroup(@PathVariable Long id) {
        log.debug("REST request to delete WalletGroup : {}", id);
        walletGroupService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
