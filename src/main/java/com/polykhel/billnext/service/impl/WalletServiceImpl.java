package com.polykhel.billnext.service.impl;

import static com.polykhel.billnext.util.StreamUtils.peek;

import com.polykhel.billnext.domain.Wallet;
import com.polykhel.billnext.repository.WalletRepository;
import com.polykhel.billnext.security.SecurityUtils;
import com.polykhel.billnext.service.WalletService;
import com.polykhel.billnext.service.dto.WalletDTO;
import com.polykhel.billnext.service.mapper.WalletMapper;
import com.polykhel.billnext.util.StreamUtils;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Wallet}.
 */
@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    public WalletServiceImpl(WalletRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public WalletDTO save(WalletDTO walletDTO) {
        log.debug("Request to save Wallet : {}", walletDTO);
        Wallet wallet = walletMapper.toEntity(walletDTO);
        SecurityUtils.validateIfCurrentUser(wallet.getWalletGroup().getUser());
        wallet = walletRepository.save(wallet);
        return walletMapper.toDto(wallet);
    }

    @Override
    public Optional<WalletDTO> partialUpdate(WalletDTO walletDTO) {
        log.debug("Request to partially update Wallet : {}", walletDTO);

        return walletRepository
            .findById(walletDTO.getId())
            .map(peek(existingWallet -> SecurityUtils.validateIfCurrentUser(existingWallet.getWalletGroup().getUser())))
            .map(
                existingWallet -> {
                    walletMapper.partialUpdate(existingWallet, walletDTO);
                    return existingWallet;
                }
            )
            .map(walletRepository::save)
            .map(walletMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Wallets");
        return walletRepository.findAll(pageable).map(walletMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WalletDTO> findOne(Long id) {
        log.debug("Request to get Wallet : {}", id);
        return walletRepository
            .findById(id)
            .map(peek(existingWallet -> SecurityUtils.validateIfCurrentUser(existingWallet.getWalletGroup().getUser())))
            .map(walletMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Wallet : {}", id);
        walletRepository
            .findById(id)
            .ifPresent(
                existingWallet -> {
                    SecurityUtils.validateIfCurrentUser(existingWallet.getWalletGroup().getUser());
                    walletRepository.deleteById(id);
                }
            );
    }
}
