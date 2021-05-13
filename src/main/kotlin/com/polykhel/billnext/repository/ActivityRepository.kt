package com.polykhel.billnext.repository

import com.polykhel.billnext.domain.Activity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Activity] entity.
 */
@Suppress("unused")
@Repository
interface ActivityRepository : JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {

    @Query("select activity from Activity activity where activity.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(pageable: Pageable): Page<Activity>
}
