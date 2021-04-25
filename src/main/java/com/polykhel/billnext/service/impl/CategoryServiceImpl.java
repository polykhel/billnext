package com.polykhel.billnext.service.impl;

import static com.polykhel.billnext.util.StreamUtils.peek;

import com.polykhel.billnext.domain.Category;
import com.polykhel.billnext.repository.CategoryRepository;
import com.polykhel.billnext.security.SecurityUtils;
import com.polykhel.billnext.service.CategoryService;
import com.polykhel.billnext.service.dto.CategoryDTO;
import com.polykhel.billnext.service.mapper.CategoryMapper;
import com.polykhel.billnext.util.StreamUtils;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Category}.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDTO save(CategoryDTO categoryDTO) {
        log.debug("Request to save Category : {}", categoryDTO);
        Category category = categoryMapper.toEntity(categoryDTO);
        SecurityUtils.validateIfCurrentUser(category.getUser());
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public Optional<CategoryDTO> partialUpdate(CategoryDTO categoryDTO) {
        log.debug("Request to partially update Category : {}", categoryDTO);

        return categoryRepository
            .findById(categoryDTO.getId())
            .map(peek(existingCategory -> SecurityUtils.validateIfCurrentUser(existingCategory.getUser())))
            .map(
                existingCategory -> {
                    categoryMapper.partialUpdate(existingCategory, categoryDTO);
                    return existingCategory;
                }
            )
            .map(categoryRepository::save)
            .map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        return categoryRepository
            .findById(id)
            .map(peek(existingCategory -> SecurityUtils.validateIfCurrentUser(existingCategory.getUser())))
            .map(categoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository
            .findById(id)
            .ifPresent(
                existingCategory -> {
                    SecurityUtils.validateIfCurrentUser(existingCategory.getUser());
                    categoryRepository.deleteById(id);
                }
            );
    }
}
