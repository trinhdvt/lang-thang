package com.langthang.controller;

import com.langthang.dto.PostRequestDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.model.Role;
import com.langthang.services.IPostServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
public class PostController {

    private final IPostServices postServices;

    @Cacheable(value = "post_detail", key = "{#id,#authentication}", unless = "#result == null")
    @GetMapping("/post/{id}")
    public ResponseEntity<Object> getPostDetailById(
            @PathVariable(value = "id") int id,
            Authentication authentication) {

        PostResponseDTO responseDTO = postServices.getPostDetailById(id);

        return ResponseEntity.ok(responseDTO);
    }

    @Cacheable(value = "post_detail", key = "{#slug,#authentication}", unless = "#result == null")
    @GetMapping(value = "/post", params = {"slug"})
    public ResponseEntity<Object> getPostDetailBySlug(
            @RequestParam("slug") String slug,
            Authentication authentication) {

        PostResponseDTO responseDTO = postServices.getPostDetailBySlug(slug);

        return ResponseEntity.ok(responseDTO);
    }

    @Cacheable(value = {"post_homepage"}, key = "{#pageable.pageNumber,#pageable.pageSize}")
    @GetMapping(value = "/post")
    @ResponseStatus(HttpStatus.OK)
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

    @Cacheable(value = {"post_homepage"}, key = "{#propertyName,#pageable.pageNumber,#pageable.pageSize}")
    @GetMapping(value = "/post", params = {"prop"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDTO> getPopularPostByProperty(
            @RequestParam("prop") String propertyName,
            @PageableDefault Pageable pageable) {

        return postServices.getPopularPostByProperty(propertyName, pageable.getPageSize());
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
        PostResponseDTO savedPost = postServices.addNewPostOrDraft(postRequestDTO, authorEmail, false);

        return Collections.singletonMap("slug", savedPost.getSlug());
    }

    @PutMapping(value = "/post/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @Caching(evict = {
            @CacheEvict(value = "post_detail", allEntries = true),
            @CacheEvict(value = "post_homepage", allEntries = true)
    })
    public Object updatePost(
            @PathVariable("id") int postId,
            @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();

//      attempting to update existing post
        String updatedSlug = postServices.updatePostById(postId, authorEmail, postRequestDTO);

        return Collections.singletonMap("slug", updatedSlug);
    }

    @Caching(evict = {
            @CacheEvict(value = "post_homepage", allEntries = true),
            @CacheEvict(value = "post_detail", allEntries = true)
    })
    @DeleteMapping(value = "/post/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deletePost(
            @PathVariable("id") int postId,
            Authentication authentication) {

        String requestEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(Role.ROLE_ADMIN);

        postServices.deletePostById(postId, requestEmail, isAdmin);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/draft")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> addDraft(
            @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();

        postServices.addNewPostOrDraft(postRequestDTO, authorEmail, true);

        return ResponseEntity.ok().build();
    }

    @Cacheable(value = "post_detail", key = "{#postId,#authentication}", unless = "#result == null")
    @GetMapping("/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getDraftById(
            @PathVariable("id") int postId,
            Authentication authentication) {

        String authorEmail = authentication.getName();

        PostResponseDTO postResponseDTO = postServices.getDraftById(postId, authorEmail);

        return ResponseEntity.ok(postResponseDTO);
    }

    @Caching(evict = {
            @CacheEvict(value = "post_detail", allEntries = true),
            @CacheEvict(value = "post_homepage", allEntries = true)
    })
    @PutMapping("/draft/{id}")
    @PreAuthorize("isAuthenticated()")
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
