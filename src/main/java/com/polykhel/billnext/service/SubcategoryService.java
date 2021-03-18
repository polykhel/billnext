package com.polykhel.billnext.service;

import com.polykhel.billnext.service.dto.SubcategoryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.polykhel.billnext.domain.Subcategory}.
 */
public interface SubcategoryService {
    /**
     * Save a subcategory.
     *
     * @param subcategoryDTO the entity to save.
     * @return the persisted entity.
     */
    SubcategoryDTO save(SubcategoryDTO subcategoryDTO);

    /**
     * Partially updates a subcategory.
     *
     * @param subcategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SubcategoryDTO> partialUpdate(SubcategoryDTO subcategoryDTO);

    /**
     * Get all the subcategories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubcategoryDTO> findAll(Pageable pageable);

    /**
     * Get the "id" subcategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SubcategoryDTO> findOne(Long id);

    /**
     * Delete the "id" subcategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all subcategories by category id.
     *
     * @param categoryId the id of the parent category.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubcategoryDTO> findAllByCategoryId(Long categoryId, Pageable pageable);
}
