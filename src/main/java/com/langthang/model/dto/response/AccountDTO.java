package com.langthang.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.langthang.model.constraints.Role;
import com.langthang.model.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
    private int accountId;

    private String name;

    private String email;

    private String slug;

    private long postCount;

    private int followCount;

    private int bookmarkOnOwnPostCount;

    private int commentOnOwnPostCount;

    private String fbLink;

    private String instagramLink;

    private String avatarLink;

    private String about;

    private Role role;

    private boolean isFollowed;

    public static AccountDTO toBasicAccount(Account entity) {
        return AccountDTO.builder()
                .accountId(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole() != Role.ROLE_ADMIN ? null : entity.getRole())
                .slug(entity.getSlug())
                .avatarLink(entity.getAvatarLink())
                .fbLink(entity.getFbLink())
                .instagramLink(entity.getInstagramLink())
                .about(entity.getAbout())
                .build();
    }
}