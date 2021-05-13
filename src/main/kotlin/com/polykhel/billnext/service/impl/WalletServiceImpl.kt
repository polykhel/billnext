package com.polykhel.billnext.service.impl

import com.polykhel.billnext.domain.Wallet
import com.polykhel.billnext.repository.WalletRepository
import com.polykhel.billnext.security.ADMIN
import com.polykhel.billnext.security.userIsCurrentUser
import com.polykhel.billnext.service.WalletService
import com.polykhel.billnext.service.dto.WalletDTO
import com.polykhel.billnext.service.mapper.WalletMapper
import com.polykhel.billnext.web.rest.errors.ForbiddenException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Wallet].
 */
@Service
@Transactional
class WalletServiceImpl(
    private val walletRepository: WalletRepository,
    private val walletMapper: WalletMapper
) : WalletService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(walletDTO: WalletDTO): WalletDTO {
        log.debug("Request to save Wallet : $walletDTO")

        if (!userIsCurrentUser(walletDTO.walletGroup?.user?.login)) {
            throw ForbiddenException()
        }

        var wallet = walletMapper.toEntity(walletDTO)
        wallet = walletRepository.save(wallet)
        return walletMapper.toDto(wallet)
    }

    override fun partialUpdate(walletDTO: WalletDTO): Optional<WalletDTO> {
        log.debug("Request to partially update Wallet : {}", walletDTO)

        val wallet = walletRepository.findById(walletDTO.id!!)

        if (!userIsCurrentUser(walletDTO.walletGroup?.user?.login)) {
            throw ForbiddenException()
        }

        return wallet
            .map {
                walletMapper.partialUpdate(it, walletDTO)
                it
            }
            .map { walletRepository.save(it) }
            .map { walletMapper.toDto(it) }
    }

    @Secured(ADMIN)
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<WalletDTO> {
        log.debug("Request to get all Wallets")
        return walletRepository.findAll(pageable)
            .map(walletMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findAllByCurrentUser(pageable: Pageable): Page<WalletDTO> {
        log.debug("Request to get all Wallets by currentUser")
        return walletRepository.findUserIsCurrentUser(pageable)
            .map(walletMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<WalletDTO> {
        log.debug("Request to get Wallet : $id")
        val wallet = walletRepository.findById(id)

        if (wallet.isPresent && !userIsCurrentUser(wallet.get().walletGroup?.user?.login)) {
            throw ForbiddenException()
        }

        return wallet
            .map(walletMapper::toDto)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Wallet : $id")

        walletRepository.findById(id).ifPresent{
            if (!userIsCurrentUser(it.walletGroup?.user?.login)) {
                throw ForbiddenException()
            }

            walletRepository.deleteById(id)
        }
    }
}
