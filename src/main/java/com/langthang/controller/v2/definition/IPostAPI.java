package com.langthang.controller.v2.definition;

import com.langthang.exception.HttpError;
import com.langthang.model.dto.v2.request.PostCreateDto;
import com.langthang.model.dto.v2.response.PostCreatedResponse;
import com.langthang.model.dto.v2.response.PostDtoV2;
import com.langthang.model.entity.Post_;
import com.langthang.security.services.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Post API", description = "API for Post")
public interface IPostAPI {

    @Operation(summary = "Get Post by ID or Slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post found"),
            @ApiResponse(responseCode = "404", description = "Post not found", content = {@Content(schema = @Schema(implementation = HttpError.class))})
    })
    @GetMapping(value = "/post/{postIdentity}", produces = {MediaType.APPLICATION_JSON_VALUE})
    PostDtoV2 getPostByIdentity(@PathVariable String postIdentity);

    @Operation(summary = "Get Latest Post")
    @GetMapping(value = "/post")
    List<PostDtoV2> getLatestPost(@ParameterObject @PageableDefault(
            sort = {Post_.PUBLISHED_DATE},
            direction = Sort.Direction.DESC) Pageable pageable);

    @Operation(summary = "Get Popular Post by Property")
    @GetMapping(value = "/post/top-{type}")
    List<PostDtoV2> getPopularPostByProperty(@ParameterObject @PageableDefault(
            sort = {Post_.PUBLISHED_DATE},
            direction = Sort.Direction.DESC) Pageable pageable, @PathVariable String type);

    @Operation(summary = "Create new Post", description = "Only authenticated user")
    @PostMapping("/post")
    @PreAuthorize("isAuthenticated()")
    PostCreatedResponse createNewPost(
            @Valid @RequestBody PostCreateDto payload,
            @AuthenticationPrincipal CurrentUser author
    );

    @Operation(summary = "Get post's editable content", description = "Only owner")
    @GetMapping("/post/{slug}/edit")
    @PreAuthorize("isAuthenticated()")
    PostDtoV2 getEditableContentBySlug(@PathVariable String slug);

    @Operation(summary = "Update Post", description = "Only owner")
    @PutMapping("/post/{postId}")
    @PreAuthorize("isAuthenticated()")
    PostCreatedResponse updatePost(
            @PathVariable Integer postId,
            @Valid @RequestBody PostCreateDto payload
    );

    @Operation(summary = "Delete Post", description = "Only owner")
    @DeleteMapping("/post/{postId}")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<?> deletePost(@PathVariable Integer postId);
}
