package com.polykhel.billnext.service.mapper;

import com.polykhel.billnext.domain.Wallet;
import com.polykhel.billnext.service.dto.WalletDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Wallet} and its DTO {@link WalletDTO}.
 */
@Mapper(componentModel = "spring", uses = { WalletGroupMapper.class })
public interface WalletMapper extends EntityMapper<WalletDTO, Wallet> {
    @Mapping(target = "walletGroup", source = "walletGroup", qualifiedByName = "id")
    WalletDTO toDto(Wallet s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WalletDTO toDtoId(Wallet wallet);
}
