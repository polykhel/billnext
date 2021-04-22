package com.polykhel.billnext.service.impl;

import com.polykhel.billnext.domain.WalletGroup;
import com.polykhel.billnext.repository.WalletGroupRepository;
import com.polykhel.billnext.service.WalletGroupService;
import com.polykhel.billnext.service.dto.WalletGroupDTO;
import com.polykhel.billnext.service.mapper.WalletGroupMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link WalletGroup}.
 */
@Service
@Transactional
public class WalletGroupServiceImpl implements WalletGroupService {

    private final Logger log = LoggerFactory.getLogger(WalletGroupServiceImpl.class);

    private final WalletGroupRepository walletGroupRepository;

    private final WalletGroupMapper walletGroupMapper;

    public WalletGroupServiceImpl(WalletGroupRepository walletGroupRepository, WalletGroupMapper walletGroupMapper) {
        this.walletGroupRepository = walletGroupRepository;
        this.walletGroupMapper = walletGroupMapper;
    }

    @Override
    public WalletGroupDTO save(WalletGroupDTO walletGroupDTO) {
        log.debug("Request to save WalletGroup : {}", walletGroupDTO);
        WalletGroup walletGroup = walletGroupMapper.toEntity(walletGroupDTO);
        walletGroup = walletGroupRepository.save(walletGroup);
        return walletGroupMapper.toDto(walletGroup);
    }

    @Override
    public Optional<WalletGroupDTO> partialUpdate(WalletGroupDTO walletGroupDTO) {
        log.debug("Request to partially update WalletGroup : {}", walletGroupDTO);

        return walletGroupRepository
            .findById(walletGroupDTO.getId())
            .map(
                existingWalletGroup -> {
                    walletGroupMapper.partialUpdate(existingWalletGroup, walletGroupDTO);
                    return existingWalletGroup;
                }
            )
            .map(walletGroupRepository::save)
            .map(walletGroupMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletGroupDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WalletGroups");
        return walletGroupRepository.findAll(pageable).map(walletGroupMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WalletGroupDTO> findOne(Long id) {
        log.debug("Request to get WalletGroup : {}", id);
        return walletGroupRepository.findById(id).map(walletGroupMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete WalletGroup : {}", id);
        walletGroupRepository.deleteById(id);
    }
}
