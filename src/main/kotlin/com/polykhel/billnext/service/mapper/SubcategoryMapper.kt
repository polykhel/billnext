package com.polykhel.billnext.service.mapper

import com.polykhel.billnext.domain.Subcategory
import com.polykhel.billnext.service.dto.SubcategoryDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [Subcategory] and its DTO [SubcategoryDTO].
 */
@Mapper(componentModel = "spring", uses = [CategoryMapper::class])
interface SubcategoryMapper :
    EntityMapper<SubcategoryDTO, Subcategory> {

    @Mappings(
        Mapping(target = "category", source = "category", qualifiedByName = ["id"])
    )
    override fun toDto(s: Subcategory): SubcategoryDTO
}
