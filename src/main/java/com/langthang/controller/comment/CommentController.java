package com.langthang.controller.comment;

import com.langthang.dto.CommentDTO;
import com.langthang.event.OnNewCommentEvent;
import com.langthang.services.ICommentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private ICommentServices commentServices;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping("/comment/post/{post_id}")
    public ResponseEntity<Object> getCommentOfPost(
            @PathVariable("post_id") int postId,
            Authentication authentication) {

        String currentEmail = authentication != null ? authentication.getName() : null;

        List<CommentDTO> commentList = commentServices.getAllCommentOfPost(postId, currentEmail);

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
            @RequestParam("content") String content,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        CommentDTO newComment = commentServices.addNewComment(postId, content, currentEmail);

        eventPublisher.publishEvent(new OnNewCommentEvent(newComment));

        return ResponseEntity.ok(newComment);
    }

    @PutMapping("/comment/{comment_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> modifyComment(
            @PathVariable("comment_id") int commentId,
            @RequestParam("content") String content,
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

        String accEmail = authentication.getName();
        int currentPostCommentCount = commentServices.deleteComment(commentId, accEmail);

        return ResponseEntity.ok(currentPostCommentCount);
    }
}
