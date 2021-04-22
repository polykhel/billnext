package com.polykhel.billnext.service.impl;

import com.polykhel.billnext.domain.Subcategory;
import com.polykhel.billnext.repository.SubcategoryRepository;
import com.polykhel.billnext.service.SubcategoryService;
import com.polykhel.billnext.service.dto.SubcategoryDTO;
import com.polykhel.billnext.service.mapper.SubcategoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Subcategory}.
 */
@Service
@Transactional
public class SubcategoryServiceImpl implements SubcategoryService {

    private final Logger log = LoggerFactory.getLogger(SubcategoryServiceImpl.class);

    private final SubcategoryRepository subcategoryRepository;

    private final SubcategoryMapper subcategoryMapper;

    public SubcategoryServiceImpl(SubcategoryRepository subcategoryRepository, SubcategoryMapper subcategoryMapper) {
        this.subcategoryRepository = subcategoryRepository;
        this.subcategoryMapper = subcategoryMapper;
    }

    @Override
    public SubcategoryDTO save(SubcategoryDTO subcategoryDTO) {
        log.debug("Request to save Subcategory : {}", subcategoryDTO);
        Subcategory subcategory = subcategoryMapper.toEntity(subcategoryDTO);
        subcategory = subcategoryRepository.save(subcategory);
        return subcategoryMapper.toDto(subcategory);
    }

    @Override
    public Optional<SubcategoryDTO> partialUpdate(SubcategoryDTO subcategoryDTO) {
        log.debug("Request to partially update Subcategory : {}", subcategoryDTO);

        return subcategoryRepository
            .findById(subcategoryDTO.getId())
            .map(
                existingSubcategory -> {
                    subcategoryMapper.partialUpdate(existingSubcategory, subcategoryDTO);
                    return existingSubcategory;
                }
            )
            .map(subcategoryRepository::save)
            .map(subcategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubcategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Subcategories");
        return subcategoryRepository.findAll(pageable).map(subcategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubcategoryDTO> findOne(Long id) {
        log.debug("Request to get Subcategory : {}", id);
        return subcategoryRepository.findById(id).map(subcategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Subcategory : {}", id);
        subcategoryRepository.deleteById(id);
    }
}
