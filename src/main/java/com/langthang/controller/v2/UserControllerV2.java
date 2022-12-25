package com.langthang.controller.v2;

import com.langthang.model.dto.v2.response.UserDtoV2;
import com.langthang.model.dto.v2.response.UserStatsDto;
import com.langthang.security.services.CurrentUser;
import com.langthang.services.IUserServices;
import com.langthang.services.v2.UserServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserControllerV2 {

    private final IUserServices userServices;
    private final UserServiceV2 userServiceV2;

    @GetMapping("/my-info")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public UserDtoV2 getCurrentUserInfo(@AuthenticationPrincipal CurrentUser currentUser) {
        return userServices.getMyProfile(currentUser.getUserId());
    }

    @GetMapping("/user/{userId}/stats")
    public UserStatsDto getAuthorStats(@PathVariable Integer userId) {
        return userServiceV2.getUserStats(userId);
    }

}
