package com.langthang.model.dto.response.v2;

import com.langthang.model.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDtoV2 postToPostDtoV2(Post post);

}
