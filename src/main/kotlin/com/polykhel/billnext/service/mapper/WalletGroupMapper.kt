package com.polykhel.billnext.service.mapper

import com.polykhel.billnext.domain.WalletGroup
import com.polykhel.billnext.service.dto.WalletGroupDTO
import org.mapstruct.*

/**
 * Mapper for the entity [WalletGroup] and its DTO [WalletGroupDTO].
 */
@Mapper(componentModel = "spring", uses = [UserMapper::class])
interface WalletGroupMapper :
    EntityMapper<WalletGroupDTO, WalletGroup> {

    @Mappings(
        Mapping(target = "user", source = "user", qualifiedByName = ["id"])
    )
    override fun toDto(s: WalletGroup): WalletGroupDTO

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoId(walletGroup: WalletGroup): WalletGroupDTO
}
