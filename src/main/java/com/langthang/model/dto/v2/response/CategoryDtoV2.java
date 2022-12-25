package com.langthang.model.dto.v2.response;

import java.io.Serializable;

public record CategoryDtoV2(
        Integer id,
        String name,
        String slug
) implements Serializable {
}
