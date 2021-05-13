package com.polykhel.billnext.repository

import com.polykhel.billnext.domain.Wallet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Wallet] entity.
 */
@Suppress("unused")
@Repository
interface WalletRepository : JpaRepository<Wallet, Long>, JpaSpecificationExecutor<Wallet> {

    @Query("select wallet from Wallet wallet where wallet.walletGroup.user.login = ?#{principal.username}")
    fun findUserIsCurrentUser(pageable: Pageable): Page<Wallet>
}
