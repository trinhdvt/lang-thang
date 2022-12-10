package com.langthang.controller;

import com.langthang.annotation.PasswordMatches;
import com.langthang.model.dto.request.AccountInfoDTO;
import com.langthang.model.dto.request.PasswordDTO;
import com.langthang.model.dto.response.AccountDTO;
import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.services.IPostServices;
import com.langthang.services.IUserServices;
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

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@Validated
@CacheConfig(cacheNames = "userCache")
public class UserController {

    private final IUserServices userServices;

    private final IPostServices postServices;

    @GetMapping("/user/{account_id}")
    public ResponseEntity<Object> getInformationOfUser(
            @PathVariable("account_id") int accountId) {

        AccountDTO accountDTO = userServices.getDetailInformationById(accountId);

        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping(value = "/user", params = {"slug"})
    public ResponseEntity<Object> getInformationOfUser(
            @RequestParam("slug") String slug) {

        AccountDTO accountDTO = userServices.getDetailInformationBySlug(slug);

        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping("/whoami")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(key = "{@securityUtils.getLoggedInEmail()}")
    public AccountDTO getCurrentUserInfo(
            Authentication authentication) {

        String currentEmail = authentication.getName();

        return userServices.getDetailInformationByEmail(currentEmail);
    }

    @GetMapping("/user/posts/{account_id}")
    public ResponseEntity<Object> getAllPostsOfUser(
            @PathVariable("account_id") int accountId,
            @PageableDefault(sort = {"publishedDate"}) Pageable pageable) {

        List<PostResponseDTO> responseList = postServices.getAllPostOfUser(accountId, pageable);

        return ResponseEntity.ok(responseList);
    }

    @GetMapping(value = "/user/posts", params = {"email"})
    public ResponseEntity<Object> getAllPostsOfUser(
            @RequestParam("email") String accountEmail,
            @PageableDefault(sort = {"publishedDate"}) Pageable pageable) {

        List<PostResponseDTO> responseList = postServices.getAllPostOfUser(accountEmail, pageable);

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/user/posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getAllPostsOfCurrentUser(
            @PageableDefault(sort = {"publishedDate"}) Pageable pageable,
            Authentication authentication) {

        String accountEmail = authentication.getName();

        List<PostResponseDTO> responseList = postServices.getAllPostOfUser(accountEmail, pageable);

        return ResponseEntity.ok(responseList);
    }


    @GetMapping("/user/drafts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getAllDraftsOfUser(
            @PageableDefault(sort = {"publishedDate"}) Pageable pageable,
            Authentication authentication) {

        String accountEmail = authentication.getName();

        List<PostResponseDTO> draftList = postServices.getAllDraftOfUser(accountEmail, pageable);

        return ResponseEntity.ok(draftList);
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

    @GetMapping("/user/{account_id}/follow")
    @ResponseStatus(HttpStatus.OK)
    public Object getFollowers(
            @PathVariable("account_id") int accountId,
            @PageableDefault(sort = {"id"}) Pageable pageable) {

        return userServices.getFollower(accountId, pageable);
    }

    @PutMapping("/user/update/info")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @CacheEvict(key = "{@securityUtils.getLoggedInEmail()}")
    public AccountDTO updateUserBasicInfo(
            @Valid AccountInfoDTO newInfo,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        return userServices.updateBasicInfo(currentEmail, newInfo);
    }

    @PutMapping("/user/update/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> updateUserPassword(
            @RequestParam("oldPassword") @Size(min = 6, max = 32) String oldPassword,
            @Valid @PasswordMatches PasswordDTO newPassword,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        userServices.checkEmailAndPassword(currentEmail, oldPassword);

        userServices.updatePassword(currentEmail, newPassword.getPassword());

        return ResponseEntity.accepted().build();
    }
}