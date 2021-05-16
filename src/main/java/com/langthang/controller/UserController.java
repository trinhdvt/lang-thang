package com.langthang.controller;

import com.langthang.annotation.PasswordMatches;
import com.langthang.annotation.ValidEmail;
import com.langthang.dto.AccountDTO;
import com.langthang.dto.AccountInfoDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.dto.ResetPasswordDTO;
import com.langthang.services.IPostServices;
import com.langthang.services.IUserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class UserController {

    @Autowired
    private IUserServices userServices;

    @Autowired
    private IPostServices postServices;

    @GetMapping("/user/{account_id}")
    public ResponseEntity<Object> getInformationOfUser(
            @PathVariable("account_id") int accountId) {

        AccountDTO accountDTO = userServices.getDetailInformation(accountId);

        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping(value = "/user", params = {"email"})
    public ResponseEntity<Object> getInformationOfUser(
            @RequestParam("email") @ValidEmail String email) {

        AccountDTO accountDTO = userServices.getDetailInformation(email);

        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping("/whoami")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getCurrentUserInfo(
            Authentication authentication){

        String currentEmail = authentication.getName();

        AccountDTO accountDTO = userServices.getDetailInformation(currentEmail);

        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping("/user/posts/{account_id}")
    public ResponseEntity<Object> getAllPostsOfUser(
            @PathVariable("account_id") int accountId,
            @PageableDefault(sort = {"publishedDate"}) Pageable pageable) {

        List<PostResponseDTO> responseList = postServices.getAllPreviewPostOfUser(accountId, pageable);

        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/user/follow/{account_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> followOrUnfollow(
            @PathVariable("account_id") int accountId,
            Authentication authentication) {

        String currentAccount = authentication.getName();

        int currentFollowCount = userServices.followOrUnfollow(currentAccount, accountId);

        return ResponseEntity.accepted().body(currentFollowCount);
    }

    @PutMapping("/user/update/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> updateUserBasicInfo(
            @Valid AccountInfoDTO newInfo,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        AccountDTO updated = userServices.updateBasicInfo(currentEmail, newInfo);

        return ResponseEntity.accepted().body(updated);
    }

    @PutMapping("/user/update/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> updateUserPassword(
            @RequestParam("oldPassword") String oldPassword,
            @Valid @PasswordMatches ResetPasswordDTO newPassword,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        userServices.checkEmailAndPassword(currentEmail, oldPassword);

        userServices.updatePassword(currentEmail, newPassword.getPassword());

        return ResponseEntity.accepted().build();
    }
}
