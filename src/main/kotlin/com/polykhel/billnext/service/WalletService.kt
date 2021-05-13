package com.polykhel.billnext.service

import com.polykhel.billnext.service.dto.WalletDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * Service Interface for managing [com.polykhel.billnext.domain.Wallet].
 */
interface WalletService {

    /**
     * Save a wallet.
     *
     * @param walletDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(walletDTO: WalletDTO): WalletDTO

    /**
     * Partially updates a wallet.
     *
     * @param walletDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(walletDTO: WalletDTO): Optional<WalletDTO>

    /**
     * Get all the wallets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<WalletDTO>

    /**
     * Get all the wallets by currentUser.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAllByCurrentUser(pageable: Pageable): Page<WalletDTO>

    /**
     * Get the "id" wallet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<WalletDTO>

    /**
     * Delete the "id" wallet.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
