package com.polykhel.billnext.repository

import com.polykhel.billnext.domain.WalletGroup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [WalletGroup] entity.
 */
@Suppress("unused")
@Repository
interface WalletGroupRepository : JpaRepository<WalletGroup, Long>, JpaSpecificationExecutor<WalletGroup> {

    @Query("select walletGroup from WalletGroup walletGroup where walletGroup.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(pageable: Pageable): Page<WalletGroup>
}
