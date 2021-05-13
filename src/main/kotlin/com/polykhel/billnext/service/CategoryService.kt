package com.polykhel.billnext.service

import com.polykhel.billnext.service.dto.CategoryDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * Service Interface for managing [com.polykhel.billnext.domain.Category].
 */
interface CategoryService {

    /**
     * Save a category.
     *
     * @param categoryDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(categoryDTO: CategoryDTO): CategoryDTO

    /**
     * Partially updates a category.
     *
     * @param categoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(categoryDTO: CategoryDTO): Optional<CategoryDTO>

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<CategoryDTO>

    /**
     * Get all the categories by currentUser.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAllByCurrentUser(pageable: Pageable): Page<CategoryDTO>

    /**
     * Get the "id" category.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<CategoryDTO>

    /**
     * Delete the "id" category.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
