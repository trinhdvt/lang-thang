package com.langthang.model.dto.v2.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
@Data
@AllArgsConstructor
public class UserDtoV2 implements Serializable {
    private Long id;
    private String name;
    private String avatarLink;
    private String about;
    private String fbLink;
    private String instagramLink;
    private String slug;
    private Boolean isEnabled;
    private String role;
    private String email;
}
