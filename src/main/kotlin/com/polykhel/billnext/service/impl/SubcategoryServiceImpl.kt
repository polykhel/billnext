package com.polykhel.billnext.service.impl

import com.polykhel.billnext.domain.Subcategory
import com.polykhel.billnext.repository.SubcategoryRepository
import com.polykhel.billnext.security.ADMIN
import com.polykhel.billnext.security.userIsCurrentUser
import com.polykhel.billnext.service.SubcategoryService
import com.polykhel.billnext.service.dto.SubcategoryDTO
import com.polykhel.billnext.service.mapper.SubcategoryMapper
import com.polykhel.billnext.web.rest.errors.ForbiddenException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Subcategory].
 */
@Service
@Transactional
class SubcategoryServiceImpl(
    private val subcategoryRepository: SubcategoryRepository,
    private val subcategoryMapper: SubcategoryMapper
) : SubcategoryService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(subcategoryDTO: SubcategoryDTO): SubcategoryDTO {
        log.debug("Request to save Subcategory : $subcategoryDTO")

        if (!userIsCurrentUser(subcategoryDTO.category?.user?.login)) {
            throw ForbiddenException()
        }

        var subcategory = subcategoryMapper.toEntity(subcategoryDTO)
        subcategory = subcategoryRepository.save(subcategory)
        return subcategoryMapper.toDto(subcategory)
    }

    override fun partialUpdate(subcategoryDTO: SubcategoryDTO): Optional<SubcategoryDTO> {
        log.debug("Request to partially update Subcategory : {}", subcategoryDTO)

        val subcategory = subcategoryRepository.findById(subcategoryDTO.id!!)

        if (subcategory.isPresent && !userIsCurrentUser(subcategory.get().category?.user?.login)) {
            throw ForbiddenException()
        }

        return subcategory
            .map {
                subcategoryMapper.partialUpdate(it, subcategoryDTO)
                it
            }
            .map { subcategoryRepository.save(it) }
            .map { subcategoryMapper.toDto(it) }
    }

    @Secured(ADMIN)
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<SubcategoryDTO> {
        log.debug("Request to get all Subcategories")
        return subcategoryRepository.findAll(pageable)
            .map(subcategoryMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findAllByCategory(category: String, pageable: Pageable): Page<SubcategoryDTO> {
        log.debug("Request to get all Subcategories by Category")
        return subcategoryRepository.findByCategoryAndUserIsCurrentUser(category, pageable)
            .map(subcategoryMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<SubcategoryDTO> {
        log.debug("Request to get Subcategory : $id")
        val subcategory = subcategoryRepository.findById(id)

        if (subcategory.isPresent && !userIsCurrentUser(subcategory.get().category?.user?.login)) {
            throw ForbiddenException()
        }

        return subcategory
            .map(subcategoryMapper::toDto)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Subcategory : $id")

        subcategoryRepository.findById(id).ifPresent {
            if (!userIsCurrentUser(it.category?.user?.login)) {
                throw ForbiddenException()
            }

            subcategoryRepository.deleteById(id)
        }

    }
}
