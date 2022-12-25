package com.langthang.model.dto.request;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class AccountInfoDTO {
    @NotNull
    @NotEmpty
    @Size(max = 50, message = "Name's length cannot exceed 50 character")
    private String name;

    @Size(max = 255, message = "Image filename is too long, please rename it")
    private String avatarLink;

    @Size(max = 255, message = "Try to make it shorter")
    private String fbLink;

    @Size(max = 255, message = "Try to make it shorter")
    private String instagramLink;

    private String about;
}