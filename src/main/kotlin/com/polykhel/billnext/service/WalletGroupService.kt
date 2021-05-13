package com.polykhel.billnext.service

import com.polykhel.billnext.service.dto.WalletGroupDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * Service Interface for managing [com.polykhel.billnext.domain.WalletGroup].
 */
interface WalletGroupService {

    /**
     * Save a walletGroup.
     *
     * @param walletGroupDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(walletGroupDTO: WalletGroupDTO): WalletGroupDTO

    /**
     * Partially updates a walletGroup.
     *
     * @param walletGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(walletGroupDTO: WalletGroupDTO): Optional<WalletGroupDTO>

    /**
     * Get all the walletGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<WalletGroupDTO>

    /**
     * Get all the walletGroups by currentUser.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    fun findAllByCurrentUser(pageable: Pageable): Page<WalletGroupDTO>

    /**
     * Get the "id" walletGroup.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<WalletGroupDTO>

    /**
     * Delete the "id" walletGroup.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
