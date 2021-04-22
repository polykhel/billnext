package com.polykhel.billnext.service.mapper;

import com.polykhel.billnext.domain.WalletGroup;
import com.polykhel.billnext.service.dto.WalletGroupDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link WalletGroup} and its DTO {@link WalletGroupDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface WalletGroupMapper extends EntityMapper<WalletGroupDTO, WalletGroup> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    WalletGroupDTO toDto(WalletGroup s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WalletGroupDTO toDtoId(WalletGroup walletGroup);
}
