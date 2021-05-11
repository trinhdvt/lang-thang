package com.langthang.controller.bookmark;

import com.langthang.dto.PostResponseDTO;
import com.langthang.services.IBookmarkServices;
import com.langthang.services.IPostServices;
import org.springframework.beans.factory.annotation.Autowired;
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
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            Authentication authentication) {

        String accEmail = authentication.getName();

        return postServices.getBookmarkedPostOfUser(accEmail, page, size);
    }


    @PostMapping("/bookmark")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> bookmarkPost(
            @RequestParam("post_id") int postId,
            Authentication authentication) {

        String accEmail = authentication.getName();
        int bookmarkCount = bookmarkServices.bookmarkPost(postId, accEmail);

        return ResponseEntity.accepted().body(bookmarkCount);
    }

    @DeleteMapping("/bookmark")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteBookmark(
            @RequestParam("post_id") int postId,
            Authentication authentication) {

        String accEmail = authentication.getName();
        int bookmarkCount = bookmarkServices.deleteBookmark(postId, accEmail);

        return ResponseEntity.accepted().body(bookmarkCount);
    }
}
