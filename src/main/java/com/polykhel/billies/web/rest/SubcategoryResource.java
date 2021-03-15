package com.polykhel.billies.web.rest;

import com.polykhel.billies.service.SubcategoryService;
import com.polykhel.billies.service.dto.SubcategoryDTO;
import com.polykhel.billies.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
 * REST controller for managing {@link com.polykhel.billies.domain.Subcategory}.
 */
@RestController
@RequestMapping("/api")
public class SubcategoryResource {

    private final Logger log = LoggerFactory.getLogger(SubcategoryResource.class);

    private static final String ENTITY_NAME = "subcategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubcategoryService subcategoryService;

    public SubcategoryResource(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
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
     * {@code PUT  /subcategories} : Updates an existing subcategory.
     *
     * @param subcategoryDTO the subcategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategoryDTO,
     * or with status {@code 400 (Bad Request)} if the subcategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subcategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/subcategories")
    public ResponseEntity<SubcategoryDTO> updateSubcategory(@Valid @RequestBody SubcategoryDTO subcategoryDTO) throws URISyntaxException {
        log.debug("REST request to update Subcategory : {}", subcategoryDTO);
        if (subcategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SubcategoryDTO result = subcategoryService.save(subcategoryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subcategoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /subcategories} : Updates given fields of an existing subcategory.
     *
     * @param subcategoryDTO the subcategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategoryDTO,
     * or with status {@code 400 (Bad Request)} if the subcategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subcategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subcategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/subcategories", consumes = "application/merge-patch+json")
    public ResponseEntity<SubcategoryDTO> partialUpdateSubcategory(@NotNull @RequestBody SubcategoryDTO subcategoryDTO)
        throws URISyntaxException {
        log.debug("REST request to update Subcategory partially : {}", subcategoryDTO);
        if (subcategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
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

    /**
     * {@code GET  /subcategories/category/:categoryId} : get subcategories by category id.
     *
     * @param categoryId the id of the parent category.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200(OK)} and the list of subcategories in body.
     */
    @GetMapping("/subcategories/category/{categoryId}")
    public ResponseEntity<List<SubcategoryDTO>> getAllSubcategoriesByCategoryId(@PathVariable Long categoryId, Pageable pageable) {
        log.debug("REST request to get a page of Subcategories by Category : {}", categoryId);
        Page<SubcategoryDTO> page = subcategoryService.findAllByCategoryId(categoryId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
