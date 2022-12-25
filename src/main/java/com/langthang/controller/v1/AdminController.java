package com.langthang.controller.v1;

import com.langthang.model.dto.response.AccountDTO;
import com.langthang.model.dto.response.SystemReportDTO;
import com.langthang.services.IAdminServices;
import com.langthang.services.IUserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@Validated
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final IAdminServices adminServices;

    private final IUserServices userServices;

    private final CacheManager cacheManager;

    @GetMapping("/system/info")
    public ResponseEntity<Object> getBasicSystemInfo() {

        SystemReportDTO reportDTO = adminServices.getBasicSystemReport();

        return ResponseEntity.ok(reportDTO);
    }

    @PostMapping("/system/clear-cache")
    public ResponseEntity<?> clearCache() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Optional.ofNullable(cacheManager.getCache(cacheName))
                        .ifPresent(Cache::clear));

        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/user/follow/top")
    public ResponseEntity<Object> getTopFollowUser(
            @PageableDefault(size = 5) Pageable pageable) {

        List<AccountDTO> topFollowing = userServices.getTopFollowUser(pageable.getPageSize());

        return ResponseEntity.ok(topFollowing);
    }

    @GetMapping("/system/users")
    public ResponseEntity<Object> getListOfUsersInSystem(
            @PageableDefault Pageable pageable) {

        List<AccountDTO> listOfUsers = userServices.getListOfUserInSystem(pageable);

        return ResponseEntity.ok(listOfUsers);
    }

    @PutMapping("/user/{user_id}/admin")
    public ResponseEntity<Object> assignRoleAdminToUser(
            @PathVariable("user_id") int userId) {

        adminServices.assignRoleAdminToUser(userId);

        return ResponseEntity.accepted().build();
    }

}