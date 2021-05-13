package com.polykhel.billnext.service.mapper

import com.polykhel.billnext.domain.Activity
import com.polykhel.billnext.service.dto.ActivityDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [Activity] and its DTO [ActivityDTO].
 */
@Mapper(componentModel = "spring", uses = [UserMapper::class, WalletMapper::class, CategoryMapper::class])
interface ActivityMapper :
    EntityMapper<ActivityDTO, Activity> {

    @Mappings(
        Mapping(target = "user", source = "user", qualifiedByName = ["id"]),
        Mapping(target = "wallet", source = "wallet", qualifiedByName = ["id"]),
        Mapping(target = "category", source = "category", qualifiedByName = ["id"])
    )
    override fun toDto(s: Activity): ActivityDTO
}
