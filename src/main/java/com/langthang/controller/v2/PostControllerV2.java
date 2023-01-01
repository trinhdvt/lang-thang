package com.langthang.controller.v2;

import com.langthang.controller.v2.definition.IPostAPI;
import com.langthang.model.constraints.PostPopularType;
import com.langthang.model.dto.v2.request.PostCreateDto;
import com.langthang.model.dto.v2.response.PostCreatedResponse;
import com.langthang.model.dto.v2.response.PostDtoV2;
import com.langthang.model.entity.Post_;
import com.langthang.security.services.CurrentUser;
import com.langthang.services.v2.PostServiceV2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@CacheConfig(cacheNames = "post-cache")
@Validated
public class PostControllerV2 implements IPostAPI {

    private final PostServiceV2 postServices;

    @Override
    public PostDtoV2 getPostByIdentity(@PathVariable String postIdentity) {
        return postServices.getByIdentity(postIdentity);
    }

    @Override
    @Cacheable(key = "{#root.methodName,#pageable}")
    public List<PostDtoV2> getLatestPost(@PageableDefault(
            sort = {Post_.PUBLISHED_DATE},
            direction = Direction.DESC) Pageable pageable) {
        return postServices.getLatestPost(pageable);
    }

    @Override
    @Cacheable(key = "{#root.methodName,#type,#pageable}")
    public List<PostDtoV2> getPopularPostByProperty(@PageableDefault(
            sort = {Post_.PUBLISHED_DATE},
            direction = Direction.DESC) Pageable pageable, @PathVariable String type) {
        return postServices.getListOfPopularPosts(PostPopularType.valueOf(type.toUpperCase()), pageable);
    }

    @Override
    public PostCreatedResponse createNewPost(
            @Valid @RequestBody PostCreateDto payload,
            @AuthenticationPrincipal CurrentUser author
    ) {
        var slug = postServices.createNewPost(author.getSource(), payload, true);
        return new PostCreatedResponse(slug);
    }

    @Override
    @CacheEvict(allEntries = true)
    public PostDtoV2 getEditableContentBySlug(@PathVariable String slug) {
        return postServices.getEditableContentBySlug(slug);
    }

    @Override
    public PostCreatedResponse updatePost(
            @PathVariable Integer postId,
            @Valid @RequestBody PostCreateDto payload
    ) {
        String slug = postServices.updatePost(postId, payload);
        return new PostCreatedResponse(slug);
    }

    @Override
    @CacheEvict(allEntries = true)
    public ResponseEntity<?> deletePost(Integer postId) {
        postServices.deletePostById(postId);
        return ResponseEntity.noContent().build();
    }
}
