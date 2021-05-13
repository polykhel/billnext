package com.polykhel.billnext.repository

import com.polykhel.billnext.domain.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Category] entity.
 */
@Repository
interface CategoryRepository : JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    @Query("select category from Category category where category.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(pageable: Pageable): Page<Category>
}
