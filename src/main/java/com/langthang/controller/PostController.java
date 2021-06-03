package com.langthang.controller;

import com.langthang.dto.PostRequestDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.model.Role;
import com.langthang.services.INotificationServices;
import com.langthang.services.IPostServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@Validated
public class PostController {

    private final IPostServices postServices;

    private final INotificationServices notificationServices;

    @GetMapping("/post/{id}")
    public ResponseEntity<Object> getPostDetailById(
            @PathVariable(value = "id") int id) {

        PostResponseDTO responseDTO = postServices.getPostDetailById(id);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(value = "/post", params = {"slug"})
    public ResponseEntity<Object> getPostDetailBySlug(
            @RequestParam("slug") String slug) {

        PostResponseDTO responseDTO = postServices.getPostDetailBySlug(slug);

        return ResponseEntity.ok(responseDTO);
    }

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

    @GetMapping(value = "/post", params = {"prop"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDTO> getPopularPostByProperty(
            @RequestParam("prop") String propertyName,
            @PageableDefault Pageable pageable) {

        return postServices.getPopularPostByProperty(propertyName, pageable.getPageSize());
    }

    @PostMapping("/post")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> addPost(
            @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();
        PostResponseDTO savedPost;

        if (postRequestDTO.getPostId() != null) {
            int postId = postRequestDTO.getPostId();

            postServices.checkResourceExistAnOwner(postId, authorEmail);

            savedPost = postServices.updateAndPublicDraft(postRequestDTO);

        } else {
            savedPost = postServices.addNewPostOrDraft(postRequestDTO, authorEmail, false);
        }

        notificationServices.sendNotificationToFollower(authorEmail, savedPost.getPostId());

        return ResponseEntity.ok(savedPost);
    }

    @PutMapping(value = "/post/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> updatePost(
            @PathVariable("id") int postId,
            @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();

        postServices.checkResourceExistAnOwner(postId, authorEmail);

        PostResponseDTO updatedPostDTO = postServices.updatePostById(postId, postRequestDTO);

        return ResponseEntity.accepted().body(updatedPostDTO);
    }

    @DeleteMapping(value = "/post/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deletePost(
            @PathVariable("id") int postId,
            Authentication authentication) {

        if (authentication.getAuthorities().contains(Role.ROLE_MEMBER)) {
            String authorEmail = authentication.getName();

            postServices.checkResourceExistAnOwner(postId, authorEmail);
        }

        postServices.deletePostById(postId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/draft")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> addDraft(
            @RequestBody @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();

        postServices.addNewPostOrDraft(postRequestDTO, authorEmail, true);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getDraftById(
            @PathVariable("id") int postId,
            Authentication authentication) {

        String authorEmail = authentication.getName();

        postServices.checkResourceExistAnOwner(postId, authorEmail);

        PostResponseDTO postResponseDTO = postServices.getDraftById(postId);

        return ResponseEntity.ok(postResponseDTO);
    }

    @PutMapping("/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> updateDraft(
            @PathVariable("id") int postId,
            @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();

        postServices.checkResourceExistAnOwner(postId, authorEmail);

        // update existing draft
        postServices.updatePostById(postRequestDTO.getPostId(), postRequestDTO);

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteDraft(
            @PathVariable("id") int postId,
            Authentication authentication) {

        String authorEmail = authentication.getName();

        postServices.checkResourceExistAnOwner(postId, authorEmail);

        postServices.deletePostById(postId);

        return ResponseEntity.noContent().build();
    }
}
