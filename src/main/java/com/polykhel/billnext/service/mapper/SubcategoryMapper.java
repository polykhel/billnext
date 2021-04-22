package com.polykhel.billnext.service.mapper;

import com.polykhel.billnext.domain.Subcategory;
import com.polykhel.billnext.service.dto.SubcategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Subcategory} and its DTO {@link SubcategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface SubcategoryMapper extends EntityMapper<SubcategoryDTO, Subcategory> {
    @Mapping(target = "category", source = "category", qualifiedByName = "id")
    SubcategoryDTO toDto(Subcategory s);
}
