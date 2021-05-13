package com.polykhel.billnext.service

import com.polykhel.billnext.domain.User_
import com.polykhel.billnext.domain.WalletGroup
import com.polykhel.billnext.domain.WalletGroup_
import com.polykhel.billnext.domain.Wallet_
import com.polykhel.billnext.repository.WalletGroupRepository
import com.polykhel.billnext.service.criteria.WalletGroupCriteria
import com.polykhel.billnext.service.dto.WalletGroupDTO
import com.polykhel.billnext.service.mapper.WalletGroupMapper
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
 * Service for executing complex queries for [WalletGroup] entities in the database.
 * The main input is a [WalletGroupCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [WalletGroupDTO] or a [Page] of [WalletGroupDTO] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class WalletGroupQueryService(
    private val walletGroupRepository: WalletGroupRepository,
    private val walletGroupMapper: WalletGroupMapper
) : QueryService<WalletGroup>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [WalletGroupDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: WalletGroupCriteria?): MutableList<WalletGroupDTO> {
        log.debug("find by criteria : $criteria")
        val specification = createSpecification(criteria)
        return walletGroupMapper.toDto(walletGroupRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [WalletGroupDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: WalletGroupCriteria?, page: Pageable): Page<WalletGroupDTO> {
        log.debug("find by criteria : $criteria, page: $page")
        val specification = createSpecification(criteria)
        return walletGroupRepository.findAll(specification, page)
            .map(walletGroupMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: WalletGroupCriteria?): Long {
        log.debug("count by criteria : $criteria")
        val specification = createSpecification(criteria)
        return walletGroupRepository.count(specification)
    }

    /**
     * Function to convert [WalletGroupCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: WalletGroupCriteria?): Specification<WalletGroup?> {
        var specification: Specification<WalletGroup?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildRangeSpecification(criteria.id, WalletGroup_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, WalletGroup_.name))
            }
            if (criteria.userId != null) {
                specification = specification.and(
                    buildSpecification(criteria.userId as Filter<Long>) {
                        it.join(WalletGroup_.user, JoinType.LEFT).get(User_.id)
                    }
                )
            }
            if (criteria.walletsId != null) {
                specification = specification.and(
                    buildSpecification(criteria.walletsId as Filter<Long>) {
                        it.join(WalletGroup_.wallets, JoinType.LEFT).get(Wallet_.id)
                    }
                )
            }
        }
        return specification
    }
}
