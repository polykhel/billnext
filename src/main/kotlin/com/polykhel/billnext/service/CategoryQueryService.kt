package com.polykhel.billnext.service

import com.polykhel.billnext.domain.Activity_
import com.polykhel.billnext.domain.Category
import com.polykhel.billnext.domain.Category_
import com.polykhel.billnext.domain.User_
import com.polykhel.billnext.repository.CategoryRepository
import com.polykhel.billnext.service.criteria.CategoryCriteria
import com.polykhel.billnext.service.dto.CategoryDTO
import com.polykhel.billnext.service.mapper.CategoryMapper
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
 * Service for executing complex queries for [Category] entities in the database.
 * The main input is a [CategoryCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [CategoryDTO] or a [Page] of [CategoryDTO] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class CategoryQueryService(
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper
) : QueryService<Category>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [CategoryDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CategoryCriteria?): MutableList<CategoryDTO> {
        log.debug("find by criteria : $criteria")
        val specification = createSpecification(criteria)
        return categoryMapper.toDto(categoryRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [CategoryDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CategoryCriteria?, page: Pageable): Page<CategoryDTO> {
        log.debug("find by criteria : $criteria, page: $page")
        val specification = createSpecification(criteria)
        return categoryRepository.findAll(specification, page)
            .map(categoryMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: CategoryCriteria?): Long {
        log.debug("count by criteria : $criteria")
        val specification = createSpecification(criteria)
        return categoryRepository.count(specification)
    }

    /**
     * Function to convert [CategoryCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: CategoryCriteria?): Specification<Category?> {
        var specification: Specification<Category?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildRangeSpecification(criteria.id, Category_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Category_.name))
            }
            if (criteria.type != null) {
                specification = specification.and(buildSpecification(criteria.type, Category_.type))
            }
            if (criteria.userId != null) {
                specification = specification.and(
                    buildSpecification(criteria.userId as Filter<Long>) {
                        it.join(Category_.user, JoinType.LEFT).get(User_.id)
                    }
                )
            }
            if (criteria.activityId != null) {
                specification = specification.and(
                    buildSpecification(criteria.activityId as Filter<Long>) {
                        it.join(Category_.activities, JoinType.LEFT).get(Activity_.id)
                    }
                )
            }
        }
        return specification
    }
}
