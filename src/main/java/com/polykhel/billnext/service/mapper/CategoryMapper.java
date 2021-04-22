package com.polykhel.billnext.service.mapper;

import com.polykhel.billnext.domain.Category;
import com.polykhel.billnext.service.dto.CategoryDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    CategoryDTO toDto(Category s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoryDTO toDtoId(Category category);
}
