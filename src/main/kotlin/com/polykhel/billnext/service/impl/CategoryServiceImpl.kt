package com.polykhel.billnext.service.impl

import com.polykhel.billnext.domain.Category
import com.polykhel.billnext.repository.CategoryRepository
import com.polykhel.billnext.security.ADMIN
import com.polykhel.billnext.security.userIsCurrentUser
import com.polykhel.billnext.service.CategoryService
import com.polykhel.billnext.service.dto.CategoryDTO
import com.polykhel.billnext.service.mapper.CategoryMapper
import com.polykhel.billnext.web.rest.errors.ForbiddenException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Category].
 */
@Service
@Transactional
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper
) : CategoryService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(categoryDTO: CategoryDTO): CategoryDTO {
        log.debug("Request to save Category : $categoryDTO")

        if (!userIsCurrentUser(categoryDTO.user?.login)) {
            throw ForbiddenException()
        }

        var category = categoryMapper.toEntity(categoryDTO)
        category = categoryRepository.save(category)
        return categoryMapper.toDto(category)
    }

    override fun partialUpdate(categoryDTO: CategoryDTO): Optional<CategoryDTO> {
        log.debug("Request to partially update Category : {}", categoryDTO)

        val category = categoryRepository.findById(categoryDTO.id!!)

        if (category.isPresent && !userIsCurrentUser(category.get().user?.login)) {
            throw ForbiddenException()
        }

        return category
            .map {
                categoryMapper.partialUpdate(it, categoryDTO)
                it
            }
            .map { categoryRepository.save(it) }
            .map { categoryMapper.toDto(it) }
    }

    @Secured(ADMIN)
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<CategoryDTO> {
        log.debug("Request to get all Categories")
        return categoryRepository.findAll(pageable)
            .map(categoryMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findAllByCurrentUser(pageable: Pageable): Page<CategoryDTO> {
        log.debug("Request to get all Categories by currentUser")
        return categoryRepository.findByUserIsCurrentUser(pageable)
            .map(categoryMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<CategoryDTO> {
        log.debug("Request to get Category : $id")
        val category = categoryRepository.findById(id)

        if (category.isPresent && !userIsCurrentUser(category.get().user?.login)) {
            throw ForbiddenException()
        }

        return category
            .map(categoryMapper::toDto)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Category : $id")

        categoryRepository.findById(id).ifPresent{
            if (!userIsCurrentUser(it.user?.login)) {
                throw ForbiddenException()
            }

            categoryRepository.deleteById(id)
        }
    }
}
