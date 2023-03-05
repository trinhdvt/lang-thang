package com.langthang.mapper;

import com.langthang.model.dto.v2.response.NotificationDtoV2;
import com.langthang.model.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, PostMapper.class}
)
public interface NotificationMapper {

    @Mapping(source = "post", target = "targetPost", qualifiedByName = "toReadOnlyDto")
    @Mapping(source = "sourceAccount", target = "sourceUser")
    @Mapping(source = "notifyDate", target = "createdDate")
    NotificationDtoV2 toDto(Notification notification);

}
