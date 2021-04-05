package com.polykhel.billnext.web.rest;

import com.polykhel.billnext.repository.SubcategoryRepository;
import com.polykhel.billnext.service.SubcategoryService;
import com.polykhel.billnext.service.dto.SubcategoryDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.polykhel.billnext.domain.Subcategory}.
 */
@RestController
@RequestMapping("/api")
public class SubcategoryResource {

    private final Logger log = LoggerFactory.getLogger(SubcategoryResource.class);

    private static final String ENTITY_NAME = "subcategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubcategoryService subcategoryService;

    private final SubcategoryRepository subcategoryRepository;

    public SubcategoryResource(SubcategoryService subcategoryService, SubcategoryRepository subcategoryRepository) {
        this.subcategoryService = subcategoryService;
        this.subcategoryRepository = subcategoryRepository;
    }

    /**
     * {@code POST  /subcategories} : Create a new subcategory.
     *
     * @param subcategoryDTO the subcategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subcategoryDTO, or with status {@code 400 (Bad Request)} if the subcategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/subcategories")
    public ResponseEntity<SubcategoryDTO> createSubcategory(@Valid @RequestBody SubcategoryDTO subcategoryDTO) throws URISyntaxException {
        log.debug("REST request to save Subcategory : {}", subcategoryDTO);
        if (subcategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new subcategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SubcategoryDTO result = subcategoryService.save(subcategoryDTO);
        return ResponseEntity
            .created(new URI("/api/subcategories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /subcategories/:id} : Updates an existing subcategory.
     *
     * @param id the id of the subcategoryDTO to save.
     * @param subcategoryDTO the subcategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategoryDTO,
     * or with status {@code 400 (Bad Request)} if the subcategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subcategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/subcategories/{id}")
    public ResponseEntity<SubcategoryDTO> updateSubcategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubcategoryDTO subcategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Subcategory : {}, {}", id, subcategoryDTO);
        if (subcategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subcategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subcategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SubcategoryDTO result = subcategoryService.save(subcategoryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subcategoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /subcategories/:id} : Partial updates given fields of an existing subcategory, field will ignore if it is null
     *
     * @param id the id of the subcategoryDTO to save.
     * @param subcategoryDTO the subcategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategoryDTO,
     * or with status {@code 400 (Bad Request)} if the subcategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subcategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subcategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/subcategories/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<SubcategoryDTO> partialUpdateSubcategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubcategoryDTO subcategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Subcategory partially : {}, {}", id, subcategoryDTO);
        if (subcategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subcategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subcategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubcategoryDTO> result = subcategoryService.partialUpdate(subcategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subcategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /subcategories} : get all the subcategories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subcategories in body.
     */
    @GetMapping("/subcategories")
    public ResponseEntity<List<SubcategoryDTO>> getAllSubcategories(Pageable pageable) {
        log.debug("REST request to get a page of Subcategories");
        Page<SubcategoryDTO> page = subcategoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /subcategories/:id} : get the "id" subcategory.
     *
     * @param id the id of the subcategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subcategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/subcategories/{id}")
    public ResponseEntity<SubcategoryDTO> getSubcategory(@PathVariable Long id) {
        log.debug("REST request to get Subcategory : {}", id);
        Optional<SubcategoryDTO> subcategoryDTO = subcategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subcategoryDTO);
    }

    /**
     * {@code DELETE  /subcategories/:id} : delete the "id" subcategory.
     *
     * @param id the id of the subcategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/subcategories/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable Long id) {
        log.debug("REST request to delete Subcategory : {}", id);
        subcategoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
