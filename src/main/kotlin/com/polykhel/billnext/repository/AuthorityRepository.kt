package com.polykhel.billnext.repository

import com.polykhel.billnext.domain.Authority
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring Data JPA repository for the [Authority] entity.
 */

interface AuthorityRepository : JpaRepository<Authority, String>
