package com.polykhel.billnext.service

import com.polykhel.billnext.domain.*
import com.polykhel.billnext.repository.ActivityRepository
import com.polykhel.billnext.service.criteria.ActivityCriteria
import com.polykhel.billnext.service.dto.ActivityDTO
import com.polykhel.billnext.service.mapper.ActivityMapper
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
 * Service for executing complex queries for [Activity] entities in the database.
 * The main input is a [ActivityCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [ActivityDTO] or a [Page] of [ActivityDTO] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class ActivityQueryService(
    private val activityRepository: ActivityRepository,
    private val activityMapper: ActivityMapper
) : QueryService<Activity>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [ActivityDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ActivityCriteria?): MutableList<ActivityDTO> {
        log.debug("find by criteria : $criteria")
        val specification = createSpecification(criteria)
        return activityMapper.toDto(activityRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [ActivityDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ActivityCriteria?, page: Pageable): Page<ActivityDTO> {
        log.debug("find by criteria : $criteria, page: $page")
        val specification = createSpecification(criteria)
        return activityRepository.findAll(specification, page)
            .map(activityMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: ActivityCriteria?): Long {
        log.debug("count by criteria : $criteria")
        val specification = createSpecification(criteria)
        return activityRepository.count(specification)
    }

    /**
     * Function to convert [ActivityCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: ActivityCriteria?): Specification<Activity?> {
        var specification: Specification<Activity?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildRangeSpecification(criteria.id, Activity_.id))
            }
            if (criteria.date != null) {
                specification = specification.and(buildRangeSpecification(criteria.date, Activity_.date))
            }
            if (criteria.amount != null) {
                specification = specification.and(buildRangeSpecification(criteria.amount, Activity_.amount))
            }
            if (criteria.remarks != null) {
                specification = specification.and(buildStringSpecification(criteria.remarks, Activity_.remarks))
            }
            if (criteria.type != null) {
                specification = specification.and(buildSpecification(criteria.type, Activity_.type))
            }
            if (criteria.userId != null) {
                specification = specification.and(
                    buildSpecification(criteria.userId as Filter<Long>) {
                        it.join(Activity_.user, JoinType.LEFT).get(User_.id)
                    }
                )
            }
            if (criteria.walletId != null) {
                specification = specification.and(
                    buildSpecification(criteria.walletId as Filter<Long>) {
                        it.join(Activity_.wallet, JoinType.LEFT).get(Wallet_.id)
                    }
                )
            }
            if (criteria.categoryId != null) {
                specification = specification.and(
                    buildSpecification(criteria.categoryId as Filter<Long>) {
                        it.join(Activity_.category, JoinType.LEFT).get(Category_.id)
                    }
                )
            }
        }
        return specification
    }
}
