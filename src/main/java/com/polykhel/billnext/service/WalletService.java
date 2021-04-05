package com.polykhel.billnext.service;

import com.polykhel.billnext.service.dto.WalletDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.polykhel.billnext.domain.Wallet}.
 */
public interface WalletService {
    /**
     * Save a wallet.
     *
     * @param walletDTO the entity to save.
     * @return the persisted entity.
     */
    WalletDTO save(WalletDTO walletDTO);

    /**
     * Partially updates a wallet.
     *
     * @param walletDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WalletDTO> partialUpdate(WalletDTO walletDTO);

    /**
     * Get all the wallets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<WalletDTO> findAll(Pageable pageable);

    /**
     * Get the "id" wallet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WalletDTO> findOne(Long id);

    /**
     * Delete the "id" wallet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
