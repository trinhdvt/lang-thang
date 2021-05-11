package com.langthang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicAccountDTO {
    private int accountId;

    private String name;

    private String email;

    private int postCount;

    private int followCount;

    private String avatarLink;

    private String about;

    private String occupation;

    public BasicAccountDTO(int accountId) {
        this.accountId = accountId;
    }
}
