package com.langthang.controller;

import com.langthang.model.constraints.Role;
import com.langthang.model.dto.request.PostRequestDTO;
import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.services.IPostServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@Validated
@CacheConfig(cacheNames = "postCache")
public class PostController {

    private final IPostServices postServices;

    @GetMapping(value = "/post", params = {"slug"})
    @Cacheable(key = "{#slug,@securityUtils.getLoggedInEmail()}", unless = "#result == null", condition = "@securityUtils.getLoggedInEmail() == null ")
    @ResponseStatus(HttpStatus.OK)
    public PostResponseDTO getPostDetailBySlug(
            @RequestParam("slug") String slug) {

        return postServices.getPostDetailBySlug(slug);
    }

    @GetMapping(value = "/post")
    @ResponseStatus(HttpStatus.OK)
    // @Cacheable(key = "{#root.methodName,#pageable}")
    public List<PostResponseDTO> getPreviewPost(
            @PageableDefault(sort = {"publishedDate"},
                    direction = Sort.Direction.DESC) Pageable pageable) {

        return postServices.getPreviewPost(pageable);
    }

    @GetMapping(value = "/post", params = {"keyword"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDTO> searchPostByKeyword(
            @RequestParam("keyword") String keyword,
            @PageableDefault Pageable pageable) {

        return postServices.findPostByKeyword(keyword, pageable);
    }

    @GetMapping(value = "/post", params = {"prop"})
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(key = "{#root.methodName,#propertyName,#pageable}")
    public List<PostResponseDTO> getPopularPostByProperty(
            @RequestParam("prop") String propertyName,
            @PageableDefault Pageable pageable) {

        return postServices.getPopularPostByProperty(propertyName, pageable);
    }

    @GetMapping("/post/{slug}/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getEditableContent(
            @PathVariable("slug") String slug,
            Authentication authentication) {

        String accEmail = authentication.getName();

        PostResponseDTO content = postServices.getPostOrDraftContent(slug, accEmail);

        return ResponseEntity.ok(content);
    }

    @PostMapping("/post")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public Object addPost(
            @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();

//      attempting to add new post
        PostResponseDTO savedPost = postServices.addNewPostOrDraft(postRequestDTO, authorEmail, true);

        return Collections.singletonMap("slug", savedPost.getSlug());
    }

    @PutMapping(value = "/post/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @CacheEvict(allEntries = true)
    public Object updatePost(
            @PathVariable("id") int postId,
            @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();

//      attempting to update existing post
        String updatedSlug = postServices.updatePostById(postId, authorEmail, postRequestDTO);

        return Collections.singletonMap("slug", updatedSlug);
    }

    @DeleteMapping(value = "/post/{id}")
    @PreAuthorize("isAuthenticated()")
    @CacheEvict(allEntries = true)
    public ResponseEntity<Object> deletePost(
            @PathVariable("id") int postId,
            Authentication authentication) {

        String requestEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(Role.ROLE_ADMIN);

        if (isAdmin) {
            postServices.deleteReportedPost(postId, requestEmail);
        } else {
            postServices.deletePostById(postId, requestEmail);
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/draft")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> addDraft(
            @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();

        postServices.addNewPostOrDraft(postRequestDTO, authorEmail, false);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(key = "{#postId,@securityUtils.getLoggedInEmail()}", unless = "#result == null")
    public PostResponseDTO getDraftById(
            @PathVariable("id") int postId,
            Authentication authentication) {

        String authorEmail = authentication.getName();
        return postServices.getDraftById(postId, authorEmail);
    }

    @PutMapping("/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    @CacheEvict(allEntries = true)
    public ResponseEntity<Object> updateDraft(
            @PathVariable("id") int postId,
            @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();

        // update existing draft
        // or make a post become a draft (hide it away)
        postServices.updateDraftById(postId, authorEmail, postRequestDTO);

        return ResponseEntity.noContent().build();
    }
}