package com.langthang.mapper;

import com.langthang.model.dto.v2.response.PostStatsDto;
import com.langthang.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostStatsMapper {
    @Mapping(target = "bookmarkedCount",
            expression = "java(post.getBookmarkedPosts().size())")
    @Mapping(target = "commentCount",
            expression = "java(post.getComments().size())")
    PostStatsDto toPostStats(Post post);
}
