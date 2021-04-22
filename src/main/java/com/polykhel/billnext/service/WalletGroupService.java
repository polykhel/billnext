package com.polykhel.billnext.service;

import com.polykhel.billnext.service.dto.WalletGroupDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.polykhel.billnext.domain.WalletGroup}.
 */
public interface WalletGroupService {
    /**
     * Save a walletGroup.
     *
     * @param walletGroupDTO the entity to save.
     * @return the persisted entity.
     */
    WalletGroupDTO save(WalletGroupDTO walletGroupDTO);

    /**
     * Partially updates a walletGroup.
     *
     * @param walletGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WalletGroupDTO> partialUpdate(WalletGroupDTO walletGroupDTO);

    /**
     * Get all the walletGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<WalletGroupDTO> findAll(Pageable pageable);

    /**
     * Get the "id" walletGroup.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WalletGroupDTO> findOne(Long id);

    /**
     * Delete the "id" walletGroup.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
