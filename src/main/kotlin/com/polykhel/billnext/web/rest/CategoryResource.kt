package com.polykhel.billnext.web.rest

import com.polykhel.billnext.repository.CategoryRepository
import com.polykhel.billnext.service.CategoryQueryService
import com.polykhel.billnext.service.CategoryService
import com.polykhel.billnext.service.criteria.CategoryCriteria
import com.polykhel.billnext.service.dto.CategoryDTO
import com.polykhel.billnext.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "category"
/**
 * REST controller for managing [com.polykhel.billnext.domain.Category].
 */
@RestController
@RequestMapping("/api")
class CategoryResource(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
    private val categoryQueryService: CategoryQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "category"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /categories` : Create a new category.
     *
     * @param categoryDTO the categoryDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new categoryDTO, or with status `400 (Bad Request)` if the category has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/categories")
    fun createCategory(@Valid @RequestBody categoryDTO: CategoryDTO): ResponseEntity<CategoryDTO> {
        log.debug("REST request to save Category : $categoryDTO")
        if (categoryDTO.id != null) {
            throw BadRequestAlertException(
                "A new category cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = categoryService.save(categoryDTO)
        return ResponseEntity.created(URI("/api/categories/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /categories/:id} : Updates an existing category.
     *
     * @param id the id of the categoryDTO to save.
     * @param categoryDTO the categoryDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated categoryDTO,
     * or with status `400 (Bad Request)` if the categoryDTO is not valid,
     * or with status `500 (Internal Server Error)` if the categoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/categories/{id}")
    fun updateCategory(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody categoryDTO: CategoryDTO
    ): ResponseEntity<CategoryDTO> {
        log.debug("REST request to update Category : {}, {}", id, categoryDTO)
        if (categoryDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, categoryDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!categoryRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = categoryService.save(categoryDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    categoryDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /categories/:id} : Partial updates given fields of an existing category, field will ignore if it is null
     *
     * @param id the id of the categoryDTO to save.
     * @param categoryDTO the categoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoryDTO,
     * or with status {@code 400 (Bad Request)} if the categoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the categoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the categoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/categories/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateCategory(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody categoryDTO: CategoryDTO
    ): ResponseEntity<CategoryDTO> {
        log.debug("REST request to partial update Category partially : {}, {}", id, categoryDTO)
        if (categoryDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, categoryDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!categoryRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = categoryService.partialUpdate(categoryDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categoryDTO.id.toString())
        )
    }

    /**
     * `GET  /categories/criteria` : get all the categories by criteria.
     *
     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of categories in body.
     */
    @GetMapping("/categories/criteria") fun getAllCategories(
        criteria: CategoryCriteria,
        pageable: Pageable
    ): ResponseEntity<MutableList<CategoryDTO>> {
        log.debug("REST request to get Categories by criteria: $criteria")
        val page = categoryQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /categories` : get all the categories.
     *
     * @param pageable the pagination information.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of categories in body.
     */
    @GetMapping("/categories") fun getAllCategoriesByCurrentUser(pageable: Pageable): ResponseEntity<List<CategoryDTO>> {
        log.debug("REST request to get Categories by currentUser")
        val page = categoryService.findAllByCurrentUser(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /categories/count}` : count all the categories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/categories/count")
    fun countCategories(criteria: CategoryCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Categories by criteria: $criteria")
        return ResponseEntity.ok().body(categoryQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /categories/:id` : get the "id" category.
     *
     * @param id the id of the categoryDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the categoryDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/categories/{id}")
    fun getCategory(@PathVariable id: Long): ResponseEntity<CategoryDTO> {
        log.debug("REST request to get Category : $id")
        val categoryDTO = categoryService.findOne(id)
        return ResponseUtil.wrapOrNotFound(categoryDTO)
    }
    /**
     *  `DELETE  /categories/:id` : delete the "id" category.
     *
     * @param id the id of the categoryDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/categories/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Category : $id")

        categoryService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
