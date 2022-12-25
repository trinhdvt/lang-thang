package com.langthang.controller.v2;

import com.langthang.model.constraints.PostPopularType;
import com.langthang.model.dto.v2.request.PostCreateDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@CacheConfig(cacheNames = "post-cache")
public class PostControllerV2 {

    private final PostServiceV2 postServices;

    @GetMapping(value = "/post", params = {"slug"})
    @ResponseStatus(HttpStatus.OK)
    public PostDtoV2 getPostBySlug(@RequestParam String slug) {
        return postServices.getBySlug(slug);
    }

    @GetMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostDtoV2 getPostById(@PathVariable Integer postId) {
        return postServices.getById(postId);
    }

    @GetMapping(value = "/post")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(key = "{#root.methodName,#pageable}")
    public List<PostDtoV2> getLatestPost(@PageableDefault(
            sort = {Post_.PUBLISHED_DATE},
            direction = Direction.DESC) Pageable pageable) {
        return postServices.getLatestPost(pageable);
    }

    @GetMapping(value = "/post/top-{type}")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(key = "{#root.methodName,#type,#pageable}")
    public List<PostDtoV2> getPopularPostByProperty(@PageableDefault(
            sort = {Post_.PUBLISHED_DATE},
            direction = Direction.DESC) Pageable pageable, @PathVariable String type) {
        return postServices.getListOfPopularPosts(PostPopularType.valueOf(type.toUpperCase()), pageable);
    }

    @PostMapping("/post")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createNewPost(
            @Valid @RequestBody PostCreateDto payload,
            @AuthenticationPrincipal CurrentUser author
    ) {
        var slug = postServices.createNewPost(author.getSource(), payload, true);
        return ResponseEntity.ok(Map.of("slug", slug));
    }

    @GetMapping("/post/{slug}/edit")
    @PreAuthorize("isAuthenticated()")
    @CacheEvict(allEntries = true)
    public PostDtoV2 getEditableContentBySlug(@PathVariable String slug) {
        return postServices.getEditableContentBySlug(slug);
    }

    @PutMapping("/post/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePost(
            @PathVariable Integer postId,
            @Valid @RequestBody PostCreateDto payload
    ) {
        String slug = postServices.updatePost(postId, payload);
        return ResponseEntity.ok(Map.of("slug", slug));
    }

}
