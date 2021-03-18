package com.polykhel.billnext.repository;

import com.polykhel.billnext.domain.Subcategory;
import com.polykhel.billnext.service.dto.SubcategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Subcategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    Page<SubcategoryDTO> findAllByCategoryId(Long categoryId, Pageable pageable);
}
