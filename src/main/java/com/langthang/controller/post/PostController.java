package com.langthang.controller.post;

import com.langthang.dto.PostRequestDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.entity.Post;
import com.langthang.model.entity.Role;
import com.langthang.services.IPostServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;

@RestController
@Validated
@Slf4j
public class PostController {

    @Autowired
    private IPostServices postServices;

    @GetMapping("/post/{id}")
    public ResponseEntity<Object> getPostDetailById(
            @PathVariable(value = "id") int id) {

        PostResponseDTO responseDTO = postServices.getPostDetailById(id);

        if (responseDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(value = "/post", params = {"slug"})
    public ResponseEntity<Object> getPostDetailBySlug(
            @RequestParam("slug") String slug) {

        PostResponseDTO responseDTO = postServices.getPostDetailBySlug(slug);

        if (responseDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(value = "/post", params = {"size", "page"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDTO> getPreviewPost(
            @RequestParam @Min(value = 0, message = "Page must greater than 0") int page,
            @RequestParam @Min(value = 0, message = "Size must greater than 0") int size) {

        return postServices.getPreviewPost(page, size);
    }

    @GetMapping(value = "/post", params = {"keyword", "page", "size"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDTO> searchPostByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam @Min(value = 0, message = "Page must greater than 0") int page,
            @RequestParam @Min(value = 0, message = "Size must greater than 0") int size) {

        return postServices.getPreviewPost(page, size, keyword);
    }

    @GetMapping(value = "/post", params = {"sort"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDTO> getPopularPostByProperty(
            @RequestParam("sort") String propertyName,
            @RequestParam(value = "size", defaultValue = "5", required = false)
            @Min(value = 1, message = "Size must greater than 1") int size) {

        return postServices.getPopularPostByProperty(propertyName, size);
    }

    @PostMapping("/post")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> addPost(
            @RequestBody @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {
        String authorEmail = authentication.getName();
        Post savedPost;

        if (postRequestDTO.getPostId() != null) {
            Integer postId = postRequestDTO.getPostId();

            boolean isOwner = postServices.checkResourceOwner(postId, authorEmail);
            if (!isOwner) {
                throw new CustomException("You cannot public other people's post", HttpStatus.FORBIDDEN);
            }
            savedPost = postServices.updateAndPublicDraft(postRequestDTO, postId);

        } else {
            savedPost = postServices.addNewPostOrDraft(postRequestDTO, authorEmail, false);
        }

        return ResponseEntity.created(URI.create("/post/" + savedPost.getId())).build();
    }

    @PutMapping(value = "/post/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public ResponseEntity<Object> modifyPost(
            @PathVariable("id") int postId,
            @RequestBody @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String ownerEmail = authentication.getName();

        boolean isOwner = postServices.checkResourceOwner(postId, ownerEmail);
        if (!isOwner) {
            throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
        }

        postServices.updatePostById(postId, postRequestDTO);

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping(value = "/post/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or isAuthenticated()")
    public ResponseEntity<Object> deletePost(
            @PathVariable("id") int postId,
            Authentication authentication) {

        if (postServices.isPostNotFound(postId)) {
            return ResponseEntity.notFound().build();
        }

        if (authentication.getAuthorities().contains(Role.ROLE_MEMBER)) {
            String ownerEmail = authentication.getName();
            boolean isOwner = postServices.checkResourceOwner(postId, ownerEmail);
            if (!isOwner) {
                throw new CustomException("Forbidden"
                        , HttpStatus.FORBIDDEN);
            }
        }

        postServices.deletePostById(postId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/post/draft")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> saveTemporaryPost(
            @RequestBody @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {
        String authorEmail = authentication.getName();

        postServices.addNewPostOrDraft(postRequestDTO, authorEmail, true);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/post/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getTemporaryPost(
            @PathVariable("id") int postId,
            Authentication authentication) {

        if (postServices.isPostNotFound(postId)) {
            return ResponseEntity.notFound().build();
        }

        String ownerEmail = authentication.getName();
        boolean isOwner = postServices.checkResourceOwner(postId, ownerEmail);
        if (!isOwner) {
            throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
        }
        PostResponseDTO postResponseDTO = postServices.getDraftById(postId);

        if (postResponseDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(postResponseDTO);
    }

    @PutMapping("/post/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> updateDraft(
            @PathVariable("id") int postId,
            @RequestBody @Valid PostRequestDTO postRequestDTO,
            Authentication authentication) {

        String authorEmail = authentication.getName();
        boolean isOwner = postServices.checkResourceOwner(postId, authorEmail);
        if (!isOwner) {
            throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
        }
        // update existing draft
        postServices.updatePostById(postRequestDTO.getPostId(), postRequestDTO);

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/post/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteDraft(
            @PathVariable("id") int postId,
            Authentication authentication) {

        if (postServices.isPostNotFound(postId)) {
            return ResponseEntity.notFound().build();
        }

        String ownerEmail = authentication.getName();
        boolean isOwner = postServices.checkResourceOwner(postId, ownerEmail);
        if (!isOwner) {
            throw new CustomException("Forbidden"
                    , HttpStatus.FORBIDDEN);
        }
        postServices.deletePostById(postId);

        return ResponseEntity.noContent().build();
    }
}
