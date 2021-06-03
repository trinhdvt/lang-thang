package com.langthang.controller;

import com.langthang.dto.AccountDTO;
import com.langthang.dto.SystemReportDTO;
import com.langthang.services.IAdminServices;
import com.langthang.services.IUserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@Validated
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final IAdminServices adminServices;

    private final IUserServices userServices;

    @GetMapping("/system/info")
    public ResponseEntity<Object> getBasicSystemInfo() {

        SystemReportDTO reportDTO = adminServices.getBasicSystemReport();

        return ResponseEntity.ok(reportDTO);
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
