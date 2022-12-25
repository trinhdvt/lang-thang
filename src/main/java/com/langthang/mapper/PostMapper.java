package com.langthang.mapper;

import com.langthang.model.dto.v2.request.PostCreateDto;
import com.langthang.model.dto.v2.response.PostDtoV2;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Post;
import org.mapstruct.*;
import org.springframework.lang.Nullable;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    @Mapping(target = "isPublished", expression = "java(post.isPublished())")
    @Mapping(target = "stats.bookmarkedCount",
            expression = "java(post.getBookmarkedPosts().size())")
    @Mapping(target = "stats.commentCount",
            expression = "java(post.getComments().size())")
    PostDtoV2 toDto(Post post);

    @Mapping(target = "stats", ignore = true)
    @Mapping(target = "isPublished", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "categories", ignore = true)
    PostDtoV2 toReadOnlyDto(Post post);

    @InheritInverseConfiguration
    Post updateFromDto(@MappingTarget Post target, PostCreateDto createDto);

    @InheritInverseConfiguration
    @Mapping(target = "author", expression = "java(author)")
    @Mapping(target = "published", expression = "java(isPublished)")
    @Nullable
    Post createFromDto(PostCreateDto createDto, @Context Account author, @Context boolean isPublished);

}
