package com.langthang.controller.v2.definition;

import com.langthang.model.dto.v2.response.UserDtoV2;
import com.langthang.model.dto.v2.response.UserStatsDto;
import com.langthang.security.services.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "User API", description = "API for user")
public interface UserAPI {
    @Operation(summary = "Get current user's info", description = "Only authenticated user")
    @GetMapping("/my-info")
    @PreAuthorize("isAuthenticated()")
    UserDtoV2 getCurrentUserInfo(@AuthenticationPrincipal CurrentUser currentUser);

    @Operation(summary = "Get author's stats by ID")
    @GetMapping("/user/{userId}/stats")
    UserStatsDto getAuthorStats(@PathVariable Integer userId);
}
