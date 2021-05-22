package com.langthang.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AccountInfoDTO {
    @NotNull
    @NotEmpty
    private String name;

    private String avatarLink;
    private String fbLink;
    private String instagramLink;
    private String about;
}
