package com.polykhel.billnext.service.mapper

import com.polykhel.billnext.domain.Category
import com.polykhel.billnext.service.dto.CategoryDTO
import org.mapstruct.*

/**
 * Mapper for the entity [Category] and its DTO [CategoryDTO].
 */
@Mapper(componentModel = "spring", uses = [UserMapper::class])
interface CategoryMapper :
    EntityMapper<CategoryDTO, Category> {

    @Mappings(
        Mapping(target = "user", source = "user", qualifiedByName = ["id"])
    )
    override fun toDto(s: Category): CategoryDTO

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoId(category: Category): CategoryDTO
}
