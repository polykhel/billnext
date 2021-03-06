package com.polykhel.billnext.web.rest

import com.polykhel.billnext.repository.SubcategoryRepository
import com.polykhel.billnext.service.SubcategoryService
import com.polykhel.billnext.service.dto.SubcategoryDTO
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

private const val ENTITY_NAME = "subcategory"
/**
 * REST controller for managing [com.polykhel.billnext.domain.Subcategory].
 */
@RestController
@RequestMapping("/api")
class SubcategoryResource(
    private val subcategoryService: SubcategoryService,
    private val subcategoryRepository: SubcategoryRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "subcategory"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /subcategories` : Create a new subcategory.
     *
     * @param subcategoryDTO the subcategoryDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new subcategoryDTO, or with status `400 (Bad Request)` if the subcategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/subcategories")
    fun createSubcategory(@Valid @RequestBody subcategoryDTO: SubcategoryDTO): ResponseEntity<SubcategoryDTO> {
        log.debug("REST request to save Subcategory : $subcategoryDTO")
        if (subcategoryDTO.id != null) {
            throw BadRequestAlertException(
                "A new subcategory cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = subcategoryService.save(subcategoryDTO)
        return ResponseEntity.created(URI("/api/subcategories/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /subcategories/:id} : Updates an existing subcategory.
     *
     * @param id the id of the subcategoryDTO to save.
     * @param subcategoryDTO the subcategoryDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated subcategoryDTO,
     * or with status `400 (Bad Request)` if the subcategoryDTO is not valid,
     * or with status `500 (Internal Server Error)` if the subcategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/subcategories/{id}")
    fun updateSubcategory(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody subcategoryDTO: SubcategoryDTO
    ): ResponseEntity<SubcategoryDTO> {
        log.debug("REST request to update Subcategory : {}, {}", id, subcategoryDTO)
        if (subcategoryDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, subcategoryDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!subcategoryRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = subcategoryService.save(subcategoryDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    subcategoryDTO.id.toString()
                )
            )
            .body(result)
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
    @PatchMapping(value = ["/subcategories/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateSubcategory(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody subcategoryDTO: SubcategoryDTO
    ): ResponseEntity<SubcategoryDTO> {
        log.debug("REST request to partial update Subcategory partially : {}, {}", id, subcategoryDTO)
        if (subcategoryDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, subcategoryDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!subcategoryRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = subcategoryService.partialUpdate(subcategoryDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subcategoryDTO.id.toString())
        )
    }

    /**
     * `GET  /subcategories` : get all the subcategories.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of subcategories in body.
     */
    @GetMapping("/subcategories")
    fun getAllSubcategories(pageable: Pageable): ResponseEntity<List<SubcategoryDTO>> {
        log.debug("REST request to get a page of Subcategories")
        val page = subcategoryService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /subcategories/category` : get all the subcategories by category and currentUser.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of subcategories in body.
     */
    @GetMapping("/subcategories/category")
    fun getAllSubcategoriesByCategoryAndCurrentUser(category: String, pageable: Pageable): ResponseEntity<List<SubcategoryDTO>> {
        log.debug("REST request to get a page of Subcategories by Category and currentUser")
        val page = subcategoryService.findAllByCategory(category, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /subcategories/:id` : get the "id" subcategory.
     *
     * @param id the id of the subcategoryDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the subcategoryDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/subcategories/{id}")
    fun getSubcategory(@PathVariable id: Long): ResponseEntity<SubcategoryDTO> {
        log.debug("REST request to get Subcategory : $id")
        val subcategoryDTO = subcategoryService.findOne(id)
        return ResponseUtil.wrapOrNotFound(subcategoryDTO)
    }
    /**
     *  `DELETE  /subcategories/:id` : delete the "id" subcategory.
     *
     * @param id the id of the subcategoryDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/subcategories/{id}")
    fun deleteSubcategory(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Subcategory : $id")

        subcategoryService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
