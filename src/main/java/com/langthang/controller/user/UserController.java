package com.langthang.controller.user;

import com.langthang.annotation.ValidEmail;
import com.langthang.dto.BasicAccountDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.services.IPostServices;
import com.langthang.services.IUserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
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

        BasicAccountDTO accountDTO = userServices.getDetailInformation(accountId);

        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping(value = "/user/", params = {"email"})
    public ResponseEntity<Object> getInformationOfUser(
            @RequestParam("email") @ValidEmail String email) {

        BasicAccountDTO accountDTO = userServices.getDetailInformation(email);

        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping("/user/posts/{account_id}")
    public ResponseEntity<Object> getAllPostsOfUser(
            @PathVariable("account_id") int accountId,
            @RequestParam(value = "page", required = false, defaultValue = "0")
            @Min(value = 0, message = "Page must >= 0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5")
            @Min(value = 1, message = "Size must >= 1") int size) {

        List<PostResponseDTO> responseList = postServices.getAllPreviewPostOfUser(accountId, page, size);

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
}
