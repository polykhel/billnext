package com.polykhel.billnext.service.mapper;

import com.polykhel.billnext.domain.Activity;
import com.polykhel.billnext.service.dto.ActivityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Activity} and its DTO {@link ActivityDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, WalletMapper.class, CategoryMapper.class })
public interface ActivityMapper extends EntityMapper<ActivityDTO, Activity> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    @Mapping(target = "wallet", source = "wallet", qualifiedByName = "id")
    @Mapping(target = "category", source = "category", qualifiedByName = "id")
    ActivityDTO toDto(Activity s);
}
