package com.langthang.mapper;

import com.langthang.model.dto.v2.request.PostCreateDto;
import com.langthang.model.dto.v2.response.PostDtoV2;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Post;
import org.mapstruct.*;
import org.springframework.lang.Nullable;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, CategoryMapper.class, PostStatsMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    @Mapping(target = "isPublished", expression = "java(post.isPublished())")
    @Mapping(target = "stats", source = ".")
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
