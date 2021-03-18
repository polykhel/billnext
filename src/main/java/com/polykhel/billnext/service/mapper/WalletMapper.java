package com.polykhel.billnext.service.mapper;

import com.polykhel.billnext.domain.*;
import com.polykhel.billnext.service.dto.WalletDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Wallet} and its DTO {@link WalletDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface WalletMapper extends EntityMapper<WalletDTO, Wallet> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    WalletDTO toDto(Wallet s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WalletDTO toDtoId(Wallet wallet);
}
