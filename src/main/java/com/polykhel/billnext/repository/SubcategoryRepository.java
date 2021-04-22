package com.polykhel.billnext.repository;

import com.polykhel.billnext.domain.Subcategory;
import com.polykhel.billnext.service.dto.SubcategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Subcategory entity.
 */
@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long>, JpaSpecificationExecutor<Subcategory> {}
