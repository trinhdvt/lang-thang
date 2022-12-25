package com.langthang.mapper;

import com.langthang.model.constraints.Role;
import com.langthang.model.dto.v2.response.UserDtoV2;
import com.langthang.model.entity.Account;
import com.langthang.utils.SecurityUtils;
import lombok.NonNull;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isEnabled", ignore = true)
    UserDtoV2 toDto(Account user);

    @AfterMapping
    default void afterMapping(@MappingTarget @NonNull UserDtoV2 target, Account source) {
        var currentUser = SecurityUtils.currentUser();
        if (currentUser.isEmpty() || !Role.ROLE_ADMIN.equals(currentUser.get().getRole())) {
            return;
        }

        target.setEmail(source.getEmail());
        target.setRole(source.getRole().toString());
        target.setIsEnabled(source.isEnabled());
    }
}
