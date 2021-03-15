package com.polykhel.billies.repository;

import com.polykhel.billies.domain.Subcategory;
import com.polykhel.billies.service.dto.SubcategoryDTO;
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
