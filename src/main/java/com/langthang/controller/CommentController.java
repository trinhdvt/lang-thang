package com.langthang.controller;

import com.langthang.dto.CommentDTO;
import com.langthang.services.ICommentServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
public class CommentController {

    private final ICommentServices commentServices;

    @GetMapping("/comment/post/{post_id}")
    public ResponseEntity<Object> getCommentOfPost(
            @PathVariable("post_id") int postId,
            @PageableDefault(sort = {"commentDate"},
                    direction = Sort.Direction.DESC) Pageable pageable) {

        List<CommentDTO> commentList = commentServices.getAllCommentOfPost(postId, pageable);

        return ResponseEntity.ok(commentList);
    }

    @GetMapping(value = "/comment/post", params = {"slug"})
    public ResponseEntity<Object> getCommentOfPost(
            @RequestParam("slug") String slug,
            @PageableDefault(sort = {"commentDate"},
                    direction = Sort.Direction.DESC) Pageable pageable) {

        List<CommentDTO> commentList = commentServices.getAllCommentOfPost(slug, pageable);

        return ResponseEntity.ok(commentList);
    }

    @PutMapping("/comment/{comment_id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> likeOrUnlikeComment(
            @PathVariable("comment_id") int commentId,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        int currentLikeCount = commentServices.likeOrUnlikeComment(commentId, currentEmail);

        return ResponseEntity.ok(currentLikeCount);
    }

    @PostMapping("/comment/post/{post_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> addCommentToPost(
            @PathVariable("post_id") int postId,
            @RequestParam("content") @NotBlank String content,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        CommentDTO newComment = commentServices.addNewComment(postId, content, currentEmail);

        return ResponseEntity.ok(newComment);
    }

    @PutMapping("/comment/{comment_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> modifyComment(
            @PathVariable("comment_id") int commentId,
            @RequestParam("content") @NotBlank String content,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        CommentDTO modifiedComment = commentServices.modifyComment(commentId, content, currentEmail);

        return ResponseEntity.ok(modifiedComment);
    }

    @DeleteMapping("/comment/{comment_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteComment(
            @PathVariable("comment_id") int commentId,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        int currentPostCommentCount = commentServices.deleteComment(commentId, currentEmail);

        return ResponseEntity.ok(currentPostCommentCount);
    }
}
