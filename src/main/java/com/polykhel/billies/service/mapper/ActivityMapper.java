package com.polykhel.billies.service.mapper;

import com.polykhel.billies.domain.*;
import com.polykhel.billies.service.dto.ActivityDTO;
import org.mapstruct.*;

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
