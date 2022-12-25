package com.langthang.controller.v1;

import com.langthang.model.constraints.Role;
import com.langthang.model.dto.request.PostRequestDTO;
import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.model.dto.v2.response.PostDtoV2;
import com.langthang.services.IPostServices;
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
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@Validated
@CacheConfig(cacheNames = "postCache")
public class PostController {

    private final IPostServices postServices;

    @GetMapping(value = "/post", params = {"keyword"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostDtoV2> searchPostByKeyword(
            @RequestParam("keyword") String keyword,
            @PageableDefault Pageable pageable) {

        return postServices.findPostByKeyword(keyword, pageable);
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