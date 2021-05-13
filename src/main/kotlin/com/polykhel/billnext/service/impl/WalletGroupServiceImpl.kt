package com.polykhel.billnext.service.impl

import com.polykhel.billnext.domain.WalletGroup
import com.polykhel.billnext.repository.WalletGroupRepository
import com.polykhel.billnext.security.ADMIN
import com.polykhel.billnext.security.userIsCurrentUser
import com.polykhel.billnext.service.WalletGroupService
import com.polykhel.billnext.service.dto.WalletGroupDTO
import com.polykhel.billnext.service.mapper.WalletGroupMapper
import com.polykhel.billnext.web.rest.errors.ForbiddenException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [WalletGroup].
 */
@Service
@Transactional
class WalletGroupServiceImpl(
    private val walletGroupRepository: WalletGroupRepository,
    private val walletGroupMapper: WalletGroupMapper
) : WalletGroupService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(walletGroupDTO: WalletGroupDTO): WalletGroupDTO {
        log.debug("Request to save WalletGroup : $walletGroupDTO")

        if (!userIsCurrentUser(walletGroupDTO.user?.login)) {
            throw ForbiddenException()
        }

        var walletGroup = walletGroupMapper.toEntity(walletGroupDTO)
        walletGroup = walletGroupRepository.save(walletGroup)
        return walletGroupMapper.toDto(walletGroup)
    }

    override fun partialUpdate(walletGroupDTO: WalletGroupDTO): Optional<WalletGroupDTO> {
        log.debug("Request to partially update WalletGroup : {}", walletGroupDTO)

        val walletGroup = walletGroupRepository.findById(walletGroupDTO.id!!)

        if (walletGroup.isPresent && !userIsCurrentUser(walletGroup.get().user?.login)) {
            throw ForbiddenException()
        }

        return walletGroup
            .map {
                walletGroupMapper.partialUpdate(it, walletGroupDTO)
                it
            }
            .map { walletGroupRepository.save(it) }
            .map { walletGroupMapper.toDto(it) }
    }

    @Secured(ADMIN)
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<WalletGroupDTO> {
        log.debug("Request to get all WalletGroups")
        return walletGroupRepository.findAll(pageable)
            .map(walletGroupMapper::toDto)
    }

    override fun findAllByCurrentUser(pageable: Pageable): Page<WalletGroupDTO> {
        log.debug("Request to get all WalletGroups by currentUser")
        return walletGroupRepository.findByUserIsCurrentUser(pageable)
            .map(walletGroupMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<WalletGroupDTO> {
        log.debug("Request to get WalletGroup : $id")
        val walletGroup = walletGroupRepository.findById(id)

        if (walletGroup.isPresent && !userIsCurrentUser(walletGroup.get().user?.login)) {
            throw ForbiddenException()
        }

        return walletGroup
            .map(walletGroupMapper::toDto)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete WalletGroup : $id")

        walletGroupRepository.findById(id).ifPresent{
            if (!userIsCurrentUser(it.user?.login)) {
                throw ForbiddenException()
            }

            walletGroupRepository.deleteById(id)
        }
    }
}
