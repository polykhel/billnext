package com.polykhel.billnext.service

import com.polykhel.billnext.service.dto.SubcategoryDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * Service Interface for managing [com.polykhel.billnext.domain.Subcategory].
 */
interface SubcategoryService {

    /**
     * Save a subcategory.
     *
     * @param subcategoryDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(subcategoryDTO: SubcategoryDTO): SubcategoryDTO

    /**
     * Partially updates a subcategory.
     *
     * @param subcategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(subcategoryDTO: SubcategoryDTO): Optional<SubcategoryDTO>

    /**
     * Get all the subcategories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<SubcategoryDTO>

    /**
     * Get all the subcategories by category.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAllByCategory(category: String, pageable: Pageable): Page<SubcategoryDTO>

    /**
     * Get the "id" subcategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<SubcategoryDTO>

    /**
     * Delete the "id" subcategory.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
