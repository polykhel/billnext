package com.polykhel.billnext.repository

import com.polykhel.billnext.domain.Subcategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Subcategory] entity.
 */
@Repository
interface SubcategoryRepository : JpaRepository<Subcategory, Long> {

    @Query("select subcategory from Subcategory subcategory where subcategory.category.user.login = ?#{principal.username}")
    fun findUserIsCurrentUser(pageable: Pageable): Page<Subcategory>

    @Query("select subcategory from Subcategory subcategory where subcategory.category.name = ?1 and subcategory.category.user.login = ?#{principal.username}")
    fun findByCategoryAndUserIsCurrentUser(category: String, pageable: Pageable): Page<Subcategory>
}
