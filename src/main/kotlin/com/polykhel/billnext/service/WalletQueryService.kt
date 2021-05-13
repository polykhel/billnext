package com.polykhel.billnext.service

import com.polykhel.billnext.domain.Activity_
import com.polykhel.billnext.domain.Wallet
import com.polykhel.billnext.domain.WalletGroup_
import com.polykhel.billnext.domain.Wallet_
import com.polykhel.billnext.repository.WalletRepository
import com.polykhel.billnext.service.criteria.WalletCriteria
import com.polykhel.billnext.service.dto.WalletDTO
import com.polykhel.billnext.service.mapper.WalletMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.jhipster.service.QueryService
import tech.jhipster.service.filter.Filter
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [Wallet] entities in the database.
 * The main input is a [WalletCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [WalletDTO] or a [Page] of [WalletDTO] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class WalletQueryService(
    private val walletRepository: WalletRepository,
    private val walletMapper: WalletMapper
) : QueryService<Wallet>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [WalletDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: WalletCriteria?): MutableList<WalletDTO> {
        log.debug("find by criteria : $criteria")
        val specification = createSpecification(criteria)
        return walletMapper.toDto(walletRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [WalletDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: WalletCriteria?, page: Pageable): Page<WalletDTO> {
        log.debug("find by criteria : $criteria, page: $page")
        val specification = createSpecification(criteria)
        return walletRepository.findAll(specification, page)
            .map(walletMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: WalletCriteria?): Long {
        log.debug("count by criteria : $criteria")
        val specification = createSpecification(criteria)
        return walletRepository.count(specification)
    }

    /**
     * Function to convert [WalletCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: WalletCriteria?): Specification<Wallet?> {
        var specification: Specification<Wallet?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildRangeSpecification(criteria.id, Wallet_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Wallet_.name))
            }
            if (criteria.amount != null) {
                specification = specification.and(buildRangeSpecification(criteria.amount, Wallet_.amount))
            }
            if (criteria.currency != null) {
                specification = specification.and(buildStringSpecification(criteria.currency, Wallet_.currency))
            }
            if (criteria.remarks != null) {
                specification = specification.and(buildStringSpecification(criteria.remarks, Wallet_.remarks))
            }
            if (criteria.walletGroupId != null) {
                specification = specification.and(
                    buildSpecification(criteria.walletGroupId as Filter<Long>) {
                        it.join(Wallet_.walletGroup, JoinType.LEFT).get(WalletGroup_.id)
                    }
                )
            }
            if (criteria.activityId != null) {
                specification = specification.and(
                    buildSpecification(criteria.activityId as Filter<Long>) {
                        it.join(Wallet_.activities, JoinType.LEFT).get(Activity_.id)
                    }
                )
            }
        }
        return specification
    }
}
