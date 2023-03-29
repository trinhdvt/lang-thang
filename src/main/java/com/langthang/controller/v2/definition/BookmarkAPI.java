package com.langthang.controller.v2.definition;

import com.langthang.model.dto.v2.response.PostStatsDto;
import com.langthang.security.services.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Bookmark API", description = "API for bookmarking post")
public interface BookmarkAPI {
    @Operation(summary = "Bookmark a post by Post's ID", description = "Only authenticated user")
    @PostMapping("/bookmark/post/{postId}")
    @PreAuthorize("isAuthenticated()")
    PostStatsDto bookmarkPost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal CurrentUser currentUser);

    @Operation(summary = "Remove bookmark of a post by Post's ID", description = "Only authenticated user")
    @DeleteMapping("/bookmark/post/{postId}")
    @PreAuthorize("isAuthenticated()")
    PostStatsDto removeBookmark(
            @PathVariable Integer postId,
            @AuthenticationPrincipal CurrentUser currentUser);
}
