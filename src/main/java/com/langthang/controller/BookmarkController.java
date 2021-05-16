package com.langthang.controller;

import com.langthang.dto.PostResponseDTO;
import com.langthang.services.IBookmarkServices;
import com.langthang.services.IPostServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookmarkController {

    @Autowired
    private IBookmarkServices bookmarkServices;

    @Autowired
    private IPostServices postServices;

    @GetMapping("/bookmark/posts")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDTO> getListOfBookmarkedPost(
            @PageableDefault Pageable pageable,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        return postServices.getBookmarkedPostOfUser(currentEmail, pageable);
    }


    @PostMapping("/bookmark")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> bookmarkPost(
            @RequestParam("post_id") int postId,
            Authentication authentication) {

        String currentEmail = authentication.getName();
        int bookmarkCount = bookmarkServices.bookmarkPost(postId, currentEmail);

        return ResponseEntity.accepted().body(bookmarkCount);
    }

    @DeleteMapping("/bookmark")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteBookmark(
            @RequestParam("post_id") int postId,
            Authentication authentication) {

        String currentEmail = authentication.getName();
        int bookmarkCount = bookmarkServices.deleteBookmark(postId, currentEmail);

        return ResponseEntity.accepted().body(bookmarkCount);
    }
}
