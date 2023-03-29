package com.langthang.mapper;

import com.langthang.model.dto.v2.response.PostStatsDto;
import com.langthang.model.entity.Post;
import com.langthang.repository.BookmarkedPostRepo;
import com.langthang.utils.SecurityUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class PostStatsMapper {

    @Autowired
    protected BookmarkedPostRepo bookmarkedPostRepo;

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "bookmarked", ignore = true)
    @Mapping(target = "bookmarkedCount",
            expression = "java(post.getBookmarkedPosts().size())")
    @Mapping(target = "commentCount",
            expression = "java(post.getComments().size())")
    public abstract PostStatsDto toPostStats(Post post);

    @AfterMapping
    public void afterMapping(@MappingTarget PostStatsDto target, Post source) {
        var currentUser = SecurityUtils.currentUser();
        currentUser.ifPresent(user -> target.setBookmarked(bookmarkedPostRepo.existsByAccountAndPost_Id(user.getSource(), source.getId())));
    }

}
