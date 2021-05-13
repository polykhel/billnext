package com.polykhel.billnext.service.mapper

import com.polykhel.billnext.domain.Wallet
import com.polykhel.billnext.service.dto.WalletDTO
import org.mapstruct.*

/**
 * Mapper for the entity [Wallet] and its DTO [WalletDTO].
 */
@Mapper(componentModel = "spring", uses = [WalletGroupMapper::class])
interface WalletMapper :
    EntityMapper<WalletDTO, Wallet> {

    @Mappings(
        Mapping(target = "walletGroup", source = "walletGroup", qualifiedByName = ["id"])
    )
    override fun toDto(s: Wallet): WalletDTO

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoId(wallet: Wallet): WalletDTO
}
